#ifndef VOLUMESECONDPARTIAL_H_
#define VOLUMESECONDPARTIAL_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"
#include "alpha.h"
#include "volume_partial.h"

class VolumeSecondPartial : public virtual GeoQuant {
private:
  Radius* rad[4];
  Alpha* alpha[4];
  Eta* eta[6];
  int wrt;
  
  // RadiusRadius only variable
  bool sameVertices;
  VolumePartial* volume_partial;
  //RadiusEta only variable
  int locality;
  
  double calculateRadRadCase();
  double calculateRadEtaCase();
  double calculateEtaEtaCase();
protected:
  VolumeSecondPartial( Vertex& v, Vertex& w, Tetra& t );
  VolumeSecondPartial( Vertex& v, Edge& e, Tetra& t );
  VolumeSecondPartial( Edge& e, Edge& f, Tetra& t );
  void recalculate();

public:
  ~VolumeSecondPartial();
  void remove();
  static VolumeSecondPartial* At( Vertex& v, Vertex& w, Tetra& t );
  static double valueAt( Vertex& v, Vertex& w, Tetra& t ) {
         return VolumeSecondPartial::At( v, w, t)->getValue();
  }
  static VolumeSecondPartial* At( Vertex& v, Edge& e, Tetra& t );
  static double valueAt( Vertex& v, Edge& e, Tetra& t ) {
         return VolumeSecondPartial::At( v, e, t)->getValue();
  }
  static VolumeSecondPartial* At( Edge& e, Edge& f, Tetra& t );
  static double valueAt( Edge& e, Edge& f, Tetra& t ) {
         return VolumeSecondPartial::At( e, f, t)->getValue();
  }
  static void CleanUp();
  static void Record( char* filename );
};

#endif /* VOLUMESECONDPARTIAL_H_ */
