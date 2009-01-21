#include "3Dtriangulationmath.h"
#include "3DInputOutput.h"
#include "triangulation/triangulation.h"
#include "spherical/sphericalmath.h"
#include "miscmath.h"
#include "delaunay.h"
#include <math.h>
#include <cerrno>
#define PI 	3.141592653589793238
double solidAngle(double angle1, double angle2, double angle3)
{
       // Beta i = dihedral angle
       double beta1 = sphericalAngle(angle1, angle2, angle3);
       double beta2 = sphericalAngle(angle1, angle3, angle2);
       double beta3 = sphericalAngle(angle2, angle3, angle1);
       return beta1 + beta2 + beta3 - PI;      
}       
double solidAngle(Vertex v, Tetra t)
{
       // Find the three faces local to v and part of the t.
       vector<int> sameAs = listIntersection(v.getLocalFaces(), t.getLocalFaces());
 
       Face f1 = Triangulation::faceTable[sameAs[0]];
       Face f2 = Triangulation::faceTable[sameAs[1]];
       Face f3 = Triangulation::faceTable[sameAs[2]];
       
       // Find the angle of each face on vertex v.
       double angle1 = angle(v, f1);
       double angle2 = angle(v, f2);
       double angle3 = angle(v, f3);
       return solidAngle(angle1, angle2, angle3);
}

double dihedralAngle(Vertex v, Edge e, Tetra t)
{
    Face f1, f2, f3;
    vector<int> faces = *(t.getLocalFaces());
    vector<int> edge_faces;
    vector<int> not_edge_faces;
    for(int i = 0; i < 4; i++)
    {
       if( Triangulation::faceTable[faces[i]].isAdjEdge(e.getIndex()) )
       {
          edge_faces.push_back(faces[i]);
       } else
       {
          not_edge_faces.push_back(faces[i]);
       }
    }
    f1 = Triangulation::faceTable[edge_faces[0]];
    f2 = Triangulation::faceTable[edge_faces[1]];
    if(Triangulation::faceTable[not_edge_faces[0]].isAdjVertex(v.getIndex()) ) 
    {
      f3 = Triangulation::faceTable[not_edge_faces[0]];                                                                       
    } else {
      f3 = Triangulation::faceTable[not_edge_faces[1]];
    }
    
    double angle1 = angle(v, f1);
    double angle2 = angle(v, f2);
    double angle3 = angle(v, f3);
    
    return sphericalAngle(angle1, angle2, angle3);
}

double volumeSq(Tetra t) 
{
   vector<int> localV = *(t.getLocalVertices());
   double radii[4] = {0};
   for(int i = 0; i < localV.size(); i++)
   {
       radii[i] = Triangulation::vertexTable[localV[i]].getRadius();
   }
   return volumeSq(radii[0], radii[1], radii[2], radii[3]);
}
double volumeSq(double r1, double r2, double r3, double r4)
{
   double sum1 = 1/r1 + 1/r2 + 1/r3 + 1/r4;
   double sum2 = 1/(r1 * r1) + 1/(r2 * r2) + 1/(r3 * r3) + 1/(r4 * r4);
   double product = 1/9.0 * (r1 * r1) * (r2 * r2) * (r3 * r3) * (r4 * r4);
   return product * (sum1 * sum1 - 2 * sum2);
}
bool isDegenerate(Tetra t)
{
     return volumeSq(t) > 0;
}

//double curvature3D(Vertex v)
//{
//       // For each tetra containing v, get the solid angle at v.
//       double sum = 0.0;
//       for(int i = 0; i < v.getLocalTetras()->size(); i++)
//       {
//           Tetra t = Triangulation::tetraTable[(*(v.getLocalTetras()))[i]];
//           sum += solidAngle(v, t);
//       }
//       // Subtract sum from 4*PI
//       return 4*PI - sum;
//}

void curvature3D()
{
   map<int, Vertex>::iterator vit;
   map<int, Edge>::iterator eit;
   map<int, Tetra>::iterator tit;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
   {
      eit->second.setDihedralAngles();
   }


   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) 
   {
      double curv = 0;
      for(int i = 0; i < vit->second.getLocalEdges()->size(); i++)
      {
        Edge e = Triangulation::edgeTable[(*(vit->second.getLocalEdges()))[i]];
        double betaSum = 2*PI;
        for(int j = 0; j < e.getLocalTetras()->size(); j++)
        {
           betaSum -= e.getDihedralAngle(j);
        }
        curv += betaSum * getPartialEdge(e, vit->second);
      }
      vit->second.setCurvature(curv);
   }
}

void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt ,
               double *initRadii,int numSteps, bool adjF)  
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
  vector<double> volumes;                                       
  double z[p]; // Temporary array to hold data for 
                                      // the intermediate steps in.
  double curCurv;
  double normalization;
  int    i,k; // ints used for "for loops". i is the step number,
              // k is the kth vertex for the arrays.
  map<int, Vertex>::iterator vit; // Iterator to traverse through the vertices.
  // Beginning and Ending pointers. (Used for "for" loops)
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  map<int, Tetra>::iterator tit;
  
   for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
   {
      // Set the radii of the Triangulation.
      z[k]=initRadii[k];
      vit->second.setRadius(z[k]);
   }
   for (i=1; i<numSteps+1; i++) // This is the main loop through each step.
   {
       for (tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
       {
           volumes.push_back(volumeSq(tit->second));
       }
       curvature3D();
       normalization = calcNormalization();
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++) 
       {
           (*radii).push_back( z[k]); // Adds the data to the vector.
           curCurv = vit->second.getCurvature();
           if(curCurv < 0.00005 && curCurv > -0.00005) // Adjusted for small numbers.
           {                                     // We want it to print nicely.
             (*curvatures).push_back(0.); // Adds the data to the vector.
           }
           else {
               (*curvatures).push_back(curCurv);
           }
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * curCurv +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * curCurv;
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
   }
   printResultsVolumes("C:/Dev-Cpp/Geocam/Triangulation Files/Volume Results.txt", &volumes);
}

void yamabeFlow(double dt, double *initRadii,int numSteps, bool adjF)
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.                                  
  double z[p]; // Temporary array to hold data for 
                                      // the intermediate steps in.
  double normalization;
  int    i,k; // ints used for "for loops". i is the step number,
              // k is the kth vertex for the arrays.
  map<int, Vertex>::iterator vit; // Iterator to traverse through the vertices.
  // Beginning and Ending pointers. (Used for "for" loops)
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
   for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
   {
     z[k]=initRadii[k]; // z[k] holds the current radii.
     // Set the radii of the Triangulation.
     vit->second.setRadius(z[k]);
   }
   for (i=1; i<numSteps+1; i++) // This is the main loop through each step.
   {
       curvature3D();
       normalization = calcNormalization();
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++) 
       {  // First "for loop" in whole step calculates everything manually.
          
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * vit->second.getCurvature() +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * vit->second.getCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
   }
}

double calcNormalization()
{
   double result = 0;
   double tempNum;
   double denom = 0;
   map<int, Edge>::iterator eit;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
   {
      tempNum = 0;
      Vertex v1 = Triangulation::vertexTable[(*(eit->second.getLocalVertices()))[0]];
      Vertex v2 = Triangulation::vertexTable[(*(eit->second.getLocalVertices()))[1]];
      
      tempNum += v1.getRadius()*v1.getCurvature() + v2.getRadius()*v2.getCurvature();
      tempNum += eit->second.getEta() * 
              (v1.getRadius()*v2.getCurvature() + v2.getRadius()*v1.getCurvature());
      result += tempNum / eit->second.getLength();
      denom += eit->second.getLength();
   }
   return result / denom;
}


void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt, double *initRadii, 
                       double accuracy, double precision, bool adjF)
{
    
    bool done = false, accurate, precise, firstStep = true;
    int numV = Triangulation::vertexTable.size();
    int k;
    double avgProp;
    double normalization;
    double curCurv;
    double z[numV];
    map<int, double> prevCurvs;
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
    for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++)  
    {
       prevCurvs.insert(pair<int, double>(vit->first, 0.0));
       z[k] = initRadii[k]; // z[k] holds the current radii.
       // Set the radii of the Triangulation.
       vit->second.setRadius(z[k]);
    }
    while(!done && errno == 0)
    {
       curvature3D();
       normalization = calcNormalization();
       for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++) 
       {   
           (*radii).push_back( z[k]); // Adds the data to the vector.
           curCurv = vit->second.getCurvature();
           if(curCurv < 0.00005 && curCurv > -0.00005) // Adjusted for small numbers.
           {                                     // We want it to print nicely.
             (*curvatures).push_back(0.); // Adds the data to the vector.
           }
           else {
               (*curvatures).push_back(curCurv);
           }         
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * curCurv +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * curCurv;
       }
       if(!firstStep) 
       {
         precise = true;
         for (vit = vBegin; vit != vEnd; vit++) 
         {
           precise = precise && ( fabs(prevCurvs[vit->first] - vit->second.getCurvature()) < precision );
         }
         if(precise)
         {
          accurate = true;
          avgProp = 0;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            avgProp += vit->second.getCurvature() / vit->second.getRadius();
          }
          avgProp = avgProp / numV;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            accurate = accurate && ( fabs(avgProp - vit->second.getCurvature() / vit->second.getRadius()) < accuracy );
          }
          done = accurate;     
         }
       } else
       {
         firstStep = false;
       }
       for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
       for (vit = vBegin; vit != vEnd; vit++) 
       {
           prevCurvs[vit->first] = vit->second.getCurvature();
           if(prevCurvs[vit->first] < -10000000)
           {
              exit(1);
           }
       }
    }
}

void yamabeFlow(double dt, double *initRadii, 
                       double accuracy, double precision, bool adjF)
{
    
    bool done = false, accurate, precise, firstStep = true;
    int numV = Triangulation::vertexTable.size();
    int k;
    double avgProp;
    double normalization;
    double z[numV];
    map<int, double> prevCurvs;
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
    for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++)  
    {
       prevCurvs.insert(pair<int, double>(vit->first, 0.0));
       z[k] = initRadii[k]; // z[k] holds the current radii.
       // Set the radii of the Triangulation.
       vit->second.setRadius(z[k]);
    }
    while(!done && errno == 0)
    {
       curvature3D();
       normalization = calcNormalization();
       if(!firstStep) 
       {
         precise = true;
         for (vit = vBegin; vit != vEnd; vit++) 
         {
           precise = precise && ( fabs(prevCurvs[vit->first] - vit->second.getCurvature()) < precision );
         }
         if(precise)
         {
          accurate = true;
          avgProp = 0;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            avgProp += vit->second.getCurvature() / vit->second.getRadius();
          }
          avgProp = avgProp / numV;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            accurate = accurate && ( fabs(avgProp - vit->second.getCurvature() / vit->second.getRadius()) < accuracy );
          }
          done = accurate;     
         }
       } else
       {
         firstStep = false;
       }
       for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++) 
       {      
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * vit->second.getCurvature() +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * vit->second.getCurvature();
           
           vit->second.setRadius(z[k]);
       }
       for (vit = vBegin; vit != vEnd; vit++) 
       {
           prevCurvs[vit->first] = vit->second.getCurvature();
       }
    }
}
