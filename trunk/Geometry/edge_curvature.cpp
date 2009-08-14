#include "edge_curvature.h"

#include <stdio.h>

//static const double PI = 3.1415926;

class EdgeCurvature;
typedef map<TriPosition, EdgeCurvature*, TriPositionCompare> EdgeCurvatureIndex;
static EdgeCurvatureIndex* Index = NULL;

EdgeCurvature::EdgeCurvature( Edge& e  ){
  length = Length::At(e); 
  sectionalCurv = SectionalCurvature::At(e);
  
  length->addDependent(this);
  sectionalCurv->addDependent(this);
}

void EdgeCurvature::remove() {
     deleteDependents();
     length->removeDependent(this);
     sectionalCurv->removeDependent(this);     
     Index->erase(pos);
     delete this;
}

EdgeCurvature::~EdgeCurvature(){ }

void EdgeCurvature::recalculate() {
    value = sectionalCurv->getValue() * length->getValue();
}

EdgeCurvature* EdgeCurvature::At( Edge& e  ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new EdgeCurvatureIndex();
  EdgeCurvatureIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EdgeCurvature* val = new EdgeCurvature( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EdgeCurvature::CleanUp(){
  if( Index == NULL ) return;
  EdgeCurvatureIndex::iterator iter;
  EdgeCurvatureIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void EdgeCurvature::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  EdgeCurvatureIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
