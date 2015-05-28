#include "display.h"
#include "globalDefines.h"

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
