#include "ehr_partial.h"

#include <stdio.h>

typedef map<TriPosition, EHRPartial*, TriPositionCompare> EHRPartialIndex;
static EHRPartialIndex* Index = NULL;

EHRPartial::EHRPartial( Vertex& v ){
  totVolume = TotalVolume::At();
  totVolume->addDependent( this );

  totCurvature = TotalCurvature::At();
  totCurvature->addDependent( this );

  localCurvature = Curvature3D::At( v );
  localCurvature->addDependent( this );

  vps = VolumePartialSum::At( v );
  vps->addDependent( this );
}

EHRPartial::~EHRPartial(){}

void EHRPartial::recalculate(){
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double K = localCurvature->getValue();
  double VPsum = vps->getValue();
  
  value = pow(totV, -4.0/3.0)*(K*totV - totK*VPsum/3.0); 
}

EHRPartial* EHRPartial::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new EHRPartialIndex();
  EHRPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EHRPartial* val = new EHRPartial( v );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EHRPartial::CleanUp(){
  if( Index == NULL ) return;
  EHRPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}
