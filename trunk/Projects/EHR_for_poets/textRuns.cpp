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
#include "ehr_partial.h"
#include "ehr_second_partial.h"

#include "utilities.h"


void runPipelinedNewtonsMethod(char* outputFile) {
     Newtons_Method(0.00001, outputFile);
}

void EHR_Partial(double log_radii[], double grad[]) {
  map<int, Vertex>::iterator vit;
  int i;
  for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
          Radius::At(vit->second)->setValue(exp(log_radii[i]));        
  }
  for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
        grad[i] = EHRPartial::valueAt(vit->second);
  }
}

void EHR_Second_Partial(double log_radii[], double *hess[]) {
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
                   hess[i][j] = EHRSecondPartial::valueAt(vit1->second, vit2->second);
              }
        }
     }     
}

double EHR() {
       return TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0);
}

double EHR(double log_radii[]) {
  map<int, Vertex>::iterator vit;
  int i;
  for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
          Radius::At(vit->second)->setValue(exp(log_radii[i]));        
  }
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
     
     // Prints Euclidean Angles
     EuclideanAngle::print(out);
     fprintf(out, "\n");
     
     // Prints The NEHR Functional:
     fprintf(out, "\nEHR: %.10f\n=================================================\n", EHR());
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

double saddleFinder(double etas[]) {
   // Set Etas
   map<int, Edge>::iterator eit;
   map<int, Vertex>::iterator vit;
   int i = 0;

   setEtas(etas);
   double radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );
//   double radius_scaling_factor = pow( 1.0/TotalVolume::valueAt(), 1.0/3.0 );

   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
     Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
   }
   
   Newtons_Method(0.00001);

/*DEBUG:   FILE* test = fopen("Data/Input_EHR/testFile1.txt", "a");
/*   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
/*           eit++, i++) {
/*       fprintf(test, "Eta %d: %f, ", eit->first, Eta::valueAt(eit->second));       
/*   }
/*   fprintf(test, "\n");
/*   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
/*           vit++) {
/*     fprintf(test, "Radius %d: %.12f, ", vit->first, Radius::valueAt(vit->second));
/*   }  
/*   fprintf(test, "\n%.12f\n\n", EHR());
/*   fclose(test);
/*DEBUG*/

 
   if(errno) {
      pause("There was an error after Pipelined_NM\n");
   }
   double value = EHR();
 
   return value;
}

double saddleFinder2(double etas[]) {
   // Set Etas
   map<int, Edge>::iterator eit;
   map<int, Vertex>::iterator vit;
   int i = 0;

   setEtas(etas);
   double radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );
//   double radius_scaling_factor = pow( 1.0/TotalVolume::valueAt(), 1.0/3.0 );

   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
     Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
   }

   
   int vertSize = Triangulation::vertexTable.size();
   double log_radii[vertSize];    
   getLogRadii(log_radii);
     
   NewtonsMethod *nm = new NewtonsMethod(EHR, EHR_Partial, EHR_Second_Partial, vertSize);     
   while(nm->step(log_radii) > 0.000001) {
     setLogRadii(log_radii);
     radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
         Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
     }    
   }
   delete nm;
   setLogRadii(log_radii);
   radius_scaling_factor = pow( 4.71404520791/TotalVolume::valueAt(), 1.0/3.0 );
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) {
      Radius::At(vit->second)->setValue( radius_scaling_factor * Radius::valueAt(vit->second) );
   }
   
/*DEBUG:   FILE* test = fopen("Data/Input_EHR/testFile2.txt", "a");
/*   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
/*           eit++, i++) {
/*       fprintf(test, "Eta %d: %f, ", eit->first, Eta::valueAt(eit->second));       
/*   }
/*   fprintf(test, "\n");
/*   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
/*           vit++) {
/*     fprintf(test, "Radius %d: %.12f, ", vit->first, Radius::valueAt(vit->second));
/*   }  
/*   fprintf(test, "\n%.12f\n\n", EHR());
/*   fclose(test);
/*DEBUG*/


   
   if(errno) {
      pause("There was an error after Pipelined_NM\n");
   }
   double value = EHR();
 
   return value;
}

void saddleFinderHess(double vals[], double *soln[]) {
       double f_x = saddleFinder2(vals);
       int size = Triangulation::edgeTable.size();
       double val;
       double delta =  0.00001;
       
       
       Eta* Etas[size];
       map<int, Edge>::iterator eit;
       int k = 0;
       for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++, k++) {
         Etas[k] = Eta::At(eit->second);        
       }
       
       for(int i = 0; i < size; i++) {
         for(int j = 0; j < size; j++) {
         printf("Approximating hessian of %d, %d\n", i, j);
           if(i > j) {
             soln[i][j] = soln[j][i];
           } else if( i != j) {
              Etas[i]->setValue(vals[i] + delta);
              Etas[j]->setValue(vals[j] + delta);
              val = EHR();
              
              Etas[j]->setValue(vals[j] - delta);
              val = val - EHR();
              
              Etas[i]->setValue(vals[i] - delta);
              val = val + EHR();
              
              Etas[j]->setValue(vals[j] + delta);
              val = val - EHR();
              
              Etas[i]->setValue(vals[i]);
              Etas[j]->setValue(vals[j]);
       
              soln[i][j] = val / (4 * delta * delta);
           } else {
              Etas[i]->setValue(vals[i] + delta);
              val = EHR();
              
              Etas[i]->setValue(vals[i] - delta);
              val = val + EHR();
              
              Etas[i]->setValue(vals[i]);
              val = val - 2*f_x;
              
              soln[i][j] = val / (delta * delta);
           }            
         }
       }
}

void runNewtonsMethod(char* outputFile) {
     int edgeSize = Triangulation::edgeTable.size();
     double etas[edgeSize];
     getEtas(etas);
          
     NewtonsMethod *nm = new NewtonsMethod(saddleFinder2, edgeSize);
     nm->setPrintFunc(printFunc);
     FILE* result = fopen(outputFile, "w");
     if(result == NULL) {
       pause("Error: %s is not a valid file\n", outputFile);
       exit(1);
     }
     
     while(nm->step(etas, NMETHOD_MIN) > 0.00001) {
       setEtas(etas);
       nm->printInfo(result);     
     }
     setEtas(etas);
     nm->printInfo(result);
     fclose(result);
}

void runMin(char* outputFile) {
     int vertSize = Triangulation::vertexTable.size();
     double log_radii[vertSize];
     double radius_scaling_factor;
     map<int, Vertex>::iterator vit;    
     getLogRadii(log_radii);
     
     NewtonsMethod *nm = new NewtonsMethod(EHR, EHR_Partial, EHR_Second_Partial, vertSize);
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

