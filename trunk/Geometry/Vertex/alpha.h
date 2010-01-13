/**************************************************************
Class: Alpha
Author: Alex Henniges?, Dan Champion?, Dave Glickenstein
Version: January 13, 2010
**************************************************************/

#ifndef ALPHA_H_
#define ALPHA_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

/*
 * The Alpha class is a geoquant used in the calculation of length.
 * Suppose e is an edge with vertices vi and vj.
 * The length is equal to:
 *   length(e) = sqrt( alpha(vi)*radius(vi) ^2 + alpha(vj)*radius(vj) ^2 +2*eta(e) * radius (vi) * radius (vj) )
 */


class Alpha : public virtual GeoQuant {

protected:
  Alpha( Vertex& v );
  void recalculate();

public:
  ~Alpha();
  static Alpha* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return Alpha::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
  static void print(FILE* out);
};

#endif /* ALPHA_H_ */
