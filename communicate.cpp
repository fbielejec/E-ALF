#include <Arduino.h>
#include "communicate.h"

const char* RESET_SIGNAL  = "R";
const char* ONLINE_SIGNAL  = "O";
const char* COLLISION_SIGNAL  = "C";
const char* DONE_SIGNAL  = "D";


float getFloatFromSerial() {
    char inData[20];
    float f = 0;
    int x = 0;
    while (x < 1) {
        String str;
        if (Serial.available()) {
            delay(100);
            int i = 0;
            while (Serial.available() > 0) {
                char inByte = Serial.read();
                str = str+inByte;
                inData[i] = inByte;
                i += 1;
                x = 2;
            }

            f = atof(inData);
            memset(inData, 0, sizeof(inData));
        }
    }//END: x loop

    return f;
}//END: getFloatFromSerial
