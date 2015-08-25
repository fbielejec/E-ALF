#include <Arduino.h>

/**---CONSTANTS---*/

const int LED_PIN = 13;

/**---PROTOTYPES---*/

void blinkNumber( byte number) ;

void dataDisplayBegin(int nbrItems, char* labels[], int minRange[], int maxRange[] ) ;

void sendLabel( int row, char *label) ;

void sendRange( int row, int min, int max);

void sendData(int row, int val) ;

void sendValue( int value) ;

void sendString(char *string);
