#include "utils.h"

double sigmoid(double input) {
    return ( 1 / ( 1 + exp( - input)));
}//END: sigmoid

double randFloat() {
    return (rand()) / (RAND_MAX + 1.0);
}//END: randFloat

double randomClamped() {
    return randFloat() - randFloat();
}//END: randomCLamped

