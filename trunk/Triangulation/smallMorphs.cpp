/**************************************************************
File: Small Morphs
Author: Alex Henniges, Tom Williams
Version: October 3, 2008
***************************************************************
The Small Morphs file holds the functions that perform isolated 
morphs on simplices. These functions are then used in morphs on
a whole Triangulation.
**************************************************************/
#include "smallMorphs.h"
#include "addRemove.h"

int addVertexToVertex(Vertex va, Vertex vb)
{    
     // Check if edge has already been created by checking
     // if va and vb are already adjacent.
     if(va.isAdjVertex(vb.getIndex())) {
          vector<int> sameAs;
          sameAs = listIntersection(va.getLocalEdges(), vb.getLocalEdges());
          if(sameAs.size() == 0)
             return sameAs[0];
     }
     
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
     
     // Return e's index.
     return e.getIndex();
}

int addVertexToEdge(Edge e, Vertex vb)
{
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
         edges.push_back(addVertexToVertex(Triangulation::vertexTable[localV[i]], vb));
     }
     
     // Create Face
     Face f(Triangulation::greatestFace() + 1);
     // Place Face in Triangulation.
     Triangulation::putFace(f.getIndex(), f);
     
     // Add vb to f
     add(&Triangulation::vertexTable[vb.getIndex()], 
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
         add(&Triangulation::edgeTable[localV[i]], 
             &Triangulation::faceTable[f.getIndex()]);
     }
     // Add local edges of vertices to new edges.
     for(int i = 0; i < edges.size(); i++)
     {
        vector<int> localE = *(Triangulation::vertexTable[localV[i]].getLocalEdges());
        for(int j = 0; j < localE.size(); j++)
        {
          if(localE[j] != edges[i])
          {
            add(&Triangulation::edgeTable[localE[j]], 
                &Triangulation::edgeTable[edges[i]]);
          }
        }
     }
     // Add first new edge to second new edge.
     add(&Triangulation::edgeTable[edges[0]], 
         &Triangulation::edgeTable[edges[1]]);
     // Return f's index
     return f.getIndex();
}

int addVertexToFace(Face f, Vertex vb)
{
    // Vertices of face f
    vector<int> localV = *(f.getLocalVertices());
    // Edges of face f
    vector<int> localE = *(f.getLocalEdges());
    // vector to hold faces that will be added.
    vector<int> faces;
    
    // Add vb to each edge of localE. Store face.
    for(int i = 0; i < localE.size(); i++)
    {
        faces.push_back(addVertexToEdge(Triangulation::edgeTable[localE[i]], vb));
    }
    
    // Create Tetra
    Tetra t(Triangulation::greatestTetra());
    // Place Tetra in Triangulation
    Triangulation::putTetra(t.getIndex(), t);
    
    // Add vb to t
    add(&Triangulation::vertexTable[vb.getIndex()], 
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
        add(&Triangulation::faceTable[faces[i]], 
            &Triangulation::tetraTable[t.getIndex()]);
        Face face = Triangulation::faceTable[faces[i]];
        for(int j = 0; j < face.getLocalEdges()->size(); j++)
        {
           add(&Triangulation::edgeTable[(*(face.getLocalEdges()))[j]], 
               &Triangulation::tetraTable[t.getIndex()]);
        }
    }
    
    // Add local faces of f's edges to new faces.
     for(int i = 0; i < faces.size(); i++)
     {
        vector<int> localF = *(Triangulation::vertexTable[localE[i]].getLocalFaces());
        for(int j = 0; j < localF.size(); j++)
        {
          if(localF[j] != faces[i])
          {
            add(&Triangulation::faceTable[localF[j]], 
                &Triangulation::faceTable[faces[i]]);
          }
        }
     }
     
     // Add new faces to each other.
     add(&Triangulation::faceTable[faces[0]], 
         &Triangulation::faceTable[faces[1]]);
     add(&Triangulation::faceTable[faces[0]], 
         &Triangulation::faceTable[faces[2]]);
     add(&Triangulation::faceTable[faces[1]], 
         &Triangulation::faceTable[faces[2]]);
    
    // Return t's index
    return t.getIndex();
}
