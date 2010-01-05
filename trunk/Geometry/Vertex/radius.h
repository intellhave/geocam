/**************************************************************
Class: Radius
Author: Alex Henniges?, Dan Champion?, Dave Glickenstein
Version: January 5, 2010
**************************************************************/

#ifndef RADIUS_H_
#define RADIUS_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

/*
 * The Radius class is a geoquant, mostly used in the calculation of length.
 * It is related to the conformal variable, which is generally log Radius.
 * Suppose e is an edge with vertices vi and vj.
 * The length is equal to:
 *   length(e) = sqrt( alpha(vi)*radius(vi) ^2 + alpha(vj)*radius(vj) ^2 +2*eta(e) * radius (vi) * radius (vj) )
 */

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
