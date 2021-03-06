#include "motors.h"
#include "sensors.h"

/**---CONSTANTS---*/

const int MIN_SPEED = 40; //60;
const int MAX_SPEED = 120; //2 * MIN_SPEED;

//const int CONSTANT_SPEED = 70;
//const int DRIFT_SPEED = 20;

const int REVERSE_SPEED = 70;

const int MOTOR_LEFT  = 0;
const int MOTOR_RIGHT = 1;

int moveState = MOV_STOP;

const char* states[] = {"Left", "Right", "Forward", "Back", "Rotate", "Stop"};

Adafruit_MotorShield AFMS = Adafruit_MotorShield();

Adafruit_DCMotor *motors[2] = {
    AFMS.getMotor(2), // left is Motor #1
    AFMS.getMotor(3) // right is Motor #2
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

void AFMSBegin() {
    AFMS.begin(1000);
}//END: AFMSBegin

void motorBegin(int motor) {
    motors[motor]->run(RELEASE);
}//END: motorBegin


void motorForward(int motor, int speed) {
    motors[motor]->run(FORWARD);
    motors[motor]->setSpeed(speed);
}//END: motorForward

void motorReverse(int motor, int speed) {
    motors[motor]->run(BACKWARD);
    motors[motor]->setSpeed(speed);
}//END: motorRevers


void motorStop(int motor) {
    motors[motor]->run(RELEASE);
    motors[motor]->setSpeed(0);
}//END: motorStop


void motorBrake(int motor) {
    motors[motor]->run(BRAKE);
}//END: motorStop

void rotateLeft() {
    motorReverse(MOTOR_LEFT, REVERSE_SPEED);
    motorForward(MOTOR_RIGHT, REVERSE_SPEED);
}//END: trunLeft

void rotateRight() {
    motorForward(MOTOR_LEFT, REVERSE_SPEED);
    motorReverse(MOTOR_RIGHT, REVERSE_SPEED);
}//END: turnRight
