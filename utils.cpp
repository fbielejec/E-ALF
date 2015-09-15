#include "utils.h"

float sigmoid(float input) {
    return ( 1 / ( 1 +  exp( - input)));
}//END: sigmoid

float mapFloat(float value, float fromLow, float fromHigh,
               float toLow, float toHigh) {
    return( toLow + (toHigh - toLow ) * ((value - fromLow) / (fromHigh - fromLow)) );
}// END: mapFloat

float randFloat() {
    return ( rand() / (RAND_MAX + 1.0));
}//END: randFloat

float randomClamped() {
    return randFloat() - randFloat();
}//END: randomCLamped


int* calculateCoordinates(int index, int nrow, int ncol) {

    int *coords = (int *) malloc(sizeof(int) * 2);

    //for each row
    for(int i = 0; i < nrow; i++) {
        //check if the index parameter is in the row
        if(index < (ncol * i) + ncol && index >= ncol * i) {
            //return x, y
            int row = index - ncol * i;
            int col = i;
            coords[0] = row;
            coords[1] = col;
        }
    }
    return coords;
}//END: calculateCoordinates

