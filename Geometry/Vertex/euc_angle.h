#ifndef EUCLIDEANANGLE_H_
#define EUCLIDEANANGLE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "triangulation/triangulation.h"

#include "length.h"

class EuclideanAngle : public virtual GeoQuant {
private:
  GeoQuant* lengthA;
  GeoQuant* lengthB;
  GeoQuant* lengthC;
  Face* fa;

protected:
  EuclideanAngle( Vertex& v, Face& f );
  void recalculate();

public:
  ~EuclideanAngle();
  static EuclideanAngle* At( Vertex& v, Face& f );
  static double valueAt(Vertex& v, Face& f) {
         return EuclideanAngle::At(v, f)->getValue();
  }
  static void CleanUp();
  void remove();
  static void print( FILE* out );
};

#endif /* EUCLIDEANANGLE_H_ */
