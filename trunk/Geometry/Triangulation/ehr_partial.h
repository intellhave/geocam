#ifndef EHRPARTIAL_H_
#define EHRPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "volume_partial.h"
#include "totalvolume.h"
#include "totalcurvature.h"
#include "curvature3D.h"
#include "volume_partial_sum.h"

class EHRPartial : public virtual GeoQuant {
private:
  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  Curvature3D* localCurvature;
  VolumePartialSum* vps;

protected:
  EHRPartial( Vertex& v );
  void recalculate();

public:
  ~EHRPartial();
    static EHRPartial* At( Vertex& v );
  static double valueAt( Vertex& v ) {
         return EHRPartial::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};

#endif /* EHRPARTIAL_H_ */
