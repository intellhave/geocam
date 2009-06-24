#ifndef DUALAREASEGMENT_H_
#define DUALAREASEGMENT_H_

#include <map>
#include <new>
using namespace std;

#include "miscmath.h"

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"

DualAreaSegmentIndex* DualAreaSegment::Index = NULL;

DualAreaSegment::DualAreaSegment( Edge& e, Tetra& t ){
  StdTetra st = labelTetra(e, t);
  
  hij_k = EdgeHeight::At( st.e12, st.f123 );
  hij_l = EdgeHeight::At( st.e12, st.f124 );
  
  hijk_l = FaceHeight::At( st.f123, t );
  hijl_k = FaceHeight::At( st.f124, t );

  hij_k->addDependent(this);
  hij_l->addDependent(this);
  hijk_l->addDependent(this);
  hijl_k->addDependent(this);
}

void DualAreaSegment::recalculate(){
  double Hij_k = hij_k->getValue();
  double Hijk_l = hijk_l->getValue();
  double Hij_l = hij_l->getValue();
  double Hijl_k = hijl_k->getValue();

  double result = 0.5*(Hij_k * Hijk_l + Hij_l * Hijl_k);
}

void DualAreaSegment::remove() {
     deleteDependents();
     hij_k->removeDependent(this);
     hijk_l->removeDependent(this);
     hij_l->removeDependent(this);
     hijl_k->removeDependent(this);
     Index->erase(pos);
}

DualAreaSegment::~DualAreaSegment(){
   remove();
}

DualAreaSegment* DualAreaSegment::At( Edge& e, Tetra& t ){
  TriPosition T( 2, e.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new DualAreaSegmentIndex();
  DualAreaSegmentIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DualAreaSegment* val = new DualAreaSegment( e, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DualAreaSegment::CleanUp(){
  if( Index == NULL ) return;
  DualAreaSegmentIndex::iterator iter;
  DualAreaSegmentIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    delete iter->second;
  }
    
  delete Index;
  Index = NULL;
}

#endif /* DUALAREASEGMENT_H_ */

