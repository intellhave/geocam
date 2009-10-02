#ifndef TWODCURVATURE_H_
#define TWODCURVATURE_H_

#include "geoquant.h"
#include "triangulation.h"

class Curvature2D : public virtual GeoQuant {
private:
  vector<GeoQuant*>* angles;

protected:
  Curvature2D( Vertex& v );
  void recalculate();
  
public:
  ~Curvature2D();
  static Curvature2D* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return Curvature2D::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};
#endif
