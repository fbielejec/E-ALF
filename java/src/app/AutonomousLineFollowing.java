package app;

import processing.core.PApplet;
import processing.core.PFont;
import simulator.genetic.EAutonomPopulation;
import simulator.linefollowing.Line;
import utils.Parameters;
import controlP5.ControlP5;

@SuppressWarnings("serial")
public class AutonomousLineFollowing extends PApplet {

	public static final int width = 900;
	public static final int height = 600;
	private PFont f;

	private Line line;
	private EAutonomPopulation population;

	// Controllers
	private float ADJUST = 10;
	private float HMOVE = 15;
	private float VMOVE = 15;
	
	private ControlP5 controller;
	private float maxspeed = Parameters.maxspeed;
	private int lifespan = Parameters.lifespan;

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

		controller = new ControlP5(this);

		controller.addSlider("maxspeed")
				.setPosition(HMOVE + ADJUST, VMOVE + 8 * ADJUST)
				.setRange(0, 50)
				.setValue(maxspeed);

		controller.addSlider("lifespan")
				.setPosition(HMOVE + ADJUST, VMOVE + 10 * ADJUST)
				.setRange(50, 800)
				.setValue(lifespan);

	}// END setup

	@Override
	public void draw() {

		background(255);
		line.display();

		population.setMaxspeed(maxspeed);
		population.setLifespan(lifespan);
		
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

		textFont(f);
		textAlign(LEFT);

		translate(0, 0);

		fill(0,0,0,100);
		stroke(0);

		rectMode(CORNER);
		rect(HMOVE, VMOVE, 300, 120, 7);
		fill(255);
		textSize(12);

		text("Individal:    " + population.getCurrentIndex(), HMOVE + ADJUST,
				VMOVE + ADJUST);
		text("Generation:   " + population.getGenerationNumber(), HMOVE
				+ ADJUST, VMOVE + 2 * ADJUST);

		String message = "";
		double[] velocities = population.getCurrentVelocity();
		for (int i = 0; i < velocities.length; i++) {
			message = message
					.concat(String.format("%.4g", velocities[i]) + " ");
		}

		text("Velocities:   " + message, HMOVE + ADJUST, VMOVE + 4 * ADJUST);

		message = "";
		double[] sensorReadings = population.getCurrentSensorReadings();
		for (int i = 0; i < sensorReadings.length; i++) {
			message = message
					.concat(String.format("%.4g", sensorReadings[i]) + " ");
		}
		
		text("Sensors:   " + message, HMOVE + ADJUST, VMOVE + 5 * ADJUST);
		
		text("Current fitness: " + population.getCurrentFitness(), HMOVE
				+ ADJUST, VMOVE + 6 * ADJUST);

		text("Top fitness: " + population.getBestFitness(), HMOVE + ADJUST,
				VMOVE + 7 * ADJUST);

	}// END: displayInfo

}// END: class
