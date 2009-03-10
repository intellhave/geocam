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
