#ifndef TWODCURVATURE_H_
#define TWODCURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "geoquants.h"
#include "triangulation/triangulation.h"

Curvature2DIndex* Curvature2D::Index = NULL;

Curvature2D::Curvature2D( Vertex& v ){    
    angles = new vector<GeoQuant*>();

    GeoQuant* angle;
    for(int i = 0; i < v.getLocalFaces()->size(); i++) {
        angle = EuclideanAngle::At(v, Triangulation::faceTable[(*(v.getLocalFaces()))[i]]);
	    angle->addDependent(this);
	    angles->push_back( angle );
    }
}

void Curvature2D::remove() {
     deleteDependents();
     for(int ii = 0; ii < angles->size(); ii++){
       angles->at(ii)->removeDependent(this);
     }    
     Index->erase(pos);
     delete angles;
}

Curvature2D::~Curvature2D(){ remove(); }
void Curvature2D::recalculate() {
    double curv = 2*PI;
    GeoQuant* angle;
    for(int ii = 0; ii < angles->size(); ii++){
      angle = angles->at(ii);
      curv -= angle->getValue();
    }
    
    value = curv;
}

Curvature2D* Curvature2D::At( Vertex& v  ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new Curvature2DIndex();
  Curvature2DIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Curvature2D* val = new Curvature2D( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Curvature2D::CleanUp(){
  if( Index == NULL ) return;
  Curvature2DIndex::iterator iter;
  Curvature2DIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    delete iter->second;
  }
    
  delete Index;
  Index = NULL;
}

#endif /* TWODCURVATURE_H_ */


