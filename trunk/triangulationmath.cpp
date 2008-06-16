/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
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


/***************************************************************************
*         SOLVING DIFFERENTIAL SYSTEMS WITH P VARIABLES OF ORDER 1         *
*                 of type yi' = f(y1,y2,...,yn), i=1..n                    *
* ------------------------------------------------------------------------ *
*  INPUTS:                                                                 *
*    m         number of points to display                                 *
*    xi, xf    begin, end values of variable x                             *
*    initWeights table of begin values of functions at xi                    *
*    p         number of independant variables                             *
*    fi        finesse (number of intermediary points)                     *
*                                                                          *
*  OUTPUTS:                                                                *
*    t1,t2     real vectors storing the results for first two functions,   *
*              y1 and y2.                                                  *
* ------------------------------------------------------------------------ *      
*  EXAMPLE:    y1'=y2+y3-3y1, y2'=y1+y3-3y2, y3'=y1+y2-3y3                 *
*              Exact solution :  y1 = 1/3 (exp(-4x)  + 2 exp(-x))          *
*                                y2 = 1/3 (4exp(-4x) + 2 exp(-x))          *
*                                y3 = 1/3 (-5exp(-4x)+ 2 exp(-x))          *
***************************************************************************/
void calcFlow(double ti,double tf,double *initWeights,int numSteps,int fi, bool adjF)  {
  int p = Triangulation::vertexTable.size();
  double h,t;
  double ta[p],tb[p],tc[p],td[p],y[p],z[p];
  int    i,j,k,ni;
  ofstream results("c:/Documents and Settings/student/Desktop/Triangulations/ODE Result.txt", ios_base::trunc);
  results << left; 
   if (fi<1) return;
   h = (tf - ti) / fi / (numSteps-1);
   p;
   for (k=0; k<p+1; k++) { 
     z[k]=initWeights[k];
   }
   for (i=1; i<numSteps+1; i++) {
    results << "Step " << i << "\n------------\n";
     
     ni=(i-1)*fi-1;
     for (j=1; j<fi+1; j++) {
       t=ti+h*(ni+j);
       for (k=0; k<p; k++)  
       {
           results << "Vertex " << k + 1<< ": " << z[k] << "\n";
           Triangulation::vertexTable[k + 1].setWeight(z[k]);
       }
       for (k=0; k<p; k++)  
       {
           if(adjF) ta[k]=h*adjDiffEQ(k + 1,t);
           else     ta[k]=h*stdDiffEQ(k + 1,t);
       }
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]+ta[k]/2);
       }
       t=t+h/2;
       for (k=0; k<p; k++)  
       {
            if(adjF) ta[k]=h*adjDiffEQ(k + 1,t);
            else     ta[k]=h*stdDiffEQ(k + 1,t);
       }
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]+tb[k]/2);
       }
       for (k=0; k<p; k++)  
       {
            if(adjF) ta[k]=h*adjDiffEQ(k + 1,t);
            else     ta[k]=h*stdDiffEQ(k + 1,t);
       }
       for (k=0; k<p; k++)  
       {
           Triangulation::vertexTable[k + 1].setWeight(z[k]+tc[k]/2);
       }
       t=t+h/2;
       for (k=0; k<p; k++)  
       {
            if(adjF) ta[k]=h*adjDiffEQ(k + 1,t);
            else     ta[k]=h*stdDiffEQ(k + 1,t);
       }
       for (k=0; k<p; k++)
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
     }
     results << "\n";
   }
   results.close();
}

double stdDiffEQ(int vertex, double t) {
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight();
}

double adjDiffEQ(int vertex, double t)
{
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight() +
                   Triangulation::netCurvature() /  Triangulation::vertexTable.size()
                   * Triangulation::vertexTable[vertex].getWeight();
}
