#define DEBUG 1

#include "autonom.h"

#include <assert.h>
#if DEBUG
#include <MemoryFree.h>
#endif
//#include "display.h"
#include "communicate.h"
#include "sensors.h"
#include "motors.h"
#include "cneuralnet.h"


/**---CONSTANTS---*/

int err;
float bias = -1;
// byte received on the serial port
int recv = 0;

/**---ARRAYS---*/

float *readings;

/**---METHODS---*/

void init_io(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

//    blinkNumber(8);
    Serial.println("\t Comm-link online.");

    // initialize motors
    motorsBegin();

    // initialize sensors
    collisionSensorsBegin();

    // initialize nn
    err = createNetwork();
    assert(err == 0);

    Serial.println("\t Systems functional.");

#if DEBUG
    Serial.print("freeMemory=");
    Serial.println(freeMemory());
#endif

}//END: init_io

int checkCollisions() {

    int err = -1;

    boolean collision = false;
    if(checkCollision(COLLISION_LEFT) == true)   {
        collision = true;
    }//END: left edge


//    if(checkCollision(COLLISION_CENTER) == true)   {
//        collision = true;
//    } //END: right edge
//
//     reflection blocked on right side
//    if(checkCollision(COLLISION_RIGHT) == true)   {
//        collision = true;
//    } //END: right edge

    if(collision) {

// brake and reverse for 2 seconds
        motorBrake(MOTOR_LEFT);
        motorBrake(MOTOR_RIGHT);

        motorReverse(MOTOR_LEFT,  MIN_SPEED);
        motorReverse(MOTOR_RIGHT,  MIN_SPEED);

        delay(2000);

        motorStop(MOTOR_LEFT);
        motorStop(MOTOR_RIGHT);

        Serial.println("Returning to normal operation.");

    }//END: collision check

    err = 0;
    return err;
}//END: checkBorders

void run() {

    init_io();


// TODO: set nn weights over serial

int k = 0;
int nWeights = getnWeights();

float *weights = (float *) malloc(sizeof(float) * nWeights);
 Serial.println("--Receiving weights...");
    while (1) {

        if (Serial.available() > 0) {

                float weight = getFloatFromSerial();

                Serial.print("--k=");
                Serial.print(k);
                Serial.print(" weight=");
                Serial.println(weight, 8);
                weights[k++] = weight;

if(k == nWeights) {

  Serial.println("--Setting weights");
  setWeights(weights);

printWeights();

  Serial.println("--Done");
  free(weights);

}

//                free(weights);


        }//END: serial check

    }


    while (1) {

        float *readings = senseLine();
        readings[INPUT_NODES - 1] = bias;

#if DEBUG
        Serial.println("NN inputs:");
        for (int i = 0; i < INPUT_NODES ; i++) {
            Serial.println(readings[i] );
        }
#endif /* DEBUG */

        err = feedforward(readings);
        assert(err == 0);

        float* output = getOutput();

#if DEBUG
        Serial.println("NN response:");
        for (int i = 0; i < OUTPUT_NODES ; i++) {
            Serial.println(output[i] );
        }
#endif /* DEBUG */

        float leftSpeed = sigmoid(output[MOTOR_LEFT]);
        float rightSpeed = sigmoid(output[MOTOR_RIGHT]);

#if DEBUG
        Serial.println("transformed response" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */

        leftSpeed = map(leftSpeed, 0, 1, MIN_SPEED, MAX_SPEED);
        rightSpeed = map(rightSpeed, 0, 1, MIN_SPEED, MAX_SPEED);

#if DEBUG
        Serial.println("mapped response" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */

        motorForward(MOTOR_LEFT, leftSpeed);
        motorForward(MOTOR_RIGHT, rightSpeed);

        err = checkCollisions();
        assert(err == 0);

        free(readings);

#ifdef DEBUG
//        delay(3000);
#endif /* DEBUG */

    }//END: loop

}//END: run


