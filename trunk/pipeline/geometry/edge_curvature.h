#ifndef EDGECURVATURE_H_
#define EDGECURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "dih_angle.h"

class EdgeCurvature : public virtual GeoQuant {
private:
  vector<GeoQuant*>* dih_angles;

protected:
  EdgeCurvature( Edge& e );
  void recalculate();

public:
  ~EdgeCurvature();
  static EdgeCurvature* At( Edge& e );
  static void CleanUp();
  static void Record( char* filename );
};

#endif /* EDGECURVATURE_H_ */
