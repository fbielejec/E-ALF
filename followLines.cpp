#include <avr/io.h>
#include <util/delay.h>
#include <Arduino.h>
#include <Wire.h>

#include "display.h"
#include "sensors.h"
#include "motors.h"
//#include "globalDefines.h"

void init_io(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online.");

    irSensorBegin();
    moveBegin();





    Serial.println("\t Systems functional.");

}//END: init_io

void followLines() {

    init_io();

    while (1) {



    }//END: loop

}//END: followLines





