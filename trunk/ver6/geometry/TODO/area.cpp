#ifndef AREA_CPP_
#define AREA_CPP_

#include <cmath>
#include <cstdio>

#include "triangulation.h"
#include "edge.h"
#include "face.h"

#include "geoquant.h"
#include "length.cpp"

class Area : public GeoQuant {
protected:
  static GQIndex* Index = NULL;
  TriPosition position;
  GeoQuant* Len[3];
  Area( Face& f );
  void recalculate();

public:
  static GeoQuant* At( Face& f );
  static void CleanUp();
};

Area::Area(Face& f) : GeoQuant(){
    position = TriPosition(1, f.getSerialNumber());

    Edge ed;
    for(int ii = 0; ii < 3; ii++){
      ed = Triangulation::edgeTable[(*(f.getLocalEdges()))[ii]];
      Len[ii] = Length::At( ed )
      Len[ii]->addDependent(this);
    }
}

void Area::recalculate(){
    double l1 = Len[0]->getValue();
    double l2 = Len[1]->getValue();
    double l3 = Len[2]->getValue();

    double s = (l1 + l2 + l3) * 0.5;
    value = sqrt(s * (s - l1) * (s - l2) * (s - l3));
}

GeoQuant* Area::At( Face& f ){
  GeoQuant* retval;
  if(Index == NULL) Index = new GQIndex();

  TriPosition t(1, f.getSerialNumber());
  GQIndex::iterator iter = Index->find( t );
  if( iter == Index->end() ){
    retval = new Area( f );
    Index->insert( make_pair( t, retval ) );
  } else {
    retval = iter->second;
  }
  
  return retval;
}

void Area::CleanUp(){
  GQIndex::iterator it;
  for(it = Index->begin(); it != Index->end(); it++)
    delete it->second;
  
  delete Index;
}


#endif /* AREA_CPP_ */
