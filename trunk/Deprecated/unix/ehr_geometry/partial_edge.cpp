#include "partial_edge.h"
#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, PartialEdge*, TriPositionCompare> PartialEdgeIndex;
static PartialEdgeIndex* Index = NULL;

PartialEdge::PartialEdge( Vertex& v, Edge& e ){
  StdEdge st = labelEdge( e, v );  
  ri = Radius::At( v );
  rj = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  Lij = Length::At( e );

  ri->addDependent( this );
  rj->addDependent( this );
  Lij->addDependent( this );
}

void PartialEdge::recalculate(){
  double length = Lij->getValue();
  double radi = ri->getValue();
  double radj = rj->getValue();

  value = (length*length + radi*radi - radj*radj)/(2*length);
}

void PartialEdge::remove() {
    deleteDependents();
    Lij->removeDependent(this);
    ri->removeDependent(this);
    rj->removeDependent(this);
    Index->erase(pos);
    delete this;
}

PartialEdge::~PartialEdge(){}

PartialEdge* PartialEdge::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new PartialEdgeIndex();
  PartialEdgeIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    PartialEdge* val = new PartialEdge( v, e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void PartialEdge::CleanUp(){
  if( Index == NULL ) return;
  PartialEdgeIndex::iterator iter;
  PartialEdgeIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void PartialEdge::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  PartialEdgeIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
