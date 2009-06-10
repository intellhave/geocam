#ifndef VOLUMEPARTIAL_H_
#define VOLUMEPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "vertex.h"
#include "edge.h"
#include "tetra.h"
#include "triangulation.h"

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"

class VolumePartial;
typedef map<TriPosition, VolumePartial*, TriPositionCompare> VolumePartialIndex;

class VolumePartial : public virtual GeoQuant {
private:
  static VolumePartialIndex* Index;
  
  Radius* rad[4];
  Eta* eta[6]
 
protected:
  VolumePartial( Vertex& v, Tetra& t );
  void recalculate();

public:
  ~VolumePartial();
  static VolumePartial* At( Vertex& v, Tetra& t );
  static void CleanUp();
};
VolumePartialIndex* VolumePartial::Index = NULL;

VolumePartial::VolumePartial( Vertex& v, Tetra& t ){
    vector<int> opedges; 
    opedges = listDifference(t.getLocalEdges(),  Triangulation::vertexTable[i].getLocalEdges());
    Edge e23, e34, e24, e12, e13, e14;
    Vertex v1, v2, v3, v4;
    
    e23 = Triangulation::edgeTable[opedges[0]]; 
    e24 = Triangulation::edgeTable[opedges[2]];
    e34 = Triangulation::edgeTable[opedges[1]];
   
    v1 = Triangulation::vertexTable[i];       
    v2 = Triangulation::vertexTable[listIntersection(e23.getLocalVertices(), e24.getLocalVertices())[0]];
    v3 = Triangulation::vertexTable[listIntersection(e23.getLocalVertices(), e34.getLocalVertices())[0]];
    v4 = Triangulation::vertexTable[listIntersection(e24.getLocalVertices(), e34.getLocalVertices())[0]];
   
    
    
    e12 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v2.getLocalEdges())[0]];
    e13 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v3.getLocalEdges())[0]];
    e14 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v4.getLocalEdges())[0]];
   
}

void VolumePartial::recalculate(){
  double r1, r2, r3, r4, Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
       
  r1 = rad[0]->getValue();   r2 = rad[1]->getValue();
  r3 = rad[2]->getValue();   r4 = rad[3]->getValue();
       
  Eta12 = eta[0]->getValue();  Eta13 = eta[1]->getValue();
  Eta14 = eta[2]->getValue();  Eta23 = eta[3]->getValue();
  Eta24 = eta[4]->getValue();  Eta34 = eta[5]->getValue();

  // Hideous!
  value = (r1*(r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			  Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			    ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
			      Eta12*(Eta23 + Eta24*Eta34))*r2 + 
			     (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
			      Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		  r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			 pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
		      2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
				Eta34 - pow(Eta12,2)*Eta34)*r2 + 
			       (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
				Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
		      ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			pow(Eta24,2))*pow(r2,2) - 
		       2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			  Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
		       (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			pow(Eta34,2))*pow(r3,2))*pow(r4,2)))) /
    (6.*sqrt(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
		  pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
	       2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			       Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			      ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				Eta12*(Eta23 + Eta24*Eta34))*r2 + 
			       (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
				Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
	       pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			     pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
			  2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
				    Eta34 - pow(Eta12,2)*Eta34)*r2 + 
				   (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
				    Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			  ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			    pow(Eta24,2))*pow(r2,2) - 
			   2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			      Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
			   (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			    pow(Eta34,2))*pow(r3,2))*pow(r4,2))));            
}

VolumePartial::~VolumePartial(){}

VolumePartial* VolumePartial::At( Vertex& v, Tetra& t ){
  TriPosition T( 1, v.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumePartialIndex();
  VolumePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumePartial* val = new VolumePartial( v, t );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void VolumePartial::CleanUp(){
  if( Index == NULL ) return;
  VolumePartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* VOLUMEPARTIAL_H_ */
