#include "triangulation/triangulation.h"
#include <windows.h>
#include <gl/gl.h>
void drawPolygon(int size, double* curv);
void reshape(int w, int h);
void setFont(HFONT* hFont, char* font, int ptSize);
void drawEdge(Edge edge);
void drawVertex(Vertex vertex);
void drawText(float xPos, float yPos, int txtSize, char *text, GLuint listbase);
