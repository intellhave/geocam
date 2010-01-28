#include "total_volume_second_partial.h"

#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, TotalVolumeSecondPartial*, TriPositionCompare> TotalVolumeSecondPartialIndex;
static TotalVolumeSecondPartialIndex* Index = NULL;

TotalVolumeSecondPartial::TotalVolumeSecondPartial( Vertex& v, Edge& e ){
  /* If v is in e, tetra_list = e.localTetras.
   * If v and e form a triangle, tetra_list =/= empty.
   * If v and e are not relates, tetra_list = empty => value = 0.
   */
  vector<int> tetra_list = listIntersection(v.getLocalTetras(), e.getLocalTetras());
  
  volume_partials = new vector<VolumeSecondPartial*>();
  VolumeSecondPartial* vp = NULL;

  for(int ii = 0; ii < tetra_list.size(); ii++){
    Tetra& t = Triangulation::tetraTable[tetra_list[ii]];
    
    vp = VolumeSecondPartial::At( v, e, t);
    vp->addDependent(this);
    volume_partials->push_back( vp );
  }
}

void TotalVolumeSecondPartial::remove() {
  deleteDependents();   
  for(int jj = 0; jj < volume_partials->size(); jj++) {
      volume_partials->at(jj)->removeDependent(this);
  }
  Index->erase(pos);
  delete volume_partials;
  delete this;
}

TotalVolumeSecondPartial::~TotalVolumeSecondPartial(){

}

void TotalVolumeSecondPartial::recalculate(){
   double result = 0;
   for(int i = 0; i < volume_partials->size(); i++) {
           result += volume_partials->at(i)->getValue();
   }
   value = result;
}

TotalVolumeSecondPartial* TotalVolumeSecondPartial::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new TotalVolumeSecondPartialIndex();
  TotalVolumeSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    TotalVolumeSecondPartial* val = new TotalVolumeSecondPartial( v, e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}


void TotalVolumeSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  TotalVolumeSecondPartialIndex::iterator iter;
  TotalVolumeSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}
