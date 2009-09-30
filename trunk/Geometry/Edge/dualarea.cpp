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
}

void DualArea::remove() {
     deleteDependents();
     for(int ii = 0; ii < segments->size(); ii++) {
        segments->at(ii)->removeDependent(this);
     }
     Index->erase(pos);
     delete segments;
     delete this;
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
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DualArea::CleanUp(){
  if( Index == NULL ) return;
  DualAreaIndex::iterator iter;
  DualAreaIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void DualArea::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  DualAreaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
