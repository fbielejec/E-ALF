#include <Arduino.h>

//---CONSTANTS---//

extern const byte NBR_SENSORS;

// % level below ambient to trigger reflection
extern const int irReflectThreshold ;

// % level above ambient to trigger edge
extern const int irEdgeThreshold ;

//---PROTOTYPES---//

void irSensorBegin() ;

void irSensorCalibrate(byte sensor);

boolean lookForObstacle(int obstacle);

boolean irEdgeDetect(int sensor);

boolean irSensorDetect(int sensor);

boolean checkMovement() ;
