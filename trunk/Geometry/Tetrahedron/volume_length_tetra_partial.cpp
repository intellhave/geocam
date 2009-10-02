#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "volume_length_tetra_partial.h"
#include "triposition.h"
#include "length.h"
#include "miscmath.h"
#include "triangulation.h"

typedef map<TriPosition, VolumeLengthTetraPartial*, TriPositionCompare> VolumeLengthTetraPartialIndex;
static VolumeLengthTetraPartialIndex* Index = NULL;

VolumeLengthTetraPartial::VolumeLengthTetraPartial( Edge& e, Tetra& t ){    
   StdTetra tetra = labelTetra(t, e);
   length12 = Length::At(e);
   length13 = Length::At(Triangulation::edgeTable[tetra.e13]);
   length14 = Length::At(Triangulation::edgeTable[tetra.e14]);
   length23 = Length::At(Triangulation::edgeTable[tetra.e23]);
   length24 = Length::At(Triangulation::edgeTable[tetra.e24]);
   length34 = Length::At(Triangulation::edgeTable[tetra.e34]);
   
   length12->addDependent(this);
   length13->addDependent(this);
   length14->addDependent(this);
   length23->addDependent(this);
   length24->addDependent(this);
   length34->addDependent(this);
}

void VolumeLengthTetraPartial::remove() {
     deleteDependents();
     
     length12->removeDependent(this);
     length13->removeDependent(this);
     length14->removeDependent(this);
     length23->removeDependent(this);
     length24->removeDependent(this);
     length34->removeDependent(this);   
       
     Index->erase(pos);
     delete this;
}

VolumeLengthTetraPartial::~VolumeLengthTetraPartial(){ }

void VolumeLengthTetraPartial::recalculate() {
    
    double L12 = length12->getValue();
    double L13 = length13->getValue();
    double L14 = length14->getValue();
    double L23 = length23->getValue();
    double L24 = length24->getValue();
    double L34 = length34->getValue();
    
    
    value = (-4*pow(L12,3)*pow(L34,2) + 2*L12*
      ((-pow(L13,2) + pow(L14,2))*(L23 - L24)*(L23 + L24) +
        (pow(L13,2) + pow(L14,2) + pow(L23,2) + pow(L24,2))*pow(L34,2) -
        pow(L34,4)))/
   (24.0*sqrt(-(pow(L13,4)*pow(L24,2)) - pow(L12,4)*pow(L34,2) +
       pow(L12,2)*((-pow(L13,2) + pow(L14,2))*(L23 - L24)*(L23 + L24) +
          (pow(L13,2) + pow(L14,2) + pow(L23,2) + pow(L24,2))*pow(L34,2) -
          pow(L34,4)) - pow(L23,2)*
        (pow(L14,4) + pow(L24,2)*pow(L34,2) +
          pow(L14,2)*(pow(L23,2) - pow(L24,2) - pow(L34,2))) +
       pow(L13,2)*(pow(L14,2)*(pow(L23,2) + pow(L24,2) - pow(L34,2)) +
          pow(L24,2)*(pow(L23,2) - pow(L24,2) + pow(L34,2)))));

}


VolumeLengthTetraPartial* VolumeLengthTetraPartial::At( Edge& e, Tetra& t  ){
  TriPosition T( 2, e.getSerialNumber(), t.getSerialNumber());
  if( Index == NULL ) Index = new VolumeLengthTetraPartialIndex();
  VolumeLengthTetraPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumeLengthTetraPartial* val = new VolumeLengthTetraPartial( e, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void VolumeLengthTetraPartial::CleanUp(){
  if( Index == NULL ) return;
  VolumeLengthTetraPartialIndex::iterator iter;
  VolumeLengthTetraPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}
