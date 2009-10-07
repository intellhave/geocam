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
#include "edge_curvature.h"
#include "total_volume_partial.h"

#include "Pipelined_Newtons_Method.h"

#include "volume_length_tetra_partial.h"

#include "simplex.h"

#define PI 3.141592653589793238
#define CONST_VOL 4.71404520791


using namespace std;

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

void setEtas(double etas[]) {
   map<int, Edge>::iterator eit;
   int i = 0;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++, i++) {
       Eta::At(eit->second)->setValue(etas[i]);        
   }
}

double EHR() {
       return TotalCurvature::valueAt() / pow(TotalVolume::valueAt(), 1.0/3.0);
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
   
   Newtons_Method(0.00001, false);
   double value = EHR();
 
   return value;
}

void saddleFinderHess(double vals[], double *soln[]) {
       double f_x = saddleFinder(vals);
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


void printFunc(FILE* out) {
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Tetra>::iterator tit;
     fprintf(out, "\n");
     
     // Prints Radii:
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
             vit++) {
        fprintf(out, "Radius %d = %.10f\n", vit->first, Radius::valueAt(vit->second));
     }
     
     // Prints Etas:
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
             eit++) {
        fprintf(out, "Eta %d = %.10f\n", eit->first, Eta::valueAt(eit->second));
     }
     
     // Prints Lengths:
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
             eit++) {
        fprintf(out, "Length %d = %.10f\n", eit->first, Length::valueAt(eit->second));
     }
     
     // Prints Edge Curvatures:
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
             eit++) {
        fprintf(out, "Edge Curvature %d = %.10f\n", eit->first, EdgeCurvature::valueAt(eit->second));
     }
     
     // Prints Einstein Ratios and Edge Volumes:
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
             eit++) {
                    double temp=0.0;
                    for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++) {
                            if(eit->second.isAdjTetra(tit->first)){
                          temp += VolumeLengthTetraPartial::valueAt(eit->second, tit->second);
                          }
                    }
        fprintf(out, "Einstein Ratio %d = %.10f \t Edge Volume = %.10f\n", eit->first, EdgeCurvature::valueAt(eit->second)/temp, temp);
          }
          
     // Prints The Constant Scalar Curvature Ratio:
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
             vit++) {
          fprintf(out, "CSC Ratio %d = %.10f\n", vit->first, Curvature3D::valueAt(vit->second)/TotalVolumePartial::valueAt(vit->second));
     }
     
     // Prints The NEHR Functional:
     fprintf(out, "EHR: %.10f\n=================================================\n", EHR());
}






int main(int arg, char** argv) { 
    
TransConformal();  

    pause("Done...press enter to exit."); // PAUSE   
}
