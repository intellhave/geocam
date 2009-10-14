#include "total_volume_partial.h"

#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, TotalVolumePartial*, TriPositionCompare> TotalVolumePartialIndex;
static TotalVolumePartialIndex* Index = NULL;

TotalVolumePartial::TotalVolumePartial( Vertex& v ){
  vector<int>* tetraIndicies = v.getLocalTetras();

  face_heights = new vector<FaceHeight*>();
  areas = new vector<Area*>();

  FaceHeight* fh = NULL;
  Area* ar = NULL;

  for(int ii = 0; ii < tetraIndicies->size(); ii++){
    Tetra& t = Triangulation::tetraTable[tetraIndicies->at(ii)];
    StdTetra st = labelTetra( t, v );

    int faceInts[3];
    faceInts[0] = st.f123;
    faceInts[1] = st.f124;
    faceInts[2] = st.f134;

    for(int jj = 0; jj < 3; jj++){
      Face& f = Triangulation::faceTable[ faceInts[jj] ];

      fh = FaceHeight::At(f,t);
      fh->addDependent( this );
      face_heights->push_back( fh );

      ar = Area::At(f);
      ar->addDependent( this );
      areas->push_back( ar );
    }
  }
}

void TotalVolumePartial::remove() {
  deleteDependents();   
  for(int jj = 0; jj < face_heights->size(); jj++){
      face_heights->at(jj)->removeDependent(this);
      areas->at(jj)->removeDependent(this);
  }
  Index->erase(pos);
  delete face_heights;
  delete areas;
  delete this;
}

TotalVolumePartial::~TotalVolumePartial(){

}

void TotalVolumePartial::recalculate(){
  value = 0.0;

  FaceHeight* fh = NULL;
  Area* ar = NULL;

  for(int ii = 0; ii < face_heights->size(); ii++){
    fh = face_heights->at(ii);
    ar = areas->at(ii);
    value += fh->getValue() * ar->getValue();
  }

  value = (1.0/3.0) * value;
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
