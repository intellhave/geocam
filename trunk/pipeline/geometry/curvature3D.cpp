#include "curvature3D.h"

#include <stdio.h>

typedef map<TriPosition, Curvature3D*, TriPositionCompare> Curvature3DIndex;
static Curvature3DIndex* Index = NULL;

Curvature3D::Curvature3D( Vertex& v ){    
  edgeCurvs = new vector<GeoQuant*>();
  partials = new vector<GeoQuant*>();

  GeoQuant* edgeC;
  GeoQuant* partial;
    
  for(int i = 0; i < v.getLocalEdges()->size(); i++){
    edgeC = EdgeCurvature::At(Triangulation::edgeTable[(*(v.getLocalEdges()))[i]]);
    partial = PartialEdge::At(v, Triangulation::edgeTable[(*(v.getLocalEdges()))[i]]);
    edgeC->addDependent(this);
    partial->addDependent(this);
    edgeCurvs->push_back( edgeC );
    partials->push_back( partial );
  }
}

Curvature3D::~Curvature3D(){ delete edgeCurvs; delete partials;}

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
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Curvature3D::CleanUp(){
  if( Index == NULL) return;
  Curvature3DIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

void Curvature3D::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  Curvature3DIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
