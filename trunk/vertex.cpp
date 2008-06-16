/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "vertex.h" // class's header file
#include "triangulation.h"

// class constructor
Vertex::Vertex() : Simplex()
{
                 weight = 0;
}

// class destructor
Vertex::~Vertex()
{
	// insert your code here
}

void Vertex::setWeight(double newWeight)
{
     weight = newWeight;
     for(int i = 0; i < getLocalEdges()->size(); i++)
     {
           Triangulation::edgeTable[(*getLocalEdges())[i]].setLength();
     }
}
