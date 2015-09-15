#include "sensors.h"
#include "cneuralnet.h"

#define DEBUG 1

/**---CONSTANTS---*/

//const byte NBR_LINE_SENSORS = 3;


/**---ARRAYS---*/


/**---METHODS---*/

float* senseLine() {

    float *readings = (float *) malloc(sizeof(float) * (INPUT_NODES-1));

    float leftVal = (float) analogRead(LINE_SENSOR_LEFT);
    float centerVal = (float) analogRead(LINE_SENSOR_CENTER);
    float rightVal = (float) analogRead(LINE_SENSOR_RIGHT);

    readings[LINE_SENSOR_LEFT] = leftVal;
    readings[LINE_SENSOR_CENTER] = centerVal;
    readings[LINE_SENSOR_RIGHT] = rightVal;

    return(readings);
}//END: senseLine


void collisionSensorsBegin() {
    pinMode(CRASH_SENSOR_PIN, INPUT);
}//END: collisionSensorsBegin


boolean checkCollision() {

    boolean collision = false;

    int value = digitalRead(CRASH_SENSOR_PIN);
    if (value == HIGH) {
        collision = false;
    } else {
        collision = true;
    }

    return collision;
}//END: checkCollision
