#include "Geometry.h"
#include "geoquant.h"
#include "triposition.h"
#include "triangulation/triangulation.h"
#include "geoquants.h"

geometry Geometry::geo = Euclidean;
dimension Geometry::dim = TwoD;
double Geometry::spherRadius = 1;
GQIndex Geometry::gqi;
mode Geometry::type = Flow;
/******************************************************************************/

void Geometry::build() {
  
  gqi.clear();
  
  switch(type) {
    case Flow: 
         // Build Radii
         printf("Build Radii\n");
         Init_Radii(gqi);
         // Build Etas
         printf("Build Etas\n");
         Init_Etas(gqi);
         // Build Lengths
         printf("Build Lengths\n");
         Init_Lengths(gqi);
         // Build Angles
         printf("Build Angles\n");
         switch(geo) {
           case Euclidean:
                Init_EuclideanAngles(gqi);
                break;
           case Hyperbolic:
                Init_HyperbolicAngles(gqi);
                break;
           case Spherical:
                Init_SphericalAngles(gqi);
                break;
           default:
                fprintf(stderr, "Error: Invalid geometry.\n");
                return;            
         }
         // Build Areas
         printf("Build Areas\n");
         Init_Areas(gqi);
         switch(dim) {
           case ThreeD:
                // Build Dihedral Angles
                printf("Build Dihederal Angles\n");
                Init_DihedralAngles(gqi);
                // Build Volumes
                printf("Build Volumes\n");
                Init_Volumes(gqi);
                // Build Partial Edges ( For Curvature)
                printf("Build Partial Edges\n");
                Init_PartialEdges(gqi);
                // Build Edge Curvature
                printf("Build Edge Curvatures\n");
                Init_EdgeCurvatures(gqi);
                // Build 3D Curvature
                printf("Build 3D Curvatures\n");
                Init_Curvature3Ds(gqi);
                break;
             case TwoD:
                // Build 2D Curvature
                printf("Build 2D Curvatures\n");
                Init_Curvature2Ds(gqi);
                break;
             default:
                fprintf(stderr, "Error: Invalid dimension.\n");
                return;                 
         }
         break;
    case Flip:
         // Build Radii
         Init_Radii(gqi);
         // Build Lengths
         Init_Lengths(gqi);
         // Build Angles
         switch(geo) {
           case Euclidean:
                Init_EuclideanAngles(gqi);
                break;
           case Hyperbolic:
                Init_HyperbolicAngles(gqi);
                break;
           case Spherical:
                Init_SphericalAngles(gqi);
                break;
           default:
                fprintf(stderr, "Error: Invalid geometry.\n");
                return;            
         }         
         // Build Partial Edges
         printf("Build Partial Edges\n");
         Init_PartialEdges(gqi);         
         break;
    default:
         printf("Error: Unidentifiable mode\n");
         return;
  }
}

void Geometry::reset() {
    gqi.clear();    
}

void Geometry::setMode(mode m) {
     type = m;
}

void Geometry::setGeometry(geometry g) {
     geo = g;
}
void Geometry::setDimension(dimension d) {
     dim = d;    
}
void Geometry::setRadius(double rad) {
     spherRadius = rad;    
}
       
void Geometry::setRadius(Vertex& v, double rad) {
     TriPosition tp(RADIUS, 1, v.getSerialNumber());
     GeoQuant* radius = gqi[tp];
     
     radius->setValue(rad);
}
void Geometry::setEta(Edge& e, double eta) {
     TriPosition tp(ETA, 1, e.getSerialNumber());
     GeoQuant* etaGQ = gqi[tp];
     
     etaGQ->setValue(eta);     
}
void Geometry::setLength(Edge& e, double len) {
     TriPosition tp(LENGTH, 1, e.getSerialNumber());
     GeoQuant* length =  gqi[tp];
     
     length->setValue(len);   
}
       
double Geometry::radius(Vertex& v) {
       TriPosition tp(RADIUS, 1, v.getSerialNumber());
       GeoQuant* radius = gqi[tp];
       
       return radius->getValue();
}
double Geometry::eta(Edge& e) {
       TriPosition tp(ETA, 1, e.getSerialNumber());
       GeoQuant* etaGQ = gqi[tp];
     
       return etaGQ->getValue();           
}
double Geometry::length(Edge& e) {
       TriPosition tp(LENGTH, 1, e.getSerialNumber());
       GeoQuant* length =  gqi[tp];
     
       return length->getValue();       
}
double Geometry::angle(Vertex& v, Face& f) {
       TriPosition tp(ANGLE, 2, v.getSerialNumber(), f.getSerialNumber());
       GeoQuant* angle = gqi[tp];
       
       return angle->getValue();      
}
double Geometry::dihedralAngle(Edge& e, Tetra& t) {
       TriPosition tp(DIHEDRAL_ANGLE, 2, e.getSerialNumber(), t.getSerialNumber());
       GeoQuant* dihAng = gqi[tp];
       
       return dihAng->getValue();   
}
double Geometry::curvature(Vertex& v) {
       TriPosition tp(CURVATURE, 1, v.getSerialNumber());
       GeoQuant* curv = gqi[tp];
       
       return curv->getValue();     
}
double Geometry::area(Face& f) {
       TriPosition tp(AREA, 1, f.getSerialNumber());
       GeoQuant* area = gqi[tp];
       
       return area->getValue();
}
double Geometry::volume(Tetra& t) {
       TriPosition tp(VOLUME, 1, t.getSerialNumber());
       GeoQuant* volume = gqi[tp];
       
       return volume->getValue();
}
double Geometry::partialEdge(Vertex &v, Edge &e) {
       TriPosition tp(PARTIAL_EDGE, 2, v.getSerialNumber(), e.getSerialNumber());
       GeoQuant* pe = gqi[tp];
       
       return pe->getValue();      
}
double Geometry::edgeCurvature(Edge &e) {
       TriPosition tp(EDGE_CURVATURE, 1, e.getSerialNumber());
       GeoQuant* ec = gqi[tp];
       
       return ec->getValue();      
}


void Geometry::setRadii(double* radii)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = Triangulation::vertexTable.begin(); 
             vit != Triangulation::vertexTable.end(); vit++)
     {
       setRadius(vit->second, radii[i]);
       i++;
     }
}
void Geometry::getRadii(double* radii)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = Triangulation::vertexTable.begin(); 
             vit != Triangulation::vertexTable.end(); vit++)
     {
       radii[i] = radius(vit->second);
       i++;
     }
}
void Geometry::setLengths(double* lengths)
{
     map<int, Edge>::iterator eit;
     int i = 0;
     for(eit = Triangulation::edgeTable.begin(); 
             eit != Triangulation::edgeTable.end(); eit++)
     {
       setLength(eit->second, lengths[i]);
       i++;
     }
}
double Geometry::netCurvature() {
     map<int, Vertex>::iterator vit;
     double sum = 0;
     for(vit = Triangulation::vertexTable.begin(); 
             vit != Triangulation::vertexTable.end(); vit++)
     {
         sum += curvature(vit->second);        
     }
     return sum;
}

double Geometry::angle(double len1, double len2, double len3) {
     return acos((len1*len1 + len2*len2 - len3*len3)/ (2*len1*len2));  
}


double Geometry::CayleyVolumeDeriv(Tetra& t)
{
   int vertex, face;
   vector<int> edges1, edge23, edge24, edge34;
   double result=0.0;
  
   vertex = (*(t.getLocalVertices()))[0];
   edges1 = listIntersection(Triangulation::vertexTable[vertex].getLocalEdges(), t.getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[1]].getLocalFaces())[0];
   edge23 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge24 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[1]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge34 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
    
   double L12 = Geometry::length(Triangulation::edgeTable[edges1[0]]);
   double L13 = Geometry::length(Triangulation::edgeTable[edges1[1]]);
   double L14 = Geometry::length(Triangulation::edgeTable[edges1[2]]);
   double L23 = Geometry::length(Triangulation::edgeTable[edge23[0]]);
   double L24 = Geometry::length(Triangulation::edgeTable[edge24[0]]);
   double L34 = Geometry::length(Triangulation::edgeTable[edge34[0]]);
   
   double  Eta12 = Geometry::eta(Triangulation::edgeTable[edges1[0]]);
   double  Eta13 = Geometry::eta(Triangulation::edgeTable[edges1[1]]);
   double  Eta14 = Geometry::eta(Triangulation::edgeTable[edges1[2]]);
   double  Eta23 = Geometry::eta(Triangulation::edgeTable[edge23[0]]);
   double  Eta24 = Geometry::eta(Triangulation::edgeTable[edge24[0]]);
   double  Eta34 = Geometry::eta(Triangulation::edgeTable[edge34[0]]);
    
   int  V2 =  listIntersection(Triangulation::edgeTable[edge23[0]].getLocalVertices(), Triangulation::edgeTable[edge24[0]].getLocalVertices())[0];
   int  V3 =  listIntersection(Triangulation::edgeTable[edge23[0]].getLocalVertices(), Triangulation::edgeTable[edge34[0]].getLocalVertices())[0];
   int  V4 =  listIntersection(Triangulation::edgeTable[edge24[0]].getLocalVertices(), Triangulation::edgeTable[edge34[0]].getLocalVertices())[0];
   
   double  K1 =  Geometry::curvature(Triangulation::vertexTable[vertex]);
   double  K2 =  Geometry::curvature(Triangulation::vertexTable[V2]);
   double  K3 =  Geometry::curvature(Triangulation::vertexTable[V3]);
   double  K4 =  Geometry::curvature(Triangulation::vertexTable[V4]);
   
   double  R1 =  Geometry::radius(Triangulation::vertexTable[vertex]);
   double  R2 =  Geometry::radius(Triangulation::vertexTable[V2]);
   double  R3 =  Geometry::radius(Triangulation::vertexTable[V3]);
   double  R4 =  Geometry::radius(Triangulation::vertexTable[V4]);
 
result=(((Eta12* K2* pow(L13, 2)* pow(L23, 2)* R1 - Eta12* K2* pow(L14, 2)* pow(L23, 2)* R1 +
        K1* pow(L23, 4)* R1 - Eta12* K2* pow(L13, 2)* pow(L24, 2)* R1 +
        Eta12* K2* pow(L14, 2)* pow(L24, 2)* R1 - 2* K1* pow(L23, 2)* pow(L24, 2)* R1 +
        K1* pow(L24, 4)* R1 + 2* Eta12* K2* pow(L12, 2)* pow(L34, 2)* R1 -
        Eta12* K2* pow(L13, 2)* pow(L34, 2)* R1 - Eta12* K2* pow(L14, 2)* pow(L34, 2)* R1
-
        2* K1* pow(L23, 2)* pow(L34, 2)* R1 - Eta12* K2* pow(L23, 2)* pow(L34, 2)* R1 -
        2* K1* pow(L24, 2)* pow(L34, 2)* R1 - Eta12* K2* pow(L24, 2)* pow(L34, 2)* R1 +
        K1* pow(L34, 4)* R1 + Eta12* K2* pow(L34, 4)* R1 +
        Eta23* K3* pow(L12, 2)* pow(L13, 2)* R2 - Eta24* K4* pow(L12, 2)* pow(L13, 2)* R2
+
        K2* pow(L13, 4)* R2 + Eta24* K4* pow(L13, 4)* R2 -
        Eta23* K3* pow(L12, 2)* pow(L14, 2)* R2 + Eta24* K4* pow(L12, 2)* pow(L14, 2)* R2
-
        2* K2* pow(L13, 2)* pow(L14, 2)* R2 - Eta23* K3* pow(L13, 2)* pow(L14, 2)* R2 -
        Eta24* K4* pow(L13, 2)* pow(L14, 2)* R2 + K2* pow(L14, 4)* R2 +
        Eta23* K3* pow(L14, 4)* R2 + Eta12* K1* pow(L13, 2)* pow(L23, 2)* R2 -
        Eta24* K4* pow(L13, 2)* pow(L23, 2)* R2 - Eta12* K1* pow(L14, 2)* pow(L23, 2)* R2
+
        2* Eta23* K3* pow(L14, 2)* pow(L23, 2)* R2 - Eta24* K4* pow(L14, 2)* pow(L23, 2)*
R2 -
        Eta12* K1* pow(L13, 2)* pow(L24, 2)* R2 - Eta23* K3* pow(L13, 2)* pow(L24, 2)* R2
+
        2* Eta24* K4* pow(L13, 2)* pow(L24, 2)* R2 + Eta12* K1* pow(L14, 2)* pow(L24, 2)*
R2 -
        Eta23* K3* pow(L14, 2)* pow(L24, 2)* R2 + 2* Eta12* K1* pow(L12, 2)* pow(L34, 2)*
R2 -
        Eta23* K3* pow(L12, 2)* pow(L34, 2)* R2 - Eta24* K4* pow(L12, 2)* pow(L34, 2)* R2
-
        Eta12* K1* pow(L13, 2)* pow(L34, 2)* R2 - 2* K2* pow(L13, 2)* pow(L34, 2)* R2 -
        Eta24* K4* pow(L13, 2)* pow(L34, 2)* R2 - Eta12* K1* pow(L14, 2)* pow(L34, 2)* R2
-
        2* K2* pow(L14, 2)* pow(L34, 2)* R2 - Eta23* K3* pow(L14, 2)* pow(L34, 2)* R2 -
        Eta12* K1* pow(L23, 2)* pow(L34, 2)* R2 + Eta24* K4* pow(L23, 2)* pow(L34, 2)* R2
-
        Eta12* K1* pow(L24, 2)* pow(L34, 2)* R2 + Eta23* K3* pow(L24, 2)* pow(L34, 2)* R2
+
        Eta12* K1* pow(L34, 4)* R2 + K2* pow(L34, 4)* R2 + K3* pow(L12, 4)* R3 +
        Eta34* K4* pow(L12, 4)* R3 + Eta23* K2* pow(L12, 2)* pow(L13, 2)* R3 -
        Eta34* K4* pow(L12, 2)* pow(L13, 2)* R3 - Eta23* K2* pow(L12, 2)* pow(L14, 2)* R3
-
        2* K3* pow(L12, 2)* pow(L14, 2)* R3 - Eta34* K4* pow(L12, 2)* pow(L14, 2)* R3 -
        Eta23* K2* pow(L13, 2)* pow(L14, 2)* R3 + Eta34* K4* pow(L13, 2)* pow(L14, 2)* R3
+
        Eta23* K2* pow(L14, 4)* R3 + K3* pow(L14, 4)* R3 -
        Eta34* K4* pow(L12, 2)* pow(L23, 2)* R3 + 2* Eta23* K2* pow(L14, 2)* pow(L23, 2)*
R3 -
        Eta34* K4* pow(L14, 2)* pow(L23, 2)* R3 - 2* K3* pow(L12, 2)* pow(L24, 2)* R3 -
        Eta34* K4* pow(L12, 2)* pow(L24, 2)* R3 - Eta23* K2* pow(L13, 2)* pow(L24, 2)* R3
-
        Eta34* K4* pow(L13, 2)* pow(L24, 2)* R3 - Eta23* K2* pow(L14, 2)* pow(L24, 2)* R3
-
        2* K3* pow(L14, 2)* pow(L24, 2)* R3 + Eta34* K4* pow(L23, 2)* pow(L24, 2)* R3 +
        K3* pow(L24, 4)* R3 - Eta23* K2* pow(L12, 2)* pow(L34, 2)* R3 +
        2* Eta34* K4* pow(L12, 2)* pow(L34, 2)* R3 - Eta23* K2* pow(L14, 2)* pow(L34, 2)*
R3 +
        Eta23* K2* pow(L24, 2)* pow(L34, 2)* R3 +
        Eta13* ((pow(L12, 2)* ((pow(L23, 2) - pow(L24, 2) - pow(L34, 2))) +
              pow(L24, 2)* ((2* pow(L13, 2) - pow(L23, 2) + pow(L24, 2) - pow(L34, 2))) -
              pow(L14, 2)* ((pow(L23, 2) + pow(L24, 2) - pow(L34, 2)))))* ((K3* R1 +
              K1* R3)) + ((K4* ((L12 - L13 - L23))* ((L12 + L13 -
                    L23))* ((L12 - L13 + L23))* ((L12 + L13 + L23)) +
              Eta34* K3* ((pow(L12, 4) + ((L13 - L23))* ((L13 +
                          L23))* ((L14 - L24))* ((L14 + L24)) -
                    pow(L12, 2)* ((pow(L13, 2) + pow(L14, 2) + pow(L23, 2) + pow(L24, 2) -
                          2* pow(L34, 2))))) +
              Eta24* K2* ((pow(L13, 4) + pow(L23, 2)* (((-pow(L14, 2)) + pow(L34, 2))) -
                    pow(L12, 2)* ((pow(L13, 2) - pow(L14, 2) + pow(L34, 2))) -
                    pow(L13, 2)* ((pow(L14, 2) + pow(L23, 2) - 2* pow(L24, 2) +
                          pow(L34, 2)))))))* R4 -
        Eta14* ((pow(L13, 2)* ((pow(L23, 2) + pow(L24, 2) - pow(L34, 2))) +
              pow(L12, 2)* ((pow(L23, 2) - pow(L24, 2) + pow(L34, 2))) +
              pow(L23, 2)* (((-2)* pow(L14, 2) - pow(L23, 2) + pow(L24, 2) +
                    pow(L34, 2)))))* ((K4* R1 +
              K1* R4))))/((12* sqrt(((-pow(L13, 4))* pow(L24, 2) -
              pow(L12, 4)* pow(L34, 2) +
              pow(L12, 2)* (((((-pow(L13, 2)) + pow(L14, 2)))* ((L23 -
                          L24))* ((L23 + L24)) + ((pow(L13, 2) + pow(L14, 2) +
                          pow(L23, 2) + pow(L24, 2)))* pow(L34, 2) - pow(L34, 4))) -
              pow(L23, 2)* ((pow(L14, 4) + pow(L24, 2)* pow(L34, 2) +
                    pow(L14, 2)* ((pow(L23, 2) - pow(L24, 2) - pow(L34, 2))))) +
              pow(L13, 2)* ((pow(L14, 2)* ((pow(L23, 2) + pow(L24, 2) - pow(L34, 2))) +
                    pow(L24, 2)* ((pow(L23, 2) - pow(L24, 2) + pow(L34, 2))))))))));
  
// The calculation above is the derivative of the volume function (not squared).   
   
   return result;
}
/******************************************************************************/
