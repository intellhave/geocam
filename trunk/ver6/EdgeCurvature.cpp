#ifndef EDGECURVATURE_CPP_
#define EDGECURVATURE_CPP_

#include "geoquant.h"
#include "geoquants.h"

EdgeCurvature::EdgeCurvature(Edge& e, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(EDGE_CURVATURE, 1, e.getSerialNumber());
    dataID = EDGE_CURVATURE;
    
    dih_angles = new vector<GeoQuant*>();

    GeoQuant* dih_angle;
    for(int i = 0; i < e.getLocalTetras()->size(); i++) {
        TriPosition tp(DIHEDRAL_ANGLE, 2, e.getSerialNumber(),
                     Triangulation::tetraTable[(*(e.getLocalTetras()))[i]].getSerialNumber());
        dih_angle = gqi[tp];
	    dih_angle->addDependent(this);
	    dih_angles->push_back( dih_angle );
    }
}

void EdgeCurvature::recalculate() {
    double curv = 2*PI;
    GeoQuant* dih_angle;
    for(int ii = 0; ii < dih_angles->size(); ii++){
      dih_angle = dih_angles->at(ii);
      curv -= dih_angle->getValue();
    }
    
    value = curv;
}

void Init_EdgeCurvatures(GQIndex& gqi) {
     map<int, Edge>::iterator eit;
     for(eit = Triangulation::edgeTable.begin();
               eit != Triangulation::edgeTable.end(); eit++)
     {
         EdgeCurvature *ec = new EdgeCurvature(eit->second, gqi);
         gqi[ ec->getPosition()] = ec;
     }     
}
#endif /* EDGECURVATURE_CPP_ */
