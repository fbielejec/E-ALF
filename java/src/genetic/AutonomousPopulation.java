package genetic;

import java.util.ArrayList;

import linefollowing.Autonom;
import linefollowing.Line;
import processing.core.PApplet;
import processing.core.PVector;
import utils.Utils;

public class AutonomousPopulation {

	private PApplet parent;
	private Line line;

	private int populationSize = 5;
	private double mutationRate = 0.1;
	private int nFittest = 100;
	private Autonom[] population;
	private ArrayList<Autonom> matingPool;

	private int currentIndex;
	private int generationNumber;
	private double bestFitness;

	public AutonomousPopulation(PApplet p, Line line) {

		this.parent = p;
		this.line = line;

		this.population = new Autonom[this.populationSize];
		for (int i = 0; i < populationSize; i++) {

			float xpos = parent.width / 2;
			float ypos = parent.height / 2;
			PVector startLocation = new PVector(xpos, ypos);

			population[i] = new Autonom(parent, startLocation, this.line);

		}// END: population loop

		this.matingPool = new ArrayList<Autonom>();

		this.currentIndex = 0;
		this.generationNumber = 0;
		this.bestFitness = 0;
	}// END: Constructor

	public void calculateFitness() {

		// let them live one by one and score them
		Autonom autonom = population[currentIndex];
		if (autonom.isAlive()) {

			autonom.performTask( );
			autonom.run();

			double currentFitness = autonom.getFitness();
			if(currentFitness > bestFitness) {
				bestFitness = currentFitness;
			}
			
		} else {

			currentIndex++;

		}

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

		}// END: i loop

		for (int i = 0; i < populationSize; i++) {

			double iFitness = Utils.map(population[i].getFitness(), 0,
					maxFitness, 0, 1);
			int n = (int) iFitness * nFittest;
			for (int j = 0; j < n; j++) {
				matingPool.add(population[i]);
			}// END: j loop

		}// END: i loop

	}// END: naturalSelection

	public void generate() {

		// Refill the population with children from the mating pool
		for (int i = 0; i < populationSize ; i++) {

//			System.out.println(matingPool.size());
			
			int a = (int) Utils.randomInt(0, matingPool.size() - 1);
			int b = (int) Utils.randomInt(0, matingPool.size() - 1);

			Autonom parentA = matingPool.get(a);
			Autonom parentB = matingPool.get(b);

			Autonom child = parentA.crossover(parentB);
			child.mutate(mutationRate);

			population[i] = child;

		}// END: i loop
		
		//reset index
		currentIndex = 0;
		generationNumber++;
	}// END: generate

	public int getCurrentIndex() {
		return currentIndex;
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

	public double[] getCurrentWeights() {
		return population[currentIndex].getPerceptron().getWeights();
	}//END: getCurrentWeights
	
	public double getBestFitness() {
		return bestFitness;
	}// END: getBestFitness

	public double[] getCurrentVelocity() {
		 return population[currentIndex].getVelocities();
	}

}// END: class
