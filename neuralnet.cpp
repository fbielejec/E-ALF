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



std::vector<double> NeuralNetwork::getWeights()  {

    std::vector<double> weights;

    //for each layer
    for (int i = 0; i < numHiddenLayers + 1; i++) {

        //for each neuron
        for (int j = 0; j < layers[i].numNeurons; j++) {

            //for each weight
            for (int k = 0; k < layers[i].neurons[j].numInputs; k++) {

                weights.push_back(layers[i].neurons[j].weights[k]);

            }//END: weights loop

        }//END: neuron loop
    }//END: layers loop

    return weights;
}//END: getWeights


void NeuralNetwork::setWeights(std::vector<double> weights) {

    int cWeight = 0;

    // for each layer
    for (int i = 0; i < numHiddenLayers + 1; i++) {

        // for each neuron
        for (int j = 0; j < layers[i].numNeurons; j++) {

            //for each weight
            for (int k = 0; k < layers[i].neurons[j].numInputs; k++) {

                layers[i].neurons[j].weights[k] = weights[cWeight++];

            }//END: weights loop

        }// END: neurons loop
    }// END: layers loop

}// END: setWeights


int NeuralNetwork::getNumberOfWeights() {

    /**
     * @return: total number of weights in the net
     * */

    int nWeights = 0;

    // for each layer
    for (int i = 0; i < numHiddenLayers + 1; i++) {

        // for each neuron
        for (int j = 0; j < layers[i].numNeurons; j++) {

            //for each weight
            for (int k = 0; k < layers[i].neurons[j].numInputs; k++) {

                nWeights++;

            }//END: weights loop

        }// END: neurons loop
    }// END: layers loop

    return nWeights;
}// END: getNumberOfWeights



std::vector<double> NeuralNetwork::update(std::vector<double> inputs) {

    std::vector<double> outputs;
    int cWeight = 0;

    if (inputs.size() != numInputs) {

// System.err.println("Input size (" +inputs.size() +")" + " is different than number of inputs declared (" + this.numInputs +")!");
        return outputs;

    }// size check

    // for each layer
    for (int i = 0; i < numHiddenLayers + 1; i++) {

        if ( i > 0 ) {
            inputs = outputs;
        }//END: i check

        outputs.clear();
        cWeight = 0;

        // for each neuron
        for (int j = 0; j < layers[i].numNeurons; j++) {

            double netinput = 0;
            int nInputs = layers[i].neurons[j].numInputs;

            //for each weight
            for (int k = 0; k < nInputs; k++) {

                // weights x inputs
                netinput += layers[i].neurons[j].weights[k] * inputs[cWeight++];

            }//END: weights loop

            outputs.push_back(netinput);
            cWeight = 0;
        }//END: neurons loop
    }//END: layers loop

    return outputs;
}//END: feedforward














