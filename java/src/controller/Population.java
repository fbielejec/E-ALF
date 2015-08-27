package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import utils.Utils;

public class Population {

	private LinkedHashMap<Integer, double[]> population;
	private int populationSize = Settings.POPULATION_SIZE;

	private int currentIndex;
	
	private ArrayList<double[]> matingPool;
	
	
	private int nInputNodes = Settings.INPUT_NODES;
	private int nHiddenNodes = Settings.HIDDEN_NODES;
	private int nOutputNodes = Settings.OUTPUT_NODES;

	private int nWeights = nInputNodes * nHiddenNodes + nHiddenNodes
			* nOutputNodes;

	public Population() {

		this.population = new LinkedHashMap<Integer, double[]>();
		for (int i = 0; i < populationSize; i++) {

			double[] weights = new double[nWeights];
			for (int j = 0; j < nWeights; j++) {

				weights[j] = Utils.randomDouble(-1, 1);

			}// END: j loop

			population.put(i, weights);

		}// END: population loop

		this.currentIndex = 0;
		this.matingPool = new ArrayList<double[]>();

		
	}// END: Constructor

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}// END: class
