#ifndef LENGTH_H_
#define LENGTH_H_

#include <cmath>
#include <map>
/**************************************************************
Class: Length
Author: Alex Henniges?, Dan Champion?, Dave Glickenstein
Version: January 5, 2010
**************************************************************/


#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "triangulation/triangulation.h"

#include "radius.h"
#include "eta.h"
#include "alpha.h"

/*
 * The Length is a geoquant.
 * Suppose e is an edge with vertices vi and vj.
 * The length is equal to:
 *   length(e) = sqrt( alpha(vi)*radius(vi) ^2 + alpha(vj)*radius(vj) ^2 +2*eta(e) * radius (vi) * radius (vj) )
 * Note that Alpha was added January 4, 2010, and older versions may not use it. Alpha defaults to 1.0, which 
 *   should generally be backwards compatible.
 */

class Length : public virtual GeoQuant {
 private:
  Radius* radius1;
  Radius* radius2;
  Alpha* alpha1;
  Alpha* alpha2;
  Eta* eta;  

protected:
  Length( Edge& e );
  void recalculate();

public:
  ~Length();
  static Length* At( Edge& e );
  static double valueAt( Edge& e ) {
         return Length::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void print(FILE* out);
  
  
};

#endif /* LENGTH_H_ */
