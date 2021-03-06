package simulator.genetic;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import processing.core.PApplet;
import simulator.linefollowing.EAutonom;
import simulator.linefollowing.Line;
import utils.Parameters;
import utils.Utils;

public class EAutonomPopulation {

	private PApplet parent;
	private Line line;

	private int populationSize = Parameters.populationSize;
	private int nFittest = Parameters.nFittest;
	private EAutonom[] population;
	private ArrayList<EAutonom> matingPool;

	// index and fitness of the best individual in this generation
	private int bestIndex;
	private double bestFitness;

	private int currentIndex;
	private int generationNumber;

	public EAutonomPopulation(PApplet p, Line line) throws UnsupportedEncodingException, FileNotFoundException {

		this.parent = p;
		this.line = line;

		this.population = new EAutonom[this.populationSize];
		for (int i = 0; i < populationSize; i++) {

			population[i] = new EAutonom(parent, this.line);

		} // END: population loop

		this.matingPool = new ArrayList<EAutonom>();

		this.currentIndex = 0;
		this.bestIndex = 0;
		this.generationNumber = 0;
		this.bestFitness = 0;

	}// END: Constructor

	public void calculateFitness() {

		// let them live one by one and score them
		EAutonom autonom = population[currentIndex];
		if (autonom.isAlive()) {

			autonom.run();
			autonom.lineFollow();

		} else {

			double currentFitness = autonom.getFitness();
			if (currentFitness > bestFitness) {

				bestFitness = currentFitness;
				bestIndex = currentIndex;

			}

			currentIndex++;

		} // END: alive check

	}// END: calculateFitness

	public void naturalSelection() {

		// Clear the ArrayList
		matingPool.clear();

		double maxFitness = 0;
		for (int i = 0; i < populationSize; i++) {

			double iFitness = population[i].getFitness();
			if (iFitness > maxFitness) {
				maxFitness = iFitness;
			}

		} // END: i loop

		for (int i = 0; i < populationSize; i++) {

			double iFitness = Utils.map(population[i].getFitness(), 0, maxFitness, 0, 1);

			int n = (int) Math.floor(iFitness * nFittest);
			for (int j = 0; j < n; j++) {

				matingPool.add(population[i]);

			} // END: j loop

		} // END: i loop

	}// END: naturalSelection

	public void generate() {

		// Refill the population with children from the mating pool
		for (int i = 0; i < populationSize; i++) {

			int a = (int) Utils.randomInt(0, matingPool.size() - 1);
			int b = (int) Utils.randomInt(0, matingPool.size() - 1);

			EAutonom parentA = matingPool.get(a);
			EAutonom parentB = matingPool.get(b);

			EAutonom child = parentA.crossover(parentB);
			child.mutate(Parameters.mutationRate);

			population[i] = child;

		} // END: i loop

		// reset indices
		this.currentIndex = 0;
		this.bestIndex = 0;
		this.bestFitness = 0;

		generationNumber++;
	}// END: generate

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getBestIndex() {
		return bestIndex;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getGenerationNumber() {
		return generationNumber;
	}// END: getGenerationNumber

	public double getCurrentFitness() {
		return population[currentIndex].getFitness();
	}// END: getCurrentFitness

	public LinkedList<Double> getCurrentWeights() {
		return population[currentIndex].getNeuralNetwork().getWeights();
	}// END: getCurrentWeights

	public LinkedList<Double> getBestWeights() {
		return population[bestIndex].getNeuralNetwork().getWeights();
	}// END: getCurrentWeights

	public int getNWeights() {
		return population[currentIndex].getNeuralNetwork().getNumberOfWeights();
	}// END: getCurrentWeights

	public double getBestFitness() {
		return bestFitness;
	}// END: getBestFitness

	public double[] getCurrentVelocity() {
		return population[currentIndex].getVelocities();
	}

	public double[] getCurrentSensorReadings() {
		return population[currentIndex].getSensorReadings();
	}

}// END: class
