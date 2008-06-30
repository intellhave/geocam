/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "edge.h" // class's header file
#include "triangulation.h"


// class constructor
Edge::Edge() : Simplex()
{
}

// class destructor
Edge::~Edge()
{
	// insert your code here
}

void Edge::setLength() {
     length = 0;
    for(int i = 0; i < getLocalVertices()->size(); i++)
    {
      length += Triangulation::vertexTable[(*getLocalVertices())[i]].getWeight();
    }
}
void Edge::setLength(double newLength) {
     length = newLength;
}
