#ifndef PARTIALEDGE_H_
#define PARTIALEDGE_H_

#include <map>
#include <new>
using namespace std;

#include "miscmath.h"
#include "geoquant.h"
#include "triposition.h"

#include "radius.cpp"
#include "length.cpp"

class PartialEdge;
typedef map<TriPosition, PartialEdge*, TriPositionCompare> PartialEdgeIndex;

class PartialEdge : public virtual GeoQuant {
private:
  static PartialEdgeIndex* Index;
  Radius* ri;
  Radius* rj;
  Length* Lij;

protected:
  PartialEdge( Vertex& v, Edge& e );
  void recalculate();

public:
  ~PartialEdge();
  static PartialEdge* At( Vertex& v, Edge& e );
  static void CleanUp();
};
PartialEdgeIndex* PartialEdge::Index = NULL;

PartialEdge::PartialEdge( Vertex& v, Edge& e ){
  StdEdge st = labelEdge( v, e );  
  ri = Radius::At( st.v1 );
  rj = Radius::At( st.v2 );
  Lij = Length::At( e );
}

void PartialEdge::recalculate(){
  double length = Lij->getValue();
  double radi = ri->getValue();
  double radj = rj->getValue();

  value = (length*length + radi*radi - radj*radj)/(2*length);
}

PartialEdge::~PartialEdge(){}

PartialEdge* PartialEdge::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new PartialEdgeIndex();
  PartialEdgeIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    PartialEdge* val = new PartialEdge( v, e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void PartialEdge::CleanUp(){
  if( Index == NULL ) return;
  PartialEdgeIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* PARTIALEDGE_H_ */
