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
#include "total_volume_partial.h"
#include "total_volume_second_partial.h"
#include "nehr_partial.h"

void testPartialEdgePartial();
void testDihedralAnglePartial();
void testMixedNEHRPartial();
void testRadiusPartial();
void testCurvaturePartial();
void testVolumePartial();
void testVolumeSecondPartial();
void testTotalVolumePartial();
void testTotalVolumeSecondPartial();
void testNEHRPartial();

int main(int argc, char *argv[])
{
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;    
    map<int, Tetra>::iterator tit;
    
    char* triangulation = "../../Data/3DManifolds/StandardFormat/pentachoron.txt";
    read3DTriangulationFile(triangulation);
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        Radius::At(vit->second)->setValue(1.0);        
    }
    //Radius::At(Triangulation::vertexTable[1])->setValue(2.0);
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        Eta::At(eit->second)->setValue(1.0);        
    }
    
   // testMixedNEHRPartial();
   // testVolumePartial();
   // testCurvaturePartial();
   // testRadiusPartial();
   // testPartialEdgePartial();
   // testDihedralAnglePartial();
   // testVolumeSecondPartial();
   // testTotalVolumePartial();
   // testTotalVolumeSecondPartial();
   testNEHRPartial();
   pause("Press enter to continue.....");
}

void testVolumePartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    
    vector<int> local_tetras;
    Tetra t;
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tVolumePartialsTest\n\t-----------------------------\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       fprintf(results, "\nVertex %d:\n", vit->first);
       local_tetras = *(vit->second.getLocalTetras());
       for(int i = 0; i < local_tetras.size(); i++) {
         t = Triangulation::tetraTable[local_tetras[i]];
         fprintf(results, "\tTetra%d: %f\n", t.getIndex(), VolumePartial::valueAt(vit->second, t));
       }
    }
    fprintf(results, "\n");
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       fprintf(results, "\nEdge %d:\n", eit->first);
       local_tetras = *(eit->second.getLocalTetras());
       for(int i = 0; i < local_tetras.size(); i++) {
         t = Triangulation::tetraTable[local_tetras[i]];
         fprintf(results, "\tTetra%d: %f\n", t.getIndex(), VolumePartial::valueAt(eit->second, t));
       }
    }
    
    fclose(results);
}

void testTotalVolumePartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tTotalVolumePartialsTest\n\t-----------------------------\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       fprintf(results, "\nVertex %d: %f\n", vit->first, TotalVolumePartial::valueAt(vit->second));
    }
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       fprintf(results, "\nEdge %d: %f\n", eit->first, TotalVolumePartial::valueAt(eit->second));
    }
    
    fclose(results);
}

void testTotalVolumeSecondPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tTotalVolumeSecondPartialsTest\n\t-----------------------------\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       fprintf(results, "\nVertex %d:\n", vit->first);
       for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
          fprintf(results, "\tEdge %d: %f\n", eit->first, 
                           TotalVolumeSecondPartial::valueAt(vit->second, eit->second));
       }
    }
    
    fclose(results);
}

void testVolumeSecondPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Tetra>::iterator tit;
    
    vector<int> local_tetras;
    vector<int> local_verts;
    vector<int> local_edges;
    Tetra t;
    Vertex v;
    Edge e;
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tVolumeSecondPartialsTest\n\t-----------------------------\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       fprintf(results, "\nVertex %d:\n", vit->first);
       local_tetras = *(vit->second.getLocalTetras());
       for(int i = 0; i < local_tetras.size(); i++) {
         t = Triangulation::tetraTable[local_tetras[i]];
         fprintf(results, "\tTetra%d:\n", t.getIndex());
         local_verts = *(t.getLocalVertices());
         for(int j = 0; j < local_verts.size(); j++) {
             v = Triangulation::vertexTable[local_verts[j]];
             fprintf(results, "\t\tVertex%d: %f\n", v.getIndex(), 
                              VolumeSecondPartial::valueAt(vit->second, v, t));      
         }
       }
    }
    fprintf(results, "\n");
    for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++) {
      fprintf(results, "\nTetra %d:\n", tit->first);
      local_verts = *(tit->second.getLocalVertices());
      local_edges = *(tit->second.getLocalEdges());
      for(int i = 0; i < local_verts.size(); i++) {
        v = Triangulation::vertexTable[local_verts[i]];
        fprintf(results, "\tVertex %d:\n", v.getIndex());
        for(int j = 0; j < local_edges.size(); j++) {
          e = Triangulation::edgeTable[local_edges[j]];
          fprintf(results, "\t\tEdge %d: %f\n", e.getIndex(), 
                           VolumeSecondPartial::valueAt(v, e, tit->second));
        }
      }
    }
    
    fclose(results);
}

void testPartialEdgePartial() {
    map<int, Edge>::iterator eit;   
    
    vector<int> local_vertices;
    
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
           
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        printf("Eta %d:\n", eit->first);
        for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
           printf("\t%f\n", CurvaturePartial::valueAt(vit->second, eit->second));
        }
        printf("\n");       
    }
}

void testNEHRPartial() {
    map<int, Edge>::iterator eit;   
    
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tNEHRPartialTest\n\t-----------------------------\n\n");
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        fprintf(results, "Eta %d: %f\n", eit->first, NEHRPartial::valueAt(eit->second));   
    }
    fclose(results);
}

void testDihedralAnglePartial() { 
    map<int, Tetra>::iterator tit;
    
    vector<int> local_edges;
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tDihedralAnglePartialsTest\n\t-----------------------------\n");
    for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++) {
       local_edges = *(tit->second.getLocalEdges());
       fprintf(results,"Tetra %d:\n", tit->first);
       for(int i = 0; i < local_edges.size(); i++) {
           Edge ei = Triangulation::edgeTable[local_edges[i]];
           for(int j = 0; j < local_edges.size(); j++) {
              Edge ej = Triangulation::edgeTable[local_edges[j]];
              fprintf(results,"\td(theta_%d)/d(eta_%d) = %f\n", ei.getIndex(), ej.getIndex(),
                        DihedralAnglePartial::valueAt(ej, ei, tit->second));
           }
           fprintf(results, "\n");
       }       
    }
    fclose(results);
}

void testMixedNEHRPartial() {
    map<int, Edge>::iterator eit;  
        
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
