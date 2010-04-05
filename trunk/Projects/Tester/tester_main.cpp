#include <cstdlib>
#include <iostream>

#include "utilities.h"
#include "3DInputOutput.h"
#include "radius.h"
#include "eta.h"
#include "partial_edge_partial.h"
#include "dih_angle_partial.h"
#include "ehr_second_partial.h"
#include "total_volume_partial.h"
#include "total_volume_second_partial.h"
#include "nehr_partial.h"
#include "nehr_second_partial.h"
#include "radius_partial.h"
#include "NMethod.h"

void testPartialEdgeSecondPartial();
void testDihedralAngleSecondPartial();
void testCurvatureSecondPartial();
void testPartialEdgePartial();
void testDihedralAnglePartial();
void testRadiusPartial();
void testCurvaturePartial();
void testVolumePartial();
void testVolumeSecondPartial();
void testTotalVolumePartial();
void testTotalVolumeSecondPartial();
void testNEHRPartial();
void testNEHRSecondPartial();
void testNEHRSecondPartialForm2();
void testMatrix();
void testNewtonsMethod();

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
    
   // testVolumePartial();
   // testCurvaturePartial();
   // testRadiusPartial();
   // testPartialEdgePartial();
   // testDihedralAnglePartial();
   // testVolumeSecondPartial();
   // testTotalVolumePartial();
   // testTotalVolumeSecondPartial();
   // testNEHRPartial();
    testNEHRSecondPartial();
   // testNEHRSecondPartialForm2();
   // testMatrix();
   // testDihedralAngleSecondPartial();
   // testPartialEdgeSecondPartial();
   // testCurvatureSecondPartial();
   // testNewtonsMethod();
   pause("Press enter to continue.....");
}

void matrixScalarMultiply(double** mat, int len, double scalar) {
  for(int i = 0; i < len; i++) {
    for(int j = 0; j < len; j++) {
      mat[i][j] = mat[i][j] * scalar;
    }
  }
}

void testMatrix() {
  Matrix<double> m(3, 3);
  m[0][0] = 3.0;
  m[0][1] = 5.0;
  m[0][2] = 2.4;
  m[1][0] = 1.0;
  m[1][1] = 2.5;
  m[1][2] = 1.0;
  m[2][0] = 0;
  m[2][1] = 4.0;
  m[2][2] = 0;
  m.print(stdout);
  printf("\ndet(m) = %f\n\n", m.determinant());
  Matrix<double> adj = m.adjoint();
  adj.print(stdout);
  printf("\n");
  Matrix<double> identity = (m * adj) * (1 / m.determinant());
  identity.print(stdout);
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
    map<int, Vertex>::iterator vit2;
    map<int, Edge>::iterator eit;
    map<int, Edge>::iterator eit2;
    
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tTotalVolumeSecondPartialsTest\n\t-----------------------------\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       fprintf(results, "\nVertex %d:\n", vit->first);
       for(vit2 = Triangulation::vertexTable.begin(); vit2 != Triangulation::vertexTable.end(); vit2++) {
          fprintf(results, "\tVertex %d: %f\n", vit2->first,
                           TotalVolumeSecondPartial::valueAt(vit->second, vit2->second));
       }
    }
    
    fprintf(results, "\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       fprintf(results, "\nVertex %d:\n", vit->first);
       for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
          fprintf(results, "\tEdge %d: %f\n", eit->first, 
                           TotalVolumeSecondPartial::valueAt(vit->second, eit->second));
       }
    }
    
    fprintf(results, "\n");

    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       fprintf(results, "\nEdge %d:\n", eit->first);
       for(eit2 = Triangulation::edgeTable.begin(); eit2 != Triangulation::edgeTable.end(); eit2++) {
          fprintf(results, "\tEdge %d: %f\n", eit2->first,
                           TotalVolumeSecondPartial::valueAt(eit->second, eit2->second));
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
    Edge e, f;
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
    fprintf(results, "\n");
    for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++) {
      fprintf(results, "\nTetra %d:\n", tit->first);
      local_edges = *(tit->second.getLocalEdges());
      for(int i = 0; i < local_edges.size(); i++) {
        e = Triangulation::edgeTable[local_edges[i]];
        fprintf(results, "\tEdge %d:\n", e.getIndex());
        for(int j = 0; j < local_edges.size(); j++) {
          f = Triangulation::edgeTable[local_edges[j]];
          fprintf(results, "\t\tEdge %d: %f\n", f.getIndex(),
                           VolumeSecondPartial::valueAt(e, f, tit->second));
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
                                 eit->first, PartialEdgePartial::valueAt(v, eit->second, eit->second));
       }
    }
}

void testPartialEdgeSecondPartial() {
  map<int, Edge>::iterator eit;
  map<int, Vertex>::iterator vit;

  vector<int> local_edges;
  Edge e;

  FILE* results = fopen("results.txt", "w");

  fprintf(results, "\t\tPartialEdgeSecondPartialsTest\n\t-----------------------------\n");

  for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
    fprintf(results, "\nVertex %d:\n", vit->first);
    local_edges = *(vit->second.getLocalEdges());
    for(int i = 0; i < local_edges.size(); i++) {
      e = Triangulation::edgeTable[local_edges[i]];
      fprintf(results, "\tEdge %d:\n", e.getIndex());
      for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        fprintf(results, "\t\tEdge %d: %f\n", eit->first,
                    PartialEdgeSecondPartial::valueAt(vit->second, e, e, eit->second));
      }
    }
  }
  
  fclose(results);
}

void testCurvatureSecondPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    map<int, Edge>::iterator eit2;

  FILE* results = fopen("results.txt", "w");

  fprintf(results, "\t\tCurvatureSecondPartialsTest\n\t-----------------------------\n");

  for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
    fprintf(results, "\nVertex %d:\n", vit->first);
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       fprintf(results, "\tEdge %d:\n", eit->first);
       for(eit2 = Triangulation::edgeTable.begin(); eit2 != Triangulation::edgeTable.end(); eit2++) {
          fprintf(results, "\t\tEdge %d: %f\n", eit2->first,
                           CurvatureSecondPartial::valueAt(vit->second, eit->second, eit2->second));
       }
    }
  }

  fclose(results);
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
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;   
    
    FILE* results = fopen("results.txt", "w");
       
    fprintf(results, "\t\tNEHRPartialTest\n\t-----------------------------\n\n");
    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        fprintf(results, "Vertex %d: %f\n", vit->first, NEHRPartial::valueAt(vit->second));   
    }    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        fprintf(results, "Eta %d: %f\n", eit->first, NEHRPartial::valueAt(eit->second));   
    }
    fclose(results);
}

void testNEHRSecondPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vit2;
    map<int, Edge>::iterator eit;
    map<int, Edge>::iterator eit2;

    FILE* results = fopen("results.txt", "w");

    fprintf(results, "\t\tNEHRSecondPartialTest\n\t-----------------------------\n\n");

    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
      fprintf(results, "Vertex %d:\n", vit->first);
      for(vit2 = Triangulation::vertexTable.begin(); vit2 != Triangulation::vertexTable.end(); vit2++) {
        fprintf(results, "\tVertex %d: %f\n", vit2->first, NEHRSecondPartial::valueAt(vit->second, vit2->second));
      }
    }
    fprintf(results, "\n");
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
      fprintf(results, "Vertex %d:\n", vit->first);
      for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        fprintf(results, "\tEta %d: %f\n", eit->first, NEHRSecondPartial::valueAt(vit->second, eit->second));
      }
    }
    fprintf(results, "\n");
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
      fprintf(results, "Edge %d:\n", eit->first);
      for(eit2 = Triangulation::edgeTable.begin(); eit2 != Triangulation::edgeTable.end(); eit2++) {
        fprintf(results, "\tEdge %d: %f\n", eit2->first, NEHRSecondPartial::valueAt(eit->second, eit2->second));
      }
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

void testDihedralAngleSecondPartial() {
    map<int, Tetra>::iterator tit;
    map<int, Edge>::iterator eit;
    map<int, Edge>::iterator eit2;
    map<int, Edge>::iterator Begin = Triangulation::edgeTable.begin();
    map<int, Edge>::iterator End = Triangulation::edgeTable.end();
    vector<int> local_edges;
    FILE* results = fopen("results.txt", "w");

    fprintf(results, "\t\tDihedralAngleSecondPartialsTest\n\t-----------------------------\n");
    for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++) {
       local_edges = *(tit->second.getLocalEdges());
       fprintf(results,"Tetra %d:\n", tit->first);
       for(int i = 0; i < local_edges.size(); i++) {
           Edge ei = Triangulation::edgeTable[local_edges[i]];
           fprintf(results, "\tEdge %d:\n", ei.getIndex());
           for(eit = Begin; eit != End; eit++) {
              fprintf(results, "\t\tEdge %d:\n", eit->first);
                for(eit2 = Begin; eit2 != End; eit2++) {
                  fprintf(results,"\t\t\t Edge % d: %f\n", eit2->first,
                        DihedralAngleSecondPartial::valueAt(eit->second, eit2->second, ei, tit->second));
                }
           }
           fprintf(results, "\n");
       }
    }
    fclose(results);
}

void testRadiusPartial() {
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;

    FILE* results = fopen("results.txt", "w");

    fprintf(results, "\t\tRadiusPartialTest\n\t-----------------------------\n\n");

    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
      fprintf(results, "Edge %d:\n", eit->first);
      for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
        fprintf(results, "\tVertex %d: %f\n", vit->first, RadiusPartial::valueAt(vit->second, eit->second));
      }
    }

    fclose(results);
}

double ellipse(double vars[]) {
  double val = 1 - pow(vars[0], 2) / 4 - pow(vars[1], 2) / 9;
  return sqrt(val);
}

void gradientF(double vars[], double sol[]) {
  double val = 1 - pow(vars[0], 2) / 4 - pow(vars[1], 2) / 9;
  sol[0] = (-vars[0]/4)/sqrt(val);
  sol[1] = (-vars[1]/9)/sqrt(val);
}

void hessianF(double vars[], Matrix<double>& sol) {
  double val = 1 - pow(vars[0], 2) / 4 - pow(vars[1], 2) / 9;
  sol[0][0] = ((-1.0/4)*val - (vars[0]/16)) / pow(val, 3.0/2);
  sol[1][1] = ((-1.0/9)*val - (vars[1]/81)) / pow(val, 3.0/2);
  sol[0][1] = (-vars[0]/4)*(-vars[1]/9)*(-1) / pow(val, 3.0/2);
  sol[1][0] = sol[0][1];
}

void testNewtonsMethod() {
    // Create the NewtonsMethod object, 2 variables
   NewtonsMethod *nm1 = new NewtonsMethod(ellipse, 2);
   double x_n[] = {1, 1};
   int i = 1;
   // Continue with the procedure until the length of the gradient is
   // less than 0.000001.
   FILE* results = fopen("results.txt", "w");
   while(nm1->step(x_n, NMETHOD_MAX) > 0.000001) {
     fprintf(results, "\n***** Step %d *****\n", i++);
     nm1->printInfo(results);
     for(int j = 0; j < 2; j++) {
       fprintf(results, "x_n_%d[%d] = %f\n", i, j, x_n[j]);
     }
   }
   
   fprintf(results, "\n\n\n");
   
   NewtonsMethod *nm2 = new NewtonsMethod(ellipse, gradientF, hessianF, 2);
   x_n[0] = 1; x_n[1] = 1;
   i = 1;
   while(nm2->step(x_n, NMETHOD_MAX) > 0.000001) {
     fprintf(results, "\n***** Step %d *****\n", i++);
     nm2->printInfo(results);
     for(int j = 0; j < 2; j++) {
       fprintf(results, "x_n_%d[%d] = %f\n", i, j, x_n[j]);
     }
   }
   
   fclose(results);
}
