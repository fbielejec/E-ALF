package neuralnetwork;

import java.util.LinkedList;

import utils.Utils;

public class Neuron {

	// weights are genes
	private LinkedList<Double> weights;
    private int nInputs;
	
	public int getnInputs() {
		return nInputs;
	}

	public Neuron(int n) {

		// additional weight for bias [a threshold]
		for (int i = 0; i < nInputs + 1; i++) {
			
			Double rWeight = Utils.randomDouble(-1, 1);
			weights.add(i, rWeight);

		}//END: weights loop

	}// END: Constructor

	public double feedforward(LinkedList<Double> inputs) {
		
		
		double sum = 0;
		for (int i = 0; i < nInputs - 1; i++) {
			
			sum += weights.get(i) * inputs.get(i);
			
		}
		
		// add bias 
		sum += weights.get(nInputs - 1) * Parameters.bias;
		
		return sum;
	}//END: feedforward
	
	
//	public double[] feedforward(double[] input) {
//		
//		double[] sum = new double[genes.length];
//		for (int i = 0; i < genes.length; i++) {
//			
//			for(int j = 0; j < sum.length; j++) {
//				input[j] = input[j] * genes[i]; 
//				sum[j] = sum[j] + input[j]; 
//			}
//			
//		}
//		
//		return sum;
//	}// END: feedforward

	public Neuron crossover(Neuron partner) {

		Neuron child = new Neuron(weights.size());

		int midpoint = Utils.randomInt(0, weights.size());
		for (int i = 0; i < weights.size(); i++) {

			if (i > midpoint) {
				
				// TODO: via set weights
				child.getWeights().set(i, this.weights.get(i));
				
			} else {

				// TODO: via set weights
				child.getWeights().set(i, partner.getWeights().get(i));
				
			}// END: midpoint check

		}// END: i loop

		return child;
	}// END: crossover

	public void mutate(double mutationRate) {

		for (int i = 0; i < weights.size(); i++) {

			if (Utils.runif() < mutationRate) {
				
				Double rWeight = Utils.randomDouble(-1, 1);
				weights.set(i, rWeight);
			
			}// END: mutationRate check

		}// END: i loop

	}// END: mutate

	public LinkedList<Double> getWeights() {
		return weights;
	}// END: getWeights

}// END: class
