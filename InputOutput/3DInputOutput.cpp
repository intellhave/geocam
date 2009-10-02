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
#include <iomanip>
#include <cmath>
#include "3DInputOutput.h"
#include "triangulationInputOutput.h"
#include "volume.h"
#include "miscmath.h"

bool read3DTriangulationFile(char* fileName) 
{
    // The three names of simplices.
    const string vertexString("Vertex");
    const string edgeString("Edge");
    const string faceString("Face");
    const string tetraString("Tetra");
    const int MAXLINE = 1000;
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
         
         char localVertices[MAXLINE];
         char localEdges[MAXLINE];
         char localFaces[MAXLINE];
         char localTetras[MAXLINE];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, MAXLINE);
         vertexStream << localVertices;
         
         while(vertexStream.good()) // While there's another int...
         {
              int index;
              vertexStream >> index;
              v.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, MAXLINE);
         edgeStream << localEdges;
         
          while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              v.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, MAXLINE);
         faceStream << localFaces;
         
          while(faceStream.good())
         {
              int index;
              faceStream >> index;
              v.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, MAXLINE);
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
         
         char localVertices[MAXLINE];
         char localEdges[MAXLINE];
         char localFaces[MAXLINE];
         char localTetras[MAXLINE];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, MAXLINE);
         vertexStream << localVertices;
         
         while(vertexStream.good())
         {
              int index;
              vertexStream >> index;
              e.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, MAXLINE);
         edgeStream << localEdges;
         
         while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              e.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, MAXLINE);
         faceStream << localFaces;
         
         while(faceStream.good())
         {
              int index;
              faceStream >> index;
              e.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, MAXLINE);
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
         
         char localVertices[MAXLINE];
         char localEdges[MAXLINE];
         char localFaces[MAXLINE];
         char localTetras[MAXLINE];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, MAXLINE);
         vertexStream << localVertices;
         
         while(vertexStream.good())
         {
              int index;
              vertexStream >> index;
              f.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, MAXLINE);
         edgeStream << localEdges;
         
          while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              f.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, MAXLINE);
         faceStream << localFaces;
         
          while(faceStream.good())
         {
              int index;
              faceStream >> index;
              f.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, MAXLINE);
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
         
         char localVertices[MAXLINE];
         char localEdges[MAXLINE];
         char localFaces[MAXLINE];
         char localTetras[MAXLINE];
         
         stringstream vertexStream(stringstream::in | stringstream::out);
         scanner.getline(localVertices, MAXLINE);
         vertexStream << localVertices;
         
         while(vertexStream.good())
         {
              int index;
              vertexStream >> index;
              t.addVertex(index);
         }
         
         stringstream edgeStream(stringstream::in | stringstream::out);
         scanner.getline(localEdges, MAXLINE);
         edgeStream << localEdges;
         
          while(edgeStream.good())
         {
              int index;
              edgeStream >> index;
              t.addEdge(index);
         }
         
         stringstream faceStream(stringstream::in | stringstream::out);
         scanner.getline(localFaces, MAXLINE);
         faceStream << localFaces;
         
          while(faceStream.good())
         {
              int index;
              faceStream >> index;
              t.addFace(index);
         }
         
         stringstream tetraStream(stringstream::in | stringstream::out);
         scanner.getline(localTetras, MAXLINE);
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
    bool contains(Pair);
    bool isInTuple(vector<int>*);
};
bool Triple::contains(int i)
{
    return (v1 == i || v2 == i) || v3 == i;
}
bool Triple::contains(Pair p)
{
     return contains(p.v1) && contains(p.v2);
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
      outfile << "\n";
      // Print local edges using list of pairs and contains function of Pair
      for (int j = 0; j < eList.size(); j++)
      {
          if (eList[j].contains(vList[i]))
          {
             outfile << j + 1 << " ";
          }   
      }
      outfile << "\n";
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
  
    //Edges
  /*
   * Prints all the edges, iterating through the list of pairs.
   */
  for(int i = 0; i < eList.size(); i++)
  {
          // name
          outfile << "Edge: " << i + 1 << "\n";
          Pair current = eList[i];
          // local vertices, just the two ints of the current pair.
          outfile << current.v1 << " " << current.v2 << "\n";
          // local edges, iterate through list finding pairs that share one vertex
          // with this. Ignore the current pair.
          for(int j = 0; j < eList.size(); j++)
          {
             if(j != i && 
                (eList[j].contains(current.v1) || eList[j].contains(current.v2)))
             {
                 outfile << j + 1 << " "; 
             }
          }
          outfile << "\n";
          // Iterate through triples, check if pair is in triple with
          // contains(Pair) function of triple.
          for(int j = 0; j < fList.size(); j++)
          {
                  if(fList[j].contains(current))
                  {
                      outfile << j + 1 << " ";
                  }
          }
          outfile << "\n";
          // Iterate through tetras, check if pair is in tetra with
          // isInTuple function for Pairs.
          for(int j = 0; j < t.size(); j++)
          {
                  if(current.isInTuple(&(t[j])))
                  {
                      outfile << j + 1 << " ";
                  }
          }
          outfile << "\n";
  }
  //Faces
  /*
   * Prints all the faces, iterating through the list.
   */
  for(int i = 0; i < fList.size(); i++)
  {
      // name
      outfile << "Face: " << i + 1 << "\n";
      Triple current = fList[i];
      // local vertices, just the ints in current triple.
      outfile << current.v1 << " " << current.v2 << " " << current.v3;
      outfile << "\n";
      // local edges, if pair is in triple, add it to local edges.
      for(int j = 0; j < eList.size(); j++)
      {
              if(current.contains(eList[j]))
              {
                 outfile << j + 1 << " ";
              }
      }
      outfile << "\n";
      // local faces, check for triples that have two vertices in common
      // with current one.
      for(int j = 0; j < fList.size(); j++)
      {
         if( j != i)
         {
            Triple other = fList[j];
            Pair p1 = {current.v1, current.v2};
            Pair p2 = {current.v1, current.v3};
            Pair p3 = {current.v2, current.v3};
            if(other.contains(p1) || other.contains(p2) || other.contains(p3))
            {
               outfile << j + 1 << " ";
            }
         }
      }
      outfile << "\n";
      // Iterate through tetras, check if triple is in tetra with
      // isInTuple function.
      for(int j = 0; j < t.size(); j++)
      {
          if(current.isInTuple(&(t[j])))
          {
               outfile << j + 1 << " ";
          }
      }
      outfile << "\n";
  }
  
  //Tetras
    /*
   * Prints all the tetras, iterating through the list.
   */
  for(int i = 0; i < t.size(); i++)
  {
      // name
      outfile << "Tetra: " << i + 1 << "\n";
      vector<int> current = t[i];
      // local vertices, just the ints in current tetra.
      for(int j = 0; j < current.size(); j++)
      {
              outfile << current[j] << " ";
      }
      outfile << "\n";
      // local edges, if pair is in tetra, add it to local edges.
      for(int j = 0; j < eList.size(); j++)
      {
              if((eList[j]).isInTuple(&current))
              {
                 outfile << j + 1 << " ";
              }
      }
      outfile << "\n";
      // local faces, if triple is in tetra, add it to loacl faces.
      for(int j = 0; j < fList.size(); j++)
      {
              if((fList[j]).isInTuple(&current))
              {
                 outfile << j + 1 << " ";
              }
      }
      outfile << "\n";
      // local tetras, check if tetra has three vertices in common with
      // other tetras.
      for(int j = 0; j < t.size(); j++)
      {
         if( j != i)
         {
            vector<int> inter = listIntersection(&current, &(t[j]));
            if(inter.size() >= 3)
            {
               outfile << j + 1 << " ";
            }
         }
      }
      outfile << "\n";   
  }
  outfile.close();
}

void printResultsVolumes(char* fileName, vector<double>* volumes)
{
     int tetraSize = Triangulation::tetraTable.size();
     int numSteps = volumes->size() / tetraSize;
     FILE* results = fopen(fileName, "w");
     if(results == NULL) {
       fprintf(stderr, "Null file given by %s\n", fileName);           
     }
     map<int, Tetra>::iterator tit;

     for(int i = 0; i < numSteps; i++)
     {
         double netVolume = 0.;
         fprintf(results, "Step %5d     Volume\n", i + 1);
         fprintf(results, "---------------------------\n");
         tit = Triangulation::tetraTable.begin();
         for(int j = 0; j < tetraSize; j++)
         {
             double volume = (*volumes)[i*tetraSize + j];
             fprintf(results, "Tetra %3d     %3.7f\n", tit->first, volume);
             netVolume += volume;
             tit++;
         }
         if(netVolume < 0.0000001 && netVolume > -0.0000001)
         {
                    netVolume= 0.;
         }
         fprintf(results, "Total Volume: %4.7f\n\n", netVolume);
     }
     fclose(results); 
}

void readEtas(char* filename)
{
     FILE* file = fopen(filename, "r");
     int index;
     double eta;
     double dummy;
     for(int i = 0; i < Triangulation::edgeTable.size(); i++) {
        fscanf(file, "Edge %d:\t%lf\t%lf\n", &index, &eta, &dummy);
        //fscanf(file, "Edge %d: %lf - %lf\n", &index, &eta, &dummy);
        Eta::At(Triangulation::edgeTable[index])->setValue(eta);
     }
     fclose(file);
}

void print3DResultsStep(char* fileName, vector<double>* radii, vector<double>* curvs)
{
     int vertSize = Triangulation::vertexTable.size();
     int numSteps = radii->size() / vertSize;
     FILE* results = fopen(fileName, "w");
     if(results == NULL) {
       fprintf(stderr, "Null file given by %s\n", fileName);
       return;        
     }
     
     map<int, Vertex>::iterator vit;
     for(int i = 0; i < numSteps; i++)
     {
         double netCurv = 0.;
         fprintf(results, "Step %5d     Radius          Curvature        Curv:Radius\n", i);
         fprintf(results, "-----------------------------------------------------\n");
         vit = Triangulation::vertexTable.begin();
         for(int j = 0; j < vertSize; j++)
         {
             fprintf(results, "Vertex %3d     %3.7f       %3.7f       %3.7f\n",
                   vit->first, 
                  (*radii)[i*vertSize + j], (*curvs)[i*vertSize+j],
                   (*curvs)[i*vertSize+j] / (*radii)[i*vertSize + j]);
             netCurv += (*curvs)[i*vertSize+j];
             vit++;
         }
         if(netCurv < 0.0000001 && netCurv > -0.0000001)
         {
                    netCurv = 0.;
         }
         fprintf(results, "Total Curvature: %4.7f\n\n", netCurv);
     }
     fclose(results);
}
