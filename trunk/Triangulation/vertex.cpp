/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "vertex.h" // class's header file
#include "triangulation/triangulation.h"

// class constructor
Vertex::Vertex() : Simplex()
{
}

// class destructor
Vertex::~Vertex()
{
}

int Vertex::getDegree()
{
    return getLocalEdges()->size();
}

