package linefollowing;

import processing.core.PApplet;
import processing.core.PVector;

public class Line {

	private static final int BORDER = 50;
	private PApplet parent;
	private float radius;
	private PVector center;
	
	public Line(PApplet p) {
		
		this.parent = p;
		this.radius = 400 - BORDER;
		
		
	}//END: Constructor
	
	public void display() {
		
		parent.strokeWeight(10);
        parent.ellipseMode(PApplet.CENTER);		
		
		float x = parent.getWidth()/2;
		float y = parent.getHeight()/2;
		
		center = new PVector(x, y);
		
		parent.stroke(0, 100);
		parent.ellipse(x, y, radius, radius);
		
	}//END: display
	
	public PVector getCenter() {
		return center;
	}
	
	public float getRadius() {
		return radius;
	}
	
}//END: class
