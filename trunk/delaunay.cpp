#include <cstdlib>
#include <iostream>
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "triangulation.h"
#include "triangulationmath.h"
#include "triangulationInputOutput.h"
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
     {
        return true;
     }
     return getDual(e) > -0.00001;

}

double getHeight(Face f, Edge e)
{
     Vertex v1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex v2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     
     vector<int> diff;
     diff = listDifference(f.getLocalVertices(), e.getLocalVertices());
     if(diff.size() == 0)
     {
       cout << "Face: " << f.getIndex() << " " << f.getLocalVertices()->size() << "(";
       cout << (*(f.getLocalVertices()))[0] << ", ";
       cout << (*(f.getLocalVertices()))[1] << ", ";
       cout << (*(f.getLocalVertices()))[2] << ")\n";
       cout << "Edge: " << e.getIndex() << " " << e.getLocalVertices()->size() << "(";
       cout << (*(e.getLocalVertices()))[0] << ", ";
       cout << (*(e.getLocalVertices()))[1] << ")" << endl;
       writeTriangulationFile("Triangulations/troubleshoot.txt");
       system("PAUSE");
     }
     Vertex v3 = Triangulation::vertexTable[diff[0]];
     
     
     vector<int> sameAs;
     sameAs = listIntersection(v1.getLocalEdges(), v3.getLocalEdges());
     sameAs = listIntersection(&sameAs, f.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(v2.getLocalEdges(), v3.getLocalEdges());
     sameAs = listIntersection(&sameAs, f.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];

     double ang1 = angle(v1, f);
     double d13 = (pow(ea1.getLength(), 2) + pow(v1.getWeight(), 2) - pow(v3.getWeight(), 2)) / (2 * ea1.getLength());
     double d12 = (pow(e.getLength(), 2) + pow(v1.getWeight(), 2) - pow(v2.getWeight(), 2)) / (2 * e.getLength());
     double h = (d13 - d12*cos(ang1)) / sin(ang1);
     
     if(f.isNegative())
          return -h;
     else
          return h;
}

double getDual(Edge e)
{
     Face f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     Face f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     
     double h1 = getHeight(f1, e);
     double h2 = getHeight(f2, e);
     if(listDifference(f1.getLocalVertices(), f2.getLocalVertices()).size() == 0)
     {
        cout << "Double triangle: " << e.getIndex() << " " << 
        f1.getIndex() << " " << f2.getIndex() << "\n";
        cout << "Heights: " << h1 << " " << h2 << "\n";
        writeTriangulationFile("Triangulations/troubleshoot.txt");
        system("PAUSE");
     }
//     cout << "Edge #" << e.getIndex() << endl;
//     cout << "Face 1: (";
//     cout << (*(f1.getLocalVertices()))[0] << ", ";
//     cout << (*(f1.getLocalVertices()))[1] << ", ";
//     cout << (*(f1.getLocalVertices()))[2] << ") ";
//     cout << f1.isNegative() << endl;
//     cout << "Face 2: (";
//     cout << (*(f2.getLocalVertices()))[0] << ", ";
//     cout << (*(f2.getLocalVertices()))[1] << ", ";
//     cout << (*(f2.getLocalVertices()))[2] << ") ";
//     cout << f2.isNegative() << endl;
//     cout << "Height 1: " << h1 << endl;
//     cout << "Height 2: " << h2 << endl;
//     cout << "Dual: ";

     if(f1.isNegative() || f2.isNegative())
     {
        //  cout << -(h1 + h2) << endl;
          return -(h1 + h2);
     }
     else
     {
       //   cout << (h1 + h2) << endl;
          return h1 + h2;
     }
}

double getPartialEdge(Edge e, Vertex v1)
{
    int v2Index;
    if((*(e.getLocalVertices()))[0] != v1.getIndex())
    {
       v2Index = (*(e.getLocalVertices()))[0];
    } else
    {
       v2Index = (*(e.getLocalVertices()))[1];
    }
    Vertex v2 = Triangulation::vertexTable[v2Index];
    double d12 = (pow(e.getLength(), 2) + pow(v1.getWeight(), 2) - pow(v2.getWeight(), 2)) / (2 * e.getLength());
    return d12;
}









