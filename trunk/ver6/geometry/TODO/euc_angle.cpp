#ifndef EUCANGLE_CPP_
#define EUCANGLE_CPP_

#include <cmath>
#include <cstdio>

#include "miscmath.h"

#include "triangulation.h"
#include "vertex.h"
#include "face.h"

#include "geoquant.h"
#include "length.cpp"

class EuclideanAngle : public virtual GeoQuant {
private:
  TriPosition position;
  static GQIndex* Index;

  GeoQuant* lengthA;
  GeoQuant* lengthB;
  GeoQuant* lengthC;
  
protected:
  EuclideanAngle( Vertex& v, Face& f );
  void recalculate();

public:
  static GeoQuant* At( Vertex& v,  Face& f );
  static void CleanUp();
};

EuclideanAngle::EuclideanAngle( Vertex& v, Face& f ) : GeoQuant(){
  position = TriPosition(2, v.getSerialNumber(), f.getSerialNumber());
    
  vector<int> sameAs, diff;
  sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
  diff = listDifference(f.getLocalEdges(), v.getLocalEdges());

  Edge e1 = Triangulation::edgeTable[sameAs[0]];
  Edge e2 = Triangulation::edgeTable[sameAs[1]];
  Edge e3 = Triangulation::edgeTable[diff[0]];
  
  lengthA = Length::At( e1 );
  lengthB = Length::At( e2 );
  lengthC = Length::At( e3 );

  lengthA->addDependent(this);
  lengthB->addDependent(this);
  lengthC->addDependent(this);
}

void EuclideanAngle::recalculate(){
  double a = lengthA->getValue();
  double b = lengthB->getValue();
  double c = lengthC->getValue();
  value = acos((a*a + b*b - c*c)/ (2*a*b));
}

GeoQuant* EuclideanAngle::At( Vertex& v, Face& f ){
  GeoQuant* retval;
  if(Index == NULL) Index = new GQIndex();

  TriPosition t(2, v.getSerialNumber(), f.getSerialNumber());
  GQIndex::iterator iter = Index->find( t );
  if( iter == Index->end() ){
    retval = new EuclideanAngle( v, f );
    Index->insert( make_pair( t, retval ) );
  } else {
    retval = iter->second;
  }
  
  return retval;
}

void EuclideanAngle::CleanUp(){
  GQIndex::iterator it;
  for(it = Index->begin(); it != Index->end(); it++)
    delete it->second;
  
  delete Index;
}

#endif /* EUCANGLE_CPP_ */
