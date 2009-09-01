#ifndef EDGEHEIGHT_H_
#define EDGEHEIGHT_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "partial_edge.h"
#include "euc_angle.h"

class EdgeHeight : public virtual GeoQuant {
private:
  PartialEdge* d_ij;
  PartialEdge* d_ik;
  EuclideanAngle* theta_i;

protected:
  EdgeHeight( Edge& e, Face& f );
  void recalculate();

public:
  ~EdgeHeight();
  static EdgeHeight* At( Edge& e, Face& f );
  static double valueAt( Edge& e, Face& f ) {
        return EdgeHeight::At( e, f )->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* EDGEHEIGHT_H_ */
