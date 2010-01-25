/**************************************************************
Class: Simplex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#include "simplex.h" // class's header file
#include <algorithm>

void Simplex::addVertex(int vertex){
  //vector<int>::iterator it = find(localVertices.begin(), localVertices.end(), vertex);
  //if(it == localVertices.end())
    localVertices.push_back(vertex);
}

void Simplex::addEdge(int edge){
  //vector<int>::iterator it = find(localEdges.begin(), localEdges.end(), edge);
  //if(it == localEdges.end())
    localEdges.push_back(edge);
}

void Simplex::addFace(int face){
  //vector<int>::iterator it = find(localFaces.begin(), localFaces.end(), face);
  //if(it == localFaces.end())
    localFaces.push_back(face);
}

void Simplex::addTetra(int tetra){
  //vector<int>::iterator it = find(localTetras.begin(), localTetras.end(), tetra);
  //if(it == localTetras.end())
    localTetras.push_back(tetra);
}

void Simplex::removeVertex(int vertex){ 
  for(int i = 0; i < localVertices.size(); i++){
    if(localVertices[i] == vertex) {
      localVertices.erase(localVertices.begin() + i);
      return;
    }
  }
}

void Simplex::removeEdge(int edge){
  for(int i = 0; i < localEdges.size(); i++){
    if(localEdges[i] == edge) {
      localEdges.erase(localEdges.begin() + i);
      return;
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
	  return;
	}
    }   
}
void Simplex::removeTetra(int tetra)
{
  for(int i = 0; i < localTetras.size(); i++)
    {
      if(localTetras[i] == tetra)
	{
	  localTetras.erase(localTetras.begin() + i);
	  return;
	}
    }   
}

void Simplex::clearLocals()
{
  localVertices.clear();
  localEdges.clear();
  localFaces.clear();
  localTetras.clear();
}

bool Simplex::isAdjVertex(int vIndex)
{
  for(int i = 0; i < localVertices.size(); i++)
    {
      if(localVertices[i] == vIndex)
	return true;
    }
  return false;
}

bool Simplex::isAdjEdge(int eIndex){
  for(int i = 0; i < localEdges.size(); i++)
    if(localEdges[i] == eIndex)
      return true;
  
  return false;
}

bool Simplex::isAdjFace(int fIndex){
  for(int i = 0; i < localFaces.size(); i++)
    if(localFaces[i] == fIndex)
      return true;
   
  return false;
}

bool Simplex::isAdjTetra(int tIndex){
  for(int i = 0; i < localTetras.size(); i++)
    if(localTetras[i] == tIndex)
      return true;

  return false;
}

vector<int>* Simplex::getLocalVertices(){
  return &localVertices;
}

vector<int>* Simplex::getLocalEdges(){
  return &localEdges;
}

vector<int>* Simplex::getLocalFaces(){
  return &localFaces;
}

vector<int>* Simplex::getLocalTetras(){
  return &localTetras;
}
