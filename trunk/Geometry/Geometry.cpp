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
         build(RADIUS);
         // Build Etas
         printf("Build Etas\n");
         build(ETA);
         // Build Lengths
         printf("Build Lengths\n");
         build(LENGTH);
         // Build Angles
         printf("Build Angles\n");
         build(ANGLE);
         // Build Areas
         printf("Build Areas\n");
         build(AREA);
         if(dim == ThreeD) {
            // Build Dihedral Angles
            printf("Build Dihederal Angles\n");
            build(DIHEDRAL_ANGLE);
            // Build Volumes
            printf("Build Volumes\n");
            build(VOLUME);
            // Build Partial Edges ( For Curvature)
            printf("Build Partial Edges\n");
            build(PARTIAL_EDGE); 
            // Build Edge Curvature
            printf("Build Edge Curvatures\n");
            build(EDGE_CURVATURE);
         }
         // Build Curvatures
         printf("Build Curvature\n");
         build(CURVATURE);
         break;
    case FLIP:
         // Build Radii
         build(RADIUS);
         // Build Lengths
         build(LENGTH);
         // Build Angles
         build(ANGLE);
         // Build Dual Lengths
         
         break;
    default:
         printf("Error: Unidentifiable mode\n");
         return;
  }
}

void Geometry::build(quantID quantType) {
     
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Face>::iterator fit;
     map<int, Tetra>::iterator tit;
     
     vector<int> edges;
     vector<int> faces;
     vector<int> tetras;
          
     switch(quantType) {
         case RADIUS:
              for(vit = Triangulation::vertexTable.begin(); 
                      vit != Triangulation::vertexTable.end(); vit++)
              {
                      Radius *r = new Radius(vit->second, gqi);
                      gqi[ r->getPosition() ] = r;
              }
              break;
         case ETA:
              for(eit = Triangulation::edgeTable.begin();
                      eit != Triangulation::edgeTable.end(); eit++) 
              {
                      Eta *e = new Eta(eit->second, gqi);
                      gqi[ e->getPosition() ] = e;
              }
              break;
         case LENGTH:
              for(eit = Triangulation::edgeTable.begin();
                      eit != Triangulation::edgeTable.end(); eit++) 
              {
                      Length *l = new Length(eit->second, gqi);
                      gqi[ l->getPosition()] = l;
              }
              break;
         case ANGLE:
              for(vit = Triangulation::vertexTable.begin(); 
                      vit != Triangulation::vertexTable.end(); vit++)
              {
                      faces = *(vit->second.getLocalFaces());
                      Face f;
                      switch(geo) {
                        case Euclidean:
                          for(int i = 0; i < faces.size(); i++)
                          {
                             f = Triangulation::faceTable[faces[i]];
                             EuclideanAngle *a = new EuclideanAngle(vit->second, f, gqi);
                             gqi[a->getPosition()] = a;           
                          }
                          break;
                        case Hyperbolic:
                          for(int i = 0; i < faces.size(); i++)
                          {
                             f = Triangulation::faceTable[faces[i]];
                             HyperbolicAngle *a = new HyperbolicAngle(vit->second, f, gqi);
                             gqi[a->getPosition()] = a;            
                          }
                          break;
                        case Spherical:
                          for(int i = 0; i < faces.size(); i++)
                          {
                             f = Triangulation::faceTable[faces[i]];
                             SphericalAngle *a = new SphericalAngle(vit->second, f, gqi);
                             gqi[a->getPosition()] = a;               
                          }
                          break;
                          default:
                               printf("Error: Unidentifiable geomtry\n");
                               return;
                      }                            
             }
             break;
         case AREA:
              for(fit = Triangulation::faceTable.begin(); 
                     fit != Triangulation::faceTable.end(); fit++)
              {
                     Area *a = new Area(fit->second, gqi);
                     gqi[a->getPosition()] = a; 
              }
              break;
         case  DIHEDRAL_ANGLE:
               for(eit = Triangulation::edgeTable.begin();
                    eit != Triangulation::edgeTable.end(); eit++)
               {
                    tetras = *(eit->second.getLocalTetras());
                    Tetra t;
                    for(int i = 0; i < tetras.size(); i++)
                    {
                       t = Triangulation::tetraTable[tetras[i]];
                       DihedralAngle *da = new DihedralAngle(eit->second, t, gqi);
                       gqi[da->getPosition()] = da;          
                    }             
               }
               break;
         case VOLUME:
              for(tit = Triangulation::tetraTable.begin();
                   tit != Triangulation::tetraTable.end(); tit++)
              {
                   Volume *v = new Volume(tit->second, gqi);
                   gqi[v->getPosition()] = v; 
              }
              break;
         case PARTIAL_EDGE:
              for(vit = Triangulation::vertexTable.begin(); 
                    vit != Triangulation::vertexTable.end(); vit++)
              {
                    edges = *(vit->second.getLocalEdges());
                    Edge e;
                    for(int i = 0; i < edges.size(); i++) {
                       e = Triangulation::edgeTable[edges[i]];
                       PartialEdge *pe = new PartialEdge(vit->second, e, gqi);
                       gqi[pe->getPosition()] = pe;         
                    }
              }
              break;
         case EDGE_CURVATURE:
               for(eit = Triangulation::edgeTable.begin();
                    eit != Triangulation::edgeTable.end(); eit++)
               {
                    EdgeCurvature *ec = new EdgeCurvature(eit->second, gqi);
                    gqi[ ec->getPosition()] = ec;
               }
               break;
         case CURVATURE:
             switch(dim) {
               case TwoD:
                 for(vit = Triangulation::vertexTable.begin(); 
                    vit != Triangulation::vertexTable.end(); vit++)
                 {
                      Curvature2D *curv2D = new Curvature2D(vit->second, gqi);
                      gqi[curv2D->getPosition()]  = curv2D;
                 }
                 break;
               case ThreeD:
                 for(vit = Triangulation::vertexTable.begin(); 
                    vit != Triangulation::vertexTable.end(); vit++)
                 {
                       Curvature3D *curv3D = new Curvature3D(vit->second, gqi);
                       gqi[curv3D->getPosition()]  = curv3D;
                 }
                 break;
               default:
                 printf("Error: Unidentifiable dimension \n");
                 return;
             }
             break;
         default:
             printf("Error: Unidentifiable type\n");
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
