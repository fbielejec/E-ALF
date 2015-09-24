#include "sensors.h"
#include "cneuralnet.h"

#define DEBUG 1

/**---CONSTANTS---*/

//const byte NBR_LINE_SENSORS = 3;


/**---ARRAYS---*/


/**---METHODS---*/

float* senseLine() {
    /**
     * read the sensor values
     * compress them to continuous (0,1) range
     * @return compressed sensor readings
     */
    float *readings = (float *) malloc(sizeof(float) * (INPUT_NODES-1));

    float leftVal = (float) analogRead(LINE_SENSOR_LEFT);
    float centerVal = (float) analogRead(LINE_SENSOR_CENTER);
    float rightVal = (float) analogRead(LINE_SENSOR_RIGHT);

    float dl = mapFloat(leftVal, LINE_SENSOR_MIN, LINE_SENSOR_MAX, 0, 1);
    float dc = mapFloat(centerVal, LINE_SENSOR_MIN, LINE_SENSOR_MAX, 0, 1);
    float dr = mapFloat(rightVal, LINE_SENSOR_MIN, LINE_SENSOR_MAX, 0, 1);

    readings[LINE_SENSOR_LEFT] = dl;
    readings[LINE_SENSOR_CENTER] = dc;
    readings[LINE_SENSOR_RIGHT] = dr;

    return(readings);
}//END: senseLine

void collisionSensorsBegin() {
    pinMode(CRASH_SENSOR_LEFT, INPUT);
    pinMode(CRASH_SENSOR_RIGHT, INPUT);
}//END: collisionSensorsBegin

boolean senseCollision(int sensor) {
    boolean collision = false;

    int value = digitalRead(sensor);
    if (value == HIGH) {
        collision = false;
    } else {
        collision = true;
    }

    return collision;
}//END: senseCollision

boolean checkCollision() {

    if(senseCollision(CRASH_SENSOR_LEFT) == true) {
        return true;
    }

    if(senseCollision(CRASH_SENSOR_RIGHT) == true) {
        return true;
    }

    return false;
}//END: checkCollision
