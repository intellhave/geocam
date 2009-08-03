#ifndef GEOQUANT_H_
#define GEOQUANT_H_

#include "triposition.h"

#define PI 	3.141592653589793238

#include <new>
#include <vector>
#include <map>
#include <algorithm>
using namespace std;

class GeoQuant {
 private:
  bool valid; /* This flag is set IN THIS CLASS ONLY!!! */

 protected:
  double value;
  TriPosition pos;
  vector<GeoQuant*>* dependents;
  virtual void recalculate() = 0;
  virtual void remove() = 0;
  GeoQuant(){
    dependents = new vector<GeoQuant*>();
    valid = false;
  }

 public:  
  ~GeoQuant() {}
  
  bool isValid(){return valid;}
  void addDependent(GeoQuant* dep){dependents->push_back(dep);}
  
  void removeDependent(GeoQuant* dep){
    vector<GeoQuant*>::iterator it;
    it = find(dependents->begin(), dependents->end(), dep);
    if( it != dependents->end() ){
      dependents->erase(it);
      return;
    }
  }
  
  /* To be called only with deconstructor */
  void deleteDependents() {
    int size = dependents->size();
    vector<GeoQuant*> copy = *dependents;
    for(int ii = size - 1; ii >= 0; ii--) {
       copy.at(ii)->remove();
    }
    delete dependents;
  }


  
  double getValue(){
    if(! valid){
      recalculate();
      valid = true;
     // notifyDependents();
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
