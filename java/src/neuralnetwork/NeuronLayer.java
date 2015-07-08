package neuralnetwork;

import java.util.LinkedList;

public class NeuronLayer {

	// the number of neurons in this layer
	private int numNeurons;
	private int numInputsPerNeuron;
	// the layer of neurons
	private LinkedList<Neuron> neurons;

	public NeuronLayer(int numNeurons, int numInputsPerNeuron) {

		this.numNeurons = numNeurons;
		this.numInputsPerNeuron = numInputsPerNeuron;

		createLayer();

	}// END: Construtor

	public void createLayer() {

		for (int i = 0; i < numNeurons; i++) {
			neurons.add(new Neuron(numInputsPerNeuron));
		}// END: neurons loop

	}// END: createLayer

	public int getNumNeurons() {
		return numNeurons;
	}// END: getNumNeurons

	public LinkedList<Neuron> getNeurons() {
		return neurons;
	}

}// END: class
