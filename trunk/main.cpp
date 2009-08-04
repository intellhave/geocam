#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include <map>
#include <vector>

#include "utilities.h"

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
#include "NMethod.h"
#include "radius.h"
#include "curvature3D.h"

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
    Radius::At(vIter->second)->setValue(radii[kk]);
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

double func(double vars[]) {
       return exp(-pow(vars[0], 2));
}

void gradFunc(double vars[], double sol[]) {
     sol[0] = -2 * vars[0] * func(vars);
}

void hessFunc(double vars[], double *sol[]) {
     sol[0][0] = func(vars) * (4 * pow(vars[0], 2) - 2);
}

double func2(double vars[]) {
       double val = (pow(vars[0], 2) + pow(vars[1], 2));
       val *= exp(1 - (pow(vars[0], 2) + pow(vars[1], 2)));
       return val;
}

double func3(double vars[]) {
       return pow(vars[0], 8) + pow(vars[1], 8);
}

double func4(double vars[]) {
       double val = 1 - pow(vars[0], 2) / 4 - pow(vars[1], 2) / 9;
       return sqrt(val);
}

double func5(double vars[]) {
       return pow(pow(vars[0], 8), 1.0/7.0);
}

int main(int arg, char** argv) {
    NewtonsMethod *nm = new NewtonsMethod(func, 1);
    //double initial[] = {0.5};
    //printf("f(%f, %f) = %f\n", initial[0], initial[1], func4(initial));
    //double soln[1];
    double x_n[] = {1.7};
    int i = 1;
    printf("Initial\n-----------------\n");
    for(int j = 0; j < 1; j++) {
      printf("x_n_%d[%d] = %f\n", i, j, x_n[j]);
    }
    while(nm->step(x_n, NMETHOD_MAX) > 0.000001) {
      printf("\n***** Step %d *****\n", i++);
      nm->printInfo(stdout);
      for(int j = 0; j < 1; j++) {
        printf("x_n_%d[%d] = %f\n", i, j, x_n[j]);
      }
      //pause(); // PAUSE
    }
    //nm->optimize(initial, soln);
    printf("\nSolution: %.10f\n", x_n[0]);
    pause(); // PAUSE
}




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
//   char from[] = "C:/Dev-Cpp/geocam/Triangulation Files/2D Manifolds/Lutz Format/tetrahedron.txt";
//   char to[] = "C:/Dev-Cpp/geocam/Triangulation Files/manifold converted.txt";
//   makeTriangulationFile(from, to);
//   readTriangulationFile(to);
//   //char tetra[] = "C:/Dev-Cpp/geocam/Triangulation Files/2D Manifolds/Standard Format/tetrahedron.txt";
//   //readTriangulationFile(tetra);
//   int vertSize = Triangulation::vertexTable.size();
//   int edgeSize = Triangulation::edgeTable.size();
//   for(int i = 1; i <= vertSize; i++) {
//      Radius::At(Triangulation::vertexTable[i])->setValue(1 + (2 - i)/4.0);        
//   }
//   for(int i = 1; i <= edgeSize; i++) {
//       Eta::At(Triangulation::edgeTable[i])->setValue(1.0);
//   }
//   Approximator *app = new EulerApprox((sysdiffeq) AdjRicci, "r2");
//   app->run(300, 0.01);
//   printResultsStep("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt", 
//                      &(app->radiiHistory), &(app->curvHistory));
//   pause(); // PAUSE
//}



/* 3D FLOW */
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
//    
//     time_t start, end;
//     
//   char from[] = "C:/Dev-Cpp/geocam/Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
//   char to[] = "C:/Dev-Cpp/geocam/Triangulation Files/manifold converted.txt";
//   make3DTriangulationFile(from, to);
//   read3DTriangulationFile(to);
//   char tetra[] = "C:/Dev-Cpp/geocam/Triangulation Files/2D Manifolds/Standard Format/tetrahedron.txt";
//   //readTriangulationFile(tetra);
//   int vertSize = Triangulation::vertexTable.size();
//   int edgeSize = Triangulation::edgeTable.size();
//   for(int i = 1; i <= vertSize; i++) {
//      Radius::At(Triangulation::vertexTable[i])->setValue(1.0 - (i - 2.5) / 5.);        
//   }
//   for(int i = 1; i <= edgeSize; i++) {
//       Eta::At(Triangulation::edgeTable[i])->setValue(1.0);
//   }
////   double sum;
////   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
////           printf("%d: %f\n", eit->first, EdgeCurvature::valueAt(eit->second));
////   }
////   pause(); // PAUSE
//   Approximator *app = new EulerApprox((sysdiffeq) Yamabe, "r3");
//   time(&start);
//   app->run(300, 0.01);
//   //app->run(0.0001, 0.01);
//   time(&end);
//   print3DResultsStep("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt", 
//                      &(app->radiiHistory), &(app->curvHistory));
//   //printResultsVolumes("C:/Dev-Cpp/geocam/Triangulation Files/Volumes.txt",
//      //                    &(app->volumeHistory));
//   printf("Time: %.2lf seconds\n", difftime(end, start));
//
//   pause(); // PAUSE
////   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
////           printf("%d: %f\n", eit->first, EdgeCurvature::valueAt(eit->second));
////   }
//}



