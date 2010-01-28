#ifndef DIHEDRALPARTIAL_H_
#define DIHEDRALPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"
#include "alpha.h"

class DihedralAnglePartial : public virtual GeoQuant {
private:
  
  Radius* rad[4];
  Alpha* alpha[4];
  Eta* eta[6];
  int locality;

protected:
  DihedralAnglePartial( Edge& e1, Edge& e2, Tetra& t );
  void recalculate();

public:
  ~DihedralAnglePartial();
  static DihedralAnglePartial* At( Edge& e1, Edge& e2, Tetra& t );
  static double valueAt(Edge& e1, Edge& e2, Tetra& t) {
         return DihedralAnglePartial::At(e1, e2, t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* DIHEDRALPARTIAL_H_ */
