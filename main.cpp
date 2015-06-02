#include <iostream>
#include "clase1.h"

using namespace std;

int main() {
   pareja par1;
   int x, y;
   
   par1.Guarda(12, 32);
   par1.Lee(x, y);
   cout << "Valor de par1.a: " << x << endl;
   cout << "Valor de par1.b: " << y << endl;

   return 0;
}
