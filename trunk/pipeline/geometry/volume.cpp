#ifndef VOLUME_H_
#define VOLUME_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "length.cpp"

#include "triangulation.h"
#include "miscmath.h"

class Volume;
typedef map<TriPosition, Volume*, TriPositionCompare> VolumeIndex;

class Volume : public virtual GeoQuant {
private:
  static VolumeIndex* Index;
  Length* len[6];

protected:
  Volume( Tetra& t );
  void recalculate();

public:
  ~Volume();
  static Volume* At( Tetra& t );
  static void CleanUp();
};
VolumeIndex* Volume::Index = NULL;

Volume::Volume( Tetra& t ){
  StdTetra st = labelTetra( t );
    
  len[0] = Length::At( st.e12 );
  len[1] = Length::At( st.e13 );
  len[2] = Length::At( st.e14 );
  len[3] = Length::At( st.e23 );
  len[4] = Length::At( st.e24 );
  len[5] = Length::At( st.e34 );
   
  for(int i = 0; i < 6; i++)
    len[i]->addDependent( this );        
}

void Volume::recalculate(){
  double CayleyMenger;
  double L12 = len[0]->getValue();
  double L13 = len[1]->getValue();
  double L14 = len[2]->getValue();
  double L23 = len[3]->getValue();
  double L24 = len[4]->getValue();
  double L34 = len[5]->getValue();
   
  CayleyMenger = (-1)*( pow(L12, 4.0)*pow(L34,2.0) + pow(L13, 4.0)*pow(L24,2.0) 
		 + pow(L14, 4.0)*pow(L23,2.0) + pow(L23, 4.0)*pow(L14,2.0)
		 + pow(L24, 4.0)*pow(L13,2.0) + pow(L34, 4.0)*pow(L12,2.0) );

  CayleyMenger += (-1)*( pow(L12, 2.0)*pow(L13,2.0)*pow(L23,2.0) 
			 + pow(L12, 2.0)*pow(L14,2.0)*pow(L24,2.0)
			 + pow(L13, 2.0)*pow(L14,2.0)*pow(L34,2.0)
			 + pow(L23, 2.0)*pow(L24,2.0)*pow(L34,2.0) );

  CayleyMenger += pow(L12, 2.0)*pow(L13,2.0)*pow(L24,2.0) 
                         + pow(L12, 2.0)*pow(L13,2.0)*pow(L34,2.0)
                         + pow(L12, 2.0)*pow(L14,2.0)*pow(L23,2.0) 
                         + pow(L12, 2.0)*pow(L14,2.0)*pow(L34,2.0);

  CayleyMenger += pow(L13, 2.0)*pow(L14,2.0)*pow(L23,2.0) 
                         + pow(L13, 2.0)*pow(L14,2.0)*pow(L24,2.0)
                         + pow(L13, 2.0)*pow(L23,2.0)*pow(L24,2.0)
                         + pow(L14, 2.0)*pow(L23,2.0)*pow(L24,2.0);

  CayleyMenger += pow(L12, 2.0)*pow(L23,2.0)*pow(L34,2.0)
                         + pow(L14, 2.0)*pow(L23,2.0)*pow(L34,2.0) 
                         + pow(L12, 2.0)*pow(L24,2.0)*pow(L34,2.0) 
                         + pow(L13, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   
  value = sqrt(CayleyMenger / 144.0);
}

Volume::~Volume(){}

Volume* Volume::At( Tetra& t ){
  TriPosition T( 1, t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeIndex();
  VolumeIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Volume* val = new Volume( t );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Volume::CleanUp(){
  if( Index == NULL ) return;
  VolumeIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* VOLUME_H_ */
