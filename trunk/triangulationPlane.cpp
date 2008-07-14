#include <cstdlib>
#include <iostream>
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "triangulation.h"
#include "triangulationmath.h"
#include "triangulationPlane.h"
#include <fstream>
#include <iomanip>
#define PI 	3.141592653589793238

void firstTriangle(double length1, double length2, double length3)
{
     if(length1 > length2 + length3)
     throw string("Invalid Edge Lengths");
     if(length2 > length1 + length3)
     throw string("Invalid Edge Lengths");
     if(length3 > length1 + length2)
     throw string("Invalid Edge Lengths");
     
     Face f1;
    Edge e1, e2, e3;
    Vertex v1, v2, v3;
    Triangulation::putFace(1, f1);
    Triangulation::putEdge(1, e1);
    Triangulation::putEdge(2, e2);
    Triangulation::putEdge(3, e3);
    Triangulation::putVertex(1, v1);
    Triangulation::putVertex(2, v2);
    Triangulation::putVertex(3, v3);
    Triangulation::vertexTable[1].addVertex(2);
    Triangulation::vertexTable[1].addVertex(3);
    Triangulation::vertexTable[2].addVertex(1);
    Triangulation::vertexTable[2].addVertex(3);
    Triangulation::vertexTable[3].addVertex(1);
    Triangulation::vertexTable[3].addVertex(2);
    Triangulation::vertexTable[1].addEdge(1);
    Triangulation::vertexTable[1].addEdge(2);
    Triangulation::vertexTable[2].addEdge(1);
    Triangulation::vertexTable[2].addEdge(3);
    Triangulation::vertexTable[3].addEdge(2);
    Triangulation::vertexTable[3].addEdge(3);
    Triangulation::vertexTable[1].addFace(1);
    Triangulation::vertexTable[2].addFace(1);
    Triangulation::vertexTable[3].addFace(1);
    Triangulation::edgeTable[1].addVertex(1);
    Triangulation::edgeTable[1].addVertex(2);
    Triangulation::edgeTable[2].addVertex(1);
    Triangulation::edgeTable[2].addVertex(3);
    Triangulation::edgeTable[3].addVertex(2);
    Triangulation::edgeTable[3].addVertex(3);
    Triangulation::edgeTable[1].addEdge(2);
    Triangulation::edgeTable[1].addEdge(3);
    Triangulation::edgeTable[2].addEdge(1);
    Triangulation::edgeTable[2].addEdge(3);
    Triangulation::edgeTable[3].addEdge(1);
    Triangulation::edgeTable[3].addEdge(2);
    Triangulation::edgeTable[1].addFace(1);
    Triangulation::edgeTable[1].addFace(0);
    Triangulation::edgeTable[2].addFace(1);
    Triangulation::edgeTable[2].addFace(0);
    Triangulation::edgeTable[3].addFace(1);
    Triangulation::edgeTable[3].addFace(0);
    Triangulation::faceTable[1].addVertex(1);
    Triangulation::faceTable[1].addVertex(2);
    Triangulation::faceTable[1].addVertex(3);
    Triangulation::faceTable[1].addEdge(1);
    Triangulation::faceTable[1].addEdge(2);
    Triangulation::faceTable[1].addEdge(3);
    Triangulation::faceTable[1].addFace(0);
    
    Triangulation::edgeTable[1].setLength(length1);
    Triangulation::edgeTable[2].setLength(length2);
    Triangulation::edgeTable[3].setLength(length3);
    
    Triangulation::vertexTable[1].setPosition(0.0, 0.0);
    Triangulation::vertexTable[2].setPosition(length1, 0.0);
    double angC = angle(length1, length2, length3);
    Triangulation::vertexTable[3].setPosition((length2 * cos(angC)), (length2 * sin(angC)));
    
}

void addTriangle(Edge e, double length1, double length2)
{
     
     if(!e.isAdjFace(0))
     throw string("Invalid Edge");
     if(e.getLength() > length1 + length2)
     throw string("Invalid Edge Lengths");
     if(length1 > e.getLength() + length2)
     throw string("Invalid Edge Lengths");
     if(length2 > e.getLength() + length1)
     throw string("Invalid Edge Lengths");
     
     Vertex vb(Triangulation::greatestVertex() + 1);
     Edge eb1(Triangulation::greatestEdge() + 1);
     Edge eb2(Triangulation::greatestEdge() + 2);
     Face fb(Triangulation::greatestFace() + 1);
     
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     Face fa;
     if((*(e.getLocalFaces()))[0] == 0)
          Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     else
          Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     
     Triangulation::vertexTable[vb.getIndex()].addVertex(va1.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addVertex(va2.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addVertex(vb.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addVertex(vb.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addEdge(eb1.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addEdge(eb2.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addEdge(eb1.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addEdge(eb2.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addVertex(vb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addVertex(va1.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addVertex(vb.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addVertex(va2.getIndex());
     for(int i = 0; i < e.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[eb1.getIndex()].addEdge((*(e.getLocalEdges()))[i]);
             Triangulation::edgeTable[eb2.getIndex()].addEdge((*(e.getLocalEdges()))[i]);
     }
     Triangulation::edgeTable[e.getIndex()].addEdge(eb1.getIndex());
     Triangulation::edgeTable[e.getIndex()].addEdge(eb2.getIndex());
     Triangulation::edgeTable[e.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(vb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(0);
     Triangulation::faceTable[fa.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fa.getIndex()].removeFace(0);
     
     Triangulation::edgeTable[eb1.getIndex()].setLength(length1);
     Triangulation::edgeTable[eb2.getIndex()].setLength(length2);
     
     
}

void addTriangle(Edge e1, Edge e2)
{
     if(!e1.isAdjEdge(e2.getIndex()))
     throw string("");
     if(!e2.isAdjEdge(e1.getIndex()))
     throw string("");
     if(!e1.isAdjFace(0) || !e2.isAdjFace(0))
     throw string("");
     
     Vertex v1, v2, va;
     vector<int> diff, sameAs;
     
     diff = listDifference(e1.getLocalVertices(), e2.getLocalVertices());
     v1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(e2.getLocalVertices(), e1.getLocalVertices());
     v2 = Triangulation::vertexTable[diff[0]];
     sameAs = listIntersection(e1.getLocalVertices(), e2.getLocalVertices());
     va = Triangulation::vertexTable[sameAs[0]];
     
     Edge eb(Triangulation::greatestEdge() + 1);
     Face fb(Triangulation::greatestFace() + 1);
     Face fa1, fa2;
     if((*(e1.getLocalFaces()))[0] == 0)
          Face fa1 = Triangulation::faceTable[(*(e1.getLocalFaces()))[1]];
     else
          Face fa1 = Triangulation::faceTable[(*(e1.getLocalFaces()))[0]];
     if((*(e2.getLocalFaces()))[0] == 0)
          Face fa2 = Triangulation::faceTable[(*(e2.getLocalFaces()))[1]];
     else
          Face fa2 = Triangulation::faceTable[(*(e2.getLocalFaces()))[0]];
     
     if(getAngleSum(va) < PI)
     throw string("");
     
     Triangulation::edgeTable[e1.getIndex()].removeFace(0);
     Triangulation::edgeTable[e2.getIndex()].removeFace(0);
     Triangulation::faceTable[fa1.getIndex()].removeFace(0);
     Triangulation::faceTable[fa2.getIndex()].removeFace(0);
     Triangulation::vertexTable[v1.getIndex()].addVertex(v2.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addVertex(v1.getIndex());
     Triangulation::vertexTable[v1.getIndex()].addEdge(eb.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addEdge(eb.getIndex());
     Triangulation::vertexTable[v1.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addVertex(v1.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addVertex(v2.getIndex());
     for(int i = 0; i < v1.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(v1.getLocalEdges()))[i]].addEdge(eb.getIndex());
             Triangulation::edgeTable[eb.getIndex()].addEdge((*(v1.getLocalEdges()))[i]);
     }
     for(int i = 0; i < v2.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(v2.getLocalEdges()))[i]].addEdge(eb.getIndex());
             Triangulation::edgeTable[eb.getIndex()].addEdge((*(v2.getLocalEdges()))[i]);
     }
     Triangulation::edgeTable[e1.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[e2.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addFace(0);
     Triangulation::faceTable[fb.getIndex()].addVertex(v1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(v2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(0);
     Triangulation::faceTable[fa1.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fa2.getIndex()].addFace(fb.getIndex());
     
     eb.setLength(getDistance(v1, v2));
     
}

double getDistance(Vertex v1, Vertex v2)
{
       return sqrt(pow(((v1.getXpos()) - (v2.getXpos())), 2) + pow(((v1.getYpos()) - (v2.getYpos())), 2));
}










