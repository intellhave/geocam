#ifndef RADIUS_H_
#define RADIUS_H_

#include "geoquants.h"

Radius::Radius( Vertex &v, GQIndex& gqi ) : GeoQuant() {
    position = new TriPosition( RADIUS, 1, v.getSerialNumber() );
    dataID = RADIUS;
}

void Radius::recalculate(){
    /* No code. Radii are initialized and
     * modified but not recalculated.*/
}

void Init_Radii(GQIndex& gqi){
  map<int, Vertex>::iterator vit;
  for(vit = Triangulation::vertexTable.begin(); 
      vit != Triangulation::vertexTable.end(); vit++){
    Radius* r = new Radius(vit->second, gqi);
    gqi[r->getPosition()] = r;
  }
}
#endif /* RADIUS_H_ */
