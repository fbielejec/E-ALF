#include <math.h>
//#include <cmath>
#include <cstdlib>

//inline
float sigmoid(float input);

//returns a random float between zero and 1
//inline
float randFloat(); //	   {return (rand())/(RAND_MAX+1.0);}

//returns a random float in the range -1 < n < 1
//inline
float randomClamped(); //   {return randFloat() - randFloat();}
