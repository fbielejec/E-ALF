package neuralnetwork;

import processing.core.PApplet;
import processing.core.PVector;
import utils.Utils;

public class SteeringPerceptron {

	// weights are genes
	private double[] genes;
//	private double fitness;

	public SteeringPerceptron(int n) {

		this.genes = new double[n];
		for (int i = 0; i < genes.length; i++) {
			genes[i] = Utils.randomDouble(-1, 1);
		}

	}// END: Constructor

	public double[] feedforward(double[] input) {
		double[] sum = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
		      sum[i] += (input[i] * genes[i]);
		}
		return sum;
	}//END: feedforward
	
	
//	public void calculateFitness(double score) {
//		this.fitness = score;
//	}// END: calculateFitness

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

//	public double getFitness() {
//		return fitness;
//	}// END: getFitness

	public double[] getWeights() {
		return genes;
	}// END: getWeights

}// END: class
