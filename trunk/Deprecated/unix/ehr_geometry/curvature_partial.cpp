#include "curvature_partial.h"
#include "math/miscmath.h"

#include <utility>
#include <cstdio>

//static const double PI = 3.1415926;

typedef map<pair<int,int>, CurvaturePartial*> CurvaturePartialIndex;
static CurvaturePartialIndex* Index = NULL;

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
    Edge& ed = Triangulation::edgeTable[ edges[ii] ];

    da = DualArea::At( ed );
    da->addDependent( this );
    dualAreas->push_back( da );

    ds = DihedralAngleSum::At( ed );
    ds->addDependent( this );
    dihSums->push_back( ds );

    l = Length::At( ed );
    l->addDependent( this );
    lengths->push_back( l );
    
    eta = Eta::At( ed );
    eta->addDependent( this );
    etas->push_back( eta );

    se = labelEdge( ed, v );
    r = Radius::At( Triangulation::vertexTable[ se.v2 ] );
    r->addDependent( this );
    radii->push_back( r );
  }
}

double CurvaturePartial::calculateEqualCase(){
  double rV = vRadius->getValue();
  double curv = vCurv->getValue();
  double dih_sum, len, Lij_star, eta, rW;
  
  double sum = 0.0;

  for(int ii = 0; ii < dualAreas->size(); ii++) {
    Lij_star = dualAreas->at(ii)->getValue();
    dih_sum = dihSums->at(ii)->getValue();
    len = lengths->at(ii)->getValue();
    eta = etas->at(ii)->getValue();
    rW = radii->at(ii)->getValue();

    sum += 2.0*Lij_star/len - (2*PI - dih_sum)* pow(rV,2) * pow(rW,2) * (1 - pow(eta,2)) / pow(len,3);
  }

  sum += curv;

  return sum;
}

double CurvaturePartial::calculateAdjCase(){
  double vr = vRadius->getValue();
  double wr = radii->at(0)->getValue();
  double Lvw_star = dualAreas->at(0)->getValue();
  double dih_sum = dihSums->at(0)->getValue();
  double l_vw = lengths->at(0)->getValue();
  double eta = etas->at(0)->getValue();
                
  return -2*Lvw_star/l_vw + (2*PI - dih_sum)*(pow(vr,2)*pow(wr,2)*(1-pow(eta,2))/pow(l_vw, 3)); 
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
    Index->erase(pairPos);
    delete dualAreas;
    delete dihSums;
    delete lengths;
    delete etas;
    delete radii;
    delete this;
}

CurvaturePartial::~CurvaturePartial(){
}

CurvaturePartial* CurvaturePartial::At( Vertex& v, Vertex& w ){
  pair<int,int> P( v.getIndex(), w.getIndex() );

  if( Index == NULL) Index = new CurvaturePartialIndex();
  CurvaturePartialIndex::iterator iter = Index->find( P );

  if( iter == Index->end() ){
    CurvaturePartial* val = new CurvaturePartial( v, w );
    val->pairPos = P;
    Index->insert( make_pair( P, val ) );
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
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void CurvaturePartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  CurvaturePartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "(%d,%d): %lf\n", iter->first.first, iter->first.second, iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
