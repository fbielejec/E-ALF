#include <Arduino.h>
#include "utils.h"

/**---CONSTANTS---*/

// including bias
const int INPUT_NODES = 4; // 2
const int HIDDEN_NODES = 2; // 6
const int OUTPUT_NODES = 2; // 1

// sigmoid function activation
const float SIGMOID_ACTIVATION = 0.5;

/**---PROTOTYPES---*/

int createNetwork();

int getnWeights();

int feedforward(float* input);

float* getOutput();

float* getWeights() ;

int setWeights(float* weights);

//---DEBUGGING---///

void printWeights();
