/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "edge.h" // class's header file
#include "Triangulation.h"


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
    vector<int> v = *(getLocalVertices());
    length =  Triangulation::vertexTable[v[0]].getWeight() 
            + Triangulation::vertexTable[v[1]].getWeight();
}
void Edge::setLength(double newLength) {
     length = newLength;
}
