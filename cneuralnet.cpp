#include "cneuralnet.h"

int i, j;
float response;
int N_WEIGHTS = 0;

float hidden[HIDDEN_NODES];
float output[OUTPUT_NODES];
//float hiddenWeights[INPUT_NODES + 1][HIDDEN_NODES];
//float outputWeights[HIDDEN_NODES + 1][OUTPUT_NODES];
float hiddenWeights[INPUT_NODES][HIDDEN_NODES];
float outputWeights[HIDDEN_NODES][OUTPUT_NODES];

int createNetwork() {

    int err = -1;

    // Initialize Hidden Weights
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
//        for( j = 0 ; j <= INPUT_NODES ; j++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            hiddenWeights[j][i] = N_WEIGHTS;
            N_WEIGHTS++;
        }
    }

    // Initialize output Weights
    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
//        for( j = 0 ; j <= HIDDEN_NODES ; j++ ) {
            outputWeights[j][i] = N_WEIGHTS;
            N_WEIGHTS++;
        }
    }

    err = 0;

    if( N_WEIGHTS != (INPUT_NODES * HIDDEN_NODES + OUTPUT_NODES * HIDDEN_NODES ) ) {
        err = -1;
    }

    return err;
}//END: createNetwork

int getnWeights() {
    return N_WEIGHTS;
}//END: getnWeights

int feedforward(float* input) {

    int err = -1;
    int w = 0;

    // compute hidden layer activations
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {

        response = hiddenWeights[INPUT_NODES][i] ;
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            response += input[j] * hiddenWeights[j][i] ;
            w++;
        }

        hidden[i] = response;
    }

    // compute output layer activations
    for( i = 0 ; i < OUTPUT_NODES ; i++ ) {
        response = outputWeights[HIDDEN_NODES][i] ;
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            response += hidden[j] * outputWeights[j][i] ;
            w++;
        }

        output[i] = response;
    }

    err = 0;

    if( w !=  N_WEIGHTS ) {
        err = -1;
    }

    return err;
}//END: feedforward

float* getOutput() {
    return output;
}//END: getOutput


int setWeights(float* weights) {

    int err = -1;

    int k = 0;
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
//        for( j = 0 ; j <= INPUT_NODES ; j++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            hiddenWeights[j][i] = weights[k++];
        }//END: j loop
    }//END: i loop

    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
//        for( j = 0 ; j <= HIDDEN_NODES ; j++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            outputWeights[j][i] = weights[k++];
        }//END: j loop
    }//END: i loop

    err = 0;

    return err;
}//END: setWeights


float* getWeights() {

    float *weights = (float *) malloc(sizeof(float) * N_WEIGHTS);

    int k = 0;
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
//        for( j = 0 ; j <= INPUT_NODES ; j++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            weights[k++] = hiddenWeights[j][i];
        }//END: j loop
    }//END: i loop

    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
//        for( j = 0 ; j <= HIDDEN_NODES ; j++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            weights[k++] = outputWeights[j][i];
        }//END: j loop
    }//END: i loop

    return weights;
}//END: getWeights


//---DEBUGGING---///

void printWeights() {

    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        Serial.print("|");
//        for( j = 0 ; j <= INPUT_NODES ; j++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            Serial.print( hiddenWeights[j][i]  );
            Serial.print(" ");
        }
        Serial.print("|\n");
    }

    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        Serial.print("|");
//        for( j = 0 ; j <= HIDDEN_NODES ; j++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            Serial.print( outputWeights[j][i] );
            Serial.print(" ");
        }
        Serial.print("|\n");
    }

}//END: printWeights
