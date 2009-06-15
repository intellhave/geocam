#ifndef RADIUS_CPP_
#define RADIUS_CPP_

#include "geoquant.h"
#include "vertex.h"

class Radius : public virtual GeoQuant {
protected:
  TriPosition position;
  void recalculate();
  Radius( Vertex& v );

public:
  static Radius* At( Vertex& v );
  static void CleanUp();
};

typedef map<TriPosition, Radius*, TriPositionCompare> RadiusIndex; 
static EtaIndex* eta_index = NULL;

Radius::Radius( Vertex &v ) : GeoQuant() {
  position = TriPosition( 1, v.getSerialNumber() );
}

/* Radii are initialized and modified but not recalculated.*/
void Radius::recalculate() {}

GeoQuant* Radius::At( Vertex& v ){
  GeoQuant* retval;
  if(Index == NULL) Index = new GQIndex();

  TriPosition t(1, v.getSerialNumber());
  GQIndex::iterator iter = Index->find( t );
  if( iter == Index->end() ){
    retval = new Radius( v );
    Index->insert( make_pair( t, retval ) );
  } else {
    retval = iter->second;
  }
  
  return retval;
}

void Radius::CleanUp(){
  GQIndex::iterator it;
  for(it = Index->begin(); it != Index->end(); it++)
    delete it->second;
  
  delete Index;
}

#endif /* RADIUS_CPP_ */
