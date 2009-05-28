#ifndef CURVATURE2D_CPP_
#define CURVATURE2D_CPP_

#include <new>
#include <cstdio>

#include "geoquant.h"
#include "triposition.h"

#include "triangulation.h"
#include "vertex.h"

#define PI 3.1415926

class Curvature2D : public virtual GeoQuant {
private:
  vector<GeoQuant*>* angles;

public:
  Curvature2D(Vertex& v, GQIndex& gqi);
  ~Curvature2D(){ delete angles; }
  void recalculate();
};

Curvature2D::Curvature2D(Vertex& v, GQIndex& gqi) : GeoQuant() {
  position = new TriPosition(CURVATURE, 1, v.getSerialNumber());
  angles = new vector<GeoQuant*>();

  GeoQuant* angle;
  for(int i = 0; i < v.getLocalFaces()->size(); i++) {
    Face& f = Triangulation::faceTable[(*(v.getLocalFaces()))[i]];
    TriPosition tp(ANGLE, 2, v.getSerialNumber(), f.getSerialNumber()) ;
    angle = gqi[tp];
    angle->addDependent(this);
    angles->push_back( angle );
  }
}

void Curvature2D::recalculate(){
  double curv = 2*PI;
  GeoQuant* angle;
  for(int ii = 0; ii < angles->size(); ii++){
    angle = angles->at(ii);
    curv -= angle->getValue();
  }
    
  value = curv;
}

void Init_Curvature2Ds(GQIndex& gqi){
  map<int, Vertex>::iterator vit;
  for(vit = Triangulation::vertexTable.begin(); 
      vit != Triangulation::vertexTable.end(); vit++){
    Curvature2D* curv = new Curvature2D(vit->second, gqi);
    gqi[curv->getPosition()] = curv;                           
  }              
}

#endif /* CURVATURE2D_CPP_ */
