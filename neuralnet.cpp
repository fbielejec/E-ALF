#include "neuralnet.h"
#include "parameters.h"

/**
* Neuron methods
*/

Neuron::Neuron(int numInputs) {

    this->numInputs = numInputs;

    for (int i = 0; i < numInputs + 1; i++) {

        weights.push_back(randomClamped());

    }//END: i loop

}//END: Neuron

int Neuron::getnInputs() {
    return numInputs;
}//END: getnInputs

std::vector<float> Neuron::getWeights() {
    return weights;
}//END: getWeights

/**
* Neuron Layer methods
*/

NeuronLayer::NeuronLayer(int numNeurons, int numInputsPerNeuron) {

    this->numNeurons = numNeurons;
    this->numInputsPerNeuron = numInputsPerNeuron;

    for (int i = 0; i < numNeurons; i++) {

        neurons.push_back(Neuron(numInputsPerNeuron));

    }//END: i loop

}//END: NeuronLayer


int NeuronLayer::getNumNeurons() {
    return numNeurons;
}//END: getNumNeurons


std::vector<Neuron> NeuronLayer::getNeurons() {
    return neurons;
}//END: getNeurons


/**
* Neural Network methods
*/

NeuralNetwork::NeuralNetwork() {

    numInputs	          =	Parameters::numInputs;
    numOutputs		      =	Parameters::numOutputs;
    numHiddenLayers	    =	Parameters::numHiddenLayers;
    neuronsPerHiddenLayer =	Parameters::neuronsPerHiddenLayer;

//    createNetwork();

}//END: Constructor

void NeuralNetwork::createNetwork() {

    // create the layers of the network
    if (numHiddenLayers > 0) {

        // create first hidden layer
        layers.push_back( NeuronLayer(neuronsPerHiddenLayer, numInputs) );

        // create the rest of hidden layers
        for (int i = 0; i < numHiddenLayers - 1; i++) {

            layers.push_back( NeuronLayer(neuronsPerHiddenLayer, neuronsPerHiddenLayer));

        }//END: i loop

        // create output layer
        layers.push_back( NeuronLayer(numOutputs, neuronsPerHiddenLayer));

    } else {

        //create output layer
        layers.push_back(NeuronLayer(numOutputs, numInputs));

    }// END: numHiddenLayers check

}//END: createNetwork

std::vector<float> NeuralNetwork::getWeights()  {

    std::vector<float> weights;

    for (int i = 0; i < numHiddenLayers + 1; i++) {

        NeuronLayer layer = layers[i];
        for (int j = 0; j < layer.getNumNeurons(); j++) {

            Neuron neuron = layer.getNeurons().at(j);
            for (int k = 0; k < neuron.getnInputs(); k++) {

                weights.push_back(neuron.getWeights().at(k));

            }//END: weights loop

        }//END: neurons loop
    }//END: layers loop

    return weights;
}//END: getWeights


void NeuralNetwork::setWeights(std::vector<float> weights) {

    int cWeight = 0;

    for (int i = 0; i < numHiddenLayers + 1; i++) {

        NeuronLayer layer = layers[i];
        for (int j = 0; j < layer.getNumNeurons(); j++) {

            Neuron neuron = layer.getNeurons().at(j);
            for (int k = 0; k < neuron.getnInputs(); k++) {

//TODO: set weights
                neuron.getWeights().at(k) = weights[cWeight++];

            }//END: weights loop

        }// END: neurons loop
    }// END: layers loop

}// END: setWeights


int NeuralNetwork::getNumberOfWeights() {

    /**
     * @return: total number of weights in the net
     * */

    int nWeights = 0;

    for (int i = 0; i < numHiddenLayers + 1; i++) {

        NeuronLayer layer = layers[i];
        for (int j = 0; j < layer.getNumNeurons(); j++) {

            Neuron neuron = layer.getNeurons().at(j);
            for (int k = 0; k < neuron.getnInputs(); k++) {

                nWeights++;

            }//END: weights loop

        }// END: neurons loop
    }// END: layers loop

    return nWeights;
}// END: getNumberOfWeights


std::vector<float> NeuralNetwork::update(std::vector<float> inputs) {

    std::vector<float> outputs;
    int cWeight = 0;

    if (inputs.size() != numInputs) {

        Serial.println("Input size is different than number of inputs declared!");

        return outputs;

    }// size check


    // for each layer
    for (int i = 0; i < numHiddenLayers + 1; i++) {

        if ( i > 0 ) {
            inputs = outputs;
        }//END: i check

        outputs.clear();
        cWeight = 0;

        NeuronLayer layer = layers[i];
        for (int j = 0; j < layer.getNumNeurons(); j++) {

            float netinput = 0;
            Neuron neuron = layer.getNeurons().at(j);
            int nInputs = neuron.getnInputs();

            for (int k = 0; k < nInputs; k++) {

                // weights x inputs
                netinput += neuron.getWeights().at(k)  * inputs.at(cWeight++);

            }//END: weights loop

            outputs.push_back(netinput);
            cWeight = 0;

        }//END: neurons loop
    }//END: layers loop

    return outputs;
}//END: feedforward

