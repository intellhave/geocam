/**************************************************************
Class: Face
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#include "face.h" // class's header file
#include "edge.h"
#include "Triangulation/Triangulation.h"
#include "Triangulation/TriangulationMath.h"
#include <math.h>

// class constructor
Face::Face() : Simplex()
{

      negative = false;

}

// class destructor
Face::~Face()
{
}


double Face::getArea()
{
    Edge e1 = Triangulation::edgeTable[(*(getLocalEdges()))[0]];
    Edge e2 = Triangulation::edgeTable[(*(getLocalEdges()))[1]];
    Edge e3 = Triangulation::edgeTable[(*(getLocalEdges()))[2]];
    double theta = angle(e1.getLength(), e2.getLength(), e3.getLength());
    return (sin(theta) * e1.getLength() * e2.getLength() / 2);    
}

void Face::switchSide()
{
     negative = !negative;
}
void Face::setNegativity(bool negativity)
{
     negative = negativity;
}
bool Face::isNegative()
{
     return negative;
}
