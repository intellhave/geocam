#ifndef PARTEDGE_H_
#define PARTEDGE_H_
#include "geoquants.h"

PartialEdge::PartialEdge( Vertex& v, Edge& e , GQIndex& gqi) : GeoQuant() {
    position = new TriPosition(PARTIAL_EDGE, 2, v.getSerialNumber(), e.getSerialNumber());
    dataID = PARTIAL_EDGE;
    
    TriPosition lenTP(LENGTH, 1, e.getSerialNumber());
    len = gqi[ lenTP ];
    len->addDependent(this);
    
    TriPosition rad1(RADIUS, 1, v.getSerialNumber());
    radA = gqi[ rad1 ];
    radA->addDependent(this);
     
    
    int v2Index;
    if((*(e.getLocalVertices()))[0] != v.getIndex())
    {
       v2Index = (*(e.getLocalVertices()))[0];
    } else
    {
       v2Index = (*(e.getLocalVertices()))[1];
    }
    
    TriPosition rad2(RADIUS, 1, Triangulation::vertexTable[v2Index].getSerialNumber());
    radB = gqi[ rad2];
    radB->addDependent(this);
}

void PartialEdge::recalculate(){
   double length = len->getValue();
   value = (pow(length, 2) + pow(radA->getValue(), 2) 
           - pow(radB->getValue(), 2)) / (2 * length);
}

void Init_PartialEdges(GQIndex& gqi) {
     map<int, Vertex>::iterator vit;
     vector<int> edges;
     for(vit = Triangulation::vertexTable.begin(); 
               vit != Triangulation::vertexTable.end(); vit++)
     {
         edges = *(vit->second.getLocalEdges());
         Edge e;
         for(int i = 0; i < edges.size(); i++) {
             e = Triangulation::edgeTable[edges[i]];
             PartialEdge *pe = new PartialEdge(vit->second, e, gqi);
             gqi[pe->getPosition()] = pe;         
         }
     }
}
#endif /* PARTEDGE_H_ */
