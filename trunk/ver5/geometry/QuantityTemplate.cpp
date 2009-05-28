#ifndef QUANTITY_H_
#define QUANTITY_H_

/* Common includes: */
#include <new>
#include <cmath>
#include <cstdio>

/* Geometry Includes: */
#include "geoquant.h"
#include "triposition.h"

/* Topology Includes: */
#include "triangulation.h"
#include "simplex.h"
#include "vertex.h"
#include "edge.h"
#include "face.h"
#include "tetra.h"

/* Miscellaneous IncludeS: */
#include "miscmath.h"

class quantity : public virtual GeoQuant {
private:
  /* Your private varaibles here. */

public:
  quantity( ??????, GQIndex& gqi);
  void recalculate();
};

quantity::quantity( ??????, GQIndex& gqi) : GeoQuant() {
  /* YOUR INITIALIZATION CODE GOES HERE:
   * 1) Establish a triposition
   * 2) Calculate dependencies (run AddDependents)
   * 3) initialize private variables.
   */
}

void quantity::recalculate(){
  /* Recalculate this quantity your chosen
   * private variables
   */
}

#endif /* QUANTITY_H_ */
