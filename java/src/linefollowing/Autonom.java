package linefollowing;

import neuralnetwork.SteeringPerceptron;
import processing.core.PApplet;
import processing.core.PVector;

public class Autonom {

	public boolean DEBUG = true;

	private boolean alive = true;
	private double score = 0;

	private final int NBR_SENSORS = 3;
	private final int SENSOR_LEFT = 0;
	private final int SENSOR_RIGHT = 1;
	private final int SENSOR_CENTER = 2;
	private final int X = 0;
	private final int Y = 1;

	private SteeringPerceptron perceptron;

	private PApplet parent;

	private PVector location;
	private PVector velocity;

	private float robotSize = 40;
	private float maxspeed = 3;
	private float[][] sensorMount;

	public Autonom(PApplet p, PVector location) {

		this.parent = p;
		this.location = location;
		this.velocity = new PVector(maxspeed, 0);
		// this.desired = new PVector();
		this.perceptron = new SteeringPerceptron(NBR_SENSORS);
		this.sensorMount = new float[NBR_SENSORS][2];

		this.sensorMount[SENSOR_LEFT][X] = 10;
		this.sensorMount[SENSOR_LEFT][Y] = -(robotSize + 5);

		this.sensorMount[SENSOR_CENTER][X] = 0;
		this.sensorMount[SENSOR_CENTER][Y] = -(robotSize + 5);

		this.sensorMount[SENSOR_RIGHT][X] = -10;
		this.sensorMount[SENSOR_RIGHT][Y] = -(robotSize + 5);

	}// END: Constructor

	public void run() {
		updateLocation();
		checkBorders();
		render();
	}// END: run

	public double[] lineSense(Line line) {
		double[] readings = new double[NBR_SENSORS];

		// calculate distance from line for each sensor
		float pX = location.x;
		float pY = location.y;
		float cX = line.getCenter().x;
		float cY = line.getCenter().y;
		float R = line.getRadius() / 2;

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

		if (Double.valueOf(leftDist).isNaN()) {

			leftDist = 0;

		}

		double[] rightSensorPosition = new double[] {
				location.x + sensorMount[SENSOR_RIGHT][X],
				location.y + sensorMount[SENSOR_RIGHT][Y] };
		rotatePoint(location.x, location.y, radians, rightSensorPosition);
		float rightDist = PApplet.dist((float) rightSensorPosition[X],
				(float) rightSensorPosition[Y], aX, aY);

		if (Double.valueOf(rightDist).isNaN()) {

			rightDist = 0;

		}

		float drift = (leftDist - rightDist);
		if (Math.abs(drift) < 1.0) {
			score += 1;
		}

		if (DEBUG) {

			parent.stroke(0);
			parent.strokeWeight(1);

			double[] p = new double[] {
					location.x + sensorMount[SENSOR_CENTER][X],
					location.y + sensorMount[SENSOR_CENTER][Y] };
			rotatePoint(location.x, location.y, radians, p);
			// float centerDist = PApplet.dist((float)p[X], (float)p[Y], aX,
			// aY);

			parent.line((float) p[X], //
					(float) p[Y], //
					aX, //
					aY //
			);

			// float drift = (leftDist - rightDist);

		}

		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		readings[SENSOR_LEFT] = leftDist;
		readings[SENSOR_RIGHT] = rightDist;

		return readings;
	}// END: lineSense;

	// https://stackoverflow.com/questions/2259476/rotating-a-point-about-another-point-2d
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

	}

	public void lineFollow(Line line) {

		double[] readings = lineSense(line);
		double[] speeds = perceptron.feedforward(readings);

		velocity.x = (float) (velocity.x + speeds[SENSOR_LEFT]);
		velocity.y = (float) (velocity.y + speeds[SENSOR_RIGHT]);

	}// END: lineFollow

	private void updateLocation() {

		velocity.limit(maxspeed);
		location.add(velocity);

	}// END: updateLocation

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

	public void setAlive() {
		this.alive = true;
		this.score = 0;
	}// END: isAlive

	public double getFitness() {
		return score;
	}// END: getScore

	public SteeringPerceptron getPerceptron() {
		return perceptron;
	}// END: getPerceptron

	public void setPerceptron(SteeringPerceptron perceptron) {
		this.perceptron = perceptron;
	}// END: setPerceptron

}// END: class
