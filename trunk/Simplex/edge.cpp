/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "edge.h" // class's header file
#include "triangulation.h"
#include <iostream>


// class constructor
Edge::Edge() : Simplex()
{
}

// class destructor
Edge::~Edge()
{
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
Line Edge::convertToLine()
{
     Vertex v1 = Triangulation::vertexTable[(*getLocalVertices())[0]];
     Vertex v2 = Triangulation::vertexTable[(*getLocalVertices())[1]];
     Line l(v1.getXpos(), v1.getYpos(), v2.getXpos(), v2.getYpos());
     return l;
}
