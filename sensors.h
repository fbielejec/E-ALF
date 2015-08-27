#include <Arduino.h>

/**---CONSTANTS---*/

// defines to identify analog sensor pins
const int LINE_SENSOR_LEFT = 0;
const int LINE_SENSOR_CENTER = 1;
const int LINE_SENSOR_RIGHT = 2;

extern const byte NBR_COLLISION_SENSORS;

// no obstacle detected
//extern const int COLLISION_NONE ;
// left edge detected
extern const int COLLISION_LEFT ;
// right edge detected
extern const int COLLISION_CENTER;
// edge detect at right sensors
extern const int COLLISION_RIGHT;


/**---PROTOTYPES---*/

float* senseLine();

void collisionSensorsBegin() ;

void calibrateCollisionSensor(byte sensor);

boolean checkCollision(int obstacle);

boolean senseCollision(int sensor);
