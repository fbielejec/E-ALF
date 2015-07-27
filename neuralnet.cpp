#include "neuralnet.h"
#include "parameters.h"

Neuron::Neuron(int numInputs) {//} : in_numInputs(numInputs) {

    for (int i = 0; i < numInputs + 1; i++) {

        weights.push_back(randomClamped());

    }//END: i loop

}//END: Neuron

NeuronLayer::NeuronLayer(int numNeurons, int numInputsPerNeuron) {//} :	in_numNeurons(numNeurons) {

    for (int i = 0; i < numNeurons; i++) {

        neurons.push_back(Neuron(numInputsPerNeuron));

    }//END: i loop

}//END: NeuronLayer

/**
* Neural Network methods
*/

NeuralNetwork::NeuralNetwork() {
    numInputs	          =	Parameters::numInputs;
    numOutputs		      =	Parameters::numOutputs;
    numHiddenLayers	    =	Parameters::numHiddenLayers;
    neuronsPerHiddenLayer =	Parameters::neuronsPerHiddenLayer;

    createNetwork();

}//END: COnstructor

void NeuralNetwork::createNetwork() {





}//END: createNetwork
