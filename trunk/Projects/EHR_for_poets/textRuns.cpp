#include "textRuns.h"
#include "triangulation/triangulation.h"
#include <map>
#include <cmath>
#include <cerrno>
#include "Pipelined_Newtons_Method.h"
#include "NMethod.h"

#include "eta.h"
#include "radius.h"
#include "curvature3D.h"
#include "totalcurvature.h"
#include "totalvolume.h"
#include "total_volume_partial.h"
#include "volume_length_tetra_partial.h"
#include "nehr_partial.h"
#include "nehr_second_partial.h"
#include "radius_partial.h"

#include "utilities.h"


void NEHR_Partial(double log_radii[], double grad[]) {
  map<int, Vertex>::iterator vit;
  int i;
  for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
          Radius::At(vit->second)->setValue(exp(log_radii[i]));
  }
  for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
        grad[i] = NEHRPartial::valueAt(vit->second)/* / Radius::valueAt(vit->second)*/;
  }
}

void NEHR_Second_Partial(double log_radii[], Matrix<double>& hess) {
     map<int, Vertex>::iterator vit1;
     map<int, Vertex>::iterator vit2;
     int i, j;
     for(vit1 = Triangulation::vertexTable.begin(), i = 0; vit1 != Triangulation::vertexTable.end(); vit1++, i++) {
              Radius::At(vit1->second)->setValue(exp(log_radii[i]));
     }
     for(vit1 = Triangulation::vertexTable.begin(), i = 0; vit1 != Triangulation::vertexTable.end(); vit1++, i++) {
        for(vit2 = Triangulation::vertexTable.begin(), j = 0; vit2 != Triangulation::vertexTable.end(); vit2++, j++) {
              if(i > j) {
                   hess[i][j] = hess[j][i];
              } else {
                   hess[i][j] = NEHRSecondPartial::valueAt(vit1->second, vit2->second)
                                /*/ (Radius::valueAt(vit1->second) * Radius::valueAt(vit2->second))*/;
              }
        }
     }
}

double NEHR(double log_radii[]) {
  map<int, Vertex>::iterator vit;
  int i;
  for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
          Radius::At(vit->second)->setValue(exp(log_radii[i]));
  }
  return TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0);
}

double NEHR() {
       return TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0);
}

void printFunc(FILE* out) {
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Tetra>::iterator tit;
       
     // Prints Radii:
     Radius::print(out);
     fprintf(out, "\n");
     
     // Prints Etas:
     Eta::print(out);
     fprintf(out, "\n");
     
     // Prints Lengths:
     Length::print(out);
     fprintf(out, "\n");
//
//     // Prints Euclidean Angles
//     EuclideanAngle::print(out);
//     fprintf(out, "\n");
     
     // Prints The NEHR Functional:
     fprintf(out, "\nNEHR: %.10f\n=================================================\n", NEHR());
}

void setEtas(double etas[]) {
   map<int, Edge>::iterator eit;
   int i = 0;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++, i++) {
       Eta::At(eit->second)->setValue(etas[i]);        
   }
}

void getEtas(double etas[]) {
   map<int, Edge>::iterator eit;
   int i = 0;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++, i++) {
       etas[i] = Eta::At(eit->second)->getValue();        
   }
}

void setLogRadii(double log_radii[]) {
   map<int, Vertex>::iterator vit;
   int i = 0;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++, i++) {
       Radius::At(vit->second)->setValue(exp(log_radii[i]));        
   }
}

void getLogRadii(double log_radii[]) {
   map<int, Vertex>::iterator vit;
   int i = 0;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++, i++) {
       log_radii[i] = log( Radius::At(vit->second)->getValue() );        
   }
}

void runMin(char* outputFile) {
     int vertSize = Triangulation::vertexTable.size();
     double log_radii[vertSize];
     double radius_scaling_factor;
     map<int, Vertex>::iterator vit;    
     getLogRadii(log_radii);
     
     NewtonsMethod *nm = new NewtonsMethod(NEHR, NEHR_Partial, NEHR_Second_Partial, vertSize);
     nm->setPrintFunc(printFunc);
     FILE* result = fopen(outputFile, "w");
     
     while(nm->step(log_radii) > 0.000001) {
       setLogRadii(log_radii);
       radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );
       for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
         Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
       }
       
       nm->printInfo(result);     
     }
          
     setLogRadii(log_radii);
     radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
        Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
     }
     nm->printInfo(result);
     fclose(result);     
}

void MinimizeRadii() {
  printf("......Minimizing Radii:.......\n");
  int vertSize = Triangulation::vertexTable.size();
  double log_radii[vertSize];
  double radius_scaling_factor;
  map<int, Vertex>::iterator vit;
  getLogRadii(log_radii);

  NewtonsMethod *nm = new NewtonsMethod(NEHR, NEHR_Partial, NEHR_Second_Partial, vertSize);
  //nm->setPrintFunc(printFunc);
  // FILE* result = fopen(outputFile, "w");

  while(nm->step(log_radii, NMETHOD_MIN) > 0.000001) {
    setLogRadii(log_radii);
    //nm->printInfo(result);
  }

  setLogRadii(log_radii);
  printf("\n.......Done minimizing......\n");
}

void NEHR_Eta_Partial(double* vars, double* sol) {
  map<int, Edge>::iterator eit;
  int edgeSize = Triangulation::edgeTable.size();
  int i;
  // Set Etas
  setEtas(vars);
  // Run Minimization in Radii
  MinimizeRadii();
  // Calc NEHR Partial w.r.t Eta into sol[]

  for(eit = Triangulation::edgeTable.begin(), i = 0; eit != Triangulation::edgeTable.end(); eit++, i++) {
    sol[i] = NEHRPartial::valueAt(eit->second);
  }
}

void NEHR_Eta_Sec_Partial(double* vars, Matrix<double>& sol) {
  int i, j;
  double sum;
  map<int, Edge>::iterator eit1;
  map<int, Edge>::iterator eit2;
  map<int, Vertex>::iterator vit;
  // Set Etas
  setEtas(vars);
  // Run Minimization in Radii
  MinimizeRadii();
  // Calc NEHR Second Partial
  i = 0;
  for(eit1 = Triangulation::edgeTable.begin(); eit1 != Triangulation::edgeTable.end(); eit1++, i++) {
    j = 0;
    for(eit2 = Triangulation::edgeTable.begin(); eit2 != Triangulation::edgeTable.end(); eit2++, j++) {
      sum = 0;
      for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        sum += NEHRSecondPartial::valueAt(vit->second, eit1->second)*RadiusPartial::valueAt(vit->second, eit2->second);
      }
      sol[i][j] = sum + NEHRSecondPartial::valueAt(eit1->second, eit2->second);
    }
  }
}

double NEHR_Eta(double etas[]) {
  setEtas(etas);
  MinimizeRadii();
  return TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0);
}

void runEtas(char* outputFile) {
    int edgeSize = Triangulation::edgeTable.size();
    double etas[edgeSize];
    getEtas(etas);
     
    NewtonsMethod *nm = new NewtonsMethod(NEHR_Eta, NEHR_Eta_Partial, NEHR_Eta_Sec_Partial, edgeSize);
    
    nm->setStepRatio(1.0/1000.0);
    nm->setStoppingGradientLength(0.0001);
//    nm->setPrintFunc(printFunc);
//    FILE* result = fopen(outputFile, "w");
    printf("min_r NEHR: %f\n", NEHR_Eta(etas));
    while(nm->step(etas) > 0.0001) {
      printf("min_r NEHR: %f\n", NEHR_Eta(etas));
      printFunc(stdout);
    }
    
    printf("min_r NEHR: %f\n", NEHR_Eta(etas));
    printFunc(stdout);

}

void NEHR_Eta_Sec_Partial_2(double* vars, Matrix<double>& sol) {
  int i, j;
  double sum;
  map<int, Edge>::iterator eit1;
  map<int, Edge>::iterator eit2;
  map<int, Vertex>::iterator vit;
  // Set Etas
  setEtas(vars);
  // Run Minimization in Radii
  MinimizeRadii();
  // Calc NEHR Second Partial
  i = 0;
  for(eit1 = Triangulation::edgeTable.begin(); eit1 != Triangulation::edgeTable.end(); eit1++, i++) {
    j = 0;
    for(eit2 = Triangulation::edgeTable.begin(); eit2 != Triangulation::edgeTable.end(); eit2++, j++) {
      sol[i][j] = NEHRSecondPartial::valueAt(eit1->second, eit2->second);
    }
  }
}

void runEtasNEHR(char* outputFile) {
    int edgeSize = Triangulation::edgeTable.size();
    double etas[edgeSize];
    getEtas(etas);

    NewtonsMethod *nm = new NewtonsMethod(NEHR_Eta, NEHR_Eta_Partial, NEHR_Eta_Sec_Partial_2, edgeSize);
    nm->setStepRatio(1.0/500.0);
    nm->setStoppingGradientLength(0.0001);
//    nm->setPrintFunc(printFunc);
//    FILE* result = fopen(outputFile, "w");
    printf("min_r NEHR: %f\n", NEHR_Eta(etas));
    while(nm->step(etas) > 0.000001) {
      printf("min_r NEHR: %f\n", NEHR_Eta(etas));
      printFunc(stdout);
    }

    printf("min_r NEHR: %f\n", NEHR_Eta(etas));
    printFunc(stdout);
}

void IDHess(double* vars, Matrix<double>& soln) {
  int edgeSize = Triangulation::edgeTable.size();
  for(int i = 0; i < edgeSize; i++) {
    for(int j = 0; j < edgeSize; j++) {
      if(i == j) {
        soln[i][j] = 1;
      } else {
        soln[i][j] = 0;
      }
    }
  }
}

void runEtasGradient(char* outputFile) {
    int edgeSize = Triangulation::edgeTable.size();
    double etas[edgeSize];
    getEtas(etas);

    NewtonsMethod *nm = new NewtonsMethod(NEHR_Eta, NEHR_Eta_Partial, IDHess, edgeSize);

//    nm->setPrintFunc(printFunc);
//    FILE* result = fopen(outputFile, "w");
    printf("min_r NEHR: %f\n", NEHR_Eta(etas));
    while(nm->step(etas) > 0.000001) {
      printf("min_r NEHR: %f\n", NEHR_Eta(etas));
      printFunc(stdout);
    }

    printf("min_r NEHR: %f\n", NEHR_Eta(etas));
    printFunc(stdout);
}

