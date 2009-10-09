#include "triposition.h"
#include <new>
#include <cstdio>

#include "simplex.h"

/* Structure of a TriPosition call:
 * TriPosition(numPoints, TYPE, SimplexID1, SimplexID2, ... , SimplexIDN */

TriPosition::TriPosition(int numPoints, ... ){
  /* Load simplicies into the structure */
  length = numPoints;
  if(length > MAX_POINT_IDS) return;

  for(int ii = 0; ii < MAX_POINT_IDS; ii++)
    pointIDs[ii] = -1;
  
  va_list arguments;
  va_start(arguments, numPoints);
  for(int ii = 0; ii < length; ii++)    
    pointIDs[ii] = va_arg(arguments, int);
  va_end(arguments);

  /* Put the simplicies in sorted order... */
  for(int ii = 1; ii < length; ii++){
    int hold = pointIDs[ii];
    int jj = ii-1;
    while(jj >= 0 && hold < pointIDs[jj]){
      pointIDs[jj+1] = pointIDs[jj]; jj--;
    }
    pointIDs[jj + 1] = hold;
  }
}

void TriPosition::print(FILE* out){
  fprintf(out, "[ " );
  for(int ii = 0; ii < length; ii++)
    fprintf(out, "%d ", pointIDs[ii]);
  fprintf(out, "]");
}
