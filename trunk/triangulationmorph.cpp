#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "Triangulation.h"
#include "Triangulationmath.h"


void addNewVertex(Face f, double newWeight)
{
     //create indices for the new objects to be added to the triangulation
     //assume that there are no "gaps" left in the list
     int newVertInd = Triangulation::vertexTable.size() + 1;
     int newEdgeInd1 = Triangulation::edgeTable.size() + 1;
     int newEdgeInd2 = Triangulation::edgeTable.size() + 2;
     int newEdgeInd3 = Triangulation::edgeTable.size() + 3;
     int newFaceInd1 = Triangulation::faceTable.size() + 1;
     int newFaceInd2 = Triangulation::faceTable.size() + 2;
     
     //give names to the actual objects themselves
     Vertex v;
     Edge e1, e2, e3;
     Face f1, f2;
     
     //make lists of the indices of the existing objects local to the chosen face
     int vertexList[3];
     int edgeList[3];
     for(int i = 0; i < 3; i++)
     {
             vertexList[i] = (*(f.getLocalVertices()))[i];
             edgeList[i] = (*(f.getLocalEdges()))[i];
     }
     
     //begin making references to the new objects within the existing objects
     //using just the new indices
     Triangulation::vertexTable[vertexList[0]].addVertex(newVertInd);
     Triangulation::vertexTable[vertexList[1]].addVertex(newVertInd);
     Triangulation::vertexTable[vertexList[2]].addVertex(newVertInd);
     Triangulation::vertexTable[vertexList[0]].addEdge(newEdgeInd1);
     Triangulation::vertexTable[vertexList[1]].addEdge(newEdgeInd2);
     Triangulation::vertexTable[vertexList[2]].addEdge(newEdgeInd3);
     
     //the chosen face will remain but will be modified to become one of the new faces
     Triangulation::vertexTable[vertexList[2]].removeFace(f.getIndex());
     
     vector<int> sameAs;
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalEdges(), Triangulation::vertexTable[vertexList[1]].getLocalEdges());
     
     Triangulation::edgeTable[sameAs[0]].addEdge(newEdgeInd1);
     Triangulation::edgeTable[sameAs[0]].addEdge(newEdgeInd2);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalEdges(), Triangulation::vertexTable[vertexList[2]].getLocalEdges());
     
     Triangulation::edgeTable[sameAs[0]].addEdge(newEdgeInd1);
     Triangulation::edgeTable[sameAs[0]].addEdge(newEdgeInd3);
     Triangulation::edgeTable[sameAs[0]].removeFace(f.getIndex());
     Triangulation::edgeTable[sameAs[0]].addFace(newFaceInd1);
     
     Triangulation::faceTable[f.getIndex()].removeEdge(sameAs[0]);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[1]].getLocalEdges(), Triangulation::vertexTable[vertexList[2]].getLocalEdges());
     
     Triangulation::edgeTable[sameAs[0]].addEdge(newEdgeInd2);
     Triangulation::edgeTable[sameAs[0]].addEdge(newEdgeInd3);
     Triangulation::edgeTable[sameAs[0]].removeFace(f.getIndex());
     Triangulation::edgeTable[sameAs[0]].addFace(newFaceInd2);
     
     Triangulation::faceTable[f.getIndex()].removeEdge(sameAs[0]);
     
     Triangulation::faceTable[f.getIndex()].removeVertex(vertexList[2]);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalFaces(), Triangulation::vertexTable[vertexList[2]].getLocalFaces());
     
     Triangulation::faceTable[f.getIndex()].removeFace(sameAs[0]);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[1]].getLocalFaces(), Triangulation::vertexTable[vertexList[2]].getLocalFaces());
     
     Triangulation::faceTable[f.getIndex()].removeFace(sameAs[0]);
     Triangulation::faceTable[f.getIndex()].addEdge(newEdgeInd1);
     Triangulation::faceTable[f.getIndex()].addEdge(newEdgeInd2);
     
     Triangulation::vertexTable[vertexList[0]].addFace(newFaceInd1);
     Triangulation::vertexTable[vertexList[1]].addFace(newFaceInd2);
     Triangulation::vertexTable[vertexList[2]].addFace(newFaceInd1);
     Triangulation::vertexTable[vertexList[2]].addFace(newFaceInd2);
     
     //just finished adding paths from old objects
     //creating paths from new objects
     
     v.addVertex(vertexList[0]);
     v.addVertex(vertexList[1]);
     v.addVertex(vertexList[2]);
     v.addEdge(newEdgeInd1);
     v.addEdge(newEdgeInd2);
     v.addEdge(newEdgeInd3);
     v.addFace(f.getIndex());
     v.addFace(newFaceInd1);
     v.addFace(newFaceInd2);
     
     e1.addVertex(vertexList[0]);
     e1.addVertex(newVertInd);
     for(int i = 0; i < Triangulation::vertexTable[vertexList[0]].getLocalEdges()->size(); i++)
     {
             e1.addEdge((*(Triangulation::vertexTable[vertexList[0]].getLocalEdges()))[i]);
     }
     e1.addEdge(newEdgeInd2);
     e1.addEdge(newEdgeInd3);
     e1.addFace(f.getIndex());
     e1.addEdge(newFaceInd1);
     
     e2.addVertex(vertexList[1]);
     e2.addVertex(newVertInd);
     for(int i = 0; i < Triangulation::vertexTable[vertexList[1]].getLocalEdges()->size(); i++)
     {
             e2.addEdge((*(Triangulation::vertexTable[vertexList[1]].getLocalEdges()))[i]);
     }
     e2.addEdge(newEdgeInd1);
     e2.addEdge(newEdgeInd3);
     e2.addFace(f.getIndex());
     e2.addEdge(newFaceInd2);
     
     e3.addVertex(vertexList[2]);
     e3.addVertex(newVertInd);
     for(int i = 0; i < Triangulation::vertexTable[vertexList[2]].getLocalEdges()->size(); i++)
     {
             e3.addEdge((*(Triangulation::vertexTable[vertexList[2]].getLocalEdges()))[i]);
     }
     e3.addEdge(newEdgeInd1);
     e3.addEdge(newEdgeInd2);
     e3.addFace(newFaceInd2);
     e3.addEdge(newFaceInd2);
     
     f1.addVertex(newVertInd);
     f1.addVertex(vertexList[0]);
     f1.addVertex(vertexList[2]);
     f1.addEdge(newEdgeInd1);
     f1.addEdge(newEdgeInd3);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalEdges(), Triangulation::vertexTable[vertexList[2]].getLocalEdges());
     f1.addEdge(sameAs[0]);
     
     f1.addFace(f.getIndex());
     f1.addFace(newFaceInd2);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalFaces(), Triangulation::vertexTable[vertexList[2]].getLocalFaces());
     f1.addFace(sameAs[0]);
     
     
     f2.addVertex(newVertInd);
     f2.addVertex(vertexList[1]);
     f2.addVertex(vertexList[2]);
     f2.addEdge(newEdgeInd2);
     f2.addEdge(newEdgeInd3);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[1]].getLocalEdges(), Triangulation::vertexTable[vertexList[2]].getLocalEdges());
     f2.addEdge(sameAs[0]);
     
     f2.addFace(f.getIndex());
     f2.addFace(newFaceInd1);
     
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[1]].getLocalFaces(), Triangulation::vertexTable[vertexList[2]].getLocalFaces());
     f2.addFace(sameAs[0]);
     
     Triangulation::putVertex(newVertInd, v);
     Triangulation::putEdge(newEdgeInd1, e1);
     Triangulation::putEdge(newEdgeInd2, e2);
     Triangulation::putEdge(newEdgeInd3, e3);
     Triangulation::putFace(newFaceInd1, f1);
     Triangulation::putFace(newFaceInd2, f2);
     
}
