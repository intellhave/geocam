#ifndef EUCANGLE_CPP_
#define EUCANGLE_CPP_

#include <cmath>
#include <cstdio>

#include "geoquants.h"

#include "geoquant.h"

#include "triposition.h"

EuclideanAngle::EuclideanAngle(Vertex& v, Face& f, GQIndex& gqi) : GeoQuant(){
    position = new TriPosition(ANGLE, 2, v.getSerialNumber(), f.getSerialNumber());
    dataID = ANGLE;
    
    vector<int> sameAs, diff;
    sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
    diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
    TriPosition t1(LENGTH, 1, Triangulation::edgeTable[sameAs[0]].getSerialNumber());
    TriPosition t2(LENGTH, 1, Triangulation::edgeTable[sameAs[1]].getSerialNumber());
    TriPosition t3(LENGTH, 1, Triangulation::edgeTable[diff[0]].getSerialNumber());  
  
    lengthA = gqi[ t1 ];
    lengthB = gqi[ t2 ];
    lengthC = gqi[ t3 ];
    lengthA->addDependent(this);
    lengthB->addDependent(this);
    lengthC->addDependent(this);
}

void EuclideanAngle::recalculate(){
    double a = lengthA->getValue();
    double b = lengthB->getValue();
    double c = lengthC->getValue();
    value = acos((a*a + b*b - c*c)/ (2*a*b));
}

void Init_EuclideanAngles(GQIndex& gqi){
  map<int, Vertex>::iterator vit;
  for(vit = Triangulation::vertexTable.begin();
      vit != Triangulation::vertexTable.end(); vit++){
    vector<int>* faces = vit->second.getLocalFaces();
    
    for(int ii = 0; ii < faces->size(); ii++){
      Face& f = Triangulation::faceTable[ faces->at(ii) ];
      EuclideanAngle* ea = new EuclideanAngle(vit->second, f, gqi);
      GeoQuant* gq = gqi[ea->getPosition()];
      gqi[ ea->getPosition() ] = ea;            
    }
  }
}

#endif /* EUCANGLE_CPP_ */
