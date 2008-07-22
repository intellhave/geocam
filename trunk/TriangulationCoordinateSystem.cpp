#include "TriangulationCoordinateSystem.h"
#include "miscmath.h"
#include <fstream>
#include <iostream>
#include <iomanip>

TriangulationCoordinateSystem::TriangulationCoordinateSystem()
{
}
TriangulationCoordinateSystem::~TriangulationCoordinateSystem()
{
}
void TriangulationCoordinateSystem::putLine(int index, Line l)
{
    lineTable.insert(pair<int, Line>(index, l));
}
void TriangulationCoordinateSystem::putPoint(int index, Point p)
{
     pointTable.insert(pair<int, Point>(index, p));
}
bool TriangulationCoordinateSystem::containsLine(int index)
{
     map<int, Line>::iterator lit;
     for(lit = lineTable.begin(); lit != lineTable.end(); lit++)
     {
         if(index == lit->first)
                  return true;
     }
     return false;
}
bool TriangulationCoordinateSystem::containsPoint(int index)
{
     map<int, Point>::iterator pit;
     for(pit = pointTable.begin(); pit != pointTable.end(); pit++)
     {
         if(index == pit->first)
                  return true;
     }
     return false;
}
Point TriangulationCoordinateSystem::getPoint(int index)
{
      return pointTable[index];
}
Line TriangulationCoordinateSystem::getLine(int index)
{
      return lineTable[index];
}
void TriangulationCoordinateSystem::generatePlane()
{
    int v1, v2, va, vb, ea1, ea2, eb1, eb2;
    map<int, Edge>::iterator eit;
    Edge e = Triangulation::edgeTable.begin()->second;
    Point p1(0, 0);
    Point p2(e.getLength(), 0);
    Line l1(p1, p2);
    putLine(e.getIndex(), l1);
    int edgesAdded = 1;
    v1 = (*(e.getLocalVertices()))[0];
    v2 = (*(e.getLocalVertices()))[1];
    putPoint(v1, p1);
    putPoint(v2, p2);
    
    Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
    vector<int> vertex = listDifference(fa.getLocalVertices(), e.getLocalVertices());
    va = vertex[0];
    vector<int> edges = listIntersection(fa.getLocalEdges(), e.getLocalEdges());
    ea1 = edges[0];
    ea2 = edges[1];
    if(!Triangulation::edgeTable[ea1].isAdjVertex(v1))
    {
        int temp = ea1;
        ea1 = ea2;
        ea2 = temp;
    }
    
    Circle c1(p1, Triangulation::edgeTable[ea1].getLength());
    Circle c2(p2, Triangulation::edgeTable[ea2].getLength());
    vector<Point> points = circleIntersection(c1, c2);
    for(int i = 0; i < points.size(); i++)
    {
         if(!l1.isAbove(points[i]))
         {
              Point p = points[i];
              putPoint(va, p);
              Line l2(p1, p);
              Line l3(p2, p);
              putLine(ea1, l2);
              putLine(ea2, l3);
              edgesAdded += 2;
              break;       
         }
    }
    if(!e.isBorder())
    {
        Face fb = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
        vertex = listDifference(fb.getLocalVertices(), e.getLocalVertices());
        vb = vertex[0];
        edges = listIntersection(fb.getLocalEdges(), e.getLocalEdges());
        eb1 = edges[0];
        eb2 = edges[1];
        if(!Triangulation::edgeTable[eb1].isAdjVertex(v1))
        {
           int temp = eb1;
           eb1 = eb2;
           eb2 = temp;
        }
        Circle c3(p1, Triangulation::edgeTable[eb1].getLength());
        Circle c4(p2, Triangulation::edgeTable[eb2].getLength());
        vector<Point> points = circleIntersection(c3, c4);
        for(int i = 0; i < points.size(); i++)
        {
             if(l1.isAbove(points[i]))
             {
                    Point p = points[i];
                    putPoint(vb, p);
                    Line l2(p1, p);
                    Line l3(p2, p);
                    putLine(eb1, l2);
                    putLine(eb2, l3);
                    edgesAdded += 2;
                    break;       
             }
        }
    }
    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], edgesAdded);
    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], edgesAdded);
    if(!e.isBorder())
    {
       edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], edgesAdded);
       edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], edgesAdded);
   }
   cout << "EA: " << edgesAdded << "\n";
}

int TriangulationCoordinateSystem::generatePlaneHelper(Edge e, int edgesAdded)
{
     if(edgesAdded == Triangulation::edgeTable.size() || e.isBorder())
     {
         return edgesAdded;
     }
     Line l = lineTable[e.getIndex()];
     int va, vb, v1, v2, ea1, ea2, eb1, eb2;
     Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     vector<int> vertex = listDifference(fa.getLocalVertices(), e.getLocalVertices());
     va = vertex[0];
     vector<int> edges = listIntersection(fa.getLocalEdges(), e.getLocalEdges());
     ea1 = edges[0];
     ea2 = edges[1];
     v1 = (listIntersection(Triangulation::edgeTable[ea1].getLocalVertices(), e.getLocalVertices()))[0];
     v2 = (listIntersection(Triangulation::edgeTable[ea2].getLocalVertices(), e.getLocalVertices()))[0];
     Face fb = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     vertex = listDifference(fb.getLocalVertices(), e.getLocalVertices());
     vb = vertex[0];
     edges = listIntersection(fb.getLocalEdges(), e.getLocalEdges());
     eb1 = edges[0];
     eb2 = edges[1];
     if(!Triangulation::edgeTable[eb1].isAdjVertex(v1))
     {
        int temp = eb1;
        eb1 = eb2;
        eb2 = temp;
     }

     if(!containsPoint(va))
     {
         Circle c1(pointTable[v1], Triangulation::edgeTable[ea1].getLength());
         Circle c2(pointTable[v2], Triangulation::edgeTable[ea2].getLength());
         vector<Point> points = circleIntersection(c1, c2);
         bool otherPos = l.isAbove(pointTable[vb]);
         for(int i = 0; i < points.size(); i++)
         {
              if(l.isAbove(points[i]) != otherPos)
              {
                  Point p = points[i];
                  Line l1(pointTable[v1], p);
                  Line l2(pointTable[v2], p);
                  putPoint(va, p);
                  if(!containsLine(ea1))
                  {
                     putLine(ea1, l1);
                     edgesAdded++;
                  }
                  if(!containsLine(ea2))
                  {
                     putLine(ea2, l2);
                     edgesAdded++;
                  }
                  edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], edgesAdded);
                  edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], edgesAdded);
              }
         }

     }
     if(!containsPoint(vb))
     {
         Circle c1(pointTable[v1], Triangulation::edgeTable[eb1].getLength());
         Circle c2(pointTable[v2], Triangulation::edgeTable[eb2].getLength());
         vector<Point> points = circleIntersection(c1, c2);
         bool otherPos = l.isAbove(pointTable[va]);
         for(int i = 0; i < points.size(); i++)
         {
              if(l.isAbove(points[i]) != otherPos)
              {
                  Point p = points[i];
                  Line l1(pointTable[v1], p);
                  Line l2(pointTable[v2], p);
                  putPoint(vb, p);
                  if(!containsLine(eb1))
                  {
                    putLine(eb1, l1);
                    edgesAdded++;
                  }
                  if(!containsLine(eb2))
                  {
                    putLine(eb2, l2);
                    edgesAdded++;
                  }
                  edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], edgesAdded);
                  edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], edgesAdded);
             }
         }
     }
     if(!containsLine(ea1))
     {
        Line l1(pointTable[v1], pointTable[va]);
        putLine(ea1, l1);
        edgesAdded++;
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], edgesAdded);
     }
     if(!containsLine(ea2))
     {
        Line l1(pointTable[v2], pointTable[va]);
        putLine(ea2, l1);
        edgesAdded++;
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], edgesAdded);
     }
     if(!containsLine(eb1))
     {
        Line l1(pointTable[v1], pointTable[vb]);
        putLine(eb1, l1);
        edgesAdded++;
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], edgesAdded);
     }
     if(!containsLine(eb2))
     {
        Line l1(pointTable[v2], pointTable[vb]);
        putLine(eb2, l1);
        edgesAdded++;
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], edgesAdded);
     }
     return edgesAdded;
}

void TriangulationCoordinateSystem::printToFile(char* filename)
{
     ofstream results(filename, ios_base::trunc);
     results << setprecision(6);
     // Go through faces, list coordinates of points
     map<int, Face>::iterator fit;
     for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
     {
        vector<int> vertices = *(fit->second.getLocalVertices());
        for(int i = 0; i < vertices.size(); i++)
        {
                results << left << setw(10) << pointTable[vertices[i]].x << "              ";
                results << pointTable[vertices[i]].y << "\n";
        }
        results << setw(10) << pointTable[vertices[0]].x << "              ";
        results<< pointTable[vertices[0]].y << "\n\n";
     }
}
