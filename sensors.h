#include <Arduino.h>
#include <StandardCplusplus.h>
#include <vector>

/**---CONSTANTS---*/

// defines to identify sensors
const int SENSE_IR_LEFT = 0;
const int SENSE_IR_RIGHT = 1;
const int SENSE_IR_CENTER = 2;

extern const byte NBR_SENSORS;

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


/**---PROTOTYPES---*/

std::vector<float> senseLine();

void irSensorBegin() ;

void irSensorCalibrate(byte sensor);

boolean lookForObstacle(int obstacle);

boolean irEdgeDetect(int sensor);

boolean irSensorDetect(int sensor);
