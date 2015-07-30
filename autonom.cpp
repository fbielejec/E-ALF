#include "autonom.h"

#include "display.h"
#include "sensors.h"
#include "motors.h"
#include "globalDefines.h"
#include "neuralnet.h"
#include "parameters.h"

/**---CONSTANTS---*/

int speed = MIN_SPEED;

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

// TODO: pass the weights

        std::vector<double> readings = senseLine();
        readings.push_back(Parameters::bias);
        std::vector<double> output = neuralnet.update(readings);

        double leftSpeed = sigmoid(output.at(MOTOR_LEFT));
        double rightSpeed = sigmoid(output.at(MOTOR_RIGHT));

        leftSpeed = constrain(speed + leftSpeed, 0, 100);
        rightSpeed = constrain(speed + rightSpeed, 0, 100);

    }//END: loop

}//END: followLines


