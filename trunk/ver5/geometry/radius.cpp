#ifndef RADIUS_CPP_
#define RADIUS_CPP_

#include <new>

#include "geoquant.h"
#include "triposition.h"

#include "triangulation.h"
#include "vertex.h"

class Radius : public virtual GeoQuant {
private:
  /* None. */

public:
  Radius(Vertex& v) : GeoQuant(){
    position = new TriPosition(RADIUS, 1, v.getSerialNumber());
  }

  void recalculate(){
    /* No code. Radii are initialized and
     * modified but not recalculated.*/
  }
};

void Init_Radii(GQIndex& gqi){
  map<int, Vertex>::iterator vit;
  for(vit = Triangulation::vertexTable.begin(); 
      vit != Triangulation::vertexTable.end(); vit++){
    Radius* r = new Radius(vit->second);
    gqi[r->getPosition()] = r;
  }
}

#endif /* RADIUS_CPP_ */
