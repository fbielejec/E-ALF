package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import simulator.linefollowing.EAutonom;
import utils.Utils;

public class Population {

	private int currentIndex;

	private int populationSize = Settings.POPULATION_SIZE;
	private float[] fittness;
	private LinkedHashMap<Integer, float[]> population;
	private ArrayList<float[]> matingPool;

	private int nInputNodes = Settings.INPUT_NODES;
	private int nHiddenNodes = Settings.HIDDEN_NODES;
	private int nOutputNodes = Settings.OUTPUT_NODES;

	// private int nWeights = (nInputNodes + 1) * nHiddenNodes + (nHiddenNodes +
	// 1) * nOutputNodes;
	private int nWeights = (nInputNodes * nHiddenNodes) + (nHiddenNodes * nOutputNodes);

	public Population() {

		this.currentIndex = 0;

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

	}// END: Constructor

//	public void naturalSelection() {
//
//		// Clear the ArrayList
//		matingPool.clear();
//		
//		double maxFitness = 0;
//		for (int i = 0; i < populationSize; i++) {
//
//			double iFitness = population[i].getFitness();
//			if (iFitness > maxFitness) {
//				maxFitness = iFitness;
//			}
//
//		}// END: i loop
//
//		for (int i = 0; i < populationSize; i++) {
//
//			double iFitness = Utils.map(population[i].getFitness(), 0,
//					maxFitness, 0, 1);
//			
//			int n = (int) Math.floor(iFitness * nFittest);
//			for (int j = 0; j < n; j++) {
//
//				matingPool.add(population[i]);
//				
//			}// END: j loop
//
//		}// END: i loop
//
//	}// END: naturalSelection
	
	
	
	public void calculateFitness() {

		// let them live one by one and score them
		// double[] weights = population.get(currentIndex);

		// if (autonom.isAlive()) {
		//
		// autonom.run();
		// autonom.lineFollow();
		//
		// double currentFitness = autonom.getFitness();
		// if(currentFitness > bestFitness) {
		// bestFitness = currentFitness;
		// }
		//
		// } else {
		//
		// currentIndex++;
		//
		// }//END: alive check

	}// END: calculateFitness

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

}// END: class
