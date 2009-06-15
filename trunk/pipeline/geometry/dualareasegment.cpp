#ifndef DUALAREASEGMENT_H_
#define DUALAREASEGMENT_H_

#include <map>
#include <new>
using namespace std;

#include "miscmath.h"

#include "geoquant.h"
#include "triposition.h"

#include "edge_height.cpp"
#include "face_height.cpp"

class DualAreaSegment;
typedef map<TriPosition, DualAreaSegment*, TriPositionCompare> DualAreaSegmentIndex;

class DualAreaSegment : public virtual GeoQuant {
private:
  static DualAreaSegmentIndex* Index;
  EdgeHeight* hij_k;
  EdgeHeight* hij_l;
  FaceHeight* hijk_l;
  FaceHeight* hijl_k;

protected:
  DualAreaSegment( Edge& e, Tetra& t );
  void recalculate();

public:
  ~DualAreaSegment();
  static DualAreaSegment* At( Edge& e, Tetra& t );
  static void CleanUp();
};
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

DualAreaSegment::~DualAreaSegment(){}

DualAreaSegment* DualAreaSegment::At( Edge& e, Tetra& t ){
  TriPosition T( 2, e.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new DualAreaSegmentIndex();
  DualAreaSegmentIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DualAreaSegment* val = new DualAreaSegment( e, t );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DualAreaSegment::CleanUp(){
  if( Index == NULL ) return;
  DualAreaSegmentIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* DUALAREASEGMENT_H_ */

