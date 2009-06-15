#ifndef LENGTH_CPP_
#define LENGTH_CPP_

#include <new>
#include <cmath>

#include "triangulation.h"

#include "geoquant.h"
#include "vertex.h"
#include "edge.h"

#include "radius.cpp"
#include "eta.cpp"

static GQIndex* Index = NULL;

class Length : public GeoQuant {
protected:
  TriPosition position;
  GeoQuant* radius1;
  GeoQuant* radius2;
  GeoQuant* eta;  
  Length( Edge& e );
  void recalculate();

public:
  static GeoQuant* At( Edge& e );
  static void CleanUp();
};

Length::Length( Edge& e ) : GeoQuant() {
  position = TriPosition( 1, e.getSerialNumber() );
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

GeoQuant* Length::At( Edge& e ){
  GeoQuant* retval;
  if(Index == NULL) Index = new GQIndex();

  TriPosition t(1, e.getSerialNumber());
  GQIndex::iterator iter = Index->find( t );
  if( iter == Index->end() ){
    retval = new Length( e );
    Index->insert( make_pair( t, retval ) );
  } else {
    retval = iter->second;
  }
  
  return retval;
}

void Length::CleanUp(){
  GQIndex::iterator it;
  for(it = Index->begin(); it != Index->end(); it++)
    delete it->second;
  
  delete Index;
}

#endif /* LENGTH_CPP_ */
