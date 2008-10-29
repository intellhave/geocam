/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "edge.h" // class's header file
#include "triangulation/triangulation.h"
#include <cmath>

// class constructor
Edge::Edge() : Simplex()
{
    intersectAngle = 0;
}

// class destructor
Edge::~Edge()
{
}

void Edge::setLength() {
     length = 0;
     if(getLocalVertices()->size() != 2)
     {
          length = -1;
          return;
     }
   Vertex v1 = Triangulation::vertexTable[(*getLocalVertices())[0]];
   Vertex v2 = Triangulation::vertexTable[(*getLocalVertices())[1]];
   // Theta = intersection angle of the spheres
   length = sqrt(pow(v1.getRadius(), 2) + pow(v2.getRadius(), 2)
                        + 2*v1.getRadius()*v2.getRadius()*cos(intersectAngle));
}
void Edge::setLength(double newLength) {
     length = newLength;
}

double Edge::getAngle()
{
       return intersectAngle;
}

void Edge::setAngle(double angle)
{
     intersectAngle = angle;
}

bool Edge::isBorder()
{
     return getLocalFaces()->size() == 1;
}
