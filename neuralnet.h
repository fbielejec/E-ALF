#include <StandardCplusplus.h>
#include <vector>
#include "utils.h"

struct Neuron {

    // inputs including bias
    int numInputs;

    // weights are genes
    std:: vector<double> weights;

    // Constructor
    Neuron(int numInputs);

};


struct NeuronLayer {

    // the number of neurons in this layer
    int numNeurons;
    int numInputsPerNeuron;
    // the layer of neurons
    std::vector<Neuron> neurons;

    // Constructor
    NeuronLayer(int numNeurons, int numInputsPerNeuron);

};


class NeuralNetwork {

private:

    int numInputs;
    int numOutputs;
    int numHiddenLayers;
    int neuronsPerHiddenLayer;
    std::vector<NeuronLayer> layers;

public:

    NeuralNetwork();

    void createNetwork();

    std::vector<double> getWeights();

    void setWeights(std::vector<double> weights);

    int getNumberOfWeights();

    std::vector<double> update(std::vector<double> inputs);

};
