#ifndef THREEDCURVATURE_H_
#define THREEDCURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"
#include "triangulation/triangulation.h"

Curvature3DIndex* Curvature3D::Index = NULL;

Curvature3D::Curvature3D( Vertex& v  ){    
    edgeCurvs = new vector<GeoQuant*>();
    partials = new vector<GeoQuant*>();

    GeoQuant* edgeC;
    GeoQuant* partial;
    
    for(int i = 0; i < v.getLocalEdges()->size(); i++)
    {
        edgeC = EdgeCurvature::At(Triangulation::edgeTable[(*(v.getLocalEdges()))[i]]);
        partial = PartialEdge::At(v, Triangulation::edgeTable[(*(v.getLocalEdges()))[i]]);
	    edgeC->addDependent(this);
	    partial->addDependent(this);
	    edgeCurvs->push_back( edgeC );
	    partials->push_back( partial );
    }
}

void Curvature3D::remove() {
     deleteDependents();
     for(int ii = 0; ii < edgeCurvs->size(); ii++){
       edgeCurvs->at(ii)->removeDependent(this);
       partials->at(ii)->removeDependent(this);
     }    
     Index->erase(pos);
     delete edgeCurvs;
     delete partials;
     delete this;
}

Curvature3D::~Curvature3D(){}
void Curvature3D::recalculate() {
    double curv = 0; 
    GeoQuant* edgeC;
    GeoQuant* partial;
    for(int ii = 0; ii < edgeCurvs->size(); ii++){
      edgeC = edgeCurvs->at(ii);
      partial = partials->at(ii);
      curv += edgeC->getValue()* partial->getValue();
    }
    
    value = curv;
}

Curvature3D* Curvature3D::At( Vertex& v  ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new Curvature3DIndex();
  Curvature3DIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Curvature3D* val = new Curvature3D( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}


void Curvature3D::CleanUp(){
  if( Index == NULL ) return;
  Curvature3DIndex::iterator iter;
  Curvature3DIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

#endif /* THREEDCURVATURE_H_ */


