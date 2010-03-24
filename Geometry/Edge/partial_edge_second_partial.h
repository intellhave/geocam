#ifndef PARTIALEDGESECONDPARTIAL_H_
#define PARTIALEDGESECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "length.h"
#include "eta.h"

class PartialEdgeSecondPartial : public virtual GeoQuant {
private:
  Radius* ri;
  Radius* rj;
  Alpha* ai;
  Alpha* aj;
  Eta* eij;
  
  bool equal;

protected:
  PartialEdgeSecondPartial( Vertex& v, Edge& e, Edge& f, Edge& g );
  void recalculate();

public:
  ~PartialEdgeSecondPartial();
  static PartialEdgeSecondPartial* At( Vertex& v, Edge& e, Edge& f, Edge& g );
  static double valueAt( Vertex& v, Edge& e, Edge& f, Edge& g ) {
         return PartialEdgeSecondPartial::At(v, e, f, g)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* PARTIALEDGESECONDPARTIAL_H_ */
