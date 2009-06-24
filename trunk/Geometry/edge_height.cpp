#ifndef EDGEHEIGHT_H_
#define EDGEHEIGHT_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "miscmath.h"
#include "geoquant.h"
#include "geoquants.h"
#include "triposition.h"


EdgeHeightIndex* EdgeHeight::Index = NULL;

EdgeHeight::EdgeHeight( Edge& e, Face& f ){
  StdFace sf = labelFace( e, f );
  d_ij = PartialEdge::At( sf.v1, sf.e12 );
  d_ik = PartialEdge::At( sf.v1, sf.e13 );
  theta_i = EuclideanAngle::At( sf.v1, f );
  d_ij->addDependent(this);
  d_ik->addDependent(this);
  theta_i->addDependent(this);
}

void EdgeHeight::recalculate(){
  double dij = d_ij->getValue();
  double dik = d_ik->getValue();
  double theta = theta_i->getValue();

  value = dik - dij * cos(theta)/sin(theta);
}

void EdgeHeight::remove() {
     deleteDependents();
     d_ij->removeDependent(this);
     d_ik->removeDependent(this);
     theta_i->removeDependent(this);
     Index->erase(pos);
     delete this;
}

EdgeHeight::~EdgeHeight(){ }

EdgeHeight* EdgeHeight::At( Edge& e, Face& f ){
  TriPosition T( 2, e.getSerialNumber(), f.getSerialNumber() );
  if( Index == NULL ) Index = new EdgeHeightIndex();
  EdgeHeightIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EdgeHeight* val = new EdgeHeight( e, f );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EdgeHeight::CleanUp(){
  if( Index == NULL ) return;
  EdgeHeightIndex::iterator iter;
  EdgeHeightIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

#endif /* EDGEHEIGHT_H_ */

