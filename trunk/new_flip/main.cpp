#include "new_flip/flip_algorithm.h"

#ifdef __APPLE__
#include <OpenGL/OpenGL.h>
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

//#include "new_flip/triangulation_display.h"

using namespace std;
float _dist = -5.0f;
float _hori = 0.0f;
float _vert = 0.0f;
Edge _e;
TriangulationCoordinateSystem _coord_system;

float _angle = 30.0f;
float _camera_angle = 0.0f;

void handleKeypress(unsigned char key, int x, int y) {
	switch (key) {
	   case 27: //Escape key
			exit(0); //Exit the program
            break;
        case 91: // [
            _dist-=.5;
            break;
        case 93: // ]
            _dist+=.5;
            break;
        case 97: // a
	       _hori -= .5;
	       break;
        case 100: // d
            _hori += .5;
            break;
        case 119: // w
            _vert += .5;
            break;
        case 115: // s
            _vert -= .5;
            break;
        case 32: //space bar
            _e = flip(_e);
            break;
	}
	glutPostRedisplay();
}

void handleSpecialKeypress(int key, int x, int y) {
	switch (key) {
		case GLUT_KEY_LEFT:
		    _hori -= .5;
            break;
		case GLUT_KEY_RIGHT:
            _hori += .5;
            break;
		case GLUT_KEY_UP:
            _vert += .5;
            break;
        case GLUT_KEY_DOWN:
            _vert -= .5;
            break;
	}
    glutPostRedisplay();
}

void initRendering() {
	glEnable(GL_DEPTH_TEST);
}

void handleResize(int w, int h) {
	glViewport(0, 0, w, h);

	glMatrixMode(GL_PROJECTION);

	//Set the camera perspective
	glLoadIdentity();
	gluPerspective(45.0,                  //The camera angle
				   (double)w / (double)h, //The width-to-height ratio
				   1.0,                   //The near z clipping coordinate
				   200.0);                //The far z clipping coordinate
}

//Draws the 3D scene
void drawScene() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glMatrixMode(GL_MODELVIEW); //Switch to the drawing perspective
    glLoadIdentity(); //Reset the drawing perspective
    glTranslatef(_hori, _vert, _dist);

    vector<triangle_parts> tps;

    _coord_system.update();
    tps = _coord_system.getTriangles();
    vector<triangle_parts>::iterator iter;
    iter = tps.begin();

    while (iter != tps.end()) {
        glBegin(GL_LINE_STRIP);
        glBegin(GL_TRIANGLES);
        if (iter->negativity == -1)
            glColor3d(1, 0, 0);
        else
            glColor3d(0, 0, 1);

        glVertex3d(iter->coords[0][0],iter->coords[0][1],0);
        glVertex3d(iter->coords[1][0],iter->coords[1][1],0);
        glVertex3d(iter->coords[2][0],iter->coords[2][1],0);
        glVertex3d(iter->coords[0][0],iter->coords[0][1],0);
        iter++;
        glEnd();
    }



    glutSwapBuffers();
}

int main(int argc, char** argv) {

  //triangulation initializaiton steps
    char *testFile = "test_files/six_triangles_with_geom.txt";
    char outFile[strlen(testFile)+5];
    strcpy(outFile, testFile);
    strcpy(&outFile[strlen(testFile)], ".out");
    //makeTriangulationFile(testFile, outFile);
    bool b = readTriangulationFile(testFile);
    if (!b) {
        cout << "file read failed";
    }
    //prepare some edge lengths
    map<int, Edge>::iterator iter;
    iter = Triangulation::edgeTable.begin();
    while(iter != Triangulation::edgeTable.end()) {
      if(((iter->second).getLocalFaces())->size() == 2 ) {
        struct simps bucket;
        _e = iter->second;
        break;
      }
      iter++;
    }
    _coord_system.generatePlane();

	//Initialize GLUT
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(400, 400); //Set the window size

	//Create the window
	glutCreateWindow("flip algorithm");
	initRendering(); //Initialize rendering

	//Set handler functions for drawing, keypresses, and window resizes
	glutDisplayFunc(drawScene);
	glutKeyboardFunc(handleKeypress);
	glutSpecialFunc(handleSpecialKeypress);
	glutReshapeFunc(handleResize);

	glutMainLoop();
	return 0; //This line is never reached
}

/*
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
*/
