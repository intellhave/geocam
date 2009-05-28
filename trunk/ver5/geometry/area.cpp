#ifndef AREA_CPP_
#define AREA_CPP_

#include <cmath>
#include <cstdio>

#include "triangulation.h"
#include "edge.h"
#include "face.h"

#include "triposition.h"
#include "geoquant.h"

class Area : public virtual GeoQuant {
 private:
  GeoQuant* Len[3];

 public:
  Area(Face f, GQIndex& gqi) : GeoQuant(){
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

  void recalculate(){
    double l1 = Len[0]->getValue();
    double l2 = Len[1]->getValue();
    double l3 = Len[2]->getValue();

    double s = (l1 + l2 + l3) * 0.5;
    value = sqrt(s * (s - l1) * (s - l2) * (s - l3));
  }
};

#endif /* AREA_CPP_ */
