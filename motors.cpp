#include "motors.h"
#include "sensors.h"
#include "globalDefines.h"

/**---CONSTANTS---*/

const int MOTOR_LEFT  = 0;
const int MOTOR_RIGHT = 1;

int moveState = MOV_STOP;

const char* states[] = {"Left", "Right", "Forward", "Back", "Rotate", "Stop"};

// first table entry is 40% speed
const int MIN_SPEED = 40;

Adafruit_MotorShield AFMS = Adafruit_MotorShield();

Adafruit_DCMotor *motors[2] = {
    AFMS.getMotor(1), // left is Motor #1
    AFMS.getMotor(2) // right is Motor #2
};

/**---METHODS---*/

void motorsBegin() {
    AFMSBegin() ;
    motorBegin(MOTOR_LEFT);
    motorBegin(MOTOR_RIGHT);
    moveStop();
}//END: moveBegin

void moveStop() {
    changeMoveState(MOV_STOP);
    motorStop(MOTOR_LEFT);
    motorStop(MOTOR_RIGHT);
}//END: moveStop

void changeMoveState(int newState) {
    if(newState != moveState) {
        Serial.print("Changing move state from ");
        Serial.print( states[moveState]);
        Serial.print(" to ");
        Serial.println(states[newState]);
        moveState = newState;
    }//END: moveState change check
}//END: changeMoveState

/**---MOTORS---*/

void AFMSBegin() {
    AFMS.begin(1000);
}

void motorBegin(int motor) {
    motors[motor]->run(RELEASE);
}//END: motorBegin


void motorForward(int motor, int speed) {
    motors[motor]->run(FORWARD);
    motors[motor]->setSpeed(speed);
}//END: motorForward

void motorStop(int motor) {
    motors[motor]->run(RELEASE);
    motors[motor]->setSpeed(0);
}//END: motorStop


