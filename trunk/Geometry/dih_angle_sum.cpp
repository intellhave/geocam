#ifndef DIHEDRALANGLESUM_H_
#define DIHEDRALANGLESUM_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "geoquants.h"
#include "triposition.h"

DihedralAngleSumIndex* DihedralAngleSum::Index = NULL;

DihedralAngleSum::DihedralAngleSum( Edge& e ){
  angles = new vector<DihedralAngle*>();
  
  DihedralAngle* angle;
  vector<int>* tetras = e.getLocalTetras();
  for(int ii = 0; ii < tetras->size(); ii++){
    Tetra& t = Triangulation::tetraTable[ tetras->at( ii ) ];
    angle = DihedralAngle::At( e, t );
    angles->push_back( angle );
    angle->addDependent( this );
  }
}

void DihedralAngleSum::recalculate(){
  value = 0.0;
  DihedralAngle* angle;
  for(int ii = 0; ii < angles->size(); ii++){
    angle = angles->at(ii);
    value += angle->getValue();
  }
}

DihedralAngleSum::~DihedralAngleSum(){
  delete angles;
}

DihedralAngleSum* DihedralAngleSum::At( Edge& e ){
  TriPosition T( 1 , e.getSerialNumber() );
  if( Index == NULL ) Index = new DihedralAngleSumIndex();
  DihedralAngleSumIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DihedralAngleSum* val = new DihedralAngleSum( e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DihedralAngleSum::CleanUp(){
  if( Index == NULL ) return;
  DihedralAngleSumIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* DIHEDRALANGLESUM_H_ */
