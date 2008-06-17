/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 17, 2008
***************************************************************
The Triangulation Math file holds the functions that perform
calculations on the triangulation.
**************************************************************/
#include <cstdlib>
#include <iostream>
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "triangulation.h"
#include "triangulationmath.h"
#include <fstream>
#include <iomanip>

#define PI 	3.141592653589793238

double angle(double lengthA, double lengthB, double lengthC)
{
       //               a^2 + b^2 - c^2
       //  (/) = acos( ----------------- )
       //                     2ab
       return acos((lengthA*lengthA + lengthB*lengthB - lengthC*lengthC)
                                    / (2*lengthA*lengthB));
}

double angle(Edge a, Edge b, Edge c)
{
       return angle(a.getLength(), b.getLength(), c.getLength());
}

double angle(Vertex v, Face f)
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
       return angle(e1.getLength(), e2.getLength(), e3.getLength());       
}

double curvature(Vertex v)
{
       double sum = 0;
       vector<int>::iterator it;
       vector<int>* vp = v.getLocalFaces();
       for(it = (*vp).begin(); it < (*vp).end(); it++)
       {
              sum += angle(v, Triangulation::faceTable[*it]);
       }
       return 2*PI - sum;
}

vector<int> listIntersection(vector<int>* list1, vector<int>* list2)
{
             vector<int> sameAs;
             
             for(int i = 0; i < (*list1).size(); i++)
             {
                     for(int j = 0; j < (*list2).size(); j++)
                     {
                             if((*list1)[i] == (*list2)[j])
                             {
                             sameAs.push_back((*list1)[i]);
                             }
                     }
             }
             return sameAs;
}


/*
 * Calculates the Ricci flow of the current Triangulation using the 
 * Runge-Kutta method. Results from the steps are written into the file 
 * "ODE Result" for viewing. calcFlow takes a number of paramters:
 *      double dt -          The time step size. Initial and ending
                             times not needed since diff. equations are
                             independent of time.
 *      double* initWeights- Array of initial weights of the Vertices 
 *                           in order.
 *      int numSteps -       The number of steps to take. 
 *                           (dt = (tf - ti)/numSteps)
 *      bool adjF -          Boolean of whether or not to use adjusted
 *                           differential equation. True to use adjusted.
 *      int fi -             The finesse, or number of intermediary points.
 *                           Default of 1.
 * 
 * The information printed in the file are the weights and curvatures for
 * each Vertex at each step point. The file is cleared at the beginning of
 * every call to calcFlow.
 *
 *            ***Credit for the algorithm goes to J-P Moreau.***
 */
void calcFlow(double dt ,double *initWeights,int numSteps, bool adjF, int fi)  {
  int p = Triangulation::vertexTable.size(); // The number of vertices / 
                                             // number of variables in system.
  double h = dt / fi; 
  double ta[p],tb[p],tc[p],td[p],y[p],z[p];
  int    i,j,k,ni;
  ofstream results("C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt", ios_base::trunc);
  results << left << setprecision(4); 
  results.setf(ios_base::showpoint);
   if (fi<1) return;
   for (k=0; k<p; k++) { 
     z[k]=initWeights[k];
   }
   for (i=1; i<numSteps+1; i++) {
    results << left << "Step " << left <<setw(4)  << i;
    results << right << setw(7) << "Weight";
    results << right << setw(7) << "Curv" << "\n-----------------------\n";
     
     ni=(i-1)*fi-1;
     for (j=1; j<fi+1; j++) {
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]);
       }
       for (k=0; k<p; k++)  
       {
           results << "Vertex " << k + 1<< ": " << z[k] << " / ";
           double curv = curvature(Triangulation::vertexTable[k + 1]);
           results << curv << "\n";
           if(adjF) ta[k]= (-1) * curv 
                           * Triangulation::vertexTable[k + 1].getWeight() +
                           Triangulation::netCurvature() /  p
                           * Triangulation::vertexTable[k+ 1].getWeight();
           else     ta[k] = (-1) * curv 
                           * Triangulation::vertexTable[k + 1].getWeight();
       }
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]+ta[k]/2);
       }
       for (k=0; k<p; k++)  
       {
            if(adjF) ta[k]=h*adjDiffEQ(k + 1);
            else     ta[k]=h*stdDiffEQ(k + 1);
       }
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]+tb[k]/2);
       }
       for (k=0; k<p; k++)  
       {
            if(adjF) ta[k]=h*adjDiffEQ(k + 1);
            else     ta[k]=h*stdDiffEQ(k + 1);
       }
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]+tc[k]/2);
       }
       for (k=0; k<p; k++)  
       {
            if(adjF) ta[k]=h*adjDiffEQ(k + 1);
            else     ta[k]=h*stdDiffEQ(k + 1);
       }
       for (k=0; k<p; k++)
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
     }
     results << "\n\n";
   }
   results.close();
}

double stdDiffEQ(int vertex) {
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight();
}

double adjDiffEQ(int vertex)
{
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight() +
                   Triangulation::netCurvature() /  Triangulation::vertexTable.size()
                   * Triangulation::vertexTable[vertex].getWeight();
}
