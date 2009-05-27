#ifndef GQUANTINDEX_H_
#define GQUANTINDEX_H_

#include "geoquant.h"

#include<new>
#include <vector>
using namespace std;

class GeoQuantIndex{
 private:
  /* An array of vectors, each containing GeoQuants of the same type.*/
  vector<GeoQuant*>** quantities; 

 public:
  GeoQuantIndex();
  ~GeoQuantIndex();
  bool add(GeoQuant* gq);

  GeoQuant* remove(quantID type, TriPosition position);
  GeoQuant* remove(quantID type, TriPosition* position){ 
    return remove(type, *position);
  }

  GeoQuant* lookup(quantID type, TriPosition position);  
  GeoQuant* lookup(quantID type, TriPosition* position){ 
    return lookup(type, *position);
  }  

  void printTables();

  vector<GeoQuant*>* clear(quantID type);

  int size();
};

#endif /* GQUANTINDEX_H_ */
