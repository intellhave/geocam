#ifndef EUCLIDEANANGLE_H_
#define EUCLIDEANANGLE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"
#include "triangulation/triangulation.h"

#include "miscmath.h"

EuclideanAngleIndex* EuclideanAngle::Index = NULL;

EuclideanAngle::EuclideanAngle( Vertex& v, Face& f ){
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

void EuclideanAngle::remove() {
  deleteDependents();
  lengthA->removeDependent(this);
  lengthB->removeDependent(this);
  lengthC->removeDependent(this);
  Index->erase(pos);
  delete this;
}

EuclideanAngle::~EuclideanAngle(){
  
}
void EuclideanAngle::recalculate() {
  double a = lengthA->getValue();
  double b = lengthB->getValue();
  double c = lengthC->getValue();
  value = acos((a*a + b*b - c*c)/ (2*a*b));
}

EuclideanAngle* EuclideanAngle::At( Vertex& v, Face& f ){
  TriPosition T( 2, v.getSerialNumber(), f.getSerialNumber() );
  if( Index == NULL ) Index = new EuclideanAngleIndex();
  EuclideanAngleIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EuclideanAngle* val = new EuclideanAngle( v, f );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EuclideanAngle::CleanUp(){
  if( Index == NULL ) return;
  EuclideanAngleIndex::iterator iter;
  EuclideanAngleIndex copy = *Index;
  
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

#endif /* EUCLIDEANANGLE_H_ */

