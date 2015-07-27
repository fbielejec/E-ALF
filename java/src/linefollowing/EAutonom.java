package linefollowing;

import java.util.LinkedList;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.Parameters;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import utils.Utils;

public class EAutonom {

	public boolean DEBUG = true;
	private float maxspeed  = Parameters.maxspeed;
	private int lifespan = Parameters.lifespan;
	
	private boolean alive = true;
	private double fitness = 0;
	private int framesAlive = 0;

	private final int NBR_SENSORS = 3;

	private final int SENSOR_LEFT = 0;
	private final int SENSOR_RIGHT = 1;
	private final int SENSOR_CENTER = 2;
	private final int X = 0;
	private final int Y = 1;

//	private final int NBR_DRIVE = 2;
	private final int DRIVE_LEFT = 0;
	private final int DRIVE_RIGHT = 1;
	
	// private Neuron perceptron;
	private NeuralNetwork neuralnet;

	private PApplet parent;
	private Line line;

	private PVector location;
	private PVector velocity;

	private float robotSize = 40;
	private float[][] sensorMount;

	public EAutonom(PApplet p, 
			Line line ) {

		this.parent = p;

		float xpos = parent.width / 2; 
		float ypos = parent.height / 2;
//		float xpos = (float) Utils.randomDouble(0, parent.width); 
//		float ypos = (float) Utils.randomDouble(0, parent.height);
		this.location = new PVector(xpos, ypos);
		
		this.line = line;
		this.velocity = new PVector(this.maxspeed, 0);

		this.neuralnet = new NeuralNetwork();

		this.sensorMount = new float[NBR_SENSORS][2];

		this.sensorMount[SENSOR_LEFT][X] = 10;
		this.sensorMount[SENSOR_LEFT][Y] = -(robotSize / 2 + 10);

		this.sensorMount[SENSOR_CENTER][X] = 0;
		this.sensorMount[SENSOR_CENTER][Y] = -(robotSize / 2 + 10);

		this.sensorMount[SENSOR_RIGHT][X] = -10;
		this.sensorMount[SENSOR_RIGHT][Y] = -(robotSize / 2 + 10);

	}// END: Constructor

	public void lineFollow() {

		LinkedList<Double> readings = senseLine();
		// add the bias
		readings.add(readings.size(), Parameters.bias);

		scoreTask(readings);

		LinkedList<Double> output = neuralnet.update(readings);

//		System.out.println("left:" + sigmoid(output.get(DRIVE_LEFT),
//				Parameters.response) + " right: "+ sigmoid(output.get(DRIVE_RIGHT),
//						Parameters.response));
		
		double drift = sigmoid(output.get(DRIVE_RIGHT),
				Parameters.response) -  sigmoid(output.get(DRIVE_LEFT),
						Parameters.response);
		
		
		
//		velocity.x = (float) (velocity.x + sigmoid(output.get(DRIVE_LEFT),
//				Parameters.response));
//		velocity.y = (float) (velocity.y + sigmoid(output.get(DRIVE_RIGHT),
//				Parameters.response));
		
		velocity.x = (float) (velocity.x - drift);
		velocity.y = (float) (velocity.y + drift);
		
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
		
		if (DEBUG) {
			parent.stroke(255, 0, 0);
			parent.strokeWeight(1);

			Utils.dashline((float) centerSensorPosition[X], //
					(float) centerSensorPosition[Y], //
					aX, //
					aY, //
					(float) 3.0, (float) 5.0, parent);

		}// END: DEBUG check
		
		readings.add(SENSOR_LEFT, (double) leftDist);
		readings.add(SENSOR_RIGHT, (double) rightDist);
		readings.add(SENSOR_CENTER, (double) centerDist);
		
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

	}// END: rotatePointsens

	private void updateLocation() {

		velocity.limit(maxspeed);
		location.add(velocity);

	}// END: updateLocation

	private void scoreTask(LinkedList<Double> readings) {

		double leftVal = readings.get(SENSOR_LEFT);
		double rightVal = readings.get(SENSOR_RIGHT);
		double centerVal = readings.get(SENSOR_CENTER);
		
		double C_LEFT = 30;
		double C_CENTER = 20;
		double C_RIGHT = 10;

		double s = //
				1/Math.pow((leftVal  - C_LEFT ), 2) + //
				1/Math.pow((centerVal - C_CENTER), 2) +  //
				1/Math.pow((rightVal - C_RIGHT), 2) //
				;
		
		fitness += s ;

		framesAlive ++;
	}// END: updateFitness

	public double getFitness() {
//		double phi = (fitness) / ((double) framesAlive);
		double phi = framesAlive;
		return  (phi);
	}// END: getScore

	private void checkBorders() {

		if (location.x > parent.width || location.x < 0) {
			this.alive = false;
		}

		if (location.y > parent.height || location.y < 0) {
			this.alive = false;
		}
		
		if(framesAlive > lifespan) {
//			this.alive = false;
		}
		
	}// END: checkBorders

	public void render() {

		// Rotate in the direction of velocity
		float theta = velocity.heading() + PApplet.radians(90);

		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);

		// chassis
		parent.strokeWeight(2);
		parent.stroke(0);
		parent.fill(255);
		parent.ellipse(0, 0, robotSize, robotSize);

		// wheels
		parent.stroke(0);
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

		// sensor mount plate
		parent.fill(255);
		parent.stroke(0);
		parent.rectMode(PConstants.CENTER);
		parent.rect(0, //
				-(robotSize / 2 + 5), //
				(float) (0.7 * robotSize), //
				robotSize / 2 //
		);

		// sensors
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

	public void setMaxspeed(float maxspeed) {
		this.maxspeed = maxspeed;
	}//END: setMaxspeed
	
	public float getMaxspeed( ) {
		return maxspeed;
	}//END: setMaxspeed

	public int getLifespan() {
		return lifespan;
	}

	public void setLifespan(int lifespan) {
		this.lifespan = lifespan;
	}
	
	public boolean isAlive() {
		return alive;
	}// END: isAlive

	public double[] getVelocities() {
		return new double[] { velocity.x, velocity.y };
	}//END: getVelocities

	public NeuralNetwork getNeuralNetwork() {
		return neuralnet;
	}// END: getNeuralNetwork

	public void setNeuralNetwork(NeuralNetwork neuralnet) {
		this.neuralnet = neuralnet;
	}// END: setPerceptron

	public EAutonom crossover(EAutonom parentB) {

		NeuralNetwork childNeuralNetwork = this.getNeuralNetwork().crossover(
				parentB.getNeuralNetwork());

		EAutonom child = new EAutonom(parent, 
				line);
		child.setNeuralNetwork(childNeuralNetwork);

		return child;
	}// END: crossover

	public void mutate(double mutationRate) {
		this.getNeuralNetwork().mutate(mutationRate);
	}// END: mutate

}// END: class
