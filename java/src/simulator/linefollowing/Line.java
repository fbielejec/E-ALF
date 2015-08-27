package simulator.linefollowing;

import processing.core.PApplet;
import processing.core.PVector;

public class Line {

	private PApplet parent;
	private float radius;
	private PVector center;

	public Line(PApplet p, float radius) {

		this.parent = p;
		this.radius = radius;

		float x = parent.getWidth() / 2;
		float y = parent.getHeight() / 2;

		center = new PVector(x, y);

	}// END: Constructor

	public void display() {

		parent.strokeWeight(10);
		parent.ellipseMode(PApplet.CENTER);

		parent.stroke(0, 100);
		parent.ellipse(center.x, center.y, radius * 2, radius * 2);

	}// END: display

	public PVector getCenter() {
		return center;
	}// END: getCenter

	public float getRadius() {
		return radius;
	}// END: getRadius

}// END: class
