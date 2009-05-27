#ifndef THREEDCURVATURE_CPP_
#define THREEDCURVATURE_CPP_

#include "geoquant.h"
#include "geoquants.h"

Curvature3D::Curvature3D(Vertex& v, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(CURVATURE, 1, v.getSerialNumber());
    dataID = CURVATURE;
    
    edgeCurvs = new vector<GeoQuant*>();
    partials = new vector<GeoQuant*>();

    GeoQuant* edgeC;
    GeoQuant* partial;
    
    for(int i = 0; i < v.getLocalEdges()->size(); i++)
    {
        TriPosition ec(EDGE_CURVATURE, 1, 
                    Triangulation::edgeTable[(*(v.getLocalEdges()))[i]].getSerialNumber());
        TriPosition pe(PARTIAL_EDGE, 2, v.getSerialNumber(), 
                    Triangulation::edgeTable[(*(v.getLocalEdges()))[i]].getSerialNumber());
        edgeC = gqi[ec];
        partial = gqi[pe];
	    edgeC->addDependent(this);
	    partial->addDependent(this);
	    edgeCurvs->push_back( edgeC );
	    partials->push_back( partial );
    }
}

void Curvature3D::recalculate() {
    double curv = 0; 
    GeoQuant* edgeC;
    GeoQuant* partial;
    for(int ii = 0; ii < edgeCurvs->size(); ii++){
      edgeC = edgeCurvs->at(ii);
      partial = partials->at(ii);
      curv += edgeC->getValue()* partial->getValue();
    }
    
    value = curv;
}

void Init_Curvature3Ds(GQIndex& gqi) {
     map<int, Vertex>::iterator vit;
     for(vit= Triangulation::vertexTable.begin(); 
              vit != Triangulation::vertexTable.end(); vit++) {
         Curvature3D *curv3D = new Curvature3D(vit->second, gqi);
         gqi[curv3D->getPosition()]  = curv3D;
     }   
}
#endif /* THREEDCURVATURE_CPP_ */
