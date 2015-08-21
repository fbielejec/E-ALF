#include <StandardCplusplus.h>
#include <vector>
#include <Arduino.h>
#include "utils.h"


class Neuron {

private :

    // inputs including bias
    int numInputs;

    // weights are genes
    std::vector<double> weights;


public:

    // Constructor
    Neuron(int numInputs);

    int getnInputs();

    std::vector<double> getWeights();

};


class NeuronLayer {

private:

    // the number of neurons in this layer
    int numNeurons;
    int numInputsPerNeuron;
    // the layer of neurons
    std::vector<Neuron> neurons;

public:

    // Constructor
    NeuronLayer(int numNeurons, int numInputsPerNeuron);

    void createLayer();

    int getNumNeurons();

    std::vector<Neuron>  getNeurons();

};

class NeuralNetwork {

private:

    int numInputs;
    int numOutputs;
    int numHiddenLayers;
    int neuronsPerHiddenLayer;
    std::vector<NeuronLayer> layers;

public:

    // Constructor
    NeuralNetwork();

    void createNetwork();

    std::vector<double> getWeights();

    void setWeights(std::vector<double> weights);

    int getNumberOfWeights();

    std::vector<double> update(std::vector<double> inputs);

};
