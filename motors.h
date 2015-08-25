#include <Arduino.h>
#include <Adafruit_MotorShield.h>

/**---CONSTANTS---*/

extern const int MOTOR_LEFT ;
extern const int MOTOR_RIGHT;

extern const int MIN_SPEED ;
extern const int MAX_SPEED ;

//extern const int SPEED_TABLE_INTERVAL;
//extern const int NBR_SPEEDS  ;
//
enum {MOV_LEFT, MOV_RIGHT, MOV_FORWARD, MOV_BACK, MOV_ROTATE, MOV_STOP};

// defines for directions
//extern const int DIR_LEFT;
//extern const int DIR_CENTER;
//extern const int DIR_RIGHT;

/**---PROTOTYPES---*/

void moveStop();

void changeMoveState(int newState);

void motorsBegin();

void AFMSBegin();

void motorBegin(int motor) ;

void motorForward(int motor, int speed);

void motorReverse(int motor, int speed) ;

void motorStop(int motor);

//void calibrateRotationRate(int sensor, int angle) ;
//
//long rotationAngleToTime( int angle, int speed);
