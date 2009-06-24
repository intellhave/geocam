#ifndef EDGECURVATURE_H_
#define EDGECURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"
#include "triangulation/triangulation.h"

EdgeCurvatureIndex* EdgeCurvature::Index = NULL;

EdgeCurvature::EdgeCurvature( Edge& e  ){    
    dih_angles = new vector<GeoQuant*>();

    GeoQuant* dih_angle;
    for(int i = 0; i < e.getLocalTetras()->size(); i++) {
        dih_angle = DihedralAngle::At(e, Triangulation::tetraTable[(*(e.getLocalTetras()))[i]]);
	    dih_angle->addDependent(this);
	    dih_angles->push_back( dih_angle );
    } 
}

void EdgeCurvature::remove() {
     deleteDependents();
     for(int ii = 0; ii < dih_angles->size(); ii++){
       dih_angles->at(ii)->removeDependent(this);
     }
     Index->erase(pos);
     delete dih_angles;
}

EdgeCurvature::~EdgeCurvature(){ remove(); }
void EdgeCurvature::recalculate() {
    double curv = 2*PI;
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
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EdgeCurvature::CleanUp(){
  if( Index == NULL ) return;
  EdgeCurvatureIndex::iterator iter;
  EdgeCurvatureIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    delete iter->second;
  }
    
  delete Index;
  Index = NULL;
}

#endif /* EDGECURVATURE_H_ */


