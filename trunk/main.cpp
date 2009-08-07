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
#include "totalcurvature.h"
#include "totalvolume.h"

#include "Pipelined_Newtons_Method.h"

#define PI 3.141592653589793238
#define CONST_VOL 4.71404520791


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

double func6(double vars[]) {
       double a = 1;
       double b = 1;
       return a * sqrt(1 + pow(vars[0] / b, 2));
}

double saddleFinder(double etas[]) {
   // Set Etas
   map<int, Edge>::iterator eit;
   map<int, Vertex>::iterator vit;
   int i = 0;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++, i++) {
       Eta::At(eit->second)->setValue(etas[i]);        
   }
   double radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );

   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
     Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
   }


//   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
//           vit++) {
//     printf("Radius[%d] = %.16f\n", vit->first, Radius::valueAt(vit->second) );
//   }


   Newtons_Method(0.00001);
   return TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0);
}

void printFunc(FILE* out) {
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     fprintf(out, "\n");
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
             vit++) {
        fprintf(out, "Radius %d = %.10f\n", vit->first, Radius::valueAt(vit->second));
     }
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
             eit++) {
        fprintf(out, "Eta %d = %.10f\n", eit->first, Eta::valueAt(eit->second));
     }
     fprintf(out, "-----------------\nEHR: %.10f\n", TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0));
}

int main(int arg, char** argv) {
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Face>::iterator fit;
     map<int, Tetra>::iterator tit;
     
     vector<int> edges;
     vector<int> faces;
     vector<int> tetras;
    
    
     time_t start, end;
     
   char from[] = "C:/Dev-Cpp/geocam/Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
   char to[] = "C:/Dev-Cpp/geocam/Triangulation Files/manifold converted.txt";
   make3DTriangulationFile(from, to);
   read3DTriangulationFile(to);
   //char tetra[] = "C:/Dev-Cpp/geocam/Triangulation Files/2D Manifolds/Standard Format/tetrahedron.txt";
   //readTriangulationFile(tetra);
   int vertSize = Triangulation::vertexTable.size();
   int edgeSize = Triangulation::edgeTable.size();
   for(int i = 1; i <= vertSize; i++) {
      if( i == 1) {
         Radius::At(Triangulation::vertexTable[i])->setValue(1.0);     
      } else {
         Radius::At(Triangulation::vertexTable[i])->setValue(1.0);
      }        
   }
   double etas[edgeSize];
   
   for(int i = 0; i < edgeSize; i++) {
      if( i ==0 ) {
          etas[i] = 1.0;
      } else {
      etas[i] = 1.0; 
      }       
   }
//  etas[0] = 0.92;
//  etas[1] = 0.87;
//  etas[2] = 1.03;
//  etas[3] = 0.98;
//  etas[4] = 1.12;
//  etas[5] = 1.06;
//  etas[6] = 0.96;
//  etas[7] = 0.83;
//  etas[8] = 1.09;
//  etas[9] = 1.00;

    NewtonsMethod *nm = new NewtonsMethod(saddleFinder, edgeSize);
    nm->setDelta(0.001);
    nm->setPrintFunc(printFunc);
    int i = 1;
    printf("%f\n", saddleFinder(etas));
    while(nm->step(etas, NMETHOD_MAX) > 0.00001) {
      printf("\n***** Step %d *****\n", i++);
      nm->printInfo(stdout);
      pause(); // PAUSE
    }
    printf("\n----Solution----\n");
    for(int j = 0; j < edgeSize; j++) {
        printf("eta_%d[%d] = %f\n", i, j, etas[j]);
    }
    pause("Done...press enter to exit."); // PAUSE   
}


//int main(int arg, char** argv) {
//    NewtonsMethod *nm = new NewtonsMethod(func6, 1);
//    //double initial[] = {0.5};
//    //printf("f(%f, %f) = %f\n", initial[0], initial[1], func4(initial));
//    //double soln[1];
//    double x_n[] = {3};
//    int i = 1;
//    printf("Initial\n-----------------\n");
//    for(int j = 0; j < 1; j++) {
//      printf("x_n_%d[%d] = %f\n", i, j, x_n[j]);
//    }
//    while(nm->step(x_n, NMETHOD_MAX) > 0.000001) {
//      printf("\n***** Step %d *****\n", i++);
//      nm->printInfo(stdout);
//      for(int j = 0; j < 1; j++) {
//        printf("x_n_%d[%d] = %f\n", i, j, x_n[j]);
//      }
//      //pause(); // PAUSE
//    }
//    //nm->optimize(initial, soln);
//    printf("\nSolution: %.10f\n", x_n[0]);
//    pause("Done...press enter to exit."); // PAUSE
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
//
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



