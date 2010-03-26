#include "nehr_second_partial.h"

#include <cstdio>

class NEHRSecondPartial;
typedef map<TriPosition, NEHRSecondPartial*, TriPositionCompare> NEHRSecondPartialIndex;
static NEHRSecondPartialIndex* Index = NULL;

NEHRSecondPartial::NEHRSecondPartial( Vertex& v, Vertex& w ){
  form = 0;
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
  form = 1;
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
  curvPartials_nm = new vector<CurvaturePartial*>();
  
  map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin();
  
  CurvaturePartial* cp;
  while( vit != Triangulation::vertexTable.end() ){
    cp = CurvaturePartial::At( vit->second, e );
    cp->addDependent( this );
    curvPartials_nm->push_back( cp );
    vit++;
  } 
}

NEHRSecondPartial::NEHRSecondPartial( Edge& e, Edge& f ){
  form = 2;
  totVolume = TotalVolume::At();
  totVolume->addDependent( this );

  totCurvature = TotalCurvature::At();
  totVolume->addDependent( this );

  vps_nm = TotalVolumePartial::At(e);
  vps_nm->addDependent(this);
  vps_op = TotalVolumePartial::At(f);
  vps_op->addDependent(this);
  
  vps_nm_op = TotalVolumeSecondPartial::At(e, f);
  
  curvPartials_nm = new vector<CurvaturePartial*>();
  curvPartials_op = new vector<CurvaturePartial*>();
  curvSecPartials = new vector<CurvatureSecondPartial*>();
  
  map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin();
  CurvaturePartial* cp;
  CurvatureSecondPartial* csp;
  while( vit != Triangulation::vertexTable.end() ){
    cp = CurvaturePartial::At( vit->second, e );
    cp->addDependent( this );
    curvPartials_nm->push_back( cp );
    
    cp = CurvaturePartial::At( vit->second, f );
    cp->addDependent( this );
    curvPartials_op->push_back( cp );
    
    csp = CurvatureSecondPartial::At( vit->second, e, f );
    csp->addDependent(this);
    curvSecPartials->push_back( csp );
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
  for(int i = 0; i < curvPartials_nm->size(); i++) {
    curvPartialSum += curvPartials_nm->at(i)->getValue();
  }
  
  double result;

  result = pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
  result *= (3 * totV * CP_inm - Ki * VPS_nm - ri * curvPartialSum * VPS_i
	    + (4.0/3.0) * ri * ( totK / totV ) * VPS_i * VPS_nm 
        - ri * totK * VolSecondPartial);
	    
  return result;
}

double NEHRSecondPartial::calculateEtaEtaCase() {
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double VPS_nm = vps_nm->getValue();
  double VPS_op = vps_op->getValue();
  double VolSecondPartial = vps_nm_op->getValue();

  double curvPartialSum_nm = 0;
  for(int i = 0; i < curvPartials_nm->size(); i++) {
    curvPartialSum_nm += curvPartials_nm->at(i)->getValue();
  }
  double curvPartialSum_op = 0;
  for(int i = 0; i < curvPartials_op->size(); i++) {
    curvPartialSum_op += curvPartials_op->at(i)->getValue();
  }
  double curvSecPartialSum = 0;
  for(int i = 0; i < curvSecPartials->size(); i++) {
    curvSecPartialSum +=  curvSecPartials->at(i)->getValue();
  }
  
  double result;

  result = pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
  result *= (3 * totV * curvSecPartialSum - curvPartialSum_nm * VPS_op
          - curvPartialSum_op * VPS_nm
	        + (4.0/3.0) * ( totK / totV ) * VPS_op * VPS_nm
          - totK * VolSecondPartial);

  return result;
}

void NEHRSecondPartial::recalculate(){
  if(form == 0) {
    value = calculateRadRadCase();               
  } else if(form == 1) {
    value = calculateRadEtaCase();       
  } else {
    value = calculateEtaEtaCase();
  }
}

void NEHRSecondPartial::remove() {
     deleteDependents();
     totVolume->removeDependent(this);
     totCurvature->removeDependent(this);

     if(form == 0) {
       vps_i->removeDependent(this);
       curvature_i->removeDependent(this);
       rad_i->removeDependent(this);
       vps_j->removeDependent(this);
       vps_ij->removeDependent(this);
       curvature_j->removeDependent(this);
       curvPartial_ij->removeDependent(this);
       rad_j->removeDependent(this);
     } else if(form == 1) {
       vps_i->removeDependent(this);
       curvature_i->removeDependent(this);
       rad_i->removeDependent(this);
       vps_nm->removeDependent(this);
       curvPartial_inm->removeDependent(this);
       vps_inm->removeDependent(this);
       for(int i = 0; i < curvPartials_nm->size(); i++) {
        curvPartials_nm->at(i)->removeDependent(this);
       }
       delete curvPartials_nm;
     } else {
       vps_nm->removeDependent(this);
       vps_op->removeDependent(this);
       vps_nm_op->removeDependent(this);
       for(int i = 0; i < curvPartials_nm->size(); i++) {
        curvPartials_nm->at(i)->removeDependent(this);
        curvPartials_op->at(i)->removeDependent(this);
        curvSecPartials->at(i)->removeDependent(this);
       }
       delete curvPartials_nm;
       delete curvPartials_op;
       delete curvSecPartials;
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

NEHRSecondPartial* NEHRSecondPartial::At( Edge& e, Edge& f ){
  TriPosition T( 2, e.getSerialNumber(), f.getSerialNumber() );
  if( Index == NULL ) Index = new NEHRSecondPartialIndex();
  NEHRSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    NEHRSecondPartial* val = new NEHRSecondPartial( e, f );
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

