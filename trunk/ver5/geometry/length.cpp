#ifndef LENGTH_CPP_
#define LENGTH_CPP_

#include <new>
#include <cmath>
#include <cstdio>

#include "geoquant.h"
#include "triposition.h"

#include "triangulation.h"
#include "edge.h"

class Length : public virtual GeoQuant {
private:
  GeoQuant* radius1;
  GeoQuant* radius2;
  GeoQuant* eta;

public:
  Length(Edge& e, GQIndex& gqi);
  void recalculate();
};

Length::Length(Edge& e, GQIndex& gqi) : GeoQuant(){
  position = new TriPosition( LENGTH, 1, e.getSerialNumber() );
  dataID = LENGTH; 

  TriPosition t(ETA, 1, e.getSerialNumber());
  eta = gqi[t];
  eta->addDependent( this );
    
  Vertex v1 = Triangulation::vertexTable[ (e.getLocalVertices())->at(0) ];
  Vertex v2 = Triangulation::vertexTable[ (e.getLocalVertices())->at(1) ];

  t = TriPosition(RADIUS, 1, v1.getSerialNumber());
  radius1 = gqi[ t ];
  radius1->addDependent( this );

  t = TriPosition(RADIUS, 1, v2.getSerialNumber());
  radius2 = gqi[ t ];
  radius2->addDependent( this );
}

void Length::recalculate(){
  double r1 = radius1->getValue();
  double r2 = radius2->getValue();
  double theta = eta->getValue();

  value = sqrt( r1*r1 + r2*r2 + 2*r1*r2*cos(theta) );
}

void Init_Lengths(GQIndex& gqi){
  map<int, Edge>::iterator eit;
  for(eit = Triangulation::edgeTable.begin();
      eit != Triangulation::edgeTable.end(); eit++){
    Length* l = new Length(eit->second, gqi);
    gqi[l->getPosition()] = l;
  }
}

#endif /* LENGTH_CPP_ */
