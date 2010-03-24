
#include "curvature_partial.h"
#include "math/miscmath.h"

#include <utility>
#include <cstdio>

//static const double PI = 3.1415926;

typedef map<pair<int,int>, CurvaturePartial*> CurvaturePartialIndex;
static CurvaturePartialIndex* Index = NULL;

CurvaturePartial::CurvaturePartial( Vertex& v, Vertex& w ){
  wrtRadius = true;
  verticesMatch = (v.getIndex() == w.getIndex());
  verticesAdjacent = ( v.isAdjVertex( w.getIndex() ) );

  if( ! ( verticesMatch || verticesAdjacent ) ) return;

  vRadius = Radius::At( v );
  vCurv = Curvature3D::At( v );

  vRadius->addDependent(this);
  vCurv->addDependent(this);

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

CurvaturePartial::CurvaturePartial( Vertex& v, Edge& e) {
   
   local = v.isAdjEdge(e.getIndex());
   
   dihPartials = new vector<vector<DihedralAnglePartial*>*>();
   dijs = new vector<PartialEdge*>();
   dihSum = DihedralAngleSum::At(e);
   if( local ) {
       dij_partial = PartialEdgePartial::At(v, e, e);
       dij_partial->addDependent(this);
   }
   
   vector<int> edges = *(v.getLocalEdges());
   vector<int> tetras;
   
   DihedralAnglePartial* dp;
   PartialEdge* dij;
   Edge mn;
   Tetra t;
   for(int i = 0; i < edges.size(); i++) {
      dihPartials->push_back(new vector<DihedralAnglePartial*>());
      mn = Triangulation::edgeTable[edges[i]];
      tetras = *(mn.getLocalTetras());
      for(int j = 0; j < tetras.size(); j++) {
         t = Triangulation::tetraTable[tetras[j]];
         dp = DihedralAnglePartial::At(e, mn, t);
         dp->addDependent(this);
         dihPartials->at(i)->push_back(dp);
      }
      dij = PartialEdge::At(v, mn);
      dij->addDependent(this);
      dijs->push_back(dij);
   }
   dihSum->addDependent(this);
   
   wrtRadius = false;
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

double CurvaturePartial::calculateEtaCase() {
  double partial;
  if( ! local ) {
    partial = 0;
  } else {
    double dih_angle_sum = dihSum->getValue();
    //printf("\tdih_sum = %f\n", dih_angle_sum);
    partial = (2 * PI - dih_angle_sum) * dij_partial->getValue();
  }
  vector<DihedralAnglePartial*>* current_vect;
  double dih_partial_sum;
  for(int i = 0; i < dihPartials->size(); i++) {
     current_vect = dihPartials->at(i);
     dih_partial_sum = 0;
     for(int j = 0; j < current_vect->size(); j++) {
        dih_partial_sum -= current_vect->at(j)->getValue();
     }
     //printf("\tdih_partial_sum %d = %f\n", i, dih_partial_sum); 
     partial += dih_partial_sum * dijs->at(i)->getValue();
  }
  return partial;
}

void CurvaturePartial::recalculate(){
  if(wrtRadius) { 
    if( verticesMatch ){
      value = calculateEqualCase();
    } else if( verticesAdjacent ) {
      value = calculateAdjCase();
    } else {
      value = 0.0;
    }
  } else {
    value = calculateEtaCase();
  }
}

void CurvaturePartial::remove() {
    deleteDependents();
    if(wrtRadius) {
       for(int ii = 0; ii < dualAreas->size(); ii++) {
            dualAreas->at(ii)->removeDependent(this);
            dihSums->at(ii)->removeDependent(this);
            lengths->at(ii)->removeDependent(this);
            etas->at(ii)->removeDependent(this);
            radii->at(ii)->removeDependent(this);
       }
    
      delete dualAreas;
      delete dihSums;
      delete lengths;
      delete etas;
      delete radii;
    } else {
      vector<DihedralAnglePartial*>* current_vect;
      for(int i = 0; i < dihPartials->size(); i++) {
         current_vect = dihPartials->at(i);
         for(int j = 0; j < current_vect->size(); j++) {
            current_vect->at(j)->removeDependent(this);
         }
         dijs->at(i)->removeDependent(this);
      }
      dihSum->removeDependent(this);
      if(local) {
        dij_partial->removeDependent(this);
      }
      delete dijs;
      delete dihPartials;
    }
    Index->erase(pairPos);
    delete this;
}

CurvaturePartial::~CurvaturePartial(){
}

CurvaturePartial* CurvaturePartial::At( Vertex& v, Vertex& w ){
  pair<int,int> P( v.getSerialNumber(), w.getSerialNumber() );

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

CurvaturePartial* CurvaturePartial::At( Vertex& v, Edge& e ){
  pair<int,int> P( v.getSerialNumber(), e.getSerialNumber() );

  if( Index == NULL) Index = new CurvaturePartialIndex();
  CurvaturePartialIndex::iterator iter = Index->find( P );

  if( iter == Index->end() ){
    CurvaturePartial* val = new CurvaturePartial( v, e );
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
