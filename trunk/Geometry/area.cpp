#ifndef AREA_H_
#define AREA_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"
#include "triangulation/triangulation.h"

AreaIndex* Area::Index = NULL;

Area::Area( Face& f ){
    for(int ii = 0; ii < 3; ii++){
      Len[ii] = Length::At( Triangulation::edgeTable[(*(f.getLocalEdges()))[ii]] );
      Len[ii]->addDependent(this);
    }
}

void Area::remove() {
    deleteDependents();
    Len[0]->removeDependent(this);
    Len[1]->removeDependent(this);
    Len[2]->removeDependent(this);
    Index->erase(pos);
}

Area::~Area() {
    remove();
}
void Area::recalculate() {
    double l1 = Len[0]->getValue();
    double l2 = Len[1]->getValue();
    double l3 = Len[2]->getValue();

    double s = (l1 + l2 + l3) * 0.5;
    value = sqrt(s * (s - l1) * (s - l2) * (s - l3));
}

Area* Area::At( Face& f ){
  TriPosition T( 1, f.getSerialNumber() );
  if( Index == NULL ) Index = new AreaIndex();
  AreaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Area* val = new Area( f );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}



void Area::CleanUp(){
  if( Index == NULL ) return;
  AreaIndex::iterator iter;
  AreaIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    delete iter->second;
  }
    
  delete Index;
  Index = NULL;
}

#endif /* AREA_H_ */


