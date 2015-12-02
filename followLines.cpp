#include "followLines.h"

#include "display.h"
#include "sensors.h"
#include "motors.h"
#include "utils.h"
//#include "globalDefines.h"
//#include "parameters.h"

/**---CONSTANTS---*/

const int ROTATE_TIME = 1500;
const int REVERSE_TIME = 2000;

//1 is most sensitive, range 1 to 1023
int damping =  2; //5

int c_speed = 50;

char* labels[] = {"", "Left Line", "Center Line", "Right Line","Drift", "Left Speed", "Right Speed", "Damping"};

int minRange[] = { 0, 0, 0, 0, -1023, 0, 0, 0 };

int maxRange[] = { 0, 1023, 1023, 1023, 1023, 100, 100, 40 };

enum { DATA_start, DATA_LEFT, DATA_CENTER, DATA_RIGHT, DATA_DRIFT, DATA_L_SPEED, DATA_R_SPEED, DATA_DAMPING, DATA_nbrItems };

/**---METHODS---*/

void init_follower(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online.");

    // initialize motors
    motorsBegin();
//    dataDisplayBegin(DATA_nbrItems, labels, minRange, maxRange );

    // initialize sensors
    collisionSensorsBegin();

    Serial.println("\t Systems functional.");
    delay(1000);

}//END: init_io

void checkBorders() {

    boolean collision = checkCollision();
    if(collision) {

// brake and reverse
   Serial.println("-- Braking...");
        motorStop(MOTOR_LEFT);
        motorStop(MOTOR_RIGHT);
        delay(1000);

        Serial.println("-- Reversing wheels...");

        motorReverse(MOTOR_LEFT, REVERSE_SPEED);
        motorReverse(MOTOR_RIGHT, REVERSE_SPEED);
        delay(REVERSE_TIME);
        motorStop(MOTOR_LEFT);
        motorStop(MOTOR_RIGHT);
        delay(500);


        if(COLLISION_DIRECTION == COLLISION_DIRECTION_LEFT) {

            rotateRight();

        } else if (COLLISION_DIRECTION == COLLISION_DIRECTION_RIGHT) {

            rotateLeft();

        } else {

            // TODO: randomly decide where to turn
            float r = randFloat();
            if(r < 0.5) {
                rotateLeft();
            } else {
                rotateRight();
            }

        }//END: COLLISION_DIRECTION check


// Choose randomly the amount of time to rotate
        delay(ROTATE_TIME);

        // brake and reverse for 2 seconds
//        motorBrake(MOTOR_LEFT);
//        motorBrake(MOTOR_RIGHT);
//
//        motorStop(MOTOR_LEFT);
//        motorStop(MOTOR_RIGHT);
//        delay(1);
//
//        motorReverse(MOTOR_LEFT,  MIN_SPEED);
//        motorReverse(MOTOR_RIGHT,  MIN_SPEED);
//        delay(2000);
//
//        motorStop(MOTOR_LEFT);
//        motorStop(MOTOR_RIGHT);
//        delay(1);
//
//        Serial.println("Returning to normal operation.");

    }//END: collision check

}//END: checkBorders

void followLines() {

    init_follower();

    while (1) {

        lineFollow(c_speed);

        checkBorders();
        delay(1);

    }//END: loop

}//END: followLines

void lineFollow( int speed ) {

    int leftVal = analogRead(LINE_SENSOR_LEFT);
    int centerVal = analogRead(LINE_SENSOR_CENTER);
    int rightVal = analogRead(LINE_SENSOR_RIGHT);

//    sendData(DATA_LEFT, leftVal);
//    sendData(DATA_CENTER, centerVal);
//    sendData(DATA_RIGHT, rightVal);

    // drift: 0 if over line, minus value if left, plus if right
    int drift = leftVal -rightVal   ;

//    sendData(DATA_DRIFT, drift);

    int leftSpeed   =  constrain(speed + (drift / damping), 0, 100);
    int rightSpeed  =  constrain(speed - (drift / damping), 0, 100);

//    sendData(DATA_L_SPEED, leftSpeed);
//    sendData(DATA_R_SPEED, rightSpeed);

    motorForward(MOTOR_LEFT, leftSpeed);
    motorForward(MOTOR_RIGHT, rightSpeed);

}//END: lineFollow

