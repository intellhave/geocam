#ifndef EHRSECONDPARTIAL_H_
#define EHRSECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "totalvolume.h"
#include "totalcurvature.h"
#include "curvature3D.h"
#include "volume_partial_sum.h"
#include "volume_second_partial.h"
#include "curvature_partial.h"

class EHRSecondPartial : public virtual GeoQuant {
private:
  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  Curvature3D* curvature_i;
  Curvature3D* curvature_j;

  CurvaturePartial* curvPartial_ij;

  VolumePartialSum* vps_i;
  VolumePartialSum* vps_j;
  vector< VolumeSecondPartial* >* volSecPartials;
  
protected:
  EHRSecondPartial( Vertex& v, Vertex& w );
  void recalculate();

public:
  ~EHRSecondPartial();
  static EHRSecondPartial* At( Vertex& v, Vertex& w );
  static void CleanUp();
};

#endif /* EHRSECONDPARTIAL_H_ */
