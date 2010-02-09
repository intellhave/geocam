#ifndef _RADIUS_PARTIAL_H
#define _RADIUS_PARTIAL_H

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

class RadiusPartial : public virtual GeoQuant {
  private:
    static void calculateRadiiPartials();
    static void build();
    static void deconstruct();
  protected:
    RadiusPartial( Vertex& v, Edge& e );
    void recalculate();

  public:
    ~RadiusPartial();
    static RadiusPartial* At( Vertex& v, Edge& e );
    static double valueAt(Vertex& v, Edge& e) {
      return RadiusPartial::At(v, e)->getValue();
    }
    void remove();
    static void CleanUp();
};

#endif
