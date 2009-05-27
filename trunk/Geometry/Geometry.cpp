#include "Geometry.h"
#include "ver2/geometry/geoquant.h"
#include "ver2/geometry/triposition.h"
#include "triangulation/triangulation.h"
#include "ver2/geometry/geoquants.h"

geometry Geometry::geo = Euclidean;
dimension Geometry::dim = TwoD;
double Geometry::spherRadius = 1;
GQIndex Geometry::gqi;
int Geometry::mode = FLOW;
/******************************************************************************/

void Geometry::build() {
  
  gqi.clear();
  
  switch(mode) {
    case FLOW: 
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
    case FLIP:
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
         // Build Dual Lengths
         
         break;
    default:
         printf("Error: Unidentifiable mode\n");
         return;
  }
}

void Geometry::setMode(int m) {
     mode = m;
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
/******************************************************************************/
