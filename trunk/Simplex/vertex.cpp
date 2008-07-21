/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "vertex.h" // class's header file
#include "triangulation.h"
#include <iostream>

// class constructor
Vertex::Vertex() : Simplex()
{
    weight = 0;
}

// class destructor
Vertex::~Vertex()
{
}

void Vertex::setWeight(double newWeight)
{
     weight = newWeight;
     for(int i = 0; i < getLocalEdges()->size(); i++)
     {
           Triangulation::edgeTable[(*getLocalEdges())[i]].setLength();
     }
}

void Vertex::setWeightIndependent(double newWeight)
{
     weight = newWeight;     
}

int Vertex::getDegree()
{
    return getLocalEdges()->size();
}

void Vertex::setPosition(double xPos, double yPos)
{
     pos.first = xPos;
     pos.second = yPos;
     
}
