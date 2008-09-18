/**************************************************************
File: Hyperbolic Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Hyperbolic Math file holds the functions that perform
calculations on the triangulation under a hyperbolic geometry.
**************************************************************/
#include "hyperbolicmath.h"
#include <cmath>
#define PI 	3.141592653589793238

double hyperbolicAngle(double lengthA, double lengthB, double lengthC)
{
       return acos((cosh(lengthA)*cosh(lengthB)-cosh(lengthC))
                            / (sinh(lengthA)*sinh(lengthB)));                                          
}

double hyperbolicAngle(Vertex v, Face f)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
     Edge e1 = Triangulation::edgeTable[sameAs[0]];
     Edge e2 = Triangulation::edgeTable[sameAs[1]];
     diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
     Edge e3 = Triangulation::edgeTable[diff[0]];
     
     return hyperbolicAngle(e1.getLength(), e2.getLength(), e3.getLength());
}

double hyperbolicCurvature(Vertex v)
{
       double sum = 0;
       vector<int>::iterator it;
       vector<int>* vp = v.getLocalFaces();
       for(it = (*vp).begin(); it < (*vp).end(); it++)
       {
              sum += hyperbolicAngle(v, Triangulation::faceTable[*it]);
       }
       return 2*PI - sum;
}

void hyperbolicCalcFlow(vector<double>* radii, vector<double>* curvatures,double dt ,double* initRadii,int numSteps, bool adjF)
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
                                         
  double ta[p],tb[p],tc[p],td[p],z[p]; // Temporary arrays to hold data in.
  int    i,k; // ints used for "for loops".
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  double net = 0; // Net and prev hold the current and previous
  double prev;    //  net curvatures, repsectively.
   for (k=0; k<p; k++) {
    z[k]=initRadii[k]; // z[k] holds the current radii.
   }
   for (i=1; i<numSteps+1; i++) 
   {
    prev = net; // Set prev to net.
    net = 0;    // Reset net.
    
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
       if(i == 1) // If first time through, use static method.
       {
            prev = Triangulation::netHyperbolicCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // First "for loop" in whole step calculates
       {                    // everything manually, prints to file.
           (*radii).push_back( z[k]);
           double curv = hyperbolicCurvature(vit->second);
           if(curv < 0.00005 && curv > -0.00005) // Adjusted for small errors.
           {
             (*curvatures).push_back(0.);
           }
           else {
               (*curvatures).push_back(curv);
             }
           net += curv;
           if(adjF) ta[k]= dt * ((prev/p) * vit->second.getRadius() - curv 
                           * sinh(vit->second.getRadius()));
           else     ta[k] = dt * (-1) * curv 
                           * sinh(vit->second.getRadius());
           
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+ta[k]/2);
       }
       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = hyperbolicCurvature(vit->second);
           net += curv;
           if(adjF) tb[k]= dt * ((prev/p) * vit->second.getRadius() - curv 
                           * sinh(vit->second.getRadius()));
           else     tb[k] = dt * (-1) * curv 
                           * sinh(vit->second.getRadius());
           // if(adjF) tb[k]=dt*spherAdjDiffEQ(vit->first, net);
//            else     tb[k]=dt*spherStdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+tb[k]/2);
       }
       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = hyperbolicCurvature(vit->second);
           net += curv;
           if(adjF) tc[k]= dt * ((prev/p) * vit->second.getRadius() - curv 
                           * sinh(vit->second.getRadius()));
           else     tc[k] = dt * (-1) * curv 
                           * sinh(vit->second.getRadius());
          //  if(adjF) tc[k]=dt*spherAdjDiffEQ(vit->first, net);
//            else     tc[k]=dt*spherStdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+tc[k]);
       }
       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = hyperbolicCurvature(vit->second);
           net += curv;
           if(adjF) td[k]= dt * ((prev/p) * vit->second.getRadius() - curv 
                           * sinh(vit->second.getRadius()));
           else     td[k] = dt * (-1) * curv 
                           * sinh(vit->second.getRadius());
         //   if(adjF) td[k]=dt*spherAdjDiffEQ(vit->first, net);
//            else     td[k]=dt*spherStdDiffEQ(vit->first);
       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
   }
}
