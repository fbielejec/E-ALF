#include <avr/io.h>
#include <util/delay.h>
#include <Arduino.h>
#include <Wire.h>

#include "display.h"
#include "sensors.h"
#include "motors.h"
#include "followLines.h"
#include "globalDefines.h"

/**---CONSTANTS---*/

//1 is most sensitive, range 1 to 1023
int damping =  5;

int speed = MIN_SPEED;

enum { DATA_start, DATA_LEFT, DATA_CENTER, DATA_RIGHT, DATA_DRIFT, DATA_L_SPEED, DATA_R_SPEED, DATA_DAMPING, DATA_nbrItems };

char* labels[] = {"", "Left Line", "Center Line", "Right Line","Drift", "Left Speed", "Right Speed", "Damping"};

int minRange[] = { 0, 0, 0, 0, -1023, 0, 0, 0 };

int maxRange[] = { 0, 1023, 1023, 1023, 1023, 100, 100, 40 };

/**---METHODS---*/

void init_io(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online.");

    motorsBegin();
    dataDisplayBegin(DATA_nbrItems, labels, minRange, maxRange );

    Serial.println("\t Systems functional.");

}//END: init_io


void followLines() {

    init_io();

    while (1) {

        int drift = lineSense();
        lineFollow(drift, speed);
        delay(1);

    }//END: loop

}//END: followLines


int lineSense() {
    /**
      * @return drift: 0 if over line, minus value if left, plus if right
    */
    int leftVal = analogRead(SENSE_IR_LEFT);
    int centerVal = analogRead(SENSE_IR_CENTER);
    int rightVal = analogRead(SENSE_IR_RIGHT);

    sendData(DATA_LEFT, leftVal);
    sendData(DATA_CENTER, centerVal);
    sendData(DATA_RIGHT, rightVal);

    int leftSense = centerVal - leftVal;
    int rightSense = rightVal - centerVal;
    int drift = rightVal - leftVal ;

    sendData(DATA_DRIFT, drift);

    return drift;
}//END: lineSense


int lineFollow(int drift, int speed) {
    int leftSpeed   =  constrain(speed - (drift / damping), 0, 100);
    int rightSpeed  =  constrain(speed + (drift / damping), 0, 100);

    sendData(DATA_L_SPEED, leftSpeed);
    sendData(DATA_R_SPEED, rightSpeed);

    motorForward(MOTOR_LEFT, leftSpeed);
    motorForward(MOTOR_RIGHT, rightSpeed);
}//END: lineFollow
