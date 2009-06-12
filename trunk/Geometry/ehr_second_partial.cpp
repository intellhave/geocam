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
  static double valueAt(Face& f) {
         return Area::At(f)->getValue();
  }
  static void CleanUp();
};
EHRSecondPartialIndex* EHRSecondPartial::Index = NULL;

EHRSecondPartial::EHRSecondPartial( SIMPLICES ){}

void EHRSecondPartial::recalculate(){}

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

