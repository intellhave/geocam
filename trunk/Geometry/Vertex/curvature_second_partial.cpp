
#include "curvature_second_partial.h"
#include "math/miscmath.h"

#include <utility>
#include <cstdio>

//static const double PI = 3.1415926;
class CurvatureSecondPartial;
typedef map<TriPosition, CurvatureSecondPartial*, TriPositionCompare> CurvatureSecondPartialIndex;
static CurvatureSecondPartialIndex* Index = NULL;

CurvatureSecondPartial::CurvatureSecondPartial( Vertex& v, Edge& e, Edge& f ){
  dih_sec_partials = new vector<vector<DihedralAngleSecondPartial*>*>();
  dih_partials_e = new vector<vector<DihedralAnglePartial*>*>();
  dih_partials_f= new vector<vector<DihedralAnglePartial*>*>();
  dihs= new vector<vector<DihedralAngle*>*>();

  dij_sec_partials = new vector<PartialEdgeSecondPartial*>();
  dij_partials_e = new vector<PartialEdgePartial*>();
  dij_partials_f = new vector<PartialEdgePartial*>();
  dijs = new vector<PartialEdge*>();
  
  DihedralAngleSecondPartial* dih_sec_partial;
  DihedralAnglePartial* dih_partial;
  DihedralAngle* dih;
  
  PartialEdgeSecondPartial* dij_sec_partial;
  PartialEdgePartial* dij_partial;
  PartialEdge* dij;
  
  
  vector<int> edges = *(v.getLocalEdges());
  vector<int> tetras;
  for(int j = 0; j < edges.size(); j++) {
    Edge ij = Triangulation::edgeTable[edges[j]];
    
    /* Dihedral Angles */
    dih_sec_partials->push_back(new vector<DihedralAngleSecondPartial*>());
    dih_partials_e->push_back(new vector<DihedralAnglePartial*>());
    dih_partials_f->push_back(new vector<DihedralAnglePartial*>());
    dihs->push_back(new vector<DihedralAngle*>());
    
    /* PartialEdgeSecondPartial */
    dij_sec_partial = PartialEdgeSecondPartial::At(v, ij, e, f);
    dij_sec_partial->addDependent(this);
    dij_sec_partials->push_back(dij_sec_partial);
    
    /* PartialEdgePartial */
    dij_partial = PartialEdgePartial::At(v, ij, e);
    dij_partial->addDependent(this);
    dij_partials_e->push_back(dij_partial);
    dij_partial = PartialEdgePartial::At(v, ij, f);
    dij_partial->addDependent(this);
    dij_partials_f->push_back(dij_partial);
    
    /* PartialEdge */
    dij = PartialEdge::At(v, ij);
    dij->addDependent(this);
    dijs->push_back(dij);
    
    tetras = *(ij.getLocalTetras());
    for(int k = 0; k < tetras.size(); k++) {
      Tetra t = Triangulation::tetraTable[tetras[k]];
      
      /* DihedralAngleSecondPartial */
      dih_sec_partial = DihedralAngleSecondPartial::At(e, f, ij, t);
      dih_sec_partial->addDependent(this);
      dih_sec_partials->at(j)->push_back(dih_sec_partial);
      
      /* DihedralAnglePartial */
      dih_partial = DihedralAnglePartial::At(e, ij, t);
      dih_partial->addDependent(this);
      dih_partials_e->at(j)->push_back(dih_partial);
      dih_partial = DihedralAnglePartial::At(f, ij, t);
      dih_partial->addDependent(this);
      dih_partials_f->at(j)->push_back(dih_partial);
      
      /* DihedralAngle */
      dih = DihedralAngle::At(ij, t);
      dih->addDependent(this);
      dihs->at(j)->push_back(dih);
    }
  }
 // printf("%d:(%d, %d) = \n", v.getIndex(), e.getIndex(), f.getIndex());
}

void CurvatureSecondPartial::recalculate(){
  value = 0;
  double edge_sum;
  double dih_sec_partial_sum;
  double dih_partial_e_sum;
  double dih_partial_f_sum;
  double dih_sum;
  double temp;
  vector<DihedralAngleSecondPartial*>* edge_dih_sec_partial;
  vector<DihedralAnglePartial*>* edge_dih_partial_e;
  vector<DihedralAnglePartial*>* edge_dih_partial_f;
  vector<DihedralAngle*>* edge_dih;
  for(int j = 0; j < dih_sec_partials->size(); j++) {
    dih_sec_partial_sum = 0;
    dih_partial_e_sum = 0;
    dih_partial_f_sum = 0;
    dih_sum = 2 * PI;
    
    edge_dih_sec_partial = dih_sec_partials->at(j);
    edge_dih_partial_e = dih_partials_e->at(j);
    edge_dih_partial_f = dih_partials_f->at(j);
    edge_dih = dihs->at(j);
    for(int k = 0; k < edge_dih_sec_partial->size(); k++) {
      dih_sec_partial_sum -= edge_dih_sec_partial->at(k)->getValue();
      dih_partial_e_sum -= edge_dih_partial_e->at(k)->getValue();
      dih_partial_f_sum -= edge_dih_partial_f->at(k)->getValue();
      dih_sum -= edge_dih->at(k)->getValue();
    }
    temp = dih_sec_partial_sum * dijs->at(j)->getValue()
           + dih_partial_e_sum * dij_partials_f->at(j)->getValue()
           + dih_partial_f_sum * dij_partials_e->at(j)->getValue()
           + dih_sum * dij_sec_partials->at(j)->getValue();
    value += temp;
   // printf("\t%f\n", temp);
  }
}

void CurvatureSecondPartial::remove() {
  deleteDependents();
  for(int i = 0; i < dih_sec_partials->size(); i++) {

    for(int k = 0; k < dih_sec_partials->at(i)->size(); k++) {
      /* DihedralAngleSecondPartial */
      dih_sec_partials->at(i)->at(k)->removeDependent(this);

      /* DihedralAnglePartial */
      dih_partials_e->at(i)->at(k)->removeDependent(this);
      dih_partials_f->at(i)->at(k)->removeDependent(this);

      /* DihedralAngle */
      dihs->at(i)->at(k)->removeDependent(this);
    }

    /* Dihedral Angles */
    delete dih_sec_partials->at(i);
    delete dih_partials_e->at(i);
    delete dih_partials_f->at(i);
    delete dihs->at(i);

    /* PartialEdgeSecondPartial */
    dij_sec_partials->at(i)->removeDependent(this);

    /* PartialEdgePartial */
    dij_partials_e->at(i)->removeDependent(this);
    dij_partials_f->at(i)->removeDependent(this);

    /* PartialEdge */
    dijs->at(i)->removeDependent(this);
  }
  delete dih_sec_partials;
  delete dih_partials_e;
  delete dih_partials_f;
  delete dihs;
  
  delete dij_sec_partials;
  delete dij_partials_e;
  delete dij_partials_f;
  delete dijs;
  

  Index->erase(pos);
  delete this;
}

CurvatureSecondPartial::~CurvatureSecondPartial(){
}

CurvatureSecondPartial* CurvatureSecondPartial::At( Vertex& v, Edge& e, Edge& f ){
  TriPosition T( 3, v.getSerialNumber(), e.getSerialNumber(), f.getSerialNumber() );
  if( Index == NULL ) Index = new CurvatureSecondPartialIndex();
  CurvatureSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    CurvatureSecondPartial* val = new CurvatureSecondPartial( v, e, f );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void CurvatureSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  CurvatureSecondPartialIndex::iterator iter;
  CurvatureSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }

  delete Index;
  Index = NULL;
}

