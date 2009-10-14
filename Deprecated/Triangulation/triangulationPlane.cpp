#include <iostream>
#include <cmath>
#include <fstream>
#include "triangulationplane.h"
#include "length.h"
#include "euc_angle.h"
#include "math/miscmath.h"
#include "utilities.h"
#define PI 	3.141592653589793238

int checkForDoubleTriangles();
bool isDoubleTriangle(Vertex);
int checkFaces();
void firstTriangle(double length1, double length2, double length3)
{
     //first check the arguments for triangle inequality
     if(length1 >= length2 + length3)
     throw string("Invalid Edge Lengths");
     if(length2 >= length1 + length3)
     throw string("Invalid Edge Lengths");
     if(length3 >= length1 + length2)
     throw string("Invalid Edge Lengths");

     //initialize the simplices with proper indices
    Face f1(1);
    Edge e1(1);
    Edge e2(2);
    Edge e3(3);
    Vertex v1(1);
    Vertex v2(2);
    Vertex v3(3);

    //place all the simplices into the triangulation structure
    Triangulation::putFace(1, f1);
    Triangulation::putEdge(1, e1);
    Triangulation::putEdge(2, e2);
    Triangulation::putEdge(3, e3);
    Triangulation::putVertex(1, v1);
    Triangulation::putVertex(2, v2);
    Triangulation::putVertex(3, v3);
    
    //give all of the simplices the proper references to one another
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
    
    //set the dimensions of the triangle
    Length::At(Triangulation::edgeTable[1])->setValue(length1);
    Length::At(Triangulation::edgeTable[2])->setValue(length2);
    Length::At(Triangulation::edgeTable[3])->setValue(length3);        
}

void addTriangle(Edge e, double length1, double length2)
{
     
     //check to make sure the edge is "open"
     if(e.getLocalFaces()->size() > 1)
     throw string("Invalid Edge");
     //check the edge lengths for triangle inequality
     if(Length::valueAt(e) >= (length1 + length2))
     throw string("Invalid Edge Lengths");
     if(length1 >= (Length::valueAt(e) + length2))
     throw string("Invalid Edge Lengths");
     if(length2 >= (Length::valueAt(e) + length1))
     throw string("Invalid Edge Lengths");
     
     //make variable references to existing simplices
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     Face fa = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     
     //make sure the structure remains locally Euclidean
     //checks each vertex to see that the total angle sum 
     //does not exceed 2 * PI
     double anglesum = getAngleSum(va1);
     if(getAngleSum(va1) + angle(Length::valueAt(e), length1, length2) > 2 * PI)
     throw string("angle sum 1");
     
     if(getAngleSum(va2) + angle(Length::valueAt(e), length2, length1) > 2 * PI)
     throw string("angle sum 2");

     //initialize the new simplices with the proper indices
     Vertex vb(Triangulation::greatestVertex() + 1);
     Edge eb1(Triangulation::greatestEdge() + 1);
     Edge eb2(Triangulation::greatestEdge() + 2);
     Face fb(Triangulation::greatestFace() + 1);
     
     //place the new simplices in the triangulation structure
     Triangulation::putVertex(vb.getIndex(), vb);
     Triangulation::putEdge(eb1.getIndex(), eb1);
     Triangulation::putEdge(eb2.getIndex(), eb2);
     Triangulation::putFace(fb.getIndex(), fb);
     
     //make the last variable reference needed
     vector<int> diff;
     diff = listDifference(fa.getLocalVertices(), e.getLocalVertices());
     Vertex va3 = Triangulation::vertexTable[diff[0]];
     
     //arrange the simplex references as necessary
     //vertex-vertex
     Triangulation::vertexTable[vb.getIndex()].addVertex(va1.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addVertex(va2.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addVertex(vb.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addVertex(vb.getIndex());
     //vertex-edge
     Triangulation::vertexTable[vb.getIndex()].addEdge(eb1.getIndex());
     Triangulation::vertexTable[vb.getIndex()].addEdge(eb2.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addEdge(eb1.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addEdge(eb2.getIndex());
     //vertex-face
     Triangulation::vertexTable[vb.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va1.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va2.getIndex()].addFace(fb.getIndex());
     //edge-vertex
     Triangulation::edgeTable[eb1.getIndex()].addVertex(vb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addVertex(va1.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addVertex(vb.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addVertex(va2.getIndex());
     //edge-edge
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
     //edge-face
     Triangulation::edgeTable[e.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb1.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb2.getIndex()].addFace(fb.getIndex());
     //face-vertex
     Triangulation::faceTable[fb.getIndex()].addVertex(va1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(vb.getIndex());
     //face-edge
     Triangulation::faceTable[fb.getIndex()].addEdge(e.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb2.getIndex());
     //face-face
     Triangulation::faceTable[fb.getIndex()].addFace(fa.getIndex());
     Triangulation::faceTable[fa.getIndex()].addFace(fb.getIndex());
     
     //set the dimensions of the new triangle
     Length::At(Triangulation::edgeTable[eb1.getIndex()])->setValue(length1);
     Length::At(Triangulation::edgeTable[eb2.getIndex()])->setValue(length2);
}

void addTriangle(Edge e1, Edge e2)
{
     //checks to make sure the addition is in a valid place
     if(!e1.isAdjEdge(e2.getIndex()))
     throw string("Error: Not adjacent");
     if(!e2.isAdjEdge(e1.getIndex()))
     throw string("Error: Not adjacent");
     if(e1.getLocalFaces()->size() > 1 || e2.getLocalFaces()->size() > 1)
     throw string("Error: Not valid edges");
     
     //initialize the variable references
     Vertex v1, v2, va;
     vector<int> diff, sameAs;
     
     //create the variable references
     diff = listDifference(e1.getLocalVertices(), e2.getLocalVertices());
     v1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(e2.getLocalVertices(), e1.getLocalVertices());
     v2 = Triangulation::vertexTable[diff[0]];
     sameAs = listIntersection(e1.getLocalVertices(), e2.getLocalVertices());
     va = Triangulation::vertexTable[sameAs[0]];
     
     //ensures the concavity of the area where the triangle is being placed
     double ang = 2 * PI - getAngleSum(va);
     if(ang > PI)
     throw string("...");
     
     //initializes the new simplices with proper indices
     Edge eb(Triangulation::greatestEdge() + 1);
     Face fb(Triangulation::greatestFace() + 1);
     Face fa1 = Triangulation::faceTable[(*(e1.getLocalFaces()))[0]];
     Face fa2 = Triangulation::faceTable[(*(e2.getLocalFaces()))[0]];
     Triangulation::putEdge(eb.getIndex(), eb);
     Triangulation::putFace(fb.getIndex(), fb);
     
     //arrange the simplex references as necessary
     //vertex-vertex
     Triangulation::vertexTable[v1.getIndex()].addVertex(v2.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addVertex(v1.getIndex());
     //vertex-edge
     Triangulation::vertexTable[v1.getIndex()].addEdge(eb.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addEdge(eb.getIndex());
     //vertex-face
     Triangulation::vertexTable[v1.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[v2.getIndex()].addFace(fb.getIndex());
     Triangulation::vertexTable[va.getIndex()].addFace(fb.getIndex());
     //edge-vertex
     Triangulation::edgeTable[eb.getIndex()].addVertex(v1.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addVertex(v2.getIndex());
     //edge-edge
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
     //edge-face
     Triangulation::edgeTable[e1.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[e2.getIndex()].addFace(fb.getIndex());
     Triangulation::edgeTable[eb.getIndex()].addFace(fb.getIndex());
     //face-vertex
     Triangulation::faceTable[fb.getIndex()].addVertex(v1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(v2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addVertex(va.getIndex());
     //face-edge
     Triangulation::faceTable[fb.getIndex()].addEdge(e1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(e2.getIndex());
     Triangulation::faceTable[fb.getIndex()].addEdge(eb.getIndex());
     //face-face
     Triangulation::faceTable[fb.getIndex()].addFace(fa1.getIndex());
     Triangulation::faceTable[fb.getIndex()].addFace(fa2.getIndex());
     Triangulation::faceTable[fa1.getIndex()].addFace(fb.getIndex());
     Triangulation::faceTable[fa2.getIndex()].addFace(fb.getIndex());
     
     //use lengths of existing edges to find new dimension
     double l1 = Length::valueAt(e1);
     double l2 = Length::valueAt(e2);
     Length::At(Triangulation::edgeTable[eb.getIndex()])->setValue(sqrt(pow(l1, 2) + pow(l2, 2) - 2 * l1 * l2 * cos(ang)));
     
}

double getAngleSum(Vertex v)
{
       //finds the sum of all angles at a given vertex
       double angleSum;
       for(int i = 0; i < v.getLocalFaces()->size(); i++)
       {
               if(!Triangulation::faceTable[(*(v.getLocalFaces()))[i]].isNegative())
                    angleSum += EuclideanAngle::valueAt(v, Triangulation::faceTable[(*(v.getLocalFaces()))[i]]);
               else
                    angleSum -= EuclideanAngle::valueAt(v, Triangulation::faceTable[(*(v.getLocalFaces()))[i]]);
       }
       return angleSum;
}

void generateTriangulation(int numFaces)
{
     //initialize the iterator for the triangulation and random number generator
     map<int, Edge>::iterator eit;
     srand(time(NULL));
     
     //initialize the variables needed to create the dimensions for the triangulation
     double randNum, length1, length2, length3, range, rDec;
     //use a large number (20,000) to get a random number in the interval (0,1]
     // then multiply by ten, creating random dimensions between 1 and 10
     randNum = (rand()%20000 + 1) / 20000.0;
     length1 = randNum *10 + 1;
     randNum = (rand()%20000 + 1) / 20000.0;
     length2 = randNum*10 + 1;
     //use a range variable ....
     //
     //need to ask Mitch about this convoluted piece of code
     //
     range = (length1 + length2 - 1) - (abs(length1 - length2) + 1);
     int rInt = (int) range;
     rDec = range - rInt;
     randNum = (rand()%20000 + 1) / 20000.0;
     length3 = rand()%(rInt + 1) + (abs(length1 - length2) + 1) + randNum * rDec;

     //make the first triangle in the plane
     firstTriangle(length1, length2, length3);

     //use a loop to generate a planar triangulation with random lengths
     // and a number of faces indicated by the argument
     while(Triangulation::faceTable.size() < numFaces)
     {
         for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
         {
            Edge e = eit->second;
            if(e.isBorder())
            {
                length1 = Length::valueAt(e);
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

void generateRadii()
{
     srand(time(NULL));
     map<int, Vertex>::iterator vit;
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
     {
          int index = (*(vit->second.getLocalEdges()))[0];
          double smallest = Length::valueAt(Triangulation::edgeTable[index]);
          for(int i = 1; i < vit->second.getLocalEdges()->size(); i++)
          {
               index = (*(vit->second.getLocalEdges()))[i];
               if(Length::valueAt(Triangulation::edgeTable[index]) < smallest)
               smallest = Length::valueAt(Triangulation::edgeTable[index]);
          }
          double randNum = ((double) rand()) / RAND_MAX;
          
          Radius::At(vit->second)->setValue(randNum * smallest * 1);
     }
}

void flipAlgorithm()
{
     int passCount = 0;
     int flipCount = 1;
     while(flipCount != 0)
     {
          passCount++;
          cout << "Attempt #" << passCount << endl;
          flipCount = 0;
          map<int, Edge>::iterator eit;
          for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
          {
               Edge e = eit->second;
               if(!(isDelaunay(Triangulation::edgeTable[e.getIndex()])))
               {
                   // flip(Triangulation::edgeTable[e.getIndex()]);
                    flipCount++;
               }
          }
     }
     cout << "Finished" << endl;
     
}

void weightedFlipAlgorithm()
{
     int passCount = 0;
     int flipCount = 1;
     int count = 0;
     vector<int> remVerts;
     TriangulationCoordinateSystem tcs;
     tcs.generatePlane();
     tcs.printToFile("Triangulations/flips/Step 0.txt");
     writeTriangulationFile("Triangulations/flips/Figure 0.txt");
     while(flipCount != 0)
     {
          passCount++;
          cout << "Attempt #" << passCount << endl;
          flipCount = 0;
          map<int, Edge>::iterator eit;
          for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
          {
               Edge e = eit->second;
               if(!(isWeightedDelaunay(e)))
               {
                    cout << "Edge# " << eit->first << " Count: " << count << endl;                                                           
                    try
                    {
                         //flip(e);
                         flipCount++;
                         count++;
                         char s[100];
                         char s2[100];
                         sprintf(s, "Triangulations/flips/Step %d.txt", count);
                         sprintf(s2, "Triangulations/flips/Figure %d.txt", count);
                         char countCh = (char) count;
                         tcs.update();
                         tcs.printToFile(s);
                         writeTriangulationFile(s2);

                         int vertex = checkForDoubleTriangles();
                         if(vertex > 0)
                         {
                            cout << "Double triangle at: " << vertex << "\n";
                         }
                         int face = checkFaces();
                         if(face > 0)
                         {
                            cout << "Face trouble at: " << face << "\n";
                         }
                    }
                    catch(string s1)
                    {
                         cout << "####################################################################" << endl;
                         Edge e = eit->second;
                         Face f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
                         Face f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
                         cout << "Edge #" << e.getIndex() << "(";
                         cout << (*(e.getLocalVertices()))[0] << ", ";
                         cout << (*(e.getLocalVertices()))[1] << ")" << endl;
                         cout << "Count: " << count << "\n";
                         cout << "Dual: " << getDual(e) << endl;
                         cout << "Height 1: " << getHeight(f1, e) << endl;
                         cout << "Height 2: " << getHeight(f2, e) << endl;
                         cout << "Face 1: (";
                         cout << (*(f1.getLocalVertices()))[0] << ", ";
                         cout << (*(f1.getLocalVertices()))[1] << ", ";
                         cout << (*(f1.getLocalVertices()))[2] << ") ";
                         cout << f1.isNegative() << "   ";
                         cout << "[" <<(*(f1.getLocalEdges()))[0] << ": ";
                         cout << Length::valueAt(Triangulation::edgeTable[(*(f1.getLocalEdges()))[0]]) << ", ";
                         cout << (*(f1.getLocalEdges()))[1] << ": ";
                         cout << Length::valueAt(Triangulation::edgeTable[(*(f1.getLocalEdges()))[1]]) << ", ";
                         cout << (*(f1.getLocalEdges()))[2] << ": ";
                         cout << Length::valueAt(Triangulation::edgeTable[(*(f1.getLocalEdges()))[2]]) << "]";
                         cout << endl;
                         cout << "Face 2: (";
                         cout << (*(f2.getLocalVertices()))[0] << ", ";
                         cout << (*(f2.getLocalVertices()))[1] << ", ";
                         cout << (*(f2.getLocalVertices()))[2] << ") ";
                         cout << f2.isNegative() << "   ";
                         cout << "[" <<(*(f2.getLocalEdges()))[0] << ": ";
                         cout << Length::valueAt(Triangulation::edgeTable[(*(f2.getLocalEdges()))[0]]) << ", ";
                         cout << (*(f2.getLocalEdges()))[1] << ": ";
                         cout << Length::valueAt(Triangulation::edgeTable[(*(f2.getLocalEdges()))[1]]) << ", ";
                         cout << (*(f2.getLocalEdges()))[2] << ": ";
                         cout << Length::valueAt(Triangulation::edgeTable[(*(f2.getLocalEdges()))[2]]) << "]";
                         cout << endl;
                         //pause(); // PAUSE
                         flipCount++;
                    }
               }
          }


     }
     
     cout << "Finished" << endl;

     cout << "Total num flips: " << count << "\n";

     vector<int> remverts;
     map<int, Vertex>::iterator vit;
     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
     {
          Vertex v = vit->second;
          if(getAngleSum(v) < 0.0001 && v.getLocalFaces()->size() == 2)
          {
               remverts.push_back(vit->first);
          }
     }
     
     for(int i = 0; i < remverts.size(); i++)
     {
          removeDoubleTriangle(Triangulation::vertexTable[remverts[i]]);
     }
     
     if(remverts.size() > 0)
     {
          char s[100];     
          char s2[100];
          count++;
          sprintf(s, "Triangulations/flips/Step %d.txt", count);
          sprintf(s2, "Triangulations/flips/Figure %d.txt", count);
          tcs.update();
          tcs.printToFile(s);
          writeTriangulationFile(s2);
          cout << remverts.size() << " vertices removed" << endl;
     }

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
     
     double tempLength1 = sqrt(pow(length1, 2) + pow(Length::valueAt(ea1), 2) - 2 * Length::valueAt(ea1) * length1 * cos(ang1));
     double tempLength2 = sqrt(pow(length2, 2) + pow(Length::valueAt(ea2), 2) - 2 * Length::valueAt(ea2) * length2 * cos(ang2));
     
     double tempAng1 = angle(Length::valueAt(ea1), tempLength1, length1);
     double tempAng2 = angle(Length::valueAt(ea2), tempLength2, length2);
     
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
     //creates the canonical example using carefully chosen radii and lengths
     firstTriangle(1.0, 1.0, 1.0);
     addTriangle(Triangulation::edgeTable[1], 1.0, 1.97387802015);
     addTriangle(Triangulation::edgeTable[2], 1.97387802015, 1.0);
     addTriangle(Triangulation::edgeTable[3], 1.0, 1.97387802015);
     addTriangle(Triangulation::edgeTable[4], Triangulation::edgeTable[6]);
     addTriangle(Triangulation::edgeTable[7], Triangulation::edgeTable[9]);
     addTriangle(Triangulation::edgeTable[8], Triangulation::edgeTable[5]);
     Radius::At(Triangulation::vertexTable[1])->setValue(0);
     Radius::At(Triangulation::vertexTable[2])->setValue(0);
     Radius::At(Triangulation::vertexTable[3])->setValue(0);
     Radius::At(Triangulation::vertexTable[4])->setValue(1.5066);
     Radius::At(Triangulation::vertexTable[5])->setValue(1.5066);
     Radius::At(Triangulation::vertexTable[6])->setValue(1.5066);     
}

void removeDoubleTriangle(Vertex vb)
{
     //checks for valid choice
     if(vb.getLocalFaces()->size() != 2 || getAngleSum(vb) > 0.0001)
     throw string("Invalid Operation");
     
     //creates reference variables
     Face f1 = Triangulation::faceTable[(*(vb.getLocalFaces()))[0]];
     Face f2 = Triangulation::faceTable[(*(vb.getLocalFaces()))[1]];
     
     vector<int> diff, sameAs;
     diff = listDifference(f1.getLocalEdges(), f2.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[diff[0]];
     diff = listDifference(f2.getLocalEdges(), f1.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[diff[0]];
     
     Vertex va1 = Triangulation::vertexTable[(*(ea1.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(ea1.getLocalVertices()))[1]];
     
     sameAs = listIntersection(vb.getLocalEdges(), va1.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(vb.getLocalEdges(), va2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     
     Face fa1, fa2;
     
     //check for instances of being on the border, and thus whether or not
     //additional reference variables are necessary
     //in whichever case, it is necessary to rearrange certain simplices
     if(!ea1.isBorder())
     {
          if((*(ea1.getLocalFaces()))[0] == f1.getIndex())
               Face fa1 = Triangulation::faceTable[(*(ea1.getLocalFaces()))[1]];
          else
               Face fa1 = Triangulation::faceTable[(*(ea1.getLocalFaces()))[0]];
     }
     
     if(!ea2.isBorder())
     {
          if((*(ea2.getLocalFaces()))[0] == f2.getIndex())
               Face fa2 = Triangulation::faceTable[(*(ea2.getLocalFaces()))[1]];
          else
               Face fa2 = Triangulation::faceTable[(*(ea2.getLocalFaces()))[0]];
          
          Triangulation::edgeTable[ea1.getIndex()].addFace(fa2.getIndex());
          Triangulation::faceTable[fa2.getIndex()].addEdge(ea1.getIndex());
          Triangulation::edgeTable[ea1.getIndex()].removeFace(f1.getIndex());
     }
     
     if(!(ea1.isBorder() || ea2.isBorder())) 
     {
          Triangulation::faceTable[fa1.getIndex()].addFace(fa2.getIndex());
          Triangulation::faceTable[fa2.getIndex()].addFace(fa1.getIndex());
     }
     
     //remove all simplices on the double triangle 
     Triangulation::eraseVertex(vb.getIndex());
     Triangulation::eraseEdge(eb1.getIndex());
     Triangulation::eraseEdge(eb2.getIndex());
     Triangulation::eraseEdge(ea2.getIndex());
     Triangulation::eraseFace(f1.getIndex());
     Triangulation::eraseFace(f2.getIndex());     
     
}

void writeYuliyaFile(char* fileName)
{
     ofstream output(fileName);
     map<int, Edge>::iterator eit;
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
     {
          output << (*(eit->second.getLocalVertices()))[0] << " ";
          output << (*(eit->second.getLocalVertices()))[1] << "; ";
          
          int f1, f2, v1, v2;
          vector<int> diff;
          
          f1 = (*(eit->second.getLocalFaces()))[0];
          diff = listDifference(Triangulation::faceTable[f1].getLocalVertices(), eit->second.getLocalVertices());
          v1 = diff[0];
          output << v1;
          if(Triangulation::faceTable[f1].isNegative())
               output << "*";
          output << " ";
          
          if(!eit->second.isBorder())
          {
               f2 = (*(eit->second.getLocalFaces()))[1];
               diff = listDifference(Triangulation::faceTable[f2].getLocalVertices(), eit->second.getLocalVertices());
               v2 = diff[0];
               output << v2;
               if(Triangulation::faceTable[f2].isNegative())
                    output << "*";
          }
          
          output << endl;
          
     }
        
     
}

int checkForDoubleTriangles()
{
         map<int, Vertex>::iterator vit;
         for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
         {
            if(isDoubleTriangle(vit->second)) 
            {
               return vit->first;
            }
         }
         return -1;
}
     
bool isDoubleTriangle(Vertex v)
{
          if(v.getDegree() != 2)
          {
             return false;
          }
          Edge e1 = Triangulation::edgeTable[(*(v.getLocalEdges()))[0]];
          Edge e2 = Triangulation::edgeTable[(*(v.getLocalEdges()))[1]];
          vector<int> faces1= *(e1.getLocalFaces());
          vector<int> faces2= *(e2.getLocalFaces());
          if(!(e1.isBorder() || e2.isBorder())) 
          {
             if((faces1[0] == faces2[0] || faces1[0] == faces2[1]) &&
                  (faces1[1] == faces2[0] || faces1[1] == faces2[1]))
             {
                  Face f1 = Triangulation::faceTable[faces1[0]];
                  Face f2 = Triangulation::faceTable[faces1 [1]];
                  if(f1.isNegative() == f2.isNegative())
                     return true;
             }
          }
          return false;                             
}

int checkFaces()
{
         map<int, Face>::iterator fit;
         for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
         {
            if(fit->second.getLocalVertices()->size() == 2)
            {
               return fit->first;
            }
         }
         return -1;
}
