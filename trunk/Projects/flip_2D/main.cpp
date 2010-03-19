#include "hinge_flip.h"
#include "TriangulationDisplay.h"
#include "delaunay.h"
#include "FlipAlgorithm.h"
#include "triangulation.h"
#include "Matrix.h"

#include "miscmath.h"


using namespace std;

void printStuff(ostream *output) {
  //ofstream output("../../Data/flip_test/otherInfo.txt");
  *output << "Lengths\n";
  map<int, Edge>::iterator eit;
  eit = Triangulation::edgeTable.begin();
  for (; eit != Triangulation::edgeTable.end(); eit++) {
    *output << "Edge " << eit->first << "{" << (*(eit->second).getLocalVertices())[0] << (*(eit->second).getLocalVertices())[1] << "}" << "\t" << Length::valueAt(eit->second) << "\n";
  }

  *output << "Radii\n";
  map<int, Vertex>::iterator vit;
  vit = Triangulation::vertexTable.begin();
  for (; vit != Triangulation::vertexTable.end(); vit++) {
    *output << "Vertex " << vit->first << "\t" << Radius::valueAt(vit->second) << "\n";
  }

  *output << "Triangles\n";
  map<int, Face>::iterator fit;
  fit = Triangulation::faceTable.begin();
  vector<int> * indices;
  for (; fit != Triangulation::faceTable.end(); fit++) {
    *output << "Face " << fit->first << "\n";
    indices = (fit->second).getLocalVertices();
    for (int i = 0; i < indices->size(); i++) {
      *output << (*indices)[i] << " ";
    }
    *output << "\n";
    indices = (fit->second).getLocalEdges();
    for (int i = 0; i < indices->size(); i++) {
      *output << (*indices)[i] << " ";
    }
    *output << "\n";
    indices = (fit->second).getLocalFaces();
    for (int i = 0; i < indices->size(); i++) {
      *output << (*indices)[i] << " ";
    }
    *output << "\n";
  }
}

/*
 *  main takes the name of a file to read from as its only argument
 *
 *  to give it an argument in dev, go to Execute > Parameters...
 *  then in the top field labeled "Parameters to pass to your program"
 *  type the file name containing the triangulation data you want used
 */
int main(int argc, char** argv) {
    //FlipAlgorithm* _flipAlg;
    //Function* _vertexF;
    char* input_file;
    //int usingflipalgorithm = 0;

    //cout << "starting!\n";
    //makeTriangulationFile("test_files/toms_triangulation.lutz", "test_files/toms_triangulation.txt");
    //cout << "done!\n";
    //system("PAUSE");
    //exit(0);


        //triangulation initializaiton steps

        if (argc <= 1) {
            cout << "\nATTENTION: YOU NEED TO SPECIFY A FILE TO BE READ BY GIVING MAIN AN ARGUMENT!!!\n"
             << "READ THE COMMENT ABOVE MAIN's DEFINITION\n\nif you press any key to continue now,"
             << " the file being used will default to test_files/convex_pair.txt\n\n";
            system("PAUSE");
            input_file = "../../Data/flip_test/non_convex_pair.txt";

    } else {
        input_file = argv[1];
    }
    char out_file[strlen(input_file)+5];
    strcpy(out_file, input_file);
    strcpy(&out_file[strlen(input_file)], ".out");

    TriangulationDisplay::ShowTriangulation(input_file, argc, argv);
    return 0;
}
