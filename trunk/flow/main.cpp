#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include <map>
#include <vector>

#include "3DTriangulation/3DTriangulationMorph.h"
#include "3DTriangulation/3Dtriangulationmath.h"

#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#include "triangulation/triangulationPlane.h"
#include "triangulation/smallMorphs.h"

#include "flow/approximator.h"
#include "flow/rungaApprox.h"
#include "flow/eulerApprox.h"
#include "flow/sysdiffeq.h"

#include "parsecalc.h"

#define PI 3.141592653589793238

using namespace std;

void validateArgs(int argc, char** argv){
  if(argc != 3){
    printf("USAGE: flow [flow-description-file] [results-file]\n");
    exit(1);
  }
}

void loadRadii(vector<double> radii){
  map<int, Vertex>::iterator vIter;
  map<int, Vertex>::iterator vStart = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  int kk = 0;
  for(vIter = vStart; vIter != vEnd && kk < radii.size(); vIter++, kk++){
    vIter->second.setRadius(radii[kk]);
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

  readTriangulationFile(specs.tri_filename.c_str());
  loadRadii(specs.radii);
  
  Approximator* app = specs.approx;

  switch(specs.ctype){
  case BY_ACCURACY:
    app->run(specs.precision, specs.accuracy, specs.stepsize);
    break;
  case BY_STEPS:
    app->run((int) specs.numsteps, specs.stepsize);
    break;
  }
  
  printResultsStep(argv[2], &(app->radiiHistory), &(app->curvHistory)); 
  delete app;
  return 0;
}
