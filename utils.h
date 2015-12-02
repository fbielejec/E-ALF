#include <math.h>
#include <float.h>
#include <stdlib.h>
#include<stdio.h>
#include <string.h>

//#define E (2.7182818284590452353602874713526624977572470937L )

//#define FLT_MAX 3.4028235E+38

//inline
float sigmoid(float input);

float mapFloat(float value, float fromLow, float fromHigh,
               float toLow, float toHigh);

// random int between (0,N)
int randomInt(int N);

// returns a random float between 0 and 1
float randFloat();

// returns a random float in the range -1 < n < 1
float randomClamped();

int* calculateCoordinates(int index, int nrow, int ncol);

char * float2s(float f, unsigned int digits);

char * float2s(float f);
