#include "testSystems.h"

#include "globalDefines.h"
#include "sensors.h"
#include "motors.h"
#include "display.h"

/**---METHODS---*/

void init_test(void) {

    init();

    // open serial port
    Serial.begin(9600);
    while (!Serial);

    blinkNumber(8);
    Serial.println("\t Comm-link online");

    AFMSBegin();

    // turn on motors
    motorBegin(MOTOR_LEFT);
    motorBegin(MOTOR_RIGHT);

    // initialize sensors
    irSensorBegin();

    Serial.println("Waiting for sensor input to detect blocked reflection");

}//END: init_io

void testSystems() {

 init_test();

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


}//END: testSystems







