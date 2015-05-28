#include <Arduino.h>
#include <Adafruit_MotorShield.h>

//---CONSTANTS---//

extern const int MOTOR_LEFT ;
extern const int MOTOR_RIGHT;

extern const int MIN_SPEED ;
extern const int SPEED_TABLE_INTERVAL;
extern const int NBR_SPEEDS  ;

//---PROTOTYPES---//

void moveBegin();

void moveStop();

void moveLeft();

void moveRight();

void moveForward() ;

void moveBackward() ;

void moveRotate(int angle) ;

void moveBrake();

void setMoveSpeed(int speed);

void moveSlower(int decrement);

void moveFaster(int increment);

void movingDelay(long duration) ;

void changeMoveState(int newState);

void AFMSBegin();

void motorBegin(int motor) ;

void motorReverse(int motor, int speed) ;

void motorForward(int motor, int speed);

void motorStop(int motor);

void motorBrake(int motor);

void motorSetSpeed(int motor, int speed);

void calibrateRotationRate(int sensor, int angle) ;

long rotationAngleToTime( int angle, int speed);
