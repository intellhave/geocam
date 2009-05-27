Volume::Volume(Tetra& t, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(1, VOLUME, t);

   int vertex, face;
   vector<int> edges1, edge23, edge24, edge34;
  
   vertex = (*(t.getLocalVertices()))[0];
   edges1 = listIntersection(Triangulation::vertexTable[vertex].getLocalEdges(), t.getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[1]].getLocalFaces())[0];
   edge23 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge24 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[1]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge34 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
    
   Triposition t12(1, LENGTH, Triangulation::edgeTable[edges1[0]]);
   len[0] = gqi[t12];
   Triposition t13(1, LENGTH, Triangulation::edgeTable[edges1[1]]);
   len[1] = gqi[t13];
   Triposition t14(1, LENGTH, Triangulation::edgeTable[edges1[2]]);
   len[2] = gqi[t14];
   Triposition t23(1, LENGTH, Triangulation::edgeTable[edges23[0]]);
   len[3] = gqi[t23];
   Triposition t24(1, LENGTH, Triangulation::edgeTable[edges24[0]]);
   len[4] = gqi[t24];   
   Triposition t34(1, LENGTH, Triangulation::edgeTable[edges34[0]]);
   len[5] = gqi[t34];
   
    for(int i = 0; i < 6; i++) {
        len[i].addDependents(*(this));        
    }
    

}

void 2DCuravture::recalculate() {
   double CayleyMenger;
   
   CayleyMenger=-pow(L12, 4.0)*pow(L34,2.0)-pow(L13, 4.0)*pow(L24,2.0)-pow(L14, 4.0)*pow(L23,2.0)-pow(L23, 4.0)*pow(L14,2.0)-pow(L24, 4.0)*pow(L13,2.0)-pow(L34, 4.0)*pow(L12,2.0);
   CayleyMenger=CayleyMenger-pow(L12, 2.0)*pow(L13,2.0)*pow(L23,2.0)-pow(L12, 2.0)*pow(L14,2.0)*pow(L24,2.0)-pow(L13, 2.0)*pow(L14,2.0)*pow(L34,2.0)-pow(L23, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger+pow(L12, 2.0)*pow(L13,2.0)*pow(L24,2.0)+pow(L12, 2.0)*pow(L13,2.0)*pow(L34,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger+pow(L13, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L13, 2.0)*pow(L14,2.0)*pow(L24,2.0)+pow(L13, 2.0)*pow(L23,2.0)*pow(L24,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L24,2.0);
   CayleyMenger=CayleyMenger+pow(L12, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L12, 2.0)*pow(L24,2.0)*pow(L34,2.0)+pow(L13, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger/144.0;
   
   value = CayleyMenger;
}
