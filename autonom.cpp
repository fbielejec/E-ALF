#include "autonom.h"

#include <assert.h>
#include <MemoryFree.h>

#include "display.h"
#include "sensors.h"
#include "motors.h"
//#include "globalDefines.h"
#include "cneuralnet.h"

#define DEBUG 1

/**---CONSTANTS---*/

int err;
float bias = -1;


/**---ARRAYS---*/

float *readings;

/**---METHODS---*/

void init_io(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online.");

    // initialize motors
    motorsBegin();

    // initialize sensors
    collisionSensorsBegin();

    // initialize nn
    err = createNetwork();
    assert(err == 0);

    Serial.println("\t Systems functional.");

    Serial.print("freeMemory=");
    Serial.println(freeMemory());

}//END: init_io


void run() {

    init_io();

 // TODO: detect collision, call isAlive()
 // TODO: set nn weights over serial

while(1) {

            // reflection blocked on left side
        if(checkCollision(COLLISION_LEFT) == true)   {


        }//END: left edge
//
//        // reflection blocked on right side
//        if(lookForObstacle(OBST_RIGHT_EDGE) == true)   {
//
//            calibrateRotationRate(DIR_RIGHT, 360);
//
//        } //END: right edge


}


    while (1) {




        float *readings = senseLine();
        readings[INPUT_NODES - 1] = bias;

#ifdef DEBUG
        Serial.println("NN inputs:");
        for (int i = 0; i < INPUT_NODES ; i++) {
            Serial.println(readings[i] );
        }
#endif /* DEBUG */

        err = feedforward(readings);
        assert(err == 0);

        float* output = getOutput();

#ifdef DEBUG
        Serial.println("NN response:");
        for (int i = 0; i < OUTPUT_NODES ; i++) {
            Serial.println(output[i] );
        }
#endif /* DEBUG */

        float leftSpeed = sigmoid(output[MOTOR_LEFT]);
        float rightSpeed = sigmoid(output[MOTOR_RIGHT]);

#ifdef DEBUG
        Serial.println("transformed response" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */

        leftSpeed = map(leftSpeed, 0, 1, MIN_SPEED, MAX_SPEED);
        rightSpeed = map(rightSpeed, 0, 1, MIN_SPEED, MAX_SPEED);

#ifdef DEBUG
        Serial.println("mapped response" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */

        motorForward(MOTOR_LEFT, leftSpeed);
        motorForward(MOTOR_RIGHT, rightSpeed);

        free(readings);

#ifdef DEBUG
        delay(3000);
#endif /* DEBUG */

    }//END: loop

}//END: run


