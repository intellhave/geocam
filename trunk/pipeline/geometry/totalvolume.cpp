#include "totalvolume.h"

#include <stdio.h>

static TotalVolume* totVol = NULL;

TotalVolume::TotalVolume(){
  volumes = new vector<Volume*>();
  
  Volume* vol;
  map<int, Tetra>::iterator tetraIter = Triangulation::tetraTable.begin();
  while( tetraIter != Triangulation::tetraTable.end() ){
    vol = Volume::At( tetraIter->second );
    volumes->push_back( vol );
    vol->addDependent( this );
    tetraIter++;
  }
}

TotalVolume::~TotalVolume(){
  delete volumes;
}

void TotalVolume::recalculate(){
  value = 0.0;
  Volume* vol;
  for(int ii = 0; ii < volumes->size(); ii++){
    vol = volumes->at(ii);
    value += vol->getValue();
  }
}

TotalVolume* TotalVolume::At(){
  if( totVol == NULL )
    totVol = new TotalVolume();
    
  return totVol;
}

void TotalVolume::CleanUp(){
  delete totVol;
}

