#include "length.h"

#include <stdio.h>

typedef map<TriPosition, Length*, TriPositionCompare> LengthIndex;
static LengthIndex* Index = NULL;

Length::Length( Edge& e ){
  Vertex v1 = Triangulation::vertexTable[ (*(e.getLocalVertices()))[0] ];
  Vertex v2 = Triangulation::vertexTable[ (*(e.getLocalVertices()))[1] ];

  radius1 = Radius::At( v1 );
  radius2 = Radius::At( v2 );
  eta = Eta::At( e );
  
  radius1->addDependent( this );
  radius2->addDependent( this );
  eta->addDependent( this );
}

void Length::recalculate(){
  double r1 = radius1->getValue();
  double r2 = radius2->getValue();
  double etaV = eta->getValue();

  value = sqrt( r1*r1 + r2*r2 + 2*r1*r2*etaV );   
}

Length::~Length(){}

Length* Length::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new LengthIndex();
  LengthIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Length* val = new Length( e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Length::CleanUp(){
  if( Index == NULL) return;
  LengthIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

void Length::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  LengthIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
