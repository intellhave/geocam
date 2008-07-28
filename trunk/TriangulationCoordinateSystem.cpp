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
void TriangulationCoordinateSystem::clearSystem()
{
     map<int, Point>::iterator pit;
     map<int, Line>::iterator lit;
     int pointSize = pointTable.size();
     int lineSize = lineTable.size();
     int pointArr[pointSize];
     int lineArr[lineSize];
     int i;
     for(pit = pointTable.begin(), i = 0 ; pit != pointTable.end(); pit++, i++)
     {
             pointArr[i] = pit->first;
     }
     for(i = 0, lit = lineTable.begin(); lit != lineTable.end(); lit++, i++)
     {
             lineArr[i] = lit->first;
     }
     for(i = 0; i < pointSize; i++)
     {
             pointTable.erase(pointArr[i]);
     }
     for(i = 0; i < lineSize; i++)
     {
             lineTable.erase(lineArr[i]);
     }
}
void TriangulationCoordinateSystem::generatePlane()
{
    clearSystem(); // Clear the system first.
    int v1, v2, va, vb, ea1, ea2, eb1, eb2; // Needed references
    Edge e = Triangulation::edgeTable.begin()->second; // Get the first edge.
    Point p1(0, 0);
    Point p2(e.getLength(), 0);
    Line l1(p1, p2); // Create the first line
    putLine(e.getIndex(), l1);
    int edgesAdded = 1; // Edges added keeps track of all the lines added 
                        // to the system.
    v1 = (*(e.getLocalVertices()))[0]; // Get the first vertex.
    v2 = (*(e.getLocalVertices()))[1]; // Get the second vertex.
    putPoint(v1, p1); // Add the points to the system.
    putPoint(v2, p2);
    
    // Get the first face.
    Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]]; 
    vector<int> vertex = listDifference(fa.getLocalVertices(), e.getLocalVertices());
    va = vertex[0]; // Get vertex on fa and oposite edge
    vector<int> edges = listIntersection(fa.getLocalEdges(), e.getLocalEdges());
    ea1 = edges[0]; // Get the other two edges
    ea2 = edges[1];
    if(!Triangulation::edgeTable[ea1].isAdjVertex(v1)) // Make sure ea1 has v1
    {                     // This is needed for finding proper solutions for va.
        int temp = ea1;
        ea1 = ea2;
        ea2 = temp;
    }
    vector<Point> points; // Set of solution points.
    // Rotate line l1 by the proper angle counter-clockwise and scale by 
    // length ea1 to find coordinate point for third vertex.
    Point p3 = findPoint(l1, Triangulation::edgeTable[ea1].getLength(), 
               angle(Triangulation::vertexTable[v1], fa));
    // Rotate line l1 by the proper angle clockwise and scale by length ea1
    // to find coordinate point for third vertex.
    Point p4 = findPoint(l1, Triangulation::edgeTable[ea1].getLength(), 
              (-1) * angle(Triangulation::vertexTable[v1], fa));
    points.push_back(p3);
    points.push_back(p4);
    for(int i = 0; i < points.size(); i++)
    {
         if(!l1.isAbove(points[i])) // Take the point that is above line l1.
         {                          // That is, l1 is not above that point.
              Point p = points[i];
              putPoint(va, p);
              Line l2(p1, p); // Construct the points and lines, place them in
              Line l3(p2, p); // coordinate system.
              putLine(ea1, l2);
              putLine(ea2, l3);
              edgesAdded += 2; // Increment edges added by 2.
              break;       
         }
    }
    if(!e.isBorder()) // If the edge has another face...do the same for it.
    {
        // Get the second face, the other vertex and the other edges.
        Face fb = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
        vertex = listDifference(fb.getLocalVertices(), e.getLocalVertices());
        vb = vertex[0];
        edges = listIntersection(fb.getLocalEdges(), e.getLocalEdges());
        eb1 = edges[0];
        eb2 = edges[1];
        if(!Triangulation::edgeTable[eb1].isAdjVertex(v1)) // Again, make sure 
        {                                                  // eb1 has v1                     
           int temp = eb1;
           eb1 = eb2;
           eb2 = temp;
        }
        vector<Point> points2; // New solution set.
        p3 = findPoint(l1, Triangulation::edgeTable[ea1].getLength(), 
               angle(Triangulation::vertexTable[v1], fa));
        p4 = findPoint(l1, Triangulation::edgeTable[ea1].getLength(), 
              (-1) * angle(Triangulation::vertexTable[v1], fa));
        points2.push_back(p3);
        points2.push_back(p4);
        
        bool otherPos = false; // The orientation of the other vertex. False
                               // since va was above line l1.
        
        bool negA = fa.isNegative(); // Determine if either fa or fb is negative
        bool negB = fb.isNegative(); // change otherPos accordingly.
        if((!(negA && negB)) && (negA || negB))
        { // If either fa or fb is negative, but not both ... switch otherPos.
              otherPos = !otherPos; // Now the correct point is also above l1.
        }  
        
        for(int i = 0; i < points.size(); i++)
        {
             if(l1.isAbove(points[i]) != otherPos)
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
    // Call helper method on each edge, setting edgesAdded to the return value.
    // It may be that by the time this call returns, many edges were added.
    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], edgesAdded);
    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], edgesAdded);
    if(!e.isBorder())
    {
       edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], edgesAdded);
       edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], edgesAdded);
   }
}

int TriangulationCoordinateSystem::generatePlaneHelper(Edge e, int edgesAdded)
{
    // This is the exit condition: if all the edges have been added or this
    // edge is a border (which means all of its info has been added already),
    // then return.
     if(edgesAdded == Triangulation::edgeTable.size() || e.isBorder())
     { 
         return edgesAdded;
     }
     
     // Get the line representing edge e. It should already exist.
     Line l = lineTable[e.getIndex()]; 

     int va, vb, v1, v2, ea1, ea2, eb1, eb2; // Needed references
     Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     vector<int> vertex = listDifference(fa.getLocalVertices(), e.getLocalVertices());
     va = vertex[0];
     vector<int> edges = listIntersection(fa.getLocalEdges(), e.getLocalEdges());
     ea1 = edges[0];
     ea2 = edges[1];
     // Make sure v1 and v2 are connected to ea1 and ea2, respectively.
     v1 = (listIntersection(Triangulation::edgeTable[ea1].getLocalVertices(), e.getLocalVertices()))[0];
     v2 = (listIntersection(Triangulation::edgeTable[ea2].getLocalVertices(), e.getLocalVertices()))[0];
     
     Face fb = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     vertex = listDifference(fb.getLocalVertices(), e.getLocalVertices());
     vb = vertex[0];
     edges = listIntersection(fb.getLocalEdges(), e.getLocalEdges());
     eb1 = edges[0];
     eb2 = edges[1];
     // Make sure that v1 is the intial point of line l. This is needed to
     // properly rotate the and find the potential points.
     if(l.getInitialX() != pointTable[v1].x || l.getInitialY() != pointTable[v1].y)
     {
         int temp = v1;
         v1 = v2;
         v2 = temp;
         temp = ea1;
         ea1 = ea2;
         ea2 = temp;
         temp = eb1;
         eb1 = eb2;
         eb2 = temp;
     }
     if(!Triangulation::edgeTable[eb1].isAdjVertex(v1)) // Since v1 and v2 are
     {                          // already fixed, change eb1 and eb2 if needed.
        int temp = eb1;
        eb1 = eb2;
        eb2 = temp;
     }

     if(!containsPoint(va)) // If point va has not already been added...add it.
     {
         vector<Point> points;
         Point p1 = findPoint(l, Triangulation::edgeTable[ea1].getLength(), 
                              angle(Triangulation::vertexTable[v1], fa));
         Point p2 = findPoint(l, Triangulation::edgeTable[ea1].getLength(), 
                              (-1) * angle(Triangulation::vertexTable[v1], fa));
         points.push_back(p1);
         points.push_back(p2);
         
         /* vb must already be added, get its orientation. Most of time, we
            want the point that is opposite the one already added. So if vb
            is above the line, va should be below.
                              . <- vb
                             / \
                            /   \
                           / *   \
                          /       \     
                         /_________\
                         
                               (*) <- We want this one for va
         */
         bool otherPos = l.isAbove(pointTable[vb]); 

         // Check for negative triangles on either face. 
         // Switch otherPos if only one is negative.
         bool negA = fa.isNegative();
         bool negB = fb.isNegative();
         if((!(negA && negB)) && (negA || negB))
         {
              otherPos = !otherPos;
         }  
         
         for(int i = 0; i < points.size(); i++)
         {
              if(l.isAbove(points[i]) != otherPos) // If it is not the same
              {
                  Point p = points[i];
                  Line l1(pointTable[v1], p);
                  Line l2(pointTable[v2], p);
                  putPoint(va, p);
                  if(!containsLine(ea1))
                  {
                     putLine(ea1, l1);
                     edgesAdded++;
                     edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], edgesAdded);
                  }
                  if(!containsLine(ea2))
                  {
                     putLine(ea2, l2);
                     edgesAdded++;
                     edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], edgesAdded);
                  }
             }
         }
     }
     if(!containsPoint(vb))
     {
         Circle c1(pointTable[v1], Triangulation::edgeTable[eb1].getLength());
         Circle c2(pointTable[v2], Triangulation::edgeTable[eb2].getLength());
         vector<Point> points;
         Point p1 = findPoint(l, Triangulation::edgeTable[eb1].getLength(), 
                              angle(Triangulation::vertexTable[v1], fb));
         Point p2 = findPoint(l, Triangulation::edgeTable[eb1].getLength(), 
                              (-1) * angle(Triangulation::vertexTable[v1], fb));
         points.push_back(p1);
         points.push_back(p2);
         bool otherPos = l.isAbove(pointTable[va]);
         bool negA = fa.isNegative();
         bool negB = fb.isNegative();
         if((!(negA && negB)) && (negA || negB))
         {
              otherPos = !otherPos;
         }
         bool foundOne = false;
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
                    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], edgesAdded);
                  }
                  if(!containsLine(eb2))
                  {
                    putLine(eb2, l2);
                    edgesAdded++;
                    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], edgesAdded);
                  }
                  foundOne = true;
             } 
         }
         if(!foundOne)
         {
               cout << "Here\n";
//             cout << l.getLength() << "   ";
//             printPoint(l.getInitial());
//             cout << "    ";
//             printPoint(l.getEnding());
//             cout << "\n";
//             cout << e.getLength() << "   ";
//             cout << Triangulation::edgeTable[eb1].getLength() << "   ";
//             cout << Triangulation::edgeTable[eb2].getLength() << "\n";
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
        if(fit->second.isNegative())
           results << "-1\n";
        else
            results << "1\n";
        vector<int> vertices = *(fit->second.getLocalVertices());
        for(int i = 0; i < vertices.size(); i++)
        {
                results << left << setw(10) << pointTable[vertices[i]].x << "              ";
                results << pointTable[vertices[i]].y << "\n";
        }
        results << setw(10) << pointTable[vertices[0]].x << "              ";
        results<< pointTable[vertices[0]].y << "\n";
     }
}
