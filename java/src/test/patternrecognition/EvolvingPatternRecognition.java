package test.patternrecognition;

import java.util.LinkedList;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.Parameters;
import processing.core.PApplet;
import processing.core.PFont;
import utils.Utils;

@SuppressWarnings("serial")
public class EvolvingPatternRecognition extends PApplet {

	private double xmin = -400;
	private double ymin = -100;
	private double xmax = 400;
	private double ymax = 100;

	private int inputRows = 2000;
	// 2 + 1 bias
	private int inputColumns = 3; 

	private PatternRecognitionPopulation population;	
	private int populationSize = 150;
	private int nFittest = 100;
	private double mutationRate = 0.01;

	private double[][] inputs;
	private int[] target;

	private int count = 0;
	private PFont f;

	public static void main(String[] args) {

		PApplet.main(new String[] { "test.patternrecognition.EvolvingPatternRecognition" });

	}// END: main

	@Override
	public void setup() {

		size(800, 200);
		f = createFont("Courier", 12, true);
		smooth();

		inputs = new double[inputRows][inputColumns];
		target = new int[inputRows];

		for (int i = 0; i < inputRows; i++) {

			double x = Utils.randomDouble(xmin, xmax);
			double y = Utils.randomDouble(ymin, ymax);

			int answer = 1;
			if (y < f(x)) {
				answer = -1;
			}

			inputs[i][0] = x;
			inputs[i][1] = y;
			// last input is the fixed bias
			inputs[i][2] = Parameters.bias;

			target[i] = answer;
		}// END: i loop

		population = new PatternRecognitionPopulation(
				populationSize,//
				nFittest, //
				mutationRate, //
				inputs //
				);

		// calculate fitness of the initial population
		population.calculateFitness(target);

	}// END setup

	@Override
	public void draw() {

		background(255);
		translate(width / 2, height / 2);

		// Generate mating pool
		population.naturalSelection();
		// Create next generation
		population.generate();
		// Calculate fitness
		population.calculateFitness(target);

		// ---REPORTING---//

		NeuralNetwork nn = population.getBestIndividual();
		displayInfo(nn);
		if (population.isFinished()) {
			noLoop();
			displayInfo(nn);
		}

	}// END draw

	private void displayInfo(NeuralNetwork nn) {

		background(255);
		LinkedList<Double> weights = nn.getWeights();

		// Draw the line
		strokeWeight(4);
		stroke(127);
		float x1 = (float) xmin;
		float y1 = (float) f(x1);
		float x2 = (float) xmax;
		float y2 = (float) f(x2);
		line(x1, y1, x2, y2);

		// Draw the line based on the current weights
		stroke(0);
		strokeWeight(1);
		x1 = (float) xmin;
		y1 = (float) ((-weights.get(2) - weights.get(0) * x1) / weights.get(1));
		x2 = (float) xmax;
		y2 = (float) ((-weights.get(2) - weights.get(0) * x2) / weights.get(1));
		line(x1, y1, x2, y2);

		// Draw the points
		count = (count + 1) % inputRows;
		for (int i = 0; i < count; i++) {

			stroke(0);

			int guess = PatternRecognitionPopulation.guess(nn, inputs[i]);

			if (guess > 0) {
				noFill();
			} else {
				fill(0);
			}

			ellipse((float) inputs[i][0], //
					(float) inputs[i][1], //
					(float) 8, //
					(float) 8 //
			);

		}// END: i loop

		// Report on the current best perceptron
		textFont(f);
		textAlign(LEFT);

		fill(0);
		rect(-width / 2, -height / 2, 250, 100, 7);
		fill(255);
		textSize(12);
		float ADJUST = 40;
		float HMOVE = 15;

		String message = "";

		text("Best fitness: " + nn.getFitness(), -width / 2,
				-(height / 2 - ADJUST));

		for (int i = 0; i < nn.getNumberOfWeights(); i++) {
			message = message.concat(String.format("%.4g", weights.get(i))
					+ " ");
		}
		text("Weights: " + message, -width / 2, -(height / 2 - ADJUST - HMOVE));
		text("Generation: " + population.getGenerationNumber(), -width / 2,
				-(height / 2 - ADJUST - 2 * HMOVE));

	}// END: displayInfo

	private double f(double x) {
		return 0.4 * x + 1;
	}// END: f

}// END: class
