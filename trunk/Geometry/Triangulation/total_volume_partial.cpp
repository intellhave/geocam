#include "total_volume_partial.h"

#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, TotalVolumePartial*, TriPositionCompare> TotalVolumePartialIndex;
static TotalVolumePartialIndex* Index = NULL;

TotalVolumePartial::TotalVolumePartial( Vertex& v ){
  wrtRadius = true;
  vector<int>* tetraIndicies = v.getLocalTetras();

  volume_partials = new vector<VolumePartial*>();
  VolumePartial* vp = NULL;

  for(int ii = 0; ii < tetraIndicies->size(); ii++){
    Tetra& t = Triangulation::tetraTable[tetraIndicies->at(ii)];
    
    vp = VolumePartial::At( v, t);
    vp->addDependent(this);
    volume_partials->push_back( vp );
  }
}

TotalVolumePartial::TotalVolumePartial( Edge& e ){
  wrtRadius = false;
  
  vector<int>* tetraIndicies = e.getLocalTetras();

  volume_partials = new vector<VolumePartial*>();
  VolumePartial* vp = NULL;

  for(int ii = 0; ii < tetraIndicies->size(); ii++){
    Tetra& t = Triangulation::tetraTable[tetraIndicies->at(ii)];
    
    vp = VolumePartial::At( e, t);
    vp->addDependent(this);
    volume_partials->push_back( vp );
  }
}

void TotalVolumePartial::remove() {
  deleteDependents();   
  for(int jj = 0; jj < volume_partials->size(); jj++) {
      volume_partials->at(jj)->removeDependent(this);
  }
  Index->erase(pos);
  delete volume_partials;
  delete this;
}

TotalVolumePartial::~TotalVolumePartial(){

}

void TotalVolumePartial::recalculate(){
   double result = 0;
   for(int i = 0; i < volume_partials->size(); i++) {
           result += volume_partials->at(i)->getValue();
   }
   value = result;
}

TotalVolumePartial* TotalVolumePartial::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new TotalVolumePartialIndex();
  TotalVolumePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    TotalVolumePartial* val = new TotalVolumePartial( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

TotalVolumePartial* TotalVolumePartial::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new TotalVolumePartialIndex();
  TotalVolumePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    TotalVolumePartial* val = new TotalVolumePartial( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void TotalVolumePartial::CleanUp(){
  if( Index == NULL ) return;
  TotalVolumePartialIndex::iterator iter;
  TotalVolumePartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}
