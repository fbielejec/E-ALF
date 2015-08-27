package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import simulator.linefollowing.EAutonom;
import utils.Utils;

public class Population {

	private LinkedHashMap<Integer, float[]> population;
	private int populationSize = Settings.POPULATION_SIZE;

	private int currentIndex;
	
	private ArrayList<float[]> matingPool;
	
	
	private int nInputNodes = Settings.INPUT_NODES;
	private int nHiddenNodes = Settings.HIDDEN_NODES;
	private int nOutputNodes = Settings.OUTPUT_NODES;

	private int nWeights = nInputNodes * nHiddenNodes + nHiddenNodes
			* nOutputNodes;

	public Population() {

		this.population = new LinkedHashMap<Integer, float[]>();
		for (int i = 0; i < populationSize; i++) {

			float[] weights = new float[nWeights];
			for (int j = 0; j < nWeights; j++) {

				weights[j] = (float)Utils.randomDouble(-1, 1);

			}// END: j loop

			population.put(i, weights);

		}// END: population loop

		this.currentIndex = 0;
		this.matingPool = new ArrayList<float[]>();

		
	}// END: Constructor

	
	public void calculateFitness() {

		// let them live one by one and score them
//		double[] weights = population.get(currentIndex);
		
		
		
		
//		if (autonom.isAlive()) {
//			
//			autonom.run();
//			autonom.lineFollow();
//			
//			double currentFitness = autonom.getFitness();
//			if(currentFitness > bestFitness) {
//				bestFitness = currentFitness;
//			}
//			
//		} else {
//
//			currentIndex++;
//
//		}//END: alive check

	}// END: calculateFitness
	
	
	
	
	
	
	
	
	public float[] getCurrentWeights() {
		return( population.get(currentIndex));
	}
	
	
	
	
	
	
	
	
}// END: class
