/**************************************************************
File: Triangulation Input/Output
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 3, 2008
***************************************************************
The Triangulation Input/Output file holds the functions that handle
the reading and writing of text files under a two-dimensional manifold.
**************************************************************/

#include <fstream>
#include <sstream>
#include <set>
#include "3DInputOutput.h"
#include "triangulation/triangulationInputOutput.h"

bool read3DTriangulationFile(char* fileName) 
{
    // The three names of simplices.
    const string vertexString("Vertex");
    const string edgeString("Edge");
    const string faceString("Face");
    const string tetraString("Tetra");
    
    // The file stream.
    ifstream scanner(fileName);
    // Name of the simplex to be read in.
    char simplexName[15];
    
    while(scanner.good()) // While there is another simplex to read in...
    {
       scanner.getline(simplexName, 15, ':'); // ':' is the delimiter.
       if(simplexName == vertexString) 
       {
         int indexMapping;
         scanner >> indexMapping;
         Vertex v(indexMapping);
         
         char ignore[5];
         scanner.getline(ignore, 5); // Used to get to next line.
         
         char localVertices[100];
         char localEdges[100];
         char localFaces[100];
         char localTetras[100];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, 100);
         vertexStream << localVertices;
         
         while(vertexStream.good()) // While there's another int...
         {
              int index;
              vertexStream >> index;
              v.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, 100);
         edgeStream << localEdges;
         
          while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              v.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, 100);
         faceStream << localFaces;
         
          while(faceStream.good())
         {
              int index;
              faceStream >> index;
              v.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, 100);
         tetraStream << localTetras;
         
          while(tetraStream.good())
         {
              int index;
              tetraStream >> index;
              v.addTetra(index);
         }
         
         Triangulation::putVertex(indexMapping, v); //Finally add to table. 
       } 
       
       else if(simplexName == edgeString) 
       {
         int indexMapping;
         scanner >> indexMapping;
         Edge e(indexMapping);
         
         char ignore[5];
         scanner.getline(ignore, 5);
         
         char localVertices[100];
         char localEdges[100];
         char localFaces[100];
         char localTetras[100];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, 100);
         vertexStream << localVertices;
         
         while(vertexStream.good())
         {
              int index;
              vertexStream >> index;
              e.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, 100);
         edgeStream << localEdges;
         
         while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              e.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, 100);
         faceStream << localFaces;
         
         while(faceStream.good())
         {
              int index;
              faceStream >> index;
              e.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, 100);
         tetraStream << localTetras;
         
          while(tetraStream.good())
         {
              int index;
              tetraStream >> index;
              e.addTetra(index);
         }
         
         Triangulation::putEdge(indexMapping, e); 
       } 
       
       else if(simplexName == faceString) 
       {
         int indexMapping;
         scanner >> indexMapping;
         Face f(indexMapping);
         
         char ignore[5];
         scanner.getline(ignore, 5);
         
         char localVertices[100];
         char localEdges[100];
         char localFaces[100];
         char localTetras[100];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, 100);
         vertexStream << localVertices;
         
         while(vertexStream.good())
         {
              int index;
              vertexStream >> index;
              f.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, 100);
         edgeStream << localEdges;
         
          while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              f.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, 100);
         faceStream << localFaces;
         
          while(faceStream.good())
         {
              int index;
              faceStream >> index;
              f.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, 100);
         tetraStream << localTetras;
         
          while(tetraStream.good())
         {
              int index;
              tetraStream >> index;
              f.addTetra(index);
         }
         
         Triangulation::putFace(indexMapping, f);    
       }
       else if(simplexName == tetraString) 
       {
         int indexMapping;
         scanner >> indexMapping;
         Tetra t(indexMapping);
         
         char ignore[5];
         scanner.getline(ignore, 5);
         
         char localVertices[100];
         char localEdges[100];
         char localFaces[100];
         char localTetras[100];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, 100);
         vertexStream << localVertices;
         
         while(vertexStream.good())
         {
              int index;
              vertexStream >> index;
              t.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, 100);
         edgeStream << localEdges;
         
          while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              t.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, 100);
         faceStream << localFaces;
         
          while(faceStream.good())
         {
              int index;
              faceStream >> index;
              t.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, 100);
         tetraStream << localTetras;
         
          while(tetraStream.good())
         {
              int index;
              tetraStream >> index;
              t.addTetra(index);
         }
         
         Triangulation::putTetra(indexMapping, t);    
       }
              
       else 
       {
            scanner.close();
            return false; // File read unsuccessful.
       }
    } 
    
    scanner.close();
    return true; // File read successfully!
}

void write3DTriangulationFile(char* newFileName)
{
     ofstream output(newFileName);
     for(map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
     {
            
             
             output << "Vertex: " << vit->first <<"\n";
             
             for(int j = 0; j < vit->second.getLocalVertices()->size(); j++)
             {
                     output << (*(vit->second.getLocalVertices()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < vit->second.getLocalEdges()->size(); j++)
             {
                     output << (*(vit->second.getLocalEdges()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < vit->second.getLocalFaces()->size(); j++)
             {
                     output << (*(vit->second.getLocalFaces()))[j] << " ";
                   
             }
             output << "\n";
             for(int j = 0; j < vit->second.getLocalTetras()->size(); j++)
             {
                     output << (*(vit->second.getLocalTetras()))[j] << " ";
                   
             }
             output << "\n";
     }
     for(map<int, Edge>::iterator eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
     {
             output << "Edge: " << eit->first  << "\n";
             
             for(int j = 0; j < eit->second.getLocalVertices()->size(); j++)
             {
                     output << (*(eit->second.getLocalVertices()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < eit->second.getLocalEdges()->size(); j++)
             {
                     output << (*(eit->second.getLocalEdges()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < eit->second.getLocalFaces()->size(); j++)
             {
                     output << (*(eit->second.getLocalFaces()))[j] << " ";
             }
             output << "\n";
             for(int j = 0; j < eit->second.getLocalTetras()->size(); j++)
             {
                     output << (*(eit->second.getLocalTetras()))[j] << " ";
             }
             output << "\n";
     }
     
     for(map<int, Face>::iterator fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
     {
             output << "Face: " << fit->first;
             output << "\n";
             for(int j = 0; j < fit->second.getLocalVertices()->size(); j++)
             {
                     output << (*(fit->second.getLocalVertices()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < fit->second.getLocalEdges()->size(); j++)
             {
                     output << (*(fit->second.getLocalEdges()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < fit->second.getLocalFaces()->size(); j++)
             {
                     output << (*(fit->second.getLocalFaces()))[j] << " ";
             }
             output << "\n";
             for(int j = 0; j < fit->second.getLocalTetras()->size(); j++)
             {
                     output << (*(fit->second.getLocalTetras()))[j] << " ";
             }
             output << "\n";
     }
     
     for(map<int, Tetra>::iterator tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
     {
             output << "Tetra: " << tit->first;
             output << "\n";
             for(int j = 0; j < tit->second.getLocalVertices()->size(); j++)
             {
                     output << (*(tit->second.getLocalVertices()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < tit->second.getLocalEdges()->size(); j++)
             {
                     output << (*(tit->second.getLocalEdges()))[j] << " ";
             }
             output << "\n";
             
             for(int j = 0; j < tit->second.getLocalFaces()->size(); j++)
             {
                     output << (*(tit->second.getLocalFaces()))[j] << " ";
             }
             output << "\n";
             for(int j = 0; j < tit->second.getLocalTetras()->size(); j++)
             {
                     output << (*(tit->second.getLocalTetras()))[j] << " ";
             }
             output << "\n";
     }
}

struct Triple 
{
    int v1, v2, v3;
    int positionOf(vector<Triple>*);
    bool contains(int);
    bool isInTuple(vector<int>*);
};
bool Triple::contains(int i)
{
    return (v1 == i || v2 == i) || v3 == i;
}
int Triple::positionOf(vector<Triple>* triple) 
{
    for(int i = 0; i < triple->size(); i++)
    {
       Triple other = (*triple)[i];
       if(other.contains(v1) && other.contains(v2) && other.contains(v3))
          return i;
    }
    return -1;
}
bool Triple::isInTuple(vector<int>* tuple)
{
     int result = 0;
     for(int i = 0; i < tuple->size(); i++)
     {
          if(v1 == (*tuple)[i])
          {
                result++;
          }
          if(v2 == (*tuple)[i])
          {
                result++;
          }
          if(v3 == (*tuple)[i])
          {
                result++;
          }
     }
     return result == 3;
}


void make3DTriangulationFile(char* from, char* to) {
       ifstream infile;
  vector< vector<int> > t; 
  infile.open(from);
  // Place the sets of four ints into a vector.
  char temp[50]; // Temp gets rid of everything up to the '=' character.
  infile.getline(temp, 50 ,'=');
  char ch;
  infile >> ch;
  while (ch != ']') 
  { // While we haven't reached the end, marked by a ']' ...
     infile >> ch;
     vector<int> tetra; // Our set to hold the 4 ints of a tetra.
     for (int n = 1; n <= 4; n++)
     {
         int element;
         infile >> element;
         tetra.push_back(element);
         infile >> ch; // Gets the ',' we don't need it.
     }
     t.push_back(tetra); // Add the set to the vector.
     infile >> ch; // Gets the ']' marking the end of the current set.
  }
  infile.close();
  
  vector<int> vList;
  vector<Pair> eList;
  vector<Triple> fList;
  vector<int>::iterator it;
  
  /*
   * This "for" loop searches through every tetra and collects all 
   * vertices, pairs and triples.
   */
  for(int i = 0; i < t.size(); i++)
  {
      for (int j = 0; j < t[i].size(); j++)
      {
          it = find(vList.begin(), vList.end(), (t[i])[j]);
          if (it == vList.end())
          {
                 vList.push_back((t[i])[j]);
          }
      }
     Pair p1 = {(t[i])[0], (t[i])[1]};
     Pair p2 = {(t[i])[0], (t[i])[2]};
     Pair p3 = {(t[i])[0], (t[i])[3]};
     Pair p4 = {(t[i])[1], (t[i])[2]};
     Pair p5 = {(t[i])[1], (t[i])[3]};
     Pair p6 = {(t[i])[2], (t[i])[3]};
     Triple t1 = {(t[i])[0], (t[i])[1], (t[i])[2]};
     Triple t2 = {(t[i])[0], (t[i])[1], (t[i])[3]};
     Triple t3 = {(t[i])[0], (t[i])[2], (t[i])[3]};
     Triple t4 = {(t[i])[1], (t[i])[2], (t[i])[3]};
     if (p1.positionOf(&eList) == -1) eList.push_back(p1);
     if (p2.positionOf(&eList) == -1) eList.push_back(p2);
     if (p3.positionOf(&eList) == -1) eList.push_back(p3);
     if (p4.positionOf(&eList) == -1) eList.push_back(p4);
     if (p5.positionOf(&eList) == -1) eList.push_back(p5);
     if (p6.positionOf(&eList) == -1) eList.push_back(p6);
     if (t1.positionOf(&fList) == -1) fList.push_back(t1);
     if (t2.positionOf(&fList) == -1) fList.push_back(t2);
     if (t3.positionOf(&fList) == -1) fList.push_back(t3);
     if (t4.positionOf(&fList) == -1) fList.push_back(t4);
  }
  
  ofstream outfile;
  outfile.open(to, ios_base::trunc);
  
  // Vertices
  /*
   * Prints all the vertices, iterating through the list.
   */
  for (int i = 0; i < vList.size(); i++)
  {
      // Print the name.
      outfile << "Vertex: " << vList[i] << "\n";
      set<int> localv; // A collection of a vertex's local vertices.
      vector<int> localt; // A collection of a vertex's local tetras.
      
      // Iterate through all triples
      for (int k = 0; k < t.size(); k++)
      {   
          // Search for vertex in triple.
          it = find(t[k].begin(), t[k].end(), vList[i]);
          if (it != t[k].end())
          { // If vertex is in triple...add face to localf
                 localt.push_back(k+1);
                 // Add all the vertices of that face.
                 // NOTE: The use of a set prevents duplicates.
                 for(int g = 0; g < t[k].size(); g++)
                 {
                       localv.insert((t[k])[g]);
                 }
          }        
      }
      // The vertex itself was added to its localv's, remove it.
      localv.erase(vList[i]);
       
      // Print local vertices
      set<int>::iterator setit;
      for (setit = localv.begin(); setit != localv.end(); setit++)
      {
          outfile << *setit << " ";
      }   
      // Print local edges using list of pairs and contains function of Pair
      for (int j = 0; j < eList.size(); j++)
      {
          if (eList[j].contains(vList[i]))
          {
             outfile << j + 1 << " ";
          }   
      }
      // Print local faces using list of triple and contains function of Triple
      for (int j = 0; j < fList.size(); j++)
      {
          if (fList[j].contains(vList[i]))
          {
             outfile << j + 1 << " ";
          }   
      } 
      outfile << "\n";
      // Print local tetras
      for (int j = 0; j < localt.size(); j++)
      {
          outfile << localt[j] << " ";
      }
      outfile << "\n";
  }
}
