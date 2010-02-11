#include "partial_edge_second_partial.h"
#include "miscmath.h"

#include <stdio.h>

typedef map<TriPosition, PartialEdgeSecondPartial*, TriPositionCompare> PartialEdgeSecondPartialIndex;
static PartialEdgeSecondPartialIndex* Index = NULL;

PartialEdgeSecondPartial::PartialEdgeSecondPartial( Vertex& v, Edge& e ){
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

void PartialEdgeSecondPartial::recalculate(){
  double r1 = ri->getValue();
  double r2 = rj->getValue();
  double alpha1 = ai->getValue();
  double alpha2 = aj->getValue();
  double Eta12 = eij->getValue();

  value = (pow(r1,2)*pow(r2,2)*(alpha1*pow(r1,2) - r2*(Eta12*r1 + 2*alpha2*r2)))/
   pow(alpha1*pow(r1,2) + r2*(2*Eta12*r1 + alpha2*r2),2.5);
  
}

void PartialEdgeSecondPartial::remove() {
    deleteDependents();
    ri->removeDependent(this);
    rj->removeDependent(this);
    ai->removeDependent(this);
    aj->removeDependent(this);
    eij->removeDependent(this);
    Index->erase(pos);
    delete this;
}

PartialEdgeSecondPartial::~PartialEdgeSecondPartial(){}

PartialEdgeSecondPartial* PartialEdgeSecondPartial::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new PartialEdgeSecondPartialIndex();
  PartialEdgeSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    PartialEdgeSecondPartial* val = new PartialEdgeSecondPartial( v, e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void PartialEdgeSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  PartialEdgeSecondPartialIndex::iterator iter;
  PartialEdgeSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void PartialEdgeSecondPartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  PartialEdgeSecondPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
