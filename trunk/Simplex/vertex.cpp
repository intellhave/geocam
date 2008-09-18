/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "vertex.h" // class's header file
#include "triangulation.h"

// class constructor
Vertex::Vertex() : Simplex()
{
    radius = 0;
}

// class destructor
Vertex::~Vertex()
{
}

void Vertex::setRadius(double newRadius)
{
     radius = newRadius;
     for(int i = 0; i < getLocalEdges()->size(); i++)
     {
           Triangulation::edgeTable[(*getLocalEdges())[i]].setLength();
     }
}

void Vertex::setRadiusIndependent(double newRadius)
{
     radius = newRadius;     
}

int Vertex::getDegree()
{
    return getLocalEdges()->size();
}

