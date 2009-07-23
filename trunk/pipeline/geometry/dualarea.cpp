#include "dualarea.h"

#include <stdio.h>

typedef map<TriPosition, DualArea*, TriPositionCompare> DualAreaIndex;
static DualAreaIndex* Index = NULL;

DualArea::DualArea( Edge& e ){
  segments = new vector<DualAreaSegment*>();
  
  vector<int>* tetras = e.getLocalTetras();
  DualAreaSegment* seg;
  for(int ii = 0; ii < tetras->size(); ii++){
    Tetra t = Triangulation::tetraTable[ tetras->at(ii) ];
    seg = DualAreaSegment::At( e, t );
    seg->addDependent( this );
    segments->push_back( seg );
  }
}

DualArea::~DualArea(){
  delete segments;
}

void DualArea::recalculate(){
  DualAreaSegment* das;
  value = 0.0;

  for(int ii = 0; ii < segments->size(); ii++){
    das = segments->at(ii);
    value += das->getValue();
  }
}

DualArea* DualArea::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new DualAreaIndex();
  DualAreaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DualArea* val = new DualArea(e);
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DualArea::CleanUp(){
  if( Index == NULL ) return;
  DualAreaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

void DualArea::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  DualAreaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
