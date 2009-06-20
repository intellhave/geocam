#ifndef EHRSECONDPARTIAL_H_
#define EHRSECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "geoquants.h"
#include "triposition.h"


EHRSecondPartialIndex* EHRSecondPartial::Index = NULL;

EHRSecondPartial::EHRSecondPartial( Vertex& v, Vertex& w ){
  totVolume = TotalVolume::At();
  totVolume->addDependent( this );

  totCurvature = TotalCurvature::At();
  totVolume->addDependent( this );

  curvature_i = Curvature3D::At( v );
  curvature_i->addDependent( this );
  curvature_j = Curvature3D::At( w );
  
  if( curvature_j != curvature_i )
    curvature_j->addDependent( this );

  vps_i = VolumePartialSum::At( v );
  vps_i->addDependent( this );

  vps_j = VolumePartialSum::At( w );
  if( vps_j != vps_i )
    vps_j->addDependent( this );

  volSecPartials = new vector< VolumeSecondPartial* >();

  curvPartial_ij = CurvaturePartial::At( v, w );

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

void EHRSecondPartial::recalculate(){
  // Calculates the second partial of the EHR (with respect to log radii).
  double totV = totVolume->getValue();
  double totK = totCurvature->getValue();
  double VPS_i = vps_i->getValue();
  double VPS_j = vps_j->getValue();

  double curvPartial = curvPartial_ij->getValue();

  double Ki = curvature_i->getValue();
  double Kj = curvature_j->getValue();

  double VolSumSecondPartial = 0;       
  for(int ii = 0; ii < volSecPartials->size(); ii++)
    VolSumSecondPartial += volSecPartials->at(ii)->getValue();

  value = pow(totV, (-4.0/3.0));
  value *= (1.0/3.0) * (3 * totV * curvPartial - Ki * VPS_j
			- Kj * VPS_i + (4.0/3.0) * totK * ( 1 / totV ) *
			VPS_i * VPS_j - totK * VolSumSecondPartial);
}

EHRSecondPartial::~EHRSecondPartial(){
  delete volSecPartials;
}

EHRSecondPartial* EHRSecondPartial::At( Vertex& v, Vertex& w ){
  TriPosition T( 2, v.getSerialNumber(), w.getSerialNumber() );
  if( Index == NULL ) Index = new EHRSecondPartialIndex();
  EHRSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EHRSecondPartial* val = new EHRSecondPartial( v, w );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void EHRSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  EHRSecondPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* EHRSECONDPARTIAL_H_ */

