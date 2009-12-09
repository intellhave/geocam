#include <cstdlib>
#include <iostream>

#include "utilities.h"
#include "3DInputOutput.h"
#include "radius.h"
#include "eta.h"
#include "partial_edge_partial.h"
#include "dih_angle_partial.h"
#include "partials.h"
#include "ehr_second_partial.h"

void testPartialEdgePartial();
void testDihedralAnglePartial();
void testMixedNEHRPartial();
void testRadiusPartial();
void testCurvaturePartial();

int main(int argc, char *argv[])
{
    testMixedNEHRPartial();
   // testCurvaturePartial();
   // testRadiusPartial();
   // testPartialEdgePartial();
   // testDihedralAnglePartial();
    
    pause("Press enter to continue.....");
}

void testPartialEdgePartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;    
    map<int, Tetra>::iterator tit;
    
    vector<int> local_edges;
    vector<int> local_vertices;
    
    char* triangulation = "../../Data/3DManifolds/StandardFormat/pentachoron.txt";
    read3DTriangulationFile(triangulation);
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        Radius::At(vit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        Eta::At(eit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       local_vertices = *(eit->second.getLocalVertices());
       for(int i = 0; i < local_vertices.size(); i++) {
          Vertex v = Triangulation::vertexTable[local_vertices[i]];
          printf("d(d_%d,%d)/d(eta_%d) = %f\n", local_vertices[i], local_vertices[(i + 1) %2],
                                 eit->first, PartialEdgePartial::valueAt(v, eit->second));       
       }
    }
}

void testCurvaturePartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;    
    map<int, Tetra>::iterator tit;
    
    vector<int> local_edges;
    vector<int> local_vertices;
    
    char* triangulation = "../../Data/3DManifolds/StandardFormat/pentachoron.txt";
    read3DTriangulationFile(triangulation);
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        Radius::At(vit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        Eta::At(eit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       local_vertices = *(eit->second.getLocalVertices());
       for(int i = 0; i < local_vertices.size(); i++) {
          Vertex v = Triangulation::vertexTable[local_vertices[i]];
          printf("d(k_%d)/d(eta_%d) = %f\n", local_vertices[i],
                                 eit->first, CurvaturePartial::valueAt(v, eit->second));       
       }
    }
}

void testDihedralAnglePartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;    
    map<int, Tetra>::iterator tit;
    
    vector<int> local_edges;
    vector<int> local_vertices;
    
    char* triangulation = "../../Data/3DManifolds/StandardFormat/pentachoron.txt";
    read3DTriangulationFile(triangulation);
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        Radius::At(vit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        Eta::At(eit->second)->setValue(1.0);        
    }
    
        for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++) {
       local_edges = *(tit->second.getLocalEdges());
       printf("Tetra %d:\n", tit->first);
       for(int i = 0; i < local_edges.size(); i++) {
           Edge ei = Triangulation::edgeTable[local_edges[i]];
           for(int j = 0; j < local_edges.size(); j++) {
              Edge ej = Triangulation::edgeTable[local_edges[j]];
              printf("\td(theta_%d)/d(eta_%d) = %f\n", ei.getIndex(), ej.getIndex(),
                        DihedralAnglePartial::valueAt(ej, ei, tit->second));
           }
           printf("\n");
       }       
    }
}

void testMixedNEHRPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;    
    map<int, Tetra>::iterator tit;
    
    vector<int> local_edges;
    vector<int> local_vertices;
    
    char* triangulation = "../../Data/3DManifolds/StandardFormat/pentachoron.txt";
    read3DTriangulationFile(triangulation);
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        Radius::At(vit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        Eta::At(eit->second)->setValue(1.0);        
    }
    
    double gradient[Triangulation::vertexTable.size()];
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        mixedNEHRPartial(eit->first, gradient);
        printf("Eta %d:\n", eit->first);
        for(int i = 0; i < Triangulation::vertexTable.size(); i++) {
           printf("\t%f\n", gradient[i]);
        }
        printf("\n");
    }
}

void testRadiusPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;    
    map<int, Tetra>::iterator tit;
    
    vector<int> local_edges;
    vector<int> local_vertices;
    
    char* triangulation = "../../Data/3DManifolds/StandardFormat/pentachoron.txt";
    read3DTriangulationFile(triangulation);
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        Radius::At(vit->second)->setValue(1.0);        
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        Eta::At(eit->second)->setValue(1.0);        
    }
    
    double solns[Triangulation::edgeTable.size()][Triangulation::vertexTable.size()];
    double hessian[Triangulation::vertexTable.size()][Triangulation::vertexTable.size()];
    double temp[Triangulation::vertexTable.size()][Triangulation::vertexTable.size()];
    map<int, Vertex>::iterator vit2;
    
    int i,j;
    // Build hessian
    for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
      for(vit2 = vit, j = i; vit2 != Triangulation::vertexTable.end(); vit2++, j++) {
         hessian[j][i] = hessian[i][j] = EHRSecondPartial::valueAt(vit->second, vit2->second);
      }
    }
    
    for(i = 0; i < Triangulation::vertexTable.size(); i++) {
          printf("hess[%d][] = <", i);
          for(j = 0; j < Triangulation::vertexTable.size(); j++) {
                printf("%f, ", hessian[i][j]);
          }
          printf(">\n");
    }
    
    for(eit = Triangulation::edgeTable.begin(), i = 0; eit != Triangulation::edgeTable.end(); eit++, i++) {
       for(j = 0; j < Triangulation::vertexTable.size(); j++) {
             for(int k = 0; k < Triangulation::vertexTable.size(); k++) {
                     temp[j][k] = hessian[j][k];
             }
       }
       radiusPartial(eit->first, solns[i], (double*) temp);
    }

       for(i = 0; i < Triangulation::vertexTable.size(); i++) {
          printf("hess[%d][] = <", i);
          for(j = 0; j < Triangulation::vertexTable.size(); j++) {
                printf("%f, ", hessian[i][j]);
          }
          printf(">\n");
       }


    
    for(i = 0; i < Triangulation::edgeTable.size(); i++) {
          printf("dr_j/de_%d = <", i + 1);
          for(j = 0; j < Triangulation::vertexTable.size(); j++) {
                printf("%f, ", solns[i][j]);
          }
          printf(">\n");
    }
}
