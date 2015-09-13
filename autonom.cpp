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


/**---VARIABLES---*/

boolean online = false;

int err;

float bias = -1;
int nWeights;

float fitness = 0;
long tick = 0;

String recv;
int counter = 0;

/**---ARRAYS---*/

float *readings;
float *weights ;

/**---PROTOTYPES---*/

int receiveWeights() ;

int updateFitness( float *readings );

float getFitness();

void(* resetFunc) (void) = 0;

/**---METHODS---*/

void init_io(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    Serial.println("-- Comm-link online.");

    // initialize motors
    motorsBegin();

    // initialize sensors
    collisionSensorsBegin();
    Serial.println("-- I sense a soul in search of answers.");

    // initialize nn
    err = createNetwork();
    assert(err == 0);

    Serial.print("-- Neural network with ");
    Serial.print(getnWeights());
    Serial.println(" weights.");

//    printWeights();

    Serial.println("-- Systems functional.");

#if DEBUG
    Serial.print("-- freeMemory=");
    Serial.println(freeMemory());
#endif

}//END: init_io


void run() {

    init_io();

    while (1) {

        // listen to reset signal
        recv = Serial.readString();
        if(recv == RESET_SIGNAL) {
            Serial.println("-- RESET signal caught!");
            delay(1000);
            resetFunc();
        }

        if(!online) {
            // let the controller know we are online and receive weights
            Serial.println("-- Sending ONLINE signal...");
            Serial.println(ONLINE_SIGNAL);
            receiveWeights();
            Serial.println("-- Sending DONE signal...");
            Serial.println(DONE_SIGNAL);
            online = true;
        }

#if DEBUG
        delay(2000);
#endif

        boolean collision = checkCollision();
        if(collision) {

            Serial.println("-- Collision detected");

            Serial.println("-- Braking...");
            motorBrake(MOTOR_LEFT);
            motorBrake(MOTOR_RIGHT);

            Serial.println("-- Sending COLLISION signal...");
            Serial.println(COLLISION_SIGNAL);

            // TODO: send fitness value
            Serial.println("-- Sending FITNESS_TRANSMITION_SIGNAL signal...");
            Serial.println(FITNESS_TRANSMITION_SIGNAL);
//            Serial.println("-- Sending fitness evaluation...");
            Serial.println(getFitness());



            receiveWeights();

            Serial.println("-- Reversing wheels...");
            motorReverse(MOTOR_LEFT, MIN_SPEED);
            motorReverse(MOTOR_RIGHT, MIN_SPEED);
            delay(2000);
            motorStop(MOTOR_LEFT);
            motorStop(MOTOR_RIGHT);

//            Serial.println("-- Returning to normal operation.");
            fitness = 0;
            tick = 0;

            Serial.println("-- Sending DONE signal...");
            Serial.println(DONE_SIGNAL);

        }//END: collision check

        float *readings = senseLine();
        readings[INPUT_NODES - 1] = bias;

#if DEBUG
        Serial.println("-- NN inputs:");
        for (int i = 0; i < INPUT_NODES ; i++) {
            Serial.println(readings[i] );
        }
#endif /* DEBUG */

        err = feedforward(readings);
        assert(err == 0);

        float* output = getOutput();

#if DEBUG
        Serial.println("-- NN response:");
        for (int i = 0; i < OUTPUT_NODES ; i++) {
            Serial.println(output[i] );
        }
#endif /* DEBUG */

        float leftSpeed = sigmoid(output[MOTOR_LEFT]);
        float rightSpeed = sigmoid(output[MOTOR_RIGHT]);

#if DEBUG
//        Serial.println("-- NN transformed response" );
//        Serial.println(leftSpeed);
//        Serial.println(rightSpeed );
#endif /* DEBUG */

        leftSpeed = map(leftSpeed, 0, 1, MIN_SPEED, MAX_SPEED);
        rightSpeed = map(rightSpeed, 0, 1, MIN_SPEED, MAX_SPEED);

#if DEBUG
        Serial.println("-- mapped NN response" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */


        err = updateFitness(readings);
        assert(err == 0);

        motorForward(MOTOR_LEFT, leftSpeed);
        motorForward(MOTOR_RIGHT, rightSpeed);

        free(readings);

#if DEBUG
//        delay(3000);
#endif /* DEBUG */

        tick++;
    }//END: forever loop

}//END: run

int updateFitness( float *readings ) {
    /**
    * @input: sensor on the line -> its reading increases
    * @return: fitness maximizes the sensor reading values
    **/

    int err = -1;

    float leftVal = readings[LINE_SENSOR_LEFT];
    float dl = map(leftVal, LINE_SENSOR_MIN, LINE_SENSOR_MAX, 0, 1);

    float centerVal = readings[LINE_SENSOR_CENTER];
    float dc = map(centerVal, LINE_SENSOR_MIN, LINE_SENSOR_MAX, 0, 1);

    float rightVal = readings[LINE_SENSOR_RIGHT];
    float dr = map(rightVal, LINE_SENSOR_MIN, LINE_SENSOR_MAX, 0, 1);

    // TODO: possible div by zero here
    fitness += 1/(1 - dl) * 1/(1 - dc) * 1/(1 - dr);

    err = 0;
    return err;
}//END: updateFitness

float getFitness() {
    float value = fitness / (float) tick;
    return value;
}//END: getFitness


int receiveWeights() {

    int err = -1;

    while(1) {

        if(counter == 0) {
            nWeights = getnWeights();
            weights = (float *) malloc(sizeof(float) * nWeights);
            Serial.println("-- Receiving weights");
        }//END: counter check

        float weight = getFloatFromSerial();

        Serial.print("\t --counter=");
        Serial.print(counter);
        Serial.print(" weight=");
        Serial.println(weight, 8);
        weights[counter++] = weight;

        if(counter == nWeights) {

            Serial.println("-- Setting weights");
            setWeights(weights);

            printWeights();

//            Serial.println("-- Done");
            free(weights);
            counter = 0;
//            interrupt = false;
            break;

        }//END: counter check

    }//END: interrupt loop

    err = 0;
    return err;
}//END: receiveWeights


