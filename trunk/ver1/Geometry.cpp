#include "Geometry.h"
#include "geoquant.h"
#include "triposition.h"
#include "triangulation/triangulation.h"

geometry geo = -1;
dimension dim = -1;
double spherRadius = 1;
GQIndex gqi;
int mode = FLOW;
/******************************************************************************/

// PRIVATE METHODS

double Geometry::ang(double lengthA, double lengthB, double lengthC)
{
       //               a^2 + b^2 - c^2
       //  (/) = acos( ----------------- )
       //                     2ab
       return acos((lengthA*lengthA + lengthB*lengthB - lengthC*lengthC)
                                    / (2*lengthA*lengthB));
}

double Geometry::ang(Vertex *v, Face *f)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v->getLocalEdges(), f->getLocalEdges());
     Edge *e1 = &(Triangulation::edgeTable[sameAs[0]]);
     Edge *e2 = &(Triangulation::edgeTable[sameAs[1]]);
     diff = listDifference(f->getLocalEdges(), v->getLocalEdges());
     Edge *e3 = &(Triangulation::edgeTable[diff[0]]);
     
     double l1 = length(e1);
     double l2 = length(e2);
     double l3 = length(e3);
     
     return ang(l1, l2, l3);       
}

double Geometry::spherAng(double lengthA, double lengthB, double lengthC, double radius)
{
   return acos((cos(lengthC/radius)-cos(lengthA/radius)*cos(lengthB/radius))
                              / (sin(lengthA/radius)*sin(lengthB/radius)));
}

double Geometry::spherAng(Vertex v, Face f, double radius)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
     Edge e1 = Triangulation::edgeTable[sameAs[0]];
     Edge e2 = Triangulation::edgeTable[sameAs[1]];
     diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
     Edge e3 = Triangulation::edgeTable[diff[0]];
     
     return spherAng(e1.getLength(), e2.getLength(), e3.getLength(), radius);
}

double Geometry::hypAng(double lengthA, double lengthB, double lengthC)
{
       return acos((cosh(lengthA)*cosh(lengthB)-cosh(lengthC))
                            / (sinh(lengthA)*sinh(lengthB)));                                          
}

double Geometry::hypAng(Vertex v, Face f)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
     Edge e1 = Triangulation::edgeTable[sameAs[0]];
     Edge e2 = Triangulation::edgeTable[sameAs[1]];
     diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
     Edge e3 = Triangulation::edgeTable[diff[0]];
     
     return hypAng(e1.getLength(), e2.getLength(), e3.getLength());
}

double Geometry::dihedralAng(Edge *e, Tetra *t)
{
    Face *f1, *f2, *f3;
    Vertex *v = &(Triangulation::vertexTable[(*(e->getLocalVertices()))[0]]);
    vector<int> faces = listintersection(t->getLocalFaces(), v->getLocalFaces());
    vector<int> edge_faces = listIntersection(&faces, e->getLocalFaces());
    vector<int> not_edge_faces = listDifference(&faces, e->getLocalFaces());

    f1 = &(Triangulation::faceTable[edge_faces[0]]);
    f2 = &(Triangulation::faceTable[edge_faces[1]]);
    f3 = &(Triangulation::faceTable[not_edge_faces[0]]);                                                                       

    
    double angle1 = angle(v, f1);
    double angle2 = angle(v, f2);
    double angle3 = angle(v, f3);
    return spherAng(angle1, angle2, angle3);
}

double Geometry::curv(Vertex *v) {
     double sum = 0;
     vector<int> vp = *(v->getLocalFaces());
     for(int i = 0; i < vp.size(); i++)
     {
            sum += angle(v, &(Triangulation::faceTable[i]));
     }
     return 2*PI - sum;      
}

double Geometry::curv3D(Vertex *v) {
   map<int, Vertex>::iterator vit;
   map<int, Edge>::iterator eit;
   map<int, Tetra>::iterator tit;
   double curv = 0;
   
   for(int i = 0; i < v->getLocalEdges()->size(); i++)
   {
        Edge *e = &(Triangulation::edgeTable[(*(v->second.getLocalEdges()))[i]]);
        double betaSum = 2*PI;
        for(int j = 0; j < e->getLocalTetras()->size(); j++)
        {
           betaSum -= dihedralAngle(e, &(Triangulation::tetraTable[(*(e->getLocalTetras()))[j]] ) );
        }
        curv += betaSum * getPartialEdge(*e, *v);
   }
   return curv;
}

/******************************************************************************/

// PUBLIC METHODS

void Geometry::build() {
  
  gqi.clear();
  
  switch(mode) {
    case FLOW: 
         // Build Radii
         build(RADIUS);
         // Build Etas 
         build(ETA);
         // Build Lengths
         build(LENGTH);
         // Build Angles
         build(ANGLE);
         // Build Areas
         build(AREA);
         if(dim == 3D) {
            // Build Dihedral Angles
            build(DIHEDRALANGLE);
            // Build Volumes
            build(VOLUME);
         }
         // Build Curvatures
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

void Geometry::build(int quantType) {
     
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Face>::iterator fit;
     map<int, Tetra>::iterator tit;
     
     vector<int> faces;
     vector<int> tetras;
          
     switch(quantType) {
         case RADIUS:
              for(vit = Triangulation::vertexTable.begin(); 
                      vit != Triangulation::vertexTable.end(); vit++)
              {
                      Radius r(vit->second, gqi);
                      addGQ(r.getPosition(), r);
              }
              break;
         case ETA:
              for(eit = Triangulation::edgeTable.begin();
                      eit != Triangulation::edgeTable.end(); eit++) 
              {
                      Eta e(eit->second, gqi);
                      addGQ(e.getPosition(), e);
              }
              break;
         case LENGTH:
              for(eit = Triangulation::edgeTable.begin();
                      eit != Triangulation::edgeTable.end(); eit++) 
              {
                      Length l(eit->second, gqi);
                      addGQ(l.getPosition(), l);
              }
              break;
         case ANGLE:
              for(vit = Triangulation::vertexTable.begin(); 
                      vit != Triangulation::vertexTable.end(); vit++)
              {
                      faces = *(vit->second.getLocalFaces());
                      Face& f;
                      switch(geo) {
                        case Euclidean:
                          for(int i = 0; i < face.size(); i++)
                          {
                             f = Triangulation::faceTable[faces[i]];
                             EucAngle a(vit->second, f, gqi);
                             addGQ(a.getPosition(), a);            
                          }
                          break;
                        case Hyperbolic:
                          for(int i = 0; i < face.size(); i++)
                          {
                             f = Triangulation::faceTable[faces[i]];
                             HypAngle a(vit->second, f, gqi);
                             addGQ(a.getPosition(), a);            
                          }
                          break;
                        case Spherical:
                          for(int i = 0; i < face.size(); i++)
                          {
                             f = Triangulation::faceTable[faces[i]];
                             SpherAngle a(vit->second, f, gqi);
                             addGQ(a.getPosition(), a);              
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
                     Area a(fit->second, gqi);
                     addGQ(a.getPosition(), a);
              }
              break;
         case  DIHEDRALANGLES:
               for(eit = Triangulation::edgeTable.begin();
                    eit != Triangulation::edgeTable.end(); eit++)
               {
                    tetras = *(eit->second.getLocaltetras());
                    Tetra& t;
                    for(int i = 0; i < face.size(); i++)
                    {
                       t = Triangulation::tetraTable[tetras[i]];
                       DihedralAngle da(eit->second, t, gqi);
                       addGQ(da.getPosition(), da);          
                    }             
               }
               break;
         case VOLUME:
              for(tit = Triangulation::tetraTable.begin();
                   tit != Triangulation::tetraTable.end(); tit++)
              {
                   Volume v(tit->second, gqi);
                   addGQ(v.getPosition(), v); 
              }
              break;
         case CURVATURE:
              for(vit = Triangulation::vertexTable.begin(); 
                    vit != Triangulation::vertexTable.end(); vit++)
              {
                    switch(dim) {
                      case 2D:
                           2DCurvature curv(vit->second, gqi);
                           addGQ(curv.getPosition(), curv);
                           break;
                      case 3D:
                           3DCurvature curv(vit->second, gqi);
                           addGQ(curv.getPosition(), curv);
                           break;
                      default:
                           printf("Error: Unidentifiable dimension \n");
                           return;
                    }
             }
             break;
         default:
             printf("Error: Unidentifiable type\n");
             return;                                     
     }    
}

void Geometry::addGQ(TriPosition tp, GeoQuant gq) {
     gqi.insert(Pair<TriPosition, GeoQuant>(tp, gq));
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
void Geomtry::setRadius(double rad) {
     spherRadius = rad;    
}
       
void Geometry::setRadius(const Vertex& v, double rad) {
     TriPosition tp(1, RADIUS, v.getSerialNumber());
     GeoQuant& radius = gqi[tp];
     
     radius.setValue(rad);
}
void Geometry::setEta(const Edge& e, double eta) {
     TriPosition tp(1, ETA, e.getSerialNumber());
     GeoQuant& etaGQ = gqi[tp];
     
     etaGQ.setValue(eta);     
}
void Geometry::setLength(const Edge& e, double len) {
     TriPosition tp(1, LENGTH, e.getSerialNumber());
     GeoQuant& length =  gqi[tp];
     
     length.setValue(len);   
}
       
double Geometry::radius(const Vertex& v) {
       TriPosition tp(1, RADIUS, v.getSerialNumber());
       GeoQuant& radius = gqi[tp];
       
       return radius.getValue();
}
double Geometry::eta(const Edge& e) {
       TriPosition tp(1, ETA, e.getSerialNumber());
       GeoQuant& etaGQ = gqi[tp];
     
       return etaGQ.getValue();           
}
double Geometry::length(const Edge& e) {
       TriPosition tp(1, LENGTH, e.getSerialNumber());
       GeoQuant& length =  gqi[tp];
     
       return length.getValue();       
}
double Geometry::angle(const Vertex& v, const Face& f) {
       TriPosition tp(2, ANGLE, v.getSerialNumber(), f.getSerialNumber());
       GeoQuant& angle = gqi[tp];
       
       return angle.getValue();      
}
double Geometry::dihedralAngle(const Edge& e, const Tetra& t) {
       TriPosition tp(2, DIHEDRALANGLE, e.getSerialNumber(), t.getSerialNumber());
       GeoQuant& dihAng = gqi[tp];
       
       return dihAng.getValue();   
}
double Geometry::curvature(const Vertex& v) {
       TriPosition tp(1, CURVATURE, v.getSerialNumber());
       GeoQuant& curv = gqi[tp];
       
       return curv.getValue();     
}
double Geometry::area(const Face& f) {
       TriPosition tp(1, AREA, f.getSerialNumber());
       GeoQuant& area = gqi[tp];
       
       return area.getValue();
}
double Geometry::volume(const Tetra& t) {
       TriPosition tp(1, VOLUME, t.getSerialNumber());
       GeoQuant& volume = gqi[tp];
       
       return volume.getValue();
}

void Geometry::setRadii(double* radii)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
       setRadius(vit->second, radii[i]);
       i++;
     }
}
void Geometry::getRadii(double* radii)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
       radii[i] = radius(vit->second);
       i++;
     }
}
void Triangulation::setLengths(double* lengths)
{
     map<int, Edge>::iterator eit;
     int i = 0;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
       setLength(eit->second, lengths[i]);
       i++;
     }
}
void Geometry::netCurvature() {
     map<int, Vertex>::iterator vit;
     double sum = 0;
     for(vit = Triangulation::vertexTable.begin(); 
             vit != Triangulation::vertexTable.end(); vit++)
     {
         sum += curvature(vit->second);        
     }
}
/******************************************************************************/
