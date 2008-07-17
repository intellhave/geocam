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
     
     cout << "Angles: " << ang1 << " " << ang2 << endl;
     
     if((ang1 + ang2) > PI)
          return false;
     else
          return true;
     
}

bool isWeightedDelauney(Edge e)
{
     
     
     
     
     return false;
}
