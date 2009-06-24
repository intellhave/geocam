#ifndef CURVATUREPARTIAL_H_
#define CURVATUREPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "geoquants.h"
#include "triposition.h"

CurvaturePartialIndex* CurvaturePartial::Index = NULL;

CurvaturePartial::CurvaturePartial( Vertex& v, Vertex& w ){  
  verticesMatch = (v.getIndex() == w.getIndex());
  verticesAdjacent = ( v.isAdjVertex( w.getIndex() ) );

  if( ! ( verticesMatch || verticesAdjacent ) ) return;

  vRadius = Radius::At( v );
  vCurv = Curvature3D::At( v );

  dualAreas = new vector<DualArea*>();
  dihSums = new vector<DihedralAngleSum*>();
  lengths = new vector<Length*>();
  etas = new vector<Eta*>();
  radii = new vector<Radius*>();

  vector<int> edges;
  if( verticesMatch ){
    edges = *(v.getLocalEdges());
  } else if ( verticesAdjacent ){
    edges = listIntersection( v.getLocalEdges(), w.getLocalEdges() );
  }
  
  StdEdge se;
  DualArea* da; 
  DihedralAngleSum* ds;
  Length* l;
  Eta* eta;
  Radius* r;

  for(int ii = 0; ii < edges.size(); ii++){
    Edge& e = Triangulation::edgeTable[ edges[ii] ];

    da = DualArea::At( e );
    da->addDependent( this );
    dualAreas->push_back( da );

    ds = DihedralAngleSum::At( e );
    ds->addDependent( this );
    dihSums->push_back( ds );

    l = Length::At( e );
    l->addDependent( this );
    lengths->push_back( l );
    
    eta = Eta::At( e );
    eta->addDependent( this );
    etas->push_back( eta  );

    se = labelEdge( v, e );
    r = Radius::At( se.v2 );
    r->addDependent( this );
    radii->push_back( r );
  }
}

double CurvaturePartial::calculateEqualCase(){
  double rV = vRadius->getValue();
  double curv = vCurv->getValue();
  double dih_sum, len, Lij_star, eta, rW;
  
  double sum = 0.0;
  for(int ii; ii < dualAreas->size(); ii++) {
    Lij_star = dualAreas->at(ii)->getValue();
    dih_sum = dihSums->at(ii)->getValue();
    len = lengths->at(ii)->getValue();
    eta = etas->at(ii)->getValue();
    rW = radii->at(ii)->getValue();

    sum -= Lij_star / len;
    sum -= (2*PI - dih_sum)* rV*rV * rW*rW * (1 - eta*eta) / (len*len*len);
    sum += curv;
  }

  return sum;
}

double CurvaturePartial::calculateAdjCase(){
  double vr = vRadius->getValue();
  double wr = radii->at(0)->getValue();
  double Lvw_star = dualAreas->at(0)->getValue();
  double dih_sum = dihSums->at(0)->getValue();
  double l_vw = lengths->at(0)->getValue();
  double eta = etas->at(0)->getValue();
                
  return Lvw_star/l_vw - (2*PI - dih_sum)* 
    (vr*vr * wr*wr * (1-eta*eta))/(l_vw*l_vw*l_vw); 
}

void CurvaturePartial::recalculate(){
  if( verticesMatch ){
    value = calculateEqualCase();
  } else if( verticesAdjacent ) {
    value = calculateAdjCase();
  } else {
    value = 0.0;
  }
}

void CurvaturePartial::remove() {
    deleteDependents();
    for(int ii = 0; ii < dualAreas->size(); ii++) {
            dualAreas->at(ii)->removeDependent(this);
            dihSums->at(ii)->removeDependent(this);
            lengths->at(ii)->removeDependent(this);
            etas->at(ii)->removeDependent(this);
            radii->at(ii)->removeDependent(this);
    }
    Index->erase(pos);
    delete dualAreas;
    delete dihSums;
    delete lengths;
    delete etas;
    delete radii;
}

CurvaturePartial::~CurvaturePartial(){
    remove();
}

CurvaturePartial* CurvaturePartial::At( Vertex& v, Vertex& w ){
  TriPosition T( 2, v.getSerialNumber(), w.getSerialNumber() );
  if( Index == NULL ) Index = new CurvaturePartialIndex();
  CurvaturePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    CurvaturePartial* val = new CurvaturePartial( v, w );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void CurvaturePartial::CleanUp(){
  if( Index == NULL ) return;
  CurvaturePartialIndex::iterator iter;
  CurvaturePartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    delete iter->second;
  }
    
  delete Index;
  Index = NULL;
}

#endif /* CURVATUREPARTIAL_H_ */
