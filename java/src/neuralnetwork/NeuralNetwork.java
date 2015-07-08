package neuralnetwork;

import java.util.LinkedList;

public class NeuralNetwork {

	private int numInputs;
	private int numOutputs;
	private int numHiddenLayers;
	private int neuronsPerHiddenLayer;

	private LinkedList<NeuronLayer> layers;

	public NeuralNetwork() {

		this.numInputs = Parameters.numInputs;
		this.numOutputs = Parameters.numOutputs;
		this.numHiddenLayers = Parameters.numHiddenLayers;
		this.neuronsPerHiddenLayer = Parameters.neuronsPerHiddenLayer;

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

	public void putWeights(LinkedList<Double> weights) {

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
				for (int k = 0; k < neuron.getnInputs(); k++) {
					// TODO: via set weights
					neuron.getWeights().set(k, weights.get(weight++));
				}

			}// END: neurons loop
		}// END: layers loop

	}// END: putWeights

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
				for (int k = 0; k < neuron.getnInputs(); k++) {

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
			return outputs;

		}// END: input size check

		// For each layer
		for (int i = 0; i < numHiddenLayers + 1; i++) {

			if (i > 0) {

				inputs = outputs;

			}

			outputs.clear();

			// for each neuron in layer
			NeuronLayer layer = layers.get(i);
			for (Neuron neuron : layer.getNeurons()) {

				double netinput = neuron.feedforward(inputs);
				outputs.add(sigmoid(netinput, Parameters.response));

			}// END: neurons loop

		}// END: hidden layers loop

		return outputs;
	}// END: update

	// sigmoid response curve
	public double sigmoid(double activation, double response) {
		return (1 / (1 + Math.exp(-activation / response)));
	}// END: sigmoid

	public NeuralNetwork crossover(NeuralNetwork neuralNetwork) {

		
		
		
		
		return null;
	}

	public void mutate(double mutationRate) {
		// TODO Auto-generated method stub

	};

}// END: class
