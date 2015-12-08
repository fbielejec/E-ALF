#include <Arduino.h>
#include "utils.h"

/**---CONSTANTS---*/

// including bias
const int INPUT_NODES = 3;
const int HIDDEN_NODES = 2;
const int OUTPUT_NODES = 2;

const float bias = -1;

/**---PROTOTYPES---*/

int createNetwork();

int getnWeights();

int feedforward(float* input);

float* getOutput();

float* getWeights() ;

int setWeights(float* weights);

//---DEBUGGING---///

void printWeights();
