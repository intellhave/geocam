#ifndef VOLUMESECONDPARTIAL_H_
#define VOLUMESECONDPARTIAL_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"

class VolumeSecondPartial : public virtual GeoQuant {
private:
  Radius* rad[4];
  Eta* eta[6];
  bool sameVertices;

protected:
  VolumeSecondPartial( Vertex& v, Vertex& w, Tetra& t );
  void recalculate();

public:
  ~VolumeSecondPartial();
  void remove();
  static VolumeSecondPartial* At( Vertex& v, Vertex& w, Tetra& t );
  static double valueAt( Vertex& v, Vertex& w, Tetra& t ) {
         return VolumeSecondPartial::At( v, w, t)->getValue();
  }
  static void CleanUp();
  static void Record( char* filename );
};

#endif /* VOLUMESECONDPARTIAL_H_ */
