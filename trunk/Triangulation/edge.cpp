/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "edge.h" // class's header file
#include "triangulation.h"
#include <cmath>
#define PI 	3.141592653589793238

// class constructor
Edge::Edge() : Simplex()
{
}
Edge::Edge(int setindex) : Simplex(setindex)
{
}
// class destructor
Edge::~Edge()
{
}

bool Edge::isBorder()
{
     return getLocalFaces()->size() == 1;
}


