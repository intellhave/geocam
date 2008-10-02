#include "smallMorphs.h"
#include "addRemove.h"

int addVertexToVertex(Vertex v)
{
     Vertex vb(Triangulation::greatestVertex() + 1);
     Edge e(Triangulation::greatestEdge() + 1);
     
     Triangulation::putVertex(vb.getIndex(), vb);
     Triangulation::putEdge(e.getIndex(), e);
     
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::vertexTable[v.getIndex()]);
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::edgeTable[e.getIndex()]);
     add(&Triangulation::vertexTable[v.getIndex()], 
         &Triangulation::edgeTable[e.getIndex()]);
     
     return e.getIndex();
}

int addVertexToEdge(Edge e)
{
     Vertex vb(Triangulation::greatestVertex() + 1);
     vector<int> localV = *(e.getLocalVertices());
     vector<int> edges;
     
     for(int i = 0; i < localV.size(); i++)
     {
         
     }
}
