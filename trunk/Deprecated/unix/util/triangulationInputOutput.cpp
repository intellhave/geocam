/**************************************************************
File: Triangulation Input/Output
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 3, 2008
***************************************************************
The Triangulation Input/Output file holds the functions that handle
the reading and writing of text files.
**************************************************************/

#include <set>
#include <fstream>
#include <sstream>
#include <iomanip>
#include "triangulationInputOutput.h"
#include "math/miscmath.h"

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
 * that they do not need to be in any sort of order and don't need to be
 * numbered from 1 to n, though it is suggested for simplicity on the user's
 * part. 
 */
bool readTriangulationFile(const char* fileName) 
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
     }
     
}

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
 
bool Pair::isInTuple(vector<int>* tuple)
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
     }
     return result == 2;
}
 
void printResultsStep(char* fileName, vector<double>* radii, vector<double>* curvs)
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
         fprintf(results, "Step %5d     Radius          Curvature\n", i);
         fprintf(results, "-----------------------------------------------------\n");
         vit = Triangulation::vertexTable.begin();
         for(int j = 0; j < vertSize; j++)
         {
             fprintf(results, "Vertex %3d     %3.7f       %3.7f\n",
                   vit->first, 
                  (*radii)[i*vertSize + j], (*curvs)[i*vertSize+j]);
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

void printResultsVertex(char* fileName, vector<double>* radii, vector<double>* curvs)
{
     int vertSize = Triangulation::vertexTable.size();
     int numSteps = radii->size() / vertSize;
     FILE* results = fopen(fileName, "w");
     if(results == NULL) {
       fprintf(stderr, "Null file given by %s\n", fileName);           
     }
     map<int, Vertex>::iterator vit;
     vit = Triangulation::vertexTable.begin(); 
     for(int k=0; k < vertSize; k++) 
     { 
       fprintf(results, "Vertex: %3d\tRadius\tCurv\n", vit->first);
       fprintf(results, "\n---------------------------------\n");
       for(int j = 0; j < numSteps; j++)
       {
           fprintf(results, "Step %4d\t%3.7f\t%3.7f\n", j,
                (*radii)[j*vertSize + k], (*curvs)[j*vertSize + k]);
       }
       fprintf(results, "\n");
       vit++;
     }
     fclose(results);
}

void printResultsNum(char* fileName, vector<double>* radii, vector<double>* curvs)
{
     int vertSize = Triangulation::vertexTable.size();
     int numSteps = radii->size() / vertSize;
     FILE* results = fopen(fileName, "w");
     if(results == NULL) {
       fprintf(stderr, "Null file given by %s\n", fileName);           
     }
     map<int, Vertex>::iterator vit;
     vit = Triangulation::vertexTable.begin(); 
     for(int k=0; k < vertSize; k++) 
     {
       for(int j = 0; j < numSteps; j++)
       {
          fprintf(results, "%3.10f\t%3.10f\n",
                (*radii)[j*vertSize + k], (*curvs)[j*vertSize + k]);
       }
       fprintf(results, "\n");
       vit++;
     }
     fclose(results);
}

void printResultsNumSteps(char* fileName, vector<double>* radii, vector<double>* curvs)
{
     int vertSize = Triangulation::vertexTable.size();
     int numSteps = radii->size() / vertSize;
     FILE* results = fopen(fileName, "w");
     if(results == NULL) {
       fprintf(stderr, "Null file given by %s\n", fileName);           
     }
     map<int, Vertex>::iterator vit;
     vit = Triangulation::vertexTable.begin();
     for(int i = 0; i < numSteps; i++)
     {
         vit = Triangulation::vertexTable.begin();
         for(int j = 0; j < vertSize; j++)
         {
             fprintf(results, "%3.10f\n", 
                 (*curvs)[i*vertSize+j]);
             vit++;
         }
     }
     fclose(results);
}


void printDegrees(char* fileName)
{
     int vertSize = Triangulation::vertexTable.size();
     ofstream results(fileName, ios_base::trunc);
     results << left << setprecision(6); 
     results.setf(ios_base::showpoint);
     for (int i = 1; i <= vertSize; i++)
     {
         results << Triangulation::vertexTable[i].getDegree() << "\n";
     }
}


