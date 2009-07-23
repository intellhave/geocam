#include "edge_height.h"
#include "miscmath.h"

#include <stdio.h>

typedef map<TriPosition, EdgeHeight*, TriPositionCompare> EdgeHeightIndex;
static EdgeHeightIndex* Index = NULL;

EdgeHeight::EdgeHeight( Edge& e, Face& f ){
  StdFace sf = labelFace( f, e );
  
  Vertex& vert1 = Triangulation::vertexTable[ sf.v1 ];
  Edge& ed13 = Triangulation::edgeTable[ sf.e13 ]; 
  
  d_ij = PartialEdge::At( vert1, e ); // Note e = edge_12
  d_ik = PartialEdge::At( vert1, ed13 );
  theta_i = EuclideanAngle::At( vert1, f );

  d_ij->addDependent( this );
  d_ik->addDependent( this );
  theta_i->addDependent( this );
}

void EdgeHeight::recalculate(){
  double dij = d_ij->getValue();
  double dik = d_ik->getValue();
  double theta = theta_i->getValue();

  value = (dik - dij * cos(theta))/sin(theta);
}

EdgeHeight::~EdgeHeight(){}

EdgeHeight* EdgeHeight::At( Edge& e, Face& f ){
  TriPosition T( 2, e.getSerialNumber(), f.getSerialNumber() );
  if( Index == NULL ) Index = new EdgeHeightIndex();
  EdgeHeightIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EdgeHeight* val = new EdgeHeight( e, f );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EdgeHeight::CleanUp(){
  if( Index == NULL ) return;
  EdgeHeightIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

void EdgeHeight::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  EdgeHeightIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
