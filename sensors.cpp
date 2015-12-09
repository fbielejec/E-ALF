#include "sensors.h"
#include "cneuralnet.h"

#define DEBUG 1

/**---CONSTANTS---*/

int LEFT_SENSOR_AMBIENT = 0;
int CENTER_SENSOR_AMBIENT = 0;
int RIGHT_SENSOR_AMBIENT = 0;


//const byte NBR_LINE_SENSORS = 3;
int COLLISION_DIRECTION = COLLISION_DIRECTION_LEFT;


/**---ARRAYS---*/


/**---METHODS---*/

void lineSensorBegin() {
    /**
     * @return: ambient (white background) values for sensors
     */
    LEFT_SENSOR_AMBIENT = analogRead(LINE_SENSOR_LEFT);
    Serial.print("-- Left sensor ambient level set to ");
    Serial.println(LEFT_SENSOR_AMBIENT );

    CENTER_SENSOR_AMBIENT = analogRead(LINE_SENSOR_CENTER);
    Serial.print("-- Center sensor ambient level set to ");
    Serial.println(CENTER_SENSOR_AMBIENT );

    RIGHT_SENSOR_AMBIENT = analogRead(LINE_SENSOR_RIGHT);
    Serial.print("-- Right sensor ambient level set to ");
    Serial.println(RIGHT_SENSOR_AMBIENT );
}//END: lineSensorBegin

float* senseLine() {
    /**
     * read the sensor values
     * compress them to continuous (0,1) range
     * @return compressed sensor readings
     */
    float *readings = (float *) malloc(sizeof(float) * (INPUT_NODES));

    float leftVal = (float) analogRead(LINE_SENSOR_LEFT);
    float centerVal = (float) analogRead(LINE_SENSOR_CENTER);
    float rightVal = (float) analogRead(LINE_SENSOR_RIGHT);

    float dl = mapFloat(leftVal, LEFT_SENSOR_AMBIENT, 1000, 0, 1);
    float dc = mapFloat(centerVal, CENTER_SENSOR_AMBIENT, 900, 0, 1);
    float dr = mapFloat(rightVal, RIGHT_SENSOR_AMBIENT, 1000, 0, 1);

    dl = clipFloat(dl, 0, 1);
    dc = clipFloat(dc, 0, 1);
    dr = clipFloat(dr, 0, 1);

    readings[LINE_SENSOR_LEFT] = dl;
    readings[LINE_SENSOR_CENTER] = dc;
    readings[LINE_SENSOR_RIGHT] = dr;

    return(readings);
}//END: senseLine


void collisionSensorsBegin() {
    pinMode(COLLISION_SENSOR_LEFT, INPUT);
    pinMode(COLLISION_SENSOR_RIGHT, INPUT);
}//END: collisionSensorsBegin


boolean senseCollision(int sensor) {
    boolean collision = false;

    int value = digitalRead(sensor);
    if (value == LOW) {
        collision = true;
    }

    return collision;
}//END: senseCollision

boolean checkCollision() {

    if(senseCollision(COLLISION_SENSOR_LEFT) == true) {
        Serial.println("-- Left collision detected");
        COLLISION_DIRECTION = COLLISION_DIRECTION_LEFT;
        return true;
    }

    if(senseCollision(COLLISION_SENSOR_RIGHT) == true) {
        Serial.println("-- Right collision detected");
        COLLISION_DIRECTION = COLLISION_DIRECTION_RIGHT;
        return true;
    }

    return false;
}//END: checkCollision

int getCollisionDirection() {
    return COLLISION_DIRECTION;
}
