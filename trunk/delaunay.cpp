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

bool isWeightedDelauney(Edge e)
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
     
     Point pa1(0.0, 0.0);
     Point pa2(e.getLength(), 0.0);
     Point pb1((ea1.getLength() * cos(ang1)), (ea1.getLength() * sin(ang1)));
     Point pb2((eb1.getLength() * cos(ang2)), (eb1.getLength() * sin(ang2)));
     
     
     
     return false;
}
