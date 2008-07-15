/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 3, 2008
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
#include "line.h"
#include "miscmath.h"
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
                             break;
                             }
                     }
             }
             return sameAs;
}

vector<int> listDifference(vector<int>* list1, vector<int>* list2)
{
            vector<int> diff;
            
            for(int i = 0; i < (*list1).size(); i++)
            {
                    for(int j = 0; j < (*list2).size(); j++)
                    {
                            if((*list1)[i] == (*list2)[j])
                            break;
                            if(j == (*list2).size() - 1)
                            diff.push_back((*list1)[i]);
                    }
            }
            return diff;
}

double getAngleSum(Vertex v)
{
       double angleSum;
       for(int i = 0; i < v.getLocalFaces()->size(); i++)
       {
               angleSum += angle(v, Triangulation::faceTable[(*(v.getLocalFaces()))[i]]);
       }
       return angleSum;
}

double getSlope(Edge e)
{
       Vertex v1 = (*(e.getLocalVertices()))[0];
       Vertex v2 = (*(e.getLocalVertices()))[1];
       
       return ((v2.getYpos() - v1.getYpos())/(v2.getXpos() - v1.getXpos()));
}


/*
 * Calculates the Ricci flow of the current Triangulation using the 
 * Runge-Kutta method. Results from the steps are written into vectors of
 * doubles provided. The parameters are:
 *      vector<double>* weights-
 *                           A vector of doubles to append the results of
 *                           weights, grouped by step, with a total size of
 *                           numSteps * numVertices.
 *      vector<double>* curvatures-
 *                           A vector of doubles to append the results of
 *                           curvatures, grouped by step, with a total size
 *                           of numSteps * numVertices.
 *      double dt -          The time step size. Initial and ending
 *                           times not needed since diff. equations are
 *                           independent of time.
 *      double* initWeights- Array of initial weights of the Vertices 
 *                           in order.
 *      int numSteps -       The number of steps to take. 
 *                           (dt = (tf - ti)/numSteps)
 *      bool adjF -          Boolean of whether or not to use adjusted
 *                           differential equation. True to use adjusted.
 * 
 * The information placed in the vectors are the weights and curvatures for
 * each Vertex at each step point. The data is grouped by steps, so the first
 * vertex of the first step is the beginning element. After n doubles are
 * placed, for an n-vertex triangulation, the first vertex of the next step
 * follows. If the vectors passed in are not empty, the data is added to the
 * end of the vector and the original information is not cleared.
 *
 *            ***Credit for the algorithm goes to J-P Moreau.***
 */
void calcFlow(vector<double>* weights, vector<double>* curvatures,double dt ,double *initWeights,int numSteps, bool adjF)  
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
    
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the weights of the Triangulation.
           vit->second.setWeight(z[k]);
       }
       if(i == 1) // If first time through, use static method.
       {
            prev = Triangulation::netCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // First "for loop" in whole step calculates
       {                    // everything manually, prints to file.
           (*weights).push_back( z[k]);
           double curv = curvature(vit->second);
           if(curv < 0.00005 && curv > -0.00005) // Adjusted for small errors.
           {
             (*curvatures).push_back(0.);
           }
           else {
               (*curvatures).push_back(curv);
             }
           net += curv;
           if(adjF) ta[k]= dt * ((-1) * curv 
                           * vit->second.getWeight() +
                           prev /  p
                           * vit->second.getWeight());
           else     ta[k] = dt * (-1) * curv 
                           * vit->second.getWeight();
           
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+ta[k]/2);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) tb[k]=dt*adjDiffEQ(vit->first, net);
            else     tb[k]=dt*stdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tb[k]/2);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) tc[k]=dt*adjDiffEQ(vit->first, net);
            else     tc[k]=dt*stdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tc[k]);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) td[k]=dt*adjDiffEQ(vit->first, net);
            else     td[k]=dt*stdDiffEQ(vit->first);
       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
     if(net < 0.00005 && net > -0.00005) // Adjusted for small errors.
     {
            net = 0;
     }
   }
}

double stdDiffEQ(int vertex) {
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight();
}

double adjDiffEQ(int vertex, double totalCurv)
{
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight() +
                   totalCurv /  Triangulation::vertexTable.size()
                   * Triangulation::vertexTable[vertex].getWeight();
}


double inRadius(Face f)
{
    if(f.getLocalVertices()->size() != 3)
    {
        return -1;
    }
    double sum = 0;
    double product = 1;
    for(int i = 0; i < 3; i++)
    {     int index =(*(f.getLocalVertices()))[i];
          double w = Triangulation::vertexTable[index].getWeight();
          sum += w;
          product *= w; 
    }
    return sqrt(product / sum);
}

double dualLength(Edge e)
{
       vector<int> localFaces = *(e.getLocalFaces());
       return inRadius(Triangulation::faceTable[localFaces[0]])
              + inRadius(Triangulation::faceTable[localFaces[1]]);
}

double dualArea(Vertex v)
{
       vector<int> localEdges = (*v.getLocalEdges());
       double areaSum = 0;
       for(int i = 0; i < localEdges.size(); i++)
       {
          areaSum += v.getWeight() * dualLength(Triangulation::edgeTable[localEdges[i]]) / 2;
       }
       return areaSum;
}

