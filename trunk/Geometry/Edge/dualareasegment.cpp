#include "dualareasegment.h"
#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, DualAreaSegment*, TriPositionCompare> DualAreaSegmentIndex;
static DualAreaSegmentIndex* Index = NULL;

DualAreaSegment::DualAreaSegment( Edge& e, Tetra& t ){
  StdTetra st = labelTetra( t, e );
  
  Face& fa123 = Triangulation::faceTable[ st.f123 ];
  Face& fa124 = Triangulation::faceTable[ st.f124 ];

  hij_k = EdgeHeight::At( e, fa123 );
  hij_l = EdgeHeight::At( e, fa124 );
  
  hijk_l = FaceHeight::At( fa123, t );
  hijl_k = FaceHeight::At( fa124, t );
 
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

  value = 0.5*(Hij_k * Hijk_l + Hij_l * Hijl_k);
}

void DualAreaSegment::remove() {
     deleteDependents();
     hij_k->removeDependent(this);
     hijk_l->removeDependent(this);
     hij_l->removeDependent(this);
     hijl_k->removeDependent(this);
     Index->erase(pos);
     delete this;
}

DualAreaSegment::~DualAreaSegment(){}

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
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void DualAreaSegment::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  DualAreaSegmentIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
