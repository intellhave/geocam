#ifndef VOL_LEN_TETRAPARTIAL_H_
#define VOL_LEN_TETRAPARTIAL_H_

#include "geoquant.h"
#include "triangulation/triangulation.h"

class VolumeLengthTetraPartial : public virtual GeoQuant {
private:
  GeoQuant* length12;
  GeoQuant* length13;
  GeoQuant* length14;
  GeoQuant* length23;
  GeoQuant* length24;
  GeoQuant* length34;
  

protected:
  VolumeLengthTetraPartial( Edge& e, Tetra& t );
  void recalculate();
  
public:
  ~VolumeLengthTetraPartial();
  static VolumeLengthTetraPartial* At( Edge& e, Tetra& t );
  static double valueAt(Edge& e, Tetra& t) {
         return VolumeLengthTetraPartial::At(e, t)->getValue();
  }
  static void CleanUp();
  void remove();
};
#endif /* VOL_LEN_TETRAPARTIAL */
