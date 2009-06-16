#ifndef DUALAREA_H_
#define DUALAREA_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "geoquants.h"

DualAreaIndex* DualArea::Index = NULL;

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

#endif /* DUALAREA_H_ */

