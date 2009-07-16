#ifndef GEOQUANTS_H_
#define GEOQUANTS_H_

#include "simplex/vertex.h"
#include "simplex/edge.h"
#include "simplex/face.h"
#include "simplex/tetra.h"

#include "triangulation/triangulation.h"

#include "geoquant.h"
#include "triposition.h"

#include <vector>
#include <cmath>

#define PI 	3.141592653589793238

/***** RADIUS *****/
class Radius;
typedef map<TriPosition, Radius*, TriPositionCompare> RadiusIndex;

class Radius : public virtual GeoQuant {
private:
  static RadiusIndex* Index;
protected:
  Radius( Vertex& v );
  void recalculate();
public:
  ~Radius();
  static Radius* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return Radius::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};
/******************/

/***** ETA *****/
class Eta;
typedef map<TriPosition, Eta*, TriPositionCompare> EtaIndex;

class Eta : public virtual GeoQuant {
private:
  static EtaIndex* Index;

protected:
  Eta( Edge& e );
  void recalculate();
  
public:
  ~Eta();
  static Eta* At( Edge& e );
  static double valueAt(Edge& e) {
         return Eta::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
};
/***************/

/***** LENGTH *****/
class Length;
typedef map<TriPosition, Length*, TriPositionCompare> LengthIndex;

class Length : public virtual GeoQuant {
private:
  static LengthIndex* Index;
  Radius* radius1;
  Radius* radius2;
  Eta* eta;  

protected:
  Length( Edge& e );
  void recalculate();
  
public:
  ~Length();
  static Length* At( Edge& e );
  static double valueAt( Edge& e ) {
         return Length::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
};
/******************/

/***** EUCLIDEAN ANGLE *****/
class EuclideanAngle;
typedef map<TriPosition, EuclideanAngle*, TriPositionCompare> EuclideanAngleIndex;

class EuclideanAngle : public virtual GeoQuant {
private:
  static EuclideanAngleIndex* Index;
  GeoQuant* lengthA;
  GeoQuant* lengthB;
  GeoQuant* lengthC;
  
protected:
  EuclideanAngle( Vertex& v, Face& f );
  void recalculate();

public:
  ~EuclideanAngle();
  static EuclideanAngle* At( Vertex& v, Face& f );
  static double valueAt(Vertex& v, Face& f) {
         return EuclideanAngle::At(v, f)->getValue();
  }
  static void CleanUp();
  void remove();
};
/**************************/

/***** AREA *****/
class Area;
typedef map<TriPosition, Area*, TriPositionCompare> AreaIndex;

class Area : public virtual GeoQuant {
private:
  static AreaIndex* Index;
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
/****************/

/***** 2D CURVATURE *****/
class Curvature2D;
typedef map<TriPosition, Curvature2D*, TriPositionCompare> Curvature2DIndex;

class Curvature2D : public virtual GeoQuant {
private:
  static Curvature2DIndex* Index;
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
/***********************/

/***** DIHEDRAL ANGLE *****/
class DihedralAngle;
typedef map<TriPosition, DihedralAngle*, TriPositionCompare> DihedralAngleIndex;

class DihedralAngle : public virtual GeoQuant {
private:
  static DihedralAngleIndex* Index;
  GeoQuant* angleA;
  GeoQuant* angleB;
  GeoQuant* angleC;


protected:
  DihedralAngle( Edge& e, Tetra& t );
  void recalculate();
  
public:
  ~DihedralAngle();
  static DihedralAngle* At( Edge& e, Tetra& t );
  static double valueAt(Edge& e, Tetra& t) {
         return DihedralAngle::At(e, t)->getValue();
  }
  static void CleanUp();
  void remove();
};
/*************************/

/***** Dihedral Angle Sum *****/
class DihedralAngleSum;
typedef map<TriPosition, DihedralAngleSum*, TriPositionCompare> DihedralAngleSumIndex;

class DihedralAngleSum : public virtual GeoQuant {
private:
  static DihedralAngleSumIndex* Index;
  vector<DihedralAngle*>* angles;

protected:
  DihedralAngleSum( Edge& e );
  void recalculate();
  
public:
  ~DihedralAngleSum();
  static DihedralAngleSum* At( Edge& e );
  static double valueAt( Edge& e ) {
         return DihedralAngleSum::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
};
/******************************/

/***** VOLUME *****/
class Volume;
typedef map<TriPosition, Volume*, TriPositionCompare> VolumeIndex;

class Volume : public virtual GeoQuant {
private:
  static VolumeIndex* Index;
  Length* len[6];

protected:
  Volume( Tetra& t );
  void recalculate();
  
public:
  ~Volume();
  static Volume* At( Tetra& t );
  static double valueAt(Tetra& t) {
         return Volume::At(t)->getValue();
  }
  static void CleanUp();
  void remove();
};
/******************/

/***** Total Volume *****/
class TotalVolume;

class TotalVolume : public virtual GeoQuant {
private:
  static TotalVolume* totVol;
  vector<Volume*>* volumes;

protected:
  TotalVolume();
  void recalculate();
  
public:
  ~TotalVolume();
  static TotalVolume* At();
  static double valueAt() {
         return TotalVolume::At()->getValue();
  }
  static void CleanUp();
  void remove();
};
/************************/

/***** EDGE CURVATURE *****/
class EdgeCurvature;
typedef map<TriPosition, EdgeCurvature*, TriPositionCompare> EdgeCurvatureIndex;

class EdgeCurvature : public virtual GeoQuant {
private:
  static EdgeCurvatureIndex* Index;
  vector<GeoQuant*>* dih_angles;

protected:
  EdgeCurvature( Edge& e );
  void recalculate();
  
public:
  ~EdgeCurvature();
  static EdgeCurvature* At( Edge& e  );
  static double valueAt(Edge& e) {
         return EdgeCurvature::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
};
/*************************/

/***** PARTIAL EDGE *****/
class PartialEdge;
typedef map<TriPosition, PartialEdge*, TriPositionCompare> PartialEdgeIndex;

class PartialEdge : public virtual GeoQuant {
private:
  static PartialEdgeIndex* Index;
  GeoQuant* length;
  GeoQuant* radA;
  GeoQuant* radB;


protected:
  PartialEdge( Vertex& v, Edge& e );
  void recalculate();
  
public:
  ~PartialEdge();
  static PartialEdge* At( Vertex& v, Edge& e  );
  static double valueAt(Vertex& v, Edge& e) {
         return PartialEdge::At(v, e)->getValue();
  }
  static void CleanUp();
  void remove();
};
/************************/

/***** 3D CURVATURE *****/
class Curvature3D;
typedef map<TriPosition, Curvature3D*, TriPositionCompare> Curvature3DIndex;

class Curvature3D : public virtual GeoQuant {
private:
  static Curvature3DIndex* Index;
  vector<GeoQuant*>* edgeCurvs;
  vector<GeoQuant*>* partials;

protected:
  Curvature3D( Vertex& v );
  void recalculate();
  
public:
  ~Curvature3D();
  static Curvature3D* At( Vertex& v );
  static double valueAt( Vertex& v ) { 
     return Curvature3D::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};
/***********************/

/***** Total Curvature *****/
class TotalCurvature;

class TotalCurvature : public virtual GeoQuant {
private:
  static TotalCurvature* totCurv;
  vector<Curvature3D*>* curvatures;

protected:
  TotalCurvature();
  void recalculate();
  
public:
  ~TotalCurvature();
  static TotalCurvature* At();
  static double valueAt() {
         return TotalCurvature::At()->getValue();
  }
  static void CleanUp();
  void remove();
};
/***************************/

/***** Volume Partial *****/
class VolumePartial;
typedef map<TriPosition, VolumePartial*, TriPositionCompare> VolumePartialIndex;

class VolumePartial : public virtual GeoQuant {
private:
  static VolumePartialIndex* Index;
  
  Radius* rad[4];
  Eta* eta[6];
 
protected:
  VolumePartial( Vertex& v, Tetra& t );
  void recalculate();
  
public:
  ~VolumePartial();
  static VolumePartial* At( Vertex& v, Tetra& t );
  static double valueAt(Vertex& v, Tetra& t) {
         return VolumePartial::At(v, t)->getValue();
  }
  static void CleanUp();
  void remove();
};
/*************************/

/***** Volume Partial Sum *****/
class VolumePartialSum;
typedef map<TriPosition, VolumePartialSum*, TriPositionCompare> VolumePartialSumIndex;

class VolumePartialSum : public virtual GeoQuant {
private:
  static VolumePartialSumIndex* Index;
  vector<VolumePartial*>* volPartials;

protected:
  VolumePartialSum( Vertex& v );
  void recalculate();
  
public:
  ~VolumePartialSum();
  static VolumePartialSum* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return VolumePartialSum::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};
/******************************/

/***** Volume Second Partial *****/
class VolumeSecondPartial;
typedef map<TriPosition, VolumeSecondPartial*, TriPositionCompare> VolumeSecondPartialIndex;

class VolumeSecondPartial : public virtual GeoQuant {
private:
  static VolumeSecondPartialIndex* Index;
  Radius* rad[4];
  Eta* eta[6];
  bool sameVertices;

protected:
  VolumeSecondPartial( Vertex& v, Vertex& w, Tetra& t );
  void recalculate();
  
public:
  ~VolumeSecondPartial();
  static VolumeSecondPartial* At( Vertex& v, Vertex& w, Tetra& t );
  static double valueAt( Vertex& v, Vertex& w, Tetra& t ) {
         return VolumeSecondPartial::At( v, w, t)->getValue();
  }
  static void CleanUp();
  void remove();
};
/*********************************/

/***** Edge Height *****/
class EdgeHeight;
typedef map<TriPosition, EdgeHeight*, TriPositionCompare> EdgeHeightIndex;

class EdgeHeight : public virtual GeoQuant {
private:
  static EdgeHeightIndex* Index;
  PartialEdge* d_ij;
  PartialEdge* d_ik;
  EuclideanAngle* theta_i;

protected:
  EdgeHeight( Edge& e, Face& f );
  void recalculate();
  
public:
  ~EdgeHeight();
  static EdgeHeight* At( Edge& e, Face& f );
  static double valueAt( Edge& e, Face& f ) {
        return EdgeHeight::At( e, f )->getValue();
  }
  static void CleanUp();
  void remove();
};
/***********************/

/***** Face Height *****/
class FaceHeight;
typedef map<TriPosition, FaceHeight*, TriPositionCompare> FaceHeightIndex;

class FaceHeight : public virtual GeoQuant {
private:
  static FaceHeightIndex* Index;
  EdgeHeight* hij_l;
  EdgeHeight* hij_k;
  DihedralAngle* beta_ij_kl;

protected:
  FaceHeight( Face& f, Tetra& t );
  void recalculate();
  
public:
  ~FaceHeight();
  static FaceHeight* At( Face& f, Tetra& t );
  static double valueAt( Face& f, Tetra& t ) {
        return FaceHeight::At( f, t )->getValue();
  }
  static void CleanUp();
  void remove();
};
/***********************/


/***** Dual Area Segment *****/
class DualAreaSegment;
typedef map<TriPosition, DualAreaSegment*, TriPositionCompare> DualAreaSegmentIndex;

class DualAreaSegment : public virtual GeoQuant {
private:
  static DualAreaSegmentIndex* Index;
  EdgeHeight* hij_k;
  EdgeHeight* hij_l;
  FaceHeight* hijk_l;
  FaceHeight* hijl_k;

protected:
  DualAreaSegment( Edge& e, Tetra& t );
  void recalculate();
  
public:
  ~DualAreaSegment();
  static DualAreaSegment* At( Edge& e, Tetra& t );
  static double valueAt( Edge& e, Tetra& t ) {
         return DualAreaSegment::At(e, t)->getValue();
  }
  static void CleanUp();
  void remove();
};
/****************************/

/***** Dual Area *****/
class DualArea;
typedef map<TriPosition, DualArea*, TriPositionCompare> DualAreaIndex;

class DualArea : public virtual GeoQuant {
private:
  static DualAreaIndex* Index;
  vector<DualAreaSegment*>* segments;

protected:
  DualArea( Edge& e );
  void recalculate();
  
public:
  ~DualArea();
  static DualArea* At( Edge& e );
  static double valueAt( Edge& e) {
         return DualArea::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
};
/*********************/

/***** Curvature Partial *****/
class CurvaturePartial;
typedef map<TriPosition, CurvaturePartial*, TriPositionCompare> CurvaturePartialIndex;

class CurvaturePartial : public virtual GeoQuant {
private:
  static CurvaturePartialIndex* Index;
  
  bool verticesMatch, verticesAdjacent;
  
  Radius* vRadius;
  Curvature3D* vCurv;

  vector<DualArea*>* dualAreas;
  vector<DihedralAngleSum*>* dihSums;
  vector<Length*>* lengths;
  vector<Eta*>* etas;
  vector<Radius*>* radii;

  double calculateEqualCase();
  double calculateAdjCase();

protected:
  CurvaturePartial( Vertex& v, Vertex& w );
  void recalculate();
  
public:
  ~CurvaturePartial();
  static CurvaturePartial* At( Vertex& v, Vertex& w );
  static double valueAt( Vertex& v, Vertex& w ) {
         return CurvaturePartial::At(v, w)->getValue();
  }
  static void CleanUp();
  void remove();
};
/*****************************/

/***** EHR Partial *****/
class EHRPartial;
typedef map<TriPosition, EHRPartial*, TriPositionCompare> EHRPartialIndex;

class EHRPartial : public virtual GeoQuant {
private:
  static EHRPartialIndex* Index;
  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  Curvature3D* localCurvature;
  VolumePartialSum* vps;

protected:
  EHRPartial( Vertex& v );
  void recalculate();
  
public:
  ~EHRPartial();
  static EHRPartial* At( Vertex& v );
  static double valueAt( Vertex& v ) {
         return EHRPartial::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
};
/***********************/

/***** EHR Second Partial *****/
class EHRSecondPartial;
typedef map<TriPosition, EHRSecondPartial*, TriPositionCompare> EHRSecondPartialIndex;

class EHRSecondPartial : public virtual GeoQuant {
private:
  static EHRSecondPartialIndex* Index;
  TotalVolume* totVolume;
  TotalCurvature* totCurvature;
  Curvature3D* curvature_i;
  Curvature3D* curvature_j;

  CurvaturePartial* curvPartial_ij;

  VolumePartialSum* vps_i;
  VolumePartialSum* vps_j;
  vector< VolumeSecondPartial* >* volSecPartials;
  
protected:
  EHRSecondPartial( Vertex& v, Vertex& w );
  void recalculate();
  
public:
  ~EHRSecondPartial();
  static EHRSecondPartial* At( Vertex& v, Vertex& w );
  static double valueAt(Vertex& v, Vertex& w) {
         return EHRSecondPartial::At(v, w)->getValue();
  }
  static void CleanUp();
  void remove();
};
/******************************/
#endif /* GEOQUANTS_H_ */
