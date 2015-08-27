#include "cneuralnet.h"

int i, j;
float response;

float hidden[HIDDEN_NODES];
float output[OUTPUT_NODES];
float hiddenWeights[INPUT_NODES + 1][HIDDEN_NODES];
float outputWeights[HIDDEN_NODES + 1][OUTPUT_NODES];


int createNetwork() {

//TODO: should take weights as argument

    int err = -1;

    // Initialize hidden weights
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        for( j = 0 ; j <= INPUT_NODES ; j++ ) {
            hiddenWeights[j][i] = randomClamped() ;
        }//END: j loop
    }//END: i loop

    // Initialize output weights
    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        for( j = 0 ; j <= HIDDEN_NODES ; j++ ) {
            outputWeights[j][i] = randomClamped();
        }//END: j loop
    }//END: i loop

    err = 0;

    return err;
}//END: createNetwork


int feedforward(float* input) {

    int err = -1;

    // calculate hidden layer responses
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {

        response = hiddenWeights[INPUT_NODES][i] ;
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            response += input[j] * hiddenWeights[j][i] ;
        }//END: j loop

        hidden[i] = response;
    }//END: i loop

// calculate output layer responses
    for( i = 0 ; i < OUTPUT_NODES ; i++ ) {
        response = outputWeights[HIDDEN_NODES][i] ;
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            response += hidden[j] * outputWeights[j][i] ;
        }

        output[i] = response;
    }

    err = 0;

    return err;
}//END: feedforward

float* getOutput() {
    return output;
}//END: getOutput
