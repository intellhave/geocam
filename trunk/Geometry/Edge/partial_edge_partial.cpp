#include "partial_edge_partial.h"
#include "miscmath.h"

#include <stdio.h>

typedef map<TriPosition, PartialEdgePartial*, TriPositionCompare> PartialEdgePartialIndex;
static PartialEdgePartialIndex* Index = NULL;

PartialEdgePartial::PartialEdgePartial( Vertex& v, Edge& e ){
  StdEdge st = labelEdge( e, v );
  ri = Radius::At( v );
  rj = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  Lij = Length::At( e );
  eij = Eta::At(e);

  ri->addDependent( this );
  rj->addDependent( this );
  Lij->addDependent( this );
  eij->addDependent(this);
}

void PartialEdgePartial::recalculate(){
  double length = Lij->getValue();
  double radi = ri->getValue();
  double radj = rj->getValue();
  double eta = eij->getValue();

  value = radi * radj * (length * length - radi * radi - radi * radj * eta) 
               / (length * length * length);
  
}

void PartialEdgePartial::remove() {
    deleteDependents();
    Lij->removeDependent(this);
    ri->removeDependent(this);
    rj->removeDependent(this);
    eij->removeDependent(this);
    Index->erase(pos);
    delete this;
}

PartialEdgePartial::~PartialEdgePartial(){}

PartialEdgePartial* PartialEdgePartial::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new PartialEdgePartialIndex();
  PartialEdgePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    PartialEdgePartial* val = new PartialEdgePartial( v, e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void PartialEdgePartial::CleanUp(){
  if( Index == NULL ) return;
  PartialEdgePartialIndex::iterator iter;
  PartialEdgePartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void PartialEdgePartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  PartialEdgePartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
