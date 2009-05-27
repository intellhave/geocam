#ifndef AREA_CPP_
#define AREA_CPP_

#include <cmath>
#include <cstdio>

#include "triangulation/triangulation.h"
#include "simplex/edge.h"
#include "simplex/face.h"

#include "triposition.h"
#include "geoquant.h"

#include "geoquants.h"

Area::Area(Face& f, GQIndex& gqi) : GeoQuant(){
    position = new TriPosition(AREA, 1, f.getSerialNumber());
    dataID = AREA;

    Edge ed;
    for(int ii = 0; ii < 3; ii++){
      ed = Triangulation::edgeTable[(*(f.getLocalEdges()))[ii]];
      TriPosition t(LENGTH, 1, ed.getSerialNumber());
      Len[ii] = gqi[ t ];
      Len[ii]->addDependent(this);
    }
}

void Area::recalculate(){
    double l1 = Len[0]->getValue();
    double l2 = Len[1]->getValue();
    double l3 = Len[2]->getValue();

    double s = (l1 + l2 + l3) * 0.5;
    value = sqrt(s * (s - l1) * (s - l2) * (s - l3));
}

void Init_Areas(GQIndex& gqi) {
     map<int, Face>::iterator fit;
     for(fit = Triangulation::faceTable.begin(); 
               fit != Triangulation::faceTable.end(); fit++)
     {
         Area *a = new Area(fit->second, gqi);
         gqi[a->getPosition()] = a; 
     }    
}
#endif /* AREA_CPP_ */
