#ifndef RADIUS_H_
#define RADIUS_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

class Radius : public virtual GeoQuant {
protected:
  Radius( Vertex& v );
  void recalculate();

public:
  ~Radius();
  static Radius* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return Radius::At(v)->getValue();
  }
  static void CleanUp();
  static void print(FILE* out);
  void remove();
};

#endif /* RADIUS_H_ */
