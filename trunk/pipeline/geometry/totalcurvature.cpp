#ifndef TOTALCURVATURE_H_
#define TOTALCURVATURE_H_

#include <vector>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"
#include "curvature3D.cpp"

class TotalCurvature;

class TotalCurvature : public virtual GeoQuant {
private:
  static TotalCurvature* totCurv;
  vector<Curvature3D*>* curvatures;

protected:
  TotalCurvature();
  void recalculate();

public:
  ~TotalCurvature();
  static TotalCurvature* At();
  static void CleanUp();
};
TotalCurvature* TotalCurvature::totCurv = NULL;

TotalCurvature::TotalCurvature(){
  curvatures = new vector<Curvature3D*>();
  
  Curvature3D* curv;
  map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin();
  while( vit != Triangulation::vertexTable.end() ){
    curv = Curvature3D::At( vit->second );
    curvatures->push_back( curv );
    curv->addDependent( this );
    vit++;
  }
}

TotalCurvature::~TotalCurvature(){
  delete curvatures;
}

void TotalCurvature::recalculate(){
  value = 0.0;
  Curvature3D* cur;
  for(int ii = 0; ii < curvatures->size(); ii++){
    cur = curvatures->at(ii);
    value += cur->getValue();
  }
}

TotalCurvature* TotalCurvature::At(){
  if( totCurv == NULL )
    totCurv = new TotalCurvature();
    
  return totCurv;
}

void TotalCurvature::CleanUp(){
  delete totCurv;
}

#endif /* TOTALCURVATURE_H_ */
