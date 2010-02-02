#include "nehr_second_partial.h"

#include <cstdio>

class NEHRSecondPartial;
typedef map<TriPosition, NEHRSecondPartial*, TriPositionCompare> NEHRSecondPartialIndex;
static NEHRSecondPartialIndex* Index = NULL;

NEHRSecondPartial::NEHRSecondPartial( Vertex& v, Vertex& w ){
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

  volSecPartials = new vector< VolumeSecondPartial* >();

  VolumeSecondPartial* vsp;
  vector<int>* localTetra = v.getLocalTetras();
  for(int ii = 0; ii < localTetra->size(); ii++){
    Tetra& t = Triangulation::tetraTable[ localTetra->at(ii) ];
    if( t.isAdjVertex( w.getIndex() ) ){
      vsp = VolumeSecondPartial::At( v, w, t );
      vsp->addDependent( this );
      volSecPartials->push_back( vsp );
    }
  }
}

void NEHRSecondPartial::recalculate(){
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

  double VolSumSecondPartial = 0;       
  for(int ii = 0; ii < volSecPartials->size(); ii++)
    VolSumSecondPartial += volSecPartials->at(ii)->getValue();

  value = pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
  value *= (3 * totV * curvPartial - Ki * VPS_j
	    - Kj * VPS_i + (4.0/3.0) * ( totK / totV ) *
	    VPS_i * VPS_j - totK * VolSumSecondPartial);
}

void NEHRSecondPartial::remove() {
     deleteDependents();
     totVolume->removeDependent(this);
     totCurvature->removeDependent(this);
     vps_i->removeDependent(this);
     vps_j->removeDependent(this);
     curvature_i->removeDependent(this);
     curvature_j->removeDependent(this);
     for(int ii = 0; ii < volSecPartials->size(); ii++)
       volSecPartials->at(ii)->removeDependent(this);
     Index->erase(pos);
     delete volSecPartials;
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

