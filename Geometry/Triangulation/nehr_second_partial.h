#ifndef NEHRSECONDPARTIAL_H_
#define NEHRSECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "totalvolume.h"
#include "totalcurvature.h"
#include "curvature3D.h"
#include "total_volume_partial.h"
#include "volume_second_partial.h"
#include "curvature_partial.h"
#include "total_volume_second_partial.h"

class NEHRSecondPartial : public virtual GeoQuant {
private:
  int form;

  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  Curvature3D* curvature_i;
  Curvature3D* curvature_j; /* Vertex only */

  CurvaturePartial* curvPartial_ij; /* Vertex only */

  TotalVolumePartial* vps_i;
  TotalVolumePartial* vps_j; /* Vertex only */
  TotalVolumeSecondPartial* vps_ij; /* Vertex only */
  Radius* rad_i;
  Radius* rad_j; /* Vertex only */
  
  bool sameVertices; /* Vertex only */
  
  TotalVolumePartial* vps_nm; /* Etas only */
  TotalVolumePartial* vps_op; /* Eta-Eta only */
  CurvaturePartial* curvPartial_inm; /* Vertex-Eta only */
  TotalVolumeSecondPartial* vps_inm; /* Vertex-Eta only */
  vector<CurvaturePartial*>* curvPartials_nm; /* Etas only */
  vector<CurvaturePartial*>* curvPartials_op; /* Eta-Eta only */
  
  TotalVolumSecondPartial* vps_nm_op; /* Eta-Eta only */
  vector<CurvatureSecondPartial*>* curvSecPartials; /* Eta-Eta only */

  double calculateRadRadCase();
  double calculateRadEtaCase();
  double calculateEtaEtaCase();
  
protected:
  NEHRSecondPartial( Vertex& v, Vertex& w );
  NEHRSecondPartial( Vertex& v, Edge& e );
  NEHRSecondPartial( Edge& e, Edge& f );
  void recalculate();

public:
  ~NEHRSecondPartial();
  static NEHRSecondPartial* At( Vertex& v, Vertex& w );
  static double valueAt(Vertex& v, Vertex& w) {
         return NEHRSecondPartial::At(v, w)->getValue();
  }
  static NEHRSecondPartial* At( Vertex& v, Edge& e );
  static double valueAt(Vertex& v, Edge& e) {
         return NEHRSecondPartial::At(v, e)->getValue();
  }
  static NEHRSecondPartial* At( Edge& e, Edge& f );
  static double valueAt( Edge& e, Edge& f ) {
         return NEHRSecondPartial::At(e, f)->getValue();
  }
  static void CleanUp();
  void remove();
};

#endif /* NEHRSECONDPARTIAL_H_ */
