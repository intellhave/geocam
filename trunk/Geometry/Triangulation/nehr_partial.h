#ifndef NEHRPARTIAL_H_
#define NEHRPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "totalvolume.h"
#include "totalcurvature.h"
#include "total_volume_partial.h"
#include "curvature_partial.h"

class NEHRPartial : public virtual GeoQuant {
 private:
  bool wrtRadius;
  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  TotalVolumePartial* volPartial;
  
  // Radius only variables
  Curvature3D* localCurvature;
  Radius* rad;
  
  // Eta only variables
  vector<CurvaturePartial*>* curvPartials;
  
  double calculateRadiusCase();
  double calculateEtaCase();
  
 protected:
  NEHRPartial( Vertex& v );         
  NEHRPartial( Edge& e );
  void recalculate();

 public:
  ~NEHRPartial();
  static NEHRPartial* At( Vertex& v );
  static double valueAt( Vertex& v ) {
         return NEHRPartial::At(v)->getValue();
  }
  static NEHRPartial* At( Edge& e );
  static double valueAt( Edge& e ) {
         return NEHRPartial::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );       
};
#endif
