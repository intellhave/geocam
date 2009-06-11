#ifndef DIHEDRALANGLE_H_
#define DIHEDRALANGLE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "euc_angle.cpp"
#include "triangulation/triangulation.h"
#include "simplex/edge.h"
#include "simplex/tetra.h"

#include "miscmath.h"


class DihedralAngle;
typedef map<TriPosition, DihedralAngle*, TriPositionCompare> DihedralAngleIndex;

class DihedralAngle : public virtual GeoQuant {
private:
  static DihedralAngleIndex* Index;
  GeoQuant* angleA;
  GeoQuant* angleB;
  GeoQuant* angleC;


protected:
  DihedralAngle( Edge& e, Tetra& t );
  void recalculate();

public:
  ~DihedralAngle();
  static DihedralAngle* At( Edge& e, Tetra& t );
  static double valueAt(Edge& e, Tetra& t) {
         return DihedralAngle::At(e, t)->getValue();
  }
  static void CleanUp();
};
DihedralAngleIndex* DihedralAngle::Index = NULL;

DihedralAngle::DihedralAngle( Edge& e, Tetra& t ){
  Vertex& v = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
    
  vector<int> faces = listIntersection(t.getLocalFaces(), v.getLocalFaces());
  vector<int> edge_faces = listIntersection(&faces, e.getLocalFaces());
  vector<int> not_edge_faces = listDifference(&faces, e.getLocalFaces());

  angleA = EuclideanAngle::At(v, Triangulation::faceTable[edge_faces[0]]);
  angleB = EuclideanAngle::At(v, Triangulation::faceTable[edge_faces[1]]);
  angleC = EuclideanAngle::At(v, Triangulation::faceTable[not_edge_faces[0]]);
 
  angleA->addDependent(this);
  angleB->addDependent(this);
  angleC->addDependent(this);
}

DihedralAngle::~DihedralAngle() {}
void DihedralAngle::recalculate() {
  double a = angleA->getValue();
  double b = angleB->getValue();
  double c = angleC->getValue();
  value =  acos( (cos(c)-cos(a)*cos(b)) / (sin(a)*sin(b)) );
}

DihedralAngle* DihedralAngle::At( Edge& e,Tetra& t ){
  TriPosition T( 2, e.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new DihedralAngleIndex();
  DihedralAngleIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DihedralAngle* val = new DihedralAngle( e, t );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DihedralAngle::CleanUp(){
  if( Index == NULL) return;
  DihedralAngleIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* DIHEDRALANGLE_H_ */
