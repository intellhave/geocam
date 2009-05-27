#include "length.h"
#include "triposition.h"

Length::Length(double val, Edge e) : GeoQuant() {
  position = new TriPosition( LENGTH, 1, e.getSerialNumber() );
  value = val;
  dataID = LENGTH; 
}

/* In the current implementation, lengths are
 * fixed or set by other procedures. */
void Length::recalculate(){}
