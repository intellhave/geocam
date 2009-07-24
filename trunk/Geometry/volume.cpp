#include "volume.h"
#include "miscmath.h"

#include <stdio.h>

typedef map<TriPosition, Volume*, TriPositionCompare> VolumeIndex;
static VolumeIndex* Index = NULL;

Volume::Volume( Tetra& t ){
  StdTetra st = labelTetra( t );
    
  len[0] = Length::At( Triangulation::edgeTable[ st.e12 ] );
  len[1] = Length::At( Triangulation::edgeTable[ st.e13 ] );
  len[2] = Length::At( Triangulation::edgeTable[ st.e14 ] );
  len[3] = Length::At( Triangulation::edgeTable[ st.e23 ] );
  len[4] = Length::At( Triangulation::edgeTable[ st.e24 ] );
  len[5] = Length::At( Triangulation::edgeTable[ st.e34 ] );
   
  for(int i = 0; i < 6; i++) len[i]->addDependent( this );        
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

void Volume::remove() {
   deleteDependents();
   len[0]->removeDependent(this);
   len[1]->removeDependent(this);
   len[2]->removeDependent(this);
   len[3]->removeDependent(this);
   len[4]->removeDependent(this);
   len[5]->removeDependent(this);
   Index->erase(pos);
   delete this;
}

Volume::~Volume(){}

Volume* Volume::At( Tetra& t ){
  TriPosition T( 1, t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeIndex();
  VolumeIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Volume* val = new Volume( t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Volume::CleanUp(){
  if( Index == NULL ) return;
  VolumeIndex::iterator iter;
  VolumeIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void Volume::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  VolumeIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}

