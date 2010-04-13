/**************************************************************
Class: Triangulation
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "triangulation.h" // class's header file
#include "triangulationInputOutput.h"
#include "utilities.h"
#include <cmath>
#include <cstdlib>
#include <map>
#include <algorithm>
#include <vector>
#include <iostream>


map<int, Vertex> Triangulation::vertexTable;
map<int, Edge> Triangulation::edgeTable;
map<int, Face> Triangulation::faceTable;
map<int, Tetra> Triangulation::tetraTable;

// class constructor
Triangulation::Triangulation()
{
}

// class destructor
Triangulation::~Triangulation()
{
}

void Triangulation::putVertex(int key, Vertex v)
{
     vertexTable.insert(pair<int, Vertex>(key, v));
}

void Triangulation::putEdge(int key, Edge v)
{
     edgeTable.insert(pair<int, Edge>(key, v));
}

void Triangulation::putFace(int key, Face v)
{
     faceTable.insert(pair<int, Face>(key, v));
}
void Triangulation::putTetra(int key, Tetra v)
{
     tetraTable.insert(pair<int, Tetra>(key, v));
}

bool Triangulation::containsVertex(int key)
{
    map<int, Vertex>::iterator vit;
    for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
    {
            if(vit->first == key)
            return true;
    }
    return false;
}

bool Triangulation::containsEdge(int key)
{
    map<int, Edge>::iterator eit;
    for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
    {
            if(eit->first == key)
            return true;
    }
    return false;
}

bool Triangulation::containsFace(int key)
{
    map<int, Face>::iterator fit;
    for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
    {
            if(fit->first == key)
            return true;
    }
    return false;
}
bool Triangulation::containsTetra(int key)
{
    map<int, Tetra>::iterator tit;
    for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
    {
            if(tit->first == key)
            return true;
    }
    return false;
}

int Triangulation::greatestVertex()
{
    int greatest = 0;
    map<int, Vertex>::iterator vit;
    for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
    {
            if(vit->first > greatest)
            greatest = vit->first;
    }
    return greatest;
}

int Triangulation::greatestEdge()
{
    int greatest = 0;
    map<int, Edge>::iterator eit;
    for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
    {
            if(eit->first > greatest)
            greatest = eit->first;
    }
    return greatest;
}

int Triangulation::greatestFace()
{
    int greatest = 0;
    map<int, Face>::iterator fit;
    for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
    {
            if(fit->first > greatest)
            greatest = fit->first;
    }
    return greatest;
}
int Triangulation::greatestTetra()
{
    int greatest = 0;
    map<int, Tetra>::iterator tit;
    for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
    {
            if(tit->first > greatest)
            greatest = tit->first;
    }
    return greatest;
}

void Triangulation::eraseVertex(int key)
{
     map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeVertex(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeVertex(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeVertex(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeVertex(key);
     }
     vertexTable.erase(key);
}

void Triangulation::eraseEdge(int key)
{
     map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeEdge(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeEdge(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeEdge(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeEdge(key);
     }
     edgeTable.erase(key);
}

void Triangulation::eraseFace(int key)
{
       map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeFace(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeFace(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeFace(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeFace(key);
     }
     faceTable.erase(key);
}

void Triangulation::eraseTetra(int key)
{
     map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeTetra(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeTetra(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeTetra(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeTetra(key);
     }
     tetraTable.erase(key);
}

bool Triangulation::isManifold()
{    
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++) {
        if(eit->second.getLocalFaces()->size() != 2){
          printf("Edge %d does not have two adjacent faces. \n", eit->second.getIndex());
          return false;
        }
     }
    
    map<int, Vertex>::iterator vit;
    for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++) {
      if(!isCyclic(vit->second))
        return false;
    }
    return true;
}
     
bool Triangulation::isCyclic(Vertex &v)
{
    /* Algorithm:
      1. Pick a face currentFace adjacent to vertex v
      2. Set currentFace equal to true in a hash map that keeps track of faces that have 
         already been visited.
      3. Pick a face adjacent to currentFace
      4. For this new face, iterate through the vertices to see if it shares the vertex v.
         If this face has not been visited AND shares vertex v, 
            Set this face to true in the hash map and set currentFace equal to this face. 
            Then go back to step 3.
         Else 
            Go on to the next face adjacent to currentFace and start from the beginning of this step. 
            If there are no more such faces that have not been visited and share vertex v,
            end the loop.
      5. If the number of faces visited equals the number of localfaces to v, return true.
         Else, return false.
    */
    
    vector<int> *faces;
    faces = v.getLocalFaces(); // Vector of local face indices
    if(faces->size() == 0){ // Making sure local faces exist
        return false;
    }
    int currentFace = *((*faces).begin()); // Get first face
    map<int, bool> visited; // Initializes hash map that tells whether or not a face has been visited
    visited[currentFace] = true;
    int counted = 1; // Keeps track of how many faces have been visited
    bool faceFound = true;
    while(faceFound){
      faceFound = false;
      
      // Gets vector of faces local to currentFace
      faces = Triangulation::faceTable[currentFace].getLocalFaces(); 
      
      // Iterates over local faces to currentFace
      for (vector<int>::iterator fit =(*faces).begin(); fit != (*faces).end(); fit++) {
        // Iterates of local vertices to each local face
          for (vector<int>::iterator vit = (*(Triangulation::faceTable[*fit].getLocalVertices())).begin();
                vit != (*(Triangulation::faceTable[*fit].getLocalVertices())).end(); vit++) {
            
            // If shares vertex and has not been visited
            if(*vit == v.getIndex() && visited[*fit] == false) {
              visited[*fit] = true;
              currentFace = *fit;
              counted++;
              faceFound = true;
              break;
            }
          }
          if(faceFound){
            break;
          }
      }
    } // End while loop
    
    if (counted == v.getLocalFaces()->size()) {
      printf("Vertex %d:\n Counted: %d, Local: %d\n", v.getIndex(), counted, v.getLocalFaces()->size());
      return true;
    } else {
      map<int, bool>::iterator fs = visited.begin();
      printf("Fails at Vertex %d: \n ", v.getIndex());
      printf("Counted: %d, Local: %d. Counted faces are ", counted, v.getLocalFaces()->size());
      for (; fs != visited.end(); fs++) {
          printf("%d ", fs->first); 
      }
      printf("\n");
      
      return false;
    }  
}
void Triangulation::reset()
{
     vertexTable.clear();
     edgeTable.clear();
     faceTable.clear();
     tetraTable.clear();
}
