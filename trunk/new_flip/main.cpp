#include "new_flip/hinge_flip.h"
#include "new_flip/TriangulationDisplay.h"
#include "delaunay.h"
#include "FlipAlgorithm.h"

#ifdef __APPLE__
#include <OpenGL/OpenGL.h>
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

using namespace std;
float _dist = -20.0f;
float _hori = -5.0f;
float _vert = -5.0f;
TriangulationDisplay *_triDisp;
FlipAlgorithm *_flipAlg;

float _angle = 30.0f;
float _camera_angle = 0.0f;
int usingflipalgorithm = 0;

void handleKeypress(unsigned char key, int x, int y) {
    bool somethingHappened = true;
    Line l;
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
            _triDisp->flipCurrentEdge();
            break;
        case 104: //h
            //_triDisp->previousEdge();
            break;
        case 108: //l
            cout << "geoquant length is " << Length::valueAt(Triangulation::edgeTable[_triDisp->getCurrentEdge().getIndex()]) << "\n";
            l = _triDisp->currentEdgeToLine();
            cout << "distance between vertices is " << sqrt(pow(l.getInitialX() - l.getEndingX(),2) + pow(l.getInitialY() - l.getEndingY(),2)) << "\n\n";
            break;
        case 114: //p
            _triDisp->showWeights = !(_triDisp->showWeights);
            break;
        case 44: // ,
            _triDisp->previousEdge();
            break;
        case 46: // .
            _triDisp->nextEdge();
            break;
        case 118: //v
            switch (_triDisp->voronoi) {
                case 0:
                    _triDisp->voronoi = 1;
                    break;
                case 1:
                    _triDisp->voronoi = 2;
                    break;
                case 2:
                    _triDisp->voronoi = 0;
                    break;
            }
            break;
        case 110: //n
            _flipAlg->step();
            _triDisp->setCurrentEdge(_flipAlg->currentEdgeIndex());
            break;
        default:
            somethingHappened = false;
            break;

	}
    if (somethingHappened) {
        glutPostRedisplay();
    }
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

    _triDisp->update();
    tps = _triDisp->getTriangles();
    vector<triangle_parts>::iterator iter;
    iter = tps.begin();

    //draws the triangulation
    while (iter != tps.end()) {
        glBegin(GL_LINE_STRIP);
        //glBegin(GL_TRIANGLES);
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
    
    //draw the duals
    if (_triDisp->voronoi == 1) {
        vector<Line> ds;
        ds = _triDisp->getDuals();
        for(int i = 0; i < ds.size(); i++) {
            glBegin(GL_LINE_STRIP);
            glColor3d(0, 255, 0);
            glVertex3d(ds[i].getInitialX(),ds[i].getInitialY(),0);
            glVertex3d(ds[i].getEndingX(),ds[i].getEndingY(),0);
            iter++;
            glEnd();
        }
    } else if (_triDisp->voronoi == 2) {

    }
    
    
    //draws the weights around the vertices
    if (_triDisp->showWeights) {
        map<int,Vertex>::iterator vit;
        for (vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
            Point p;
            p = _triDisp->getPoint(vit->first);
            double radius = Radius::valueAt(vit->second);
            glBegin(GL_LINE_STRIP);
            glColor3d(255,250,250);
            for (double i = 0; i <= 2*PI; i+=0.0174532925) {
                glVertex3d(p.x + cos(i) * radius, p.y + sin(i) * radius, 0);
            }
            glEnd();
        }
    }
    
    
    
    //draws the edge that is currently selected
    Line l;
    l = _triDisp->currentEdgeToLine();
    //draws the selected edge, should appear on top
    glBegin(GL_LINE_STRIP);
    glColor3d(255, 127, 0);
    glVertex3d(l.getInitialX(), l.getInitialY(), 0.01);
    glVertex3d(l.getEndingX(), l.getEndingY(), 0.01);
    glEnd();
    
    Edge e = _triDisp->getCurrentEdge();

    glutSwapBuffers();
}

void setup_view_parameters(void) {
    double count = 0;
    double sumx = 0;
    double sumy = 0;
    //average all the coordinates to figure out where the view should be centered
    map<int,Vertex>::iterator vit;
    vit = Triangulation::vertexTable.begin();
    Point p;
    p = _triDisp->getPoint(vit->first);
    double minx = p.x;
    double maxx = p.x;
    double miny = p.y;
    double maxy = p.y;
    for (; vit != Triangulation::vertexTable.end(); vit++) {
        p = _triDisp->getPoint(vit->first);
        sumx += p.x;
        sumy += p.y;
        count++;
        if (p.x < minx) {minx = p.x;}
        if (p.x > maxx) {maxx = p.x;}
        if (p.y < miny) {miny = p.y;}
        if (p.y > maxy) {maxy = p.y;}
    }
    double centerx = sumx / count;
    double centery = sumy / count;
    _hori = 0 - centerx;
    _vert = 0 - centery;
    
    //the screen will be shifted in the positive z direction by _dist
    if (maxx - minx > maxy - miny) {
        _dist = 0 - abs(maxx - minx) - 2;
    } else {
        _dist = 0 - abs(maxy - miny) - 2;
    }
}

/*
 *  Main takes the name of a file to read from as its only argument
 *
 *  to give it an argument in dev, go to Execute > Parameters...
 *  then in the top field labeled "Parameters to pass to your program"
 *  type the file name containing the triangulation data you want used
 */
int main(int argc, char** argv) {

    //cout << "starting!\n";
    //makeTriangulationFile("", "");
    //cout << "done!\n";
    //system("PAUSE");
    //exit(0);

    //triangulation initializaiton steps
    char *testFile;
    if (argc <= 1) {
        cout << "ATTENTION: YOU NEED TO SPECIFY A FILE TO BE READ BY GIVING MAIN AN ARGUMENT!!!\n"
             << "READ THE COMMENT ABOVE MAIN's DEFINITION\n\nif you press any key to continue now,"
             << " the file being used will default to test_files/convex_pair.txt\n\n";
            system("PAUSE");
            testFile = "test_files/convex_pair.txt";
    } else {
        testFile = argv[1];
    }
    char outFile[strlen(testFile)+5];
    strcpy(outFile, testFile);
    strcpy(&outFile[strlen(testFile)], ".out");

    _triDisp = new TriangulationDisplay (testFile);
    _flipAlg = new FlipAlgorithm ();
    setup_view_parameters();

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
	system("PAUSE"); //This line is never reached
	return 0;

}
