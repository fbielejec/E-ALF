#include "cneuralnet.h"

int i, j;
float response;
int N_WEIGHTS = 0;

float hidden[HIDDEN_NODES];
float output[OUTPUT_NODES];

float hiddenWeights[INPUT_NODES][HIDDEN_NODES];
float hiddenBiasWeights[HIDDEN_NODES];

float outputWeights[HIDDEN_NODES][OUTPUT_NODES];
float outputBiasWeights[OUTPUT_NODES];

int createNetwork() {

    int err = -1;

    // Initialize hidden bias weights
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        hiddenBiasWeights[i] = static_cast<float> (N_WEIGHTS++);
    }

    // Initialize hidden weights
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            hiddenWeights[j][i] =static_cast<float> (N_WEIGHTS++);
        }
    }

    // Initialize output bias weights
    for( i = 0 ; i < OUTPUT_NODES ; i++ ) {
        outputBiasWeights[i] =static_cast<float> (N_WEIGHTS++);
    }

    // Initialize output weights
    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            outputWeights[j][i] = static_cast<float> (N_WEIGHTS++);
        }
    }

    err = 0;

    if( N_WEIGHTS != ( // #weights
                INPUT_NODES * HIDDEN_NODES + OUTPUT_NODES * HIDDEN_NODES
                // #bias
                + HIDDEN_NODES + OUTPUT_NODES) ) {
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

        response = bias * hiddenBiasWeights[i];
        w++;
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            response += input[j] * hiddenWeights[j][i] ;
            w++;
        }

        hidden[i] = sigmoid(response);
    }

//std::cout << "Hidden response:" << std::endl;
//std::cout << hidden[0] << std::endl;

    // compute output layer activations
    for( i = 0 ; i < OUTPUT_NODES ; i++ ) {

        response = bias * outputBiasWeights[i];
        w++;
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            response += hidden[j] * outputWeights[j][i] ;
            w++;
        }

        output[i] = (response);
    }

    err = 0;

//    if( w !=  N_WEIGHTS ) {
//        err = -1;
//    }

    return err;
}//END: feedforward

float* getOutput() {
    return output;
}//END: getOutput


int setWeights(float* weights) {

    int err = -1;

    int k = 0;
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        hiddenBiasWeights[i] = weights[k++];
    }//END: i loop

    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            hiddenWeights[j][i] = weights[k++];
        }//END: j loop
    }//END: i loop

    for( i = 0 ; i < OUTPUT_NODES ; i++ ) {
        outputBiasWeights[i] = weights[k++];
    }//END: i loop


    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            outputWeights[j][i] = weights[k++];
        }//END: j loop
    }//END: i loop

    err = 0;

    if( k !=  N_WEIGHTS ) {
        err = -1;
    }

    return err;
}//END: setWeights


float* getWeights() {

    float *weights = (float *) malloc(sizeof(float) * N_WEIGHTS);

    int k = 0;
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        weights[k++] = hiddenBiasWeights[i];
    }//END: i loop


    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
            weights[k++] = hiddenWeights[j][i];
        }//END: j loop
    }//END: i loop

    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        weights[k++] = outputBiasWeights[i];
    }//END: i loop

    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
            weights[k++] = outputWeights[j][i];
        }//END: j loop
    }//END: i loop

//    assert( k ==  N_WEIGHTS );

    return weights;
}//END: getWeights


//---DEBUGGING---///

void printWeights() {

     Serial.print("Hidden bias weights\n");
     Serial.print("| ");
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
         Serial.print(hiddenBiasWeights[i] );
           Serial.print(" ");
    }
     Serial.print("|\n");

     Serial.print("Hidden weights\n");
    for( i = 0 ; i < HIDDEN_NODES ; i++ ) {
         Serial.print("| ");
        for( j = 0 ; j < INPUT_NODES ; j++ ) {
             Serial.print(hiddenWeights[j][i] );
               Serial.print(" ");
        }
         Serial.print("|\n");
    }

     Serial.print("Output bias weights\n");
     Serial.print("| ");
    for( i = 0 ; i < OUTPUT_NODES ; i++ ) {
         Serial.print(  outputBiasWeights[i] );
           Serial.print(" ");
    }
     Serial.print("|\n");

     Serial.print("Output weights\n");
    for( i = 0 ; i < OUTPUT_NODES ; i ++ ) {
         Serial.print("| ");
        for( j = 0 ; j < HIDDEN_NODES ; j++ ) {
             Serial.print(  outputWeights[j][i] );
               Serial.print(" ");
        }
         Serial.print("|\n");
    }

}//END: printWeights

