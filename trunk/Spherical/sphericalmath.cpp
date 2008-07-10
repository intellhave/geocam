#include "sphericalmath.h"
#include <cmath>
#include <algorithm>
#include <iostream>
#define PI 	3.141592653589793238

double sphericalAngle(double lengthA, double lengthB, double lengthC, double radius)
{
    return acos((cos(lengthC/radius)-cos(lengthA/radius)*cos(lengthB/radius))
                               / (sin(lengthA/radius)*sin(lengthB/radius)));
}
double sphericalAngle(Edge edgeA, Edge edgeB, Edge edgeC, double radius)
{
       return sphericalAngle(edgeA.getLength(), edgeB.getLength(), edgeC.getLength(), radius);
}
double sphericalAngle(Vertex v, Face f, double radius)
{
       vector<int>::iterator it;
       vector<int>* vEdges = v.getLocalEdges();
       vector<int> fLocalEdges = *(f.getLocalEdges());
       
       int edges[2]; // Holds the indices of the edges that are
                     // in common between the face and vertex.
       int j = 0;
       for(int i = 0; i < 3; i++) // Iterate through the three edges
       {                          // of the face.
           
           int edgeToMatch = fLocalEdges[i];
           
           // Uses the find algortihm in C++ library to point to match
           it = find((*vEdges).begin(), (*vEdges).end(), edgeToMatch);
           // If there is a match (not pointing to end iterator)...
           
           if(it != (*vEdges).end())
           {     
                 // Add index of the vector holding the int of the
                 // edge.
                 edges[j] = i;
                 j++;
           }
           if(j == 2) { // Already found the two matches
                break;
           }
       }
       
       Edge e1 = Triangulation::edgeTable[fLocalEdges[edges[0]]];
       Edge e2 = Triangulation::edgeTable[fLocalEdges[edges[1]]];
       // Finds the index of the third and opposite edge.
       int eC = ((edges[0] + edges[1]) * 2) % 3;
       Edge e3 = Triangulation::edgeTable[fLocalEdges[eC]];
       return sphericalAngle(e1.getLength(), e2.getLength(), e3.getLength(), radius);
}
double sphericalCurvature(Vertex v)
{
       double sum = 0;
       vector<int>::iterator it;
       vector<int>* vp = v.getLocalFaces();
       for(it = (*vp).begin(); it < (*vp).end(); it++)
       {
              sum += sphericalAngle(v, Triangulation::faceTable[*it]);
       }
       return 2*PI - sum;
}

void sphericalCalcFlow(vector<double>* weights, vector<double>* curvatures,double dt ,double* initWeights,int numSteps, bool adjF)
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
    z[k]=initWeights[k]; // z[k] holds the current weights.
   }
   for (i=1; i<numSteps+1; i++) 
   {
    prev = net; // Set prev to net.
    net = 0;    // Reset net.
    if(prev < 0.0000001 && prev > -0.0000001)
            {
                    prev = 0.;
    }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the weights of the Triangulation.
           vit->second.setWeight(z[k]);
       }
       if(i == 1) // If first time through, use static method.
       {
            prev = Triangulation::netSphericalCurvature();
            if(prev < 0.0000001 && prev > -0.0000001)
            {
                    prev = 0.;
            }
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // First "for loop" in whole step calculates
       {                    // everything manually, prints to file.
           (*weights).push_back( z[k]);
           double curv = sphericalCurvature(vit->second);
           if(curv < 0.00005 && curv > -0.00005) // Adjusted for small errors.
           {
             (*curvatures).push_back(0.);
           }
           else {
               (*curvatures).push_back(curv);
             }
           net += curv;
           if(adjF) ta[k]= dt * ((4*PI/p - dualArea(vit->second))* curv 
                           * sin(vit->second.getWeight()));
           else     ta[k] = dt * (-1) * curv 
                           * sin(vit->second.getWeight());
           
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+ta[k]/2);
       }
       prev = net;
       net = 0;
       if(prev < 0.0000001 && prev > -0.0000001)
            {
                    prev = 0.;
            }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second);
           net += curv;
           if(adjF) tb[k]= dt * ((4*PI/p - dualArea(vit->second))* curv
                           * sin(vit->second.getWeight()));
           else     tb[k] = dt * (-1) * curv 
                           * sin(vit->second.getWeight());
           // if(adjF) tb[k]=dt*spherAdjDiffEQ(vit->first, net);
//            else     tb[k]=dt*spherStdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tb[k]/2);
       }
       prev = net;
       net = 0;
       if(prev < 0.0000001 && prev >-0.0000001)
            {
                    prev = 0.;
            }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second);
           net += curv;
           if(adjF) tc[k]= dt * ((4*PI/p - dualArea(vit->second)) * curv
                           * sin(vit->second.getWeight()));
           else     tc[k] = dt * (-1) * curv 
                           * sin(vit->second.getWeight());
          //  if(adjF) tc[k]=dt*spherAdjDiffEQ(vit->first, net);
//            else     tc[k]=dt*spherStdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tc[k]);
       }
       prev = net;
       net = 0;
       if(prev < 0.0000001 && prev > -0.0000001)
       {
                    prev = 0.;
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second);
           net += curv;
           if(adjF) td[k]= dt * ((4*PI/p - dualArea(vit->second)) * curv 
                           * sin(vit->second.getWeight()));
           else     td[k] = dt * (-1) * curv 
                           * sin(vit->second.getWeight());
         //   if(adjF) td[k]=dt*spherAdjDiffEQ(vit->first, net);
//            else     td[k]=dt*spherStdDiffEQ(vit->first);
       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
   }
}

double spherStdDiffEQ(int vertex) {
       return (-1) * sphericalCurvature(Triangulation::vertexTable[vertex])
                   * sin(Triangulation::vertexTable[vertex].getWeight());
}

double spherAdjDiffEQ(int vertex, double totalCurv)
{
       return (totalCurv /  Triangulation::vertexTable.size()
                         - sphericalCurvature(Triangulation::vertexTable[vertex]))
                         * sin(Triangulation::vertexTable[vertex].getWeight());
}

double delArea()
{     
       double bigSum = 0;
       map<int, Face>::iterator fit;
       for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
       {
               double littleSum = 0;
               for(int i = 0; i < fit->second.getLocalEdges()->size(); i++)
               {
                       Edge e = Triangulation::edgeTable[(*(fit->second.getLocalEdges()))[i]];
                       for(int j = 0; j < e.getLocalVertices()->size(); j++)
                         littleSum += sphericalCurvature(Triangulation::vertexTable[(
                                      *(e.getLocalVertices()))[j]  ]  )
                                      * tan(e.getLength()/2);
               }
               Vertex v = Triangulation::vertexTable[(*(fit->second.getLocalVertices()))[0]];
               bigSum += sin(v.getWeight())*tan(sphericalAngle(v, fit->second))*littleSum;
       }
       return bigSum;
}

double sphericalArea(Face f)
{
       double sum = 0;
       for(int i = 0; i < f.getLocalVertices()->size(); i++)
       {
           sum += sphericalAngle(Triangulation::vertexTable[(*(f.getLocalVertices()))[i]], f);    
       }
       sum -= PI;
       return sum;
}
