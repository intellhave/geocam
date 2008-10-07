/**************************************************************
File: Triangulation Morph
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 20, 2008
***************************************************************
The Triangulation Morph file holds the functions that manipulates
the Triangulation in some way.
**************************************************************/
#include <cmath>
#include <iostream>
#include "triangulationmorph.h"
#define PI 	3.141592653589793238

void addNewVertex(Face f, double newRadius)
{
     
     //start giving names to the existing simplices that are involved in this procedure
     Vertex va1 = Triangulation::vertexTable[(*(f.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(f.getLocalVertices()))[1]];
     Vertex va3 = Triangulation::vertexTable[(*(f.getLocalVertices()))[2]];
     
     vector<int> sameAs;
     
     sameAs = listIntersection(va1.getLocalEdges(), va2.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), va3.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), va3.getLocalEdges());
     Edge ea3 = Triangulation::edgeTable[sameAs[0]];
     
     vector<Face> fa1, fa2, fa3;
     sameAs = listIntersection(ea1.getLocalFaces(), f.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa1.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(ea2.getLocalFaces(), f.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa2.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(ea3.getLocalFaces(), f.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa3.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     
     //the create the new simplices needed, using new indices
     Vertex vb;
     vb.setIndex(Triangulation::greatestVertex() + 1);
     
     Edge eb1(Triangulation::greatestEdge() + 1);
     Edge eb2(Triangulation::greatestEdge() + 2);
     Edge eb3(Triangulation::greatestEdge() + 3);
     Face fb1(Triangulation::greatestFace() + 1);
     Face fb2(Triangulation::greatestFace() + 2);
     
     //finally, add the new simplices to the triangulation and give the new vertex a radius
     Triangulation::putVertex(vb.getIndex(), vb);
     Triangulation::putEdge(eb1.getIndex(), eb1);
     Triangulation::putEdge(eb2.getIndex(), eb2);
     Triangulation::putEdge(eb3.getIndex(), eb3);
     Triangulation::putFace(fb1.getIndex(), fb1);
     Triangulation::putFace(fb2.getIndex(), fb2);
     
     //then adjust all of the references
     Triangulation::vertexTable[(va3.getIndex())].removeFace(f.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].removeFace(f.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].removeFace(f.getIndex());
     Triangulation::faceTable[(f.getIndex())].removeVertex(va3.getIndex());
     Triangulation::faceTable[(f.getIndex())].removeEdge(ea2.getIndex());
     Triangulation::faceTable[(f.getIndex())].removeEdge(ea3.getIndex());
     for(int i = 0; i < fa2.size(); i++)
     {
       Triangulation::faceTable[(f.getIndex())].removeFace(fa2[i].getIndex());
       Triangulation::faceTable[(fa2[i].getIndex())].removeFace(f.getIndex());  
     }
     for(int i = 0; i < fa3.size(); i++)
     {
       Triangulation::faceTable[(f.getIndex())].removeFace(fa3[i].getIndex());
       Triangulation::faceTable[(fa3[i].getIndex())].removeFace(f.getIndex());
     }
     
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addVertex(va3.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb2.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb3.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addEdge(eb1.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addEdge(eb2.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addEdge(eb3.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addFace(f.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb1.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb1.getIndex())].addVertex(vb.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addVertex(vb.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addVertex(va3.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addVertex(vb.getIndex());
     
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(eb1.getIndex())].addEdge((*(va1.getLocalEdges()))[i]);
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].addEdge(eb1.getIndex());
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(eb2.getIndex())].addEdge((*(va2.getLocalEdges()))[i]);
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].addEdge(eb2.getIndex());
     }
     for(int i = 0; i < va3.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(eb3.getIndex())].addEdge((*(va3.getLocalEdges()))[i]);
             Triangulation::edgeTable[(*(va3.getLocalEdges()))[i]].addEdge(eb3.getIndex());
     }
     Triangulation::edgeTable[(eb1.getIndex())].addEdge(eb2.getIndex());
     Triangulation::edgeTable[(eb1.getIndex())].addEdge(eb3.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addEdge(eb1.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addEdge(eb3.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addEdge(eb1.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addEdge(eb2.getIndex());
     Triangulation::edgeTable[(eb1.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb1.getIndex())].addFace(f.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addFace(f.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va3.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(va2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(va3.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(vb.getIndex());
     Triangulation::faceTable[(f.getIndex())].addVertex(vb.getIndex());
     Triangulation::faceTable[(f.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(f.getIndex())].addEdge(eb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb3.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb3.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(ea3.getIndex());
     Triangulation::faceTable[(f.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(f.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(f.getIndex());
     for(int i = 0; i < fa2.size(); i++)
     {
       Triangulation::faceTable[(fb1.getIndex())].addFace(fa2[i].getIndex());
       Triangulation::faceTable[(fa2[i].getIndex())].addFace(fb1.getIndex());
     }
     for(int i = 0; i < fa3.size(); i++)
     {
        Triangulation::faceTable[(fb2.getIndex())].addFace(fa3[i].getIndex());
        Triangulation::faceTable[(fa3[i].getIndex())].addFace(fb2.getIndex());
     }
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(f.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb1.getIndex());
     
     
     
     Triangulation::vertexTable[vb.getIndex()].setRadius(newRadius);
     
}

void flip(Edge e)
{
     
     //start out by naming every object that is local to the flip
     Face f1 = Triangulation::faceTable[(*(e.getLocalFaces()))[0]];
     Face f2 = Triangulation::faceTable[(*(e.getLocalFaces()))[1]];
     
     vector<int> sameAs;
     vector<int> diff;
     
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
          
     diff = listDifference(f1.getLocalVertices(), e.getLocalVertices());
     if(diff.size() == 0){
     throw string("Invalid move, operation cancelled");
     }
     Vertex vb1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(f2.getLocalVertices(), e.getLocalVertices());
     Vertex vb2 = Triangulation::vertexTable[diff[0]];
     
     sameAs = listIntersection(va1.getLocalEdges(), vb1.getLocalEdges());
     sameAs = listIntersection(&sameAs, f1.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb1.getLocalEdges());
     sameAs = listIntersection(&sameAs, f1.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), vb2.getLocalEdges());
     sameAs = listIntersection(&sameAs, f2.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb2.getLocalEdges());
     sameAs = listIntersection(&sameAs, f2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     
     double ang1, ang2;
     if(f1.isNegative() && (!f2.isNegative())) 
     {
        ang1 = (-angle(va1, f1)) + angle(va1, f2);
        ang2 = (-angle(va2, f1)) + angle(va2, f2);
        if(ang1 < 0 && ang2 < 0)
        {
           Triangulation::faceTable[f2.getIndex()].switchSide();
        } else if(ang1 > 0 && ang2 > 0)
        {
            Triangulation::faceTable[f1.getIndex()].switchSide();
        } else if (ang2 < 0)
        {
            Triangulation::faceTable[f1.getIndex()].switchSide();
            Triangulation::faceTable[f2.getIndex()].switchSide();
        }
     } else if((!f1.isNegative()) && f2.isNegative())
     {
        ang1 = angle(va1, f1) - angle(va1, f2);
        ang2 = angle(va2, f1) - angle(va2, f2);
        if(ang1 < 0 && ang2 < 0)
        {
           Triangulation::faceTable[f1.getIndex()].switchSide();
        } else if(ang1 > 0 && ang2 > 0)
        {
            Triangulation::faceTable[f2.getIndex()].switchSide();
        }  else if (ang1 < 0)
        {
            Triangulation::faceTable[f1.getIndex()].switchSide();
            Triangulation::faceTable[f2.getIndex()].switchSide();
        }
     } else
     {
        ang1 = angle(va1, f1) + angle(va1, f2);
        ang2 = angle(va2, f1) + angle(va2, f2);
        if(ang1 > PI)
        {//
//           cout << "Angle1: " << ang1 << " f1: " << f1.isNegative();
//           cout << " f2: " << f2.isNegative() << "\n";
           Triangulation::faceTable[f1.getIndex()].switchSide();
           f1 = Triangulation::faceTable[f1.getIndex()];
//           cout << "f1: " << f1.isNegative() << "\n";
           ang1 = 2 * PI - ang1;
        }
        else if(ang2 > PI)
        {
//           cout << "Angle2: " << ang2 << " f1: " << f1.isNegative();
//           cout << " f2: " << f2.isNegative() << "\n";
           Triangulation::faceTable[f2.getIndex()].switchSide();
           f2 = Triangulation::faceTable[f2.getIndex()];
//           cout << "f2: " << f2.isNegative() << "\n";   
        }
     }

     ang1 = abs(ang1);
     
     
     vector<Face> fa1, fb1, fa2, fb2;
     sameAs = listIntersection(f1.getLocalFaces(), ea1.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa1.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(f1.getLocalFaces(), eb1.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fb1.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(f2.getLocalFaces(), ea2.getLocalFaces());
       for(int i = 0; i < sameAs.size(); i++)
     {
        fa2.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(f2.getLocalFaces(), eb2.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fb2.push_back(Triangulation::faceTable[sameAs[i]]);
     }

     
     //the next task is naturally to rearrange all the necessary references
     //there are no new references added, so the task  is simply made up
     //of working with the objects that already exist that have newly
     //created references made for this method above
     //the procedure follows a very methodical and categorical structure
     //done first on paper and then transferred to code
     //removals
     
     Triangulation::vertexTable[(va1.getIndex())].removeVertex(va2.getIndex()); 
     Triangulation::vertexTable[(va2.getIndex())].removeVertex(va1.getIndex()); 
     Triangulation::vertexTable[(va1.getIndex())].removeEdge(e.getIndex()); 
     Triangulation::vertexTable[(va2.getIndex())].removeEdge(e.getIndex()); 
     Triangulation::vertexTable[(va1.getIndex())].removeFace(f2.getIndex()); 
     Triangulation::vertexTable[(va2.getIndex())].removeFace(f1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].removeVertex(va1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].removeVertex(va2.getIndex()); 
     for(int i = 0; i < e.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(e.getIndex())].removeEdge((*(e.getLocalEdges()))[i]); 
     }
     for(int i = 0; i < va1.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].removeEdge(e.getIndex()); 
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].removeEdge(e.getIndex());  
     }
     Triangulation::edgeTable[(eb1.getIndex())].removeFace(f1.getIndex()); 
     Triangulation::edgeTable[(ea2.getIndex())].removeFace(f2.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].removeVertex(va2.getIndex()); 
     Triangulation::faceTable[(f2.getIndex())].removeVertex(va1.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].removeEdge(eb1.getIndex()); 
     Triangulation::faceTable[(f2.getIndex())].removeEdge(ea2.getIndex());
     for(int i = 0; i < fb1.size(); i++)
     {
       Triangulation::faceTable[(f1.getIndex())].removeFace(fb1[i].getIndex());
       Triangulation::faceTable[(fb1[i].getIndex())].removeFace(f1.getIndex());      
     }
     for(int i = 0; i < fa2.size(); i++)
     {
       Triangulation::faceTable[(f2.getIndex())].removeFace(fa2[i].getIndex());
       Triangulation::faceTable[(fa2[i].getIndex())].removeFace(f2.getIndex());      
     }
     
     //additions
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb2.getIndex()); 
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb1.getIndex()); 
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(e.getIndex()); 
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(e.getIndex()); 
     Triangulation::vertexTable[(vb1.getIndex())].addFace(f2.getIndex()); 
     Triangulation::vertexTable[(vb2.getIndex())].addFace(f1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].addVertex(vb1.getIndex()); 
     Triangulation::edgeTable[(e.getIndex())].addVertex(vb2.getIndex()); 
     for(int i = 0; i < vb1.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(e.getIndex())].addEdge((*(vb1.getLocalEdges()))[i]); 
             Triangulation::edgeTable[(*(vb1.getLocalEdges()))[i]].addEdge(e.getIndex()); 
     }
     for(int i = 0; i < vb2.getLocalEdges()->size(); i ++)
     {
             Triangulation::edgeTable[(e.getIndex())].addEdge((*(vb2.getLocalEdges()))[i]); 
             Triangulation::edgeTable[(*(vb2.getLocalEdges()))[i]].addEdge(e.getIndex()); 
     }
     Triangulation::edgeTable[(ea2.getIndex())].addFace(f1.getIndex()); 
     Triangulation::edgeTable[(eb1.getIndex())].addFace(f2.getIndex()); 
     Triangulation::faceTable[(f1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(f2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(f1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(f2.getIndex())].addEdge(eb1.getIndex());
     for(int i = 0; i < fa2.size(); i++)
     {
         Triangulation::faceTable[(f1.getIndex())].addFace(fa2[i].getIndex());
         Triangulation::faceTable[(fa2[i].getIndex())].addFace(f1.getIndex());
     }
      for(int i = 0; i < fb1.size(); i++)
     {
         Triangulation::faceTable[(f2.getIndex())].addFace(fb1[i].getIndex());
         Triangulation::faceTable[(fb1[i].getIndex())].addFace(f2.getIndex());
     }
     
     double l1 = ea1.getLength();
     double l2 = ea2.getLength();
//     cout << "f1: " << f1.getIndex();
//     cout << "  f2: " << f2.getIndex() << "\n";
//     ea1 = Triangulation::edgeTable[ea1.getIndex()];
//     ea2 = Triangulation::edgeTable[ea2.getIndex()];
//     eb1 = Triangulation::edgeTable[eb1.getIndex()];
//     eb2 = Triangulation::edgeTable[eb2.getIndex()];
//     f1 = Triangulation::faceTable[f1.getIndex()];
//     f2 = Triangulation::faceTable[f2.getIndex()];
//     if(!(ea1.isBorder() || ea2.isBorder()))
//     {
//         vector<int> facesA1 = *(ea1.getLocalFaces());
//         vector<int> facesA2 = *(ea2.getLocalFaces());
//         cout << "FA1_1: " << facesA1[0];
//         cout << "  FA1_2: " << facesA1[1];
//         cout << "  FA2_1: " << facesA2[0];
//         cout << "  FA2_2: " << facesA2[1];
//         cout << "\n";
//         if((facesA1[0] == facesA2[0] || facesA1[0] == facesA2[1]) &&
//              (facesA1[1] == facesA2[0] || facesA1[1] == facesA2[1]))
//         {
//              int otherFace = facesA1[0] == f1.getIndex() ? facesA1[1] : facesA1[0];
//              Face other = Triangulation::faceTable[otherFace];
//              cout << "f1 is negative?: " << f1.isNegative();
//              cout << "  Face " << otherFace << " is negative?: ";
//              cout << other.isNegative() << endl;
//              cout << "Edge " << e.getIndex() << " length: " << sqrt(pow(l1, 2) + pow(l2, 2) - 2 * l1 * l2 * cos(ang1));
//              sameAs = listDifference(other.getLocalEdges(), f1.getLocalEdges());
//              Edge otherEdge = Triangulation::edgeTable[sameAs[0]];
//              cout << "  Other edge's length: " << otherEdge.getLength() << "\n";
//         }
//     }
//     if(!(eb1.isBorder() || eb2.isBorder()))
//     {
//         vector<int> facesB1 = *(eb1.getLocalFaces());
//         vector<int> facesB2 = *(eb2.getLocalFaces());
//         cout << "FB1_1: " << facesB1[0];
//         cout << "  FB1_2: " << facesB1[1];
//         cout << "  FB2_1: " << facesB2[0];
//         cout << "  FB2_2: " << facesB2[1];
//         cout << "\n";
//         if((facesB1[0] == facesB2[0] || facesB1[0] == facesB2[1]) &&
//              (facesB1[1] == facesB2[0] || facesB1[1] == facesB2[1]))
//         {
//              int otherFace = facesB1[0] == f2.getIndex() ? facesB1[1] : facesB1[0];
//              Face other = Triangulation::faceTable[otherFace];
//              cout << "f2 is negative?: " << f2.isNegative();
//              cout << "  Face " << otherFace << " is negative?: ";
//              cout << other.isNegative() << endl;
//              cout << "Edge " << e.getIndex() << " length: " << sqrt(pow(l1, 2) + pow(l2, 2) - 2 * l1 * l2 * cos(ang1));
//              sameAs = listDifference(other.getLocalEdges(), f2.getLocalEdges());
//              Edge otherEdge = Triangulation::edgeTable[sameAs[0]];
//              cout << "  Other edge's length: " << otherEdge.getLength() << "\n";
//         }
//     } 
  
     
     Triangulation::edgeTable[e.getIndex()].setLength(sqrt(pow(l1, 2) + pow(l2, 2) - 2 * l1 * l2 * cos(ang1)));
}

void removeVertex(Vertex v)
{
     
     if(v.getLocalVertices()->size() != 3)
     throw string("Invalid move, operation cancelled");
     
     Vertex va1 = Triangulation::vertexTable[(*(v.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(v.getLocalVertices()))[1]];
     Vertex va3 = Triangulation::vertexTable[(*(v.getLocalVertices()))[2]];
     
     vector<int> sameAs;
     
     sameAs = listIntersection(v.getLocalEdges(), va1.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(v.getLocalEdges(), va2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(v.getLocalEdges(), va3.getLocalEdges());
     Edge eb3 = Triangulation::edgeTable[sameAs[0]];     
     sameAs = listIntersection(va1.getLocalEdges(), va2.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), va3.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), va3.getLocalEdges());
     Edge ea3 = Triangulation::edgeTable[sameAs[0]];
     
     
     sameAs = listIntersection(eb1.getLocalFaces(), eb2.getLocalFaces());
     Face fb1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(eb1.getLocalFaces(), eb3.getLocalFaces());
     Face fb2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(eb2.getLocalFaces(), eb3.getLocalFaces());
     Face fb3 = Triangulation::faceTable[sameAs[0]];
     
     vector<Face> fa1, fa2, fa3;
     sameAs = listIntersection(fb1.getLocalFaces(), ea1.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa1.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(fb2.getLocalFaces(), ea2.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa2.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     sameAs = listIntersection(fb3.getLocalFaces(), ea3.getLocalFaces());
     for(int i = 0; i < sameAs.size(); i++)
     {
        fa3.push_back(Triangulation::faceTable[sameAs[i]]);
     }
     
     //Triangulation::vertexTable[(va1.getIndex())].removeVertex(v.getIndex());
//     Triangulation::vertexTable[(va2.getIndex())].removeVertex(v.getIndex());
//     Triangulation::vertexTable[(va3.getIndex())].removeVertex(v.getIndex());
//     Triangulation::vertexTable[(va1.getIndex())].removeEdge(eb1.getIndex());
//     Triangulation::vertexTable[(va2.getIndex())].removeEdge(eb2.getIndex());
//     Triangulation::vertexTable[(va3.getIndex())].removeEdge(eb3.getIndex());
//     Triangulation::vertexTable[(va1.getIndex())].removeFace(fb2.getIndex());
//     Triangulation::vertexTable[(va2.getIndex())].removeFace(fb3.getIndex());
//     Triangulation::vertexTable[(va3.getIndex())].removeFace(fb2.getIndex());
//     Triangulation::vertexTable[(va3.getIndex())].removeFace(fb3.getIndex());
//     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
//     {
//             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].removeEdge(eb1.getIndex());
//     }
//     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
//     {
//             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].removeEdge(eb2.getIndex());
//     }
//     for(int i = 0; i < va3.getLocalEdges()->size(); i++)
//     {
//             Triangulation::edgeTable[(*(va3.getLocalEdges()))[i]].removeEdge(eb3.getIndex());
//     }
//     Triangulation::edgeTable[(ea2.getIndex())].removeFace(fb2.getIndex());
//     Triangulation::edgeTable[(ea3.getIndex())].removeFace(fb3.getIndex());
//     Triangulation::faceTable[(fb1.getIndex())].removeVertex(v.getIndex());
//     Triangulation::faceTable[(fb1.getIndex())].removeEdge(eb1.getIndex());
//     Triangulation::faceTable[(fb1.getIndex())].removeEdge(eb2.getIndex());
//     Triangulation::faceTable[(fa2.getIndex())].removeFace(fb2.getIndex());
//     Triangulation::faceTable[(fa3.getIndex())].removeFace(fb3.getIndex());
//     Triangulation::faceTable[(fb1.getIndex())].removeFace(fb2.getIndex());
//     Triangulation::faceTable[(fb1.getIndex())].removeFace(fb3.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va3.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea3.getIndex());
     for(int i = 0; i < fa2.size(); i++)
     {
        Triangulation::faceTable[(fb1.getIndex())].addFace(fa2[i].getIndex());
        Triangulation::faceTable[(fa2[i].getIndex())].addFace(fb1.getIndex());  
     }
     for(int i = 0; i < fa3.size(); i++)
     {
        Triangulation::faceTable[(fb1.getIndex())].addFace(fa3[i].getIndex());
        Triangulation::faceTable[(fa3[i].getIndex())].addFace(fb1.getIndex());
     }
     
     
     Triangulation::eraseVertex(v.getIndex());
     Triangulation::eraseEdge(eb1.getIndex());
     Triangulation::eraseEdge(eb2.getIndex());
     Triangulation::eraseEdge(eb3.getIndex());
     Triangulation::eraseFace(fb2.getIndex());
     Triangulation::eraseFace(fb3.getIndex());
     
         
}

void addLeaf(Edge e, double newRadius)
{
     Edge eb(Triangulation::greatestEdge() + 1);
     Edge ea1(Triangulation::greatestEdge() + 2);
     Edge ea2(Triangulation::greatestEdge() + 3);
     Vertex vb(Triangulation::greatestVertex() + 1);
     Face fb1(Triangulation::greatestFace() + 1);
     Face fb2(Triangulation::greatestFace() + 2);
     
     Vertex va1 = Triangulation::vertexTable[(*(e.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(e.getLocalVertices()))[1]];
     
     vector<int> sameAs = listIntersection(va1.getLocalFaces(), e.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     Face fa2 = Triangulation::faceTable[sameAs[1]];
     
     Triangulation::putEdge(eb.getIndex(), eb);
     Triangulation::putEdge(ea1.getIndex(), ea1);
     Triangulation::putEdge(ea2.getIndex(), ea2);
     Triangulation::putVertex(vb.getIndex(), vb);
     Triangulation::putFace(fb1.getIndex(), fb1);
     Triangulation::putFace(fb2.getIndex(), fb2);
     
     Triangulation::edgeTable[(e.getIndex())].removeFace(fa2.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].removeEdge(e.getIndex());
     Triangulation::faceTable[(fa1.getIndex())].removeFace(fa2.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].removeFace(fa1.getIndex());
     
     Triangulation::vertexTable[(vb.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(ea1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(ea2.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addEdge(ea1.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addEdge(ea2.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addVertex(vb.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addVertex(vb.getIndex());
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(eb.getIndex())].addEdge((*(va1.getLocalEdges()))[i]);
             Triangulation::edgeTable[(ea1.getIndex())].addEdge((*(va1.getLocalEdges()))[i]);
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].addEdge(eb.getIndex());
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].addEdge(ea1.getIndex());
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(eb.getIndex())].addEdge((*(va2.getLocalEdges()))[i]);
             Triangulation::edgeTable[(ea2.getIndex())].addEdge((*(va2.getLocalEdges()))[i]);
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].addEdge(eb.getIndex());
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].addEdge(ea2.getIndex());
     }
     Triangulation::edgeTable[(eb.getIndex())].addEdge(e.getIndex());
     Triangulation::edgeTable[(eb.getIndex())].addEdge(ea1.getIndex());
     Triangulation::edgeTable[(eb.getIndex())].addEdge(ea2.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addEdge(e.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addEdge(eb.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addEdge(e.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addEdge(eb.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addEdge(ea1.getIndex());
     Triangulation::edgeTable[(e.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb.getIndex())].addFace(fa2.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(vb.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(va2.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addEdge(eb.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(e.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(ea1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(fa1.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fa1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fa2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb1.getIndex());
     
     Triangulation::vertexTable[vb.getIndex()].setRadius(newRadius);
     
}

void addHandle(Face f, double newRadius)
{
     
     Vertex va1 = Triangulation::vertexTable[(*(f.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(f.getLocalVertices()))[1]];
     Vertex va3 = Triangulation::vertexTable[(*(f.getLocalVertices()))[2]];
     
     vector<int> sameAs;
     
     sameAs = listIntersection(va1.getLocalEdges(), va2.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), va3.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), va3.getLocalEdges());
     Edge ea3 = Triangulation::edgeTable[sameAs[0]];
     
     sameAs = listIntersection(ea1.getLocalFaces(), f.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(ea2.getLocalFaces(), f.getLocalFaces());
     Face fa2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(ea3.getLocalFaces(), f.getLocalFaces());
     Face fa3 = Triangulation::faceTable[sameAs[0]];
     
     Triangulation::eraseFace(f.getIndex());
     
     Vertex vb1(Triangulation::greatestVertex() + 1);
     Vertex vb2(Triangulation::greatestVertex() + 2);
     Vertex vb3(Triangulation::greatestVertex() + 3);
     Vertex vb4(Triangulation::greatestVertex() + 4);
     Vertex vb5(Triangulation::greatestVertex() + 5);
     Vertex vb6(Triangulation::greatestVertex() + 6);
     
     Edge eb1(Triangulation::greatestEdge() + 1);
     Edge eb2(Triangulation::greatestEdge() + 2);
     Edge eb3(Triangulation::greatestEdge() + 3);
     Edge eb4(Triangulation::greatestEdge() + 4);
     Edge eb5(Triangulation::greatestEdge() + 5);
     Edge eb6(Triangulation::greatestEdge() + 6);
     Edge eb7(Triangulation::greatestEdge() + 7);
     Edge eb8(Triangulation::greatestEdge() + 8);
     Edge eb9(Triangulation::greatestEdge() + 9);
     Edge eb10(Triangulation::greatestEdge() + 10);
     Edge eb11(Triangulation::greatestEdge() + 11);
     Edge eb12(Triangulation::greatestEdge() + 12);
     Edge eb13(Triangulation::greatestEdge() + 13);
     Edge eb14(Triangulation::greatestEdge() + 14);
     Edge eb15(Triangulation::greatestEdge() + 15);
     Edge eb16(Triangulation::greatestEdge() + 16);
     Edge eb17(Triangulation::greatestEdge() + 17);
     Edge eb18(Triangulation::greatestEdge() + 18);
     Edge eb19(Triangulation::greatestEdge() + 19);
     Edge eb20(Triangulation::greatestEdge() + 20);
     Edge eb21(Triangulation::greatestEdge() + 21);
     Edge eb22(Triangulation::greatestEdge() + 22);
     Edge eb23(Triangulation::greatestEdge() + 23);
     Edge eb24(Triangulation::greatestEdge() + 24);
     
     Face fb1(Triangulation::greatestFace() + 1);
     Face fb2(Triangulation::greatestFace() + 2);
     Face fb3(Triangulation::greatestFace() + 3);
     Face fb4(Triangulation::greatestFace() + 4);
     Face fb5(Triangulation::greatestFace() + 5);
     Face fb6(Triangulation::greatestFace() + 6);
     Face fb7(Triangulation::greatestFace() + 7);
     Face fb8(Triangulation::greatestFace() + 8);
     Face fb9(Triangulation::greatestFace() + 9);
     Face fb10(Triangulation::greatestFace() + 10);
     Face fb11(Triangulation::greatestFace() + 11);
     Face fb12(Triangulation::greatestFace() + 12);
     Face fb13(Triangulation::greatestFace() + 13);
     Face fb14(Triangulation::greatestFace() + 14);
     Face fb15(Triangulation::greatestFace() + 15);
     Face fb16(Triangulation::greatestFace() + 16);
     Face fb17(Triangulation::greatestFace() + 17);
     
     Triangulation::putVertex(vb1.getIndex(), vb1);
     Triangulation::putVertex(vb2.getIndex(), vb2);
     Triangulation::putVertex(vb3.getIndex(), vb3);
     Triangulation::putVertex(vb4.getIndex(), vb4);
     Triangulation::putVertex(vb5.getIndex(), vb5);
     Triangulation::putVertex(vb6.getIndex(), vb6);
     
     Triangulation::putEdge(eb1.getIndex(), eb1);
     Triangulation::putEdge(eb2.getIndex(), eb2);
     Triangulation::putEdge(eb3.getIndex(), eb3);
     Triangulation::putEdge(eb4.getIndex(), eb4);
     Triangulation::putEdge(eb5.getIndex(), eb5);
     Triangulation::putEdge(eb6.getIndex(), eb6);
     Triangulation::putEdge(eb7.getIndex(), eb7);
     Triangulation::putEdge(eb8.getIndex(), eb8);
     Triangulation::putEdge(eb9.getIndex(), eb9);
     Triangulation::putEdge(eb10.getIndex(), eb10);
     Triangulation::putEdge(eb11.getIndex(), eb11);
     Triangulation::putEdge(eb12.getIndex(), eb12);
     Triangulation::putEdge(eb13.getIndex(), eb13);
     Triangulation::putEdge(eb14.getIndex(), eb14);
     Triangulation::putEdge(eb15.getIndex(), eb15);
     Triangulation::putEdge(eb16.getIndex(), eb16);
     Triangulation::putEdge(eb17.getIndex(), eb17);
     Triangulation::putEdge(eb18.getIndex(), eb18);
     Triangulation::putEdge(eb19.getIndex(), eb19);
     Triangulation::putEdge(eb20.getIndex(), eb20);
     Triangulation::putEdge(eb21.getIndex(), eb21);
     Triangulation::putEdge(eb22.getIndex(), eb22);
     Triangulation::putEdge(eb23.getIndex(), eb23);
     Triangulation::putEdge(eb24.getIndex(), eb24);
     
     Triangulation::putFace(fb1.getIndex(), fb1);
     Triangulation::putFace(fb2.getIndex(), fb2);
     Triangulation::putFace(fb3.getIndex(), fb3);
     Triangulation::putFace(fb4.getIndex(), fb4);
     Triangulation::putFace(fb5.getIndex(), fb5);
     Triangulation::putFace(fb6.getIndex(), fb6);
     Triangulation::putFace(fb7.getIndex(), fb7);
     Triangulation::putFace(fb8.getIndex(), fb8);
     Triangulation::putFace(fb9.getIndex(), fb9);
     Triangulation::putFace(fb10.getIndex(), fb10);
     Triangulation::putFace(fb11.getIndex(), fb11);
     Triangulation::putFace(fb12.getIndex(), fb12);
     Triangulation::putFace(fb13.getIndex(), fb13);
     Triangulation::putFace(fb14.getIndex(), fb14);
     Triangulation::putFace(fb15.getIndex(), fb15);
     Triangulation::putFace(fb16.getIndex(), fb16);
     Triangulation::putFace(fb17.getIndex(), fb17);
     
     //additions
     //vertex-vertex
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb4.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb5.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb4.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb6.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(vb5.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(vb6.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(vb4.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addVertex(vb5.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addVertex(vb6.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(vb5.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addVertex(vb4.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addVertex(vb6.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(vb6.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addVertex(vb4.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addVertex(vb5.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb5.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb6.getIndex());
     
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb4.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb6.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb4.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb5.getIndex());
     
     //vertex-edge
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb1.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb3.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb4.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb6.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb19.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb22.getIndex());
     
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb2.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb7.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb9.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb17.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb21.getIndex());
     
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb3.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb10.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb11.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb20.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb24.getIndex());
     
     Triangulation::vertexTable[(vb4.getIndex())].addEdge(eb4.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addEdge(eb5.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addEdge(eb12.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addEdge(eb15.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addEdge(eb17.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addEdge(eb23.getIndex());
     
     Triangulation::vertexTable[(vb5.getIndex())].addEdge(eb5.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addEdge(eb6.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addEdge(eb13.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addEdge(eb16.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addEdge(eb18.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addEdge(eb24.getIndex());
     
     Triangulation::vertexTable[(vb6.getIndex())].addEdge(eb7.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addEdge(eb8.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addEdge(eb12.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addEdge(eb14.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addEdge(eb18.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addEdge(eb20.getIndex());
     
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb8.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb9.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb13.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb19.getIndex());
     
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb10.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb14.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb15.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb22.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb11.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb16.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb21.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb23.getIndex());
     
     //vertex-face
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb3.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb7.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb12.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb15.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb17.getIndex());
     
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb8.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb10.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb15.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb16.getIndex());
     
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb3.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb9.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb11.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb16.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb17.getIndex());
     
     Triangulation::vertexTable[(vb4.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addFace(fb4.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addFace(fb6.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addFace(fb10.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addFace(fb12.getIndex());
     Triangulation::vertexTable[(vb4.getIndex())].addFace(fb14.getIndex());
     
     Triangulation::vertexTable[(vb5.getIndex())].addFace(fb4.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addFace(fb7.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addFace(fb9.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addFace(fb13.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addFace(fb14.getIndex());
     Triangulation::vertexTable[(vb5.getIndex())].addFace(fb17.getIndex());
     
     Triangulation::vertexTable[(vb6.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addFace(fb4.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addFace(fb5.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addFace(fb10.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addFace(fb11.getIndex());
     Triangulation::vertexTable[(vb6.getIndex())].addFace(fb13.getIndex());
     
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb5.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb7.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb8.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb13.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb15.getIndex());
     
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb3.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb5.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb6.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb11.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb12.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb6.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb8.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb9.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb14.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb16.getIndex());
     
     //edge-vertex
     Triangulation::edgeTable[(eb1.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb4.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb19.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb22.getIndex())].addVertex(vb1.getIndex());
     
     Triangulation::edgeTable[(eb1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb7.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb9.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb17.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb21.getIndex())].addVertex(vb2.getIndex());
     
     Triangulation::edgeTable[(eb2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb10.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb11.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb20.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb24.getIndex())].addVertex(vb3.getIndex());
     
     Triangulation::edgeTable[(eb4.getIndex())].addVertex(vb4.getIndex());
     Triangulation::edgeTable[(eb5.getIndex())].addVertex(vb4.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addVertex(vb4.getIndex());
     Triangulation::edgeTable[(eb15.getIndex())].addVertex(vb4.getIndex());
     Triangulation::edgeTable[(eb17.getIndex())].addVertex(vb4.getIndex());
     Triangulation::edgeTable[(eb23.getIndex())].addVertex(vb4.getIndex());
     
     Triangulation::edgeTable[(eb5.getIndex())].addVertex(vb5.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addVertex(vb5.getIndex());
     Triangulation::edgeTable[(eb13.getIndex())].addVertex(vb5.getIndex());
     Triangulation::edgeTable[(eb16.getIndex())].addVertex(vb5.getIndex());
     Triangulation::edgeTable[(eb18.getIndex())].addVertex(vb5.getIndex());
     Triangulation::edgeTable[(eb24.getIndex())].addVertex(vb5.getIndex());
     
     Triangulation::edgeTable[(eb7.getIndex())].addVertex(vb6.getIndex());
     Triangulation::edgeTable[(eb8.getIndex())].addVertex(vb6.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addVertex(vb6.getIndex());
     Triangulation::edgeTable[(eb14.getIndex())].addVertex(vb6.getIndex());
     Triangulation::edgeTable[(eb18.getIndex())].addVertex(vb6.getIndex());
     Triangulation::edgeTable[(eb20.getIndex())].addVertex(vb6.getIndex());
     
     Triangulation::edgeTable[(eb8.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb9.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb13.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb19.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::edgeTable[(eb10.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(eb14.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(eb15.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(eb22.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::edgeTable[(eb11.getIndex())].addVertex(va3.getIndex());
     Triangulation::edgeTable[(eb16.getIndex())].addVertex(va3.getIndex());
     Triangulation::edgeTable[(eb21.getIndex())].addVertex(va3.getIndex());
     Triangulation::edgeTable[(eb23.getIndex())].addVertex(va3.getIndex());
     
     //edge-edge
     vb1 = Triangulation::vertexTable[vb1.getIndex()];
     for(int i = 0; i < vb1.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb1.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb1.getLocalEdges()))[i]].addEdge((*(vb1.getLocalEdges()))[j]);
             }
     }
     
     vb2 = Triangulation::vertexTable[vb2.getIndex()];
     for(int i = 0; i < vb2.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb2.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb2.getLocalEdges()))[i]].addEdge((*(vb2.getLocalEdges()))[j]);
             }
     }
     
     vb3 = Triangulation::vertexTable[vb3.getIndex()];
     for(int i = 0; i < vb3.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb3.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb3.getLocalEdges()))[i]].addEdge((*(vb3.getLocalEdges()))[j]);
             }
     }
     
     vb4 = Triangulation::vertexTable[vb4.getIndex()];
     for(int i = 0; i < vb4.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb4.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb4.getLocalEdges()))[i]].addEdge((*(vb4.getLocalEdges()))[j]);
             }
     }
     
     vb5 = Triangulation::vertexTable[vb5.getIndex()];
     for(int i = 0; i < vb5.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb5.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb5.getLocalEdges()))[i]].addEdge((*(vb5.getLocalEdges()))[j]);
             }
     }
     
     vb6 = Triangulation::vertexTable[vb6.getIndex()];
     for(int i = 0; i < vb6.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb6.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb6.getLocalEdges()))[i]].addEdge((*(vb6.getLocalEdges()))[j]);
             }
     }
     
     va1 = Triangulation::vertexTable[va1.getIndex()];
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < va1.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].addEdge((*(va1.getLocalEdges()))[j]);
             }
     }
     
     va2 = Triangulation::vertexTable[va2.getIndex()];
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < va2.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].addEdge((*(va2.getLocalEdges()))[j]);
             }
     }
     
     va3 = Triangulation::vertexTable[va3.getIndex()];
     for(int i = 0; i < va3.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < va3.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(va3.getLocalEdges()))[i]].addEdge((*(va3.getLocalEdges()))[j]);
             }
     }
     
     //edge-face
     Triangulation::edgeTable[(eb1.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb4.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb17.getIndex())].addFace(fb1.getIndex());
     
     Triangulation::edgeTable[(eb2.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb7.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb20.getIndex())].addFace(fb2.getIndex());
     
     Triangulation::edgeTable[(eb3.getIndex())].addFace(fb3.getIndex());
     Triangulation::edgeTable[(eb10.getIndex())].addFace(fb3.getIndex());
     Triangulation::edgeTable[(eb22.getIndex())].addFace(fb3.getIndex());
     
     Triangulation::edgeTable[(eb5.getIndex())].addFace(fb4.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addFace(fb4.getIndex());
     Triangulation::edgeTable[(eb18.getIndex())].addFace(fb4.getIndex());
     
     Triangulation::edgeTable[(eb8.getIndex())].addFace(fb5.getIndex());
     Triangulation::edgeTable[(eb14.getIndex())].addFace(fb5.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addFace(fb5.getIndex());
     
     Triangulation::edgeTable[(eb15.getIndex())].addFace(fb6.getIndex());
     Triangulation::edgeTable[(eb23.getIndex())].addFace(fb6.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].addFace(fb6.getIndex());
     
     Triangulation::edgeTable[(eb6.getIndex())].addFace(fb7.getIndex());
     Triangulation::edgeTable[(eb13.getIndex())].addFace(fb7.getIndex());
     Triangulation::edgeTable[(eb19.getIndex())].addFace(fb7.getIndex());
     
     Triangulation::edgeTable[(eb9.getIndex())].addFace(fb8.getIndex());
     Triangulation::edgeTable[(eb21.getIndex())].addFace(fb8.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb8.getIndex());
     
     Triangulation::edgeTable[(eb11.getIndex())].addFace(fb9.getIndex());
     Triangulation::edgeTable[(eb16.getIndex())].addFace(fb9.getIndex());
     Triangulation::edgeTable[(eb24.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::edgeTable[(eb7.getIndex())].addFace(fb10.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addFace(fb10.getIndex());
     Triangulation::edgeTable[(eb17.getIndex())].addFace(fb10.getIndex());
     
     Triangulation::edgeTable[(eb10.getIndex())].addFace(fb11.getIndex());
     Triangulation::edgeTable[(eb14.getIndex())].addFace(fb11.getIndex());
     Triangulation::edgeTable[(eb20.getIndex())].addFace(fb11.getIndex());
     
     Triangulation::edgeTable[(eb4.getIndex())].addFace(fb12.getIndex());
     Triangulation::edgeTable[(eb15.getIndex())].addFace(fb12.getIndex());
     Triangulation::edgeTable[(eb22.getIndex())].addFace(fb12.getIndex());
     
     Triangulation::edgeTable[(eb8.getIndex())].addFace(fb13.getIndex());
     Triangulation::edgeTable[(eb13.getIndex())].addFace(fb13.getIndex());
     Triangulation::edgeTable[(eb18.getIndex())].addFace(fb13.getIndex());
     
     Triangulation::edgeTable[(eb5.getIndex())].addFace(fb14.getIndex());
     Triangulation::edgeTable[(eb16.getIndex())].addFace(fb14.getIndex());
     Triangulation::edgeTable[(eb23.getIndex())].addFace(fb14.getIndex());
     
     Triangulation::edgeTable[(eb1.getIndex())].addFace(fb15.getIndex());
     Triangulation::edgeTable[(eb9.getIndex())].addFace(fb15.getIndex());
     Triangulation::edgeTable[(eb19.getIndex())].addFace(fb15.getIndex());
     
     Triangulation::edgeTable[(eb2.getIndex())].addFace(fb16.getIndex());
     Triangulation::edgeTable[(eb11.getIndex())].addFace(fb16.getIndex());
     Triangulation::edgeTable[(eb21.getIndex())].addFace(fb16.getIndex());
     
     Triangulation::edgeTable[(eb3.getIndex())].addFace(fb17.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addFace(fb17.getIndex());
     Triangulation::edgeTable[(eb24.getIndex())].addFace(fb17.getIndex());
     
     //face-vertex
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb4.getIndex());
     
     Triangulation::faceTable[(fb2.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(vb6.getIndex());
     
     Triangulation::faceTable[(fb3.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addVertex(vb4.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb4.getIndex())].addVertex(vb4.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addVertex(vb5.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addVertex(vb6.getIndex());
     
     Triangulation::faceTable[(fb5.getIndex())].addVertex(vb6.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb6.getIndex())].addVertex(vb4.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addVertex(va2.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb7.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addVertex(vb5.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::faceTable[(fb8.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb9.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addVertex(vb5.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb10.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb10.getIndex())].addVertex(vb4.getIndex());
     Triangulation::faceTable[(fb10.getIndex())].addVertex(vb6.getIndex());
     
     Triangulation::faceTable[(fb11.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb11.getIndex())].addVertex(vb6.getIndex());
     Triangulation::faceTable[(fb11.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb12.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb12.getIndex())].addVertex(vb4.getIndex());
     Triangulation::faceTable[(fb12.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb13.getIndex())].addVertex(vb5.getIndex());
     Triangulation::faceTable[(fb13.getIndex())].addVertex(vb6.getIndex());
     Triangulation::faceTable[(fb13.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::faceTable[(fb14.getIndex())].addVertex(vb4.getIndex());
     Triangulation::faceTable[(fb14.getIndex())].addVertex(vb5.getIndex());
     Triangulation::faceTable[(fb14.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb15.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb15.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb15.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::faceTable[(fb16.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb16.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb16.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb17.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb17.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb17.getIndex())].addVertex(vb5.getIndex());
     
     //face-edge
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb4.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb17.getIndex());
     
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb7.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb20.getIndex());
     
     Triangulation::faceTable[(fb3.getIndex())].addEdge(eb3.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addEdge(eb10.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addEdge(eb22.getIndex());
     
     Triangulation::faceTable[(fb4.getIndex())].addEdge(eb5.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addEdge(eb12.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addEdge(eb18.getIndex());

     Triangulation::faceTable[(fb5.getIndex())].addEdge(eb8.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addEdge(eb14.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addEdge(ea1.getIndex());
     
     Triangulation::faceTable[(fb6.getIndex())].addEdge(eb15.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addEdge(eb23.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addEdge(ea3.getIndex());
     
     Triangulation::faceTable[(fb7.getIndex())].addEdge(eb6.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addEdge(eb13.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addEdge(eb19.getIndex());
     
     Triangulation::faceTable[(fb8.getIndex())].addEdge(eb9.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addEdge(eb21.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addEdge(ea2.getIndex());
     
     Triangulation::faceTable[(fb9.getIndex())].addEdge(eb11.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addEdge(eb16.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addEdge(eb24.getIndex());
     
     Triangulation::faceTable[(fb10.getIndex())].addEdge(eb7.getIndex());
     Triangulation::faceTable[(fb10.getIndex())].addEdge(eb12.getIndex());
     Triangulation::faceTable[(fb10.getIndex())].addEdge(eb17.getIndex());
     
     Triangulation::faceTable[(fb11.getIndex())].addEdge(eb10.getIndex());
     Triangulation::faceTable[(fb11.getIndex())].addEdge(eb14.getIndex());
     Triangulation::faceTable[(fb11.getIndex())].addEdge(eb20.getIndex());
     
     Triangulation::faceTable[(fb12.getIndex())].addEdge(eb4.getIndex());
     Triangulation::faceTable[(fb12.getIndex())].addEdge(eb15.getIndex());
     Triangulation::faceTable[(fb12.getIndex())].addEdge(eb22.getIndex());
     
     Triangulation::faceTable[(fb13.getIndex())].addEdge(eb8.getIndex());
     Triangulation::faceTable[(fb13.getIndex())].addEdge(eb13.getIndex());
     Triangulation::faceTable[(fb13.getIndex())].addEdge(eb18.getIndex());
     
     Triangulation::faceTable[(fb14.getIndex())].addEdge(eb5.getIndex());
     Triangulation::faceTable[(fb14.getIndex())].addEdge(eb16.getIndex());
     Triangulation::faceTable[(fb14.getIndex())].addEdge(eb23.getIndex());
     
     Triangulation::faceTable[(fb15.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(fb15.getIndex())].addEdge(eb9.getIndex());
     Triangulation::faceTable[(fb15.getIndex())].addEdge(eb19.getIndex());
     
     Triangulation::faceTable[(fb16.getIndex())].addEdge(eb2.getIndex());
     Triangulation::faceTable[(fb16.getIndex())].addEdge(eb11.getIndex());
     Triangulation::faceTable[(fb16.getIndex())].addEdge(eb21.getIndex());
     
     Triangulation::faceTable[(fb17.getIndex())].addEdge(eb3.getIndex());
     Triangulation::faceTable[(fb17.getIndex())].addEdge(eb6.getIndex());
     Triangulation::faceTable[(fb17.getIndex())].addEdge(eb24.getIndex());
          
     //face-face
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb10.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb12.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb15.getIndex());
     
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb10.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb11.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb16.getIndex());

     Triangulation::faceTable[(fb3.getIndex())].addFace(fb11.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addFace(fb12.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addFace(fb17.getIndex());
     
     Triangulation::faceTable[(fb4.getIndex())].addFace(fb10.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addFace(fb13.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addFace(fb14.getIndex());
     
     Triangulation::faceTable[(fb5.getIndex())].addFace(fb11.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addFace(fb13.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addFace(fa1.getIndex());
     
     Triangulation::faceTable[(fb6.getIndex())].addFace(fb12.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addFace(fb14.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addFace(fa3.getIndex());
     
     Triangulation::faceTable[(fb7.getIndex())].addFace(fb13.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addFace(fb15.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addFace(fb17.getIndex());
     
     Triangulation::faceTable[(fb8.getIndex())].addFace(fb15.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addFace(fb16.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addFace(fa2.getIndex());
     
     Triangulation::faceTable[(fb9.getIndex())].addFace(fb14.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addFace(fb16.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addFace(fb17.getIndex());
     
     Triangulation::faceTable[(fb10.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb10.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb10.getIndex())].addFace(fb4.getIndex());
     
     Triangulation::faceTable[(fb11.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb11.getIndex())].addFace(fb3.getIndex());
     Triangulation::faceTable[(fb11.getIndex())].addFace(fb5.getIndex());
     
     Triangulation::faceTable[(fb12.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb12.getIndex())].addFace(fb3.getIndex());
     Triangulation::faceTable[(fb12.getIndex())].addFace(fb6.getIndex());
     
     Triangulation::faceTable[(fb13.getIndex())].addFace(fb4.getIndex());
     Triangulation::faceTable[(fb13.getIndex())].addFace(fb5.getIndex());
     Triangulation::faceTable[(fb13.getIndex())].addFace(fb7.getIndex());
     
     Triangulation::faceTable[(fb14.getIndex())].addFace(fb4.getIndex());
     Triangulation::faceTable[(fb14.getIndex())].addFace(fb6.getIndex());
     Triangulation::faceTable[(fb14.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::faceTable[(fb15.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb15.getIndex())].addFace(fb7.getIndex());
     Triangulation::faceTable[(fb15.getIndex())].addFace(fb8.getIndex());
     
     Triangulation::faceTable[(fb16.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb16.getIndex())].addFace(fb8.getIndex());
     Triangulation::faceTable[(fb16.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::faceTable[(fb17.getIndex())].addFace(fb3.getIndex());
     Triangulation::faceTable[(fb17.getIndex())].addFace(fb7.getIndex());
     Triangulation::faceTable[(fb17.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::faceTable[(fa1.getIndex())].addFace(fb5.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addFace(fb8.getIndex());
     Triangulation::faceTable[(fa3.getIndex())].addFace(fb6.getIndex());
     
}

void addCrossCap(Face f, double newRadius)
{
     
     Vertex va1 = Triangulation::vertexTable[(*(f.getLocalVertices()))[0]];
     Vertex va2 = Triangulation::vertexTable[(*(f.getLocalVertices()))[1]];
     Vertex va3 = Triangulation::vertexTable[(*(f.getLocalVertices()))[2]];
     
     vector<int> sameAs;
     
     sameAs = listIntersection(va1.getLocalEdges(), va2.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), va3.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), va3.getLocalEdges());
     Edge ea3 = Triangulation::edgeTable[sameAs[0]];
     
     sameAs = listIntersection(ea1.getLocalFaces(), f.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(ea2.getLocalFaces(), f.getLocalFaces());
     Face fa2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(ea3.getLocalFaces(), f.getLocalFaces());
     Face fa3 = Triangulation::faceTable[sameAs[0]];
     
     Triangulation::eraseFace(f.getIndex());
     
     Vertex vb1(Triangulation::greatestVertex() + 1);
     Vertex vb2(Triangulation::greatestVertex() + 2);
     Vertex vb3(Triangulation::greatestVertex() + 3);
     
     Edge eb1(Triangulation::greatestEdge() + 1);
     Edge eb2(Triangulation::greatestEdge() + 2);
     Edge eb3(Triangulation::greatestEdge() + 3);
     Edge eb4(Triangulation::greatestEdge() + 4);
     Edge eb5(Triangulation::greatestEdge() + 5);
     Edge eb6(Triangulation::greatestEdge() + 6);
     Edge eb7(Triangulation::greatestEdge() + 7);
     Edge eb8(Triangulation::greatestEdge() + 8);
     Edge eb9(Triangulation::greatestEdge() + 9);
     Edge eb10(Triangulation::greatestEdge() + 10);
     Edge eb11(Triangulation::greatestEdge() + 11);
     Edge eb12(Triangulation::greatestEdge() + 12);
     
     Face fb1(Triangulation::greatestFace() + 1);
     Face fb2(Triangulation::greatestFace() + 2);
     Face fb3(Triangulation::greatestFace() + 3);
     Face fb4(Triangulation::greatestFace() + 4);
     Face fb5(Triangulation::greatestFace() + 5);
     Face fb6(Triangulation::greatestFace() + 6);
     Face fb7(Triangulation::greatestFace() + 7);
     Face fb8(Triangulation::greatestFace() + 8);
     Face fb9(Triangulation::greatestFace() + 9);
     
     Triangulation::putVertex(vb1.getIndex(), vb1);
     Triangulation::putVertex(vb2.getIndex(), vb2);
     Triangulation::putVertex(vb3.getIndex(), vb3);
     
     Triangulation::putEdge(eb1.getIndex(), eb1);
     Triangulation::putEdge(eb2.getIndex(), eb2);
     Triangulation::putEdge(eb3.getIndex(), eb3);
     Triangulation::putEdge(eb4.getIndex(), eb4);
     Triangulation::putEdge(eb5.getIndex(), eb5);
     Triangulation::putEdge(eb6.getIndex(), eb6);
     Triangulation::putEdge(eb7.getIndex(), eb7);
     Triangulation::putEdge(eb8.getIndex(), eb8);
     Triangulation::putEdge(eb9.getIndex(), eb9);
     Triangulation::putEdge(eb10.getIndex(), eb10);
     Triangulation::putEdge(eb11.getIndex(), eb11);
     Triangulation::putEdge(eb12.getIndex(), eb12);
     
     Triangulation::putFace(fb1.getIndex(), fb1);
     Triangulation::putFace(fb2.getIndex(), fb2);
     Triangulation::putFace(fb3.getIndex(), fb3);
     Triangulation::putFace(fb4.getIndex(), fb4);
     Triangulation::putFace(fb5.getIndex(), fb5);
     Triangulation::putFace(fb6.getIndex(), fb6);
     Triangulation::putFace(fb7.getIndex(), fb7);
     Triangulation::putFace(fb8.getIndex(), fb8);
     Triangulation::putFace(fb9.getIndex(), fb9);
     
     //vertex-vertex
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(va2.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb1.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb2.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(vb3.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(va1.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addVertex(va2.getIndex());
     
     //vertex-edge
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb1.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb2.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb4.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb5.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addEdge(eb12.getIndex());
     
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb3.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb6.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb9.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addEdge(eb11.getIndex());
     
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb2.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb3.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb7.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb8.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addEdge(eb10.getIndex());
     
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb4.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb6.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addEdge(eb8.getIndex());
     
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb5.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb7.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addEdge(eb9.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb10.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb11.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addEdge(eb12.getIndex());
     
     //vertex-face
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb3.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb8.getIndex());
     Triangulation::vertexTable[(vb1.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb4.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb6.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb7.getIndex());
     Triangulation::vertexTable[(vb2.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb3.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb4.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb5.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb7.getIndex());
     Triangulation::vertexTable[(vb3.getIndex())].addFace(fb8.getIndex());
     
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb1.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb4.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].addFace(fb5.getIndex());
     
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb2.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb3.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb6.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].addFace(fb7.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb5.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb6.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb8.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb9.getIndex());
     
     //edge-vertex
     Triangulation::edgeTable[(eb1.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb4.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb5.getIndex())].addVertex(vb1.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addVertex(vb1.getIndex());
     
     Triangulation::edgeTable[(eb1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb9.getIndex())].addVertex(vb2.getIndex());
     Triangulation::edgeTable[(eb11.getIndex())].addVertex(vb2.getIndex());
     
     Triangulation::edgeTable[(eb2.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb3.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb7.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb8.getIndex())].addVertex(vb3.getIndex());
     Triangulation::edgeTable[(eb10.getIndex())].addVertex(vb3.getIndex());
     
     Triangulation::edgeTable[(eb4.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addVertex(va1.getIndex());
     Triangulation::edgeTable[(eb8.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::edgeTable[(eb5.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(eb7.getIndex())].addVertex(va2.getIndex());
     Triangulation::edgeTable[(eb9.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::edgeTable[(eb10.getIndex())].addVertex(va3.getIndex());
     Triangulation::edgeTable[(eb11.getIndex())].addVertex(va3.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addVertex(va3.getIndex());
     
     //edge-edge
     vb1 = Triangulation::vertexTable[vb1.getIndex()];
     for(int i = 0; i < vb1.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb1.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb1.getLocalEdges()))[i]].addEdge((*(vb1.getLocalEdges()))[j]);
             }
     }
     
     vb2 = Triangulation::vertexTable[vb2.getIndex()];
     for(int i = 0; i < vb2.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb2.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb2.getLocalEdges()))[i]].addEdge((*(vb2.getLocalEdges()))[j]);
             }
     }
     
     vb3 = Triangulation::vertexTable[vb3.getIndex()];
     for(int i = 0; i < vb3.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < vb3.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(vb3.getLocalEdges()))[i]].addEdge((*(vb3.getLocalEdges()))[j]);
             }
     }
     
     va1 = Triangulation::vertexTable[va1.getIndex()];
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < va1.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].addEdge((*(va1.getLocalEdges()))[j]);
             }
     }
     
     va2 = Triangulation::vertexTable[va2.getIndex()];
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < va2.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].addEdge((*(va2.getLocalEdges()))[j]);
             }
     }
     
     va3 = Triangulation::vertexTable[va3.getIndex()];
     for(int i = 0; i < va3.getLocalEdges()->size(); i++)
     {
             for(int j = 0; j < va3.getLocalEdges()->size(); j++)
             {
                     if(i != j)
                     Triangulation::edgeTable[(*(va3.getLocalEdges()))[i]].addEdge((*(va3.getLocalEdges()))[j]);
             }
     }
     
     //edge-face
     Triangulation::edgeTable[(eb1.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb4.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addFace(fb1.getIndex());
     
     Triangulation::edgeTable[(eb4.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(eb5.getIndex())].addFace(fb2.getIndex());
     Triangulation::edgeTable[(ea1.getIndex())].addFace(fb2.getIndex());
     
     Triangulation::edgeTable[(eb2.getIndex())].addFace(fb3.getIndex());
     Triangulation::edgeTable[(eb5.getIndex())].addFace(fb3.getIndex());
     Triangulation::edgeTable[(eb7.getIndex())].addFace(fb3.getIndex());
     
     Triangulation::edgeTable[(eb3.getIndex())].addFace(fb4.getIndex());
     Triangulation::edgeTable[(eb6.getIndex())].addFace(fb4.getIndex());
     Triangulation::edgeTable[(eb8.getIndex())].addFace(fb4.getIndex());
     
     Triangulation::edgeTable[(eb8.getIndex())].addFace(fb5.getIndex());
     Triangulation::edgeTable[(eb10.getIndex())].addFace(fb5.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb5.getIndex());
     
     Triangulation::edgeTable[(eb9.getIndex())].addFace(fb6.getIndex());
     Triangulation::edgeTable[(eb11.getIndex())].addFace(fb6.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].addFace(fb6.getIndex());
     
     Triangulation::edgeTable[(eb3.getIndex())].addFace(fb7.getIndex());
     Triangulation::edgeTable[(eb7.getIndex())].addFace(fb7.getIndex());
     Triangulation::edgeTable[(eb9.getIndex())].addFace(fb7.getIndex());
     
     Triangulation::edgeTable[(eb2.getIndex())].addFace(fb8.getIndex());
     Triangulation::edgeTable[(eb10.getIndex())].addFace(fb8.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addFace(fb8.getIndex());
     
     Triangulation::edgeTable[(eb1.getIndex())].addFace(fb9.getIndex());
     Triangulation::edgeTable[(eb11.getIndex())].addFace(fb9.getIndex());
     Triangulation::edgeTable[(eb12.getIndex())].addFace(fb9.getIndex());
     
     //face-vertex
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::faceTable[(fb2.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb3.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb4.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addVertex(va1.getIndex());
     
     Triangulation::faceTable[(fb5.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addVertex(va1.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb6.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addVertex(va2.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb7.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addVertex(va2.getIndex());
     
     Triangulation::faceTable[(fb8.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addVertex(vb3.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addVertex(va3.getIndex());
     
     Triangulation::faceTable[(fb9.getIndex())].addVertex(vb1.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addVertex(vb2.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addVertex(va3.getIndex());
     
     //face-edge
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb4.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(eb6.getIndex());
     
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb4.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(eb5.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addEdge(ea1.getIndex());
     
     Triangulation::faceTable[(fb3.getIndex())].addEdge(eb2.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addEdge(eb5.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addEdge(eb7.getIndex());
     
     Triangulation::faceTable[(fb4.getIndex())].addEdge(eb3.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addEdge(eb6.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addEdge(eb8.getIndex());
     
     Triangulation::faceTable[(fb5.getIndex())].addEdge(eb8.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addEdge(eb10.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addEdge(ea2.getIndex());
     
     Triangulation::faceTable[(fb6.getIndex())].addEdge(eb9.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addEdge(eb11.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addEdge(ea3.getIndex());
     
     Triangulation::faceTable[(fb7.getIndex())].addEdge(eb3.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addEdge(eb7.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addEdge(eb9.getIndex());
     
     Triangulation::faceTable[(fb8.getIndex())].addEdge(eb2.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addEdge(eb10.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addEdge(eb12.getIndex());
     
     Triangulation::faceTable[(fb9.getIndex())].addEdge(eb1.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addEdge(eb11.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addEdge(eb12.getIndex());
     
     //face-face
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb4.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb3.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fa1.getIndex());
     
     Triangulation::faceTable[(fb3.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addFace(fb7.getIndex());
     Triangulation::faceTable[(fb3.getIndex())].addFace(fb8.getIndex());
     
     Triangulation::faceTable[(fb4.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addFace(fb5.getIndex());
     Triangulation::faceTable[(fb4.getIndex())].addFace(fb7.getIndex());
     
     Triangulation::faceTable[(fb5.getIndex())].addFace(fb4.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addFace(fb8.getIndex());
     Triangulation::faceTable[(fb5.getIndex())].addFace(fa2.getIndex());
     
     Triangulation::faceTable[(fb6.getIndex())].addFace(fb7.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addFace(fb9.getIndex());
     Triangulation::faceTable[(fb6.getIndex())].addFace(fa3.getIndex());

     Triangulation::faceTable[(fb7.getIndex())].addFace(fb3.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addFace(fb4.getIndex());
     Triangulation::faceTable[(fb7.getIndex())].addFace(fb6.getIndex());
     
     Triangulation::faceTable[(fb8.getIndex())].addFace(fb3.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addFace(fb5.getIndex());
     Triangulation::faceTable[(fb8.getIndex())].addFace(fb9.getIndex());
     
     Triangulation::faceTable[(fb9.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addFace(fb6.getIndex());
     Triangulation::faceTable[(fb9.getIndex())].addFace(fb8.getIndex());
     
     Triangulation::faceTable[(fa1.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addFace(fb5.getIndex());
     Triangulation::faceTable[(fa3.getIndex())].addFace(fb6.getIndex());
     
     
     
     
     
     
}

void oneThreeMove(Face f) {
     
     Vertex v(Triangulation::greatestVertex() + 1);
     addVertexToFace(f, v);
     Triangulation::eraseFace(f.getIndex());
     Triangulation::eraseTetra(t.getIndex());
     
//     Edge e1 = Triangulation::edgeTable[(*(f.getLocalEdges()))[0]];
//     Edge e2 = Triangulation::edgeTable[(*(f.getLocalEdges()))[1]];
//     Edge e3 = Triangulation::edgeTable[(*(f.getLocalEdges()))[2]];
//     
//     vector<int> sameAs;
//     sameAs = listIntersection(f.getLocalFaces(), e1.getLocalFaces());
//     
//     
//     Vertex v(Triangulation::greatestVertex() + 1);
//     Triangulation::putVertex(v.getIndex(), v);
//     Triangulation::eraseFace(f.getIndex());
//     
//     Face fb1 = Triangulation::faceTable[addVertexToEdge(e1, v)];
//     Face fb2 = Triangulation::faceTable[addVertexToEdge(Triangulation::edgeTable[e2.getIndex()], 
//                Triangulation::vertexTable[v.getIndex()])];
//     Face fb3 = Triangulation::faceTable[addVertexToEdge(Triangulation::edgeTable[e3.getIndex()], 
//                Triangulation::vertexTable[v.getIndex()])];
     
     
     
}

void threeOneMove(Vertex v) {
     Vertex v1 = Triangulation::vertexTable[(*(v.getLocalVertices()))[0]];
     Vertex v2 = Triangulation::vertexTable[(*(v.getLocalVertices()))[1]];
     Vertex v3 = Triangulation::vertexTable[(*(v.getLocalVertices()))[2]];
     
     for(int i = 0; i < v.getLocalEdges()->size(); i++) {
             Triangulation::eraseEdge((*(v.getLocalEdges()))[i]);
     }
     for(int i = 0; i < v.getLocalFaces()->size(); i++) {
             Triangulation::eraseFace((*(v.getLocalFaces()))[i]);
     }
     
     makeFace(v1, v2, v3);
     
}
























