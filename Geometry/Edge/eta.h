/**************************************************************
Class: Eta
Author: Alex Henniges?, Dan Champion?, Dave Glickenstein
Version: January 5, 2010
**************************************************************/

#ifndef ETA_H_
#define ETA_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

/*
 * The Eta class is a geoquant used in the calculation of length.
 * Suppose e is an edge with vertices vi and vj.
 * The length is equal to:
 *   length(e) = sqrt( alpha(vi)*radius(vi) ^2 + alpha(vj)*radius(vj) ^2 +2*eta(e) * radius (vi) * radius (vj) )
 */


class Eta : public virtual GeoQuant {

protected:
  Eta( Edge& e );
  void recalculate();

public:
  ~Eta();
  static Eta* At( Edge& e );
  static double valueAt(Edge& e) {
         return Eta::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void print(FILE* out);
};

#endif /* ETA_H_ */
