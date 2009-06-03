/**************************************************************
Class: GeoQuant
Author: Alex Henniges, Joe Thomas
Version: June 3, 2009

 * This is the abstract class that represents a quantity in our
 * geometry. We consider a quantity to be a value that is defined
 * by one or more simplices. We use this definition as the basis
 * for accessing the quantities.
 *
 * We also define here a dependency tree. When a quantity is
 * calculated using a set of other quantities, it is considered
 * to be dependent on those quantities. When this quantity is created,
 * the quantities it depends on our notified of its existence. When
 * one of these other quantities' value changes, the dependent quantity
 * is considered invalid and must be recalculated once it is requested
 * 
**************************************************************/

#ifndef GEOQUANT_H_
#define GEOQUANT_H_

#include "triposition.h"

#include <cstdio>
#include <new>
#include <vector>
#include <map>
using namespace std;

static const int TOTALTYPES = 3; 

class GeoQuant {
 private:
  bool valid; /* This flag is set IN THIS CLASS ONLY!!! */

 protected:
  quantID dataID;
  double value;

  vector<GeoQuant*>* dependents;
  TriPosition* position;
  virtual void recalculate() = 0;

 public:
  GeoQuant(){
    dependents = new vector<GeoQuant*>();
    valid = false;
  }

  ~GeoQuant(){
    //delete dependents;
  }

  quantID getType(){ return dataID; }
  bool isValid(){return valid;}
  TriPosition getPosition(){ return *position; } 

  void addDependent(GeoQuant* dep){
    dependents->push_back(dep);
  }

  // void copyDependents(vector<GeoQuant*>* v);
  // void dropDependent(GeoQuant* dep);

  double getValue(){
    if(! valid){
      recalculate();
      valid = true;
      notifyDependents();
    }
    return value;
  }

  void setValue(double val){ 
    value = val; 
    invalidate();
    valid = true;
  }

  void notifyDependents(){
    unsigned int ii;
    for(ii = 0; ii < dependents->size(); ii++)
      dependents->at(ii)->invalidate();
  }

  void invalidate(){
    if(valid){ 
      valid = false;
      notifyDependents();
    }
  }
};

typedef map<TriPosition, GeoQuant*, TriPositionCompare> GQIndex;


/*
POSSIBLY ADD THIS LATER...

void GeoQuant::copyDependents(vector<GeoQuant*>* deps){
  unsigned int ii;
  for(ii = 0; ii < dependents.size(); ii++)
    deps.push_back( dependents.at(ii) );
}

void GeoQuant::dropDependent(GeoQuant* dep){
  for(int ii = 0; ii < dependents->size(); ii++)
    if(dependents->at(ii) == dep){
      dependents->erase(ii);
      return;
    }
}
*/


#endif  /* GEOQUANT_H_ */
