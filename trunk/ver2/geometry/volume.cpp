#ifndef VOLUME_H_
#define VOLUME_H_

#include "geoquants.h"

Volume::Volume(Tetra& t, GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(VOLUME, 1, t.getSerialNumber());
    dataID = VOLUME;

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
    
   TriPosition t12(LENGTH, 1, Triangulation::edgeTable[edges1[0]].getSerialNumber());
   len[0] = gqi[t12];
   TriPosition t13(LENGTH, 1, Triangulation::edgeTable[edges1[1]].getSerialNumber());
   len[1] = gqi[t13];
   TriPosition t14(LENGTH, 1, Triangulation::edgeTable[edges1[2]].getSerialNumber());
   len[2] = gqi[t14];
   TriPosition t23(LENGTH, 1, Triangulation::edgeTable[edge23[0]].getSerialNumber());
   len[3] = gqi[t23];
   TriPosition t24(LENGTH, 1, Triangulation::edgeTable[edge24[0]].getSerialNumber());
   len[4] = gqi[t24];   
   TriPosition t34(LENGTH, 1, Triangulation::edgeTable[edge34[0]].getSerialNumber());
   len[5] = gqi[t34];
   
    for(int i = 0; i < 6; i++) {
        len[i]->addDependent( this );        
    }
    

}

void Volume::recalculate() {
   double CayleyMenger;
   double L12 = len[0]->getValue();
   double L13 = len[1]->getValue();
   double L14 = len[2]->getValue();
   double L23 = len[3]->getValue();
   double L24 = len[4]->getValue();
   double L34 = len[5]->getValue();
   
   CayleyMenger=-pow(L12, 4.0)*pow(L34,2.0)-pow(L13, 4.0)*pow(L24,2.0)-pow(L14, 4.0)*pow(L23,2.0)-pow(L23, 4.0)*pow(L14,2.0)-pow(L24, 4.0)*pow(L13,2.0)-pow(L34, 4.0)*pow(L12,2.0);
   CayleyMenger=CayleyMenger-pow(L12, 2.0)*pow(L13,2.0)*pow(L23,2.0)-pow(L12, 2.0)*pow(L14,2.0)*pow(L24,2.0)-pow(L13, 2.0)*pow(L14,2.0)*pow(L34,2.0)-pow(L23, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger+pow(L12, 2.0)*pow(L13,2.0)*pow(L24,2.0)+pow(L12, 2.0)*pow(L13,2.0)*pow(L34,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger+pow(L13, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L13, 2.0)*pow(L14,2.0)*pow(L24,2.0)+pow(L13, 2.0)*pow(L23,2.0)*pow(L24,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L24,2.0);
   CayleyMenger=CayleyMenger+pow(L12, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L12, 2.0)*pow(L24,2.0)*pow(L34,2.0)+pow(L13, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger/144.0;
   
   value = CayleyMenger;
}

#endif /* VOLUME_H_ */
