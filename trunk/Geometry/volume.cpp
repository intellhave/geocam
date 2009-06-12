#ifndef VOLUME_H_
#define VOLUME_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"

#include "triangulation/triangulation.h"

VolumeIndex* Volume::Index = NULL;

Volume::Volume( Tetra& t ){
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
    
  len[0] = Length::At( Triangulation::edgeTable[edges1[0]] );
  len[1] = Length::At( Triangulation::edgeTable[edges1[1]] );
  len[2] = Length::At( Triangulation::edgeTable[edges1[2]] );
  len[3] = Length::At( Triangulation::edgeTable[edge23[0]] );
  len[4] = Length::At( Triangulation::edgeTable[edge24[0]] );   
  len[5] = Length::At( Triangulation::edgeTable[edge34[0]] );
   
  for(int i = 0; i < 6; i++)
    len[i]->addDependent( this );        
}

void Volume::recalculate(){
  double CayleyMenger;
  double L12 = len[0]->getValue();
  double L13 = len[1]->getValue();
  double L14 = len[2]->getValue();
  double L23 = len[3]->getValue();
  double L24 = len[4]->getValue();
  double L34 = len[5]->getValue();
   
  CayleyMenger = (-1)*( pow(L12, 4.0)*pow(L34,2.0) + pow(L13, 4.0)*pow(L24,2.0) 
		 + pow(L14, 4.0)*pow(L23,2.0) + pow(L23, 4.0)*pow(L14,2.0)
		 + pow(L24, 4.0)*pow(L13,2.0) + pow(L34, 4.0)*pow(L12,2.0) );

  CayleyMenger += (-1)*( pow(L12, 2.0)*pow(L13,2.0)*pow(L23,2.0) 
			 + pow(L12, 2.0)*pow(L14,2.0)*pow(L24,2.0)
			 + pow(L13, 2.0)*pow(L14,2.0)*pow(L34,2.0)
			 + pow(L23, 2.0)*pow(L24,2.0)*pow(L34,2.0) );

  CayleyMenger +=  pow(L12, 2.0)*pow(L13,2.0)*pow(L24,2.0) 
                         + pow(L12, 2.0)*pow(L13,2.0)*pow(L34,2.0)
                         + pow(L12, 2.0)*pow(L14,2.0)*pow(L23,2.0) 
                         + pow(L12, 2.0)*pow(L14,2.0)*pow(L34,2.0);

  CayleyMenger += pow(L13, 2.0)*pow(L14,2.0)*pow(L23,2.0) 
                         + pow(L13, 2.0)*pow(L14,2.0)*pow(L24,2.0)
                         + pow(L13, 2.0)*pow(L23,2.0)*pow(L24,2.0)
                         + pow(L14, 2.0)*pow(L23,2.0)*pow(L24,2.0);

  CayleyMenger += pow(L12, 2.0)*pow(L23,2.0)*pow(L34,2.0)
                         + pow(L14, 2.0)*pow(L23,2.0)*pow(L34,2.0) 
                         + pow(L12, 2.0)*pow(L24,2.0)*pow(L34,2.0) 
                         + pow(L13, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   
  value = sqrt(CayleyMenger / 144.0);
}

Volume::~Volume(){}

Volume* Volume::At( Tetra& t ){
  TriPosition T( 1, t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeIndex();
  VolumeIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Volume* val = new Volume( t );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Volume::CleanUp(){
  if( Index == NULL ) return;
  VolumeIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* VOLUME_H_ */
