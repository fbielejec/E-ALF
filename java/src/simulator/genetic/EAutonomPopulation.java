package simulator.genetic;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

	private int currentIndex;
	private int generationNumber;
	private double bestFitness;

	private PrintWriter writer;

	public EAutonomPopulation(PApplet p, Line line) throws FileNotFoundException, UnsupportedEncodingException {

		this.parent = p;
		this.line = line;

		this.population = new EAutonom[this.populationSize];
		for (int i = 0; i < populationSize; i++) {

			population[i] = new EAutonom(parent, this.line);

		} // END: population loop

		this.matingPool = new ArrayList<EAutonom>();

		this.currentIndex = 0;
		this.generationNumber = 0;
		this.bestFitness = 0;

		// logging
		this.writer = new PrintWriter("/home/filip/Pulpit/fitness_simulation.log", "UTF-8");
		String header = "generation" + Utils.TAB + "individual" + Utils.TAB + "fitness" + Utils.TAB;
		for (int i = 0; i < this.getNWeights(); i++) {

			String w = "w" + i;
			header += w + Utils.TAB;

		}
		writer.println(header);

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
			}

			// ---LOGGING---//

			// log data
			while (true) {
				
				System.out.println("Writing to log file ");
				int currentIndex = this.getCurrentIndex();
				
				String line = this.getGenerationNumber() + Utils.TAB + currentIndex + Utils.TAB
						+ this.getCurrentFitness() + Utils.TAB;
				for (Double w : this.getCurrentWeights()) {

					line += w + Utils.TAB;

				}

				writer.println(line);
				writer.flush();

				break;
			} // END: logging loop

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

		// reset index
		currentIndex = 0;
		generationNumber++;
	}// END: generate

//	public void setMaxspeed(float maxspeed) {
//
//		for (int i = 0; i < populationSize; i++) {
//
//			EAutonom autonom = population[i];
//			if (autonom.isAlive()) {
//
//				autonom.setMaxspeed(maxspeed);
//
//			} // END: alive check
//		} // END: population loop
//	}// END: setMaxspeed

	public void setLifespan(int lifespan) {
		for (int i = 0; i < populationSize; i++) {

			EAutonom autonom = population[i];
			if (autonom.isAlive()) {

				autonom.setLifespan(lifespan);

			} // END: alive check
		} // END: population loop
	}// END: setLifespan

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

	public LinkedList<Double> getCurrentWeights() {
		return population[currentIndex].getNeuralNetwork().getWeights();
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
