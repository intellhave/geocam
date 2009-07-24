#include "face_height.h"
#include "miscmath.h"

#include <cstdio>

class FaceHeight;
typedef map<TriPosition, FaceHeight*, TriPositionCompare> FaceHeightIndex;
FaceHeightIndex* Index = NULL;

FaceHeight::FaceHeight( Face& f, Tetra& t ){
  StdTetra st = labelTetra( t, f );
  
  Edge& ed12 = Triangulation::edgeTable[ st.e12 ];
  Face& fa123 = Triangulation::faceTable[ st.f123 ];
  Face& fa124 = Triangulation::faceTable[ st.f124 ];

  hij_k = EdgeHeight::At( ed12, fa123 );
  hij_l = EdgeHeight::At( ed12, fa124 );

  beta_ij_kl = DihedralAngle::At( ed12, t);

  hij_l->addDependent(this);
  hij_k->addDependent(this);
  beta_ij_kl->addDependent(this);
}

void FaceHeight::recalculate(){
  double Hij_l = hij_l->getValue();
  double Hij_k = hij_k->getValue();
  double angle = beta_ij_kl->getValue();

  value = (Hij_l - Hij_k * cos(angle))/sin(angle);
}

void FaceHeight::remove() {
     deleteDependents();
     hij_l->removeDependent(this);
     hij_k->removeDependent(this);
     beta_ij_kl->removeDependent(this);
     Index->erase(pos);  
     delete this;
}


FaceHeight::~FaceHeight(){}

FaceHeight* FaceHeight::At( Face& f, Tetra& t ){
  TriPosition T( 2, f.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new FaceHeightIndex();
  FaceHeightIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    FaceHeight* val = new FaceHeight( f, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void FaceHeight::CleanUp(){
  if( Index == NULL ) return;
  FaceHeightIndex::iterator iter;
  FaceHeightIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void FaceHeight::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  FaceHeightIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
