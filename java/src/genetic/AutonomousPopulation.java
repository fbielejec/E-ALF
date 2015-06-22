package genetic;

import java.util.ArrayList;

import linefollowing.Autonom;
import linefollowing.Line;


import neuralnetwork.SteeringPerceptron;

import processing.core.PApplet;
import processing.core.PVector;
import utils.Utils;

public class AutonomousPopulation {

	private PApplet parent;
	private Line line;

	private int populationSize = 10;
	private int nFittest = 100;
	private double mutationRate = 0.01;

	private Autonom[] population;
	private ArrayList<Autonom> matingPool;
	private int currentIndex;
	private int generationNumber;
	private double bestFitness;

	public AutonomousPopulation(PApplet p, Line line) {

		this.parent = p;
		this.line = line;

		matingPool = new ArrayList<Autonom>();
		population = new Autonom[this.populationSize];

		for (int i = 0; i < this.populationSize; i++) {

			float xpos = parent.random(0, parent.width);
			float ypos = parent.random(0, parent.height);

			population[i] = new Autonom(parent, new PVector(xpos, ypos));
		}// END: i loop

		currentIndex = 0;
		generationNumber = 0;
		bestFitness = 0;
	}// END: Constructor

	public void naturalSelection() {

		// matingPool.clear();

		// let them live for a while
		if (currentIndex < populationSize) {

			Autonom autonom = population[currentIndex];

			if (autonom.isAlive()) {

				autonom.lineFollow(line);
				autonom.run();

			} else {

				currentIndex++;

			}// END: alive check

		} else {

			// score them
			double bestPopulationFitness = 0;
			for (int i = 0; i < populationSize; i++) {

				double iFitness = population[i].getFitness();
				if (iFitness > bestPopulationFitness) {
					bestPopulationFitness = iFitness;
					// bestIndex = i;
				}

			}// END: i loop

			// generate mating pool
			matingPool.clear();
			for (int i = 0; i < populationSize; i++) {

				double iFitness = Utils.map(population[i].getFitness(), 0,
						bestPopulationFitness, 0, 1);
				int n = (int) iFitness * nFittest;
				for (int j = 0; j < n; j++) {
					matingPool.add(population[i]);
				}// END: j loop

			}// END: i loop

			this.generate();
			currentIndex = 0;
			for (int i = 0; i < populationSize; i++) {
				population[i].setAlive();
			}

			if (bestPopulationFitness > this.bestFitness) {
				this.bestFitness = bestPopulationFitness;
			}

		}// END: popSize check

	}// END: naturalSelection

	private void generate() {

		//TODO
		System.out.println(matingPool.size());
		
		// Refill the population with children from the mating pool
		for (int i = 0; i < populationSize; i++) {

			int a = (int) Utils.randomInt(0, matingPool.size() - 1);
			int b = (int) Utils.randomInt(0, matingPool.size() - 1);

			SteeringPerceptron parentA = matingPool.get(a).getPerceptron();
			SteeringPerceptron parentB = matingPool.get(b).getPerceptron();

			SteeringPerceptron child = parentA.crossover(parentB);
			child.mutate(mutationRate);

			population[i].setPerceptron(child);

		}// END: i loop

		generationNumber++;
	}// END: generate

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getGenerationNumber() {
		return generationNumber;
	}

	public double getCurrentFitness() {

		double fitness = 0;
		if (currentIndex < populationSize) {
			fitness = population[currentIndex].getFitness();
		}
		return fitness;
	}

	public double getBestFitness() {
		return bestFitness;
	}

}// END: class
