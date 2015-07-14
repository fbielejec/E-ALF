package neuralnetwork;

import java.util.LinkedList;

import utils.Utils;

public class Neuron {

	// weights are genes
	private LinkedList<Double> weights;
    private int nInputs;
	
	public Neuron(int nInputs) {

		this.nInputs = nInputs;
		this.weights = new LinkedList<Double>();
		
		// additional weight for bias [a threshold]
		for (int i = 0; i < this.nInputs + 1; i++) {
			
			Double rWeight = Utils.randomDouble(-1, 1);
			this.weights.add(i, rWeight);

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
	
	public int getnInputs() {
		return nInputs;
	}
	
	public int getnWeights() {
		return weights.size();
	}
	
	public LinkedList<Double> getWeights() {
		return weights;
	}// END: getWeights

	public void setWeights(LinkedList<Double> weights) {
		this.weights = weights;
	}//END: setWeights
	
}// END: class
