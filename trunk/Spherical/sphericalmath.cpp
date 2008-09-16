#include "sphericalmath.h"
#include <cmath>
#define PI 3.141592653589793238

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
     
     return sphericalAngle(e1.getLength(), e2.getLength(), e3.getLength());
}
double sphericalCurvature(Vertex v)
{
      double sum = 0;
      vector<int>::iterator it;
      vector<int>* vp = v.getLocalFaces();
      for(it = (*vp).begin(); it < (*vp).end(); it++)
      {
             sum += sphericalAngle(v, Triangulation::faceTable[*it]);
      }
      return 2*PI - sum;
}

void sphericalCalcFlow(vector<double>* weights, vector<double>* curvatures,double dt ,double* initWeights,int numSteps, bool adjF)
{

  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
                                         
  double ta[p],tb[p],tc[p],td[p],z[p]; // Temporary arrays to hold data in.
  int    i,k; // ints used for "for loops".
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  int chi = p - Triangulation::edgeTable.size() + Triangulation::faceTable.size();
  double net = 0; // Net and prev hold the current and previous
  double prev;    //  net curvatures, repsectively.
   for (k=0; k<p; k++) {
    z[k]=initWeights[k]; // z[k] holds the current weights.
   }
   for (i=1; i<numSteps+1; i++) 
   {
    prev = net; // Set prev to net.
    net = 0;    // Reset net.
    for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]);
       }
       if(i == 1) // If first time through, use static method.
       {
            prev = Triangulation::netSphericalCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // First "for loop" in whole step calculates
       {                    // everything manually, prints to file.
           (*weights).push_back( z[k]);
           double curv = sphericalCurvature(vit->second);     
           
           if(curv < 0.00005 && curv > -0.00005) // Adjusted for small errors.
           {
             (*curvatures).push_back(0.);
           }
           else {
               (*curvatures).push_back(curv);
             }
           net += curv;
           if(adjF) ta[k]= dt * ((prev/p) * vit->second.getWeight() - curv 
                           * sin(vit->second.getWeight()));
           else     ta[k] = dt * (-1) * curv * sin(vit->second.getWeight());
           
       }

       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+ta[k]/2);
       }


       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second);         
           net += curv;
           if(adjF) tb[k]= dt * ((prev/p) * vit->second.getWeight() - curv 
                           * sin(vit->second.getWeight()));
           else     tb[k] = dt * (-1) * curv * sin(vit->second.getWeight());
      }
      for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
      {
          vit->second.setWeight(z[k]+tb[k]/2);
      }

      prev = net;
      net = 0;
      for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
      {
          double curv = sphericalCurvature(vit->second);
          net += curv;
          if(adjF) tc[k]= dt * ((prev/p) * vit->second.getWeight() - curv
                         * sin(vit->second.getWeight()));
          else     tc[k] = dt * (-1) * curv
                          * sin(vit->second.getWeight());

       }
       
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tb[k]/2);
       }

       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second);
           net += curv;
           if(adjF) tc[k]= dt * ((prev/p) * vit->second.getWeight() - curv 
                         * sin(vit->second.getWeight()));
           else     tc[k] = dt * (-1) * curv * sin(vit->second.getWeight());
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tc[k]);
       } 

       prev = net;
       net = 0;
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           double curv = sphericalCurvature(vit->second);
           net += curv;
           if(adjF) td[k]= dt * ((prev/p) * vit->second.getWeight() - curv 
                           * sin(vit->second.getWeight()));
           else     td[k] = dt * (-1) * curv * sin(vit->second.getWeight());


       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
   }   
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
              bigSum += sin(v.getWeight())*tan(sphericalAngle(v, fit->second))*littleSum;
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
          double inArea = sin(v.getWeight())*tan(sphericalAngle(v, f));
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
