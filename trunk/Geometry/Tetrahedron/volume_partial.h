#ifndef VOLUMEPARTIAL_H_
#define VOLUMEPARTIAL_H_

#include <map>
#include <new>
#include <cmath>

using namespace std;

#include "miscmath.h"
#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"

class VolumePartial : public virtual GeoQuant {
private:
  Radius* rad[4];
  Eta* eta[6];
  bool isIncident;

protected:
  VolumePartial( Vertex& v, Tetra& t );
  void recalculate();

public:
  ~VolumePartial();
  static VolumePartial* At( Vertex& v, Tetra& t );
  static double valueAt(Vertex& v, Tetra& t) {
         return VolumePartial::At(v, t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* VOLUMEPARTIAL_H_ */
