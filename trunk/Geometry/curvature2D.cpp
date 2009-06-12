#ifndef TWODCURVATURE_H_
#define TWODCURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "euc_angle.cpp"
#include "triangulation/triangulation.h"
#include "simplex/vertex.h"

class Curvature2D;
typedef map<TriPosition, Curvature2D*, TriPositionCompare> Curvature2DIndex;

class Curvature2D : public virtual GeoQuant {
private:
  static Curvature2DIndex* Index;
  vector<GeoQuant*>* angles;

protected:
  Curvature2D( Vertex& v );
  void recalculate();

public:
  ~Curvature2D();
  static Curvature2D* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return Curvature2D::At(v)->getValue();
  }
  static void CleanUp();
};
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

Curvature2D::~Curvature2D(){ delete angles; }
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
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Curvature2D::CleanUp(){
  if( Index == NULL) return;
  Curvature2DIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* EDGECURVATURE_H_ */


