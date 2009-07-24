#ifndef DIHEDRALANGLE_H_
#define DIHEDRALANGLE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "triangulation.h"
#include "euc_angle.h"

class DihedralAngle : public virtual GeoQuant {
private:
  GeoQuant* angleA;
  GeoQuant* angleB;
  GeoQuant* angleC;

protected:
  DihedralAngle( Edge& e, Tetra& t );
  void recalculate();

public:
  ~DihedralAngle();
  static DihedralAngle* At( Edge& e, Tetra& t );
  static double valueAt(Edge& e, Tetra& t) {
         return DihedralAngle::At(e, t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* DIHEDRALANGLE_H_ */
