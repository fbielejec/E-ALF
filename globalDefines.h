#define DEBUG 1

/**SENSORS**/

// defines to identify sensors
const int SENSE_IR_LEFT = 0;
const int SENSE_IR_RIGHT = 1;

/**DIRECTIONS**/

// defines for directions
const int DIR_LEFT = 0;
const int DIR_RIGHT = 1;
const int DIR_CENTER = 2;

// Debug labels
extern const char* locationString[];

/**OBSTACLES**/

// no obstacle detected
const int OBST_NONE = 0;
// left edge detected
const int OBST_LEFT_EDGE = 1;
// right edge detected
const int OBST_RIGHT_EDGE = 2;
// edge detect at both left and right sensors
const int OBST_FRONT_EDGE = 3;

/**MOVES**/

enum        {MOV_LEFT, MOV_RIGHT, MOV_FORWARD, MOV_BACK, MOV_ROTATE, MOV_STOP};
extern const char* states[];// = {"Left", "Right", "Forward", "Back", "Rotate", "Stop"};

/**OTHER**/

const int LED_PIN = 13;

