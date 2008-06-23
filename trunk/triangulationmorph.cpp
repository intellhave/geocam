/**************************************************************
File: Triangulation Morph
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 20, 2008
***************************************************************
The Triangulation Morph file holds the functions that manipulates
the Triangulation in some way.
**************************************************************/
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "Triangulation.h"
#include "Triangulationmath.h"
#include <cstdlib>
#include <iostream>

void addNewVertex(Face f, double newWeight)
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
     
     sameAs = listIntersection(ea1.getLocalFaces(), f.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(ea2.getLocalFaces(), f.getLocalFaces());
     Face fa2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(ea3.getLocalFaces(), f.getLocalFaces());
     Face fa3 = Triangulation::faceTable[sameAs[0]];
     
     //the create the new simplices needed, using new indices
     Vertex vb;
     vb.setIndex(Triangulation::vertexTable.size() + 1);
     
     Edge eb1(Triangulation::edgeTable.size() + 1);
     Edge eb2(Triangulation::edgeTable.size() + 2);
     Edge eb3(Triangulation::edgeTable.size() + 3);
     Face fb1(Triangulation::faceTable.size() + 1);
     Face fb2(Triangulation::faceTable.size() + 2);
     
     //finally, add the new simplices to the triangulation and give the new vertex a weight
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
     Triangulation::faceTable[(f.getIndex())].removeFace(fa2.getIndex());
     Triangulation::faceTable[(f.getIndex())].removeFace(fa3.getIndex());
     
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
     Triangulation::faceTable[(fb1.getIndex())].addFace(fa2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fb2.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(f.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb2.getIndex())].addFace(fa3.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fa3.getIndex())].addFace(fb2.getIndex());
     
     
     Triangulation::vertexTable[vb.getIndex()].setWeight(newWeight);
     
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
          
     diff = listDifference(f1.getLocalVertices(), f2.getLocalVertices());
     if(diff.size() == 0)
     throw string("Invalid move, operation cancelled");
     Vertex vb1 = Triangulation::vertexTable[diff[0]];
     diff = listDifference(f2.getLocalVertices(), f1.getLocalVertices());
     Vertex vb2 = Triangulation::vertexTable[diff[0]];
     
     sameAs = listIntersection(va1.getLocalEdges(), vb1.getLocalEdges());
     Edge ea1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb1.getLocalEdges());
     Edge eb1 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va1.getLocalEdges(), vb2.getLocalEdges());
     Edge ea2 = Triangulation::edgeTable[sameAs[0]];
     sameAs = listIntersection(va2.getLocalEdges(), vb2.getLocalEdges());
     Edge eb2 = Triangulation::edgeTable[sameAs[0]];
     
     sameAs = listIntersection(f1.getLocalFaces(), ea1.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(f1.getLocalFaces(), eb1.getLocalFaces());
     Face fb1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(f2.getLocalFaces(), ea2.getLocalFaces());
     Face fa2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(f2.getLocalFaces(), eb2.getLocalFaces());
     Face fb2 = Triangulation::faceTable[sameAs[0]];
     
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
     Triangulation::faceTable[(f1.getIndex())].removeFace(fb1.getIndex()); 
     Triangulation::faceTable[(fb1.getIndex())].removeFace(f1.getIndex()); 
     Triangulation::faceTable[(f2.getIndex())].removeFace(fa2.getIndex()); 
     Triangulation::faceTable[(fa2.getIndex())].removeFace(f2.getIndex()); 
     
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
     //if(fa1.getIndex() != fa2.getIndex())
     Triangulation::faceTable[(f1.getIndex())].addFace(fa2.getIndex());
     //if(fa1.getIndex() != fa2.getIndex())
     Triangulation::faceTable[(fa2.getIndex())].addFace(f1.getIndex());
     //if(fb1.getIndex() != fb2.getIndex())
     Triangulation::faceTable[(f2.getIndex())].addFace(fb1.getIndex());
     //if(fb1.getIndex() != fb2.getIndex())
     Triangulation::faceTable[(fb1.getIndex())].addFace(f2.getIndex());
     
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
     sameAs = listIntersection(fb1.getLocalFaces(), ea1.getLocalFaces());
     Face fa1 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(fb2.getLocalFaces(), ea2.getLocalFaces());
     Face fa2 = Triangulation::faceTable[sameAs[0]];
     sameAs = listIntersection(fb3.getLocalFaces(), ea3.getLocalFaces());
     Face fa3 = Triangulation::faceTable[sameAs[0]];
     
     Triangulation::vertexTable[(va1.getIndex())].removeVertex(v.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].removeVertex(v.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].removeVertex(v.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].removeEdge(eb1.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].removeEdge(eb2.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].removeEdge(eb3.getIndex());
     Triangulation::vertexTable[(va1.getIndex())].removeFace(fb2.getIndex());
     Triangulation::vertexTable[(va2.getIndex())].removeFace(fb3.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].removeFace(fb2.getIndex());
     Triangulation::vertexTable[(va3.getIndex())].removeFace(fb3.getIndex());
     for(int i = 0; i < va1.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(va1.getLocalEdges()))[i]].removeEdge(eb1.getIndex());
     }
     for(int i = 0; i < va2.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(va2.getLocalEdges()))[i]].removeEdge(eb2.getIndex());
     }
     for(int i = 0; i < va3.getLocalEdges()->size(); i++)
     {
             Triangulation::edgeTable[(*(va3.getLocalEdges()))[i]].removeEdge(eb3.getIndex());
     }
     Triangulation::edgeTable[(ea2.getIndex())].removeFace(fb2.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].removeFace(fb3.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].removeVertex(v.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].removeEdge(eb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].removeEdge(eb2.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].removeFace(fb2.getIndex());
     Triangulation::faceTable[(fa3.getIndex())].removeFace(fb3.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].removeFace(fb2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].removeFace(fb3.getIndex());
     
     Triangulation::vertexTable[(va3.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea2.getIndex())].addFace(fb1.getIndex());
     Triangulation::edgeTable[(ea3.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addVertex(va3.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addEdge(ea3.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fa2.getIndex());
     Triangulation::faceTable[(fb1.getIndex())].addFace(fa3.getIndex());
     Triangulation::faceTable[(fa2.getIndex())].addFace(fb1.getIndex());
     Triangulation::faceTable[(fa3.getIndex())].addFace(fb1.getIndex());
     
     vector<Edge> inOrder;
     inOrder.push_back(eb1);
     if(eb2.getIndex() > eb1.getIndex())
     {
        if(eb2.getIndex() < eb3.getIndex())
        {
            inOrder.push_back(eb2);
            inOrder.push_back(eb3);
        } else if(eb3.getIndex() < eb1.getIndex())
        {
            inOrder.insert(inOrder.begin(), eb3);
            inOrder.push_back(eb2);
        } else
        {
            inOrder.push_back(eb3);
            inOrder.push_back(eb2);
        }
     }
     else
     {
        if(eb2.getIndex() > eb3.getIndex())
        {
            inOrder.insert(inOrder.begin(), eb2);
            inOrder.insert(inOrder.begin(), eb3);
        } else if(eb3.getIndex() > eb1.getIndex())
        {
            inOrder.insert(inOrder.begin(), eb2);
            inOrder.push_back(eb3);
        } else
        {
            inOrder.insert(inOrder.begin(), eb3);
            inOrder.insert(inOrder.begin(), eb2);
        }
     }
     Triangulation::eraseVertex(v.getIndex());
     for(int i = 2; i >= 0; i--)
     {
        Triangulation::eraseEdge(inOrder[i].getIndex());
     }
     if(fb3.getIndex() > fb2.getIndex())
     {
        Triangulation::eraseFace(fb3.getIndex());
        Triangulation::eraseFace(fb2.getIndex());
     }else
     {
        Triangulation::eraseFace(fb2.getIndex());
        Triangulation::eraseFace(fb3.getIndex());
     }
     
         
}
