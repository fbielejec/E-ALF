package app;

import linefollowing.Line;
import genetic.EAutonomPopulation;
import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class AutonomousLineFollowing extends PApplet {

	public static final int width = 900;
	public static final int height = 600;
	private PFont f;

	private Line line;
	private EAutonomPopulation population;

	public static void main(String[] args) {

		PApplet.main(new String[] { "app.AutonomousLineFollowing" });

	}// END: main

	@Override
	public void setup() {

		size(width, height);
		f = createFont("Courier", 12, true);
		smooth();

		float radius = 200;
		this.line = new Line(this, radius);
		population = new EAutonomPopulation(this, line);

	}// END setup

	@Override
	public void draw() {

		background(255);
		line.display();

		// Let them live and score them
		population.calculateFitness();

		if (population.getCurrentIndex() > population.getPopulationSize() - 1) {

			// Generate mating pool
			population.naturalSelection();
			// Create next generation
			population.generate();

		}// END: pop size check

		// ---REPORTING---//

		displayInfo();

	}// END draw

	private void displayInfo() {

		float ADJUST = 10;
		float HMOVE = 15;
		float VMOVE = 15;

		textFont(f);
		textAlign(LEFT);

		translate(0, 0);
		
		fill(0);
		stroke(0);
		
		rectMode(CORNER);
		rect(HMOVE, VMOVE, 300, 100, 7);
		fill(255);
		textSize(12);

		text("Individal:    " + population.getCurrentIndex(), HMOVE + ADJUST,
				VMOVE + ADJUST);
		text("Generation:   " + population.getGenerationNumber(), HMOVE
				+ ADJUST, VMOVE + 2 * ADJUST);

		String message = "";
		// TODO: format to display network
		// double[] weights = population.getCurrentWeights();
		// for (int i = 0; i < weights.length; i++) {
		// message = message.concat(String.format("%.4g", weights[i]) + " ");
		// }

		// text("Weights:   " + message, HMOVE + ADJUST, VMOVE + 3 * ADJUST);

		message = "";
		double[] velocities = population.getCurrentVelocity();
		for (int i = 0; i < velocities.length; i++) {
			message = message
					.concat(String.format("%.4g", velocities[i]) + " ");
		}

		text("Velocities:   " + message, HMOVE + ADJUST, VMOVE + 4 * ADJUST);

		text("Current fitness: " + population.getCurrentFitness(), HMOVE
				+ ADJUST, VMOVE + 5 * ADJUST);

		// if (population.getGenerationNumber() > 0) {
		text("Top fitness: " + population.getBestFitness(), HMOVE + ADJUST,
				VMOVE + 6 * ADJUST);
		// }

	}// END: displayInfo

}// END: class
