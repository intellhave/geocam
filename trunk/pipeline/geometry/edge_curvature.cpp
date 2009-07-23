#include "edge_curvature.h"

#include <stdio.h>

static const double PI = 3.1415926;

class EdgeCurvature;
typedef map<TriPosition, EdgeCurvature*, TriPositionCompare> EdgeCurvatureIndex;
static EdgeCurvatureIndex* Index = NULL;

EdgeCurvature::EdgeCurvature( Edge& e  ){    
  dih_angles = new vector<GeoQuant*>();

  GeoQuant* dih_angle;
  for(int i = 0; i < e.getLocalTetras()->size(); i++) {
    dih_angle = DihedralAngle::At(e, Triangulation::tetraTable[(*(e.getLocalTetras()))[i]]);
    dih_angle->addDependent(this);
    dih_angles->push_back( dih_angle );
  } 
}

EdgeCurvature::~EdgeCurvature(){ delete dih_angles; }

void EdgeCurvature::recalculate() {
    double curv = 2 * PI;
    GeoQuant* dih_angle;
    for(int ii = 0; ii < dih_angles->size(); ii++){
      dih_angle = dih_angles->at(ii);
      curv -= dih_angle->getValue();
    }
    
    value = curv;
}

EdgeCurvature* EdgeCurvature::At( Edge& e  ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new EdgeCurvatureIndex();
  EdgeCurvatureIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EdgeCurvature* val = new EdgeCurvature( e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EdgeCurvature::CleanUp(){
  if( Index == NULL) return;
  EdgeCurvatureIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

void EdgeCurvature::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  EdgeCurvatureIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
