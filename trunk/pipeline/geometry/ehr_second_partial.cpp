#ifndef EHRSECONDPARTIAL_H_
#define EHRSECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

class EHRSecondPartial;
typedef map<TriPosition, EHRSecondPartial*, TriPositionCompare> EHRSecondPartialIndex;

class EHRSecondPartial : public virtual GeoQuant {
private:
  static EHRSecondPartialIndex* Index;

protected:
  EHRSecondPartial( SIMPLICES );
  void recalculate();

public:
  ~EHRSecondPartial();
  static EHRSecondPartial* At( SIMPLICES );
  static void CleanUp();
};
EHRSecondPartialIndex* EHRSecondPartial::Index = NULL;

EHRSecondPartial::EHRSecondPartial( SIMPLICES ){}

void EHRSecondPartial::recalculate(){
  // Calculates the second partial of the EHR (with respect to log radii).
  double V = Total_Volume();
  double K = Total_Curvature();
  double result = 0;
  double VolSumPartial_i = 0;
  double VolSumPartial_j = 0;
  double VolSumSecondPartial = 0;
  map<int, Tetra>::iterator tit;
       
  for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++){
      VolSumPartial_i += Volume_Partial(i,tit->second);
       
      VolSumPartial_j += Volume_Partial(j,tit->second);
      VolSumSecondPartial += Volume_Second_Partial(i, j, tit->second);
  }

  value = pow(V, (-4.0/3.0))*(1.0/3.0)*(3*V*Curvature_Partial(i,j)
					-Geometry::curvature(Triangulation::vertexTable[i])*VolSumPartial_j
					-Geometry::curvature(Triangulation::vertexTable[j])*VolSumPartial_i
					+(4.0/3.0)*K*pow(V, -1.0)*VolSumPartial_i*VolSumPartial_j
					-K*VolSumSecondPartial);
                       
}

EHRSecondPartial::~EHRSecondPartial(){}

EHRSecondPartial* EHRSecondPartial::At( SIMPLICES ){
  TriPosition T( NUMSIMPLICES, SIMPLICES );
  if( Index == NULL ) Index = new EHRSecondPartialIndex();
  EHRSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    EHRSecondPartial* val = new EHRSecondPartial( SIMPLICES );
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

