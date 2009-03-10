#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include <map>
#include <vector>

#include "parsecalc.h"

#define PI 3.141592653589793238

using namespace std;

void validateArgs(int argc, char** argv){
  if(argc != 2){
    printf("USAGE: flow [flow-description-file]\n");
    exit(1);
  }
}

void loadRadii(vector<double> radii){
  map<int, Vertex>::iterator vIter;
  map<int, Vertex>::iterator vStart = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  int kk;
  for(vIter = vStart, kk = 0;
      vIter != vEnd && kk < radii.size();
      vIter++, kk++)
  {
    vIter->second.setRadius(rInput);
  }

  if(kk != radii.size() || vIter != vEnd){
    printf("Number of radii specified does not match\n");
    printf("number of vertices specified. Aborting.\n");
    exit(1);
  }
}

int main(int argc, char** argv){
  validateArgs(argc, argv);
  char* log_filename = argv[1];
  calcSpecs specs(log_filename);

  readTriangulationFile(specs.tri_filename);
  loadRadii(specs.radii);
  
  Approximator app = specs.approx;

  switch(specs.ctype){
  case BY_ACCURACY:
    app.run(specs.precision, specs.accuracy, specs.stepsize);
    break;
  case BY_STEPS:
    app.run(specs.numsteps, specs.stepsize);
    break;
  }
  
  printResultsStep("Output.txt", &(app[ii]->radiiHistory),
                                  &(app[ii]->curvHistory)); 
  return 0;
}
