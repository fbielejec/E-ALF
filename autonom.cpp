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
int counter = 0;
int nWeights;
//boolean interrupt = false;

/**---ARRAYS---*/

float *readings;
float *weights ;

/**---PROTOTYPES---*/

boolean checkCollisions();

int receiveWeights() ;


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
    Serial.println("\t I sense a soul in search of answers.");

    // initialize nn
    err = createNetwork();
    assert(err == 0);
    Serial.print("\t Neural network with ");
    Serial.print(getnWeights());
    Serial.println(" weights.");

printWeights();

    Serial.println("\t Systems functional.");


#if DEBUG
    Serial.print("\t freeMemory=");
    Serial.println(freeMemory());
#endif

}//END: init_io


void run() {

    init_io();

    while (1) {

        delay(2000);
        boolean collision = checkCollisions();
        if(collision) {

            Serial.println("Braking...");
            motorBrake(MOTOR_LEFT);
            motorBrake(MOTOR_RIGHT);

            Serial.println("Sending collision signal...");
            Serial.println(COLLISION_SIGNAL);

            receiveWeights();

            Serial.println("Reversing wheels...");
            motorReverse(MOTOR_LEFT, MIN_SPEED);
            motorReverse(MOTOR_RIGHT, MIN_SPEED);
            delay(2000);
            motorStop(MOTOR_LEFT);
            motorStop(MOTOR_RIGHT);

        }//END: collision check

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
//        Serial.println("NN response:");
//        for (int i = 0; i < OUTPUT_NODES ; i++) {
//            Serial.println(output[i] );
//        }
#endif /* DEBUG */

        float leftSpeed = sigmoid(output[MOTOR_LEFT]);
        float rightSpeed = sigmoid(output[MOTOR_RIGHT]);

#if DEBUG
//        Serial.println("transformed response" );
//        Serial.println(leftSpeed);
//        Serial.println(rightSpeed );
#endif /* DEBUG */

        leftSpeed = map(leftSpeed, 0, 1, MIN_SPEED, MAX_SPEED);
        rightSpeed = map(rightSpeed, 0, 1, MIN_SPEED, MAX_SPEED);

#if DEBUG
        Serial.println("NN response" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */

        motorForward(MOTOR_LEFT, leftSpeed);
        motorForward(MOTOR_RIGHT, rightSpeed);

        free(readings);

#if DEBUG
//        delay(3000);
#endif /* DEBUG */

    }//END: forever loop

}//END: run


boolean checkCollisions() {

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

    return collision;
}//END: checkCollisions


int receiveWeights() {

    int err = -1;

    if(counter == 0) {
        nWeights = getnWeights();
        weights = (float *) malloc(sizeof(float) * nWeights);
        Serial.println("Receiving weights");
    }//END: counter check

    float weight = getFloatFromSerial();

    Serial.print("--counter=");
    Serial.print(counter);
    Serial.print(" weight=");
    Serial.println(weight, 8);
    weights[counter++] = weight;

    if(counter == nWeights) {

        Serial.println("--Setting weights");
        setWeights(weights);

        printWeights();

        Serial.println("--Done");
        free(weights);
        counter = 0;

    }//END: counter check

    err = 0;
    return err;
}//END: receiveWeights


