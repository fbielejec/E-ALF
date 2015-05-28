#include <Arduino.h>
#include <Adafruit_MotorShield.h>

/**---CONSTANTS---*/

extern const int MOTOR_LEFT ;
extern const int MOTOR_RIGHT;

extern const int MIN_SPEED ;

/**---PROTOTYPES---*/

void moveStop();

void changeMoveState(int newState);

void motorsBegin();

void AFMSBegin();

void motorBegin(int motor) ;

void motorForward(int motor, int speed);

void motorStop(int motor);
