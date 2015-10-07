#include <Arduino.h>

/**---CONSTANTS---*/

// defines to identify analog sensor pins
const int LINE_SENSOR_LEFT = 0;
const int LINE_SENSOR_CENTER = 1;
const int LINE_SENSOR_RIGHT = 2;

const int LINE_SENSOR_MIN = 0;
const int LINE_SENSOR_MAX = 1023;

// defines to identify digital sensor pins
const int COLLISION_SENSOR_LEFT = 3;
const int COLLISION_SENSOR_RIGHT = 4;

/**---PROTOTYPES---*/

float* senseLine();

void collisionSensorsBegin();

boolean senseCollision(int sensor) ;

boolean checkCollision();
