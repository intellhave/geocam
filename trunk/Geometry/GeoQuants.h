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
};
/*************************/

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
};
/******************/

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
};
/***********************/

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
};
/*************************/

#endif /* GEOQUANTS_H_ */
