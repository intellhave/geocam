#include "nehr_partial.h"

#include <stdio.h>

typedef map<TriPosition, NEHRPartial*, TriPositionCompare> NEHRPartialIndex;
static NEHRPartialIndex* Index = NULL;

NEHRPartial::NEHRPartial( Vertex& v ){
  wrtRadius = true;
  totVolume = TotalVolume::At();
  totVolume->addDependent( this );

  totCurvature = TotalCurvature::At();
  totCurvature->addDependent( this );

  volPartial = TotalVolumePartial::At( v );
  volPartial->addDependent( this );
  
  localCurvature = Curvature3D::At( v );
  localCurvature->addDependent( this );
  
  rad = Radius::At( v );
  rad->addDependent( this );
}

NEHRPartial::NEHRPartial( Edge& e ){
  wrtRadius = false;
  totVolume = TotalVolume::At();
  totCurvature = TotalCurvature::At();
  volPartial = TotalVolumePartial::At(e);
  
  totVolume->addDependent(this);
  totCurvature->addDependent(this);
  volPartial->addDependent(this);
  
  curvPartials = new vector<CurvaturePartial*>();
  
  map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin();
  
  CurvaturePartial* cp;
  while( vit != Triangulation::vertexTable.end() ){
    cp = CurvaturePartial::At( vit->second, e );
    cp->addDependent( this );
    curvPartials->push_back( cp );
    vit++;
  }
}

double NEHRPartial::calculateRadiusCase() {
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double K = localCurvature->getValue();
  double volumePartial = volPartial->getValue();
  double rad_i = rad->getValue();
  
  return pow(totV, -4.0/3.0)*(K*totV - totK*volumePartial/3.0); 
}

double NEHRPartial::calculateEtaCase() {
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double volumePartial = volPartial->getValue();
  
  double curv_partial_sum = 0;
  for(int i = 0; i < curvPartials->size(); i++) {
    curv_partial_sum += curvPartials->at(i)->getValue();
  }
  
  return pow(totV, -4.0/3.0)*(curv_partial_sum*totV - totK*volumePartial/3.0);
}

void NEHRPartial::recalculate(){
  if(wrtRadius) {
    value = calculateRadiusCase();
  } else {
    value = calculateEtaCase();
  }
}

void NEHRPartial::remove() {
  deleteDependents();
  
  totVolume->removeDependent(this);
  totCurvature->removeDependent(this);
  volPartial->removeDependent(this);
  
  if(wrtRadius) {
    localCurvature->removeDependent(this);
    rad->removeDependent(this);
  } else {
    for(int i = 0; i < curvPartials->size(); i++) {
      curvPartials->at(i)->removeDependent(this);
    }
    delete curvPartials;
  }
  
  Index->erase(pos);
  delete this;
}

NEHRPartial::~NEHRPartial(){}

NEHRPartial* NEHRPartial::At( Vertex& v ){
  TriPosition T( 1, v.getSerialNumber() );
  if( Index == NULL ) Index = new NEHRPartialIndex();
  NEHRPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    NEHRPartial* val = new NEHRPartial( v );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

NEHRPartial* NEHRPartial::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new NEHRPartialIndex();
  NEHRPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    NEHRPartial* val = new NEHRPartial( e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void NEHRPartial::CleanUp(){
  if( Index == NULL ) return;
  NEHRPartialIndex::iterator iter;
  NEHRPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void NEHRPartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  NEHRPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
