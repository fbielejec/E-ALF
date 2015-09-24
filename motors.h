#include <Arduino.h>
#include <Adafruit_MotorShield.h>

/**---CONSTANTS---*/

extern const int MOTOR_LEFT ;
extern const int MOTOR_RIGHT;
extern const int REVERSE_SPEED;

extern const int MIN_SPEED;
extern const int CONSTANT_SPEED;
extern const int DRIFT_SPEED ;

enum {MOV_LEFT, MOV_RIGHT, MOV_FORWARD, MOV_BACK, MOV_ROTATE, MOV_STOP};

/**---PROTOTYPES---*/

void moveStop();

void changeMoveState(int newState);

void motorsBegin();

void AFMSBegin();

void motorBegin(int motor) ;

void motorForward(int motor, int speed);

void motorReverse(int motor, int speed) ;

void motorStop(int motor);

void motorBrake(int motor);

