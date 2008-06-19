#include <cstdlib>
#include <iostream>
#include <vector>
#include "simplex/simplex.h"
#include <map>
#include <fstream>
#include <string>
#include "simplex/vertex.h"
#include <sstream>
#include "triangulation.h"
#include "triangulationmath.h"


/*
 * Function to read in a file and build the Triangulation. The 
 * function parses the file and creates the simplices, adding them to
 * the Triangulation tables. A file should be formatted as below:
 *                
 *               Vertex: 1     <---  Simplex name (Vertex 1)
 *               2 3 4         <---  Local vertices
 *               1 2 3         <---  Local edges
 *               1 2 3         <---  Local faces
 *               Vertex: 2     <---  Next simplex (Vertex 2)
 *               1 3 4
 *               1 4 5
 *               1 2 4
 *               :
 *               :
 *               Edge: 5       <--- Another simplex (Edge 5)
 *               1 2
 *               2 3 4 5
 *               1 2
 *               :
 *               :
                 
          (Note: ":" on its own line is being used to show that the 
 *         file continues in the same style.)       
 *                    
 * A simplex is first declared by its name (Vertex, Edge, or Face)
 * followed by a colon and the integer that represents it in the table.
 * Underneath this are three lines of integers, representing in order,
 * the local vertices, edges, and faces of the simplex. No indication
 * for the end of each line or the declaration of a simplex is needed, but
 * every simplex must have three lines of integers. The current
 * maximum size for a list of local simplices is 24. Finally, note that 
 * not all vertices must be listed before declaring edges or faces and 
 * that they do not need to be in any sort of order or numbering scheme. 
 */

bool readTriangulationFile(char* fileName) 
{
    // The three names of simplices.
    const string vertexString("Vertex");
    const string edgeString("Edge");
    const string faceString("Face");
    
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
         
         Triangulation::putFace(indexMapping, f);    
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

void writeTriangulationFile(char* newFileName)
{
     ofstream output(newFileName);
     
     for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
     {
             output << "Vertex: " << i << "\n";
             
             for(int j = 0; j < Triangulation::vertexTable[i].getLocalVertices()->size(); j++)
             {
                     output << ((*(Triangulation::vertexTable[i].getLocalVertices()))[j]) << " ";
             }
             output << "\n";
             
             for(int j = 0; j < Triangulation::vertexTable[i].getLocalEdges()->size(); j++)
             {
                     output << ((*(Triangulation::vertexTable[i].getLocalEdges()))[j]) << " ";
             }
             output << "\n";
             
             for(int j = 0; j < Triangulation::vertexTable[i].getLocalFaces()->size(); j++)
             {
                     output << ((*(Triangulation::vertexTable[i].getLocalFaces()))[j]) << " ";
             }
             output << "\n";
     }
     
     for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
     {
             output << "Edge: " << i << "\n";
             
             for(int j = 0; j < Triangulation::edgeTable[i].getLocalVertices()->size(); j++)
             {
                     output << ((*(Triangulation::edgeTable[i].getLocalVertices()))[j]) << " ";
             }
             output << "\n";
             
             for(int j = 0; j < Triangulation::edgeTable[i].getLocalEdges()->size(); j++)
             {
                     output << ((*(Triangulation::edgeTable[i].getLocalEdges()))[j]) << " ";
             }
             output << "\n";
             
             for(int j = 0; j < Triangulation::edgeTable[i].getLocalFaces()->size(); j++)
             {
                     output << ((*(Triangulation::edgeTable[i].getLocalFaces()))[j]) << " ";
             }
             output << "\n";
     }
     
     for(int i = 1; i <= Triangulation::faceTable.size(); i++)
     {
             output << "Face: " << i << "\n";
             
             for(int j = 0; j < Triangulation::faceTable[i].getLocalVertices()->size(); j++)
             {
                     output << ((*(Triangulation::faceTable[i].getLocalVertices()))[j]) << " ";
             }
             output << "\n";
             
             for(int j = 0; j < Triangulation::faceTable[i].getLocalEdges()->size(); j++)
             {
                     output << ((*(Triangulation::faceTable[i].getLocalEdges()))[j]) << " ";
             }
             output << "\n";
             
             for(int j = 0; j < Triangulation::faceTable[i].getLocalFaces()->size(); j++)
             {
                     output << ((*(Triangulation::faceTable[i].getLocalFaces()))[j]) << " ";
             }
             output << "\n";
     }
     
}
