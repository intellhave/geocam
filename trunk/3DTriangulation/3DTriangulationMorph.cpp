#include "3DTriangulationMorph.h"
#include <iostream>

void oneFourMove(Tetra t)
{
     Face f1 = Triangulation::faceTable[(*(t.getLocalFaces()))[0]];
     Face f2 = Triangulation::faceTable[(*(t.getLocalFaces()))[1]];
     Face f3 = Triangulation::faceTable[(*(t.getLocalFaces()))[2]];
     Face f4 = Triangulation::faceTable[(*(t.getLocalFaces()))[3]];
     Vertex v(Triangulation::greatestVertex() + 1);
     Triangulation::putVertex(v.getIndex(), v);
     
     Tetra t1 = Triangulation::tetraTable[addVertexToFace(v.getIndex(), f1.getIndex())];
     Tetra t2 = Triangulation::tetraTable[addVertexToFace(v.getIndex(), f2.getIndex())];
     Tetra t3 = Triangulation::tetraTable[addVertexToFace(v.getIndex(), f3.getIndex())];
     Tetra t4 = Triangulation::tetraTable[addVertexToFace(v.getIndex(), f4.getIndex())];
     
     Triangulation::eraseTetra(t.getIndex());
     add(&Triangulation::tetraTable[t1.getIndex()], &Triangulation::tetraTable[t2.getIndex()]);
     add(&Triangulation::tetraTable[t1.getIndex()], &Triangulation::tetraTable[t3.getIndex()]);
     add(&Triangulation::tetraTable[t1.getIndex()], &Triangulation::tetraTable[t4.getIndex()]);
     add(&Triangulation::tetraTable[t2.getIndex()], &Triangulation::tetraTable[t3.getIndex()]);
     add(&Triangulation::tetraTable[t2.getIndex()], &Triangulation::tetraTable[t4.getIndex()]);
     add(&Triangulation::tetraTable[t3.getIndex()], &Triangulation::tetraTable[t4.getIndex()]);
     
}

void twoThreeMove(Face f)
{
     Edge e1 = Triangulation::edgeTable[(*(f.getLocalEdges()))[0]];
     Edge e2 = Triangulation::edgeTable[(*(f.getLocalEdges()))[1]];
     Edge e3 = Triangulation::edgeTable[(*(f.getLocalEdges()))[2]];
     Tetra t1 = Triangulation::tetraTable[(*(f.getLocalTetras()))[0]];
     Tetra t2 = Triangulation::tetraTable[(*(f.getLocalTetras()))[1]];
     
     vector<int> diff;
     diff = listDifference(t1.getLocalVertices(), f.getLocalVertices());
     Vertex va1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(t2.getLocalVertices(), f.getLocalVertices());
     Vertex va2 = Triangulation::vertexTable[diff[0]];
     
     Triangulation::eraseTetra(t1.getIndex());
     Triangulation::eraseTetra(t2.getIndex());
     Triangulation::eraseFace(f.getIndex());
     
     Edge eb = Triangulation::edgeTable[addVertexToVertex(va1.getIndex(), va2.getIndex())];
     addEdgeToEdge(eb.getIndex(), e1.getIndex());
     addEdgeToEdge(eb.getIndex(), e2.getIndex());
     addEdgeToEdge(eb.getIndex(), e3.getIndex());
}

void threeTwoMove(Edge e)
{
     Vertex v1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex v2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     Face f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     Face f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     Face f3 = Triangulation::faceTable[(*(e.getLocalFaces()))[2]];
     Tetra t1 = Triangulation::tetraTable[(*(e.getLocalTetras()))[0]];
     Tetra t2 = Triangulation::tetraTable[(*(e.getLocalTetras()))[1]];
     Tetra t3 = Triangulation::tetraTable[(*(e.getLocalTetras()))[2]];
     
     vector<int> diff;
     diff = listDifference(f1.getLocalVertices(), e.getLocalVertices());
     Vertex va1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(f2.getLocalVertices(), e.getLocalVertices());
     Vertex va2 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(f3.getLocalVertices(), e.getLocalVertices());
     Vertex va3 = Triangulation::vertexTable[diff[0]];

     Triangulation::eraseFace(f1.getIndex());
     Triangulation::eraseFace(f2.getIndex());
     Triangulation::eraseFace(f3.getIndex());
     Triangulation::eraseTetra(t1.getIndex());
     Triangulation::eraseTetra(t2.getIndex());
     Triangulation::eraseTetra(t3.getIndex());
     Triangulation::eraseEdge(e.getIndex());

     Face fb = Triangulation::faceTable[makeFace(va1.getIndex(), va2.getIndex(), va3.getIndex())];
     addVertexToFace(v1.getIndex(), fb.getIndex());
     addVertexToFace(v2.getIndex(), fb.getIndex());     
}

void fourOneMove(Vertex v)
{
     Vertex v1 = Triangulation::vertexTable[(*(v.getLocalVertices()))[0]];
     Vertex v2 = Triangulation::vertexTable[(*(v.getLocalVertices()))[1]];
     Vertex v3 = Triangulation::vertexTable[(*(v.getLocalVertices()))[2]];
     Vertex v4 = Triangulation::vertexTable[(*(v.getLocalVertices()))[3]];
     
     for(int i = 0; i < v.getLocalEdges()->size(); i++) {
             Triangulation::eraseEdge((*(v.getLocalEdges()))[i]);
     }
     for(int i = 0; i < v.getLocalFaces()->size(); i++) {
             Triangulation::eraseFace((*(v.getLocalFaces()))[i]);
     }
     for(int i = 0; i < v.getLocalTetras()->size(); i++) {
             Triangulation::eraseTetra((*(v.getLocalTetras()))[i]);
     }
     Triangulation::eraseVertex(v.getIndex());
     
     makeTetra(v1.getIndex(), v2.getIndex(), v3.getIndex(), v4.getIndex());
}
