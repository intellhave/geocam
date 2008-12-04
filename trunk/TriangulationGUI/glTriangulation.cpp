#include "glTriangulation.h"
#include <gl/gl.h>
#include <cmath>
#define PI 3.141592653589793238
void drawPolygon(int size, double* curv)
{
   glBegin(GL_LINE_LOOP);
   glColor3f( 1.0f, 0.0f, 0.0f );
   for(int i = 0; i < 25; i++)
   {
       double angle = 2 * PI / 25 * i;
       double radii = .3 * (PI / 2);
       glVertex2f(radii * cos(angle), radii * sin(angle));
   }
   glEnd();
   glBegin(GL_LINE_LOOP);
   glColor3f( 1.0f, 0.0f, 0.0f );
   for(int i = 0; i < 25; i++)
   {
       double angle = 2 * PI / 25 * i;
       double radii = .3 * (PI);
       glVertex2f(radii * cos(angle), radii * sin(angle));
   }
   glEnd();
   glBegin(GL_LINE_LOOP);
   glColor3f( 0.0f, 0.0f, 0.0f ); 
   for(int i = 0; i < size; i++)
   {
       double angle = 2 * PI / size * i;
       double radii = .3 * (atan(curv[i])+ PI / 2);
                        
       glVertex2f(radii * cos(angle), radii * sin(angle));
       // The above computation performs an arctan scaling of
       // the curvatures so that the curvature data is always
       // outputted in a disk of radius Pi.  The circle of 
       // radius Pi coreesponds to positive infinity, the 
       // origin corresponds to negative infinity.
   }
   glEnd();
   glFlush(); 
}
void reshape(int w, int h)
{
     glViewport(0, 0, (GLsizei) w, (GLsizei)h);
     glMatrixMode(GL_PROJECTION);
     glLoadIdentity();
     glOrtho(0.0, (GLdouble) w, 0.0, (GLdouble) h, 0.0, 0.0);
}
void setFont(HFONT* hFont, char* font, int ptSize)
{
    *hFont = CreateFont(-ptSize, // Size of text
                  0,
                  0,
                  0,
                  FW_NORMAL, // Style of text, such as bold, etc.
                  FALSE, // Don't Change
                  FALSE, // Don't Change    
                  FALSE, // Don't Change
                  ANSI_CHARSET, // Don't Change
                  OUT_TT_PRECIS, // Don't Change
                  CLIP_DEFAULT_PRECIS, // Don't Change
                  ANTIALIASED_QUALITY, // Don't Change
                  FF_DONTCARE|DEFAULT_PITCH, // Don't change
                  font); // Name of font, e.g. "Arial"
}
void drawEdge(Edge edge)
{
    vector<int> localV = *(edge.getLocalVertices());
    int size = localV.size();
    double edgeL = edge.getLength();
    double radii1 = Triangulation::vertexTable[localV[0]].getRadius();
    double radii2 = Triangulation::vertexTable[localV[1]].getRadius();
                             
    glBegin(GL_LINES);
    glColor3f( 0.0f, 0.0f, 0.0f );
      glVertex2f(0.9f, 0.0f); glVertex2f(-0.9, 0.0);
    glEnd();
             
    radii1 = radii1 * 1.8 / edgeL;
    radii2 = radii2 * 1.8 / edgeL;
    glBegin(GL_LINE_LOOP);
    glColor3f(0.0f, 1.0f, 0.0f);
    for(int i = 0; i < 25; i++) {
        float angle = (float) 2 * PI / 25 * i;
        glVertex2f(radii1*cos(angle) + 0.9, radii1*sin(angle));
    }
    glEnd();
    glBegin(GL_LINE_LOOP);
    glColor3f(0.0f, 1.0f, 0.0f);
    for(int i = 0; i < 25; i++) {
        float angle = (float) 2 * PI / 25 * i;
        glVertex2f(radii2*cos(angle) - 0.9, radii2*sin(angle));
    }
    glEnd();
    glFlush();
}
void drawVertex(Vertex vertex)
{
     vector<int> localE = *(vertex.getLocalEdges());
     int size = localE.size();
     glBegin(GL_LINES);
     glColor3f( 0.0f, 0.0f, 0.0f );
     for(int i = 0; i < size; i++) {
         double angle = 2 * PI / size * i;
         glVertex2f(0.0f, 0.0f); glVertex2f(.9*cos(angle), .9*sin(angle));
     }
     glEnd();
     glFlush();
}
void drawText(float xPos, float yPos, int txtSize, char* text, GLuint listbase)
{
     glDisable(GL_TEXTURE_2D);
     glRasterPos2f(xPos, yPos);
     glPushAttrib(GL_LIST_BIT);
       glListBase(listbase); //32 offset backwards since we offset it forwards
       glCallLists(txtSize, GL_UNSIGNED_BYTE, text);
     glPopAttrib();
     glEnable(GL_TEXTURE_2D);
}
