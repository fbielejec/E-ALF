/*
 *@author: fbielejec
 */
#include <avr/io.h>
#include <util/delay.h>
#include <Arduino.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>

//---Prototypes---//

void irSensorBegin() ;
void irSensorCalibrate(byte sensor);
boolean lookForObstacle(int obstacle);
boolean irEdgeDetect(int sensor);

void motorBegin(int motor) ;
void motorReverse(int motor, int speed) ;
void motorForward(int motor, int speed);
void motorStop(int motor);
void motorBrake(int motor);

void calibrateRotationRate(int sensor, int angle) ;
long rotationAngleToTime( int angle, int speed);

void blinkNumber( byte number) ;



//---Motors---//
const int MOTOR_LEFT  = 0;
const int MOTOR_RIGHT = 1;

Adafruit_MotorShield AFMS = Adafruit_MotorShield();

Adafruit_DCMotor *motors[2] = {
    AFMS.getMotor(1), // left is Motor #1
    AFMS.getMotor(2) // right is Motor #2
};

// first table entry is 40% speed
const int MIN_SPEED = 40;
// each table entry is 10% faster speed
const int SPEED_TABLE_INTERVAL = 10;
const int NBR_SPEEDS =  1 + (100 - MIN_SPEED) / SPEED_TABLE_INTERVAL;
// speeds
int speedTable[NBR_SPEEDS]  =  {40,     50,   60,   70,   80,   90,  100};
// time
int rotationTime[NBR_SPEEDS] = {5500, 3300, 2400, 2000, 1750, 1550, 1150};


// defines for directions
const int DIR_LEFT   = 0;
const int DIR_RIGHT  = 1;
const int DIR_CENTER = 2;

//---Sensors---//

const byte NBR_SENSORS = 3;
// analog pins for sensors
const byte IR_SENSOR[NBR_SENSORS] = { 0, 1, 2 };
// values considered no reflection
int irSensorAmbient[NBR_SENSORS];
// values considered detecting an object
int irSensorReflect[NBR_SENSORS];
// values considered detecting an edge
int irSensorEdge[NBR_SENSORS];
// % level below ambient to trigger reflection
const int irReflectThreshold = 10;
// % level above ambient to trigger edge
const int irEdgeThreshold    = 90;

// debug labels
const char* locationString[] = { "Left", "Right", "Center" };
// for initial detection
boolean isDetected[NBR_SENSORS] = { false, false, false };

// no obstacle detected
const int OBST_NONE       = 0;
// left edge detected
const int OBST_LEFT_EDGE  = 1;
// right edge detected
const int OBST_RIGHT_EDGE = 2;
// edge detect at both left and right sensors
const int OBST_FRONT_EDGE = 3;

//---Other---//
const int LED_PIN = 13;


void init_io(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online");

    AFMS.begin(1000);

    // turn on motors
    motorBegin(MOTOR_LEFT);
    motorBegin(MOTOR_RIGHT);

    // initialize sensors
    irSensorBegin();

    Serial.println("Waiting for sensor input to detect blocked reflection");

}//END: init_io

int main(void) {

    init_io();

    while (1) {

        // reflection blocked on left side
        if(lookForObstacle(OBST_LEFT_EDGE) == true)   {

            calibrateRotationRate(DIR_LEFT, 360);

        }//END: left edge

        // reflection blocked on right side
        if(lookForObstacle(OBST_RIGHT_EDGE) == true)   {

            calibrateRotationRate(DIR_RIGHT, 360);

        } //END: right edge

    }//END: loop

    return 0;
}//END: main

void irSensorBegin() {

    for(int sensor = 0; sensor < NBR_SENSORS; sensor++) {
        irSensorCalibrate(sensor);
    }//END: sensor loop

}//END: irSensorBegin


void irSensorCalibrate(byte sensor) {
    /**
    * Calibrate thresholds for ambient light
    */

    int ambient = analogRead(IR_SENSOR[sensor]); // get ambient level
    irSensorAmbient[sensor] = ambient;
    // precalculate the levels for object and edge detection
    irSensorReflect[sensor] = (ambient * (long)(100 - irReflectThreshold)) / 100;
    irSensorEdge[sensor]    = (ambient * (long)(100 + irEdgeThreshold)) / 100;
}//END: irSensorCalibrate


boolean lookForObstacle(int obstacle) {

    switch(obstacle) {

    case  OBST_FRONT_EDGE:
        return irEdgeDetect(DIR_LEFT) || irEdgeDetect(DIR_RIGHT);

    case  OBST_LEFT_EDGE:
        return irEdgeDetect(DIR_LEFT);

    case  OBST_RIGHT_EDGE:
        return irEdgeDetect(DIR_RIGHT);
    }//END: switch

    return false;
}//END: lookForObstacle


boolean irEdgeDetect(int sensor) {

    boolean result = false;
    // get IR light level
    int value = analogRead(IR_SENSOR[sensor]);

    if( value >= irSensorEdge[sensor]) {
        result = true; // edge detected (higher value means less reflection)

        if( isDetected[sensor] == false) {
            Serial.print(locationString[sensor]);
            Serial.println(" Edge detected");
        }//END: initial detection check

    }//END: value check

    isDetected[sensor] = result;

    return result;
}//END: irEdgeDetect


void calibrateRotationRate(int sensor, int angle) {

    Serial.print(locationString[sensor]);
    Serial.println(" calibration" );

    for(int speed = MIN_SPEED; speed <= 100; speed += SPEED_TABLE_INTERVAL) {

        blinkNumber(speed / 10);

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
                               speedTable[index + 1], t0, t1);

        // Serial.print("index= ");  Serial.print(index); Serial.print(", t0 = ");
        // Serial.print(t0);  Serial.print(", t1 = ");  Serial.print(t1);

    }//END: speed check

    // Serial.print(" full rotation time = ");  Serial.println(fullRotationTime);
    long result = map(angle, 0,360, 0, fullRotationTime);

    return result;
}//END: rotationAngleToTime



void blinkNumber( byte number) {
    /**
    * indicate numbers by flashing the built-in LED
    */
    pinMode(LED_PIN, OUTPUT);

    while(number--) {
        digitalWrite(LED_PIN, HIGH);
        delay(100);
        digitalWrite(LED_PIN, LOW);
        delay(400);
    }//END: number loop

}//END: blinkNumber


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
    motors[motor]->run(BRAKE);      // stopped
}//END: motorBrake
