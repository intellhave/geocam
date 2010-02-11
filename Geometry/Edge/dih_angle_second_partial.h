#ifndef DIHEDRALSECONDPARTIAL_H_
#define DIHEDRALSECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "eta.h"
#include "alpha.h"

class DihedralAngleSecondPartial : public virtual GeoQuant {
private:
  
  Radius* rad[4];
  Alpha* alpha[4];
  Eta* eta[6];
  int locality;

protected:
  /* Dih_Angle At (e3, t) wrt eta_e1, eta_e2 */
  DihedralAngleSecondPartial( Edge& e1, Edge& e2, Edge& e3, Tetra& t );
  void recalculate();

public:
  ~DihedralAngleSecondPartial();
  static DihedralAngleSecondPartial* At( Edge& e1, Edge& e2, Edge& e3, Tetra& t );
  static double valueAt(Edge& e1, Edge& e2, Edge& e3, Tetra& t) {
         return DihedralAngleSecondPartial::At(e1, e2, e3, t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* DIHEDRALSECONDPARTIAL_H_ */
