#include "smallMorphs.h"
#include "addRemove.h"

Edge addVertexToVertex(Vertex v)
{
     Vertex vb(Triangulation::greatestVertex() + 1);
     Edge e(Triangulation::greatestEdge() + 1);
     
     Triangulation::putVertex(vb.getIndex(), vb);
     Triangulation::putEdge(e.getIndex(), e);
     
     add(&Triangulation::vertexTable[vb.getIndex()], &Triangulation::vertexTable[v.getIndex()]);
     add(&Triangulation::vertexTable[vb.getIndex()], &Triangulation::edgeTable[e.getIndex()]);
     add(&Triangulation::vertexTable[v.getIndex()], &Triangulation::edgeTable[e.getIndex()]);
     
     return e;
}
