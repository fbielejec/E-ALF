package test.patternrecognition;

import java.util.ArrayList;
import java.util.LinkedList;

import neuralnetwork.NeuralNetwork;
import utils.Utils;

public class NeuralNetworkPopulation {

	private double[][] inputs;
	// private int[] target;
	private double mutationRate;
	private int populationSize;
	private int nFittest;

	private NeuralNetwork[] population;
	private ArrayList<NeuralNetwork> matingPool;

	private int nGenerations;
	private int bestIndex;
	private double perfectScore;
	private boolean finished = false;

	public NeuralNetworkPopulation(int nWeights, int populationSize,
			int nFittest, double mutationRate, double[][] inputs) {

		this.inputs = inputs;
		// this.target = target;
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.nFittest = nFittest;
		this.nGenerations = 0;
		this.perfectScore = 2000;

		matingPool = new ArrayList<NeuralNetwork>();
		population = new NeuralNetwork[this.populationSize];

		for (int i = 0; i < this.populationSize; i++) {
			population[i] = new NeuralNetwork();
		}// END: i loop

	}// END: Constructor

	public void naturalSelection() {

		// Clear the ArrayList
		matingPool.clear();

		double bestFitness = 0;
		for (int i = 0; i < populationSize; i++) {

			double iFitness = population[i].getFitness();
			if (iFitness > bestFitness) {
				bestFitness = iFitness;
				bestIndex = i;
			}

		}// END: i loop

		if (bestFitness >= perfectScore) {
			finished = true;
		}

		for (int i = 0; i < populationSize; i++) {

			double iFitness = Utils.map(population[i].getFitness(), 0,
					bestFitness, 0, 1);
			int n = (int) iFitness * nFittest;
			for (int j = 0; j < n; j++) {
				matingPool.add(population[i]);
			}// END: j loop

		}// END: i loop

	}// END: naturalSelection

	public void generate() {

		// Refill the population with children from the mating pool
		for (int i = 0; i < populationSize; i++) {

			int a = (int) Utils.randomInt(0, matingPool.size() - 1);
			int b = (int) Utils.randomInt(0, matingPool.size() - 1);

			NeuralNetwork parentA = matingPool.get(a);
			NeuralNetwork parentB = matingPool.get(b);

			NeuralNetwork child = parentA.crossover(parentB);
			child.mutate(mutationRate);

			population[i] = child;

		}// END: i loop

		nGenerations++;
	}// END: generate

	private static int activate(double sum) {
		if (sum > 0) {
			return 1;
		} else {
			return -1;
		}
	}// END: activate

	public static int guess(NeuralNetwork neuralNetwork, double[] inputs) {

		LinkedList<Double> in = new LinkedList<Double>();
		in.add(0, inputs[0]);
		in.add(1, inputs[1]);

		LinkedList<Double> output = neuralNetwork.update(in);

		int guess = activate(output.get(0));

		return guess;
	}// END: guess

	public void calculateFitness(int[] targets) {

		for (int i = 0; i < populationSize; i++) {

			double fitness = 0;
			NeuralNetwork neuralNetwork = population[i];

			for (int j = 0; j < targets.length; j++) {

				int guess = guess(neuralNetwork, inputs[j]);
				int target = targets[j];

				if (guess == target) {
					fitness++;
				}

			}// END: data loop

			neuralNetwork.setFitness(fitness);

		}// END: individuals loop

	}// END: calculateFitness

	public NeuralNetwork getBestIndividual() {
		return population[bestIndex];
	}// END: getBestIndividual

	public int getGenerationNumber() {
		return nGenerations;
	}// END: getGenerations

	public int getPopulationSize() {
		return populationSize;
	}// END: getGenerations

	public boolean isFinished() {
		return finished;
	}

}// END: class
