package app;

import linefollowing.Line;
import genetic.AutonomousPopulation;
import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class AutonomousLineFollowing extends PApplet {

	public static final int width = 900;
	public static final int height = 600;
	private PFont f;

	private Line line;
	private AutonomousPopulation population;

	public static void main(String[] args) {
		PApplet.main(new String[] { "app.AutonomousLineFollowing" });
	}// END: main

	@Override
	public void setup() {

		size(width, height);
		f = createFont("Courier", 12, true);
		smooth();

		this.line = new Line(this);
		population = new AutonomousPopulation(this, line);

	}// END setup

	@Override
	public void draw() {

		background(255);

		line.display();
		population.naturalSelection();

		// ---REPORTING---//
		displayInfo();

	}// END draw

	private void displayInfo() {

		float ADJUST = 10;
		float HMOVE = 15;
		float VMOVE = 15;

		textFont(f);
		textAlign(LEFT);

		fill(0);
		rect(HMOVE, VMOVE, 250, 100, 7);
		fill(255);
		textSize(12);

		text("Individal:    " + population.getCurrentIndex(), HMOVE + ADJUST,
				VMOVE + ADJUST);
		text("Generation:   " + population.getGenerationNumber(), HMOVE
				+ ADJUST, VMOVE + 2 * ADJUST);
		text("Current fitness: " + population.getCurrentFitness(), HMOVE
				+ ADJUST, VMOVE + 3 * ADJUST);
		if (population.getGenerationNumber() > 0) {
			text("Top fitness: " + population.getBestFitness(), HMOVE + ADJUST,
					VMOVE + 4 * ADJUST);
		}
	}// END: displayInfo

}// END: class
