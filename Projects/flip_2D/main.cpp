#include "hinge_flip.h"
#include "TriangulationDisplay.h"
#include "delaunay.h"
#include "FlipAlgorithm.h"
#include "Function.h"
#include "triangulation.h"
#include "Matrix.h"

#include "miscmath.h"


using namespace std;

//TriangulationDisplay* _triDisp;
FlipAlgorithm* _flipAlg;
Function* _vertexF;
char* input_file;

int usingflipalgorithm = 0;


//void setup_view_parameters(void);

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

  //cout << "starting!\n";
  //makeTriangulationFile("test_files/toms_triangulation.lutz", "test_files/toms_triangulation.txt");
  //cout << "done!\n";
  //system("PAUSE");
  //exit(0);

  //triangulation initializaiton steps

  if (argc <= 1) {
        cout << "ATTENTION: YOU NEED TO SPECIFY A FILE TO BE READ BY GIVING MAIN AN ARGUMENT!!!\n"
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

  // big triangulation's lengths
  //double lengths[] = {0,5.09901951359,14.3178210633,13.152946438,8.0622577483,17.2046505341,2.82842712475,5.83095189485,3.16227766017,5.09901951359,3.16227766017,4.472135955,3.60555127546,7.61577310586,6.40312423743,7.0,5.0,8.0,5.83095189485,4.472135955,5.09901951359,11.1803398875,16.1554944214,4.12310562562,7.21110255093,6.32455532034,3.60555127546,7.0,3.0,7.61577310586,8.0622577483,7.28010988928,11.401754251,11.0453610172,15.6524758425,12.0415945788,3.0,6.32455532034,8.94427191,5.83095189485,3.60555127546,5.38516480713,7.21110255093,5.83095189485,3.16227766017,4.0,5.0,6.7082039325,5.0,9.21954445729,5.38516480713,4.472135955,4.472135955,6.40312423743,5.09901951359,3.0,4.24264068712,5.83095189485,2.0,5.09901951359,12.0415945788,12.3693168769,6.7082039325,6.7082039325,4.24264068712,3.60555127546,4.12310562562,6.40312423743,4.12310562562,5.38516480713,6.0827625303,10.0,6.40312423743,4.472135955,2.2360679775,5.0,6.0827625303,4.24264068712,13.152946438,5.65685424949,3.16227766017,5.09901951359,12.3693168769,15.0,19.4164878389,5.65685424949,6.0827625303,20.0997512422,13.416407865,11.313708499,5.09901951359,7.61577310586,1.41421356237,8.24621125124,6.0,7.07106781187,1.41421356237,13.9283882772,8.60232526704,8.24621125124,3.60555127546,2.82842712475,4.12310562562,8.94427191,5.09901951359,5.65685424949,3.16227766017,2.2360679775,3.60555127546,13.6014705087,11.401754251,2.2360679775,2.0,9.21954445729,9.0,9.43398113206,8.94427191,10.6301458127,10.7703296143,5.0,10.295630141,12.5299640861,10.4403065089,12.7279220614,3.60555127546,6.0827625303,8.54400374532,2.2360679775,4.0,3.16227766017,7.07106781187,7.07106781187,6.0827625303,3.60555127546,6.0,5.0,3.16227766017,6.7082039325,1.41421356237,3.0,2.2360679775,2.2360679775,5.09901951359,1.41421356237,2.2360679775,3.16227766017,6.7082039325,5.65685424949,10.0498756211,2.2360679775,8.24621125124,3.60555127546,2.0,9.05538513814,1.41421356237,9.05538513814,2.0,9.05538513814,7.28010988928,6.0827625303,6.0827625303,4.12310562562,4.24264068712,2.2360679775,2.2360679775,2.2360679775,5.0,5.38516480713,2.82842712475,2.82842712475,3.0,2.82842712475,5.38516480713,4.12310562562,2.2360679775,3.60555127546,2.0,2.2360679775,5.0,4.472135955,4.0,7.21110255093,4.472135955,2.0,2.2360679775,4.0,2.2360679775,6.7082039325,4.472135955,4.24264068712,7.21110255093,10.1980390272,5.09901951359,5.83095189485,2.2360679775,5.09901951359,2.82842712475,8.0622577483,6.0827625303,2.82842712475,4.12310562562,3.60555127546,4.12310562562,9.43398113206,6.32455532034,3.60555127546,4.12310562562,4.0,3.60555127546,3.60555127546,3.16227766017,5.83095189485,4.0,2.2360679775,3.0,5.38516480713,4.472135955,7.28010988928,4.472135955,7.81024967591,3.60555127546,5.09901951359,8.0622577483,5.0,2.0,3.60555127546,8.0622577483,6.32455532034,2.2360679775,2.0,2.2360679775,3.0,1.41421356237,4.12310562562,2.2360679775,4.12310562562,3.16227766017,4.24264068712,2.82842712475,6.0827625303,7.07106781187,8.0622577483,3.0,5.0,6.7082039325,9.89949493661,6.40312423743,8.94427191,10.7703296143,11.1803398875,22.803508502,5.09901951359,8.48528137424,5.0,19.6468827044,12.8062484749,6.32455532034,9.05538513814,10.4403065089,17.88854382,7.21110255093,12.0,13.0};

  //graphics code starts below here
  //_triDisp = new TriangulationDisplay(input_file, argc, argv);

  //_flipAlg = new FlipAlgorithm();
  //_vertexF = new Function("someNumbers.txt", "vertex", Triangulation::vertexTable, true);


  TriangulationDisplay::ShowTriangulation(input_file, argc, argv);
	return 0;
}
