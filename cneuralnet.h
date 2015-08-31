#include <Arduino.h>
//#include "parameters.h"
#include "utils.h"

// including bias
const int INPUT_NODES = 2; // 4
const int HIDDEN_NODES = 1; // 6
const int OUTPUT_NODES = 1; // 2

int createNetwork();

int getnWeights();

int feedforward(float* input);

float* getOutput();

float* getWeights() ;

int setWeights(float* weights);

//---DEBUGGING---///

void printWeights();
