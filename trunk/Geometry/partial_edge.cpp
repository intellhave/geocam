#ifndef PARTEDGE_H_
#define PARTEDGE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "geoquants.h"
#include "triangulation/triangulation.h"

PartialEdgeIndex* PartialEdge::Index = NULL;

PartialEdge::PartialEdge( Vertex& v, Edge& e  ){    
    length = Length::At(e);
    length->addDependent(this);
    
    radA = Radius::At(v);
    radA->addDependent(this);
     
    
    int v2Index;
    if((*(e.getLocalVertices()))[0] != v.getIndex())
    {
       v2Index = (*(e.getLocalVertices()))[0];
    } else
    {
       v2Index = (*(e.getLocalVertices()))[1];
    }
    
    radB = Radius::At(Triangulation::vertexTable[v2Index]);
    radB->addDependent(this);  
}

PartialEdge::~PartialEdge(){ }
void PartialEdge::recalculate() {
   double len = length->getValue();
   value = (pow(len, 2) + pow(radA->getValue(), 2) 
           - pow(radB->getValue(), 2)) / (2 * len);
}

PartialEdge* PartialEdge::At( Vertex& v, Edge& e  ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new PartialEdgeIndex();
  PartialEdgeIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    PartialEdge* val = new PartialEdge( v, e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void PartialEdge::CleanUp(){
  if( Index == NULL) return;
  PartialEdgeIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* PARTEDGE_H_ */


