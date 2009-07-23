#include "eta.h"

#include <stdio.h>

typedef map<TriPosition, Eta*, TriPositionCompare> EtaIndex;
static EtaIndex* Index = NULL;

Eta::Eta( Edge& e ){}

void Eta::recalculate(){}

Eta::~Eta(){}

Eta* Eta::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new EtaIndex();
  EtaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Eta* val = new Eta( e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Eta::CleanUp(){
  if( Index == NULL) return;
  EtaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

void Eta::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  EtaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
