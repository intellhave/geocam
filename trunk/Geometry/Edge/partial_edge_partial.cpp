#include "partial_edge_partial.h"
#include "miscmath.h"

#include <stdio.h>

typedef map<TriPosition, PartialEdgePartial*, TriPositionCompare> PartialEdgePartialIndex;
static PartialEdgePartialIndex* Index = NULL;

PartialEdgePartial::PartialEdgePartial( Vertex& v, Edge& e ){
  StdEdge st = labelEdge( e, v );
  ri = Radius::At( v );
  rj = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  ai = Alpha::At( v );
  aj = Alpha::At( Triangulation::vertexTable[ st.v2 ] );
  eij = Eta::At(e);

  ri->addDependent( this );
  rj->addDependent( this );
  ai->addDependent( this );
  aj->addDependent( this );
  eij->addDependent(this);
}

void PartialEdgePartial::recalculate(){
  double radi = ri->getValue();
  double radj = rj->getValue();
  double alphi = ai->getValue();
  double alphj = aj->getValue();
  double eta = eij->getValue();

  value = radi * pow(radj, 2) * (eta * radi + alphj * radj) 
     / pow(alphi * pow(radi, 2) + alphj * pow(radj, 2) + 2*eta*radi*radj, 1.5);
  
}

void PartialEdgePartial::remove() {
    deleteDependents();
    ri->removeDependent(this);
    rj->removeDependent(this);
    ai->removeDependent(this);
    aj->removeDependent(this);
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
