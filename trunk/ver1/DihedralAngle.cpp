DihedralAngle::DihedralAngle(Edge& e, Tetra &t, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(2, DIHEDRALANGLE, e, t);
    
    Vertex *v = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
    
    vector<int> faces = listintersection(t.getLocalFaces(), v->getLocalFaces());
    vector<int> edge_faces = listIntersection(&faces, e.getLocalFaces());
    vector<int> not_edge_faces = listDifference(&faces, e.getLocalFaces());

    TriPosition tp1(2, ANGLE, *v, Triangulation::faceTable[edge_faces[0]]);
    TriPosition tp2(2, ANGLE, *v, Triangulation::faceTable[edge_faces[1]]);
    TriPosition tp3(2, ANGLE, *v, Triangulation::faceTable[not_edge_faces[0]]);
    
    angles[0] = gqi[tp1]; angles[1] = gqi[tp2]; angles[2] = gqi[tp3];
    for(int i = 0; i < 3; i++) {
       angles[i].addDependents(*(this));        
    }
}

void DihedralAngle::recalculate() {
    double ang0, ang1, ang2;
    ang0 = angles[0].getValue(); 
    ang1 = angles[1].getValue(); 
    ang2 = angles[2].getValue(); 
    value =  acos((cos(ang2)-cos(ang0)*cos(ang1))
                              / (sin(ang0)*sin(ang1)));;
}
