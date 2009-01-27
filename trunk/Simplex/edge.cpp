/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "edge.h" // class's header file
#include "triangulation/triangulation.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include <cmath>
#define PI 	3.141592653589793238

// class constructor
Edge::Edge() : Simplex() //: eta = 1.0
{
    eta = 1.0;
    length = 0;
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
                        + 2*v1.getRadius()*v2.getRadius()*eta);
}
void Edge::setLength(double newLength) {
     length = newLength;
}
double Edge::getEta() {
     return eta;
}
double Edge::getIntersectAngle()
{
    return acos(eta);
}
void Edge::setEta(double newEta) {
     eta = newEta;
     setLength();
}
void Edge::setIntersectAngle(double angle)
{
    eta = cos(angle);
    setLength();
}

bool Edge::isBorder()
{
     return getLocalFaces()->size() == 1;
}

void Edge::setDihedralAngles()
{
     dihedralAngles.clear();
     for(int i = 0; i < getLocalTetras()->size(); i++)
     {
         Tetra t = Triangulation::tetraTable[(*getLocalTetras())[i]];
         Vertex v = Triangulation::vertexTable[(*getLocalVertices())[0]];
         dihedralAngles.insert(pair<int, double>(t.getIndex(), dihedralAngle(v,*this,t)) );
     }
}

double Edge::getDihedralAngle(int index)
{
   return dihedralAngles[index];
}

