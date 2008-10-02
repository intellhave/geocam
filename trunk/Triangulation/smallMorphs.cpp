#include "smallMorphs.h"
#include "addRemove.h"

int addVertexToVertex(Vertex va, Vertex vb)
{    
     if(va.isAdjacentVertex(vb)) {
          vector<int> sameAs;
          sameAs = listIntersection(va.getLocalEdges(), vb.getLocalEdges());
          return sameAs[0];
     }
     
     
     Edge e(Triangulation::greatestEdge() + 1);
     
     Triangulation::putEdge(e.getIndex(), e);
     
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::vertexTable[va.getIndex()]);
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::edgeTable[e.getIndex()]);
     add(&Triangulation::vertexTable[va.getIndex()], 
         &Triangulation::edgeTable[e.getIndex()]);
     
     return e.getIndex();
}

int addVertexToEdge(Edge e, Vertex vb)
{
     vector<int> localV = *(e.getLocalVertices());
     vector<int> edges;
     
     for(int i = 0; i < localV.size(); i++)
     {
         edges.push_back(addVertexToVertex(Triangulation::vertexTable[localV[i]], 
                                           Triangulation::vertexTable[vb.getIndex()]));
     }
     
     Face f(Triangulation::greatestFace() + 1);
     
     Triangulation::putFace(f.getIndex(), f);
     
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::faceTable[f.getIndex()]);
     add(&Triangulation::edgeTable[e.getIndex()], 
         &Triangulation::faceTable[f.getIndex()]);
     for(int i = 0; i < localV.size(); i++)
     {
         add(&Triangulation::vertexTable[localV[i]], 
             &Triangulation::faceTable[f.getIndex()]);
     }
     for(int i = 0; i < edges.size(); i++)
     {
         add(&Triangulation::edgeTable[localV[i]], 
             &Triangulation::faceTable[f.getIndex()]);
     }
     
     vector<int> localE = *(Triangulation::vertexTable[localV[0]].getLocalEdges());
     for(int i = 0; i < localE.size(); i++)
     {
         if(localE[i] != edges[0])
         {
            add(&Triangulation::edgeTable[localE[i]], 
                &Triangulation::edgeTable[edges[0]]);
         }
     }
     localE = *(Triangulation::vertexTable[localV[1]].getLocalEdges());
     for(int i = 0; i < localE.size(); i++)
     {
         if(localE[i] != edges[1])
         {
            add(&Triangulation::edgeTable[localE[i]], 
                &Triangulation::edgeTable[edges[1]]);
         }
     }
     
     add(&Triangulation::edgeTable[edges[0]], 
         &Triangulation::edgeTable[edges[1]]);
     
     return f.getIndex();
}
