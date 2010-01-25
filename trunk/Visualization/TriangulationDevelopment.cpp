#include <fstream>
#include <iostream>
#include <iomanip>
#include "TriangulationDevelopment.h"
#include "euc_angle.h"
#include "partial_edge.h"
#define PI 	3.141592653589793238

TriangulationDevelopment::TriangulationDevelopment()
{
}
TriangulationDevelopment::~TriangulationDevelopment()
{
}
void TriangulationDevelopment::putLine(int index, Line l)
{
    lineTable.insert(pair<int, Line>(index, l));
}
void TriangulationDevelopment::putPoint(int index, Point p)
{
     pointTable.insert(pair<int, Point>(index, p));
}
bool TriangulationDevelopment::containsLine(int index)
{
     map<int, Line>::iterator lit;
     for(lit = lineTable.begin(); lit != lineTable.end(); lit++)
     {
         if(index == lit->first)
                  return true;
     }
     return false;
}
bool TriangulationDevelopment::containsPoint(int index)
{
     map<int, Point>::iterator pit;
     for(pit = pointTable.begin(); pit != pointTable.end(); pit++)
     {
         if(index == pit->first)
                  return true;
     }
     return false;
}
Point TriangulationDevelopment::getPoint(int index)
{
      return pointTable[index];
}
Line TriangulationDevelopment::getLine(int index)
{
      return lineTable[index];
}
void TriangulationDevelopment::clearSystem()
{
    dualList.clear();
    pointTable.clear();
    lineTable.clear();
}
void TriangulationDevelopment::generatePlane()
{
    clearSystem(); // Clear the system first.
    int v1, v2, va, vb, ea1, ea2, eb1, eb2; // Needed references
    Edge e = Triangulation::edgeTable.begin()->second; // Get the first edge.
    Point p1(0, 0);
    Point p2(Length::valueAt(e), 0);
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
    Point p3 = findPoint(l1, Length::valueAt(Triangulation::edgeTable[ea1]), 
               EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fa));
    // Rotate line l1 by the proper angle clockwise and scale by length ea1
    // to find coordinate point for third vertex.
    Point p4 = findPoint(l1, Length::valueAt(Triangulation::edgeTable[ea1]), 
              (-1) * EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fa));
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
        p3 = findPoint(l1, Length::valueAt(Triangulation::edgeTable[eb1]), 
               EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fb));
        p4 = findPoint(l1, Length::valueAt(Triangulation::edgeTable[eb1]), 
              (-1) * EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fb));
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
        for(int i = 0; i < points2.size(); i++)
        {
             if(l1.isAbove(points2[i]) != otherPos)
             {
                    Point p = points2[i];
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

int TriangulationDevelopment::generatePlaneHelper(Edge e, int edgesAdded)
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
     /* Reference Chart:
                              . <- va    
                             / \      
                            /   \      
                        ea1/[fa] \ea2    
                          /       \      
                   v1-> ./___ e ___\. <- v2   
                         \         /      
                          \  [fb] /      
                        eb1\     /eb2   
                            \   /      
                             \ /      
                              . <- vb   
      */
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
     // properly rotate the lines and find the potential points.
     while(l.getInitialX() != pointTable[v1].x || l.getInitialY() != pointTable[v1].y)
     {
         int temp = v1;
         v1 = v2;
         v2 = temp;
         temp = ea1; // We have to change ea1 as well.
         ea1 = ea2;
         ea2 = temp;
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
         // Rotate counter-clockwise along v1 from edge e to edge ea1.
         Point p1 = findPoint(l, Length::valueAt(Triangulation::edgeTable[ea1]), 
                              EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fa));
         // Rotate clockwise along v1 from edge e to edge ea1.
         Point p2 = findPoint(l, Length::valueAt(Triangulation::edgeTable[ea1]), 
                              (-1) * EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fa));
         points.push_back(p1);
         points.push_back(p2);
         
         /* vb must already be added, get its orientation. Most of time, we
            want the point that is opposite the one already added. So if vb
            is above the line, va should be below.
                              . <- vb
                             / \
                            /   \
                        eb1/ *   \eb2
                          /       \     
                     v1  /_________\ v2
                         
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
              if(l.isAbove(points[i]) != otherPos) // If it is not the same ...
              {                                    // we want this point.
                  Point p = points[i];
                  Line l1(pointTable[v1], p); // Line for ea1
                  Line l2(pointTable[v2], p); // Line for ea2
                  putPoint(va, p);
                  if(!containsLine(ea1)) // If ea1 has not been added...add it
                  {
                     putLine(ea1, l1);
                     edgesAdded++; // Increment edges added.
                     edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], 
                                edgesAdded); // Recursively go through edge ea1.
                  }
                  if(!containsLine(ea2)) // If ea2 has not been added...add it
                  {
                     putLine(ea2, l2);
                     edgesAdded++; // Increment edges added.
                     edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], 
                                edgesAdded); // Recursively go through edge ea2.
                  }
             }
         }
     } 
    else if(!containsPoint(vb)) // If point vb has not been added...add it.
     {
         vector<Point> points;
         // Rotate counter-clockwise along v1 from edge e to edge eb1.
         Point p1 = findPoint(l, Length::valueAt(Triangulation::edgeTable[eb1]), 
                              EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fb));
         // Rotate clockwise along v1 from edge e to edge eb1.
         Point p2 = findPoint(l, Length::valueAt(Triangulation::edgeTable[eb1]), 
                              (-1) * EuclideanAngle::valueAt(Triangulation::vertexTable[v1], fb));
         points.push_back(p1);
         points.push_back(p2);
         
         /* va must already be added, get its orientation. Most of time, we
            want the point that is opposite the one already added. So if va
            is above the line, vb should be below.
                              . <- va
                             / \
                            /   \
                        ea1/ *   \ea2
                          /       \     
                     v1  /_________\ v2
                         
                               (*) <- We want this one for vb
         */
         bool otherPos = l.isAbove(pointTable[va]);
         bool negA = fa.isNegative();
         bool negB = fb.isNegative();
         if((!(negA && negB)) && (negA || negB))
         { // Switch otherPos if only either fa or fb is a "negative" triangle.
              otherPos = !otherPos;
         }
         for(int i = 0; i < points.size(); i++)
         {
              if(l.isAbove(points[i]) != otherPos) // If it is not the same ...
              {                                    // we want this point.
                  Point p = points[i];
                  Line l1(pointTable[v1], p); // Line for eb1
                  Line l2(pointTable[v2], p); // Line for eb2
                  putPoint(vb, p);
                  if(!containsLine(eb1)) // If eb1 has not been added... add it.
                  {
                    putLine(eb1, l1);
                    edgesAdded++; // Increment edges added.
                    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], 
                               edgesAdded); // Recursively go through edge eb1.
                  }
                  if(!containsLine(eb2)) // If eb2 has not been added... add it.
                  {
                    putLine(eb2, l2);
                    edgesAdded++; // Increment edges added.
                    edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], 
                               edgesAdded); // Recursively go through edge eb2.
                  }
             } 
         }
     }
   else // Both va and vb were already added, but that does not mean all of 
   {   // the edges have been added.
     if(!containsLine(ea1)) // If ea1 has not been added ... add it.
     {
        Line l1(pointTable[v1], pointTable[va]); // Connect v1 to va. 
        putLine(ea1, l1);
        edgesAdded++; // Increment edges added.
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea1], 
                   edgesAdded); // Recursively go through edge ea1.
     }
     if(!containsLine(ea2)) // If ea2 has not been added ... add it.
     {
        Line l1(pointTable[v2], pointTable[va]); // Connect v2 to va.
        putLine(ea2, l1);
        edgesAdded++; // Increment edges added.
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[ea2], 
                   edgesAdded); // Recursively go through edge ea2.
     }
     if(!containsLine(eb1)) // If eb1 has not been added ... add it.
     {
        Line l1(pointTable[v1], pointTable[vb]); // Connect v1 to vb.
        putLine(eb1, l1);
        edgesAdded++; // Increment edges added.
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb1], 
                   edgesAdded); // Recursively go through edge eb1.
     }
     if(!containsLine(eb2)) // If eb2 has not been added ... add it.
     {
        Line l1(pointTable[v2], pointTable[vb]); // Connect v2 to vb.
        putLine(eb2, l1);
        edgesAdded++; // Increment edges added.
        edgesAdded = generatePlaneHelper(Triangulation::edgeTable[eb2], 
                   edgesAdded); // Recursively go through edge eb2.
     }
   }
     return edgesAdded;
}

void TriangulationDevelopment::update()
{
     lineTable.clear();
     map<int, Point>::iterator pit;
     for(pit = pointTable.begin(); pit != pointTable.end(); pit++)
     {
         vector<int> edges = *(Triangulation::vertexTable[pit->first]).getLocalEdges();
         for(int i = 0; i < edges.size(); i++)
         {
               if(!containsLine(edges[i]))
               {
                  vector<int> vertices = *(Triangulation::edgeTable[edges[i]].getLocalVertices());
                  Point p1 = pointTable[vertices[0]];
                  Point p2 = pointTable[vertices[1]];
                  Line l(p1, p2);
                  putLine(edges[i], l);
               }
         }
     }
     
    dualList.clear();
    map<int, Edge>::iterator eit;
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
        addDual(eit->second);
    }
}
void TriangulationDevelopment::addHalfDual(Edge e, Face f)
{
     int v1 = (*(e.getLocalVertices()))[0];
     int v2 = (*(e.getLocalVertices()))[1];
     Line l = lineTable[e.getIndex()];
     if((l.getInitialX() != pointTable[v1].x) || (l.getInitialY() != pointTable[v1].y))
     {
       int temp = v1;
       v1 = v2;
       v2 = temp;
     }
     Point p1 = pointTable[v1];

     double d12 = PartialEdge::valueAt(Triangulation::vertexTable[v1], e);

     double height = getHeight(f, e);
     //added the next line to flip the height if face is negative to handle duals of neg faces
     if (f.isNegative()) {
        height = -height;
     }
     double xComp = p1.x + (d12*(pointTable[v2].x - p1.x) / Length::valueAt(e));
     double yComp = p1.y + (d12*(pointTable[v2].y - p1.y) / Length::valueAt(e));
     Point partial(xComp, yComp);
     Line halfLine(partial, p1);
     Point center1 = findPoint(halfLine, height, -PI/2);
     Point center2 = findPoint(halfLine, height, PI/2);
     int v3 = listDifference(f.getLocalVertices(), e.getLocalVertices())[0];
     bool v3Pos = l.isAbove(pointTable[v3]);
     
     if(height < 0)
     {
        v3Pos = !v3Pos;
     }
     if(l.isAbove(center1) == v3Pos)
     {
        Line halfDual(partial, center1);
        dualList.push_back(halfDual);                   
     }
     else if(l.isAbove(center2) == v3Pos)
     {
           Line halfDual(partial, center2);
           dualList.push_back(halfDual); 
     }
     
}

void TriangulationDevelopment::addDuals(Face f)
{
     for (int i = 0; i <= 2; i++)
     {
        Edge e = Triangulation::edgeTable[(*(f.getLocalEdges()))[i]]; 
        addHalfDual(e, f);
     }
}

void TriangulationDevelopment::addDual(Edge e)
{
     Face f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     addHalfDual(e, f1);
     if(!e.isBorder())
     {
         Face f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
         addHalfDual(e, f2);
     }
}

void TriangulationDevelopment::addDuals(Vertex v)
{
     for(int i = 0; i < v.getLocalEdges()->size(); i++)
     {
         Edge e = Triangulation::edgeTable[(*(v.getLocalEdges()))[i]];
         addDual(e);
     }
}

void TriangulationDevelopment::printToFile(char* filename)
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
                results <<  left << setw(10) << vertices[i];
                results << left << setw(10) << pointTable[vertices[i]].x << "              ";
                results << pointTable[vertices[i]].y << "\n";
        }
        results << "\n";
     }
}

vector<triangle_parts> TriangulationDevelopment::getTriangles(void) {
    vector<triangle_parts> ts;
    map<int, Face>::iterator fit;
    for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
    {
        vector<int> vertices = *(fit->second.getLocalVertices());
        struct triangle_parts a_tri;
        if(fit->second.isNegative())
            a_tri.negativity = -1;
        else
            a_tri.negativity = 1;
        for(int i = 0; i < vertices.size(); i++)
        {
            a_tri.coords[i][0] = pointTable[vertices[i]].x;
            a_tri.coords[i][1] = pointTable[vertices[i]].y;
        }
        ts.push_back(a_tri);
    }

    return ts;
}

void TriangulationDevelopment::printDuals(char* filename)
{
     ofstream results(filename, ios_base::trunc);
     results << setprecision(6);
     for(int i = 0; i < dualList.size(); i++)
     {
             results << left << setw(10) << dualList[i].getInitialX() << "              ";
             results << dualList[i].getInitialY() << "\n";
             results << left << setw(10) << dualList[i].getEndingX() << "              ";
             results << dualList[i].getEndingY() << "\n\n";
     }
}

vector<Line> TriangulationDevelopment::getDuals(void)
{
    vector<Line> ds;
    for(int i = 0; i < dualList.size(); i++) {
        ds.push_back(dualList[i]);
    }
    return ds;
}

Line TriangulationDevelopment::getDual(int e) {
    Line l = dualList[e];
    return l;
}
