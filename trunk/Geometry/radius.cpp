#ifndef RADIUS_H_
#define RADIUS_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "geoquants.h"
#include "triposition.h"
#include "triangulation/triangulation.h"

RadiusIndex* Radius::Index = NULL;

Radius::Radius( Vertex& v ){
}

Radius::~Radius(){
  remove();
}
void Radius::remove() {
   deleteDependents();
   Index->erase(pos);
}

void Radius::recalculate(){}

Radius* Radius::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new RadiusIndex();
  RadiusIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Radius* val = new Radius( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Radius::CleanUp(){
  if( Index == NULL ) return;
  RadiusIndex::iterator iter;
  RadiusIndex copy = *Index;
  
  for(iter = copy.begin(); iter != copy.end(); iter++)
    delete iter->second;

  delete Index;
  Index = NULL;
}

#endif /* RADIUS_H_ */
