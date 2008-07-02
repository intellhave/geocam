/**************************************************************
File: Triangulation Input/Output
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 20, 2008
***************************************************************
The Triangulation Input/Output file holds the functions that handle
the reading and writing of text files.
**************************************************************/

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
bool readTriangulationFile(char*);

/*
 * This function takes the current Triangulation and writes it to a file in
 * the format read in from readTriangulationFile. The file name is given as
 * the only parameter.
 */
void writeTriangulationFile(char*);

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

void printResultsStep(char*, vector<double>*, vector<double>*, int);
void printResultsVertex(char*, vector<double>*, vector<double>*, int);
void printResultsNum(char*, vector<double>*, vector<double>*, int);
