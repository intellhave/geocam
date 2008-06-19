//way to transform data from website into our format

#include <simplex/edge.h>
#include <simplex/vertex.h>
#include <simplex/face.h>
#include <simplex/simplex.h>
#include <iostream>
#include <string>
#include <sstream>
#include "triangulation.h"
#include <fstream>
#include <vector>
#include <algorithm>
#include <set>

struct Pair
{
       int v1, v2;
       int positionOf(vector<Pair>*);  
       bool contains(int);  
};

int Pair::positionOf(vector<Pair>* list)
{
     for (int i = 0; i < (*list).size(); i++)
     {
         Pair other = (*list)[i];
         if ((this->v1 == other.v1 && this-> v2 == other.v2)||(this->v1 == other.v2 && this-> v2 == other.v1))
         {
                return i;    
         }
     }
     return -1;
}

bool Pair::contains(int i)
{
     if (i == v1 || i == v2)
     {
           return true;
     }
     else
         return false;
}
 
void makeTriangulationFile (char* from, char* to) {

  ifstream infile;
  vector< vector<int> > f; 
  infile.open(from);
  // >> i/o operations here <<
  char temp[50];
  infile.getline(temp, 50 ,'=');
  char ch;
  infile >> ch;
  while (ch != ']')
        {
            infile >> ch;
            vector<int> face;
            for (int n = 1; n <= 3; n++)
            {
                int element;
                infile >> element;
                face.push_back(element);
                infile >> ch;
            }
            f.push_back(face);
            infile >> ch;
        }
        
  vector<Pair> list; 
  for(int i = 0; i < f.size(); i++)
  {
     Pair p1 = {(f[i])[0], (f[i])[1]};
     Pair p2 = {(f[i])[0], (f[i])[2]};
     Pair p3 = {(f[i])[1], (f[i])[2]};
     if (p1.positionOf(&list) == -1)
     {
         list.push_back(p1);
     }
     if (p2.positionOf(&list) == -1)
     {
         list.push_back(p2);
     }
     if (p3.positionOf(&list) == -1)
     {
         list.push_back(p3);
     }
  }     
  
  vector<int> v;
  vector<int>::iterator it;
  for (int i = 0; i < f.size(); i++)
  {
      for (int j = 0; j < 3; j++)
      {
          it = find(v.begin(), v.end(), (f[i])[j]);
          if (it == v.end())
          {
                 v.push_back((f[i])[j]);
          }
      }
  }
   
  ofstream outfile;
  outfile.open(to);
  
  for (int i = 0; i < v.size(); i++)
  {
      outfile << "Vertex: " << v[i] << "\n";
      set<int> localv; 
      vector<int> localf; 
      for (int k = 0; k < f.size(); k++)
      {      
          it = find(f[k].begin(), f[k].end(), v[i]);
          if (it != v.end())
          {
                 localf.push_back(k+1);
                 for(int g = 0; g < 3; g++)
                 {
                       localv.insert((f[k])[g]);
                 }
          }        
      }
      localv.erase(v[i]);
      set<int>::iterator notit;
      set<int>::iterator end = localv.end();
      end--;
      
      for (notit = localv.begin(); notit != end; notit++)
      {
          outfile << *notit << " ";
      }
      outfile << *notit << "\n";    
      for (int j = 0; j < list.size(); j++)
      {
          if (list[j].contains(v[i]))
          {
             outfile << j + 1 << " ";
          }
             
      } 
      outfile << "\n";
      for (int i = 0; i < localf.size(); i++)
      {
          outfile << localf[i] << " ";
      } 
  }
  
  
  infile.close(); }
  


/* basic steps:
         
         Step1. Establishing faces and its vertices
         
             Get input in text format as a string
             
             Ignore everything up to =
             
             Find number of faces
                  idea: count # of '[', subtract 1
                  
             Make faces vector thing of right length
             
             Get rid of all non numbers in string
                 turn it into an int vector or so
                      
             Store three at a time into the index of faces[i]
         
         Step2. Establishing vertices vectors
         
             Find the largest vertex #
             
             Create a vertex file of that size
             
         Step3. Establishing an edge vector
         
             Use vertices as a for loop to determine # edges
             int edgenumber = 0;
             for (i = 1, i < max v, i++){
                 for (int j = i+1, j<= max v, j++){
                             for (int t = 1; t = number of faces; t++){
                                 if(i and j are in one of the faces together){
                                  edgenumber++;
                                               if possible, and i and j in vector as we make it?
                                  }
                             }
                     }
                 }
             Create edge vector thing of size edgenumber
             
         Step4. Linking vertices to neighbors, edges, faces
                neighbors: 
                          run through edges
                          if vertex i is in edge e, add other vertex to neighbor list
                edges:
                          run through edges
                          if vertex i is in edge e, add edge to list
                faces:
                          run through faces
                          if vertex i is in face f, add face to list
         
         Step5. Linking edges to vertices, neighbors, and faces
                vertices:
                             
                edges:
                      
                faces:
                      
         Step6. Linking faces to vertices, edges, and neighbors
                vertices: 
                          already done- how we started
                edges: 
                       
                faces: 
                       
         Step 7. Compiling it all
         
                For (int i = 1; i <= vertexSize; i++){
                    add "Vertex: "i to beginning of index
                likewise for Edges, Faces
                                                     
                Output result in text format
*/



