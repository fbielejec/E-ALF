#include <Arduino.h>
#include <SoftwareSerial.h>

extern const char* RESET_SIGNAL;
extern const char* ONLINE_SIGNAL;

extern const char* COLLISION_SIGNAL;

extern const char* TRANSMITION_SIGNAL;
extern const char* DONE_SIGNAL;

float getFloatFromSerial();
