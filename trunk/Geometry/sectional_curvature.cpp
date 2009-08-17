#include "sectional_curvature.h"

#include <stdio.h>

//static const double PI = 3.1415926;

class SectionalCurvature;
typedef map<TriPosition, SectionalCurvature*, TriPositionCompare> SectionalCurvatureIndex;
static SectionalCurvatureIndex* Index = NULL;

SectionalCurvature::SectionalCurvature( Edge& e  ){    
  dih_angles = new vector<GeoQuant*>();

  GeoQuant* dih_angle;
  for(int i = 0; i < e.getLocalTetras()->size(); i++) {
    dih_angle = DihedralAngle::At(e, Triangulation::tetraTable[(*(e.getLocalTetras()))[i]]);
    dih_angle->addDependent(this);
    dih_angles->push_back( dih_angle );
  } 
}

void SectionalCurvature::remove() {
     deleteDependents();
     for(int ii = 0; ii < dih_angles->size(); ii++){
       dih_angles->at(ii)->removeDependent(this);
     }
     Index->erase(pos);
     delete dih_angles;
     delete this;
}

SectionalCurvature::~SectionalCurvature(){ }

void SectionalCurvature::recalculate() {
    double curv = 2 * PI;
    GeoQuant* dih_angle;
    for(int ii = 0; ii < dih_angles->size(); ii++){
      dih_angle = dih_angles->at(ii);
      curv -= dih_angle->getValue();
    }
    
    value = curv;
}

SectionalCurvature* SectionalCurvature::At( Edge& e  ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new SectionalCurvatureIndex();
  SectionalCurvatureIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    SectionalCurvature* val = new SectionalCurvature( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void SectionalCurvature::CleanUp(){
  if( Index == NULL ) return;
  SectionalCurvatureIndex::iterator iter;
  SectionalCurvatureIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void SectionalCurvature::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  SectionalCurvatureIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
