#include <math.h>
#include <stdlib.h>

//inline
float sigmoid(float input);

float mapFloat(float value, float fromLow, float fromHigh,
               float toLow, float toHigh);

//returns a random float between zero and 1
float randFloat();

//returns a random float in the range -1 < n < 1
float randomClamped();

int* calculateCoordinates(int index, int nrow, int ncol);
