#include "sphericalmath.h"
#include <cmath>
#define PI 3.141592653589793238
#include "Geometry/Geometry.h"

double sphericalAngle(double lengthA, double lengthB, double lengthC, double radius)
{
   return acos((cos(lengthC/radius)-cos(lengthA/radius)*cos(lengthB/radius))
                              / (sin(lengthA/radius)*sin(lengthB/radius)));
}

double sphericalAngle(Vertex v, Face f, double radius)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
     Edge e1 = Triangulation::edgeTable[sameAs[0]];
     Edge e2 = Triangulation::edgeTable[sameAs[1]];
     diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
     Edge e3 = Triangulation::edgeTable[diff[0]];
     
     return sphericalAngle(Geometry::length(e1), Geometry::length(e2), Geometry::length(e3));
}

double sphericalCurvature(Vertex v, double radius)
{
      double sum = 0;
      vector<int>::iterator it;
      vector<int>* vp = v.getLocalFaces();
      for(it = (*vp).begin(); it < (*vp).end(); it++)
      {
             sum += sphericalAngle(v, Triangulation::faceTable[*it], radius);
      }
      return 2*PI - sum;
}


void sphericalCurvature(double radius)
{
//      Triangulation::setSphericalAngles(radius);
//  Why is this here???
      map<int, Vertex>::iterator vit;
      map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
      map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
      double curv;
      for(vit = vBegin; vit != vEnd; vit++) {
         curv = 2 * PI;
         vector<int> localF = (*(vit->second.getLocalFaces()));
         for(int i = 0; i < localF.size(); i++)
         {
//            curv -= Triangulation::faceTable[localF[i]].getAngle(vit->first);
// I replaced the above with the following line.  I think it is flawed (not calculating spherical angles?.
            curv -= Geometry::angle(vit->second, Triangulation::faceTable[localF[i]]);
         }
         vit->second.setCurvature(curv);
      }
}


void sphericalCalcFlow(vector<double>* radii, vector<double>* curvatures,double dt ,double* initRadii,int numSteps, bool adjF)
{

  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
                                         
  double ta[p],tb[p],tc[p],td[p],z[p]; // Temporary arrays to hold data in.
  int    i,k; // ints used for "for loops".
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  FILE* radiusFile = fopen("C:/Dev-Cpp/geocam/Triangulation Files/radiusFile.txt", "w");
  
  int chi = p - Triangulation::edgeTable.size() + Triangulation::faceTable.size();
  double net = 0; // Net and prev hold the current and previous
  double prev;    //  net curvatures, repsectively.
  double radius = 1;
  double dR;
   for (k=0; k<p; k++) {
    z[k]=initRadii[k]; // z[k] holds the current radii.
   }
   for (i=1; i<numSteps+1; i++) 
   {
       prev = net; // Set prev to net.
       net = 0;    // Reset net.
       dR = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]);
       }
       if(i == 1) // If first time through, use static method.
       {
            prev = Triangulation::netSphericalCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // First "for loop" in whole step calculates
       {                    // everything manually, prints to file.
           (*radii).push_back( z[k]);
           double curv = sphericalCurvature(vit->second, radius);     
           
           if(curv < 0.00005 && curv > -0.00005) // Adjusted for small errors.
           {
             (*curvatures).push_back(0.);
           }
           else {
               (*curvatures).push_back(curv);
             }
           net += curv;
           dR += curv / radius * vertexSum(vit->second, radius);
           if(adjF) ta[k]= dt * ((prev/p) * vit->second.getRadius() / radius - curv 
                           * sin(vit->second.getRadius() / radius));
           else     ta[k] = dt * (-1) * curv * sin(vit->second.getRadius() / radius);
           
       }
       fprintf(radiusFile, "Step %d: %.6f\n", i, radius);
       //dR -= prev;
       dR /= angleTotalSum(radius);
       radius += dt * dR;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+ta[k]/2);
       }


       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second, radius);         
           net += curv;
           if(adjF) tb[k]= dt * ((prev/p) * vit->second.getRadius() / radius - curv 
                           * sin(vit->second.getRadius() / radius));
           else     tb[k] = dt * (-1) * curv * sin(vit->second.getRadius() / radius);
      }
      for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
      {
          vit->second.setRadius(z[k]+tb[k]/2);
      }

      prev = net;
      net = 0;
      for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
      {
          double curv = sphericalCurvature(vit->second, radius);
          net += curv;
          if(adjF) tc[k]= dt * ((prev/p) * vit->second.getRadius() / radius - curv
                         * sin(vit->second.getRadius() / radius));
          else     tc[k] = dt * (-1) * curv
                          * sin(vit->second.getRadius() / radius);

       }
       
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+tb[k]/2);
       }

       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second, radius);
           net += curv;
           if(adjF) tc[k]= dt * ((prev/p) * vit->second.getRadius() / radius - curv 
                         * sin(vit->second.getRadius() / radius));
           else     tc[k] = dt * (-1) * curv * sin(vit->second.getRadius() / radius);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new radii.
       {
           vit->second.setRadius(z[k]+tc[k]);
       } 

       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second, radius);
           net += curv;
           if(adjF) td[k]= dt * ((prev/p) * vit->second.getRadius() / radius - curv 
                           * sin(vit->second.getRadius() / radius));
           else     td[k] = dt * (-1) * curv * sin(vit->second.getRadius() / radius);


       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
   }
   fclose(radiusFile);
}


double delArea()
{    
      double bigSum = 0;
      map<int, Face>::iterator fit;
      for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
      {
              double littleSum = 0;
              for(int i = 0; i < fit->second.getLocalEdges()->size(); i++)
              {
                      Edge e = Triangulation::edgeTable[(*(fit->second.getLocalEdges()))[i]];
                      for(int j = 0; j < e.getLocalVertices()->size(); j++)
                        littleSum += sphericalCurvature(Triangulation::vertexTable[(
                                     *(e.getLocalVertices()))[j]  ]  )
                                     * tan(e.getLength()/2);
              }
              Vertex v = Triangulation::vertexTable[(*(fit->second.getLocalVertices()))[0]];
              bigSum += sin(v.getRadius())*tan(sphericalAngle(v, fit->second))*littleSum;
      }
      return bigSum;
}

double sphericalArea(Face f)
{
      double sum = 0;
      for(int i = 0; i < f.getLocalVertices()->size(); i++)
      {
          sum += sphericalAngle(Triangulation::vertexTable[(*(f.getLocalVertices()))[i]], f);    
      }
      sum -= PI;
      return sum;
}

double delta(Face f)
{
       Vertex v = Triangulation::vertexTable[(*(f.getLocalVertices()))[0]];
       vector<int> edges = listIntersection(f.getLocalEdges(), v.getLocalEdges());
       if(edges.size() != 2)
       {
         return (-1) * 300.0;
       }
       Edge e1 = Triangulation::edgeTable[edges[0]];
       Edge e2 = Triangulation::edgeTable[edges[1]];
       return sin(e1.getLength())*sin(e2.getLength())*sin(sphericalAngle(v, f));
}

double sphericalTotalArea()
{
      map<int, Face>::iterator fit;
      double total = 0;
      for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
      {
              total += sphericalArea(fit->second);
      }
      return total;
}

double delCurv(Vertex v)
{
       map<int, Face>::iterator fit;
       double sum = 0;
       for(int i = 0; i < v.getLocalFaces()->size(); i++)
       {
          Face f = Triangulation::faceTable[(*(v.getLocalFaces()))[i]];
          double vCurv = sphericalCurvature(v);
          double inArea = sin(v.getRadius())*tan(sphericalAngle(v, f));
          vector<int> edges = listIntersection(v.getLocalEdges(), f.getLocalEdges());
          Edge e1 = Triangulation::edgeTable[edges[0]];
          Edge e2 = Triangulation::edgeTable[edges[1]];
          Vertex v2;
          if((*(e1.getLocalVertices()))[0] == v.getIndex())
              v2 = Triangulation::vertexTable[(*(e1.getLocalVertices()))[1]];
          else
              v2 = Triangulation::vertexTable[(*(e1.getLocalVertices()))[0]];
          Vertex v3;
          if((*(e2.getLocalVertices()))[0] == v.getIndex())
              v3 = Triangulation::vertexTable[(*(e2.getLocalVertices()))[1]];
          else
              v3 = Triangulation::vertexTable[(*(e2.getLocalVertices()))[0]];
          sum += inArea/tan(e1.getLength())*(sphericalCurvature(v2) - vCurv);
          sum += inArea/tan(e2.getLength())*(sphericalCurvature(v3) - vCurv);
       }
      return sum;
}

double vertexSum(Vertex v, double radius) 
{
   double result = 0;
   vector<int> localF = *(v.getLocalFaces());
   for(int i = 0; i < localF.size(); i++) {
       Face f = Triangulation::faceTable[localF[i]];
       vector<int> edges = listIntersection(f.getLocalEdges(), v.getLocalEdges());
       double l1 = Triangulation::edgeTable[edges[0]].getLength();
       double l2 = Triangulation::edgeTable[edges[1]].getLength();
       result += tan(inRadius(f, radius) / radius) * 
                (tan(l1 / (2*radius)) + tan(l2 / (2*radius)));
   }
   return result;
}

double angleTotalSum(double radius)
{
   map<int, Face>::iterator fit;
   map<int, Face>::iterator fBegin = Triangulation::faceTable.begin();
   map<int, Face>::iterator fEnd = Triangulation::faceTable.end();
   
   double result = 0;
   
   for(fit = fBegin; fit != fEnd; fit++)
   {
       result += angleDiffSums(fit->second, radius);
   }
   return result;
}

double angleDiffSums(Face f, double radius)
{
       vector<int> localV = *(f.getLocalVertices());
       vector<int> localE = *(f.getLocalEdges());
       
       Vertex v_i = Triangulation::vertexTable[localV[0]];
       Vertex v_j = Triangulation::vertexTable[localV[1]];
       Vertex v_k = Triangulation::vertexTable[localV[2]];
       
       Edge e_ij = Triangulation::edgeTable[listIntersection(v_i.getLocalEdges(), v_j.getLocalEdges())[0]];
       Edge e_ik = Triangulation::edgeTable[listIntersection(v_i.getLocalEdges(), v_k.getLocalEdges())[0]];
       Edge e_jk = Triangulation::edgeTable[listIntersection(v_j.getLocalEdges(), v_k.getLocalEdges())[0]];
       
       double l_ij = e_ij.getLength() / radius;
       double l_ik = e_ik.getLength() / radius;
       double l_jk = e_jk.getLength() / radius;
       
       return angleDiff(l_ij, l_ik, l_jk, radius) + 
              angleDiff(l_jk, l_ij, l_ik, radius) +
              angleDiff(l_ik, l_jk, l_ij, radius);
}

double angleDiff(double l_ij, double l_ik, double l_jk, double radius) {
    double result = 0;
    result+= l_ij * sin(l_ij) * cos(l_ik) + l_ik * sin(l_ik) * cos(l_ij) - l_jk * sin(l_jk);
    result *= sin(l_ij) * sin(l_ik) / radius;
    result -= (cos(l_jk) - cos(l_ij)*cos(l_ik)) * 
              (l_ij * cos(l_ij) * sin(l_ik) + l_ik * cos(l_ik) * sin(l_ij)) / radius;
    result /= pow(sin(l_ij), 2) * pow(sin(l_ik), 2) * 
              -sin(sphericalAngle(l_ij, l_ik, l_jk));
    return result;
}
