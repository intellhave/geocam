#ifndef DIHEDRALANGLE_cpp_
#define DIHEDRALANGLE_cpp_
#include "geoquants.h"

DihedralAngle::DihedralAngle(Edge& e, Tetra& t, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(DIHEDRAL_ANGLE, 2, e.getSerialNumber(), t.getSerialNumber());
    dataID = DIHEDRAL_ANGLE;
    
    Vertex *v = &Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
    
    vector<int> faces = listIntersection(t.getLocalFaces(), v->getLocalFaces());
    vector<int> edge_faces = listIntersection(&faces, e.getLocalFaces());
    vector<int> not_edge_faces = listDifference(&faces, e.getLocalFaces());

    TriPosition tp1(ANGLE, 2, v->getSerialNumber(), Triangulation::faceTable[edge_faces[0]].getSerialNumber());
    TriPosition tp2(ANGLE, 2, v->getSerialNumber(), Triangulation::faceTable[edge_faces[1]].getSerialNumber());
    TriPosition tp3(ANGLE, 2, v->getSerialNumber(), Triangulation::faceTable[not_edge_faces[0]].getSerialNumber());
    
    angles[0] = gqi[tp1]; 
    angles[1] = gqi[tp2]; 
    angles[2] = gqi[tp3];
    
    for(int i = 0; i < 3; i++) {
      angles[i]->addDependent(this);
    }
}
void DihedralAngle::recalculate() {
    double ang0, ang1, ang2;
    ang0 = angles[0]->getValue(); 
    ang1 = angles[1]->getValue(); 
    ang2 = angles[2]->getValue(); 
    value =  acos((cos(ang2)-cos(ang0)*cos(ang1))
		  / (sin(ang0)*sin(ang1)));;
}

void Init_DihedralAngles(GQIndex& gqi){
  vector<int>* tetras;
  map<int, Edge>::iterator eit;  
  for(eit = Triangulation::edgeTable.begin();
      eit != Triangulation::edgeTable.end(); eit++){
    tetras = eit->second.getLocalTetras();
    
    for(int i = 0; i < tetras->size(); i++){
      Tetra& t = Triangulation::tetraTable[ tetras->at(i) ];
      DihedralAngle* da = new DihedralAngle(eit->second, t, gqi);
      gqi[ da->getPosition() ] = da;          
    }             
  }
}
#endif /* DIHEDRALANGLE_CPP_ */
