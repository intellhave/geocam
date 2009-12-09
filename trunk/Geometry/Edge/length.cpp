#include "length.h"

#include <stdio.h>

typedef map<TriPosition, Length*, TriPositionCompare> LengthIndex;
static LengthIndex* Index = NULL;

Length::Length( Edge& e ){
  Vertex v1 = Triangulation::vertexTable[ (*(e.getLocalVertices()))[0] ];
  Vertex v2 = Triangulation::vertexTable[ (*(e.getLocalVertices()))[1] ];

  radius1 = Radius::At( v1 );
  radius2 = Radius::At( v2 );
  eta = Eta::At( e );
  
  radius1->addDependent( this );
  radius2->addDependent( this );
  eta->addDependent( this );
}

void Length::recalculate(){
  double r1 = radius1->getValue();
  double r2 = radius2->getValue();
  double etaV = eta->getValue();

  value = sqrt( r1*r1 + r2*r2 + 2*r1*r2*etaV );   
}

void Length::remove() {
   deleteDependents();
   radius1->removeDependent(this);
   radius2->removeDependent(this);
   eta->removeDependent(this);
   Index->erase(pos);
   delete this;
}

Length::~Length(){}

Length* Length::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new LengthIndex();
  LengthIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Length* val = new Length( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Length::CleanUp(){
  if( Index == NULL ) return;
  LengthIndex::iterator iter;
  LengthIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void Length::print(FILE* out) {
  map<int, Edge>::iterator eit;
  
  fprintf(out, "Length [ e ]\n=============\n");
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
     fprintf(out, "Length [%3d]\t= % 2.8f\n", eit->first, Length::valueAt(eit->second));
  }       
}
