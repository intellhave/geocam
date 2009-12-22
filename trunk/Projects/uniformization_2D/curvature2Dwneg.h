#ifndef TWODCURVATURE_H_
#define TWODCURVATURE_H_

#include "geoquant.h"
#include "triangulation/triangulation.h"

class Curvature2Dwneg : public virtual GeoQuant {
private:
  vector<GeoQuant*>* angles;

protected:
  Curvature2Dwneg( Vertex& v );
  void recalculate();
  
public:
  ~Curvature2Dwneg();
  static Curvature2Dwneg* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return Curvature2Dwneg::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};
#endif
