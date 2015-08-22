#include "autonom.h"

#include <MemoryFree.h>
#include "display.h"
#include "sensors.h"
#include "motors.h"
#include "globalDefines.h"
#include "neuralnet.h"
#include "parameters.h"

/**---CONSTANTS---*/

int MAX_SPEED = 100;

NeuralNetwork neuralnet;//   = NeuralNetwork();

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

//  Serial.print("freeMemory()=");
//  Serial.println(freeMemory());

}//END: init_io


void run() {

    init_io();

    while (1) {

        // TODO: set nn weights over serial

        std::vector<float> readings = senseLine();
        readings.push_back(Parameters::bias);

//Serial.println(readings.at(SENSE_IR_LEFT));
//Serial.println(readings.at(SENSE_IR_CENTER));
//Serial.println(readings.at(SENSE_IR_RIGHT));
//Serial.println( readings.at(readings.size()-1));

  Serial.print("no of weights:");
Serial.println( neuralnet.getNumberOfWeights());

        std::vector<float> output = neuralnet.update(readings);

//Serial.println(output.at(MOTOR_LEFT));
//Serial.println(output.at(MOTOR_RIGHT));

  Serial.print("freeMemory()=");
  Serial.println(freeMemory());

delay(1000);

//Serial.println(output.at(MOTOR_LEFT));
//Serial.println(output.at(MOTOR_RIGHT));



//        int leftSpeed = sigmoid(output.at(MOTOR_LEFT));
//        int rightSpeed = sigmoid(output.at(MOTOR_RIGHT));

//int leftSpeed = 40;
//int rightSpeed = 80;

//        leftSpeed = map(leftSpeed, 0, 1, -MAX_SPEED, MAX_SPEED);
//        rightSpeed = map(rightSpeed, 0, 1, -MAX_SPEED, MAX_SPEED);
//
//        leftSpeed = constrain(MIN_SPEED + leftSpeed, 0, MAX_SPEED);
//        rightSpeed = constrain(MIN_SPEED + rightSpeed, 0, MAX_SPEED);
//
//        motorForward(MOTOR_LEFT, leftSpeed);
//        motorForward(MOTOR_RIGHT, rightSpeed);

    }//END: loop

}//END: run


