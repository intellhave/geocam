#include "hinge_flip.h"
#include "TriangulationDisplay.h"
#include "delaunay.h"
#include "FlipAlgorithm.h"
#include "Function.h"
#include "triangulation.h"

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
TriangulationDisplay* _triDisp;
FlipAlgorithm* _flipAlg;
Function* _vertexF;
char* input_file;

float _angle = 30.0f;
float _camera_angle = 0.0f;
int usingflipalgorithm = 0;
int _lastFlip = 0;

void setup_view_parameters(void);

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
        case 100: // d
            cout << (isWeightedDelaunay(_triDisp->getCurrentEdge()) ? "Edge is weighted Delaunay\n" : "Edge is NOT weighted Delaunay\n");
            break;
        case ' ': //space bar
            printf("\n%lf\n\n", dirichletEnergy(*_vertexF));
            _triDisp->flipCurrentEdge();
            printf("\n%lf\n\n", dirichletEnergy(*_vertexF));
            break;
        case 'h': //h
            //_triDisp->previousEdge();
            break;
        case 'l': //l
            cout << "geoquant length is " << Length::valueAt(Triangulation::edgeTable[_triDisp->getCurrentEdge().getIndex()]) << "\n";
            l = _triDisp->currentEdgeToLine();
            cout << "distance between vertices is " << sqrt(pow(l.getInitialX() - l.getEndingX(),2) + pow(l.getInitialY() - l.getEndingY(),2)) << "\n\n";
            break;
        case 'p': //p
            _triDisp->showWeights = !(_triDisp->showWeights);
            break;
        case ',': // ,
            _triDisp->previousEdge();
            break;
        case '.': // .
            _triDisp->nextEdge();
            break;
        case 's':
            _triDisp->flat = !(_triDisp->flat);
            break;
        case 'v': //v
            switch (_triDisp->voronoi) {
                case 0:
                    _triDisp->voronoi = 1;
                    break;
                case 1:
                    _triDisp->voronoi = 0;
                    break;
                //case 2:
                //    _triDisp->voronoi = 0;
                //    break;
            }
            break;
        case 'n': //n
            _flipAlg->runFlipAlgorithm();
            break;
        case 'm': //m
            _lastFlip = _flipAlg->step();
            _triDisp->setCurrentEdge(_lastFlip);
            break;
        case 't':
            cout << isWeightedDelaunay();
            break;
        case 'b': // b
            Triangulation::vertexTable.clear();
            Triangulation::edgeTable.clear();
            Triangulation::faceTable.clear();
            delete _triDisp;
            delete _flipAlg;
            delete _vertexF;
            _triDisp = new TriangulationDisplay (input_file);
            _flipAlg = new FlipAlgorithm ();
            _vertexF = new Function("someNumbers.txt", "vertex", Triangulation::vertexTable, true);
            setup_view_parameters();
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

//draws ticks along the edge petween p1 and p2 in the direction of p3
void drawTicks(double x1, double y1, double x2, double y2, double x3, double y3, int depth) {
    if (0 == depth) {
        return;
    }
    double avx, avy;
    avx = (x1 + x2) / 2.0;
    avy = (y1 + y2) / 2.0;
    double vecx, vecy;
    vecx = (x3 - avx)/10.0;
    vecy = (y3 - avy)/10.0;
    glBegin(GL_LINES);
        glVertex3d(avx, avy, 0);
        glVertex3d(avx + vecx, avy + vecy, 0);
    glEnd();
    drawTicks(x1, y1, avx, avy, x3, y3, depth-1);
    drawTicks(avx, avy, x2, y2, x3, y3, depth-1);
}

void drawSceneFlat() {
    vector<triangle_parts> tps;
    tps = _triDisp->getTriangles();
    vector<triangle_parts>::iterator iter;
    iter = tps.begin();

    //draws the triangulation
    while (iter != tps.end()) {
        glBegin(GL_LINE_STRIP);
        //glBegin(GL_TRIANGLES);
        if (iter->negativity == -1) {
            //glColor3d(1, 0, 0);
            glColor3d(0, 0, 1);
        } else {
            glColor3d(0, 0, 1);
        }

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
        glBegin(GL_LINES);
        glColor3d(0, 255, 0);
        for(int i = 0; i < ds.size(); i++) {
            glVertex3d(ds[i].getInitialX(),ds[i].getInitialY(),0);
            glVertex3d(ds[i].getEndingX(),ds[i].getEndingY(),0);
            iter++;
        }
        glEnd();
    }//TODO: need to figure out a good way to display only the duals of the current hinge
    /*else if (_triDisp->voronoi == 2) {
        Edge e = _triDisp->getCurrentEdge();
        int numFaces = (*(e.getLocalFaces())).size();
        int f0 = (*(e.getLocalFaces()))[0];
        vector<int> edges;
        vector<int>::iterator eit;
        for (eit = (*(Triangulation::faceTable[f0].getLocalEdges())).begin(); eit != (*(Triangulation::faceTable[f0].getLocalEdges())).end(); eit++) {
            edges.push_back(*eit);
        }
        if (numFaces >= 2) {
            int f1 = (*(e.getLocalFaces()))[1];
            for (eit = (*(Triangulation::faceTable[f1].getLocalEdges())).begin(); eit != (*(Triangulation::faceTable[f1].getLocalEdges())).end(); eit++) {
                edges.push_back(*eit);
            }
        }
        Line l;
        glBegin(GL_LINES);
        glColor3d(0, 255, 0);
        for (eit = edges.begin(); eit != edges.end(); eit++) {
            l = _triDisp->getDualAtEdge(*eit);
            glVertex3d(l.getInitialX(),l.getInitialY(),0);
            glVertex3d(l.getEndingX(),l.getEndingY(),0);
        }
        glEnd();
    }*/

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

    //draw little tick marks along the inside of each triangle
    map<int, Face>::iterator fit = Triangulation::faceTable.begin();
    for (; fit != Triangulation::faceTable.end(); fit++) {
        vector<int>* verts;
        verts = (fit->second).getLocalVertices();
        Point p1, p2, p3;
        p1 = _triDisp->getPoint((*verts)[0]);
        p2 = _triDisp->getPoint((*verts)[1]);
        p3 = _triDisp->getPoint((*verts)[2]);

        drawTicks(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, 3);
        drawTicks(p3.x, p3.y, p1.x, p1.y, p2.x, p2.y, 3);
        drawTicks(p2.x, p2.y, p3.x, p3.y, p1.x, p1.y, 3);
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
}

void drawSceneStacked() {

}

//Draws the 3D scene
void drawScene() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glMatrixMode(GL_MODELVIEW); //Switch to the drawing perspective
    glLoadIdentity(); //Reset the drawing perspective
    glTranslatef(_hori, _vert, _dist);

    _triDisp->update();
    
    if (_triDisp->flat) {
        drawSceneFlat();
    } else {
        drawSceneStacked();
    }
    

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
        _dist = 0 - 2 * abs(maxx - minx);
    } else {
        _dist = 0 - 2 * abs(maxy - miny);
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
    //makeTriangulationFile("test_files/toms_triangulation.lutz", "test_files/toms_triangulation.txt");
    //cout << "done!\n";
    //system("PAUSE");
    //exit(0);

   /* readTriangulationFile("test_files/non_convex_pair.txt");

    map<int, Vertex>::iterator sit;
    sit = Triangulation::vertexTable.begin();
    vector<int> v;
    for (; sit != Triangulation::vertexTable.end(); sit++) {
        v.push_back(sit->first);
        cout << sit->first << "\n";
    }

    Functional* f;
    f = new Functional("someNumbers.txt", "vertex", v);
    cout << "here\n";

    for (sit = Triangulation::vertexTable.begin(); sit != Triangulation::vertexTable.end(); sit++) {
        cout << sit->first << "    " << f->valueOf(sit->first) << "\n";
    }*/


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

    _triDisp = new TriangulationDisplay(input_file);
    _flipAlg = new FlipAlgorithm();
    _vertexF = new Function("someNumbers.txt", "vertex", Triangulation::vertexTable, true);
    setup_view_parameters();

	//Initialize GLUT

	glutInit(&argc, argv);
	
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(600, 600); //Set the window size

	//Create the window
	glutCreateWindow("flip algorithm");
	initRendering(); //Initialize rendering

	//Set handler functions for drawing, keypresses, and window resizes
	glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glutDisplayFunc(drawScene);
	glutKeyboardFunc(handleKeypress);
	glutSpecialFunc(handleSpecialKeypress);
	glutReshapeFunc(handleResize);
printf("r is %d",Length::valueAt(Triangulation::edgeTable[2]));
	glutMainLoop();
	system("PAUSE"); //This line is never reached
	return 0;
}
