/**************************************************************
File: Triangulation Input/Output
Author: Alex Henniges, Tom Williams
Version: Sept 15, 2008
***************************************************************
The  3D Triangulation Input/Output file holds the functions that handle
the reading and writing of text files under a three-dimensional manifold.
**************************************************************/

#include "triangulation.h"

/*
 * Function to read in a file and build the a three-dimensional
 * Triangulation. The function parses the file and creates the 
 * simplices, adding them to the Triangulation tables. A file 
 * should be formatted as below:
 *                
 *               Vertex: 1     <---  Simplex name (Vertex 1)
 *               2 3 4         <---  Local vertices
 *               1 2 3 5       <---  Local edges
 *               1 2 3 6       <---  Local faces
 *               2 4 5         <---  Local tetras
 *               Vertex: 2     <---  Next simplex (Vertex 2)
 *               1 3 4
 *               1 4 5 
 *               1 2 4
 *               1 3 4
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
 * A simplex is first declared by its name (Vertex, Edge, Face, or Tetra)
 * followed by a colon and the integer that represents it in the table.
 * Underneath this are four lines of integers, representing in order,
 * the local vertices, edges, faces, and tetras of the simplex. No indication
 * for the end of each line or the declaration of a simplex is needed, but
 * every simplex must have four lines of integers. The current
 * maximum size for a list of local simplices is 24. Finally, note that 
 * not all vertices must be listed before declaring edges or faces or tetras
 * and that they do not need to be in any sort of order and don't need to be
 * numbered from 1 to n, though it is suggested for simplicity on the user's
 * part. 
 */
bool read3DTriangulationFile(char* filename);

/*
 * This function takes the current 3-dimensional Triangulation and writes 
 * it to a file in the format read in from read3DTriangulationFile. The file
 * name is given as the only parameter.
 */
void write3DTriangulationFile(char* newFileName);

void make3DTriangulationFile(char* from, char* to);

void print3DResultsStep(char* fileName, vector<double>* radii, vector<double>* curvs);

void printResultsVolumes(char* fileName, vector<double>* volumes);

void readEtas(char* fileneame);
