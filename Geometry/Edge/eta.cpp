#include "eta.h"

#include <stdio.h>

typedef map<TriPosition, Eta*, TriPositionCompare> EtaIndex;
static EtaIndex* Index = NULL;

Eta::Eta( Edge& e ){}

void Eta::recalculate(){}

void Eta::remove() {
     deleteDependents();
     Index->erase(pos);
     delete this;
}

Eta::~Eta(){}

Eta* Eta::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new EtaIndex();
  EtaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Eta* val = new Eta( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Eta::CleanUp(){
  if( Index == NULL ) return;
  EtaIndex::iterator iter;
  EtaIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void Eta::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  EtaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}

void Eta::print(FILE* out) {
  EtaIndex::iterator iter;
  
  for(iter = Index->begin(); iter != Index->end(); iter++) {
     fprintf(out, "Eta");
     fprintf(out, "[ " );
     for(int ii = 0; ii < (iter->first).length; ii++) {
       fprintf(out, "%d ", (iter->first).pointIDs[ii]);
     }
     fprintf(out, "]");
     fprintf(out, " = % 2.8f\n", iter->second->getValue());
  }       
}
