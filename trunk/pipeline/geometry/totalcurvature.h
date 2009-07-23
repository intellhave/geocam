#ifndef TOTALCURVATURE_H_
#define TOTALCURVATURE_H_

#include <vector>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "curvature3D.h"

class TotalCurvature;

class TotalCurvature : public virtual GeoQuant {
private:
  vector<Curvature3D*>* curvatures;

protected:
  TotalCurvature();
  void recalculate();

public:
  ~TotalCurvature();
  static TotalCurvature* At();
  static void CleanUp();
};

#endif /* TOTALCURVATURE_H_ */
