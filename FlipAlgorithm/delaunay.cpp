#include "delaunay.h"
#include <iostream>
#include <set>
#include <fstream>
#include <sstream>
#include <iomanip>
#include "partial_edge.h"
#include "euc_angle.h"
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

bool isWeightedDelaunay(void) {
    map<int, Edge>::iterator eit;
    for (eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        if (false == isWeightedDelaunay(eit->second)) {
            return false;
        }
    }
    return true;
}

bool isConvexHinge(Edge e) {
    if (e.isBorder()) {
        true;
    }
    int f0 = (*(e.getLocalFaces()))[0];
    int f1 = (*(e.getLocalFaces()))[1];
    int v0 = (*(e.getLocalVertices()))[0];
    int v1 = (*(e.getLocalVertices()))[1];
    
    double ang0 = EuclideanAngle::valueAt(Triangulation::vertexTable[v0], Triangulation::faceTable[f0]);
    ang0 += EuclideanAngle::valueAt(Triangulation::vertexTable[v0], Triangulation::faceTable[f1]);
    
    double ang1 = EuclideanAngle::valueAt(Triangulation::vertexTable[v1], Triangulation::faceTable[f0]);
    ang1 += EuclideanAngle::valueAt(Triangulation::vertexTable[v1], Triangulation::faceTable[f1]);
    
    if (ang0 > PI || ang1 > PI) {
        return false;
    } else {
        true;
    }
}

bool facesAreTheSame(Edge e) {
    if (e.isBorder()) {
        false;
    }
    int f0 = (*(e.getLocalFaces()))[0];
    int f1 = (*(e.getLocalFaces()))[1];
    return Triangulation::faceTable[f0].isNegative() == Triangulation::faceTable[f1].isNegative();
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

    return h1+h2;
}

/*
 *  E(f) = 1/2 sum_({i,j} in T_1) |*{i,j}|/|{i,j}| (f_j - f_i)^2
 *
 */
double dirichletEnergy(Function vertexF) {

    double total = 0.0;
    //cout << "\n";
    map<int, Edge>::iterator eit;
    //Face face0 = Triangulation::faceTable[0];
    //Face face1 = Triangulation::faceTable[1];

    for (eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        double f_i = vertexF.valueOf((*(eit->second).getLocalVertices())[0]);
        double f_j = vertexF.valueOf((*(eit->second).getLocalVertices())[1]);
        //begin special example stuff
        /*int f0 = (*((eit->second).getLocalFaces()))[0];
        int f1 = (*((eit->second).getLocalFaces()))[1];
        if (f0 != face0.getIndex()) {
            int temp = f0;
            f0 = f1;
            f1 = temp;
        }
        double a = 0;
        double b = 0;
        if ( (*((eit->second).getLocalFaces())).size() >= 2 ) {
            a = getHeight(face0, eit->second);
            b = getHeight(face1, eit->second);
        } else if ( (*((eit->second).getLocalFaces()))[0] == f0 ) {
            a = getHeight(face0, eit->second);
        } else {
            b = getHeight(face1, eit->second);
        }
        printf("  {%d, %d}:   f_i  ,   f_j  ,   |*{i,j}|  ,  face0   ,  face1  ,   |{i,j}|\n        %9lf, %9lf, %9lf, %9lf, %9lf, %9lf\n",(*((eit->second).getLocalVertices()))[0], (*((eit->second).getLocalVertices()))[1], f_i, f_j, getDual(eit->second), a, b, Length::valueAt(eit->second) );
        *///end special example stuff
        //printf("    %lf\n", subtotal);
        total += (getDual(eit->second)/Length::valueAt(eit->second)) * pow(f_j-f_i, 2);
    }
    total = total / 2;
return total;
}
