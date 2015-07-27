#include <vector>
#include "utils.h"


using namespace std;

struct Neuron {

 // inputs including bias
// int in_numInputs;
 int numInputs;

 // weights are genes
 vector<double> weights;

 // Constructor
 Neuron(int numInputs);

};


struct NeuronLayer {

   // the number of neurons in this layer
//	 int in_numNeurons;
   int numNeurons;
	 int numInputsPerNeuron;
	 // the layer of neurons
	 vector<Neuron> neurons;

  // Constructor
  NeuronLayer(int numNeurons, int numInputsPerNeuron);

};


class NeuralNetwork {

private:

  int numInputs;
	int numOutputs;
	int numHiddenLayers;
  int neuronsPerHiddenLayer;
  vector<NeuronLayer> layers;

public:

  NeuralNetwork();

	void createNetwork();

  vector<double> getWeights();

  void setWeights(vector<double> weights);

  int getNumberOfWeights();

  vector<double> update(vector<double> inputs);




};
