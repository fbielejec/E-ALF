#include "sensors.h"

#include "cneuralnet.h"
//#include "globalDefines.h"

#define DEBUG 1

/**---CONSTANTS---*/

const byte NBR_LINE_SENSORS = 3;

// analog pins for sensors
//const byte LINE_SENSOR_PIN[NBR_LINE_SENSORS]  = { 0, 1, 2 };


const byte NBR_COLLISION_SENSORS = 3;

// analog pins for sensors
const byte COLLISION_SENSOR_PIN[NBR_COLLISION_SENSORS] = { 3, 4, 5 };

// defines for directions
const int COLLISION_SENSOR_LEFT   = 0;
const int COLLISION_SENSOR_CENTER = 1;
const int COLLISION_SENSOR_RIGHT  = 2;

// debug labels
const char* locationString[] = { "Left", "Center" , "Right" };

// no collision detected
//const int COLLISION_NONE = 0;
// left edge detected
const int COLLISION_LEFT  = 1;
// edge detect at both left and right sensors
const int COLLISION_CENTER = 2;
// right edge detected
const int COLLISION_RIGHT = 3;

// % level below ambient to trigger reflection
const int irCollisionThreshold = 10;

/**---ARRAYS---*/

// initial detection
boolean isDetected[NBR_COLLISION_SENSORS] = { false, false, false };

// values considered no reflection
int irCollisionAmbient[NBR_COLLISION_SENSORS];

// values considered detecting an object
int irCollisionValue[NBR_COLLISION_SENSORS];

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

    for(int sensor = 0; sensor < NBR_COLLISION_SENSORS; sensor++) {
        calibrateCollisionSensor(sensor);
    }//END: sensor loop

}//END: collisionSensorBegin


void calibrateCollisionSensor(byte sensor) {
    /**
    * Calibrate thresholds for ambient light
    */
    // get ambient level
    int ambient = analogRead(COLLISION_SENSOR_PIN[sensor]);
    irCollisionAmbient[sensor] = ambient;
    // precalculate the levels for collision detection
    irCollisionValue[sensor] = (ambient * (long)(100 - irCollisionThreshold)) / 100;
}//END: irSensorCalibrate


boolean checkCollision(int obstacle) {

    switch(obstacle) {

    case  COLLISION_LEFT:
        return senseCollision(COLLISION_SENSOR_LEFT) ;//|| senseCollision(DIR_RIGHT);

    case  COLLISION_CENTER:
        return senseCollision(COLLISION_SENSOR_CENTER);

    case  COLLISION_RIGHT:
        return senseCollision(COLLISION_SENSOR_RIGHT);
    }//END: switch

    return false;
}//END: checkCollision


boolean senseCollision(int sensor) {
    /**
    * @parameter: index into the sensor array
    * @return: true if an object reflection detected on the given sensor
    */
    boolean result = false;
    int value = analogRead(COLLISION_SENSOR_PIN[sensor]);

#if DEBUG
    Serial.print("sensor ");
    Serial.print(locationString[sensor]);
    Serial.print(" reading ");
    Serial.print(value);
    Serial.print(" trigger value ");
    Serial.print(irCollisionValue[sensor]);
    Serial.println();
    delay(1000);
#endif

    if( value <= irCollisionValue[sensor]) {

        // object detected (lower value means more reflection from a closer object)
        result = true;
        if( isDetected[sensor] == false) {
            Serial.print(locationString[sensor]);
            Serial.println(" object detected");

        }//END: initial detection check
    }//END: value check

    isDetected[sensor] = result;
    return result;
}//END: irSensorDetect
