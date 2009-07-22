#include <cmath>
#include "delaunay.h"
#include <iostream>
#include "Geometry/geoquants.h"
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
     
     
     double ang1 = EuclideanAngle::valueAt(vb1, fa1);
     double ang2 = EuclideanAngle::valueAt(vb2, fa2);
          
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
     
    //almost always, calculations of dimensions, which involve arcsin,
    // will not yield a zero value, but instead a value very close to zero
    return getDual(e) > -0.00001;
}

double getHeight(Face f, Edge e)
{
    Vertex v1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
    Vertex v2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];

    vector<int> diff;
    diff = listDifference(f.getLocalVertices(), e.getLocalVertices());

    Vertex v3 = Triangulation::vertexTable[diff[0]];

    vector<int> sameAs;
    sameAs = listIntersection(v1.getLocalEdges(), v3.getLocalEdges());
    sameAs = listIntersection(&sameAs, f.getLocalEdges());
    Edge ea1 = Triangulation::edgeTable[sameAs[0]];
    /*sameAs = listIntersection(v2.getLocalEdges(), v3.getLocalEdges());
    sameAs = listIntersection(&sameAs, f.getLocalEdges());
    Edge ea2 = Triangulation::edgeTable[sameAs[0]];*/

    double ang1 = EuclideanAngle::valueAt(v1, f);

    double d13 = PartialEdge::valueAt(v1,ea1);
    double d12 = PartialEdge::valueAt(v1,e);

    double h = (d13 - d12*cos(ang1)) / sin(ang1);

    if(f.isNegative())
        return -h;
    else
        return h;
}

double getDual(Edge e)
{
    Face f1, f2;
    double h1 = 0;
    double h2 = 0;
    
    int numFaces = (*(e.getLocalFaces())).size();

    if (numFaces > 0) {
        f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
        h1 = getHeight(f1, e);
    }
    if (numFaces > 1) {
        f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
        h2 = getHeight(f2, e);
    }

    /*if(f1.isNegative() || f2.isNegative()) {
        return -(h1 + h2);
    }
    else {
        return h1 + h2;
    }*/
    return h1+h2;
}

/*
 *  E(f) = 1/2 sum_({i,j} in T_1) |*{i,j}|/|{i,j}| (f_j - f_i)^2
 *
 */
double dirichletEnergy() {
    double total = 0.0;
    cout << "\n";
    map<int, Edge>::iterator eit;
    //perform the {i,j} and {j,i} computation at the same time
    for (eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        double f_i = (*(eit->second).getLocalVertices())[0];
        double f_j = (*(eit->second).getLocalVertices())[1];
        cout << "\t" << f_i << " " << f_j << " " << getDual(eit->second) << " " << Length::valueAt(eit->second) << "\n";
        double subtotal = (getDual(eit->second)/Length::valueAt(eit->second));
        cout << "\t" << subtotal << "\n";
        total += subtotal * pow(f_i-f_j, 2) + subtotal * pow(f_j-f_i, 2);
    }
    total = total / 2;
return total;
}

//double getPartialEdge(Edge e, Vertex v1)
//{
//    int v2Index;
//    if((*(e.getLocalVertices()))[0] != v1.getIndex())
//    {
//       v2Index = (*(e.getLocalVertices()))[0];
//    } else
//    {
//       v2Index = (*(e.getLocalVertices()))[1];
//    }
//    Vertex v2 = Triangulation::vertexTable[v2Index];
//    double d12 = (pow(e.getLength(), 2) + pow(v1.getRadius(), 2) - pow(v2.getRadius(), 2)) / (2 * e.getLength());
////    
////    double d21 = (pow(e.getLength(), 2) + pow(v2.getRadius(), 2) - pow(v1.getRadius(), 2)) / (2 * e.getLength());
////    cout << d12 + d21 << "     " << e.getLength() << "\n";
//    return d12;
//}
