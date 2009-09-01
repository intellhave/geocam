#ifndef GEOQUANT_H_
#define GEOQUANT_H_

#include "triposition.h"

#define PI 	3.141592653589793238

#include <new>
#include <list>
#include <map>
#include <algorithm>
using namespace std;

class GeoQuant {
 private:
  bool valid; /* This flag is set IN THIS CLASS ONLY!!! */

 protected:
  double value;
  TriPosition pos;
  list<GeoQuant*>* dependents;
  virtual void recalculate() = 0;
  virtual void remove() = 0;
  GeoQuant(){
    dependents = new list<GeoQuant*>();
    valid = false;
  }

 public:  
  ~GeoQuant() {}
  
  bool isValid(){return valid;}
  void addDependent(GeoQuant* dep){dependents->push_back(dep);}
  
  void removeDependent(GeoQuant* dep){
    dependents->remove(dep);
  }
  
  /* To be called only with deconstructor */
  void deleteDependents() {
    list<GeoQuant*>::iterator it;
    list<GeoQuant*> copy = *dependents;
    for(it = copy.begin(); it != copy.end(); it++) {
       (*it)->remove();
    }
    delete dependents;
  }


  
  double getValue(){
    if(! valid){
      recalculate();
      valid = true;
    }
    return value;
  }

  void setValue(double val){ 
    value = val; 
    invalidate();
    valid = true;
  }

  void notifyDependents(){
    list<GeoQuant*>::iterator it;
    for(it = dependents->begin(); it != dependents->end(); it++) {
       (*it)->invalidate();
    }
  }

  void invalidate(){
    if(valid){ 
      valid = false;
      notifyDependents();
    }
  }

  static void CleanUp(){}
};

/*
POSSIBLY ADD THESE LATER...
void GeoQuant::copyDependents(vector<GeoQuant*>* deps){
  unsigned int ii;
  for(ii = 0; ii < dependents.size(); ii++)
    deps.push_back( dependents.at(ii) );
}


*/

#endif  /* GEOQUANT_H_ */
