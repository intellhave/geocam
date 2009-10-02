#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "curvature2D.h"
#include "euc_angle.h"
#include "triposition.h"
#include "triangulation.h"

typedef map<TriPosition, Curvature2D*, TriPositionCompare> Curvature2DIndex;
static Curvature2DIndex* Index = NULL;

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
     delete this;
}

Curvature2D::~Curvature2D(){ }
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
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}



