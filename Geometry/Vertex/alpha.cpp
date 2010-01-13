#include "alpha.h"

#include <stdio.h>

typedef map<TriPosition, Alpha*, TriPositionCompare> AlphaIndex;
static AlphaIndex* Index = NULL;

Alpha::Alpha( Vertex& v ){}

void Alpha::recalculate(){
   // DEFAULT
   value = 1;
}

void Alpha::remove() {
     deleteDependents();
     Index->erase(pos);
     delete this;
}

Alpha::~Alpha(){}

Alpha* Alpha::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new AlphaIndex();
  AlphaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Alpha* val = new Alpha( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Alpha::CleanUp(){
  if( Index == NULL ) return;
  AlphaIndex::iterator iter;
  AlphaIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void Alpha::print(FILE* out) {
  map<int, Vertex>::iterator vit;
  
  fprintf(out, "Alpha [ v ]\n=============\n");
  for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
     fprintf(out, "Alpha [%3d]\t= % 2.8f\n", vit->first, Alpha::valueAt(vit->second));
  }       
}
