#include "curvature3D.h"

#include <stdio.h>

typedef map<TriPosition, Curvature3D*, TriPositionCompare> Curvature3DIndex;
static Curvature3DIndex* Index = NULL;

Curvature3D::Curvature3D( Vertex& v ){    
  sectionalCurvs = new vector<GeoQuant*>();
  partials = new vector<GeoQuant*>();

  GeoQuant* sectC;
  GeoQuant* partial;
    
  for(int i = 0; i < v.getLocalEdges()->size(); i++){
    sectC = SectionalCurvature::At(Triangulation::edgeTable[(*(v.getLocalEdges()))[i]]);
    partial = PartialEdge::At(v, Triangulation::edgeTable[(*(v.getLocalEdges()))[i]]);
    sectC->addDependent(this);
    partial->addDependent(this);
    sectionalCurvs->push_back( sectC );
    partials->push_back( partial );
  }
}

void Curvature3D::recalculate() {
  double curv = 0; 
  GeoQuant* sectC;
  GeoQuant* partial;
  for(int ii = 0; ii < sectionalCurvs->size(); ii++){
    sectC = sectionalCurvs->at(ii);
    partial = partials->at(ii);
    curv += sectC->getValue()* partial->getValue();
  }
    
  value = curv;
}

Curvature3D::~Curvature3D() {}

void Curvature3D::remove() {
     deleteDependents();
     for(int ii = 0; ii < sectionalCurvs->size(); ii++){
       sectionalCurvs->at(ii)->removeDependent(this);
       partials->at(ii)->removeDependent(this);
     }    
     Index->erase(pos);
     delete sectionalCurvs;
     delete partials;
     delete this;
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

void Curvature3D::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  Curvature3DIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
