#include <avr/io.h>
#include <util/delay.h>
#include <Arduino.h>
#include <Wire.h>

#include "basicTest.h"

#include "display.h"
#include "sensors.h"
#include "motors.h"
#include "globalDefines.h"

void init_basicTest(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online");

    AFMSBegin();

    // turn on motors
    motorBegin(MOTOR_LEFT);
    motorBegin(MOTOR_RIGHT);

    // initialize sensors
    irSensorBegin();

    Serial.println("Waiting for sensor input to detect blocked reflection");

}//END: init_basicTest

void runBasicTest() {

    init_basicTest();

    while (1) {

        // reflection blocked on left side
        if(lookForObstacle(OBST_LEFT_EDGE) == true)   {

            calibrateRotationRate(DIR_LEFT, 360);

        }//END: left edge

        // reflection blocked on right side
        if(lookForObstacle(OBST_RIGHT_EDGE) == true)   {

            calibrateRotationRate(DIR_RIGHT, 360);

        } //END: right edge

    }//END: loop

}//END: runBasicTest

