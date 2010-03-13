#include "triangulation.h"
#include "3DInputOutput.h"
#include "triangulationInputOutput.h"
#include "utilities.h"
#include "radius.h"
#include "eta.h"
#include "length.h"
#include <cmath>

//used in flip section
#include "hinge_flip.h"

int main(int argc, char * argv[]) {

      //relative path names
     char filename1[] = "../../Data/2DManifolds/LutzFormat/unusualtorus.txt";
     char modified1[] = "../../Data/2DManifolds/StandardFormat/unusualtorus.txt";

     
     makeTriangulationFile(filename1, modified1);
     readTriangulationFile(modified1);

     map<int, Vertex>::iterator vit;
     map<int, Edge>::iterator eit;
     map<int, Face>::iterator fit;
     map<int, Tetra>::iterator tit;
     
     vector<int> edges;
     vector<int> faces;
     vector<int> tetras;

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

   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++) {

           ZIM = (eit->second).getLocalFaces();
           printf("Faces sharing edge %d are:\n", eit->first);
           printf("size of local faces vector = %d\n",(*(ZIM)).size());
           for (int n=0; n < (*(ZIM)).size(); ++n) {
               printf("%d\n", ZIM->at(n));
               }
               pause("press enter to continue");
               }
               pause("press enter to continue, this is the end of local faces checking.");
               
               for(int i=1; i<=vertSize; i++){
                Radius::At(Triangulation::vertexTable[i])->setValue(1.0+.01*i);
                printf("Radius at vertex %d is: %f\n",i,Radius::valueAt(Triangulation::vertexTable[i]));
                }
                int j=1;
                for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++,j++) {
                    Eta::At(eit->second)->setValue(1.0+.01*j);
                    printf("Eta at edge %d is: %f\n",eit->first,Eta::valueAt(eit->second));
                  }

              for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++) {
              double Etaij;
              ZUM = (eit->second).getLocalVertices();
              Etaij = Eta::valueAt(eit->second);
              double ri;
              double rj;
              double temp;
              
              ri = Radius::valueAt(Triangulation::vertexTable[ZUM->at(0)]);
              rj = Radius::valueAt(Triangulation::vertexTable[ZUM->at(1)]);
              
              temp = (ri * ri) + (rj *  rj) + (2 * ri * rj * Etaij);
              temp = sqrt(temp);
              
              printf("Length at edge %d is: %f\n",eit->first, temp);
              printf("Geoquant length is: %f\n", Length::valueAt(eit->second));
              }

    pause("Done...press enter to exit."); // PAUSE
    

    //flip section
    readTriangulationFile("../../Data/flip_test/eight_triangles_redux.txt");

    //may need to redeclare this depending on it the other sections
    //are modified and it hasn't been declared yet
    //map<int, Edge>::iterator eit;
    eit = Triangulation::edgeTable.begin();

    cout << (eit == Triangulation::edgeTable.end());
    while(eit != Triangulation::edgeTable.end() && (eit->second).isBorder()) {
      eit++;
    }
    Edge e = eit->second;

    writeTriangulationFile("beforeFlip.txt");
    e = flip(e);
    writeTriangulationFile("afterFlip.txt");

    cout << e.getIndex();

    system("PAUSE");
    
    return 0;
}
