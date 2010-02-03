#include "nehr_second_partial.h"

#include <cstdio>

class NEHRSecondPartial;
typedef map<TriPosition, NEHRSecondPartial*, TriPositionCompare> NEHRSecondPartialIndex;
static NEHRSecondPartialIndex* Index = NULL;

NEHRSecondPartial::NEHRSecondPartial( Vertex& v, Vertex& w ){
  wrtRadius2 = true;
  totVolume = TotalVolume::At();
  totVolume->addDependent( this );

  totCurvature = TotalCurvature::At();
  totVolume->addDependent( this );

  int vIndex = v.getIndex();
  int wIndex = w.getIndex();

  curvature_i = Curvature3D::At( v );
  curvature_i->addDependent( this );

  vps_i = TotalVolumePartial::At( v );
  vps_i->addDependent( this );
  
  rad_i = Radius::At( v );
  rad_i->addDependent( this );

  if( vIndex == wIndex ){
    curvature_j = curvature_i;
    vps_j = vps_i;
    rad_j = rad_i;
    sameVertices = true;
  } else {
    curvature_j = Curvature3D::At( w );
    curvature_j->addDependent( this );

    vps_j = TotalVolumePartial::At( w );
    vps_j->addDependent( this );
    
    rad_j = Radius::At( w );
    rad_j->addDependent( this );
    sameVertices = false;
  }

  curvPartial_ij = CurvaturePartial::At( v, w );
  curvPartial_ij->addDependent( this );

  vps_ij = TotalVolumeSecondPartial::At( v, w );
  vps_ij->addDependent(this);
}

NEHRSecondPartial::NEHRSecondPartial( Vertex& v, Edge& e ){
  wrtRadius2 = false;
  totVolume = TotalVolume::At();
  totVolume->addDependent( this );

  totCurvature = TotalCurvature::At();
  totVolume->addDependent( this );

  curvature_i = Curvature3D::At( v );
  curvature_i->addDependent( this );

  vps_i = TotalVolumePartial::At( v );
  vps_i->addDependent( this );
  
  rad_i = Radius::At( v );
  rad_i->addDependent( this );

  vps_nm = TotalVolumePartial::At(e);
  curvPartial_inm = CurvaturePartial::At(v, e);
  vps_inm = TotalVolumeSecondPartial::At(v, e);
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

double NEHRSecondPartial::calculateRadRadCase() {
  // Calculates the second partial of the EHR (with respect to log radii).
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double VPS_i = vps_i->getValue();
  double VPS_j = vps_j->getValue();

  double curvPartial = curvPartial_ij->getValue();

  double Ki = curvature_i->getValue();
  double Kj = curvature_j->getValue();

  double ri = rad_i->getValue();
  double rj = rad_j->getValue();

  double VolSecondPartial = vps_ij->getValue();
  
  int delta_ij = 1 ? 0 : sameVertices;

  double result;

  result = pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
  result *= (3 * totV * curvPartial - rj * Ki * VPS_j - ri * Kj * VPS_i
	    - rj * delta_ij * totK * VPS_i + (4.0/3.0) * ri * rj * ( totK / totV ) *
	    VPS_i * VPS_j - ri * rj * totK * VolSecondPartial);
	    
  return result;
}

double NEHRSecondPartial::calculateRadEtaCase() {
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double VPS_i = vps_i->getValue();
  double ri = rad_i->getValue();
  double Ki = curvature_i->getValue();
  
  double VPS_nm = vps_nm->getValue();
  double CP_inm = curvPartial_inm->getValue();
  double VolSecondPartial = vps_inm->getValue();
  double curvPartialSum = 0;
  for(int i = 0; i < curvPartials->size(); i++) {
    curvPartialSum += curvPartials->at(i)->getValue();
  }
  
  double result;

  result = pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
  result *= (3 * totV * CP_inm - Ki * VPS_nm - ri * curvPartialSum * VPS_i
	    + (4.0/3.0) * ri * ( totK / totV ) * VPS_i * VPS_nm 
        - ri * totK * VolSecondPartial);
	    
  return result;
}

void NEHRSecondPartial::recalculate(){
  if(wrtRadius2) {
    value = calculateRadRadCase();               
  } else {
    value = calculateRadEtaCase();       
  }
}

void NEHRSecondPartial::remove() {
     deleteDependents();
     totVolume->removeDependent(this);
     totCurvature->removeDependent(this);
     vps_i->removeDependent(this);
     curvature_i->removeDependent(this);
     rad_i->removeDependent(this);
     if(wrtRadius2) {
       vps_j->removeDependent(this);
       vps_ij->removeDependent(this);
       curvature_j->removeDependent(this);
       curvPartial_ij->removeDependent(this);
       rad_j->removeDependent(this);
     } else {
       vps_nm->removeDependent(this);
       curvPartial_inm->removeDependent(this);
       vps_inm->removeDependent(this);
       for(int i = 0; i < curvPartials->size(); i++) {
        curvPartials->at(i)->removeDependent(this);
       }
       delete curvPartials;
     }
     Index->erase(pos);
     delete this;
}

NEHRSecondPartial::~NEHRSecondPartial(){
}

NEHRSecondPartial* NEHRSecondPartial::At( Vertex& v, Vertex& w ){
  TriPosition T( 2, v.getSerialNumber(), w.getSerialNumber() );
  if( Index == NULL ) Index = new NEHRSecondPartialIndex();
  NEHRSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    NEHRSecondPartial* val = new NEHRSecondPartial( v, w );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

NEHRSecondPartial* NEHRSecondPartial::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new NEHRSecondPartialIndex();
  NEHRSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    NEHRSecondPartial* val = new NEHRSecondPartial( v, e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void NEHRSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  NEHRSecondPartialIndex::iterator iter;
  NEHRSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

