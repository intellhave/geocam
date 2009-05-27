#ifndef EUCANGLE_H_
#define EUCANGLE_H_

#include <map>
using namespace std;

#include "geoquant.h"
#include "gqindex.h"
#include "vertex.h"
#include "face.h"

class EucAngle : GeoQuant {
 private:
  GeoQuant lengthA;
  GeoQuant lengthB;
  GeoQuant lengthC;
  
 public:  
  EucAngle(Vertex v, Face f, Map<Triposition, GeoQuant, TriPositionCompare>  gqi);
  ~EucAngle(){};
  void recalculate();
};

#endif /* EUCANGLE_H_ */
