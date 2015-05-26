#include <Arduino.h>
#include <Adafruit_MotorShield.h>

//---CONSTANTS---//

extern const int MOTOR_LEFT ;
extern const int MOTOR_RIGHT;

// first table entry is 40% speed
extern const int MIN_SPEED ;
// each table entry is 10% faster speed
extern const int SPEED_TABLE_INTERVAL;
extern const int NBR_SPEEDS  ;
// speeds
//extern int speedTable[NBR_SPEEDS];
// time
//extern int rotationTime[NBR_SPEEDS];

// defines for directions
extern const int DIR_LEFT;
extern const int DIR_CENTER;
extern const int DIR_RIGHT;

//---PROTOTYPES---//

void AFMSBegin();

void motorBegin(int motor) ;

void motorReverse(int motor, int speed) ;

void motorForward(int motor, int speed);

void motorStop(int motor);

void motorBrake(int motor);

void calibrateRotationRate(int sensor, int angle) ;

long rotationAngleToTime( int angle, int speed);
