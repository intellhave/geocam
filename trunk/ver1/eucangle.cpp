#include <new>
#include <cmath>
#include <cstdio>

#include "eucangle.h"
#include "triposition.h"
#include "miscmath.h"
#include "triangulation.h"

EucAngle::EucAngle(Vertex v, Face f, Map<TriPosition, GeoQuant, TriPositionCompare> gqi) : GeoQuant(){
  position = TriPosition(ANGLE, 2, v, f);
  dataID = ANGLE;

  vector<int> sameAs, diff;
  sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
  diff = listDifference(f.getLocalEdges(), v.getLocalEdges());  

  TriPosition t1 (ANGLE, 1, Triangulation::edgeTable[sameAs[0]]);
  TriPosition t2 (ANGLE, 1, Triangulation::edgeTable[sameAs[1]]);
  TriPosition t3 (ANGLE, 1, Triangulation::edgeTable[diff[0]]);  
  
  lengthA = gqi[ t1 ];
  lengthB = gqi[ t2 ];
  lengthC = gqi[ t3 ];

  if(lengthA == NULL || lengthB == NULL || lengthC == NULL){
    fprintf(stderr, "Error, EucAngle depends upon intialized lengths!\n");
    return;
  }

  lengthA.addDependent(this);
  lengthB.addDependent(this);
  lengthC.addDependent(this);
}

static double calcAngle(double lengthA, double lengthB, double lengthC){
  //               a^2 + b^2 - c^2
  //  (/) = acos( ----------------- )
  //                     2ab
  return acos((lengthA*lengthA + lengthB*lengthB - lengthC*lengthC)
		/ (2*lengthA*lengthB));
}

void EucAngle::recalculate(){
  value = calcAngle(lengthA->getValue(), lengthB->getValue(), lengthC->getValue());
}

