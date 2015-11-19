#include "pause.h"

// D13
const int PAUSE_BUTTON_PIN = 12;
const int PAUSE_LED_PIN = 11;


int buttonState = 0;
int lastButtonState = 0;
int PAUSE_STATE = 0;
//int pauseState = 0;

void pauseButtonBegin() {
    pinMode(PAUSE_BUTTON_PIN, INPUT);
    pinMode(PAUSE_LED_PIN, OUTPUT);
}//END: collisionSensorsBegin

// TODO; pause / resume button
// http://www.kasperkamperman.com/blog/arduino/arduino-programming-state-change/

void checkPauseButton() {

    buttonState = digitalRead(PAUSE_BUTTON_PIN);

    if (buttonState != lastButtonState) {

        // change the state of the led when button pressed
        if (buttonState == 1) {

            if(PAUSE_STATE == 1) {
                PAUSE_STATE = 0;
                Serial.println("-- Resuming operations.");
            } else {
                PAUSE_STATE = 1;
            }

        }//END: button pressed check

        // remember the current state of the button
        lastButtonState = buttonState;
    }//END: buttonState changed check

    // turns LED on if the PAUSE_STATE=1 or off if the PAUSE_STATE=0
    digitalWrite(PAUSE_LED_PIN, PAUSE_STATE);

    // debounce the signal
    delay(20);
}//END: checkPause
