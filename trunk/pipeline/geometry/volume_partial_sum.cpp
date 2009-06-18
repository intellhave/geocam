#ifndef VOLUMEPARTIALSUM_H_
#define VOLUMEPARTIALSUM_H_

#include <vector>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "volume_partial.cpp"

class VolumePartialSum;
typedef map<TriPosition, VolumePartialSum*, TriPositionCompare> VolumePartialSumIndex;

class VolumePartialSum : public virtual GeoQuant {
private:
  static VolumePartialSumIndex* Index;
  vector<VolumePartial*>* volPartials;

protected:
  VolumePartialSum( Vertex& v );
  void recalculate();

public:
  ~VolumePartialSum();
  static VolumePartialSum* At( Vertex& v );
  static void CleanUp();
};
VolumePartialSumIndex* VolumePartialSum::Index = NULL;

VolumePartialSum::VolumePartialSum( Vertex& v ){
  volPartials = new vector<VolumePartial*>();
  vector<int>* localTetras = v.getLocalTetras();
  
  int tetraIndex; 
  VolumePartial* vp;
  
  for(int ii = 0; ii < localTetras->size(); ii++){
    tetraIndex = localTetras->at(ii);
    Tetra& t = Triangulation::tetraTable[ tetraIndex ];
    vp = VolumePartial::At( v, t );
    vp->addDependent( this );
    volPartials->push_back( vp );
  }
}

void VolumePartialSum::recalculate(){
  value = 0.0;
  VolumePartial* vp;
  for(int ii = 0; ii < volPartials->size(); ii++){
    vp = volPartials->at(ii);
    value += vp->getValue();
  }
}

VolumePartialSum::~VolumePartialSum(){
  delete volPartials;
}

VolumePartialSum* VolumePartialSum::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new VolumePartialSumIndex();
  VolumePartialSumIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumePartialSum* val = new VolumePartialSum( v );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void VolumePartialSum::CleanUp(){
  if( Index == NULL ) return;
  VolumePartialSumIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* VOLUMEPARTIALSUM_H_ */

