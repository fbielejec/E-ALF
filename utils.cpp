#include "utils.h"

float sigmoid(float input) {
    return ( 1 / ( 1 + exp( - input)));
}//END: sigmoid

float randFloat() {
    return (  (float)rand()  ) / (float)(  RAND_MAX + 1.0);
}//END: randFloat

float randomClamped() {
    return randFloat() - randFloat();
}//END: randomCLamped

