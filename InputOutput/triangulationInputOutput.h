/**************************************************************
File: Triangulation Input/Output
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 3, 2008
***************************************************************
The Triangulation Input/Output file holds the functions that handle
the reading and writing of text files under a two-dimensional manifold.
**************************************************************/

#include "triangulation/triangulation.h"
#ifndef triangulationInputOutput
#define triangulationInputOutput
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
bool readTriangulationFile(const char*);

/*
 * This function takes the current Triangulation and writes it to a file in
 * the format read in from readTriangulationFile. The file name is given as
 * the only parameter.
 */
void writeTriangulationFile(char*);

void writeTriangulationFileWithData(char*);
/*
 * Reads a file in the format found on The Manifold Page, 
 * http://www.math.tu-berlin.de/diskregeom/stellar/ , and writes to another
 * file in the standard format used in this project. The file name to be read
 * is the first parameter, the file to write to is the second parameter.
 * 
 * Writing a triangulation in this other format can sometimes be simpler,
 * just create one as shown below:
 *      =[[1,2,3],[1,2,4],[2,3,4],[1,3,4]]
 * The '=' sign indicates the beginning of the Triangulation information.      
 */
void makeTriangulationFile(char*, char*);

/*
 * Prints the results of a calcFlow into the file given. Requires two vectors
 * of doubles, one representing the changing radii and the second the
 * curvatures. The number of vertices is assumed to be the current number in
 * the triangulation and the number of steps is then derived from the size of
 * the vectors. The file is cleared before writing, and the format is grouping
 * by step as shown below:
 *            :
 *          Vertex 6: 10.6390     -1.21810
 *
 *          Step 5    Radius       Curv
 *          ----------------------------
 *          Vertex 1: 15.2785     -5.90324    
 *          Vertex 2: 12.3130     -1.03506    
 *          Vertex 3: 12.7753     -0.856032   
 *          Vertex 4: 24.1124     -2.15461    
 *          Vertex 5: 10.8755     -1.19773    
 *          Vertex 6: 10.3957     -1.41971 
 *          
 *          Step 7    Radius       Curv
 *          ----------------------------
 *            :  
 */
void printResultsStep(char*, vector<double>*, vector<double>*);

/*
 * Prints the results of a calcFlow into the file given. Requires two vectors
 * of doubles, one representing the changing radii and the second the
 * curvatures. The number of vertices is assumed to be the current number in
 * the triangulation and the number of steps is then derived from the size of
 * the vectors. The file is cleared before writing, and the format is grouping
 * by vertex as shown below:
 *            :
 *           Step 99     20.8731     -2.09440    
 *           Step 100    20.8731     -2.09440    
 *
 *           Vertex: 2   Radius      Curv
 *           ------------------------------
 *           Step 1      14.0000     1.92788     
 *           Step 2      12.5241     1.29678     
 *           Step 3      11.4253     0.638433    
 *           Step 4      10.6237     0.0369037
 *            :
 */
void printResultsVertex(char*, vector<double>*, vector<double>*);

/*
 * Prints the results of a calcFlow into the file given. Requires two vectors
 * of doubles, one representing the changing radii and the second the
 * curvatures. The number of vertices is assumed to be the current number in
 * the triangulation and the number of steps is then derived from the size of
 * the vectors. The file is cleared before writing, and the format is grouped
 * by vertex but with no labels. It is designed for easy importing into an
 * excel document. An example is hsown below:
 *            :
 *           27.9836       -2.09439    
 *           27.9836       -2.09439    
 *           27.9836       -2.09439    
 *
 *           3.00000       -6.69377    
 *           3.42280       -6.28556    
 *           3.85808       -5.88621    
 *           4.29797       -5.50438
 *            :
 */
void printResultsNum(char*, vector<double>*, vector<double>*);
void printResultsNumSteps(char* fileName, vector<double>* radii, vector<double>* curvs);

struct Pair
{
       int v1, v2;
       int positionOf(vector<Pair>*);  
       bool contains(int);
       bool isInTuple(vector<int>*);
};
#endif
