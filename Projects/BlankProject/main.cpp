#include "triangulation.h"
#include "triangulationInputOutput.h"
#include "utilities.h"

int main(int argc, char * argv[]) {
      
     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Face>::iterator fit;
     map<int, Tetra>::iterator tit;
     
     vector<int> edges;
     vector<int> faces;
     vector<int> tetras;
      
    char from[] = "outputtriangles.txt";

   char to[] = "converted.txt";
   makeTriangulationFile(from, to);
   readTriangulationFile(to);

   int vertSize = Triangulation::vertexTable.size();
   int edgeSize = Triangulation::edgeTable.size();
   int faceSize = Triangulation::faceTable.size();
   
   printf("V= %d, E=%d, F=%d\n", vertSize, edgeSize, faceSize);
      
   int EulerCharacteristic = vertSize-edgeSize+faceSize;
   
   printf("Euler Characteristic = %d\n", EulerCharacteristic);
   pause("press enter to continue");
   vector<int>* ZIM;
   vector<int>* ZAM;
   vector<int>* ZUM;

   for(int i = 1; i <= edgeSize; i++) {
           ZIM = Triangulation::edgeTable[i].getLocalFaces();
           printf("Faces sharing edge %d are:\n", i);
           for (int n=0; n < (*(ZIM)).size(); ++n) {
               printf("%d\n", ZIM->at(n));
               }
               pause("press enter to continue");
               }
               
    for(int i = 1; i <= vertSize; i++) {
           ZAM = Triangulation::vertexTable[i].getLocalVertices();
           printf("Vertices near vertex %d are:\n", i);
           for (int n=0; n < (*(ZAM)).size(); ++n) {
               printf("%d\n", ZAM->at(n));
               }
               pause("press enter to continue");
               }
               

   
// The following calculations verify that each face is shared by exactly two tetrahedra   
//   for(int i = 1; i <= faceSize; i++) {
//           ZIM = Triangulation::faceTable[i].getLocalTetras();
//           printf("Tetra sharing face %d are:\n", i);
//           for (int n=0; n < (*(ZIM)).size(); ++n) {
//               printf("%d\n", ZIM->at(n));
//               }
//               system("PAUSE");
//               }

// The following calculations verify that around each vertex the triangulation is a manifold.
//   for(int i = 1; i<= vertSize; i++) {
//           ZIM = Triangulation::vertexTable[i].getLocalEdges();
//           ZAM = Triangulation::vertexTable[i].getLocalFaces();
//           ZUM = Triangulation::vertexTable[i].getLocalTetras();
//           printf("Euler Characteristic for vertex %d = %d\n", i, (*(ZIM)).size()-(*(ZAM)).size()+(*(ZUM)).size());
//           pause("press enter to continue")
//
//           }

// The following calculation verifies that the edges satisfy the manifold conditions
   
    pause("Done...press enter to exit."); // PAUSE   
    

    
    
}
