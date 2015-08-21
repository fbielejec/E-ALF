#include "autonom.h"

#include "display.h"
#include "sensors.h"
#include "motors.h"
#include "globalDefines.h"
#include "neuralnet.h"
#include "parameters.h"

/**---CONSTANTS---*/

int MAX_SPEED = 100;

NeuralNetwork neuralnet;

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

    // initialize nn
    neuralnet.createNetwork();

    Serial.println("\t Systems functional.");

}//END: init_io


void run() {

    init_io();

    while (1) {

        // TODO: set nn weights over serial

        std::vector<double> readings = senseLine();
        readings.push_back(Parameters::bias);

        std::vector<double> output = neuralnet.update(readings);

        int leftSpeed = sigmoid(output.at(MOTOR_LEFT));
        int rightSpeed = sigmoid(output.at(MOTOR_RIGHT));

//int leftSpeed = 40;
//int rightSpeed = 80;

        leftSpeed = map(leftSpeed, 0, 1, -MAX_SPEED, MAX_SPEED);
        rightSpeed = map(rightSpeed, 0, 1, -MAX_SPEED, MAX_SPEED);

        leftSpeed = constrain(MIN_SPEED + leftSpeed, 0, MAX_SPEED);
        rightSpeed = constrain(MIN_SPEED + rightSpeed, 0, MAX_SPEED);

        motorForward(MOTOR_LEFT, leftSpeed);
        motorForward(MOTOR_RIGHT, rightSpeed);

    }//END: loop

}//END: run


