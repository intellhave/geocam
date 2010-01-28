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

  localCurvature = Curvature3D::At( v );
  localCurvature->addDependent( this );

  vps = VolumePartialSum::At( v );
  vps->addDependent( this );
}

NEHRPartial::NEHRPartial( Edge& e ){
  wrtRadius = true;
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

void NEHRPartial::recalculate(){
  double totalCurv = totCurvature->getValue();
  double totalVol = totVolume->getValue();
  double volumePartial = volPartial->getValue();
  
  double curv_partial_sum = 0;
  for(int i = 0; i < curvPartials->size(); i++) {
    curv_partial_sum += curvPartials->at(i)->getValue();
  }
  
  value = 1.0/(3 * pow(totalVol, 4.0/3)) * volumePartial * totalCurv
          + 1.0 / pow(totalVol, 4.0/3) * curv_partial_sum;
}

void NEHRPartial::remove() {
  deleteDependents();
  
  totVolume->removeDependent(this);
  totCurvature->removeDependent(this);
  volPartial->removeDependent(this);
  
  for(int i = 0; i < curvPartials->size(); i++) {
    curvPartials->at(i)->removeDependent(this);
  }
  
  delete curvPartials;
  Index->erase(pos);
  delete this;
}

NEHRPartial::~NEHRPartial(){}

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
