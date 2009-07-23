#include "area.h"

#include <cmath>
#include <map>
#include <new>
using namespace std;

typedef map<TriPosition, Area*, TriPositionCompare> AreaIndex;
static AreaIndex* Index = NULL;

Area::Area( Face& f ){
  vector<int>* edgeIndices = f.getLocalEdges();

  for(int ii = 0; ii < 3; ii++){
    int ed = edgeIndices->at(ii);
    Edge& e = Triangulation::edgeTable[ ed ];
    Len[ii] = Length::At( e );
    Len[ii]->addDependent(this);
  }
}

Area::~Area(){ }

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
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Area::CleanUp(){
  if( Index == NULL) return;
  AreaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}


