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

const int REVERSE_TIME = 5000;
// measured in actions
const int LIFE_LENGTH = 50;

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

boolean isAlive();

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
    Serial.println("-- Sensors activated.");

    // initialize nn
    err = createNetwork();
    assert(err == 0);

    Serial.print("-- Neural network with ");
    Serial.print(getnWeights());
    Serial.println(" weights is active.");

//    printWeights();

    Serial.println("-- All systems functional.");

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
            Serial.println("-- RESET signal caught");
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
//        delay(2000);
#endif

        boolean alive = isAlive();
        if(!alive) {

            Serial.println("-- Life span over");

            Serial.println("-- Braking...");
            motorStop(MOTOR_LEFT);
            motorStop(MOTOR_RIGHT);
            delay(1000);

            Serial.println("-- Reversing wheels...");

            if(COLLISION_DIRECTION == COLLISION_DIRECTION_LEFT) {

                motorReverse(MOTOR_LEFT, REVERSE_SPEED);
                motorReverse(MOTOR_RIGHT, REVERSE_SPEED / 2);

            } else if (COLLISION_DIRECTION == COLLISION_DIRECTION_RIGHT) {

                motorReverse(MOTOR_LEFT, REVERSE_SPEED / 2);
                motorReverse(MOTOR_RIGHT, REVERSE_SPEED);

            } else {

                //

            }

            delay(REVERSE_TIME);

            Serial.println("-- Stopping engines...");
            motorStop(MOTOR_LEFT);
            motorStop(MOTOR_RIGHT);
            delay(1000);

            Serial.println("-- Sending COLLISION signal...");
            Serial.println(COLLISION_SIGNAL);

            Serial.println("-- Sending FITNESS_TRANSMITION_SIGNAL signal...");
            Serial.println(FITNESS_TRANSMITION_SIGNAL);
            Serial.println(getFitness());

            receiveWeights();

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
            Serial.println( output[i] ) ;
        }
#endif /* DEBUG */

        float leftSpeed = sigmoid(output[MOTOR_LEFT]);
        float rightSpeed = sigmoid(output[MOTOR_RIGHT]);

#if DEBUG
        Serial.println("-- sigmoid transformed NN response:" );
        Serial.println(leftSpeed, 2);
        Serial.println(rightSpeed, 2);
#endif /* DEBUG */

        leftSpeed = mapFloat(leftSpeed, -1, 1, MIN_SPEED, MAX_SPEED);
        rightSpeed = mapFloat(rightSpeed, -1, 1, MIN_SPEED, MAX_SPEED);
//        leftSpeed = mapFloat(leftSpeed, -1, 1, -DRIFT_SPEED, DRIFT_SPEED);
//        rightSpeed = mapFloat(rightSpeed, -1, 1, -DRIFT_SPEED, DRIFT_SPEED);

#if DEBUG
        Serial.println("-- NN response mapped to wheel speed:" );
        Serial.println(leftSpeed);
        Serial.println(rightSpeed );
#endif /* DEBUG */

        err = updateFitness(readings);
        assert(err == 0);

        motorForward(MOTOR_LEFT, leftSpeed);
        motorForward(MOTOR_RIGHT, rightSpeed);
//        motorForward(MOTOR_LEFT, CONSTANT_SPEED + leftSpeed);
//        motorForward(MOTOR_RIGHT, CONSTANT_SPEED + rightSpeed);

        free(readings);

#if DEBUG
        Serial.print("-- Performing action no: ");
        Serial.println(tick);
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

    float dl = readings[LINE_SENSOR_LEFT];
    float dc = readings[LINE_SENSOR_CENTER];
    float dr = readings[LINE_SENSOR_RIGHT];

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

            free(weights);
            counter = 0;
            break;

        }//END: counter check

    }//END: interrupt loop

    err = 0;
    return err;
}//END: receiveWeights

boolean isAlive() {

    boolean alive = true;

    boolean collision = checkCollision();
    if(collision) {
        alive = false;
    }

    if(tick > LIFE_LENGTH) {
        alive = false;
    }

    return alive;
}//END: isAlive
