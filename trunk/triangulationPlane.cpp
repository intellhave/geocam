#include <cstdlib>
#include <iostream>
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "triangulation.h"
#include "triangulationmath.h"
#include "triangulationPlane.h"
#include "triangulationInputOutput.h"
#include "triangulationMorph.h"
#include "TriangulationCoordinateSystem.h"
#include "miscmath.h"
#include <fstream>
#include <iomanip>
#include "delaunay.h"
#define PI 	3.141592653589793238

void firstTriangle(double length1, double length2, double length3)
{
     if(length1 >= length2 + length3)
     throw string("Invalid Edge Lengths");
     if(length2 >= length1 + length3)
     throw string("Invalid Edge Lengths");
     if(length3 >= length1 + length2)
     throw string("Invalid Edge Lengths");
     

    Face f1(1);
    Edge e1(1);
    Edge e2(2);
    Edge e3(3);
    Vertex v1(1);
    Vertex v2(2);
    Vertex v3(3);

    Triangulation::putFace(1, f1);
    Triangulation::putEdge(1, e1);
    Triangulation::putEdge(2, e2);
    Triangulation::putEdge(3, e3);
    Triangulation::putVertex(1, v1);
    Triangulation::putVertex(2, v2);
    Triangulation::putVertex(3, v3);
    Triangulation::vertexTable[1].addVertex(2);
    Triangulation::vertexTable[1].addVertex(3);
    Triangulation::vertexTable[2].addVertex(1);
    Triangulation::vertexTable[2].addVertex(3);
    Triangulation::vertexTable[3].addVertex(1);
    Triangulation::vertexTable[3].addVertex(2);
    Triangulation::vertexTable[1].addEdge(1);
    Triangulation::vertexTable[1].addEdge(2);
    Triangulation::vertexTable[2].addEdge(1);
    Triangulation::vertexTable[2].addEdge(3);
    Triangulation::vertexTable[3].addEdge(2);
    Triangulation::vertexTable[3].addEdge(3);
    Triangulation::vertexTable[1].addFace(1);
    Triangulation::vertexTable[2].addFace(1);
    Triangulation::vertexTable[3].addFace(1);
    Triangulation::edgeTable[1].addVertex(1);
    Triangulation::edgeTable[1].addVertex(2);
    Triangulation::edgeTable[2].addVertex(1);
    Triangulation::edgeTable[2].addVertex(3);
    Triangulation::edgeTable[3].addVertex(2);
    Triangulation::edgeTable[3].addVertex(3);
    Triangulation::edgeTable[1].addEdge(2);
    Triangulation::edgeTable[1].addEdge(3);
    Triangulation::edgeTable[2].addEdge(1);
    Triangulation::edgeTable[2].addEdge(3);
    Triangulation::edgeTable[3].addEdge(1);
    Triangulation::edgeTable[3].addEdge(2);
    Triangulation::edgeTable[1].addFace(1);
    Triangulation::edgeTable[2].addFace(1);
    Triangulation::edgeTable[3].addFace(1);
    Triangulation::faceTable[1].addVertex(1);
    Triangulation::faceTable[1].addVertex(2);
    Triangulation::faceTable[1].addVertex(3);
    Triangulation::faceTable[1].addEdge(1);
    Triangulation::faceTable[1].addEdge(2);
    Triangulation::faceTable[1].addEdge(3);
    
    Triangulation::edgeTable[1].setLength(length1);
    Triangulation::edgeTable[2].setLength(length2);
    Triangulation::edgeTable[3].setLength(length3);
        
}

void addTriangle(Edge e, double length1, double length2)
{
     
     if(e.getLocalFaces()->size() > 1)
     throw string("Invalid Edge");
     if(e.getLength() >= (length1 + length2))
     throw string("Invalid Edge Lengths");
     if(length1 >= (e.getLength() + length2))
     throw string("Invalid Edge Lengths");
     if(length2 >= (e.getLength() + length1))
     throw string("Invalid Edge Lengths");
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     
     double anglesum = getAngleSum(va1);
     if(getAngleSum(va1) + angle(e.getLength(), length1, length2) > 2 * PI)
     throw string("angle sum 1");
     
     if(getAngleSum(va2) + angle(e.getLength(), length2, length1) > 2 * PI)
     throw string("angle sum 2");

     Vertex vb(Triangulation::greatestVertex() + 1);
     Edge eb1(Triangulation::greatestEdge() + 1);
     Edge eb2(Triangulation::greatestEdge() + 2);
     Face fb(Triangulation::greatestFace() + 1);
     Triangulation::putVertex(vb.getIndex(), vb);
     Triangulation::putEdge(eb1.getIndex(), eb1);
     Triangulation::putEdge(eb2.getIndex(), eb2);
     
     Triangulation::putFace(fb.getIndex(), fb);
     
     vector<int> diff;
     diff = listDifference(fa.getLocalVertices(), e.getLocalVertices());
     Vertex va3 = Triangulation::vertexTable[diff[0]];
     
     Triangulation::vertexTable[vb.getIndex()].addVertex(va1.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addVertex(va2.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addVertex(vb.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addVertex(vb.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addEdge(eb1.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addEdge(eb2.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addEdge(eb1.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addEdge(eb2.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addVertex(vb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addVertex(va1.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addVertex(vb.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addVertex(va2.getIndex());
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].addEdge(eb1.getIndex());
             Triangulation::edgeTable[eb1.getIndex()].addEdge((*(va1.getLocalEdges()))[i]);
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].addEdge(eb2.getIndex());
             Triangulation::edgeTable[eb2.getIndex()].addEdge((*(va2.getLocalEdges()))[i]);
     }
     Triangulation::edgeTable[eb1.getIndex()].addEdge(eb2.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addEdge(eb1.getIndex());
     Triangulation::edgeTable[e.getIndex()].addEdge(eb1.getIndex());
     Triangulation::edgeTable[e.getIndex()].addEdge(eb2.getIndex());
     Triangulation::edgeTable[e.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(vb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa.getIndex());
     Triangulation::faceTable[fa.getIndex()].addFace(fb.getIndex());
     
     Triangulation::edgeTable[eb1.getIndex()].setLength(length1);
     Triangulation::edgeTable[eb2.getIndex()].setLength(length2);
}

void addTriangle(Edge e1, Edge e2)
{
     if(!e1.isAdjEdge(e2.getIndex()))
     throw string("Error: Not adjacent");
     if(!e2.isAdjEdge(e1.getIndex()))
     throw string("Error: Not adjacent");
     if(e1.getLocalFaces()->size() > 1 || e2.getLocalFaces()->size() > 1)
     throw string("Error: Not valid edges");
     
     Vertex v1, v2, va;
     vector<int> diff, sameAs;
     
     diff = listDifference(e1.getLocalVertices(), e2.getLocalVertices());
     v1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(e2.getLocalVertices(), e1.getLocalVertices());
     v2 = Triangulation::vertexTable[diff[0]];
     sameAs = listIntersection(e1.getLocalVertices(), e2.getLocalVertices());
     va = Triangulation::vertexTable[sameAs[0]];
     
     double ang = 2 * PI - getAngleSum(va);
     if(ang > PI)
     throw string("...");
     
     Edge eb(Triangulation::greatestEdge() + 1);
     Face fb(Triangulation::greatestFace() + 1);
     Face fa1 = Triangulation::faceTable[(*(e1.getLocalFaces()))[0]];
     Face fa2 = Triangulation::faceTable[(*(e2.getLocalFaces()))[0]];
     Triangulation::putEdge(eb.getIndex(), eb);
     Triangulation::putFace(fb.getIndex(), fb);
     
     
     Triangulation::vertexTable[v1.getIndex()].addVertex(v2.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addVertex(v1.getIndex());
     Triangulation::vertexTable[v1.getIndex()].addEdge(eb.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addEdge(eb.getIndex());
     Triangulation::vertexTable[v1.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addVertex(v1.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addVertex(v2.getIndex());
     for(int i = 0; i < v1.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(v1.getLocalEdges()))[i]].addEdge(eb.getIndex());
             Triangulation::edgeTable[eb.getIndex()].addEdge((*(v1.getLocalEdges()))[i]);
     }
     for(int i = 0; i < v2.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(v2.getLocalEdges()))[i]].addEdge(eb.getIndex());
             Triangulation::edgeTable[eb.getIndex()].addEdge((*(v2.getLocalEdges()))[i]);
     }
     Triangulation::edgeTable[e1.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[e2.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(v1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(v2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa2.getIndex());
     Triangulation::faceTable[fa1.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fa2.getIndex()].addFace(fb.getIndex());
     
     double l1 = e1.getLength();
     double l2 = e2.getLength();
     
     Triangulation::edgeTable[eb.getIndex()].setLength(sqrt(pow(l1, 2) + pow(l2, 2) - 2 * l1 * l2 * cos(ang)));
     
}


void generateTriangulation(int numFaces)
{
     map<int, Edge>::iterator eit;
     srand(time(NULL));
     
     double randNum, length1, length2, length3, range, rDec;
     randNum = (rand()%20000 + 1) / 20000.0;
     length1 = randNum *10 + 1;
     randNum = (rand()%20000 + 1) / 20000.0;
     length2 = randNum*10 + 1;
     range = (length1 + length2 - 1) - (abs(length1 - length2) + 1);
     int rInt = (int) range;
     rDec = range - rInt;
     randNum = (rand()%20000 + 1) / 20000.0;
     length3 = rand()%(rInt + 1) + (abs(length1 - length2) + 1) + randNum * rDec;

     firstTriangle(length1, length2, length3);

     while(Triangulation::faceTable.size() < numFaces)
     {
         for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
         {
            Edge e = eit->second;
            if(e.isBorder())
            {
                length1 = e.getLength();
                randNum = (rand()%20000 + 1) / 20000.0;
                if(randNum < 0.5)
                   randNum = -randNum;
                length2 = length1 + randNum;
                range = (length1 + length2 - 1) - (abs(length1 - length2) + 1);
                rInt = (int) range;
                if(rInt < 0)
                        rInt = 0;
                rDec = range - rInt;
                randNum = (rand()%20000 + 1) / 20000.0;
                length3 = rand()%(rInt + 1) + abs(length1 - length2) + 1
                          + randNum * rDec;
                try{
                    addTriangle(e, length2, length3);
                     if(Triangulation::faceTable.size() >= numFaces)
                       return;
                    }
                    catch(string s)
                    {
                      if(s.compare("angle sum 1") == 0)
                      {
                         Vertex v = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
                         vector<int> edges = listIntersection(v.getLocalEdges(), e.getLocalEdges());
                         for(int i = 0; i < edges.size(); i++)
                         {
                            if(Triangulation::edgeTable[edges[i]].isBorder())
                            {
                               try{                                             
                               addTriangle(e, Triangulation::edgeTable[edges[i]]);
                               }
                               catch(string s){ cout << "here\n";}
                               break;
                            }
                         }
                         if(Triangulation::faceTable.size() >= numFaces)
                               return;
                      }
                       else if(s.compare("angle sum 2") == 0)
                      {
                         Vertex v = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
                         vector<int> edges = listIntersection(v.getLocalEdges(), e.getLocalEdges());
                         for(int i = 0; i < edges.size(); i++)
                         {
                            if(Triangulation::edgeTable[edges[i]].isBorder())
                            {
                               try{                                             
                               addTriangle(e, Triangulation::edgeTable[edges[i]]);
                               }
                               catch(string s){cout << "here\n";}
                               break;
                            }
                         }
                         if(Triangulation::faceTable.size() >= numFaces)
                             return;
                      }
                    }
            }
            EdgeLoop: ;
         }
     }
}

void generateWeights()
{
     srand(time(NULL));
     map<int, Vertex>::iterator vit;
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
     {
          int index = (*(vit->second.getLocalEdges()))[0];
          double smallest = Triangulation::edgeTable[index].getLength();
          for(int i = 1; i < vit->second.getLocalEdges()->size(); i++)
          {
               index = (*(vit->second.getLocalEdges()))[i];
               if(Triangulation::edgeTable[index].getLength() < smallest)
               smallest = Triangulation::edgeTable[index].getLength();
          }
          double randNum = ((double) rand()) / RAND_MAX;
          vit->second.setWeightIndependent(randNum * smallest);
          
     }
}

void flipAlgorithm()
{
     int flipCount = 1;
     while(flipCount != 0)
     {
          cout << "No" << endl;
          flipCount = 0;
          map<int, Edge>::iterator eit;
          for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
          {
               Edge e = eit->second;
               if(!(isDelaunay(Triangulation::edgeTable[e.getIndex()])))
               {
                    flip(Triangulation::edgeTable[e.getIndex()]);
                    flipCount++;
               }
          }
     }
     cout << "Yes" << endl;
     
}

void weightedFlipAlgorithm()
{
     int flipCount = 1;
    // TriangulationCoordinateSystem tcs;
     while(flipCount != 0)
     {
          cout << "No" << endl;
          flipCount = 0;
          map<int, Edge>::iterator eit;
          for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
          {
               //cout << eit->first << "." << endl;
               Edge e = eit->second;
               if(!(isWeightedDelaunay(Triangulation::edgeTable[e.getIndex()])))
               {
                    flip(Triangulation::edgeTable[e.getIndex()]);
                    flipCount++;
               }
          }
          //tcs.generatePlane();
//          tcs.printToFile("Triangulations/ODE Result.txt");
//          system("PAUSE");
     }
     cout << "Yes" << endl;
     
}

void checkTriangle(Edge e, double length1, double length2)
{
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     Vertex vb1, vb2;
     Edge ea1, ea2;
     
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
          if(Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].isBorder() && (*(va1.getLocalEdges()))[i] != e.getIndex())
          {
               ea1 = Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]];
               break;
          }
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
          if(Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].isBorder() && (*(va2.getLocalEdges()))[i] != e.getIndex())
          {
               ea2 = Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]];
               break;
          }
     }
     
     if((*(ea1.getLocalVertices()))[0] == va1.getIndex())
          vb1 = Triangulation::vertexTable[(*(ea1.getLocalVertices()))[1]];
     else
          vb1 = Triangulation::vertexTable[(*(ea1.getLocalVertices()))[0]];
          
     if((*(ea2.getLocalVertices()))[0] == va2.getIndex())
          vb2 = Triangulation::vertexTable[(*(ea2.getLocalVertices()))[1]];
     else
          vb2 = Triangulation::vertexTable[(*(ea2.getLocalVertices()))[0]];

     
     double ang1 = 2 * PI - getAngleSum(va1);
     double ang2 = 2 * PI - getAngleSum(va2);
     
     double tempLength1 = sqrt(pow(length1, 2) + pow(ea1.getLength(), 2) - 2 * ea1.getLength() * length1 * cos(ang1));
     double tempLength2 = sqrt(pow(length2, 2) + pow(ea2.getLength(), 2) - 2 * ea2.getLength() * length2 * cos(ang2));
     
     double tempAng1 = angle(ea1.getLength(), tempLength1, length1);
     double tempAng2 = angle(ea2.getLength(), tempLength2, length2);
     
     if(getAngleSum(vb1) + tempAng1 > 2 * PI){
                         cout << "ANgle\n";
     throw string("angle sum 1");
     }
     if(getAngleSum(vb2) + tempAng2 > 2 * PI){
                         cout << "ANgle\n";
     throw string("angle sum 2");
     }

}

void makeSpecialCase()
{   
    firstTriangle(2.73205080757, 1.0, 1.93185165258);
    addTriangle(Triangulation::edgeTable[3], 1.0, 1.0);
    addTriangle(Triangulation::edgeTable[5], 1.0, 1.0);
    addTriangle(Triangulation::edgeTable[2], Triangulation::edgeTable[7]);
    addTriangle(Triangulation::edgeTable[8], 2.73205080757, 1.0);
    addTriangle(Triangulation::edgeTable[6], Triangulation::edgeTable[10]);
    addTriangle(Triangulation::edgeTable[4], Triangulation::edgeTable[11]);

}


