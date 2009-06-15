#ifndef LENGTH_H_
#define LENGTH_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "triangulation.h"

#include "edge.h"
#include "radius.cpp"
#include "eta.cpp"

class Length;
typedef map<TriPosition, Length*, TriPositionCompare> LengthIndex;

class Length : public virtual GeoQuant {
private:
  static LengthIndex* Index;
  Radius* radius1;
  Radius* radius2;
  Eta* eta;  

protected:
  Length( Edge& e );
  void recalculate();

public:
  ~Length();
  static Length* At( Edge& e );
  static void CleanUp();
};
LengthIndex* Length::Index = NULL;

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

#endif /* LENGTH_H_ */

