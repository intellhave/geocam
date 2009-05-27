#ifndef TWODCURVATURE_CPP_
#define TWODCURVATURE_CPP_

#include "geoquant.h"
#include "triposition.h"
#include "triangulation/triangulation.h"

#include "simplex/vertex.h"

#include "geoquants.h"

Curvature2D::Curvature2D(Vertex& v, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(CURVATURE, 1, v.getSerialNumber());
    dataID = CURVATURE;    
    angles = new vector<GeoQuant*>();

    GeoQuant* angle;
    for(int i = 0; i < v.getLocalFaces()->size(); i++) {
        TriPosition tp(ANGLE, 2, v.getSerialNumber(), 
                    Triangulation::faceTable[(*(v.getLocalFaces()))[i]].getSerialNumber());
        angle = gqi[tp];
	    angle->addDependent(this);
	    angles->push_back( angle );
    }
}

void Curvature2D::recalculate() {
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
#endif /* TWODCURVATURE_CPP_ */
