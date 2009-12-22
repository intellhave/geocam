#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "curvature2Dwneg.h"
#include "euc_angle.h"
#include "triposition.h"
#include "triangulation.h"
#include "simplex.h"
#include "face.h"
#include "vertex.h"

typedef map<TriPosition, Curvature2Dwneg*, TriPositionCompare> Curvature2DwnegIndex;
static Curvature2DwnegIndex* Index = NULL;

Curvature2Dwneg::Curvature2Dwneg( Vertex& v ){    
    angles = new vector<GeoQuant*>();

    GeoQuant* angle;
    for(int i = 0; i < v.getLocalFaces()->size(); i++) {
        angle = EuclideanAngle::At(v, Triangulation::faceTable[(*(v.getLocalFaces()))[i]]);
	    angle->addDependent(this);
	    angles->push_back( angle );
    }
}

void Curvature2Dwneg::remove() {
     deleteDependents();
     for(int ii = 0; ii < angles->size(); ii++){
       angles->at(ii)->removeDependent(this);
     }    
     Index->erase(pos);
     delete angles;
     delete this;
}

Curvature2Dwneg::~Curvature2Dwneg(){ }
void Curvature2Dwneg::recalculate() {
    double curv = 0;
    GeoQuant* angle;
    for(int ii = 0; ii < angles->size(); ii++){
      angle = angles->at(ii);
      curv -= angle->getValue();
    }
    
    value = curv;
}

Curvature2Dwneg* Curvature2Dwneg::At( Vertex& v  ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new Curvature2DwnegIndex();
  Curvature2DwnegIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Curvature2Dwneg* val = new Curvature2Dwneg( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Curvature2Dwneg::CleanUp(){
  if( Index == NULL ) return;
  Curvature2DwnegIndex::iterator iter;
  Curvature2DwnegIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}



