#include "dih_angle.h"
#include "miscmath.h"

#include <stdio.h>

typedef map<TriPosition, DihedralAngle*, TriPositionCompare> DihedralAngleIndex;
static DihedralAngleIndex* Index = NULL;

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

void DihedralAngle::remove() {
     deleteDependents();
     angleA->removeDependent(this);
     angleB->removeDependent(this);
     angleC->removeDependent(this);
     Index->erase(pos);
     delete this;
}

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
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DihedralAngle::CleanUp(){
  if( Index == NULL ) return;
  DihedralAngleIndex::iterator iter;
  DihedralAngleIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void DihedralAngle::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  DihedralAngleIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
