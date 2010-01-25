#include "hinge_flip.h"
#include "TriangulationDisplay.h"
#include "delaunay.h"
#include "FlipAlgorithm.h"
#include "Function.h"
#include "triangulation.h"

#include "miscmath.h"

#include "EtaGenerator.h"
#include "curvature2d.h"

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
float _camera_angle_yaw = 0.0f;
float _camera_angle_pitch = 0.0f;
float gap_factor = 1;
int usingflipalgorithm = 0;
int _lastFlip = 0;

void setup_view_parameters(void);

void printStuff(void) {
  ofstream output("../../Data/flip_test/otherInfo.txt");
  output << "Lengths\n";
  map<int, Edge>::iterator eit;
  eit = Triangulation::edgeTable.begin();
  for (; eit != Triangulation::edgeTable.end(); eit++) {
    output << "Edge " << eit->first << "{" << (*(eit->second).getLocalVertices())[0] << (*(eit->second).getLocalVertices())[1] << "}" << "\t" << Length::valueAt(eit->second) << "\n";
  }

  output << "Radii\n";
  map<int, Vertex>::iterator vit;
  vit = Triangulation::vertexTable.begin();
  for (; vit != Triangulation::vertexTable.end(); vit++) {
    output << "Vertex " << vit->first << "\t" << Radius::valueAt(vit->second) << "\n";
  }
}

void handleKeypress(unsigned char key, int x, int y) {
  map<int, Face>::iterator fit;
  map<int, Edge>::iterator eit;
  vector<int> localVerts;

  bool somethingHappened = true;
  Line l;
	switch (key) {
	   case 27: //Escape key
			exit(0); //Exit the program
            break;
        case 91: // [
            _dist-=1;
            break;
        case 93: // ]
            _dist+=1;
            break;
        case 'd': // d
            cout << "\n\n" << (isWeightedDelaunay(_triDisp->getCurrentEdge()) ? "Edge is weighted Delaunay\n" : "Edge is NOT weighted Delaunay\n");
            cout << "\n" << getDual(_triDisp->getCurrentEdge());
            break;
        case 'e':
          cout << isWeightedDelaunay();
          break;
        case 'p':
          writeTriangulationFile("../../Data/flip_test/beforeDisaster.txt");
          printStuff();
          break;
        case ' ': //space bar

            _triDisp->flipCurrentEdge();
            
            fit = Triangulation::faceTable.begin();
            cout << "\n";
            for (; fit != Triangulation::faceTable.end(); fit++) {
              cout << "face " << fit->first << "\t" << (fit->second).isNegative() << "  ";
              localVerts = *((fit->second).getLocalVertices());
              for (int i = 0; i < localVerts.size(); i++) {
                cout << localVerts[i] << " ";
              }
              cout << "\n";
            }

            eit = Triangulation::edgeTable.begin();
            cout << "\n";
            for (; eit != Triangulation::edgeTable.end(); eit++) {
              cout << "edge " << eit->first << "\t";
              localVerts = *((eit->second).getLocalVertices());
              for (int i = 0; i < localVerts.size(); i++) {
                cout << localVerts[i] << " ";
              }
              cout << "\n";
            }

            //printStuff();


            //printf("\n%lf\n\n", dirichletEnergy(*_vertexF));
            //writeTriangulationFile("../../Data/flip_test/beforeFlip.txt");
            
            //writeTriangulationFile("../../Data/flip_test/afterFlip.txt");
            //printf("\n%lf\n\n", dirichletEnergy(*_vertexF));
            break;
        case 'l': //l
            cout << "geoquant length is " << Length::valueAt(Triangulation::edgeTable[_triDisp->getCurrentEdge().getIndex()]) << "\n";
            l = _triDisp->currentEdgeToLine();
            cout << "distance between vertices is " << sqrt(pow(l.getInitialX() - l.getEndingX(),2) + pow(l.getInitialY() - l.getEndingY(),2)) << "\n\n";
            break;
        case 'o':
          cout << "writing triangulation file to flip_test/out.txt";
          writeTriangulationFile("../../Data/flip_test/out.txt");
          cout << "finished writing triangulation";
          break;
        case 'r': //r toggles circles centered on the vertices, which represent the weights
            _triDisp->showWeights = !(_triDisp->showWeights);
            break;
        //move forward and backward through the list of edges
        case ',': // ,
            _triDisp->previousEdge();
            break;
        case '.': // .
            _triDisp->nextEdge();
            break;
        //grow of shrink the gaps between triangles in the 3d view
        case '\'':
          gap_factor += .1;
          break;
        case ';':
          gap_factor -= .1;
          break;
        case 's'://s    switch veiw modes
            _triDisp->flat = (_triDisp->flat + 1)%3;
            //_triDisp->flat = !(_triDisp->flat);
            if (_triDisp->flat == 1 || _triDisp->flat == 2) {
              glMatrixMode(GL_MODELVIEW);
              glLoadIdentity();
              glTranslatef(0, 0, -10);
            }
            setup_view_parameters();
            break;
        case 'v': //v toggles the dual edges on and off
            _triDisp->voronoi = !_triDisp->voronoi;
            break;
        case 'n': //n runs the flipalgorithm
            _flipAlg->runFlipAlgorithm();
            break;
        case 'm': //m performs a step of the flip algorithm
            _lastFlip = _flipAlg->step();
            _triDisp->setCurrentEdge(_lastFlip);
            break;
        case 't'://t toggles tickmarks in the 2d view
            _triDisp->tickMarks = !_triDisp->tickMarks;
            break;
        case 'b'://b resets the triangulation
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

void matrix_mult(double A[3][3], double in[3], double out[3]) {
  double temp[3];
  for (int j = 0; j < 3; j++) {
    for(int k = 0; k < 3; k++) {
      temp[j] += A[j][k] * in[k];
    }
  }
  for (int i = 0; i < 3; i++) {
    out[i] = temp[i];
  }
}



double unit_y[3] = {0,1,0};
double unit_x[3] = {1,0,0};
double alpha = 5;
double c = cos(alpha);
double s = sin(alpha);
double mc = cos(-alpha);
double ms = sin(-alpha);

void R(double vec[3], double out[3][3], bool plus) {
  double cc, ss;
  if (plus) {
    cc = c;
    ss = s;
  } else {
    cc = mc;
    ss = ms;
  }
  out[0][0] = vec[0] * vec[0] + (1-vec[0]*vec[0])*cc;
  out[0][1] = vec[0] * vec[1] * (1-cc)- vec[2]*ss;
  out[0][2] = vec[0] * vec[2] * (1 - cc) + vec[1] * ss;
  out[1][0] = vec[0] * vec[1] * (1-cc) + vec[2] * ss;
  out[1][1] = vec[1] * vec[1] + (1-vec[1]*vec[1]) * cc;
  out[1][2] = vec[1] * vec[2] * (1-cc) - vec[0]*ss;
  out[2][0] = vec[0] * vec[2] * (1-cc) - vec[1] * ss;
  out[2][1] = vec[1] * vec[2] * (1-cc) + vec[0] * ss;
  out[2][2] = vec[2] * vec[2] + (1-vec[2] * vec[2]) * cc;
}
double M[3][3];
void handleSpecialKeypress(int key, int x, int y) {
	switch (key) {
		case GLUT_KEY_LEFT:
            if (_triDisp->flat == 0) {
                _hori -= .5;
            } else {
                _camera_angle_yaw -= alpha;
            }
            break;
		case GLUT_KEY_RIGHT:
            if (_triDisp->flat == 0) {
                _hori += .5;
            } else {
                _camera_angle_yaw += alpha;
            }
            break;
		case GLUT_KEY_UP:
            if (_triDisp->flat == 0) {
                _vert += .5;
            } else {
                _camera_angle_pitch -= alpha;
            }
            break;
        case GLUT_KEY_DOWN:
            if (_triDisp->flat == 0) {
                _vert -= .5;
            } else {
               _camera_angle_pitch += alpha;
            }
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

void drawSceneFlatAlt() {
    glLoadIdentity();
    glTranslatef(_hori, _vert, _dist);

    map<int, Edge>::iterator eit;
    eit = Triangulation::edgeTable.begin();
    Point p1, p2;
    glBegin(GL_LINES);
    glColor3d(0, 0, 1);
    for (; eit != Triangulation::edgeTable.end(); eit++) {
      p1 = _triDisp->getVertexCoords((*(eit->second).getLocalVertices())[0]);
      p2 = _triDisp->getVertexCoords((*(eit->second).getLocalVertices())[1]);
      glVertex3d(p1.x, p1.y, 0);
      glVertex3d(p2.x, p2.y, 0);
    }
    glEnd();

    //draws the edge that is currently selected
    Edge e;
    e = _triDisp->getCurrentEdge();
    p1 = _triDisp->getVertexCoords((*e.getLocalVertices())[0]);
    p2 = _triDisp->getVertexCoords((*e.getLocalVertices())[1]);
    //draws the selected edge, should appear on top
    glBegin(GL_LINES);
    glColor3d(255, 127, 0);
    glVertex3d(p1.x, p1.y, 0.01);
    glVertex3d(p2.x, p2.y, 0.01);
    glEnd();

}

void drawSceneFlat() {
    glLoadIdentity();
    
    glTranslatef(_hori, _vert, _dist);
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
    if (_triDisp->tickMarks) {
      map<int, Face>::iterator fit = Triangulation::faceTable.begin();
      glColor3d(0, 0, 255);
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

double z_i_calc(double x, double y, double r) {
  //return 0;
  //return x*x+y*y-r*r;
  return x+y-r;
}

//##################################################################################################
void drawSceneStacked() {
    vector<triangle_parts> tps;
    tps = _triDisp->getTriangles();
    vector<triangle_parts>::iterator iter;
    iter = tps.begin();

    int depth = tps.size();

    glLoadIdentity();
    glTranslatef(0, 0, _dist + (depth * 2 * gap_factor));
    glRotatef(_camera_angle_yaw, 0, 1, 0);
    glRotatef(_camera_angle_pitch, 1, 0, 0);
    glTranslatef(0, 0, -(depth * gap_factor)/2.0);
    glTranslatef(_hori, _vert, 0);
    
    //draws the triangulation
    while (iter != tps.end()) {
        glBegin(GL_LINE_STRIP);
        //glBegin(GL_TRIANGLES);
        if (iter->negativity == -1) {
            glColor3d(1, 0, 0);
            glColor3d(1, 0, 0);
        } else {
            glColor3d(0, 0, 1);
        }

        glVertex3d(iter->coords[0][0],iter->coords[0][1], 0);
        glVertex3d(iter->coords[1][0],iter->coords[1][1], 0);
        glVertex3d(iter->coords[2][0],iter->coords[2][1], 0);
        glVertex3d(iter->coords[0][0],iter->coords[0][1], 0);
        iter++;
        glEnd();
        
        glTranslatef(0, 0, (2*gap_factor));
    }
}

//##################################################################################################
/*void drawSceneWithZDepth() {
    vector<triangle_parts> tps;
    tps = _triDisp->getTriangles();
    vector<triangle_parts>::iterator iter;
    iter = tps.begin();

    int depth = tps.size();

    glLoadIdentity();
    glTranslatef(0, 0, _dist);
    glRotatef(_camera_angle_yaw, 0, 1, 0);
    glRotatef(_camera_angle_pitch, 1, 0, 0);
    glTranslatef(0, 0, -(depth * gap_factor)/2.0);
    glTranslatef(_hori, _vert, 0);

    //draws the triangulation
    while (iter != tps.end()) {
        glBegin(GL_LINE_STRIP);
        //glBegin(GL_TRIANGLES);
        if (iter->negativity == -1) {
            glColor3d(1, 0, 0);
            glColor3d(1, 0, 0);
        } else {
            glColor3d(0, 0, 1);
        }

        glVertex3d(iter->coords[0][0],iter->coords[0][1], z_i_calc(iter->coords[0][0], iter->coords[0][1],iter->weight[0]));
        glVertex3d(iter->coords[1][0],iter->coords[1][1], z_i_calc(iter->coords[1][0], iter->coords[1][1],iter->weight[1]));
        glVertex3d(iter->coords[2][0],iter->coords[2][1], z_i_calc(iter->coords[2][0], iter->coords[2][1],iter->weight[2]));
        glVertex3d(iter->coords[0][0],iter->coords[0][1], z_i_calc(iter->coords[0][0], iter->coords[0][1],iter->weight[0]));
        iter++;
        glEnd();

        //glTranslatef(0, 0, (2*gap_factor));
    }
}*/

//Draws the 3D or 2D scene
void drawScene() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glMatrixMode(GL_MODELVIEW); //Switch to the drawing perspective

    //_triDisp->update();
    
    if (_triDisp->flat == 0) {
        drawSceneFlatAlt();
    } else if (_triDisp->flat == 1) {
        drawSceneStacked();
    } else {
        //drawSceneWithZDepth();
    }

    glutSwapBuffers();
}

void setup_view_parameters(void) {
    _camera_angle_yaw = 0.0f;
    _camera_angle_pitch = 0.0f;
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
    if (_triDisp->flat == false && _dist > -20) {
      _dist = -20;
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

    //_triDisp = new TriangulationDisplay(input_file);
    
    //starts here

    //readTriangulationFile(input_file);

    /*double lengths[] = {0,5.09901951359,14.3178210633,13.152946438,8.0622577483,17.2046505341,2.82842712475,5.83095189485,3.16227766017,5.09901951359,3.16227766017,4.472135955,3.60555127546,7.61577310586,6.40312423743,7.0,5.0,8.0,5.83095189485,4.472135955,5.09901951359,11.1803398875,16.1554944214,4.12310562562,7.21110255093,6.32455532034,3.60555127546,7.0,3.0,7.61577310586,8.0622577483,7.28010988928,11.401754251,11.0453610172,15.6524758425,12.0415945788,3.0,6.32455532034,8.94427191,5.83095189485,3.60555127546,5.38516480713,7.21110255093,5.83095189485,3.16227766017,4.0,5.0,6.7082039325,5.0,9.21954445729,5.38516480713,4.472135955,4.472135955,6.40312423743,5.09901951359,3.0,4.24264068712,5.83095189485,2.0,5.09901951359,12.0415945788,12.3693168769,6.7082039325,6.7082039325,4.24264068712,3.60555127546,4.12310562562,6.40312423743,4.12310562562,5.38516480713,6.0827625303,10.0,6.40312423743,4.472135955,2.2360679775,5.0,6.0827625303,4.24264068712,13.152946438,5.65685424949,3.16227766017,5.09901951359,12.3693168769,15.0,19.4164878389,5.65685424949,6.0827625303,20.0997512422,13.416407865,11.313708499,5.09901951359,7.61577310586,1.41421356237,8.24621125124,6.0,7.07106781187,1.41421356237,13.9283882772,8.60232526704,8.24621125124,3.60555127546,2.82842712475,4.12310562562,8.94427191,5.09901951359,5.65685424949,3.16227766017,2.2360679775,3.60555127546,13.6014705087,11.401754251,2.2360679775,2.0,9.21954445729,9.0,9.43398113206,8.94427191,10.6301458127,10.7703296143,5.0,10.295630141,12.5299640861,10.4403065089,12.7279220614,3.60555127546,6.0827625303,8.54400374532,2.2360679775,4.0,3.16227766017,7.07106781187,7.07106781187,6.0827625303,3.60555127546,6.0,5.0,3.16227766017,6.7082039325,1.41421356237,3.0,2.2360679775,2.2360679775,5.09901951359,1.41421356237,2.2360679775,3.16227766017,6.7082039325,5.65685424949,10.0498756211,2.2360679775,8.24621125124,3.60555127546,2.0,9.05538513814,1.41421356237,9.05538513814,2.0,9.05538513814,7.28010988928,6.0827625303,6.0827625303,4.12310562562,4.24264068712,2.2360679775,2.2360679775,2.2360679775,5.0,5.38516480713,2.82842712475,2.82842712475,3.0,2.82842712475,5.38516480713,4.12310562562,2.2360679775,3.60555127546,2.0,2.2360679775,5.0,4.472135955,4.0,7.21110255093,4.472135955,2.0,2.2360679775,4.0,2.2360679775,6.7082039325,4.472135955,4.24264068712,7.21110255093,10.1980390272,5.09901951359,5.83095189485,2.2360679775,5.09901951359,2.82842712475,8.0622577483,6.0827625303,2.82842712475,4.12310562562,3.60555127546,4.12310562562,9.43398113206,6.32455532034,3.60555127546,4.12310562562,4.0,3.60555127546,3.60555127546,3.16227766017,5.83095189485,4.0,2.2360679775,3.0,5.38516480713,4.472135955,7.28010988928,4.472135955,7.81024967591,3.60555127546,5.09901951359,8.0622577483,5.0,2.0,3.60555127546,8.0622577483,6.32455532034,2.2360679775,2.0,2.2360679775,3.0,1.41421356237,4.12310562562,2.2360679775,4.12310562562,3.16227766017,4.24264068712,2.82842712475,6.0827625303,7.07106781187,8.0622577483,3.0,5.0,6.7082039325,9.89949493661,6.40312423743,8.94427191,10.7703296143,11.1803398875,22.803508502,5.09901951359,8.48528137424,5.0,19.6468827044,12.8062484749,6.32455532034,9.05538513814,10.4403065089,17.88854382,7.21110255093,12.0,13.0};

    map<int, Edge>::iterator eit;

    map<int, Vertex>::iterator vit;
    vit = Triangulation::vertexTable.begin();
    for (; vit != Triangulation::vertexTable.end(); vit++) {
      Radius::At(vit->second)->setValue(0.1);
    }

    vector<double>::iterator leng_iter;
    int numEdges = Triangulation::edgeTable.size();

    double lengths[numEdges+1];
    int i = 1;
    for (leng_iter = global_lengths.begin(); leng_iter != global_lengths.end(); leng_iter++) {
      lengths[i] = *leng_iter;
      i++;
    }

    EtaGeneration(lengths);
    vit = Triangulation::vertexTable.begin();
    for (;vit != Triangulation::vertexTable.end(); vit++) {
      cout << vit->first << "\t" << Curvature2D::valueAt(vit->second) << "\t" << Radius::valueAt(vit->second) << "\n";
    }
    
    eit = Triangulation::edgeTable.begin();
    for (; eit != Triangulation::edgeTable.end(); eit++) {
      cout << eit->first << "\t" << Length::valueAt(eit->second) << "\n";
    }

    cout << "end\n";*/
    //system("PAUSE");

    //graphics code starts below here
    _triDisp = new TriangulationDisplay(input_file);
    //_triDisp->reGeneratePlane();

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
//printf("r is %d",Length::valueAt(Triangulation::edgeTable[2]));
	glutMainLoop();
	system("PAUSE"); //This line is never reached
	return 0;
}
