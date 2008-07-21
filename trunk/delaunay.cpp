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
#include "delaunay.h"
#define PI 	3.141592653589793238

bool isDelaunay(Edge e)
{
     if(e.isBorder())
     {
        return true;
     }
     Face fa1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     Face fa2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     
     vector<int> diff;
     diff = listDifference(fa1.getLocalVertices(), e.getLocalVertices());
     Vertex vb1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(fa2.getLocalVertices(), e.getLocalVertices());
     Vertex vb2 = Triangulation::vertexTable[diff[0]];
     
     
     double ang1 = angle(vb1, fa1);
     double ang2 = angle(vb2, fa2);
          
     if((ang1 + ang2) > PI)
          return false;
     else
          return true;
     
}

bool isWeightedDelaunay(Edge e)
{
     Face fa1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     Face fa2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     
     vector<int> diff;
     diff = listDifference(fa1.getLocalVertices(), e.getLocalVertices());
     Vertex vb1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(fa2.getLocalVertices(), e.getLocalVertices());
     Vertex vb2 = Triangulation::vertexTable[diff[0]];
     
     vector<int> sameAs;
     sameAs = listIntersection(va1.getLocalEdges(), vb1.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), vb2.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb1.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     
     double ang1 = angle(va1, fa1);
     double ang2 = angle(va1, fa2);
     double ang3 = angle(va2, fa1);
     double ang4 = angle(va2, fa2);
     
     Point pa1(0.0, 0.0);
     Point pa2(e.getLength(), 0.0);
     Point pb1((ea1.getLength() * cos(ang1)), (ea1.getLength() * sin(ang1)));
     Point pb2((eb1.getLength() * cos(ang2)), (-(eb1.getLength() * sin(ang2))));
     
     Circle ca1(pa1, va1.getWeight());
     Circle ca2(pa2, va2.getWeight());
     Circle cb1(pb1, vb1.getWeight());
     Circle cb2(pb2, vb2.getWeight());
     
//     double da1b1 = (pow(ea1.getLength(), 2) + va1.getWeight() - vb1.getWeight()) / (2 * ea1.getLength());
//     double db1a1 = (pow(ea1.getLength(), 2) + vb1.getWeight() - va1.getWeight()) / (2 * ea1.getLength());
//     double da1b2 = (pow(ea2.getLength(), 2) + va1.getWeight() - vb2.getWeight()) / (2 * ea2.getLength());
//     double db2a1 = (pow(ea2.getLength(), 2) + vb2.getWeight() - va1.getWeight()) / (2 * ea2.getLength());
//     double db1a2 = (pow(eb1.getLength(), 2) + vb1.getWeight() - va2.getWeight()) / (2 * eb1.getLength());
//     double da2b1 = (pow(eb1.getLength(), 2) + va2.getWeight() - vb1.getWeight()) / (2 * eb1.getLength());
//     double da2b2 = (pow(eb2.getLength(), 2) + va2.getWeight() - vb2.getWeight()) / (2 * eb2.getLength());
//     double db2a2 = (pow(eb2.getLength(), 2) + vb2.getWeight() - va2.getWeight()) / (2 * eb2.getLength());
//     double da1a2 = (pow(e.getLength(), 2) + va1.getWeight() - va2.getWeight()) / (2 * e.getLength());
//     double da2a1 = (pow(e.getLength(), 2) + va2.getWeight() - va1.getWeight()) / (2 * e.getLength());
//     
//     Line l(pa1, pa2);
//     Line la1(pa1, pb1);
//     Line la2(pa1, pb2);
//     Line lb1(pb1, pa2);
//     Line lb2(pb2, pa2);
//     
//     Point pa1b1(da1b1 * cos(ang1), da1b1 * sin(ang1));
//     Line f = la1.getPerpendicular(pa1b1);
//     Point pa1a2(da1a2, 0.0);
//     Line g = l.getPerpendicular(pa1a2);
//     Point pa2b1(pa2.x - (da2b1 * cos(ang3)), da2b1 * sin(ang3));
//     Line h = lb1.getPerpendicular(pa2b1);
//     Point o = f.intersection(g);
     
     return false;
}
