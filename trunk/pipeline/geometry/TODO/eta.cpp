#ifndef ETA_CPP_
#define ETA_CPP_

#include "geoquant.h"
#include "edge.h"

class Eta : public virtual GeoQuant {
protected:
  TriPosition position;
  void recalculate();
  Eta( Edge& e );

public:
  static Eta* At( Edge& e );
  static void CleanUp();
};
typedef map<TriPosition, Eta*, TriPositionCompare> EtaIndex; 
static EtaIndex* eta_index = NULL;

Eta::Eta( Edge& e ) : GeoQuant() {
  position = TriPosition(1, e.getSerialNumber());
}

/* We assume Etas are fixed, so "recalculate" does nothing. */
void Eta::recalculate() {}

Eta* Eta::At( Edge& e ){
  Eta* retval;
  if(eta_index == NULL) eta_index = new EtaIndex();

  TriPosition t(1, e.getSerialNumber());
  EtaIndex::iterator iter = eta_index->find( t );
  if( iter == eta_index->end() ){
    retval = new Eta( e );
    eta_index->insert( make_pair( t, retval ) );
  } else {
    retval = iter->second;
  }
  
  return retval;
}

void Eta::CleanUp(){
  EtaIndex::iterator it;
  for(it = eta_index->begin(); it != eta_index->end(); it++)
    delete it->second;
  
  delete eta_index;
}

#endif /* ETA_CPP_ */
