#ifndef CURVATUREPARTIAL_H_
#define CURVATUREPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

class CurvaturePartial;
typedef map<TriPosition, CurvaturePartial*, TriPositionCompare> CurvaturePartialIndex;

class CurvaturePartial : public virtual GeoQuant {
private:
  static CurvaturePartialIndex* Index;
  int case;
  
  Radius* rv;
  



protected:
  CurvaturePartial( Vertex& v, Vertex& w );
  void recalculate();

public:
  ~CurvaturePartial();
  static CurvaturePartial* At( Vertex& v, Vertex& w );
  static void CleanUp();
};
CurvaturePartialIndex* CurvaturePartial::Index = NULL;

CurvaturePartial::CurvaturePartial( Vertex& v, Vertex& w ){  
  verticesMatch = (v.getIndex == w.getIndex);
  verticesAdjacent = ( v.isAdjVertex( w ) );
}

static double calculateEqualCase(){
  edges = Triangulation::vertexTable[i].getLocalEdges();
  double ri = Geometry::radius(Triangulation::vertexTable[i]);
  double sum = 0.0;
  double dihedral_sum = 0.0;
                      
  for (int n=0; n < edges->size(); ++n) {
    int zork = edges->at(n);
    local_tetra = Triangulation::edgeTable[zork].getLocalTetras();
    dihedral_sum = 0.0;
    E = Triangulation::edgeTable[zork];
    Vprime = Triangulation::vertexTable[listDifference(E.getLocalVertices(), &Varray)[0]];
                
    for (int m=0; m < (*(local_tetra)).size(); ++m) {
      T = Triangulation::tetraTable[local_tetra->at(m)];
      dihedral_sum += Geometry::dihedralAngle(E,T);
    }

    sum += -1.0*(Lij_star(E)/(Geometry::length(E))
		 -(2*PI-dihedral_sum)*(pow(Geometry::radius(V), 2)*pow(Geometry::radius(Vprime),2)*(1-pow(Geometry::eta(E),2)))/pow(Geometry::length(E),3))+Geometry::curvature(V);
  }
  result = sum;
  return result;
}

static double calculateAdjCase(){
  double vr = vRadius->getValue();
  double wr = wRadius->getValue();
  double Lvw_star = vwDualArea->getValue();
  double dih_sum = dihedral_sum->getValue();
  double l_vw = length_vw->getValue();
  double eta = eta_vw->getValue();
                
  return Lij_star/l_vw - (2*PI - dih_sum)* 
    (vr*vr * vw*vw * (1-eta*eta))/(l_vw*l_vw*l_vw); 
}

void CurvaturePartial::recalculate(){
  if( verticesMatch ){
    value = calculateCase1();
  } else if( verticesAdjacent ) {
    value = calculateCase2();
  } else {
    value = 0.0;
  }
}

CurvaturePartial::~CurvaturePartial(){}

CurvaturePartial* CurvaturePartial::At( Vertex& v, Vertex& w ){
  TriPosition T( 2, v.getSerialNumber(), w.getSerialNumber() );
  if( Index == NULL ) Index = new CurvaturePartialIndex();
  CurvaturePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    CurvaturePartial* val = new CurvaturePartial( v, w );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void CurvaturePartial::CleanUp(){
  if( Index == NULL ) return;
  CurvaturePartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* CURVATUREPARTIAL_H_ */

