package simulator.neuralnetwork;

import java.util.LinkedList;

import utils.Parameters;
import utils.Utils;

public class NeuralNetwork {

	private int numInputs;
	private int numOutputs;
	private int numHiddenLayers;
	private int neuronsPerHiddenLayer;
	private LinkedList<NeuronLayer> layers;

	private double fitness;

	public NeuralNetwork() {

		this.numInputs = Parameters.numInputs;
		this.numOutputs = Parameters.numOutputs;

		this.numHiddenLayers = Parameters.numHiddenLayers;
		this.neuronsPerHiddenLayer = Parameters.neuronsPerHiddenLayer;
		this.layers = new LinkedList<NeuronLayer>();

		createNetwork();

	}// END: Constructor

	void createNetwork() {

		// create the layers of the network
		if (numHiddenLayers > 0) {

			// create first hidden layer
			layers.add(new NeuronLayer(neuronsPerHiddenLayer, numInputs));

			// create the rest of hidden layers
			for (int i = 0; i < numHiddenLayers - 1; i++) {

				layers.add(new NeuronLayer(neuronsPerHiddenLayer,
						neuronsPerHiddenLayer));

			}// END: i loop

			// create output layer
			layers.add(new NeuronLayer(numOutputs, neuronsPerHiddenLayer));

		} else {

			// create output layer
			layers.add(new NeuronLayer(numOutputs, numInputs));

		}// END: numHiddenLayers check

	}// END: createNetwork

	public LinkedList<Double> getWeights() {

		LinkedList<Double> weights = new LinkedList<>();

		// for each layer
		for (int i = 0; i < numHiddenLayers + 1; i++) {

			// for each neuron in layer
			NeuronLayer layer = layers.get(i);
			for (Neuron neuron : layer.getNeurons()) {

				LinkedList<Double> neuronWeights = neuron.getWeights();
				weights.addAll(neuronWeights);

			}// END: neurons loop

		}// END: layers loop

		return weights;
	}// END: getWeights

	public void setWeights(LinkedList<Double> weights) {

		/**
		 * Replace the weights with new ones
		 * */

		int weight = 0;

		// for each layer
		for (int i = 0; i < numHiddenLayers + 1; i++) {

			// for each neuron in layer
			NeuronLayer layer = layers.get(i);
			for (Neuron neuron : layer.getNeurons()) {

				// for each weight
				LinkedList<Double> neuronWeights = neuron.getWeights();
				for (int k = 0; k < neuron.getnInputs(); k++) {
					
					neuronWeights.set(k, weights.get(weight++));
					
				}// END: inputs loop

				neuron.setWeights(neuronWeights);

			}// END: neurons loop
		}// END: layers loop

	}// END: setWeights

	public int getNumberOfWeights() {

		/**
		 * @return: total number of weights in the net
		 * */

		int nWeights = 0;

		// for each layer
		for (int i = 0; i < numHiddenLayers + 1; i++) {

			// for each neuron in layer
			NeuronLayer layer = layers.get(i);
			for (Neuron neuron : layer.getNeurons()) {

				// for each weight
				// for (int k = 0; k < neuron.getnInputs(); k++) {
				for (int k = 0; k < neuron.getnWeights(); k++) {

					nWeights++;

				}

			}// END: neurons loop
		}// END: layers loop

		return nWeights;
	}// END: getNumberOfWeights

	public LinkedList<Double> update(LinkedList<Double> inputs) {

		/**
		 * Calculate the neural response for a set of inputs
		 * */

		// stores the resulting outputs from each layer
		LinkedList<Double> outputs = new LinkedList<Double>();

		// first check that we have the correct amount of inputs
		if (inputs.size() != this.numInputs) {

			// just return an empty vector if incorrect.

			System.err.println("Input size (" +inputs.size() +")" + " is different than number of inputs declared (" + this.numInputs +")!");
			return outputs;
			
		}// END: input size check

		// For each layer
		for (int i = 0; i < numHiddenLayers + 1; i++) {

			if (i > 0) {
				
//				inputs.clear();
//				inputs.addAll(outputs);
				
				inputs = outputs;

			}

//			outputs.clear();

			// for each neuron in layer
			NeuronLayer layer = layers.get(i);
			for (Neuron neuron : layer.getNeurons()) {

				
				double netinput = neuron.feedforward(inputs);
				outputs.add(netinput);

			}// END: neurons loop

		}// END: hidden layers loop

		return outputs;
	}// END: update

	public double getFitness() {
		return fitness;
	}// END: getFitness

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}// END: getFitness

	public NeuralNetwork crossover(NeuralNetwork partner) {

		LinkedList<Double> weights = this.getWeights();
		LinkedList<Double> partnerWeights = partner.getWeights();

		LinkedList<Double> childWeights = new LinkedList<Double>();
		for (int i = 0; i < weights.size(); i++) {
			childWeights.add(Double.NaN);
		}

		int midpoint = Utils.randomInt(0, weights.size());
		for (int i = 0; i < weights.size(); i++) {

			if (i > midpoint) {

				childWeights.set(i, weights.get(i));

			} else {

				childWeights.set(i, partnerWeights.get(i));

			}// END: midpoint check

		}// END: i loop

		NeuralNetwork child = new NeuralNetwork();
		child.setWeights(childWeights);

		return child;
	}// END: crossover

	public void mutate(double mutationRate) {

		LinkedList<Double> weights = this.getWeights();
		for (int i = 0; i < this.getNumberOfWeights(); i++) {

			if (Utils.runif() < mutationRate) {

				Double rWeight = Utils.randomDouble(-1, 1);
				weights.set(i, rWeight);
				// this.getWeights().set(i, rWeight);

			}// END: mutationRate check

		}// END: i loop

		this.setWeights(weights);

	}// END: mutate

}// END: class
