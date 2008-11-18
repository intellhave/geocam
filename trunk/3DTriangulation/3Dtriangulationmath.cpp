#include "3Dtriangulationmath.h"
#include "3DInputOutput.h"
#include "triangulation/triangulation.h"
#include "spherical/sphericalmath.h"
#include "miscmath.h"
#define PI 	3.141592653589793238
double solidAngle(double angle1, double angle2, double angle3)
{
       // Beta i = dihedral angle
       double beta1 = sphericalAngle(angle1, angle2, angle3);
       double beta2 = sphericalAngle(angle1, angle3, angle2);
       double beta3 = sphericalAngle(angle2, angle3, angle1);
       return beta1 + beta2 + beta3 - PI;      
}       
double solidAngle(Vertex v, Tetra t)
{
       // Find the three faces local to v and part of the t.
       vector<int> sameAs = listIntersection(v.getLocalFaces(), t.getLocalFaces());
 
       Face f1 = Triangulation::faceTable[sameAs[0]];
       Face f2 = Triangulation::faceTable[sameAs[1]];
       Face f3 = Triangulation::faceTable[sameAs[2]];
       
       // Find the angle of each face on vertex v.
       double angle1 = angle(v, f1);
       double angle2 = angle(v, f2);
       double angle3 = angle(v, f3);
       return solidAngle(angle1, angle2, angle3);
}
double volumeSq(Tetra t) 
{
   vector<int> localV = *(t.getLocalVertices());
   double radii[4] = {0};
   for(int i = 0; i < localV.size(); i++)
   {
       radii[i] = Triangulation::vertexTable[localV[i]].getRadius();
   }
   return volumeSq(radii[0], radii[1], radii[2], radii[3]);
}
double volumeSq(double r1, double r2, double r3, double r4)
{
   double sum1 = 1/r1 + 1/r2 + 1/r3 + 1/r4;
   double sum2 = 1/(r1 * r1) + 1/(r2 * r2) + 1/(r3 * r3) + 1/(r4 * r4);
   double product = 1/9.0 * (r1 * r1) * (r2 * r2) * (r3 * r3) * (r4 * r4);
   return product * (sum1 * sum1 - 2 * sum2);
}
bool isDegenerate(Tetra t)
{
     return volumeSq(t) > 0;
}
double curvature3D(Vertex v)
{
       // For each tetra containing v, get the dihedral angle at v.
       double sum = 0.0;
       for(int i = 0; i < v.getLocalTetras()->size(); i++)
       {
           Tetra t = Triangulation::tetraTable[(*(v.getLocalTetras()))[i]];
           sum += solidAngle(v, t);
       }
       // Subtract sum from 4*PI
       return 4*PI - sum;
}

void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt ,
               double *initRadii,int numSteps, bool adjF)  
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
  vector<double> volumes;                                       
  double ta[p],tb[p],tc[p],td[p],z[p]; // Temporary arrays to hold data for 
                                      // the intermediate steps in.
  double curv[p];
  int    i,k; // ints used for "for loops". i is the step number,
              // k is the kth vertex for the arrays.
  map<int, Vertex>::iterator vit; // Iterator to traverse through the vertices.
  // Beginning and Ending pointers. (Used for "for" loops)
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  map<int, Tetra>::iterator tit;
  double netC = 0; // Net and prev hold the current and previous
  double netR = 0;
  double prevC;    //  net curvatures, repsectively.
  
   for (k=0; k<p; k++) {
    z[k]=initRadii[k]; // z[k] holds the current radii.
   }
   for (i=1; i<numSteps+1; i++) // This is the main loop through each step.
   {
       prevC = netC; // Set prev to net.
       netC = 0;    // Reset net.
       netR = 0;
    
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
           netR += z[k];
       }
       for (tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
       {
           volumes.push_back(volumeSq(tit->second));
       }
       if(i == 1) // If first time through, use static method to calculate total
       {           // cuvature.
          prevC = Triangulation::net3DCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++) 
       {  // First "for loop" in whole step calculates everything manually.
           (*radii).push_back( z[k]); // Adds the data to the vector.
           curv[k] = curvature3D(vit->second);
           if(curv[k] < 0.00005 && curv[k] > -0.00005) // Adjusted for small numbers.
           {                                     // We want it to print nicely.
             (*curvatures).push_back(0.); // Adds the data to the vector.
           }
           else {
               (*curvatures).push_back(curv[k]);
           }
           //netC += curv[k]; // Calculating the net curvature.
           netC += curv[k] * z[k]; // Calculating the net curvature.
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) ta[k]= dt * ((-1) * curv[k] 
                           * vit->second.getRadius() +
                           prevC /  netR
                           * vit->second.getRadius());
           else     ta[k] = dt * (-1) * curv[k] 
                           * vit->second.getRadius();
           
       }
       netR = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       { // Set the new radii to our triangulation.
           vit->second.setRadius(z[k]+ta[k]/2);
           netR += z[k]+ta[k]/2;
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            // Again calculates the differential equation, but we still need
            // the data in ta[] so we use tb[] now.
            if(adjF) tb[k]=dt * ((-1) * curv[k] 
                           * vit->second.getRadius() +
                           netC /  netR
                           * vit->second.getRadius());
            else     tb[k]=dt * (-1) * curv[k] 
                           * vit->second.getRadius();
       }
       netR = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+tb[k]/2);
           netR += z[k]+tb[k]/2;
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) tc[k]=dt * ((-1) * curv[k] 
                           * vit->second.getRadius() +
                           netC /  netR
                           * vit->second.getRadius());
            else     tc[k]=dt * (-1) * curv[k] 
                           * vit->second.getRadius();
       }
       netR = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+tc[k]);
           netR += z[k]+tc[k];
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) td[k]=dt * ((-1) * curv[k] 
                           * vit->second.getRadius() +
                           netC /  netR
                           * vit->second.getRadius());
            else     td[k]=dt * (-1) * curv[k] 
                           * vit->second.getRadius();
       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
   }
   printResultsVolumes("C:/Dev-Cpp/Geocam/Triangulation Files/Volume Results.txt", &volumes);
}

double stdDiffEQ3D(int vertex) 
{
       return (-1) * curvature3D(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getRadius();
}

double adjDiffEQ3D(int vertex, double totalCurv, double totalRadii)
{
       return (-1) * curvature3D(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getRadius() +
                   totalCurv /  totalRadii
                   * Triangulation::vertexTable[vertex].getRadius();
}
