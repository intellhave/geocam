#ifndef GEOQUANT_H_
#define GEOQUANT_H_

#include "triposition.h"

#define PI 	3.141592653589793238

#include <new>
#include <vector>
#include <map>
using namespace std;

class GeoQuant {
 private:
  bool valid; /* This flag is set IN THIS CLASS ONLY!!! */

 protected:
  double value;
  vector<GeoQuant*>* dependents;
  virtual void recalculate() = 0;

  GeoQuant(){
    dependents = new vector<GeoQuant*>();
    valid = false;
  }

 public:  
  virtual ~GeoQuant(){
    delete dependents;
  }

  bool isValid(){return valid;}
  void addDependent(GeoQuant* dep){dependents->push_back(dep);}

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

  static void CleanUp(){}
};

/*
POSSIBLY ADD THESE LATER...
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
