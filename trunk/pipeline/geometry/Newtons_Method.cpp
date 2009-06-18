#include "delaunay.h"
#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include "triangulation/smallMorphs.h"
#include "3DTriangulation/3DTriangulationMorph.h"
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#define PI 	3.141592653589793238
#include "Geometry/Geometry.h"

int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution);
//int LinearEquationsSolving(int nDim, double pfMatr[3][3], double pfVect[3], double pfSolution[3]);
double EHR_Second_Partial (int i, int j);
double EHR_Partial (int i);


void Newtons_Method()
{
//     Geometry::setDimension(ThreeD);
//     Geometry::setGeometry(Euclidean);
//     Geometry::build();
          
     int N = Triangulation::vertexTable.size();

     double Rvector[Triangulation::vertexTable.size()];
     double ARE[Triangulation::vertexTable.size()];
     double X_n[Triangulation::vertexTable.size()];
     double X_np1[Triangulation::vertexTable.size()];
     double P_n[Triangulation::vertexTable.size()];
     
     double EHRhessian[Triangulation::vertexTable.size()][Triangulation::vertexTable.size()];
     double EHRneg_gradient[Triangulation::vertexTable.size()];
     double BEE[Triangulation::vertexTable.size()];
     double Solution[Triangulation::vertexTable.size()];  

     double* matrix;

     map<int, Vertex>::iterator vit;
     
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        X_n[vit->first] = log(Geometry::radius(vit->second));
        X_np1[vit->first] = 0.0;
        }

while(true) {
           
            
for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
   {
       double curv = Geometry::curvature(vit->second);
       printf("vertex %3d: radius = %f\t  curvature = %.10f\n", vit->first, Geometry::radius(vit->second), curv/(Geometry::radius(vit->second)));
           }            

//     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
//        Rvector[vit->first] = vit->second.getRadius(); 
//        X_n[vit->first] = log(vit->second.getRadius());
//        X_np1[vit->first] = 0.0;
//        }

// Should we use the same technique for the procedures directly 
// above and below this comment (iterating through the vertices?  Is the top better?
     for(int i = 0; i < Triangulation::vertexTable.size(); ++i) {
               for(int j = 0; j < Triangulation::vertexTable.size(); ++j) {
                       if (i <= j) {
                       EHRhessian[i][j]=EHR_Second_Partial( i+1 , j+1 );
                       EHRhessian[j][i]=EHRhessian[i][j];
//                       printf("EHR_Second_Partial = %.10f, i=%d, j=%d\n", EHR_Second_Partial( i+1 , j+1 ), i+1, j+1);
//                           system("PAUSE");
//                    printf("i=%d, j=%d, Hess[%d,%d]=%12.10f\n", i+1, j+1, i, j, Hess[i][j]);
                       }
               }
      }
printf("end hessian\n"); 
//system("PAUSE");
     for(int i=0; i < Triangulation::vertexTable.size(); ++i) {
               EHRneg_gradient[i] = -1.0*EHR_Partial(i+1);
               BEE[i] = EHRneg_gradient[i];
               }
printf("end gradient\n"); 
//system("PAUSE");
     matrix = (double*) EHRhessian;
//       matrix = **(EHRhessian);

     LinearEquationsSolving( N , matrix, BEE, Solution);
     
printf("Linear system solved\n"); 
//system("PAUSE");     

     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        X_np1[(vit->first)-1] = Solution[(vit->first)-1] + X_n[(vit->first)-1];
        Rvector[(vit->first)-1] = exp(X_np1[(vit->first)-1]);
        Geometry::setRadius(vit->second, Rvector[(vit->first)-1]);
        X_n[(vit->first)-1] = X_np1[(vit->first)-1];
        }

// The following prints data from the cuurrent interation.  
      
printf("new radii updated\n");
//           system("PAUSE");

     
} // End of while (true)    
      
////////////////////////
//What follows between the "///////////////'s" is a toy use of 
//LinearEquationSolving that works.  Solves: AYE*AXE=BEE for AXE. 
//double AYE[3][3] = {{ 1, 2, 3 }, { 4, 5, 6 }, { 0, 8, 9 } };
//  double BEE[3];
//  double SOL[3];
//  double AXE[3];
//  int N;
//
//  BEE[0] = 1;
//  BEE[1] = 2;
//  BEE[2] = 3;
//  N=3;
//
//  double* matrix = (double*) AYE;
//
//  LinearEquationsSolving(N, matrix, BEE, SOL);
//
//  printf("%12.10f\n", SOL[0]);
//  printf("%12.10f\n", SOL[1]);
//  printf("%12.10f\n", SOL[2]);
//
//  return 0;
//////////////////////////////
     
    
//     int i,j;
//     for(i=1; i<=Triangulation::faceTable.size(); ++i) {
//              vector<int> vertexIds;
//              for(j=0; j<3; ++j)  {
//                       vertexIds = *(Triangulation::faceTable[i].getLocalVertices());
//                       int zork = vertexIds[j];
//                       double temp = angle(Triangulation::vertexTable[zork] , Triangulation::faceTable[i]);
//                       printf("Face: %d, Vertex: %d, Angle = %12.10f\n", i, j+1, temp);
//                       }
//                       system("PAUSE");
//                       }
}  // End of Newtons Method


double dij( Vertex vi, Vertex vj)  
{
       Edge eij;
       vector<int> temp = listIntersection(vi.getLocalEdges(), vj.getLocalEdges());
       eij = Triangulation::edgeTable[temp[0]];
       // This assumes vi and vj are not connected by more than one edge.
       double ri=Geometry::radius(vi);
       double rj=Geometry::radius(vj);
       double Lij=Geometry::length(eij);
       
       double result=(Lij*Lij+ri*ri-rj*rj)/(2*Lij);
       return result;
}

double hij_k( Vertex vi, Vertex vj, Vertex vk){
  Face fijk;
  vector<int> temp_ij = listIntersection(vi.getLocalFaces(), vj.getLocalFaces());
  vector<int> temp = listIntersection( &temp_ij, vk.getLocalFaces() );
  // Is there another way to get the above in cpp language
  fijk = Triangulation::faceTable[temp[0]];
  // This assumes three vertices have a unique face
  double result = (dij(vi, vk)-dij(vi,vj)*cos(Geometry::angle(vi, fijk)))/sin(Geometry::angle(vi, fijk));
  return result;
}

double hijk_l( Vertex vi, Vertex vj, Vertex vk, Vertex vl)
{
  Edge eij;
  Tetra tijkl;
  vector<int> temp_ij = listIntersection(vi.getLocalTetras(), vj.getLocalTetras());
  vector<int> temp_ijk = listIntersection(&temp_ij, vk.getLocalTetras());
  vector<int> temp_ijkl = listIntersection(&temp_ijk, vl.getLocalTetras());
  vector<int> temp2_ij = listIntersection(vi.getLocalEdges(), vj.getLocalEdges());
  // replace the above with a different operator
  eij = Triangulation::edgeTable[temp2_ij[0]];
  tijkl = Triangulation::tetraTable[temp_ijkl[0]];
  // This assumes four vertices define a unique tetrahedron.
  double result = (hij_k(vi, vj, vl) - hij_k(vi, vj, vk) * 
		   cos(Geometry::dihedralAngle(eij, tijkl))) / 
    sin(Geometry::dihedralAngle(eij, tijkl));
  return result;
}

double Aij_kl( Vertex vi, Vertex vj, Vertex vk, Vertex vl)
{
       double result = 0.5*(hij_k(vi,vj,vk)*hijk_l(vi,vj,vk,vl)+hij_k(vi,vj,vl)*hijk_l(vi,vj,vl,vk));
       return result;
}

double Lij_star( Edge eij)
{
       vector<int> sum_over = *(eij.getLocalTetras());
       int i;
       double sum = 0.0;
       vector<int> T_vertices, e_vertices;
       Tetra T;
       Vertex vi,vj,vk,vl;
       for(i=0; i<sum_over.size(); ++i) {
                T = Triangulation::tetraTable[sum_over[i]];
                T_vertices = *(T.getLocalVertices());
                e_vertices = *(eij.getLocalVertices());
                vi = Triangulation::vertexTable[e_vertices[0]];
                vj = Triangulation::vertexTable[e_vertices[1]];
                vk = Triangulation::vertexTable[listDifference(&T_vertices, &e_vertices)[0]];
                vl = Triangulation::vertexTable[listDifference(&T_vertices, &e_vertices)[1]];
                
                sum += Aij_kl(vi, vj, vk, vl);
                }
       return sum;
}

//int Is_ij_an_edge (int i, int j)


double Curvature_Partial ( int i, int l )
{
  double result;
  vector<int> temp;
  Vertex V, Vprime;
  Edge E;
  Tetra T;
  V = Triangulation::vertexTable[i];

  vector<int> Varray;
  Varray.push_back(i);
  vector<int>* local_tetra;

  temp = listDifference(&Varray, Triangulation::vertexTable[l].getLocalVertices());
  vector<int>* edges;
       
  if ( i == l ) {
    edges = Triangulation::vertexTable[i].getLocalEdges();
    double ri = Geometry::radius(Triangulation::vertexTable[i]);
    double sum = 0.0;
    double dihedral_sum = 0.0;
                      
    for (int n=0; n < (*(edges)).size(); ++n) {
      int zork = edges->at(n);
      local_tetra = Triangulation::edgeTable[zork].getLocalTetras();
      dihedral_sum = 0.0;
      E = Triangulation::edgeTable[zork];
      Vprime = Triangulation::vertexTable[listDifference(E.getLocalVertices(), &Varray)[0]];
                
      for (int m=0; m < (*(local_tetra)).size(); ++m) {
	T = Triangulation::tetraTable[local_tetra->at(m)];
	dihedral_sum += Geometry::dihedralAngle(E,T);
      }
      // printf("dihedral_sum = %.10f\n", dihedral_sum);    
      sum += -1.0*(Lij_star(E)/(Geometry::length(E))
		   -(2*PI-dihedral_sum)*(pow(Geometry::radius(V), 2)*pow(Geometry::radius(Vprime),2)*(1-pow(Geometry::eta(E),2)))/pow(Geometry::length(E),3))+Geometry::curvature(V);
    }
                
     // printf("sum = %.10f\n", sum);    
                
                
    result = sum;
    return result;
  }
                
  else if (Triangulation::vertexTable[i].isAdjVertex(l))  {
    //            printf("i= %d, l= %d\n", i, l);
    double sum = 0.0;
    double dihedral_sum = 0.0;
    Vprime = Triangulation::vertexTable[l];
    E = Triangulation::edgeTable[listIntersection(V.getLocalEdges(), Vprime.getLocalEdges())[0]];
    // This assumes that there is a unique edge between two vertices.
    local_tetra = E.getLocalTetras();
                
    for (int m=0; m < (*(local_tetra)).size(); ++m) {
      T = Triangulation::tetraTable[local_tetra->at(m)];
      dihedral_sum += Geometry::dihedralAngle(E,T);
    }
                    
    result = Lij_star(E)/(Geometry::length(E))
      -(2*PI-dihedral_sum)*(pow(Geometry::radius(V), 2)*pow(Geometry::radius(Vprime),2)*(1-pow(Geometry::eta(E),2)))/pow(Geometry::length(E),3); 
    return result;
  }
  else  return 0.0;
}



double Volume_Partial (int i, Tetra t) {
       // This function finds the partial derivative of the volume of tetrahedron t 
       // with respect to vertex i.
       double result;
       
       if (t.isAdjVertex(i)) {
       
       vector<int> opedges; 
       opedges = listDifference(t.getLocalEdges(),  Triangulation::vertexTable[i].getLocalEdges());
       Edge e23, e34, e24, e12, e13, e14;
       
       e23 = Triangulation::edgeTable[opedges[0]];
       e34 = Triangulation::edgeTable[opedges[1]];
       e24 = Triangulation::edgeTable[opedges[2]];
       
       Vertex v1, v2, v3, v4;
       
       v1 = Triangulation::vertexTable[i];
       
       v2 = Triangulation::vertexTable[listIntersection(e23.getLocalVertices(), e24.getLocalVertices())[0]];
       v3 = Triangulation::vertexTable[listIntersection(e23.getLocalVertices(), e34.getLocalVertices())[0]];
       v4 = Triangulation::vertexTable[listIntersection(e24.getLocalVertices(), e34.getLocalVertices())[0]];
       
       e12 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v2.getLocalEdges())[0]];
       e13 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v3.getLocalEdges())[0]];
       e14 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v4.getLocalEdges())[0]];
       // there must be a better way to do the above assignments.
       
       double r1, r2, r3, r4, Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
       
       r1 = Geometry::radius(v1);
       r2 = Geometry::radius(v2);
       r3 = Geometry::radius(v3);
       r4 = Geometry::radius(v4);
       
       Eta12 = Geometry::eta(e12);
       Eta13 = Geometry::eta(e13);
       Eta14 = Geometry::eta(e14);
       Eta23 = Geometry::eta(e23);
       Eta24 = Geometry::eta(e24);
       Eta34 = Geometry::eta(e34);
       
       result = (r1*(r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
             Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
          ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta23 + Eta24*Eta34))*r2 + 
             (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
       r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
               pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
          2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                Eta34 - pow(Eta12,2)*Eta34)*r2 + 
             (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
          ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                pow(Eta24,2))*pow(r2,2) - 
             2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
             (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                pow(Eta34,2))*pow(r3,2))*pow(r4,2))))/
   (6.*sqrt(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
            pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
       2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
             Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
          ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta23 + Eta24*Eta34))*r2 + 
             (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
       pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
               pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
          2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                Eta34 - pow(Eta12,2)*Eta34)*r2 + 
             (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
          ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                pow(Eta24,2))*pow(r2,2) - 
             2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
             (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                pow(Eta34,2))*pow(r3,2))*pow(r4,2))));
                
       return result;
       }
       else return 0.0;

       
}


double Volume_Second_Partial ( int i, int j, Tetra t) {
       
       double result;
       
       if (i == j && t.isAdjVertex(i)) {
               
               vector<int> opedges = listDifference(t.getLocalEdges(),  Triangulation::vertexTable[i].getLocalEdges());
               Edge e23, e34, e24, e12, e13, e14;
       
               e23 = Triangulation::edgeTable[opedges[0]];
               e34 = Triangulation::edgeTable[opedges[1]];
               e24 = Triangulation::edgeTable[opedges[2]];
       
               Vertex v1, v2, v3, v4;
       
               v1 = Triangulation::vertexTable[i];
       
               v2 = Triangulation::vertexTable[listIntersection(e23.getLocalVertices(), e24.getLocalVertices())[0]];
               v3 = Triangulation::vertexTable[listIntersection(e23.getLocalVertices(), e34.getLocalVertices())[0]];
               v4 = Triangulation::vertexTable[listIntersection(e24.getLocalVertices(), e34.getLocalVertices())[0]];
       
               e12 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v2.getLocalEdges())[0]];
               e13 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v3.getLocalEdges())[0]];
               e14 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v4.getLocalEdges())[0]];
       // there must be a better way to do the above assignments.
       
                double r1, r2, r3, r4, Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
       
                r1 = Geometry::radius(v1);
                r2 = Geometry::radius(v2);
                r3 = Geometry::radius(v3);
                r4 = Geometry::radius(v4);
       
                Eta12 = Geometry::eta(e12);
                Eta13 = Geometry::eta(e13);
                Eta14 = Geometry::eta(e14);
                Eta23 = Geometry::eta(e23);
                Eta24 = Geometry::eta(e24);
                Eta34 = Geometry::eta(e34);
                
                result = (r1*(-(r1*pow(2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                  Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3\
                + ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                     Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                  (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                     Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
            2*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
                    pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
               2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                     Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2\
                   + (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + 
                     Eta23*Eta34 + Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
               ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                     pow(Eta24,2))*pow(r2,2) - 
                  2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + 
                     Eta24*Eta34 + Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                  (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                     pow(Eta34,2))*pow(r3,2))*pow(r4,2)),2)) + 
       4*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
               pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
          2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                Eta34 - pow(Eta12,2)*Eta34)*r2 + 
             (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
          ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                pow(Eta24,2))*pow(r2,2) - 
             2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
             (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                pow(Eta34,2))*pow(r3,2))*pow(r4,2))*
        (-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
               pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
          2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
             ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                   Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                   Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
          pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
                  2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
             2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                   Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
                (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
             ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                   pow(Eta24,2))*pow(r2,2) - 
                2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                   Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                   pow(Eta34,2))*pow(r3,2))*pow(r4,2))) + 
       2*(2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
             ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                   Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                   Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
          2*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
                  pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
             2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                   Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
                (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
             ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                   pow(Eta24,2))*pow(r2,2) - 
                2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                   Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                   pow(Eta34,2))*pow(r3,2))*pow(r4,2)))*
        (-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
               pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
          2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
             ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                   Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                   Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
          pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
                  2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
             2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                   Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
                (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
             ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                   pow(Eta24,2))*pow(r2,2) - 
                2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                   Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                   pow(Eta34,2))*pow(r3,2))*pow(r4,2)))))/
   (24.*pow(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
            pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
       2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
             Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
          ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta23 + Eta24*Eta34))*r2 + 
             (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
       pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
               pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
          2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                Eta34 - pow(Eta12,2)*Eta34)*r2 + 
             (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
          ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                pow(Eta24,2))*pow(r2,2) - 
             2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
             (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                pow(Eta34,2))*pow(r3,2))*pow(r4,2)),1.5));
//printf("Volume_Second_Partial= %.10f\n", result);                
       return result;
       }
       
             
       else if (i != j && t.isAdjVertex(i) && t.isAdjVertex(j)) {

             Edge e23, e34, e24, e12, e13, e14;
             vector<int> zool;
             
          
             zool.push_back(i);
             zool.push_back(j);
             
             // push_back(alpha) adds alpha to the end of the vector.
             
             vector<int> tempvertices = listDifference(t.getLocalVertices(), &zool);
                   
             Vertex v1, v2, v3, v4;
       
             v1 = Triangulation::vertexTable[i];
             v2 = Triangulation::vertexTable[j];
       
             v3 = Triangulation::vertexTable[tempvertices[0]];
             v4 = Triangulation::vertexTable[tempvertices[1]];
            
             e12 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v2.getLocalEdges())[0]];
             e13 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v3.getLocalEdges())[0]];
             e14 = Triangulation::edgeTable[listIntersection(v1.getLocalEdges(),v4.getLocalEdges())[0]];
             e23 = Triangulation::edgeTable[listIntersection(v2.getLocalEdges(),v3.getLocalEdges())[0]];
             e24 = Triangulation::edgeTable[listIntersection(v2.getLocalEdges(),v4.getLocalEdges())[0]];
             e34 = Triangulation::edgeTable[listIntersection(v3.getLocalEdges(),v4.getLocalEdges())[0]];
             
            // there must be a better way to do the above assignments.
       
             double r1, r2, r3, r4, Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
       
             r1 = Geometry::radius(v1);
             r2 = Geometry::radius(v2);
             r3 = Geometry::radius(v3);
             r4 = Geometry::radius(v4);
       
             Eta12 = Geometry::eta(e12);
             Eta13 = Geometry::eta(e13);
             Eta14 = Geometry::eta(e14);
             Eta23 = Geometry::eta(e23);
             Eta24 = Geometry::eta(e24);
             Eta34 = Geometry::eta(e34);
             
             result = r2*(-(r1*(-2*(-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
              pow(Eta34,2))*r2*pow(r3,2)*pow(r4,2) + 
           2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                 Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r3 + 
              (Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                 Eta12*(Eta23 + Eta24*Eta34))*r4) + 
           2*r1*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                 Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
              ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                    Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                 (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                    Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
           pow(r1,2)*(-2*(-1 + pow(Eta12,2) + pow(Eta13,2) + 
                 2*Eta12*Eta13*Eta23 + pow(Eta23,2))*r2*pow(r3,2) + 
              2*(Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                 Eta34 - pow(Eta12,2)*Eta34)*r2*r3*r4 + 
              2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                    Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2\
                  + (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + 
                    Eta23*Eta34 + Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
              (2*(-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                    pow(Eta24,2))*r2 - 
                 2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + 
                    Eta24*Eta34 + Eta12*(Eta13 + Eta14*Eta34))*r3)*pow(r4,2)))*
         (2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
                 Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
              ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                    Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                 (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                    Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
           2*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
                   pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
              2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                    Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2\
                  + (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + 
                    Eta23*Eta34 + Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
              ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                    pow(Eta24,2))*pow(r2,2) - 
                 2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + 
                    Eta24*Eta34 + Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                 (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                    pow(Eta34,2))*pow(r3,2))*pow(r4,2))))/
      (24.*pow(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
               pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
          2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
             ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                   Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                   Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
          pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
                  2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
             2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                   Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
                (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
             ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                   pow(Eta24,2))*pow(r2,2) - 
                2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                   Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                   pow(Eta34,2))*pow(r3,2))*pow(r4,2)),1.5)) + 
     (r1*(2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta24 + Eta23*Eta34))*r3 + 
             (Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta23 + Eta24*Eta34))*r4) + 
          2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
                Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
             ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                   Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                   Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
          2*r1*(-2*(-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
                pow(Eta23,2))*r2*pow(r3,2) + 
             2*(Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                Eta34 - pow(Eta12,2)*Eta34)*r2*r3*r4 + 
             2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
                   Eta34 - pow(Eta12,2)*Eta34)*r2 + 
                (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
             (2*(-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                   pow(Eta24,2))*r2 - 
                2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                   Eta12*(Eta13 + Eta14*Eta34))*r3)*pow(r4,2))))/
      (12.*sqrt(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
               pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
          2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
                Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
             ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
                   Eta12*(Eta23 + Eta24*Eta34))*r2 + 
                (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
                   Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
          pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
                  2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
             2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
                   Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
                (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
                   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
             ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
                   pow(Eta24,2))*pow(r2,2) - 
                2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
                   Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
                (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
                   pow(Eta34,2))*pow(r3,2))*pow(r4,2)))));
                   
//printf("Volume_Second_Partial= %.10f\n", result);         
          return result;
          }
         
          else return 0.0;
//printf("Volume_Second_Partial= %.10f\n", result); 
}


double Total_Volume () {
       map<int, Tetra>::iterator tit;
       double sumV = 0;
       for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
       {
       sumV += Geometry::volume(tit->second);
// sumV calculates the total volume of the manifold
        }
        return sumV;
}

double Total_Curvature () {
       map<int, Vertex>::iterator vit;
       double sumK = 0;
       
       for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
       {
       sumK += Geometry::curvature(vit->second);

       }

       return sumK;
       
}
       

double EHR_Partial (int i) {
  // Calculates the partial derivative of the EHR-functional with respect to the 
  // logarithm of radius i.  
  double V = Total_Volume();
  double K = Total_Curvature();
  double result = 0;
  double VolSumPartial = 0;
  map<int, Tetra>::iterator tit;
       
  for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++){
      VolSumPartial += Volume_Partial(i,tit->second);
  }
       
  result = pow(V, -4.0/3.0)*(Geometry::curvature(Triangulation::vertexTable[i])*V 
			     - (1.0/3.0)*K*VolSumPartial);

  return result;
}


double EHR_Second_Partial (int i, int j) {
  // Calculates the second partial of the EHR (with respect to log radii).
  double V = Total_Volume();
  double K = Total_Curvature();
  double result = 0;
  double VolSumPartial_i = 0;
  double VolSumPartial_j = 0;
  double VolSumSecondPartial = 0;
  map<int, Tetra>::iterator tit;
       
  for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++){
    VolSumPartial_i += Volume_Partial(i,tit->second);
    VolSumPartial_j += Volume_Partial(j,tit->second);
    VolSumSecondPartial += Volume_Second_Partial(i, j, tit->second);
  }
  
  result = pow(V, (-4.0/3.0))*(1.0/3.0)*(3*V*Curvature_Partial(i,j)
					 -Geometry::curvature(Triangulation::vertexTable[i])*VolSumPartial_j
					 -Geometry::curvature(Triangulation::vertexTable[j])*VolSumPartial_i
					 +(4.0/3.0)*K*pow(V, -1.0)*VolSumPartial_i*VolSumPartial_j
					 -K*VolSumSecondPartial);

  return result;
}

            
            

         
       
       
       
       












//==============================================================================
// return 1 if system not solving
// nDim - system dimension
// pfMatr - matrix with coefficients
// pfVect - vector with free members
// pfSolution - vector with system solution
// pfMatr becames trianglular after function call
// pfVect changes after function call
//
// Developer: Henry Guennadi Levkin
//
//==============================================================================
int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution)
//int LinearEquationsSolving(int nDim, double pfMatr[3][3], double pfVect[3], double pfSolution[3])
{
  double fMaxElem;
  double fAcc;

  int i , j, k, m;


  for(k=0; k<(nDim-1); k++) // base row of matrix
  {
    // search of line with max element
    fMaxElem = fabs( pfMatr[k*nDim + k] );
    m = k;
    for(i=k+1; i<nDim; i++)
    {
      if(fMaxElem < fabs(pfMatr[i*nDim + k]) )
      {
        fMaxElem = pfMatr[i*nDim + k];
        m = i;
      }
    }
    
    // permutation of base line (index k) and max element line(index m)
    if(m != k)
    {
      for(i=k; i<nDim; i++)
      {
        fAcc               = pfMatr[k*nDim + i];
        pfMatr[k*nDim + i] = pfMatr[m*nDim + i];
        pfMatr[m*nDim + i] = fAcc;
      }
      fAcc = pfVect[k];
      pfVect[k] = pfVect[m];
      pfVect[m] = fAcc;
    }

    if( pfMatr[k*nDim + k] == 0.) return 1; // needs improvement !!!

    // triangulation of matrix with coefficients
    for(j=(k+1); j<nDim; j++) // current row of matrix
    {
      fAcc = - pfMatr[j*nDim + k] / pfMatr[k*nDim + k];
      for(i=k; i<nDim; i++)
      {
        pfMatr[j*nDim + i] = pfMatr[j*nDim + i] + fAcc*pfMatr[k*nDim + i];
      }
      pfVect[j] = pfVect[j] + fAcc*pfVect[k]; // free member recalculation
    }
  }

  for(k=(nDim-1); k>=0; k--)
  {
    pfSolution[k] = pfVect[k];
    for(i=(k+1); i<nDim; i++)
    {
      pfSolution[k] -= (pfMatr[k*nDim + i]*pfSolution[i]);
    }
    pfSolution[k] = pfSolution[k] / pfMatr[k*nDim + k];
  }

  return 0;
}
