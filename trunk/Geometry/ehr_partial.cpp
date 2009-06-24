#ifndef EHRPARTIAL_H_
#define EHRPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "geoquants.h"
#include "triposition.h"


EHRPartialIndex* EHRPartial::Index = NULL;

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

void EHRPartial::remove() {
     deleteDependents();
     totVolume->removeDependent(this);
     totCurvature->removeDependent(this);
     localCurvature->removeDependent(this);
     vps->removeDependent(this);
     Index->erase(pos);
}

EHRPartial::~EHRPartial(){ remove(); }

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
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EHRPartial::CleanUp(){
  if( Index == NULL ) return;
  EHRPartialIndex::iterator iter;
  EHRPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    delete iter->second;
  }
    
  delete Index;
  Index = NULL;
}

#endif /* EHRPARTIAL_H_ */

