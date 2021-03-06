package app;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import processing.core.PApplet;
import processing.core.PFont;
import simulator.genetic.EAutonomPopulation;
import simulator.linefollowing.Line;
import utils.Utils;

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

	// logging
	private double bestFitnessOverall;
	private PrintWriter writer;
	
	public static void main(String[] args) {

		PApplet.main(new String[] { "app.AutonomousLineFollowing" });

	}// END: main

	@Override
	public void setup() {

		try {

			size(width, height);
			f = createFont("Courier", 12, true);
			smooth();

			float radius = 200;
			this.line = new Line(this, radius);
			this.population = new EAutonomPopulation(this, line);
            this.bestFitnessOverall = 0;
			
			// logging
			
			try {
				this.writer = new PrintWriter("/home/filip/Pulpit/fitness_simulation.log", "UTF-8");
			} catch (FileNotFoundException e) {
				this.writer = new PrintWriter("/home/filip/Desktop/fitness_simulation.log", "UTF-8");
			}
			
			String header = "generation" + Utils.TAB + "individual" + Utils.TAB + "fitness" + Utils.TAB;
			for (int i = 0; i < population.getNWeights(); i++) {

				String w = "w" + i;
				header += w + Utils.TAB;

			}
			writer.println(header);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}// END setup

	@Override
	public void draw() {

		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		background(255);
		line.display();

		// Let them live and score them
		population.calculateFitness();

		if (population.getCurrentIndex() > population.getPopulationSize() - 1) {

			// ---LOGGING---//

			// log best individual from current generation
			while (true) {
				
				System.out.println("Writing to log file ");
				int bestIndex = population.getBestIndex();
				
				String line = population.getGenerationNumber() + Utils.TAB + bestIndex + Utils.TAB
						+ population.getBestFitness() + Utils.TAB;
				for (Double w : population.getBestWeights()) {

					line += w + Utils.TAB;

				}

				writer.println(line);
				writer.flush();

				break;
			} // END: logging loop
			
			// Generate mating pool
			population.naturalSelection();
			// Create next generation
			population.generate();

		} // END: pop size check

		// ---REPORTING---//

		displayInfo();

	}// END draw

	private void displayInfo() {

		textFont(f);
		textAlign(LEFT);

		translate(0, 0);

		fill(0, 0, 0, 100);
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
			message = message.concat(String.format("%.4g", sensorReadings[i])
					+ " ");
		}

		text("Sensors:   " + message, HMOVE + ADJUST, VMOVE + 5 * ADJUST);

		text("Current fitness: " + population.getCurrentFitness(), HMOVE
				+ ADJUST, VMOVE + 6 * ADJUST);

		double bestFitness = population.getBestFitness();
		if(bestFitness > bestFitnessOverall) {
			bestFitnessOverall = bestFitness;
		}
		
		text("Top fitness: " + population.getBestFitness(), HMOVE + ADJUST,
				VMOVE + 7 * ADJUST);

	}// END: displayInfo

}// END: class
