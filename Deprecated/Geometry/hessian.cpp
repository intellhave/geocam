#include "delaunay.h"
#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include "triangulation/smallMorphs.h"
#include "3DTriangulation/3DTriangulationMorph.h"
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"

#include "flow/approximator.h"
#include "flow/rungaApprox.h"
#include "flow/eulerApprox.h"
#include "flow/sysdiffeq.h"

#include "curvature3D.h"
#define PI 	3.141592653589793238

double SecondPartial(int i, int j, double dx_i, double dx_j);
double FEE(int i, int j, double dx_i, double dx_j);
double F();

void Hessian()
{
     Approximator* app = new EulerApprox( (sysdiffeq) Yamabe, "r3"); 
char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");

// initializes quantities for hessian calculation
double initRadii[Triangulation::vertexTable.size()];
//double initEta[Triangulation::edgeTable.size()];
double dt = 0.020;
double precision = 0.000000001;
getRadii(initRadii);
app->run(precision, dt);
//Triangulation::setRadii(initRadii);
getRadii(initRadii);
printf("F = %12.10f\n", F());
// end initialization
 
double Hess[Triangulation::edgeTable.size()][Triangulation::edgeTable.size()];
//double currentEta[Triangulation::edgeTable.size()];
int i, j;
double deltaEta=0.001;
//printf("%d\n", errno);
for(i=0; i<Triangulation::edgeTable.size(); ++i) {
      for(j=0; j<Triangulation::edgeTable.size(); ++j) {
               if (i <= j) {
                    setRadii(initRadii); 
                    Hess[i][j]=SecondPartial(i+1, j+1, deltaEta, deltaEta);
                    Hess[j][i]=Hess[i][j];
                    printf("i=%d, j=%d, Hess[%d,%d]=%12.10f\n", i+1, j+1, i, j, Hess[i][j]);
                    }
               }
      }
fprintf(result, "{");
for (i=0; i<Triangulation::edgeTable.size(); ++i) {
    fprintf(result, "{");
      for(j=0; j<Triangulation::edgeTable.size(); ++j) {
              if (j != Triangulation::edgeTable.size()-1)
              fprintf(result, "%12.10f, ", Hess[i][j]);
              else if (i != Triangulation::edgeTable.size()-1)
              fprintf(result, "%12.10f}, ", Hess[i][j]);
              else
              fprintf(result, "%12.10f}", Hess[i][j]);
              }
      }        
fprintf(result, "}");                     
fclose(result);       
}
         

double FEE(int ii, int jj, double dx_ii, double dx_jj)
{
       Approximator* app = new EulerApprox( (sysdiffeq) Yamabe, "r3"); 
       readEtas("Triangulation Files/MinMax Results/temp.txt");
       Eta* eta_ii = Eta::At(Triangulation::edgeTable[ii]);
       Eta* eta_jj = Eta::At(Triangulation::edgeTable[jj]);
       double curEta_ii = eta_ii->getValue();
       double curEta_jj = eta_jj->getValue();
       if (ii != jj)
              {
                eta_ii->setValue(curEta_ii+dx_ii);
                eta_jj->setValue(curEta_jj+dx_jj);
              }
       else if (ii == jj)
              {
                eta_ii->setValue(curEta_ii+dx_ii+dx_jj);
              // take special note of the calculation above; it adds the two infinitesimals!
              }                
       double initRadii[Triangulation::vertexTable.size()];
       double dt = 0.030;
       double precision = 0.000000001;
       getRadii(initRadii);
       app->run(precision, dt);
//       map<int, Vertex>::iterator vit;

       double result = F();
//       Triangulation::getRadii(initRadii);
       eta_ii->setValue(curEta_ii);
       eta_jj->setValue(curEta_jj);
       setRadii(initRadii);
       return result;
}



double SecondPartial(int i, int j, double dx_i, double dx_j)
{
       double result=9.999;
       if (i != j)
       //result=2.0;
       result = (FEE(i,j,dx_i,dx_j)-FEE(i,j,dx_i,-dx_j)-FEE(i,j,-dx_i,dx_j)+FEE(i,j,-dx_i,-dx_j))/(4.0*dx_i*dx_j);
       else if (i == j)
       {
       //result=4.0;
       double Alpha=FEE(i,j,dx_i,0.00);
       double Beta=FEE(i,j,-1.0*dx_i,0.00);
       double Gamma=FEE(i,j,0.00,0.00);
//       result = (FEE(i,j,dx_i,0.00)-2.0*FEE(i,j,0.00,0.00)+FEE(i,j,-dx_i,0.00))/(dx_i*dx_i);
       result = (Alpha-2.0*Gamma+Beta)/(dx_i*dx_i);
       printf("%.10f, %.10f, %.10f\n", Alpha, Beta, Gamma);
//       printf("%.10f, %.10f, %.10f\n", FE(dx_i, i), FE(-dx_i, i), FE(0.00, i));
       }
       //       printf("%d\n", errno);
       return result;
}






