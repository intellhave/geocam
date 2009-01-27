/**************************************************************
File: Small Morphs
Author: Alex Henniges, Tom Williams
Version: October 3, 2008
***************************************************************
The Small Morphs file holds the functions that perform isolated 
morphs on simplices. These functions are then used in morphs on
a whole Triangulation.
**************************************************************/
#include <iostream>
#include "smallMorphs.h"
#include "addRemove.h"

int addVertexToVertex(int vaInd, int vbInd)
{    

     Vertex va = Triangulation::vertexTable[vaInd];
     Vertex vb = Triangulation::vertexTable[vbInd];
     
     // Check if edge has already been created by checking
     // if va and vb are already adjacent.
     if(va.isAdjVertex(vb.getIndex())) {
          vector<int> sameAs;
          sameAs = listIntersection(va.getLocalEdges(), vb.getLocalEdges());
          if(sameAs.size() > 0)
             return sameAs[0];
     }
     
     vector<int> vaEdges = *(va.getLocalEdges());
     vector<int> vbEdges = *(vb.getLocalEdges());
     
     // Create Edge
     Edge e(Triangulation::greatestEdge() + 1);
     // Place Edge in Triangulation
     Triangulation::putEdge(e.getIndex(), e);
     
     // Add references.
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::vertexTable[va.getIndex()]);
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::edgeTable[e.getIndex()]);
     add(&Triangulation::vertexTable[va.getIndex()], 
         &Triangulation::edgeTable[e.getIndex()]);
         
     for(int i = 0; i < va.getLocalEdges()->size(); i++) 
             add(&Triangulation::edgeTable[(*(va.getLocalEdges()))[i]],
                 &Triangulation::edgeTable[e.getIndex()]);
     for(int i = 0; i < vb.getLocalEdges()->size(); i++) 
             add(&Triangulation::edgeTable[(*(vb.getLocalEdges()))[i]],
                 &Triangulation::edgeTable[e.getIndex()]);
     
     // Add existing edges to the new edge    
     for(int i = 0; i < vaEdges.size(); i++) {
             add(&Triangulation::edgeTable[e.getIndex()], &Triangulation::edgeTable[vaEdges[i]]);
     }        
     for(int i = 0; i < vbEdges.size(); i++) {
             add(&Triangulation::edgeTable[e.getIndex()], &Triangulation::edgeTable[vbEdges[i]]);
     }        
     // Return e's index.
     return e.getIndex();
}

int addVertexToEdge(int vbInd, int eInd)
{
    
    Vertex vb = Triangulation::vertexTable[vbInd];
    Edge e = Triangulation::edgeTable[eInd];
    
     // Check if face has already been created by checking
     // if any face already has e and vb.
     map<int, Face>::iterator fit;
     
     for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
     {
         if(fit->second.isAdjVertex(vb.getIndex()) && fit->second.isAdjEdge(e.getIndex()))
         {
            return fit->first;
         }
     }
     // Vertices of Edge e
     vector<int> localV = *(e.getLocalVertices());
     // vector to hold edges that will be created.
     vector<int> edges;
          
     // Add vb to each vertex of localV. Store created edge.
     for(int i = 0; i < localV.size(); i++)
     {
         edges.push_back(addVertexToVertex(localV[i], vb.getIndex()));
     }
     // Create Face
     Face f(Triangulation::greatestFace() + 1);
     // Place Face in Triangulation.
     Triangulation::putFace(f.getIndex(), f);
     
     if(Triangulation::edgeTable[e.getIndex()].getLocalFaces()->size() > 0) {
          Face fa1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
          add(&Triangulation::faceTable[fa1.getIndex()], &Triangulation::faceTable[f.getIndex()]);
     }
     
     for(int i = 0; i < edges.size(); i++) {
          for(int j = 0; j < Triangulation::edgeTable[edges[i]].getLocalFaces()->size(); j++) {
                add(&Triangulation::faceTable[(*(Triangulation::edgeTable[edges[i]].getLocalFaces()))[j]],
                    &Triangulation::faceTable[f.getIndex()]);
          }
     }
     
     // Add vb to f
     add(&Triangulation::vertexTable[vb.getIndex()], 
         &Triangulation::faceTable[f.getIndex()]);
     // Add e's other face to f
     if(e.getLocalFaces()->size() > 0)
         add(&Triangulation::faceTable[(*(e.getLocalFaces()))[0]],
             &Triangulation::faceTable[f.getIndex()]);     
     // Add e to f
     add(&Triangulation::edgeTable[e.getIndex()], 
         &Triangulation::faceTable[f.getIndex()]);
     // Add e's vertices to f
     for(int i = 0; i < localV.size(); i++)
     {
         add(&Triangulation::vertexTable[localV[i]], 
             &Triangulation::faceTable[f.getIndex()]);
     }
     // Add new edges to f
     for(int i = 0; i < edges.size(); i++)
     {
         if(Triangulation::edgeTable[edges[i]].getLocalFaces()->size() > 0)
         add(&Triangulation::faceTable[(*(Triangulation::edgeTable[edges[i]].getLocalFaces()))[0]],
             &Triangulation::faceTable[f.getIndex()]);
         add(&Triangulation::edgeTable[edges[i]], 
             &Triangulation::faceTable[f.getIndex()]);
     }
//     // Add local edges of vertices to new edges.
//     for(int i = 0; i < edges.size(); i++)
//     {
//        vector<int> localE = *(Triangulation::vertexTable[localV[i]].getLocalEdges());
//        for(int j = 0; j < localE.size(); j++)
//        {
//          if(localE[j] != edges[i])
//          {
//            add(&Triangulation::edgeTable[localE[j]], 
//                &Triangulation::edgeTable[edges[i]]);
//          }
//        }
//     }
//     // Add first new edge to second new edge.
//     add(&Triangulation::edgeTable[edges[0]], 
//         &Triangulation::edgeTable[edges[1]]);
          
     // Return f's index
     return f.getIndex();
}

int addVertexToFace(int vbInd, int fInd)
{
        
    Vertex vb = Triangulation::vertexTable[vbInd];
    Face f = Triangulation::faceTable[fInd];
        
    // Vertices of face f
    vector<int> localV = *(f.getLocalVertices());
    // Edges of face f
    vector<int> localE = *(f.getLocalEdges());
    // vector to hold faces that will be added.
    vector<int> faces;
    
    // Add vb to each edge of localE. Store face.
    for(int i = 0; i < localE.size(); i++)
    {
        faces.push_back(addVertexToEdge(vb.getIndex(), localE[i]));
    }
    // Create Tetra
    Tetra t(Triangulation::greatestTetra() + 1);
    // Place Tetra in Triangulation
    Triangulation::putTetra(t.getIndex(), t);
    
     if(Triangulation::faceTable[f.getIndex()].getLocalTetras()->size() > 0) {
          Tetra ta1 = Triangulation::tetraTable[(*(f.getLocalTetras()))[0]];
          add(&Triangulation::tetraTable[ta1.getIndex()], &Triangulation::tetraTable[t.getIndex()]);
     }
     
     for(int i = 0; i < faces.size(); i++) {
          for(int j = 0; j < Triangulation::faceTable[faces[i]].getLocalTetras()->size(); j++) {
                add(&Triangulation::tetraTable[(*(Triangulation::faceTable[faces[i]].getLocalTetras()))[j]],
                    &Triangulation::tetraTable[t.getIndex()]);
          }
     }
     
    // Add vb to t
    add(&Triangulation::vertexTable[vb.getIndex()], 
        &Triangulation::tetraTable[t.getIndex()]);
    // Add f's other tetra to t
    if(f.getLocalTetras()->size() > 0)
        add(&Triangulation::tetraTable[(*(f.getLocalTetras()))[0]],
            &Triangulation::tetraTable[t.getIndex()]);
    // Add f to t
    add(&Triangulation::faceTable[f.getIndex()], 
        &Triangulation::tetraTable[t.getIndex()]);
    // Add f's vertices to t
    for(int i = 0; i < localV.size(); i++)
    {
        add(&Triangulation::vertexTable[localV[i]], 
            &Triangulation::tetraTable[t.getIndex()]);
    }
    // Add new faces to t, and the edges of the new faces to t.
    for(int i = 0; i < faces.size(); i++)
    {
        if(Triangulation::faceTable[faces[i]].getLocalTetras()->size() > 0)
        add(&Triangulation::tetraTable[(*(Triangulation::faceTable[faces[i]].getLocalTetras()))[0]],
            &Triangulation::tetraTable[t.getIndex()]);
        add(&Triangulation::faceTable[faces[i]], 
            &Triangulation::tetraTable[t.getIndex()]);
        Face face = Triangulation::faceTable[faces[i]];
        for(int j = 0; j < face.getLocalEdges()->size(); j++)
        {
           add(&Triangulation::edgeTable[(*(face.getLocalEdges()))[j]], 
               &Triangulation::tetraTable[t.getIndex()]);
        }
    }
    
//    // Add local faces of f's edges to new faces.
//     for(int i = 0; i < faces.size(); i++)
//     {
//        vector<int> edges = listIntersection(f.getLocalEdges(), 
//                           Triangulation::faceTable[faces[i]].getLocalEdges());
//        vector<int> localF = *(Triangulation::edgeTable[edges[0]].getLocalFaces());
//        for(int j = 0; j < localF.size(); j++)
//        {
//          if(localF[j] != faces[i])
//          {
//            add(&Triangulation::faceTable[localF[j]], 
//                &Triangulation::faceTable[faces[i]]);
//          }
//        }
//     }
//     
//     // Add new faces to each other.
//     add(&Triangulation::faceTable[faces[0]], 
//         &Triangulation::faceTable[faces[1]]);
//     add(&Triangulation::faceTable[faces[0]], 
//         &Triangulation::faceTable[faces[2]]);
//     add(&Triangulation::faceTable[faces[1]], 
//         &Triangulation::faceTable[faces[2]]);
    
    // Return t's index
    return t.getIndex();
}

int makeFace(int v1, int v2, int v3) {    
    Edge e = Triangulation::edgeTable[addVertexToVertex(v1, v2)];
    return addVertexToEdge(v3, e.getIndex());
}

int addEdgeToEdge(int e1Ind, int e2Ind) {
    Edge e1 = Triangulation::edgeTable[e1Ind];
    Edge e2 = Triangulation::edgeTable[e2Ind];
    Vertex v1 = Triangulation::vertexTable[(*(e1.getLocalVertices()))[0]];
    Face f1 = Triangulation::faceTable[addVertexToEdge(v1.getIndex(), e2.getIndex())];
    Vertex v2 = Triangulation::vertexTable[(*(e1.getLocalVertices()))[1]];    
    Tetra t = Triangulation::tetraTable[addVertexToFace(v2.getIndex(), f1.getIndex())];
    return t.getIndex();
}

int makeTetra(int v1, int v2, int v3, int v4) {
    Edge e1 = Triangulation::edgeTable[addVertexToVertex(v1, v2)];
    Edge e2 = Triangulation::edgeTable[addVertexToVertex(v3, v4)];
    return addEdgeToEdge(e1.getIndex(), e2.getIndex());
}

void cube() {
     
}

void makePentagon(int v1, int v2, int v3, int v4, int v5, int v6) {
     
     
     addVertexToEdge(v1, addVertexToVertex(v2, v3));
     addVertexToEdge(v1, addVertexToVertex(v3, v4));
     addVertexToEdge(v1, addVertexToVertex(v4, v5));
     addVertexToEdge(v1, addVertexToVertex(v5, v6));
     addVertexToEdge(v1, addVertexToVertex(v6, v2));
     
          
}

void makeDodecahedron() {
     vector<Vertex> vertices;
     
     for(int i = 0; i <= 32; i++) {
             Vertex v(i + 1);
             Triangulation::putVertex(v.getIndex(), v);
             vertices.push_back(v);
     }
     

     makePentagon(vertices[21].getIndex(), vertices[1].getIndex(), vertices[2].getIndex(), 
                  vertices[3].getIndex(), vertices[4].getIndex(), vertices[5].getIndex());
     makePentagon(vertices[22].getIndex(), vertices[1].getIndex(), vertices[2].getIndex(), 
                  vertices[7].getIndex(), vertices[12].getIndex(), vertices[6].getIndex());
     makePentagon(vertices[23].getIndex(), vertices[2].getIndex(), vertices[3].getIndex(), 
                  vertices[8].getIndex(), vertices[13].getIndex(), vertices[7].getIndex());
     makePentagon(vertices[24].getIndex(), vertices[3].getIndex(), vertices[4].getIndex(), 
                  vertices[9].getIndex(), vertices[14].getIndex(), vertices[8].getIndex());
     makePentagon(vertices[25].getIndex(), vertices[4].getIndex(), vertices[5].getIndex(), 
                  vertices[10].getIndex(), vertices[15].getIndex(), vertices[9].getIndex());
     makePentagon(vertices[26].getIndex(), vertices[5].getIndex(), vertices[1].getIndex(), 
                  vertices[6].getIndex(), vertices[11].getIndex(), vertices[10].getIndex());
     makePentagon(vertices[27].getIndex(), vertices[6].getIndex(), vertices[11].getIndex(), 
                  vertices[17].getIndex(), vertices[18].getIndex(), vertices[12].getIndex());
     makePentagon(vertices[28].getIndex(), vertices[7].getIndex(), vertices[12].getIndex(), 
                  vertices[18].getIndex(), vertices[19].getIndex(), vertices[13].getIndex());
     makePentagon(vertices[29].getIndex(), vertices[8].getIndex(), vertices[13].getIndex(), 
                  vertices[19].getIndex(), vertices[20].getIndex(), vertices[14].getIndex());
     makePentagon(vertices[30].getIndex(), vertices[9].getIndex(), vertices[14].getIndex(), 
                  vertices[20].getIndex(), vertices[16].getIndex(), vertices[15].getIndex());
     makePentagon(vertices[31].getIndex(), vertices[10].getIndex(), vertices[15].getIndex(), 
                  vertices[16].getIndex(), vertices[17].getIndex(), vertices[11].getIndex());
     makePentagon(vertices[32].getIndex(), vertices[16].getIndex(), vertices[17].getIndex(), 
                  vertices[18].getIndex(), vertices[19].getIndex(), vertices[20].getIndex());
     

     for(int i = 1; i <= 60; i++) {
             addVertexToFace(vertices[0].getIndex(), i);
     }
}

void identifyVertex(int v1, int v2) {
     Vertex va = Triangulation::vertexTable[min(v1, v2)];
     Vertex vb = Triangulation::vertexTable[max(v1, v2)];
     
     
     
}













