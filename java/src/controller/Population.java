package controller;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import utils.Utils;

public class Population {

	private int currentIndex;
	private int bestIndex;
	private int generationNumber;
	private int populationSize = Settings.POPULATION_SIZE;

	private float[] fitness;
	private float bestFitness;

	private LinkedHashMap<Integer, float[]> population;
	private LinkedList<float[]> matingPool;

	private int nWeights = (Settings.INPUT_NODES * Settings.HIDDEN_NODES)
			+ (Settings.HIDDEN_NODES * Settings.OUTPUT_NODES) + Settings.HIDDEN_NODES + Settings.OUTPUT_NODES;

	public Population() {

		this.currentIndex = 0;
		this.bestIndex = this.currentIndex;
		this.generationNumber = 0;

		this.population = new LinkedHashMap<Integer, float[]>();
		for (int i = 0; i < populationSize; i++) {

			float[] weights = new float[nWeights];
			for (int j = 0; j < nWeights; j++) {

				weights[j] = (float) Utils.randomDouble(-1, 1);

			} // END: j loop

			population.put(i, weights);

		} // END: population loop

		this.matingPool = new LinkedList<float[]>();
		this.fitness = new float[populationSize];
		this.bestFitness = -Float.MAX_VALUE;
		// Arrays.fill(fittness, 0.0);

	}// END: Constructor

	public void naturalSelection() {

		// Clear the ArrayList
		matingPool.clear();

		double maxFitness = 0;
		for (int i = 0; i < populationSize; i++) {

			double iFitness = fitness[i];
			if (iFitness > maxFitness) {
				maxFitness = iFitness;
			}

		} // END: i loop

		for (int i = 0; i < populationSize; i++) {

			double iFitness = Utils.map(fitness[i], 0, maxFitness, 0, 1);

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
		this.currentIndex = 0;
		this.bestIndex = 0;
		
		this.fitness = new float[populationSize];
		this.bestFitness = 0;
		
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

		this.fitness[index] = value;

		if (this.fitness[index] > this.bestFitness) {
			this.bestFitness = this.fitness[index];
			this.bestIndex = index;
		}

	}// END: setFitness

	public float getFitness(int index) {
		return (fitness[index]);
	}// END: setFitness

	public float getBestFitness() {
		return bestFitness;
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

	
	public int getBestIndex() {
		return bestIndex;
	}// END: getCurrentWeights

	public int getnWeights() {
		return nWeights;
	}

	public float[] getIndividualWeights(int individual) {
		return population.get(individual);
	}

	public void setIndividualWeights(int individual, float[] weights) {
		 population.put(individual, weights);
	}//END: setIndividualWeights
	
	public float[] getCurrentWeights() {
		return population.get(currentIndex);
	}

	public float[] getBestWeights() {
		return population.get(bestIndex);
	}
	
	public int getGenerationNumber() {
		return generationNumber;
	}

	public void setGenerationNumber(int generationNumber) {
		this.generationNumber = generationNumber;
	}// END: getCurrentWeights
	
}// END: class
