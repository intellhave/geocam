#ifndef RADIUS_H_
#define RADIUS_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

class Radius : public virtual GeoQuant {
protected:
  Radius( Vertex& v );
  void recalculate();

public:
  ~Radius();
  static Radius* At( Vertex& v );
  static void CleanUp();
};

#endif /* RADIUS_H_ */
