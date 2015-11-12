#include "pause.h"

// D13
const int PAUSE_BUTTON_PIN = 12;
const int PAUSE_LED_PIN = 13;

int buttonState      = 0;
int lastButtonState  = 0;
int ledState         = 1;

void pauseButtonBegin() {
    pinMode(PAUSE_BUTTON_PIN, INPUT);
    pinMode(PAUSE_LED_PIN, OUTPUT);
}//END: collisionSensorsBegin

void checkPauseButton() {

    buttonState = digitalRead(PAUSE_BUTTON_PIN);

//TODO
Serial.println(buttonState);
delay(1000);

    if (buttonState != lastButtonState) {

        // change the state of the led when button pressed
        if (buttonState == 1) {

            if(ledState == 1) {
                ledState = 0;
            } else {
                ledState = 1;
            }

        }//END: button pressed check

        // remember the current state of the button
        lastButtonState = buttonState;
    }//END: buttonState changed check

    // turns LED on if the ledState=1 or off if the ledState=0
    digitalWrite(PAUSE_LED_PIN, ledState);

    // debounce the signal
    delay(20);
}//END: checkPause
