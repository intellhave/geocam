#ifndef SPHERANGLE_CPP_
#define SPHERANGLE_CPP_

#include <cmath>
#include <cstdio>

#include "geoquant.h"
#include "geoquants.h"

SphericalAngle::SphericalAngle(Vertex& v, Face& f, GQIndex& gqi) : GeoQuant(){
    position = new TriPosition(ANGLE, 2, v.getSerialNumber(), f.getSerialNumber());
    dataID = ANGLE;

    vector<int> sameAs, diff;
    sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
    diff = listDifference(f.getLocalEdges(), v.getLocalEdges());  

    TriPosition t1(ANGLE, 1, Triangulation::edgeTable[sameAs[0]].getSerialNumber());
    TriPosition t2(ANGLE, 1, Triangulation::edgeTable[sameAs[1]].getSerialNumber());
    TriPosition t3(ANGLE, 1, Triangulation::edgeTable[diff[0]].getSerialNumber());  
  
    lengthA = gqi[ t1 ];
    lengthB = gqi[ t2 ];
    lengthC = gqi[ t3 ];

    lengthA->addDependent(this);
    lengthB->addDependent(this);
    lengthC->addDependent(this);
}

void SphericalAngle::recalculate(){
    double a = lengthA->getValue();
    double b = lengthB->getValue();
    double c = lengthC->getValue();
    value = acos((cos(c*c) - cos(b*b)*cos(a*a))/ (sin(a)*sin(b)));
}

#endif /* SPHERANGLE_CPP_ */
