package linefollowing;

import java.util.LinkedList;

import com.sun.java.swing.plaf.windows.resources.windows;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.Parameters;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import utils.Utils;

public class EAutonom {

	public boolean DEBUG = true;

	private boolean alive = true;
	// private double fitness = 0;
	private double framesAlive = 0;
	private double topSpeed = 0;
	private double lineDist = Integer.MAX_VALUE;

	private float maxspeed = 3;

	private final int NBR_SENSORS = 3;
	private final int NBR_DRIVE = 2;

	private final int SENSOR_LEFT = 0;
	private final int SENSOR_RIGHT = 1;
	private final int SENSOR_CENTER = 2;
	private final int X = 0;
	private final int Y = 1;

	// private Neuron perceptron;
	private NeuralNetwork neuralnet;

	private PApplet parent;
	private Line line;

	private PVector location;
	private PVector velocity;

	private float robotSize = 40;
	private float[][] sensorMount;

	public EAutonom(PApplet p, PVector location, Line line) {

		this.parent = p;
		this.location = location;
		this.line = line;
		this.velocity = new PVector(-maxspeed, -maxspeed);

		this.neuralnet = new NeuralNetwork();

		this.sensorMount = new float[NBR_SENSORS][2];

		this.sensorMount[SENSOR_LEFT][X] = 10;
		this.sensorMount[SENSOR_LEFT][Y] = -(robotSize + 5);

		this.sensorMount[SENSOR_CENTER][X] = 0;
		this.sensorMount[SENSOR_CENTER][Y] = -(robotSize + 5);

		this.sensorMount[SENSOR_RIGHT][X] = -10;
		this.sensorMount[SENSOR_RIGHT][Y] = -(robotSize + 5);

	}// END: Constructor

	public void performTask() {

		LinkedList<Double> readings = senseLine();
		// add the bias
		readings.add(readings.size(), Parameters.bias);

		scoreTask(readings);

		LinkedList<Double> output = neuralnet.update(readings);

		velocity.x = (float) (velocity.x + sigmoid(output.get(SENSOR_LEFT),
				Parameters.response));
		velocity.y = (float) (velocity.y + sigmoid(output.get(SENSOR_RIGHT),
				Parameters.response));

	}// END: lineFollow

	private double sigmoid(double input, double response) {
		return (1 / (1 + Math.exp(-input / response)));
	}// END: sigmoid

	public void run() {
		updateLocation();
		checkBorders();
		render();
	}// END: run

	public LinkedList<Double> senseLine() {
		/**
		 * Try to follow the path
		 * */

		LinkedList<Double> readings = new LinkedList<Double>();

		// calculate distance from line for each sensor
		float pX = location.x;
		float pY = location.y;
		float cX = line.getCenter().x;
		float cY = line.getCenter().y;
		float R = line.getRadius();

		float vX = pX - cX;
		float vY = pY - cY;
		float magV = (float) Math.sqrt(vX * vX + vY * vY);
		float aX = cX + vX / magV * R;
		float aY = cY + vY / magV * R;

		float radians = velocity.heading() + PApplet.radians(90);

		double[] leftSensorPosition = new double[] {
				location.x + sensorMount[SENSOR_LEFT][X],
				location.y + sensorMount[SENSOR_LEFT][Y] };

		rotatePoint(location.x, location.y, radians, leftSensorPosition);

		float leftDist = PApplet.dist((float) leftSensorPosition[X],
				(float) leftSensorPosition[Y], aX, aY);

		if (DEBUG) {
			parent.stroke(255, 0, 0);
			parent.strokeWeight(1);

			Utils.dashline((float) leftSensorPosition[X], //
					(float) leftSensorPosition[Y], //
					aX, //
					aY, //
					(float) 3.0, (float) 5.0, parent);

		}// END: DEBUG check

		double[] rightSensorPosition = new double[] {
				location.x + sensorMount[SENSOR_RIGHT][X],
				location.y + sensorMount[SENSOR_RIGHT][Y] };

		rotatePoint(location.x, location.y, radians, rightSensorPosition);

		float rightDist = PApplet.dist((float) rightSensorPosition[X],
				(float) rightSensorPosition[Y], aX, aY);

		if (DEBUG) {
			parent.stroke(255, 0, 0);
			parent.strokeWeight(1);

			Utils.dashline((float) rightSensorPosition[X], //
					(float) rightSensorPosition[Y], //
					aX, //
					aY, //
					(float) 3.0, (float) 5.0, parent);

		}// END: DEBUG check

		double[] centerSensorPosition = new double[] {
				location.x + sensorMount[SENSOR_CENTER][X],
				location.y + sensorMount[SENSOR_CENTER][Y] };

		rotatePoint(location.x, location.y, radians, centerSensorPosition);

		float centerDist = PApplet.dist((float) centerSensorPosition[X],
				(float) centerSensorPosition[Y], aX, aY);

		readings.add(SENSOR_LEFT, (double) leftDist);
		readings.add(SENSOR_RIGHT, (double) rightDist);

		return readings;
	}// END: lineSense;

	private void rotatePoint(float cx, double cy, double angle, double[] p) {

		double s = Math.sin(angle);
		double c = Math.cos(angle);

		// translate point to origin:
		p[X] -= cx;
		p[Y] -= cy;

		// rotate point
		double xnew = p[X] * c - p[Y] * s;
		double ynew = p[X] * s + p[Y] * c;

		// translate point back:
		p[X] = xnew + cx;
		p[Y] = ynew + cy;

	}// END: rotatePoint

	private void updateLocation() {

		velocity.limit(maxspeed);
		location.add(velocity);

	}// END: updateLocation

	private void scoreTask(LinkedList<Double> readings) {

		// TODO: on the line component

		framesAlive += 1;

		double V = Math.abs(velocity.x) + Math.abs(velocity.y);
		if (V > topSpeed) {
			topSpeed = V;
		}

		double leftVal = readings.get(SENSOR_LEFT);
		double righVal = readings.get(SENSOR_RIGHT);

		double dist = leftVal < righVal ? leftVal : righVal;

		if (dist < lineDist) {
			lineDist = dist;
		}

	}// END: updateFitness

	public double getFitness() {
		double fitness = Math.exp(1 - lineDist / (2 * line.getRadius()))
				* Math.pow(topSpeed, 2) * framesAlive;
		fitness = Math.log(fitness);

		return fitness;
	}// END: getScore

	private void checkBorders() {

		if (location.x > parent.width || location.x < 0) {
			this.alive = false;
		}

		if (location.y > parent.height || location.y < 0) {
			this.alive = false;
		}

	}// END: checkBorders

	public void render() {

		// Rotate in the direction of velocity
		float theta = velocity.heading() + PApplet.radians(90);

		// chassis
		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);

		parent.strokeWeight(2);
		parent.stroke(0);
		parent.ellipse(0, 0, robotSize, robotSize);
		parent.popMatrix();

		// wheels
		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);

		parent.fill(150);
		parent.rectMode(PConstants.CENTER);

		parent.rect(robotSize / 2, // left wheel
				robotSize / 7, //
				robotSize / 7, //
				robotSize / 3, //
				robotSize / (robotSize / 10) //
		);

		parent.rect(-robotSize / 2, // right wheel
				robotSize / 7, //
				robotSize / 7, //
				robotSize / 3, //
				robotSize / (robotSize / 10) //
		);
		parent.popMatrix();

		// TODO: sensor mount plate
		
		// sensors
		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);
		parent.strokeWeight(5);
		parent.stroke(0);

		// left sensor
		parent.point(sensorMount[SENSOR_LEFT][X], sensorMount[SENSOR_LEFT][Y]);
		// central sensor
		parent.point(sensorMount[SENSOR_CENTER][X],
				sensorMount[SENSOR_CENTER][Y]);
		// right sensor
		parent.point(sensorMount[SENSOR_RIGHT][X], sensorMount[SENSOR_RIGHT][Y]);

		parent.popMatrix();
	}// END: render

	public boolean isAlive() {
		return alive;
	}// END: isAlive

	public double[] getVelocities() {
		return new double[] { velocity.x, velocity.y };
	}

	public NeuralNetwork getNeuralNetwork() {
		return neuralnet;
	}// END:

	public void setNeuralNetwork(NeuralNetwork neuralnet) {
		this.neuralnet = neuralnet;
	}// END: setPerceptron

	public EAutonom crossover(EAutonom parentB) {

		NeuralNetwork childNeuralNetwork = this.getNeuralNetwork().crossover(
				parentB.getNeuralNetwork());

		float xpos = parent.width / 2;
		float ypos = parent.height / 2;

		EAutonom child = new EAutonom(parent, new PVector(xpos, ypos), line);
		child.setNeuralNetwork(childNeuralNetwork);

		return child;
	}// END: crossover

	public void mutate(double mutationRate) {
		this.getNeuralNetwork().mutate(mutationRate);
	}// END: mutate

}// END: class
