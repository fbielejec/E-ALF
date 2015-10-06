#include "utils.h"

float sigmoid(float input) {
    /**
     * @return number in [-1.0, 1.0], 0.0 returned at input = 0
     * */
    return ((1 / (1 + pow(E, -input)))-0.5)*2;
}//END: sigmoid

float mapFloat(float value, float fromLow, float fromHigh,
               float toLow, float toHigh) {
    return( toLow + (toHigh - toLow ) * ((value - fromLow) / (fromHigh - fromLow)) );
}// END: mapFloat

float randFloat() {
    return ( rand() / (RAND_MAX + 1.0));
}//END: randFloat

float randomClamped() {
    return randFloat() - randFloat();
}//END: randomCLamped

int* calculateCoordinates(int index, int nrow, int ncol) {

    int *coords = (int *) malloc(sizeof(int) * 2);

    //for each row
    for(int i = 0; i < nrow; i++) {
        //check if the index parameter is in the row
        if(index < (ncol * i) + ncol && index >= ncol * i) {
            //return x, y
            int row = index - ncol * i;
            int col = i;
            coords[0] = row;
            coords[1] = col;
        }
    }
    return coords;
}//END: calculateCoordinates


char* float2s(float f, unsigned int digits) {
    int index = 0;
    static char s[16];                    // buffer to build string representation
    // handle sign
    if (f < 0.0) {
        s[index++] = '-';
        f = -f;
    }
    // handle infinite values
    if (isinf(f)) {
        strcpy(&s[index], "INF");
        return s;
    }
    // handle Not a Number
    if (isnan(f)) {
        strcpy(&s[index], "NaN");
        return s;
    }

    // max digits
    if (digits > 6) digits = 6;
    long multiplier = pow(10, digits);     // fix int => long

    int exponent = int(log10(f));
    float g = f / pow(10, exponent);
    if ((g < 1.0) && (g != 0.0)) {
        g *= 10;
        exponent--;
    }

    long whole = long(g);                     // single digit
    long part = long((g-whole)*multiplier);   // # digits
    char format[16];
    sprintf(format, "%%ld.%%0%dld E%%+d", digits);
    sprintf(&s[index], format, whole, part, exponent);

    return s;
}

char* float2s(float f) {
    return float2s(f, 2);
}
