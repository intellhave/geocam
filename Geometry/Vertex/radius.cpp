#include "radius.h"

#include <stdio.h>

typedef map<TriPosition, Radius*, TriPositionCompare> RadiusIndex;
static RadiusIndex* Index = NULL;

Radius::Radius( Vertex& v ){}

void Radius::remove() {
   deleteDependents();
   Index->erase(pos);
   delete this;
}

Radius::~Radius(){}

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
  
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void Radius::print(FILE* out) {
  map<int, Vertex>::iterator vit;
  
  fprintf(out, "Radius [ v ]\n=============\n");
  for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
     fprintf(out, "Radius [%3d]\t= % 2.8f\n", vit->first, Radius::valueAt(vit->second));
  }       
}
