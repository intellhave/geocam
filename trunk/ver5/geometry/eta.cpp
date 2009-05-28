#ifndef ETA_CPP_
#define ETA_CPP_

#include <new>

#include "geoquant.h"
#include "triposition.h"

#include "triangulation.h"
#include "edge.h"

class Eta : public virtual GeoQuant {
public:
  Eta(Edge& e) : GeoQuant(){
    position = new TriPosition(ETA, 1, e.getSerialNumber()); 
  }

  void recalculate(){
    /* Here we assume Etas are fixed, 
     * so no changes occur here. */
  }
};

void Init_Etas(GQIndex& gqi){
  map<int, Edge>::iterator eit;
  for(eit = Triangulation::edgeTable.begin();
      eit != Triangulation::edgeTable.end(); eit++){
    Eta* e = new Eta(eit->second);
    gqi[e->getPosition()] = e;
  }
}

#endif /* ETA_CPP_ */
