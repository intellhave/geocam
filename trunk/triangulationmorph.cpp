#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "Triangulation.h"
#include "Triangulationmath.h"
#include <cstdlib>
#include <iostream>

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
     if(faceList[1] != faceList[0])
     Triangulation::faceTable[f.getIndex()].removeFace(faceList[1]);
     if(faceList[2] != faceList[0])
     Triangulation::faceTable[f.getIndex()].removeFace(faceList[2]);
     Triangulation::faceTable[f.getIndex()].addEdge(newEdgeInd1);
     Triangulation::faceTable[f.getIndex()].addEdge(newEdgeInd2);
     Triangulation::faceTable[f.getIndex()].addVertex(newVertInd);
     Triangulation::faceTable[f.getIndex()].addFace(newFaceInd1);
     Triangulation::faceTable[f.getIndex()].addFace(newFaceInd2);
     
     //then deal with the other existing local faces
     if(faceList[1] != faceList[0])
     Triangulation::faceTable[faceList[1]].removeFace(f.getIndex());
     Triangulation::faceTable[faceList[1]].addFace(newFaceInd1);
     if(faceList[2] != faceList[0])
     Triangulation::faceTable[faceList[2]].removeFace(f.getIndex());
     Triangulation::faceTable[faceList[2]].addFace(newFaceInd2);
     
     
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

void flip(Edge e)
{
     
     //start out by naming every object that is local to the flip
     Face f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     Face f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     
     vector<int> sameAs;
     vector<int> diff;
     
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
          
     diff = listDifference(f1.getLocalVertices(), f2.getLocalVertices());
     Vertex vb1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(f2.getLocalVertices(), f1.getLocalVertices());
     Vertex vb2 = Triangulation::vertexTable[diff[0]];
     
     sameAs = listIntersection(va1.getLocalEdges(), vb1.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb1.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), vb2.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     
     sameAs = listIntersection(f1.getLocalFaces(), ea1.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(f1.getLocalFaces(), eb1.getLocalFaces());
     Face fb1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(f2.getLocalFaces(), ea2.getLocalFaces());
     Face fa2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(f2.getLocalFaces(), eb2.getLocalFaces());
     Face fb2 = Triangulation::faceTable[sameAs[0]];
     
     //the next task is naturally to rearrange all the necessary references
     //there are no new references added, so the task  is simply made up
     //of working with the objects that already exist that have newly
     //created references made for this method above
     //the procedure follows a very methodical and categorical structure
     //done first on paper and then transferred to code
     Triangulation::vertexTable[(va1.getIndex())].removeVertex(va2.getIndex()); 
     Triangulation::vertexTable[(va2.getIndex())].removeVertex(va1.getIndex()); 
     Triangulation::vertexTable[(va1.getIndex())].removeEdge(e.getIndex()); 
     Triangulation::vertexTable[(va2.getIndex())].removeEdge(e.getIndex()); 
     Triangulation::vertexTable[(va1.getIndex())].removeFace(f2.getIndex()); 
     Triangulation::vertexTable[(va2.getIndex())].removeFace(f1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].removeVertex(va1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].removeVertex(va2.getIndex()); 
     for(int i = 0; i < e.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(e.getIndex())].removeEdge((*(e.getLocalEdges()))[i]); 
     }
     for(int i = 0; i < va1.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].removeEdge(e.getIndex()); 
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].removeEdge(e.getIndex());  
     }
     Triangulation::edgeTable[(eb1.getIndex())].removeFace(f1.getIndex()); 
     Triangulation::edgeTable[(ea2.getIndex())].removeFace(f2.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].removeVertex(va2.getIndex()); 
     Triangulation::faceTable[(f2.getIndex())].removeVertex(va1.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].removeEdge(eb1.getIndex()); 
     Triangulation::faceTable[(f2.getIndex())].removeEdge(ea2.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].removeFace(fb1.getIndex()); 
     Triangulation::faceTable[(fb1.getIndex())].removeFace(f1.getIndex()); 
     Triangulation::faceTable[(f2.getIndex())].removeFace(fa2.getIndex()); 
     Triangulation::faceTable[(fa2.getIndex())].removeFace(f2.getIndex()); 
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb2.getIndex()); 
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb1.getIndex()); 
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(e.getIndex()); 
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(e.getIndex()); 
     Triangulation::vertexTable[(vb1.getIndex())].addFace(f2.getIndex()); 
     Triangulation::vertexTable[(vb2.getIndex())].addFace(f1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].addVertex(vb1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].addVertex(vb2.getIndex()); 
     for(int i = 0; i < vb1.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(e.getIndex())].addEdge((*(vb1.getLocalEdges()))[i]); 
             Triangulation::edgeTable[(*(vb1.getLocalEdges()))[i]].addEdge(e.getIndex()); 
     }
     for(int i = 0; i < vb2.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(e.getIndex())].addEdge((*(vb2.getLocalEdges()))[i]); 
             Triangulation::edgeTable[(*(vb2.getLocalEdges()))[i]].addEdge(e.getIndex()); 
     }
     Triangulation::edgeTable[(ea2.getIndex())].addFace(f1.getIndex()); 
     Triangulation::edgeTable[(eb1.getIndex())].addFace(f2.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(f2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(f1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(f2.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(f1.getIndex())].addFace(fa2.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addFace(f1.getIndex());
     Triangulation::faceTable[(f2.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(f2.getIndex());
     
}







