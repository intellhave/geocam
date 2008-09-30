#include "addremove.h"
#include "triangulation/triangulation.h"
#include <vector>

void add(Vertex* s1, Vertex* s2) 
{
     (*s1).addVertex((*s2).getIndex());
     (*s2).addVertex((*s1).getIndex());
}
void add(Vertex* s1, Edge* s2)
{
     (*s1).addEdge((*s2).getIndex());
     (*s2).addVertex((*s1).getIndex());
}
void add(Vertex* s1, Face* s2)
{
     (*s1).addFace((*s2).getIndex());
     (*s2).addVertex((*s1).getIndex());
}
void add(Vertex* s1, Tetra* s2)
{
     (*s1).addTetra((*s2).getIndex());
     (*s2).addVertex((*s1).getIndex());
}
void add(Edge* s1, Vertex* s2)
{
     (*s1).addVertex((*s2).getIndex());
     (*s2).addEdge((*s1).getIndex());
}
void add(Edge* s1, Edge* s2)
{
     (*s1).addEdge((*s2).getIndex());
     (*s2).addEdge((*s1).getIndex());
}
void add(Edge* s1, Face* s2)
{
     (*s1).addFace((*s2).getIndex());
     (*s2).addEdge((*s1).getIndex());
}
void add(Edge* s1, Tetra* s2)
{
     (*s1).addTetra((*s2).getIndex());
     (*s2).addEdge((*s1).getIndex());
}
void add(Face* s1, Vertex* s2)
{
     (*s1).addVertex((*s2).getIndex());
     (*s2).addFace((*s1).getIndex());
}
void add(Face* s1, Edge* s2)
{
     (*s1).addEdge((*s2).getIndex());
     (*s2).addFace((*s1).getIndex());
}
void add(Face* s1, Face* s2)
{
     (*s1).addFace((*s2).getIndex());
     (*s2).addFace((*s1).getIndex());
}
void add(Face* s1, Tetra* s2)
{
     (*s1).addTetra((*s2).getIndex());
     (*s2).addFace((*s1).getIndex());
}
void add(Tetra* s1, Vertex* s2)
{
     (*s1).addVertex((*s2).getIndex());
     (*s2).addTetra((*s1).getIndex());
}
void add(Tetra* s1, Edge* s2)
{
     (*s1).addEdge((*s2).getIndex());
     (*s2).addTetra((*s1).getIndex());
}
void add(Tetra* s1, Face* s2)
{
     (*s1).addFace((*s2).getIndex());
     (*s2).addTetra((*s1).getIndex());
}
void add(Tetra* s1, Tetra* s2)
{
     (*s1).addTetra((*s2).getIndex());
     (*s2).addTetra((*s1).getIndex());
}



void add(Vertex* s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Vertex* s1, vector<Edge*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Vertex* s1, vector<Face*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Vertex* s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Edge* s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Edge* s1, vector<Edge*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Edge* s1, vector<Face*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Edge* s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Face* s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Face* s1, vector<Edge*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Face* s1, vector<Face*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Face* s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Tetra* s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Tetra* s1, vector<Edge*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Tetra* s1, vector<Face*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}
void add(Tetra* s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         add(s1, s2[i]);
     }
}

void add(vector<Vertex*> s1, Vertex* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Vertex*> s1, Edge* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Vertex*> s1, Face* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Vertex*> s1, Tetra* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Edge*> s1, Vertex* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Edge*> s1, Edge* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Edge*> s1, Face* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Edge*> s1, Tetra* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Face*> s1, Vertex* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Face*> s1, Edge* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Face*> s1, Face* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Face*> s1, Tetra* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Tetra*> s1, Vertex* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Tetra*> s1, Edge* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Tetra*> s1, Face* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}
void add(vector<Tetra*> s1, Tetra* s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         add(s1[i], s2);
     }
}


void add(vector<Vertex*> s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Vertex*> s1, vector<Edge*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Vertex*> s1, vector<Face*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Vertex*> s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Edge*> s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Edge*> s1, vector<Edge*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Edge*> s1, vector<Face*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Edge*> s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Face*> s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Face*> s1, vector<Edge*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Face*> s1, vector<Face*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Face*> s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Tetra*> s1, vector<Vertex*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Tetra*> s1, vector<Edge*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Tetra*> s1, vector<Face*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}
void add(vector<Tetra*> s1, vector<Tetra*> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            add(s1[i], s2[j]);
         }
     }
}

/******************************Remove***********************************/

void remove(Vertex s1, Vertex s2) 
{
     Triangulation::vertexTable[s1.getIndex()].removeVertex(s2.getIndex());
     Triangulation::vertexTable[s2.getIndex()].removeVertex(s1.getIndex());
}
void remove(Vertex s1, Edge s2)
{
     Triangulation::vertexTable[s1.getIndex()].removeEdge(s2.getIndex());
     Triangulation::edgeTable[s2.getIndex()].removeVertex(s1.getIndex());
}
void remove(Vertex s1, Face s2)
{
     Triangulation::vertexTable[s1.getIndex()].removeFace(s2.getIndex());
     Triangulation::faceTable[s2.getIndex()].removeVertex(s1.getIndex());
}
void remove(Vertex s1, Tetra s2)
{
     Triangulation::vertexTable[s1.getIndex()].removeTetra(s2.getIndex());
     Triangulation::tetraTable[s2.getIndex()].removeVertex(s1.getIndex());
}
void remove(Edge s1, Vertex s2)
{
     Triangulation::edgeTable[s1.getIndex()].removeVertex(s2.getIndex());
     Triangulation::vertexTable[s2.getIndex()].removeEdge(s1.getIndex());
}
void remove(Edge s1, Edge s2)
{
     Triangulation::edgeTable[s1.getIndex()].removeEdge(s2.getIndex());
     Triangulation::edgeTable[s2.getIndex()].removeEdge(s1.getIndex());
}
void remove(Edge s1, Face s2)
{
     Triangulation::edgeTable[s1.getIndex()].removeFace(s2.getIndex());
     Triangulation::faceTable[s2.getIndex()].removeEdge(s1.getIndex());
}
void remove(Edge s1, Tetra s2)
{
     Triangulation::edgeTable[s1.getIndex()].removeTetra(s2.getIndex());
     Triangulation::tetraTable[s2.getIndex()].removeEdge(s1.getIndex());
}
void remove(Face s1, Vertex s2)
{
     Triangulation::faceTable[s1.getIndex()].removeVertex(s2.getIndex());
     Triangulation::vertexTable[s2.getIndex()].removeFace(s1.getIndex());
}
void remove(Face s1, Edge s2)
{
     Triangulation::faceTable[s1.getIndex()].removeEdge(s2.getIndex());
     Triangulation::edgeTable[s2.getIndex()].removeFace(s1.getIndex());
}
void remove(Face s1, Face s2)
{
     Triangulation::faceTable[s1.getIndex()].removeFace(s2.getIndex());
     Triangulation::faceTable[s2.getIndex()].removeFace(s1.getIndex());
}
void remove(Face s1, Tetra s2)
{
     Triangulation::faceTable[s1.getIndex()].removeTetra(s2.getIndex());
     Triangulation::tetraTable[s2.getIndex()].removeFace(s1.getIndex());
}
void remove(Tetra s1, Vertex s2)
{
     Triangulation::tetraTable[s1.getIndex()].removeVertex(s2.getIndex());
     Triangulation::vertexTable[s2.getIndex()].removeTetra(s1.getIndex());
}
void remove(Tetra s1, Edge s2)
{
     Triangulation::tetraTable[s1.getIndex()].removeEdge(s2.getIndex());
     Triangulation::edgeTable[s2.getIndex()].removeTetra(s1.getIndex());
}
void remove(Tetra s1, Face s2)
{
     Triangulation::tetraTable[s1.getIndex()].removeFace(s2.getIndex());
     Triangulation::faceTable[s2.getIndex()].removeTetra(s1.getIndex());
}
void remove(Tetra s1, Tetra s2)
{
     Triangulation::tetraTable[s1.getIndex()].removeTetra(s2.getIndex());
     Triangulation::tetraTable[s2.getIndex()].removeTetra(s1.getIndex());
}



void remove(Vertex s1, vector<Vertex> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Vertex s1, vector<Edge> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Vertex s1, vector<Face> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Vertex s1, vector<Tetra> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Edge s1, vector<Vertex> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Edge s1, vector<Edge> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Edge s1, vector<Face> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Edge s1, vector<Tetra> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Face s1, vector<Vertex> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Face s1, vector<Edge> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Face s1, vector<Face> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Face s1, vector<Tetra> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Tetra s1, vector<Vertex> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Tetra s1, vector<Edge> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Tetra s1, vector<Face> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}
void remove(Tetra s1, vector<Tetra> s2)
{
     for(int i = 0; i < s2.size(); i++)
     {
         remove(s1, s2[i]);
     }
}

void remove(vector<Vertex> s1, Vertex s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Vertex> s1, Edge s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Vertex> s1, Face s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Vertex> s1, Tetra s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Edge> s1, Vertex s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Edge> s1, Edge s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Edge> s1, Face s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Edge> s1, Tetra s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Face> s1, Vertex s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Face> s1, Edge s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Face> s1, Face s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Face> s1, Tetra s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Tetra> s1, Vertex s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Tetra> s1, Edge s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Tetra> s1, Face s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}
void remove(vector<Tetra> s1, Tetra s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         remove(s1[i], s2);
     }
}


void remove(vector<Vertex> s1, vector<Vertex> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Vertex> s1, vector<Edge> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Vertex> s1, vector<Face> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Vertex> s1, vector<Tetra> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Edge> s1, vector<Vertex> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Edge> s1, vector<Edge> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Edge> s1, vector<Face> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Edge> s1, vector<Tetra> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Face> s1, vector<Vertex> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Face> s1, vector<Edge> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Face> s1, vector<Face> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Face> s1, vector<Tetra> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Tetra> s1, vector<Vertex> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Tetra> s1, vector<Edge> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Tetra> s1, vector<Face> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
void remove(vector<Tetra> s1, vector<Tetra> s2)
{
     for(int i = 0; i < s1.size(); i++)
     {
         for(int j = 0; j < s2.size(); j++) 
         {
            remove(s1[i], s2[j]);
         }
     }
}
