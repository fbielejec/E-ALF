#include <Arduino.h>

extern const int PAUSE_LED_PIN;
extern const int PAUSE_BUTTON;

extern int PAUSE_STATE;

void pauseButtonBegin();

void checkPauseButton();
