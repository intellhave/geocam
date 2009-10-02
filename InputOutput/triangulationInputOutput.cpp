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
#include "triangulationinputoutput.h"
#include "length.h"
#include "area.h"
#include "miscmath.h"


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
         char specifiedradius[100];
         
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
         
         //vertex radius info
         char nextChar;
         double radius = 0;
         nextChar = scanner.peek();
         if(!(nextChar == 'V' || nextChar == 'E' || nextChar == 'F')) {
            stringstream radiusStream(stringstream::in | stringstream::out);
            scanner.getline(specifiedradius, 100);
            radiusStream << specifiedradius;

            while(radiusStream.good())
            {
                radiusStream >> radius;
            }
         }
         
         Triangulation::putVertex(indexMapping, v); //Finally add to table.
         //sets the radius to a value that was given, zero if nothing was specified
         Radius::At(Triangulation::vertexTable[indexMapping])->setValue(radius);
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
         char specifiedlength[100];
         
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
         
         //edge length info
         char nextChar;
         double len = 0;
         nextChar = scanner.peek();
         if(!(nextChar == 'V' || nextChar == 'E' || nextChar == 'F')) {
            stringstream lengthStream(stringstream::in | stringstream::out);
            scanner.getline(specifiedlength, 100);
            lengthStream << specifiedlength;

            while(lengthStream.good())
            {
                lengthStream >> len;
            }
         }
         
         Triangulation::putEdge(indexMapping, e);
         //sets the length to a value given, zero if no value was specified
         Length::At(Triangulation::edgeTable[indexMapping])->setValue(len);
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

//void buildTriangulation(char* filename) {
//   FILE* file = fopen(filename, "r");
//   if(file == NULL) {
//      printf("buildTriangulation error: file does not exist.\n");
//      return;
//   }
//   char
//}

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

void writeTriangulationFileWithData(char* newFileName)
{
     ofstream output(newFileName);
     for(map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
     {
            
             
             output << "Vertex: " << vit->first <<  "Radius: " << Radius::valueAt(vit->second);
             //output << " Angle sum: " << getAngleSum(vit->second) << "\n";
             
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
             output << "Edge: " << eit->first  << "Length: " << Length::valueAt(eit->second) << "\n";
             
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
             output << "Face: " << fit->first << "Negativity: " << fit->second.isNegative();
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
 
void makeTriangulationFile (char* from, char* to) {

  ifstream infile;
  vector< vector<int> > f; 
  infile.open(from);
    if (infile.fail()) {
        fprintf(stderr, "!!!!!!!!!!!!!!!!!\nThere was an error in reading the file you specified\nThe file name you provided to makeTriangulationFile\nwas likely incorrect\n!!!!!!!!!!!!!!!!!\n");
        system("PAUSE");
        exit(1);
    }
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
  
  infile.close();      
  vector<Pair> list;
  /*
   * This "for" loop searches through every triple and collects all pairs.
   * If the pair has not yet been added to the list of all pairs, add it.
   * Uses the positionOf function of the struct Pair to search the list.
   */
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
  /*
   * This "for" loop iterates through every triple and collects all vertices.
   * If a vertex has not yet been added to the list of all vertices, add it.
   * Uses the find function in vector to search through the list.
   */
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
  outfile.open(to, ios_base::trunc);
  // Vertices
  /*
   * Prints all the vertices, iterating through the list.
   */
  for (int i = 0; i < v.size(); i++)
  {
      // Print the name.
      outfile << "Vertex: " << v[i] << "\n";
      set<int> localv; // A collection of a vertex's local vertices.
      vector<int> localf; // A collection of a vertex's local faces.
      
      // Iterate through all triples
      for (int k = 0; k < f.size(); k++)
      {   
          // Search for vertex in triple.
          it = find(f[k].begin(), f[k].end(), v[i]);
          if (it != f[k].end())
          { // If vertex is in triple...add face to localf
                 localf.push_back(k+1);
                 // Add all the vertices of that face.
                 // NOTE: The use of a set prevents duplicates.
                 for(int g = 0; g < 3; g++)
                 {
                       localv.insert((f[k])[g]);
                 }
          }        
      }
      // The vertex itself was added to its localv's, remove it.
      localv.erase(v[i]);
      // Print local vertices
      set<int>::iterator notit;
      for (notit = localv.begin(); notit != localv.end(); notit++)
      {
          outfile << *notit << " ";
      } 
      outfile << "\n";
      // Print local edges using list of pairs and contians function of Pair
      
      for (int j = 0; j < list.size(); j++)
      {
          if (list[j].contains(v[i]))
          {
             outfile << j + 1 << " ";
          }
             
      } 
      outfile << "\n";
      
      // Print local faces
      for (int j = 0; j < localf.size(); j++)
      {
          outfile << localf[j] << " ";
      }
      outfile << "\n";
  }
  
  //Edges
  /*
   * Prints all the edges, iterating through the list of pairs.
   */
  for(int i = 0; i < list.size(); i++)
  {
          // name
          outfile << "Edge: " << i + 1 << "\n";
          Pair current = list[i];
          // local vertices, just the two ints of the current pair.
          outfile << current.v1 << " " << current.v2 << "\n";
          // local edges, iterate through list finding pairs that share one vertex
          // with this. Ignore the current pair.
          for(int j = 0; j < list.size(); j++)
          {
             if(j != i && 
                (list[j].contains(current.v1) || list[j].contains(current.v2)))
             {
                 outfile << j + 1 << " "; 
             }
          }
          outfile << "\n";
          // Iterate through triples, check if pair is in triple with
          // isInTriple function.
          for(int j = 0; j < f.size(); j++)
          {
                  if(current.isInTuple(&(f[j])))
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
  for(int i = 0; i < f.size(); i++)
  {
        //this looks weird but iwill explain
        if (i > 0) {
            //this new line was inserted after each face was specified, but this left
            //a new line at the end of the file which interfered with the file reading function
            outfile << "\n";
        }
      // name
      outfile << "Face: " << i + 1 << "\n";
      vector<int> current = f[i];
      // local vertices, just the ints in current triple.
      for(int j = 0; j < current.size(); j++)
      {
              outfile << current[j] << " ";
      }
      outfile << "\n";
      // local edges, if pair is in triple, add it to local edges.
      for(int j = 0; j < list.size(); j++)
      {
              if((list[j]).isInTuple(&current))
              {
                 outfile << j + 1 << " ";
              }
      }
      outfile << "\n";
      // local faces, check for triples that have two vertices in common
      // with current one.
      for(int j = 0; j < f.size(); j++)
      {
         if( j != i)
         {
            vector<int> inter = listIntersection(&current, &(f[j]));
            if(inter.size() >= 2)
            {
               outfile << j + 1 << " ";
            }
         }
      }
  }
  outfile.close();
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
                 (*curvs)[i*vertSize+j] / (*radii)[i*vertSize+j]);
             vit++;
         }
     }
     fclose(results);
}

void setLengths(double* lengths)
{
     map<int, Edge>::iterator eit;
     int i = 0;
     for(eit = Triangulation::edgeTable.begin(); 
             eit != Triangulation::edgeTable.end(); eit++)
     {
       Length::At(eit->second)->setValue(lengths[i]);
       i++;
     }
}

