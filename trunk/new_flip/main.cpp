#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include "triangulation/triangulationInputOutput.h"
#include "triangulation/triangulationmorph.h"
#include "new_flip/flip_algorithm.h"

#include "new_flip/flip_algorithm.h"

#define PI 	3.141592653589793238

using namespace std;

//several tests for the different cases
//tester unfortunately has to visually inspect the output to confirm
//that the functions are working, need to think of an easy way to automate this
void testTopoFlip();
void testPP2PP();
void testPP2PN();
void testPN2PN();
void testPN2PP();
void testPN2NN();
void testPN2NN();
void testNN2PN();
void testNN2NN();

int main(int argc, char *argv[]) {
    char *testFile = "test_files/pair_for_geo_tests.txt";
    char outFile[strlen(testFile)+5];
    strcpy(outFile, testFile);
    strcpy(&outFile[strlen(testFile)], ".out");
    //makeTriangulationFile(testFile, outFile);
    readTriangulationFile(testFile);
    //call test here
    testNN2PN();
    writeTriangulationFile(outFile);

    map<int, Edge>::iterator iter;
    Edge e;
    iter = Triangulation::edgeTable.begin();
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        e = iter->second;
      }
      iter++;
    }

    cout << "\n\n\n";
    
    while(iter != Triangulation::edgeTable.end()){
      printf("edge %d ended up with length %lf\n", (iter->second).getIndex(), (iter->second).getLength());
      iter++;
    }
    vector<int> *fs = Triangulation::edgeTable[e.getIndex()].getLocalFaces();
    vector<int> *es = Triangulation::faceTable[(*fs).at(0)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(0)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";

    es = Triangulation::faceTable[(*fs).at(1)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(1)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";



    system("PAUSE");
    return 0;
}

void testTopoFlip() {
     map<int, Edge>::iterator iter;
     iter = Triangulation::edgeTable.begin();
    
     Edge e = iter->second;
     flip(e);
}

void testPP2PP() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    double len = 5;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        e = iter->second;
        printf("common edge found!\n");
      }
      printf("edge %d started with length %lf\n", (iter->second).getIndex(), len);
      (iter->second).setLength(len);
      len++;
      iter++;
    }
    flip(e);
}

void testPP2PN() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        e = iter->second;
        prep_for_flip(iter->second, &bucket);
        Triangulation::edgeTable[bucket.e0].setLength(2.0);
        Triangulation::edgeTable[bucket.e1].setLength(4.472);
        Triangulation::edgeTable[bucket.e2].setLength(2.828);
        Triangulation::edgeTable[bucket.e3].setLength(2.828);
        Triangulation::edgeTable[bucket.e4].setLength(4.472);
        break;
      }
      iter++;
    }
    flip(e);
}

void testPN2PN() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        e = iter->second;
        prep_for_flip(iter->second, &bucket);
        Triangulation::edgeTable[bucket.e0].setLength(3.0);
        Triangulation::edgeTable[bucket.e1].setLength(3.162);
        Triangulation::edgeTable[bucket.e2].setLength(5.0);
        Triangulation::edgeTable[bucket.e3].setLength(3.163);
        Triangulation::edgeTable[bucket.e4].setLength(5.1);
            vector<int> *fs = Triangulation::edgeTable[e.getIndex()].getLocalFaces();
            Triangulation::faceTable[(*fs).at(0)].setNegativity(true);
    vector<int> *es = Triangulation::faceTable[(*fs).at(0)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(0)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";

    es = Triangulation::faceTable[(*fs).at(1)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(1)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";
        break;
      }
      iter++;
    }
    flip(e);
}

void testPN2PP() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        e = iter->second;
        prep_for_flip(iter->second, &bucket);
        Triangulation::edgeTable[bucket.e0].setLength(3.998);
        Triangulation::edgeTable[bucket.e1].setLength(2.828);
        Triangulation::edgeTable[bucket.e2].setLength(2.828);
        Triangulation::edgeTable[bucket.e3].setLength(4.472);
        Triangulation::edgeTable[bucket.e4].setLength(4.472);
            vector<int> *fs = Triangulation::edgeTable[e.getIndex()].getLocalFaces();
            Triangulation::faceTable[(*fs).at(0)].setNegativity(true);
    vector<int> *es = Triangulation::faceTable[(*fs).at(0)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(0)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";

    es = Triangulation::faceTable[(*fs).at(1)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(1)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";
        break;
      }
      iter++;
    }
    flip(e);
}

void testPN2NN() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        e = iter->second;
        prep_for_flip(iter->second, &bucket);
        Triangulation::edgeTable[bucket.e0].setLength(3.998);
        Triangulation::edgeTable[bucket.e1].setLength(2.828);
        Triangulation::edgeTable[bucket.e2].setLength(2.828);
        Triangulation::edgeTable[bucket.e3].setLength(4.472);
        Triangulation::edgeTable[bucket.e4].setLength(4.472);
    vector<int> *fs = Triangulation::edgeTable[e.getIndex()].getLocalFaces();
    //only line different between the PN2PP and PN2NN tests
    Triangulation::faceTable[(*fs).at(1)].setNegativity(true);
    vector<int> *es = Triangulation::faceTable[(*fs).at(0)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(0)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";

    es = Triangulation::faceTable[(*fs).at(1)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(1)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";
        break;
      }
      iter++;
    }
    flip(e);
}

void testNN2NN() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        e = iter->second;
        prep_for_flip(iter->second, &bucket);
        Triangulation::edgeTable[bucket.e0].setLength(2.0);
        Triangulation::edgeTable[bucket.e1].setLength(3.0);
        Triangulation::edgeTable[bucket.e2].setLength(3.0);
        Triangulation::edgeTable[bucket.e3].setLength(3.0);
        Triangulation::edgeTable[bucket.e4].setLength(3.0);
    vector<int> *fs = Triangulation::edgeTable[e.getIndex()].getLocalFaces();
    //only line different between the PN2PP and PN2NN tests
    Triangulation::faceTable[(*fs).at(0)].setNegativity(true);
    Triangulation::faceTable[(*fs).at(1)].setNegativity(true);
    vector<int> *es = Triangulation::faceTable[(*fs).at(0)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(0)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";

    es = Triangulation::faceTable[(*fs).at(1)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(1)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";
        break;
      }
      iter++;
    }
    flip(e);
}

void testNN2PN() {
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    Edge e;
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        e = iter->second;
        prep_for_flip(iter->second, &bucket);
        Triangulation::edgeTable[bucket.e0].setLength(2.0);
        Triangulation::edgeTable[bucket.e1].setLength(4.472);
        Triangulation::edgeTable[bucket.e2].setLength(2.828);
        Triangulation::edgeTable[bucket.e3].setLength(2.828);
        Triangulation::edgeTable[bucket.e4].setLength(4.472);
    vector<int> *fs = Triangulation::edgeTable[e.getIndex()].getLocalFaces();
    //only line different between the PN2PP and PN2NN tests
    Triangulation::faceTable[(*fs).at(0)].setNegativity(true);
    Triangulation::faceTable[(*fs).at(1)].setNegativity(true);
    vector<int> *es = Triangulation::faceTable[(*fs).at(0)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(0)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";

    es = Triangulation::faceTable[(*fs).at(1)].getLocalEdges();
    cout << "the face with negativity set to " << Triangulation::faceTable[(*fs).at(1)].isNegative();
    cout << " has sides with lengths \n" << Triangulation::edgeTable[(*es).at(0)].getLength();
    cout << "\n" << Triangulation::edgeTable[(*es).at(1)].getLength() << "\n" << Triangulation::edgeTable[(*es).at(2)].getLength();
    cout << "\n\n";
        break;
      }
      iter++;
    }
    flip(e);
}
