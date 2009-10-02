#ifndef AREA_H_
#define AREA_H_

#include "geoquant.h"
#include "triposition.h"
#include "triangulation.h"

#include "length.h"

class Area : public virtual GeoQuant {
private:
  GeoQuant* Len[3];
  
protected:
  Area( Face& f );
  void recalculate();
  
public:
  ~Area();
  static Area* At( Face& f );
  static double valueAt(Face& f) {
         return Area::At(f)->getValue();
  }
  static void CleanUp();
  void remove();
};
#endif
