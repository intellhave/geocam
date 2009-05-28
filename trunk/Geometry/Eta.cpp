#ifndef ETA_CPP_
#define ETA_CPP_

#include <new>
#include "geoquants.h"

Eta::Eta(Edge& e, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(ETA, 1, e.getSerialNumber());
    dataID = ETA;
}

void Eta::recalculate() {
    /* Here we assume Etas are fixed, 
     * so no changes occur here. */
}

void Init_Etas(GQIndex& gqi){
  map<int, Edge>::iterator eit;
  for(eit = Triangulation::edgeTable.begin();
      eit != Triangulation::edgeTable.end(); eit++){
    Eta* e = new Eta(eit->second, gqi);
    gqi[e->getPosition()] = e;
  }
}
#endif /* ETA_CPP_ */
