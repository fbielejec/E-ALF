#include "motors.h"
#include "sensors.h"
#include "globalDefines.h"

/**---CONSTANTS---*/

const int MOTOR_LEFT  = 0;
const int MOTOR_RIGHT = 1;

//  robot movement state stored here
int moveState = MOV_STOP;
// move speed stored here (0-100%)
int  moveSpeed   = 0;
// percent to increase or decrease speed
int  speedIncrement = 10;

// first table entry is 40% speed
const int MIN_SPEED = 40;

// each table entry is 10% faster speed
const int SPEED_TABLE_INTERVAL = 10;
const int NBR_SPEEDS =  1 + (100 - MIN_SPEED) / SPEED_TABLE_INTERVAL;

// speeds
int speedTable[NBR_SPEEDS]  =  {40, 50, 60, 70, 80, 90, 100};

// time
int rotationTime[NBR_SPEEDS] = {5500, 3300, 2400, 2000, 1750, 1550, 1150};

const char* states[] = {"Left", "Right", "Forward", "Back", "Rotate", "Stop"};

Adafruit_MotorShield AFMS = Adafruit_MotorShield();

Adafruit_DCMotor *motors[2] = {
    AFMS.getMotor(1), // left is Motor #1
    AFMS.getMotor(2) // right is Motor #2
};

/**---MOVEMENT---*/

void moveBegin() {
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

void moveLeft() {
    changeMoveState(MOV_LEFT);
    motorForward(MOTOR_LEFT,  0);
    motorForward(MOTOR_RIGHT, moveSpeed);
}//END: moveLeft

void moveRight() {
    changeMoveState(MOV_RIGHT);
    motorForward(MOTOR_LEFT,  moveSpeed);
    motorForward(MOTOR_RIGHT, 0);
}//END: moveRight

void moveForward() {
    changeMoveState(MOV_FORWARD);
    motorForward(MOTOR_LEFT,  moveSpeed);
    motorForward(MOTOR_RIGHT, moveSpeed);
}//END: moveForward

void moveBackward() {
    changeMoveState(MOV_BACK);
    motorReverse(MOTOR_LEFT, moveSpeed);
    motorReverse(MOTOR_RIGHT, moveSpeed);
}//END: moveBackward

void moveRotate(int angle) {

    changeMoveState(MOV_ROTATE);

    Serial.print("Rotating ");
    Serial.println(angle);
    if(angle < 0) {

        Serial.println(" (left)");
        motorReverse(MOTOR_LEFT,  moveSpeed);
        motorForward(MOTOR_RIGHT, moveSpeed);
        angle = -angle;

    } else if(angle > 0) {

        Serial.println(" (right)");
        motorForward(MOTOR_LEFT,  moveSpeed);
        motorReverse(MOTOR_RIGHT, moveSpeed);
    }

    int ms = rotationAngleToTime(angle, moveSpeed);
    movingDelay(ms);
    moveBrake();
}//END: moveRotate

void moveBrake() {
    changeMoveState(MOV_STOP);
    motorBrake(MOTOR_LEFT);
    motorBrake(MOTOR_RIGHT);
}//END: moveBrake

void setMoveSpeed(int speed) {
    motorSetSpeed(MOTOR_LEFT, speed) ;
    motorSetSpeed(MOTOR_RIGHT, speed) ;
    moveSpeed = speed;
}//END: setMoveSpeed

void moveSlower(int decrement) {

    Serial.print(" Slower: ");
    if( moveSpeed >= speedIncrement + MIN_SPEED) {

        moveSpeed -= speedIncrement;

    } else {

        moveSpeed = MIN_SPEED;

    }

    setMoveSpeed(moveSpeed);
}//END: moveSlower

void moveFaster(int increment) {

    Serial.print(" Faster: ");
    moveSpeed += speedIncrement;
    if(moveSpeed > 100) {
        moveSpeed = 100;
    }

    setMoveSpeed(moveSpeed);
}//END: moveFaster


int getMoveState() {
    return moveState;
}//END: getMoveState


void movingDelay(long duration) {

    /**
    * check for obstacles while delaying the given duration in ms
    */

    long startTime = millis();
    while(millis() - startTime < duration) {

        // function in Look module checks for obstacle in direction of movement
        if(checkMovement() == false) {

            // rotate is only valid movement
            if( moveState != MOV_ROTATE) {
                Serial.println("Stopping in moving Delay()");
                moveBrake();
            }//END: moveState check

        }//END: movementCheck
    }//END: time loop
}//END: movingDelay


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

void motorReverse(int motor, int speed) {
    motors[motor]->run(BACKWARD);
    motors[motor]->setSpeed(speed);
}//END: motorRevers

void motorForward(int motor, int speed) {
    motors[motor]->run(FORWARD);
    motors[motor]->setSpeed(speed);
}//END: motorForward

void motorStop(int motor) {
    motors[motor]->run(RELEASE);
    motors[motor]->setSpeed(0);
}//END: motorStop

void motorBrake(int motor) {
    motors[motor]->run(BRAKE);
}//END: motorBrake

void motorSetSpeed(int motor, int speed) {
    motors[motor]->setSpeed(speed);
}//END: motorSetSpeed

void calibrateRotationRate(int sensor, int angle) {

    Serial.print(locationString[sensor]);
    Serial.println(" calibration" );

    for(int speed = MIN_SPEED; speed <= 100; speed += SPEED_TABLE_INTERVAL) {

        if( sensor == DIR_LEFT) {

            // rotate left
            motorReverse(MOTOR_LEFT,  speed);
            motorForward(MOTOR_RIGHT, speed);

        } else if( sensor == DIR_RIGHT) {

            // rotate right
            motorForward(MOTOR_LEFT, speed);
            motorReverse(MOTOR_RIGHT,  speed);

        } else {

            Serial.println("Invalid sensor");

        }//END: sensor check

        int time = rotationAngleToTime(angle, speed);

        Serial.print(locationString[sensor]);
        Serial.print(": rotate ");
        Serial.print(angle);
        Serial.print(" degrees at speed ");
        Serial.print(speed);
        Serial.print(" for ");
        Serial.print(time);
        Serial.println("ms");

        delay(time);

        // stop motors
        motorStop(MOTOR_LEFT);
        motorStop(MOTOR_RIGHT);

        // delay between speeds
        delay(1000);
    }//END: speed loop

}//END: calibrateRotationRate

long rotationAngleToTime( int angle, int speed) {
    /**
     * @return time in milliseconds to turn the given angle at the given speed
     */

    int fullRotationTime; // time to rotate 360 degrees at given speed

    if(speed < MIN_SPEED) {
        // ignore speeds slower then the first table entry
        return 0;
    }//END: speed check

    angle = abs(angle);

    if(speed >= 100) {

        // the last entry is 100%
        fullRotationTime = rotationTime[NBR_SPEEDS - 1];

    } else {

        int index = (speed - MIN_SPEED) / SPEED_TABLE_INTERVAL;

        int t0 =  rotationTime[index];
        int t1 = rotationTime[index + 1];

        // time of the next higher speed
        fullRotationTime = map(speed,
                               speedTable[index],
                               speedTable[index + 1],
                               t0,
                               t1);

        // Serial.print("index= ");  Serial.print(index); Serial.print(", t0 = ");
        // Serial.print(t0);  Serial.print(", t1 = ");  Serial.print(t1);

    }//END: speed check

    // Serial.print(" full rotation time = ");  Serial.println(fullRotationTime);
    long result = map(angle, 0, 360, 0, fullRotationTime);

    return result;
}//END: rotationAngleToTime
