/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Triangulation Math file holds the functions that perform
calculations on the triangulation.
**************************************************************/

#include <cmath>
#include "triangulationmath.h"
#include "math/miscmath.h"
#define PI 	3.141592653589793238

double angle(double lengthA, double lengthB, double lengthC)
{
       //               a^2 + b^2 - c^2
       //  (/) = acos( ----------------- )
       //                     2ab
       return acos((lengthA*lengthA + lengthB*lengthB - lengthC*lengthC)
                                    / (2*lengthA*lengthB));
}

double angle(Vertex v, Face f)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
     Edge e1 = Triangulation::edgeTable[sameAs[0]];
     Edge e2 = Triangulation::edgeTable[sameAs[1]];
     diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
     Edge e3 = Triangulation::edgeTable[diff[0]];
     
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

void curvature() {
   Triangulation::setAngles();
   map<int, Vertex>::iterator vit;
   double curv;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
   {
      curv = 2 * PI;
      for(int i = 0; i < vit->second.getLocalFaces()->size(); i++)
      {
         curv -= Triangulation::faceTable[(*(vit->second.getLocalFaces()))[i]].getAngle(vit->first);
      }
      vit->second.setCurvature(curv);
   }
}

double inRadius(Face f, double radius)
{
    if(f.getLocalVertices()->size() != 3) // Make sure the face has 3 vertices.
    {
        return -1;
    }
    double sum = 0;
    double product = 1;
    for(int i = 0; i < 3; i++)
    {     int index =(*(f.getLocalVertices()))[i];
          double r = Triangulation::vertexTable[index].getRadius();
          sum += r / radius;
          product *= r / radius; 
    }
    return radius * sqrt(product / sum);
}

double dualLength(Edge e)
{
       vector<int> localFaces = *(e.getLocalFaces());
       return inRadius(Triangulation::faceTable[localFaces[0]], 1)
              + inRadius(Triangulation::faceTable[localFaces[1]], 1);
}

double dualArea(Vertex v)
{
       vector<int> localEdges = (*v.getLocalEdges());
       double areaSum = 0;
       for(int i = 0; i < localEdges.size(); i++)
       {
          // Area of one of the triangles in this polygon.
          areaSum += v.getRadius() * dualLength(Triangulation::edgeTable[localEdges[i]]) / 2;
       }
       return areaSum;
}

double area(Face f) {
   Edge e1 = Triangulation::edgeTable[(*(f.getLocalVertices()))[0]];
   Edge e2 = Triangulation::edgeTable[(*(f.getLocalVertices()))[1]];
   Edge e3 = Triangulation::edgeTable[(*(f.getLocalVertices()))[2]];
   
   double l1 = e1.getLength();
   double l2 = e2.getLength();
   double l3 = e3.getLength();
   
   double area = pow(l1*l1 + l2*l2 + l3*l3, 2) + 
                    2*(pow(l1,4) + pow(l2,4) + pow(l3,4));
   return (1.0 / 4) * sqrt(area);
}

double totalArea() {
   double total = 0;
   map<int, Face>::iterator fit;
   
   for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
   {
       total += area(fit->second);
   }
   return total;
}

