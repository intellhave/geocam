/**************************************************************
Class: Simplex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#include "simplex.h" // class's header file
#include "triangulation.h"
#include <iostream>
#include <algorithm>
#include <vector>
#include <cstdlib>


Simplex::Simplex()
{
}
Simplex::Simplex(int setIndex)
{
     index = setIndex;
}
Simplex::~Simplex()
{
}
int Simplex::getIndex()
{
    return index;
}
void Simplex::addVertex(int vertex)
{
     vector<int>::iterator it = find(localVertices.begin(), localVertices.end(), vertex);
     if(it == localVertices.end())
     localVertices.push_back(vertex);
}
void Simplex::addEdge(int edge)
{
     vector<int>::iterator it = find(localEdges.begin(), localEdges.end(), edge);
     if(it == localEdges.end())
     localEdges.push_back(edge);
}
void Simplex::addFace(int face)
{
     vector<int>::iterator it = find(localFaces.begin(), localFaces.end(), face);
     if(it == localFaces.end())
     localFaces.push_back(face);
}
void Simplex::removeVertex(int vertex)
{
     for(int i = 0; i < localVertices.size(); i++)
     {
             if(localVertices[i] == vertex)
             {
               localVertices.erase(localVertices.begin() + i);
               break;
             }
     }
}
void Simplex::removeEdge(int edge)
{
     for(int i = 0; i < localEdges.size(); i++)
     {
             if(localEdges[i] == edge)
             {
              localEdges.erase(localEdges.begin() + i);
              break;
             }
     }
     
}
void Simplex::removeFace(int face)
{
     for(int i = 0; i < localFaces.size(); i++)
     {
             if(localFaces[i] == face)
             {
              localFaces.erase(localFaces.begin() + i);
              break;
             }
     }
     
}
vector<int>* Simplex::getLocalVertices()
{
     return &localVertices;
}
vector<int>* Simplex::getLocalEdges()
{
     return &localEdges;
}
vector<int>* Simplex::getLocalFaces() 
{
     return &localFaces;
}

