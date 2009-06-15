#ifndef RADIUS_H_
#define RADIUS_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "vertex.h"

class Radius;
typedef map<TriPosition, Radius*, TriPositionCompare> RadiusIndex;

class Radius : public virtual GeoQuant {
private:
  static RadiusIndex* Index;

protected:
  Radius( Vertex& v );
  void recalculate();

public:
  ~Radius();
  static Radius* At( Vertex& v );
  static void CleanUp();
};
RadiusIndex* Radius::Index = NULL;

Radius::Radius( Vertex& v ){}

Radius::~Radius(){}

void Radius::recalculate(){}

Radius* Radius::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new RadiusIndex();
  RadiusIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Radius* val = new Radius( v );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Radius::CleanUp(){
  if( Index == NULL ) return;
  RadiusIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* RADIUS_H_ */
