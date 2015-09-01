package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import simulator.linefollowing.EAutonom;
import simulator.neuralnetwork.NeuralNetwork;
import sun.awt.X11.MWMConstants;
import utils.Parameters;
import utils.Utils;

public class Population {

	private int currentIndex;
	private int generationNumber;
	private int populationSize = Settings.POPULATION_SIZE;
	
	private float[] fittness;
	private LinkedHashMap<Integer, float[]> population;
	private ArrayList<float[]> matingPool;

	private int nInputNodes = Settings.INPUT_NODES;
	private int nHiddenNodes = Settings.HIDDEN_NODES;
	private int nOutputNodes = Settings.OUTPUT_NODES;
	private int nWeights = (nInputNodes * nHiddenNodes) + (nHiddenNodes * nOutputNodes);
	// private int nWeights = (nInputNodes + 1) * nHiddenNodes + (nHiddenNodes +
	// 1) * nOutputNodes;
	
	public Population() {

		this.currentIndex = 0;
		this.generationNumber = 0;

		this.population = new LinkedHashMap<Integer, float[]>();
		for (int i = 0; i < populationSize; i++) {

			float[] weights = new float[nWeights];
			for (int j = 0; j < nWeights; j++) {

				weights[j] = (float) Utils.randomDouble(-1, 1);

			} // END: j loop

			population.put(i, weights);

		} // END: population loop

		this.matingPool = new ArrayList<float[]>();
		this.fittness = new float[populationSize];
		// Arrays.fill(fittness, 0.0);

	}// END: Constructor

	public void naturalSelection() {

		// Clear the ArrayList
		matingPool.clear();

		double maxFitness = 0;
		for (int i = 0; i < populationSize; i++) {

			double iFitness = fittness[i];
			if (iFitness > maxFitness) {
				maxFitness = iFitness;
			}

		} // END: i loop

		for (int i = 0; i < populationSize; i++) {

			double iFitness = Utils.map(fittness[i], 0, maxFitness, 0, 1);

			int n = (int) Math.floor(iFitness * Settings.N_FITTEST);
			for (int j = 0; j < n; j++) {

				matingPool.add(population.get(i));

			} // END: j loop

		} // END: i loop

	}// END: naturalSelection

	public void generate() {

		// Refill the population with children from the mating pool
		for (int i = 0; i < populationSize; i++) {

			int a = (int) Utils.randomInt(0, matingPool.size() - 1);
			int b = (int) Utils.randomInt(0, matingPool.size() - 1);

			float[] parentA = matingPool.get(a);
			float[] parentB = matingPool.get(b);

			float[] child = crossover(parentA, parentB);
			mutate(child);

			population.put(i, child);

		} // END: i loop

		// reset index
		currentIndex = 0;
		this.fittness = new float[populationSize];
		generationNumber++;
	}// END: generate

	private void mutate(float[] weights) {

		for (int i = 0; i < nWeights; i++) {

			float rand = (float) Utils.runif();
			if (rand < Settings.MUTATION_RATE) {

				float rWeight = (float) Utils.randomDouble(-1, 1);
				weights[i] = rWeight;

			} // END: mutationRate check

		} // END: i loop

	}// END: mutate

	private float[] crossover(float[] parentA, float[] parentB) {

		float[] child = new float[nWeights];

		int midpoint = Utils.randomInt(0, nWeights);
		for (int i = 0; i < nWeights; i++) {

			if (i > midpoint) {

				child[i] = parentA[i];

			} else {

				child[i] = parentB[i];

			} // END: midpoint check

		} // END: i loop

		return child;
	}// END: crossover

	public void setFitness(float value, int index) {
		this.fittness[index] = value;
	}// END: setFitness

	public int getPopulationSize() {
		return populationSize;
	}// END: getCurrentWeights

	public void increaseIndex() {
		currentIndex++;
	}// END: increaseIndex

	public int getCurrentIndex() {
		return currentIndex;
	}// END: getCurrentWeights

	public float[] getCurrentWeights() {
		return population.get(currentIndex);
	}

	public int getGenerationNumber() {
		return generationNumber;
	}

}// END: class
