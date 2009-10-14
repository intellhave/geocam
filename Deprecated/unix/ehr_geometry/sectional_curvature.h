#ifndef SECTIONALCURVATURE_H_
#define SECTIONALCURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "dih_angle.h"

class SectionalCurvature : public virtual GeoQuant {
private:
  vector<GeoQuant*>* dih_angles;

protected:
  SectionalCurvature( Edge& e );
  void recalculate();

public:
  ~SectionalCurvature();
  static SectionalCurvature* At( Edge& e  );
  static double valueAt(Edge& e) {
         return SectionalCurvature::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* SECTIONALCURVATURE_H_ */
