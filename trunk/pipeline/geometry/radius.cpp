#include "radius.h"

#include <stdio.h>

typedef map<TriPosition, Radius*, TriPositionCompare> RadiusIndex;
static RadiusIndex* Index = NULL;

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
