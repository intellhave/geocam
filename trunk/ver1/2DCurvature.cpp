2DCuravture::2DCuravture(Vertex& v, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(1, CURVATURE, v);
    
    for(int i = 0; i < v.getLocalFaces()->size(); i++) {
        TriPosition tp(2, ANGLE, v, Triangulation::faceTable[(*(v.getLocalFaces()))[i]]);
        angles.push_back(gqi[tp]);
    }
    for(int i = 0; i < angles.size(); i++) {
        angles[i].addDependents(*(this));        
    }
}

void 2DCuravture::recalculate() {
    double curv = 2*PI;
    for(int i = 0; i < angles.size(); i++) {
       curv -= angles[i];        
    }
    value = curv;
}
