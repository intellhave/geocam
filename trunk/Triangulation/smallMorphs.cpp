#include "smallMorphs.h"
#include "addRemove.h"

int addVertexToVertex(Vertex va, Vertex vb)
{    
     // Check if edge has already been created by checking
     // if va and vb are already adjacent.
     if(va.isAdjVertex(vb.getIndex())) {
          vector<int> sameAs;
          sameAs = listIntersection(va.getLocalEdges(), vb.getLocalEdges());
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
     for(int j = 0; j < edges.size(); j++)
     {
        vector<int> localE = *(Triangulation::vertexTable[localV[j]].getLocalEdges());
        for(int i = 0; i < localE.size(); i++)
        {
          if(localE[i] != edges[j])
          {
            add(&Triangulation::edgeTable[localE[i]], 
                &Triangulation::edgeTable[edges[j]]);
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
    vector<int> localV = *(f.getLocalVertices());
    vector<int> localE = *(f.getLocalEdges());
    vector<int> faces;
    
    for(int i = 0; i < localE.size(); i++)
    {
        faces.push_back(addVertexToEdge(Triangulation::edgeTable[localE[i]], vb));
    }
    
    Tetra t(Triangulation::greatestTetra());
    Triangulation::putTetra(t.getIndex(), t);
    
    add(&Triangulation::vertexTable[vb.getIndex()], 
        &Triangulation::tetraTable[t.getIndex()]);
    add(&Triangulation::faceTable[f.getIndex()], 
        &Triangulation::tetraTable[t.getIndex()]);
    for(int i = 0; i < localV.size(); i++)
    {
        add(&Triangulation::vertexTable[localV[i]], 
            &Triangulation::tetraTable[t.getIndex()]);
    }
    for(int i = 0; i < localE.size(); i++)
    {
        add(&Triangulation::edgeTable[localE[i]], 
            &Triangulation::tetraTable[t.getIndex()]);
    }
    for(int i = 0; i < faces.size(); i++)
    {
        add(&Triangulation::faceTable[faces[i]], 
            &Triangulation::tetraTable[t.getIndex()]);
    }
    
     for(int j = 0; j < faces.size(); j++)
     {
        vector<int> localF = *(Triangulation::vertexTable[localE[j]].getLocalFaces());
        for(int i = 0; i < localF.size(); i++)
        {
          if(localF[i] != faces[j])
          {
            add(&Triangulation::faceTable[localF[i]], 
                &Triangulation::faceTable[faces[j]]);
          }
        }
     }
     
     add(&Triangulation::faceTable[faces[0]], 
         &Triangulation::faceTable[faces[1]]);
     add(&Triangulation::faceTable[faces[0]], 
         &Triangulation::faceTable[faces[2]]);
     add(&Triangulation::faceTable[faces[1]], 
         &Triangulation::faceTable[faces[2]]);
    
    return t.getIndex();
}
