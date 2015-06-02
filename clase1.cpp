//#include <stdio.h>
#include <stdlib.h>
//#include <string.h>
#include <iostream>
#include "clase1.h"

using namespace std;

pareja::pareja() {

}

void pareja::Lee(int &a2, int &b2) {
   a2 = a;
   b2 = b;
}

void pareja::Guarda(int a2, int b2) {
   a = a2;
   b = b2;
}
