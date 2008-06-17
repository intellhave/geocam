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
     vector<int> sameAs;
     
     //make lists of the indices of the existing objects local to the chosen face
     int vertexList[3];
     int edgeList[3];
     int faceList[3];
     
     //assign arbitrary indices to the vertices
     for(int i = 0; i < 3; i++)
     {
             vertexList[i] = (*(f.getLocalVertices()))[i];
     }
     
     //assign indices to edges and faces based on the vertex indices
     //creates a structure that is used throughout
     //                       
     //                 [1]
     //  (0)*------------------------------*(2)
     //     \ \                           / /
     //      \  \       <1>             /  /
     //       \   \                  /    /
     //        \     \            /      /
     //     [0] \  <f> \        /  <2>  / [2]
     //          \       \    /        /
     //           \       \*/        /
     //            \       |       /    
     //              \     |     /
     //                \   |   /
     //                  \ | /
     //                    * (1)
     //
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalEdges(), Triangulation::vertexTable[vertexList[1]].getLocalEdges());
     edgeList[0] = sameAs[sameAs.size() - 1];
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalEdges(), Triangulation::vertexTable[vertexList[2]].getLocalEdges());
     edgeList[1] = sameAs[sameAs.size() - 1];
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[1]].getLocalEdges(), Triangulation::vertexTable[vertexList[2]].getLocalEdges());
     edgeList[2] = sameAs[sameAs.size() - 1];
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalFaces(), Triangulation::vertexTable[vertexList[1]].getLocalFaces());
     faceList[0] = sameAs[sameAs.size() - 1];
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[0]].getLocalFaces(), Triangulation::vertexTable[vertexList[2]].getLocalFaces());
     faceList[1] = sameAs[sameAs.size() - 1];
     sameAs = listIntersection(Triangulation::vertexTable[vertexList[1]].getLocalFaces(), Triangulation::vertexTable[vertexList[2]].getLocalFaces());
     faceList[2] = sameAs[sameAs.size() - 1];
     
     //begin making references to the new objects within the existing objects using the new indices
     //start with existing vertices
     Triangulation::vertexTable[vertexList[0]].addVertex(newVertInd);
     Triangulation::vertexTable[vertexList[1]].addVertex(newVertInd);
     Triangulation::vertexTable[vertexList[2]].addVertex(newVertInd);
     Triangulation::vertexTable[vertexList[0]].addEdge(newEdgeInd1);
     Triangulation::vertexTable[vertexList[1]].addEdge(newEdgeInd2);
     Triangulation::vertexTable[vertexList[2]].addEdge(newEdgeInd3);
     Triangulation::vertexTable[vertexList[0]].addFace(newFaceInd1);
     Triangulation::vertexTable[vertexList[1]].addFace(newFaceInd2);
     Triangulation::vertexTable[vertexList[2]].addFace(newFaceInd1);
     Triangulation::vertexTable[vertexList[2]].addFace(newFaceInd2);
     //the chosen face will remain but will be modified to become one of the new faces
     Triangulation::vertexTable[vertexList[2]].removeFace(f.getIndex());
     
     //then edges
     Triangulation::edgeTable[edgeList[0]].addEdge(newEdgeInd1);
     Triangulation::edgeTable[edgeList[0]].addEdge(newEdgeInd2);
     Triangulation::edgeTable[edgeList[1]].addEdge(newEdgeInd1);
     Triangulation::edgeTable[edgeList[1]].addEdge(newEdgeInd3);
     Triangulation::edgeTable[edgeList[1]].removeFace(f.getIndex());
     Triangulation::edgeTable[edgeList[1]].addFace(newFaceInd1);
     Triangulation::edgeTable[edgeList[2]].addEdge(newEdgeInd2);
     Triangulation::edgeTable[edgeList[2]].addEdge(newEdgeInd3);
     Triangulation::edgeTable[edgeList[2]].removeFace(f.getIndex());
     Triangulation::edgeTable[edgeList[2]].addFace(newFaceInd2);
     
     //then faces
     //first deal with the face being modified
     Triangulation::faceTable[f.getIndex()].removeVertex(vertexList[2]);
     Triangulation::faceTable[f.getIndex()].removeEdge(edgeList[1]);
     Triangulation::faceTable[f.getIndex()].removeEdge(edgeList[2]);
     Triangulation::faceTable[f.getIndex()].removeFace(faceList[1]);
     Triangulation::faceTable[f.getIndex()].removeFace(faceList[2]);
     Triangulation::faceTable[f.getIndex()].addEdge(newEdgeInd1);
     Triangulation::faceTable[f.getIndex()].addEdge(newEdgeInd2);
     Triangulation::faceTable[f.getIndex()].addVertex(newVertInd);
     Triangulation::faceTable[f.getIndex()].addFace(newFaceInd1);
     Triangulation::faceTable[f.getIndex()].addFace(newFaceInd2);
     
     //now that references from existing objects have been repaired
     //begin creating references from new objects
     //start with the new vertex
     v.addVertex(vertexList[0]);
     v.addVertex(vertexList[1]);
     v.addVertex(vertexList[2]);
     v.addEdge(newEdgeInd1);
     v.addEdge(newEdgeInd2);
     v.addEdge(newEdgeInd3);
     v.addFace(f.getIndex());
     v.addFace(newFaceInd1);
     v.addFace(newFaceInd2);
     
     //then the new edges
     e1.addVertex(vertexList[0]);
     e1.addVertex(newVertInd);
     for(int i = 0; i < Triangulation::vertexTable[vertexList[0]].getLocalEdges()->size(); i++)
     {
             e1.addEdge((*(Triangulation::vertexTable[vertexList[0]].getLocalEdges()))[i]);
     }
     e1.addEdge(newEdgeInd2);
     e1.addEdge(newEdgeInd3);
     e1.removeEdge(newEdgeInd1);
     e1.addFace(f.getIndex());
     e1.addFace(newFaceInd1);
     
     e2.addVertex(vertexList[1]);
     e2.addVertex(newVertInd);
     for(int i = 0; i < Triangulation::vertexTable[vertexList[1]].getLocalEdges()->size(); i++)
     {
             e2.addEdge((*(Triangulation::vertexTable[vertexList[1]].getLocalEdges()))[i]);
     }
     e2.addEdge(newEdgeInd1);
     e2.addEdge(newEdgeInd3);
     e2.removeEdge(newEdgeInd2);
     e2.addFace(f.getIndex());
     e2.addFace(newFaceInd2);
     
     e3.addVertex(vertexList[2]);
     e3.addVertex(newVertInd);
     for(int i = 0; i < Triangulation::vertexTable[vertexList[2]].getLocalEdges()->size(); i++)
     {
             e3.addEdge((*(Triangulation::vertexTable[vertexList[2]].getLocalEdges()))[i]);
     }
     e3.addEdge(newEdgeInd1);
     e3.addEdge(newEdgeInd2);
     e3.removeEdge(newEdgeInd3);
     e3.addFace(newFaceInd1);
     e3.addFace(newFaceInd2);
     
     //finally the new faces
     f1.addVertex(newVertInd);
     f1.addVertex(vertexList[0]);
     f1.addVertex(vertexList[2]);
     f1.addEdge(newEdgeInd1);
     f1.addEdge(newEdgeInd3);   
     f1.addEdge(edgeList[1]);
     f1.addFace(f.getIndex());
     f1.addFace(newFaceInd2);
     f1.addFace(faceList[1]);
     
     f2.addVertex(newVertInd);
     f2.addVertex(vertexList[1]);
     f2.addVertex(vertexList[2]);
     f2.addEdge(newEdgeInd2);
     f2.addEdge(newEdgeInd3);
     f2.addEdge(edgeList[2]);
     f2.addFace(f.getIndex());
     f2.addFace(newFaceInd1);
     f2.addFace(faceList[2]);
     
     //after all the references have been sorted, the new objects must be placed within the triangulation
     Triangulation::putVertex(newVertInd, v);
     Triangulation::putEdge(newEdgeInd1, e1);
     Triangulation::putEdge(newEdgeInd2, e2);
     Triangulation::putEdge(newEdgeInd3, e3);
     Triangulation::putFace(newFaceInd1, f1);
     Triangulation::putFace(newFaceInd2, f2);
     
     //the new vertex accepts its new weight from the argument
     v.setWeight(newWeight);
     
}
