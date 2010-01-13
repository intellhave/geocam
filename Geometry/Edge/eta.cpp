#include "eta.h"

#include <stdio.h>

typedef map<TriPosition, Eta*, TriPositionCompare> EtaIndex;
static EtaIndex* Index = NULL;

Eta::Eta( Edge& e ){}

void Eta::recalculate(){
   // DEFAULT
   value = 1;     
}

void Eta::remove() {
     deleteDependents();
     Index->erase(pos);
     delete this;
}

Eta::~Eta(){}

Eta* Eta::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new EtaIndex();
  EtaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Eta* val = new Eta( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Eta::CleanUp(){
  if( Index == NULL ) return;
  EtaIndex::iterator iter;
  EtaIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void Eta::print(FILE* out) {
  map<int, Edge>::iterator eit;
  
  fprintf(out, "Eta [ e ]\n=============\n");
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
     fprintf(out, "Eta [%3d]\t= % 2.8f\n", eit->first, Eta::valueAt(eit->second));
  }       
}
