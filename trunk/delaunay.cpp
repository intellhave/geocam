#include <cstdlib>
#include <iostream>
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "triangulation.h"
#include "triangulationmath.h"
#include "miscmath.h"
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
     if(e.isBorder())
     return true;
     
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
     sameAs = listIntersection(va2.getLocalEdges(), vb1.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), vb2.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     
     double anga1a1 = angle(va1, fa1);
     double anga1a2 = angle(va1, fa2);
     double anga2a1 = angle(va2, fa1);
     double anga2a2 = angle(va2, fa2);
     double angb1a1 = angle(vb1, fa1);
     double angb2a2 = angle(vb2, fa2);
     
     Point pa1(0.0, 0.0);
     Point pa2(e.getLength(), 0.0);
     Point pb1((ea1.getLength() * cos(anga1a1)), (ea1.getLength() * sin(anga1a1)));
     Point pb2((eb1.getLength() * cos(anga1a2)), (-(eb1.getLength() * sin(anga1a2))));
     
     double da1b1 = (pow(ea1.getLength(), 2) + pow(va1.getWeight(), 2) - pow(vb1.getWeight(), 2)) / (2 * ea1.getLength());
     double db1a1 = (pow(ea1.getLength(), 2) + pow(vb1.getWeight(), 2) - pow(va1.getWeight(), 2)) / (2 * ea1.getLength());
     double da1b2 = (pow(eb1.getLength(), 2) + pow(va1.getWeight(), 2) - pow(vb2.getWeight(), 2)) / (2 * eb1.getLength());
     double db2a1 = (pow(eb1.getLength(), 2) + pow(vb2.getWeight(), 2) - pow(va1.getWeight(), 2)) / (2 * eb1.getLength());
     double db1a2 = (pow(ea2.getLength(), 2) + pow(vb1.getWeight(), 2) - pow(va2.getWeight(), 2)) / (2 * ea2.getLength());
     double da2b1 = (pow(ea2.getLength(), 2) + pow(va2.getWeight(), 2) - pow(vb1.getWeight(), 2)) / (2 * ea2.getLength());
     double da2b2 = (pow(eb2.getLength(), 2) + pow(va2.getWeight(), 2) - pow(vb2.getWeight(), 2)) / (2 * eb2.getLength());
     double db2a2 = (pow(eb2.getLength(), 2) + pow(vb2.getWeight(), 2) - pow(va2.getWeight(), 2)) / (2 * eb2.getLength());
     double da1a2 = (pow(e.getLength(), 2) + pow(va1.getWeight(), 2) - pow(va2.getWeight(), 2)) / (2 * e.getLength());
     double da2a1 = (pow(e.getLength(), 2) + pow(va2.getWeight(), 2) - pow(va1.getWeight(), 2)) / (2 * e.getLength());
     
     double ha1 = (da1b1 - da1a2*cos(anga1a1)) / sin(anga1a1);
     double thing1 = (da2b1 - da2a1*cos(anga2a1)) / sin(anga2a1);
     double ha2 = (da2b2 - da2a1*cos(anga2a2)) / sin(anga2a2);
     double thing2 = (da1b2 - da1a2*cos(anga1a2)) / sin(anga1a2);
     
     Point oa1(da1a2, ha1);
     Point oa2(da1a2, (-ha2));
     
     double ra1 = sqrt((((da1a2*da1a2) + (da1b1*da1b1) - 2*da1a2*da1b1*cos(anga1a1)) / (sin(anga1a1)*sin(anga1a1))) - pow(va1.getWeight(), 2));
     double ra2 = sqrt((((da1a2*da1a2) + (da1b2*da1b2) - 2*da1a2*da1b2*cos(anga1a2)) / (sin(anga1a2)*sin(anga1a2))) - pow(va1.getWeight(), 2));
     
     Circle ca1(oa1, ra1);
     Circle ca2(oa2, ra2);
          
     return ha1 + ha2 >= 0;
}
