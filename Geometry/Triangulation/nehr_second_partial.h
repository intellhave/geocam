#ifndef NEHRSECONDPARTIAL_H_
#define NEHRSECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "totalvolume.h"
#include "totalcurvature.h"
#include "curvature3D.h"
#include "total_volume_partial.h"
#include "volume_second_partial.h"
#include "curvature_partial.h"

class NEHRSecondPartial : public virtual GeoQuant {
private:
  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  Curvature3D* curvature_i;
  Curvature3D* curvature_j;

  CurvaturePartial* curvPartial_ij;

  TotalVolumePartial* vps_i;
  TotalVolumePartial* vps_j;
  vector< VolumeSecondPartial* >* volSecPartials;
  Radius* rad_i;
  Radius* rad_j;
  
  bool sameVertices;
  
protected:
  NEHRSecondPartial( Vertex& v, Vertex& w );
  void recalculate();

public:
  ~NEHRSecondPartial();
  static NEHRSecondPartial* At( Vertex& v, Vertex& w );
  static double valueAt(Vertex& v, Vertex& w) {
         return NEHRSecondPartial::At(v, w)->getValue();
  }
  static void CleanUp();
  void remove();
};

#endif /* NEHRSECONDPARTIAL_H_ */
