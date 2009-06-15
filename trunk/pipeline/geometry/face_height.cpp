#ifndef FACEHEIGHT_H_
#define FACEHEIGHT_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

class FaceHeight;
typedef map<TriPosition, FaceHeight*, TriPositionCompare> FaceHeightIndex;

class FaceHeight : public virtual GeoQuant {
private:
  static FaceHeightIndex* Index;
  EdgeHeight* hij_l;
  EdgeHeight* hij_k;
  DihedralAngle* beta_ij_kl;

protected:
  FaceHeight( Face& f, Tetra& t );
  void recalculate();

public:
  ~FaceHeight();
  static FaceHeight* At( Face& f, Tetra& t );
  static void CleanUp();
};
FaceHeightIndex* FaceHeight::Index = NULL;

FaceHeight::FaceHeight( Face& f, Tetra& t ){
  StdTetra st = labelTetra( f, t );
  
  


  hij_l = EdgeHeight::At(st.e12, st.f123);
  hij_k = EdgeHeight::At(st.e

}

void FaceHeight::recalculate(){}

FaceHeight::~FaceHeight(){}

FaceHeight* FaceHeight::At( Face& f, Tetra& t ){
  TriPosition T( 2, f.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new FaceHeightIndex();
  FaceHeightIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    FaceHeight* val = new FaceHeight( SIMPLICES );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void FaceHeight::CleanUp(){
  if( Index == NULL ) return;
  FaceHeightIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* FACEHEIGHT_H_ */

