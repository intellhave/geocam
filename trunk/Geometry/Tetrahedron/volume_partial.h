#ifndef VOLUMEPARTIAL_H_
#define VOLUMEPARTIAL_H_

#include <map>
#include <new>
#include <cmath>

using namespace std;

#include "math/miscmath.h"
#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"
#include "alpha.h"

class VolumePartial : public virtual GeoQuant {
private:
  bool wrtRadius;
  
  Radius* rad[4];
  Alpha* alpha[4];
  Eta* eta[6];
  bool isIncident;

  double calculateRadiusCase();
  double calculateEtaCase();

protected:
  VolumePartial( Vertex& v, Tetra& t);
  VolumePartial( Edge& e, Tetra& t );
  void recalculate();

public:
  ~VolumePartial();
  static VolumePartial* At( Vertex& v, Tetra& t );
  static double valueAt(Vertex& v, Tetra& t) {
         return VolumePartial::At(v, t)->getValue();
  }
  static VolumePartial* At( Edge& e, Tetra& t );
  static double valueAt(Edge& e, Tetra& t) {
         return VolumePartial::At(e, t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* VOLUMEPARTIAL_H_ */
