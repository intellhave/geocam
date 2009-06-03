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
#include "3DTriangulation/3DInputOutput.h"

#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#include "triangulation/triangulationPlane.h"
#include "triangulation/smallMorphs.h"

#include "flow/approximator.h"
#include "flow/rungaApprox.h"
#include "flow/eulerApprox.h"
#include "flow/sysdiffeq.h"

#include "flow/parsecalc.h"

#include "Geometry/Geometry.h"

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
    Geometry::setRadius(vIter->second, radii[kk]);
  }

  if(kk != radii.size() || vIter != vEnd){
    printf("Number of radii specified does not match\n");
    printf("number of vertices specified. Aborting.\n");
    exit(1);
  }
}

/* COMMAND-LINE FLOW */
//int main(int argc, char** argv){
//  validateArgs(argc, argv);
//  char* log_filename = argv[1];
//  calcSpecs specs(log_filename);
//
//  readTriangulationFile(specs.tri_filename.c_str());
//  loadRadii(specs.radii);
//  
//  Approximator* app = specs.approx;
//
//  switch(specs.ctype){
//  case BY_ACCURACY:
//    app->run(specs.precision, specs.accuracy, specs.stepsize);
//    break;
//  case BY_STEPS:
//    app->run((int) specs.numsteps, specs.stepsize);
//    break;
//  }
//  
//  printResultsStep(argv[2], &(app->radiiHistory), &(app->curvHistory)); 
//  delete app;
//  return 0;
//}

/* 2D FLOW */
//int main(int argc, char** argv){
//     map<int, Vertex>::iterator vit;
//     map<int, Edge>::iterator eit;
//     map<int, Face>::iterator fit;
//     map<int, Tetra>::iterator tit;
//     
//     vector<int> edges;
//     vector<int> faces;
//     vector<int> tetras;
//    
//   char from[] = "C:/Dev-Cpp/geocam/Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
//   char to[] = "C:/Dev-Cpp/geocam/Triangulation Files/manifold converted.txt";
//   //make3DTriangulationFile(from, to);
//   //read3DTriangulationFile(to);
//   char tetra[] = "C:/Dev-Cpp/geocam/Triangulation Files/2D Manifolds/Standard Format/torus-9.txt";
//   readTriangulationFile(tetra);
//   int vertSize = Triangulation::vertexTable.size();
//   int edgeSize = Triangulation::edgeTable.size();
//   //Geometry::setDimension(ThreeD);
//   printf("Building Geoemtry\n");
//   Geometry::build();
//   printf("Done Building Geometry\n");
//   for(int i = 1; i <= vertSize; i++) {
//      Geometry::setRadius(Triangulation::vertexTable[i], 1 + (5 - i)/15.0);        
//   }
//   for(int i = 1; i <= edgeSize; i++) {
//       Geometry::setEta(Triangulation::edgeTable[i], 1.0);
//   }
//   Approximator *app = new EulerApprox((sysdiffeq) AdjRicci);
//   app->run(0.0001, 0.0001, 0.01);
//   printResultsStep("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt", 
//                      &(app->radiiHistory), &(app->curvHistory));
//   system("PAUSE");
//}

/* 3D FLOW */
int main(int argc, char** argv){
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Face>::iterator fit;
     map<int, Tetra>::iterator tit;
     
     vector<int> edges;
     vector<int> faces;
     vector<int> tetras;
    
    
     time_t start, end;
     
   char from[] = "C:/Dev-Cpp/geocam/Triangulation Files/3D Manifolds/Lutz Format/poincare-16.txt";
   char to[] = "C:/Dev-Cpp/geocam/Triangulation Files/manifold converted.txt";
   make3DTriangulationFile(from, to);
   read3DTriangulationFile(to);
   char tetra[] = "C:/Dev-Cpp/geocam/Triangulation Files/2D Manifolds/Standard Format/tetrahedron.txt";
   //readTriangulationFile(tetra);
   int vertSize = Triangulation::vertexTable.size();
   int edgeSize = Triangulation::edgeTable.size();
   Geometry::setDimension(ThreeD);
   printf("Building Geoemtry\n");
   Geometry::build();
   printf("Done Building Geometry\n");
   for(int i = 1; i <= vertSize; i++) {
      Geometry::setRadius(Triangulation::vertexTable[i], 1);        
   }
   for(int i = 1; i <= edgeSize; i++) {
       Geometry::setEta(Triangulation::edgeTable[i], 1.0);
   }
   Approximator *app = new EulerApprox((sysdiffeq) Yamabe, "rcv");
   time(&start);
   app->run(300, 0.01);
   time(&end);
   printResultsStep("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt", 
                      &(app->radiiHistory), &(app->curvHistory));
   printResultsVolumes("C:/Dev-Cpp/geocam/Triangulation Files/Volumes.txt",
                          &(app->volumeHistory));
   printf("Time: %.2lf seconds\n", difftime(end, start));
   system("PAUSE");
}

