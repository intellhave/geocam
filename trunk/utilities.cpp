#include "utilities.h"
#include <stdio.h>
#include <stdarg.h>
void pause() {
  pause("PAUSE...");
}
void pause(char* format, ...) {
  va_list args;
  va_start(args, format);
  vprintf(format, args); scanf("%*c", NULL); fflush(stdin); // PAUSE
  va_end(args);
}
