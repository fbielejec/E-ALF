package neuralnetwork;

import processing.core.PVector;
import utils.Utils;

public class SteeringPerceptron {

	// weights are genes
	private double[] genes;

	public SteeringPerceptron(int n) {

		this.genes = new double[n];
		for (int i = 0; i < genes.length; i++) {
			genes[i] = Utils.randomDouble(-1, 1);
		}

	}// END: Constructor

//	  PVector feedforward(PVector[] forces) {
//		    
//	    // Sum all values
//	    PVector sum = new PVector();
//	    for (int i = 0; i < genes.length; i++) {
//	      forces[i].mult(genes[i]);
//	      sum.add(forces[i]);
//	    }
//	    
//	    return sum;
//	  }
	
	public double[] feedforward(double[] input) {
		
		double[] sum = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
			
			for(int j = 0; j < sum.length; j++) {
				input[j] = input[j] * genes[i]; 
				sum[j] = sum[j] + input[j]; 
			}
			
		}
		
		return sum;
	}// END: feedforward

	public SteeringPerceptron crossover(SteeringPerceptron partner) {

		SteeringPerceptron child = new SteeringPerceptron(genes.length);

		int midpoint = Utils.randomInt(0, genes.length);
		for (int i = 0; i < genes.length; i++) {

			if (i > midpoint) {
				child.genes[i] = genes[i];
			} else {
				child.genes[i] = partner.genes[i];
			}// END: midpoint check

		}// END: i loop

		return child;
	}// END: crossover

	public void mutate(double mutationRate) {

		for (int i = 0; i < genes.length; i++) {

			if (Utils.runif() < mutationRate) {
				genes[i] = Utils.randomDouble(-1, 1);
			}// END: mutationRate check

		}// END: i loop

	}// END: mutate

	public double[] getWeights() {
		return genes;
	}// END: getWeights

}// END: class
