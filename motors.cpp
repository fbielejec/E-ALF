#include "motors.h"

#include "sensors.h"
//#include "globalDefines.h"

/**---CONSTANTS---*/

const int MOTOR_LEFT  = 0;
const int MOTOR_RIGHT = 1;

int moveState = MOV_STOP;

const char* states[] = {"Left", "Right", "Forward", "Back", "Rotate", "Stop"};

// first table entry is 40% speed
const int MIN_SPEED = 40;
const int MAX_SPEED = 100;

// each table entry is 10% faster speed
//const int SPEED_TABLE_INTERVAL = 10;
//const int NBR_SPEEDS =  1 + (100 - MIN_SPEED) / SPEED_TABLE_INTERVAL;
//// speeds
//int speedTable[NBR_SPEEDS]  =  {40,     50,   60,   70,   80,   90,  MAX_SPEED};
//// time
//int rotationTime[NBR_SPEEDS] = {5500, 3300, 2400, 2000, 1750, 1550, 1150};

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

//void calibrateRotationRate(int sensor, int angle) {
//
//    Serial.print(locationString[sensor]);
//    Serial.println(" calibration" );
//
//    for(int speed = MIN_SPEED; speed <= 100; speed += SPEED_TABLE_INTERVAL) {
//
////        blinkNumber(speed / 10);
//
//        if( sensor == DIR_LEFT) {
//
//            // rotate left
//            motorReverse(MOTOR_LEFT,  speed);
//            motorForward(MOTOR_RIGHT, speed);
//
//        } else if( sensor == DIR_RIGHT) {
//
//            // rotate right
//            motorForward(MOTOR_LEFT, speed);
//            motorReverse(MOTOR_RIGHT,  speed);
//
//        } else {
//
//            Serial.println("Invalid sensor");
//
//        }//END: sensor check
//
//        int time = rotationAngleToTime(angle, speed);
//
//        Serial.print(locationString[sensor]);
//        Serial.print(": rotate ");
//        Serial.print(angle);
//        Serial.print(" degrees at speed ");
//        Serial.print(speed);
//        Serial.print(" for ");
//        Serial.print(time);
//        Serial.println("ms");
//
//        delay(time);
//
//        // stop motors
//        motorStop(MOTOR_LEFT);
//        motorStop(MOTOR_RIGHT);
//
//        // delay between speeds
//        delay(1000);
//    }//END: speed loop
//
//}//END: calibrateRotationRate
//
//long rotationAngleToTime( int angle, int speed) {
//    /**
//     * @return time in milliseconds to turn the given angle at the given speed
//     */
//
//    int fullRotationTime; // time to rotate 360 degrees at given speed
//
//    if(speed < MIN_SPEED) {
//        // ignore speeds slower then the first table entry
//        return 0;
//    }//END: speed check
//
//    angle = abs(angle);
//
//    if(speed >= 100) {
//
//        // the last entry is 100%
//        fullRotationTime = rotationTime[NBR_SPEEDS - 1];
//
//    } else {
//
//        int index = (speed - MIN_SPEED) / SPEED_TABLE_INTERVAL;
//
//        int t0 =  rotationTime[index];
//        int t1 = rotationTime[index + 1];
//
//        // time of the next higher speed
//        fullRotationTime = map(speed,
//                               speedTable[index],
//                               speedTable[index + 1],
//                               t0,
//                               t1);
//
//        // Serial.print("index= ");  Serial.print(index); Serial.print(", t0 = ");
//        // Serial.print(t0);  Serial.print(", t1 = ");  Serial.print(t1);
//
//    }//END: speed check
//
//    // Serial.print(" full rotation time = ");  Serial.println(fullRotationTime);
//    long result = map(angle, 0, 360, 0, fullRotationTime);
//
//    return result;
//}//END: rotationAngleToTime
