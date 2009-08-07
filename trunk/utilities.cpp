#include "utilities.h"
#include <stdio.h>
#include <stdarg.h>
#include <map>
#include "triangulation.h"
#include "Volume.h"
void pause() {
  pause("PAUSE...");
}
void pause(char* format, ...) {
  va_list args;
  va_start(args, format);
  vprintf(format, args); scanf("%*c", NULL); fflush(stdin); // PAUSE
  va_end(args);
}

void printVolumes() {
     map<int, Tetra>::iterator tit;
     for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end();
             tit++) {
         printf("Volume at tetra %d: %f\n", tit->first, Volume::valueAt(tit->second));
     }

      // for(int i = 0; i < Triangulation::e
}
