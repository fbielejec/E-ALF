//#include <math.h>
//#include "parameters.h"
#include "utils.h"

// including bias
const int INPUT_NODES = 4;
const int HIDDEN_NODES = 10;
const int OUTPUT_NODES = 2;

int createNetwork();

int feedforward(float* input);

float* getOutput();

