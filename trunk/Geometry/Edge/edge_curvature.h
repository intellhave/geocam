#ifndef EDGECURVATURE_H_
#define EDGECURVATURE_H_

//#include <cmath>
#include <math.h>
#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "sectional_curvature.h"
#include "length.h"

class EdgeCurvature : public virtual GeoQuant {
private:
  GeoQuant* sectionalCurv;
  GeoQuant* length;

protected:
  EdgeCurvature( Edge& e );
  void recalculate();

public:
  ~EdgeCurvature();
  static EdgeCurvature* At( Edge& e  );
  static double valueAt(Edge& e) {
         return EdgeCurvature::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* EDGECURVATURE_H_ */
