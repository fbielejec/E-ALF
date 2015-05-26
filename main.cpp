/*
 *@author: fbielejec
 */
#include <avr/io.h>
#include <util/delay.h>
#include <Arduino.h>
#include <Wire.h>
//#include <Adafruit_MotorShield.h>
#include "sensors.h"
#include "motors.h"
//#include "utils.h"

//---Prototypes---//

void blinkNumber( byte number) ;

//---Debugging---//
#define DEBUG 1

//---Other---//
const int LED_PIN = 13;

void init_io(void) {

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

}//END: init_io

int main(void) {

    init_io();

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

    return 0;
}//END: main


void blinkNumber( byte number) {
    /**
    * indicate numbers by flashing the built-in LED
    */
    pinMode(LED_PIN, OUTPUT);

    while(number--) {
        digitalWrite(LED_PIN, HIGH);
        delay(100);
        digitalWrite(LED_PIN, LOW);
        delay(400);
    }//END: number loop

}//END: blinkNumber
