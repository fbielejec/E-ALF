#include "globalDefines.h"
#include "sensors.h"

/**---CONSTANTS---*/

const byte NBR_SENSORS = 3;
// analog pins for sensors
const byte IR_SENSOR[NBR_SENSORS] = { 0, 1, 2 };

// debug labels
const char* locationString[] = { "Left", "Right", "Center" };

// values considered no reflection
int irSensorAmbient[NBR_SENSORS];
// values considered detecting an object
int irSensorReflect[NBR_SENSORS];
// values considered detecting an edge
int irSensorEdge[NBR_SENSORS];
// % level below ambient to trigger reflection
const int irReflectThreshold = 10;
// % level above ambient to trigger edge
const int irEdgeThreshold    = 90;

// for initial detection
boolean isDetected[NBR_SENSORS] = { false, false, false };

// no obstacle detected
const int OBST_NONE       = 0;
// left edge detected
const int OBST_LEFT_EDGE  = 1;
// right edge detected
const int OBST_RIGHT_EDGE = 2;
// edge detect at both left and right sensors
const int OBST_FRONT_EDGE = 3;



/**---METHODS---*/


void irSensorBegin() {

    for(int sensor = 0; sensor < NBR_SENSORS; sensor++) {
        irSensorCalibrate(sensor);
    }//END: sensor loop

}//END: irSensorBegin


void irSensorCalibrate(byte sensor) {
    /**
    * Calibrate thresholds for ambient light
    */
    // get ambient level
    int ambient = analogRead(IR_SENSOR[sensor]);
    irSensorAmbient[sensor] = ambient;
    // precalculate the levels for object and edge detection
    irSensorReflect[sensor] = (ambient * (long)(100 - irReflectThreshold)) / 100;
    irSensorEdge[sensor]    = (ambient * (long)(100 + irEdgeThreshold)) / 100;
}//END: irSensorCalibrate


boolean lookForObstacle(int obstacle) {

    switch(obstacle) {

    case  OBST_FRONT_EDGE:
        return irEdgeDetect(DIR_LEFT) || irEdgeDetect(DIR_RIGHT);
// return irSensorDetect(DIR_LEFT) || irSensorDetect(DIR_RIGHT);

    case  OBST_LEFT_EDGE:
        return irEdgeDetect(DIR_LEFT);
// return irSensorDetect(DIR_LEFT);

    case  OBST_RIGHT_EDGE:
        return irEdgeDetect(DIR_RIGHT);
// return irSensorDetect(DIR_RIGHT);
    }//END: switch

    return false;
}//END: lookForObstacle

boolean irSensorDetect(int sensor) {
    /**
    * @parameter: is the index into the sensor array
    * @return: true if an object reflection detected on the given sensor
    */
    boolean result = false;
    int value = analogRead(IR_SENSOR[sensor]);

#if DEBUG
    Serial.print("sensor ");
    Serial.print(locationString[sensor]);
    Serial.print(" reading ");
    Serial.print(value);
    Serial.print(" trigger value ");
    Serial.print(irSensorReflect[sensor]);
    Serial.println();
    delay(1000);
#endif

    if( value <= irSensorReflect[sensor]) {

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

boolean irEdgeDetect(int sensor) {
    /**
    * @parameter: is the index into the sensor array
    * @return: true if an edge is detected on the given sensor
    */
    boolean result = false;
    int value = analogRead(IR_SENSOR[sensor]);

#if DEBUG
    Serial.print("sensor ");
    Serial.print(locationString[sensor]);
    Serial.print(" reading ");
    Serial.print(value);
    Serial.print(" trigger value ");
    Serial.print(irSensorEdge[sensor]);
    Serial.println();
    delay(1000);
#endif

    if( value >= irSensorEdge[sensor]) {
        // edge detected (higher value means less reflection from a distant object)
        result = true;

        if( isDetected[sensor] == false) {
            Serial.print(locationString[sensor]);
            Serial.println(" Edge detected");
        }//END: initial detection check

    }//END: value check

    isDetected[sensor] = result;

    return result;
}//END: irEdgeDetect
