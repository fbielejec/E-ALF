#include <Arduino.h>

//---CONSTANTS---//

extern const byte NBR_SENSORS;

// analog pins for sensors
//extern const byte IR_SENSOR;
// values considered no reflection
//extern int irSensorAmbient;
// values considered detecting an object
//extern int irSensorReflect;
// values considered detecting an edge
//extern int irSensorEdge;

extern const char* locationString[];

// % level below ambient to trigger reflection
extern const int irReflectThreshold ;
// % level above ambient to trigger edge
extern const int irEdgeThreshold ;

// for initial detection
//extern boolean isDetected;

// no obstacle detected
extern const int OBST_NONE ;
// left edge detected
extern const int OBST_LEFT_EDGE ;
// right edge detected
extern const int OBST_RIGHT_EDGE;
// edge detect at both left and right sensors
extern const int OBST_FRONT_EDGE;


//---PROTOTYPES---//

void irSensorBegin() ;

void irSensorCalibrate(byte sensor);

boolean lookForObstacle(int obstacle);

boolean irEdgeDetect(int sensor);

boolean irSensorDetect(int sensor);
