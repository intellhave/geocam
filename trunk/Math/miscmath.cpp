/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/
#include "math/miscmath.h"
#include <cmath>
#include <cstdio>

vector<int> listIntersection(vector<int>* list1, vector<int>* list2){
  vector<int> sameAs;

  map<int, bool> indexExists;

  for (int i = 0; i < list1->size(); i++) {
    indexExists[(*list1)[i]] = true;
  }

  for (int i = 0; i < list2->size(); i++) {
    if (indexExists[(*list2)[i]]) {
      sameAs.push_back((*list2)[i]);
      indexExists[(*list2)[i]] = false;
    }
  }

  return sameAs;
}

vector<int> listDifference(vector<int>* list1, vector<int>* list2){
  vector<int> diff;

  map<int, bool> indexExists;

  for (int i = 0; i < list2->size(); i++) {
    indexExists[(*list2)[i]] = true;
  }

  for (int i = 0; i < list1->size(); i++) {
    if (!indexExists[(*list1)[i]]) {
      diff.push_back((*list1)[i]);
      indexExists[(*list1)[i]] = true;
    }
  }

  return diff;
}

vector<int> multiplicityUnion(vector<int> * list1, vector<int> * list2) {
  vector<int> merge;
  for (int i = 0; i < list1->size(); i++) {
    merge.push_back((*list1)[i]);
  }
  for (int i = 0; i < list2->size(); i++) {
    merge.push_back((*list2)[i]);
  }
  return merge;
}

vector<int> multiplicityIntersection(vector<int> * list1, vector<int> * list2) {
  vector<int> inter;

  map<int, int> indexToQuantity;

  for (int i = 0; i < list1->size(); i++) {
    indexToQuantity[(*list1)[i]] = indexToQuantity[(*list1)[i]] + 1;
  }

  for (int i = 0; i < list2->size(); i++) {
    if (indexToQuantity[(*list2)[i]] > 0) {
      inter.push_back((*list2)[i]);
      indexToQuantity[(*list2)[i]] = indexToQuantity[(*list2)[i]] - 1;
    }
  }

  return inter;
}

vector<int> multiplicityDifference(vector<int> * list1, vector<int> * list2) {
  vector<int> diff;

  map<int, int> indexToQuantity;

  for (int i = 0; i < list2->size(); i++) {
    indexToQuantity[(*list2)[i]] = indexToQuantity[(*list2)[i]] + 1;
  }

  for (int i = 0; i < list1->size(); i++) {
    if (indexToQuantity[(*list1)[i]] > 0) {
      indexToQuantity[(*list1)[i]] = indexToQuantity[(*list1)[i]] - 1;
    } else {
      diff.push_back((*list1)[i]);
    }
  }

  return diff;

}

//==============================================================================
// return 1 if system not solving
// nDim - system dimension
// pfMatr - matrix with coefficients
// pfVect - vector with free members
// pfSolution - vector with system solution
// pfMatr becames trianglular after function call
// pfVect changes after function call
//
// Developer: Henry Guennadi Levkin
//
//==============================================================================
int LinearEquationsSolver(Matrix<double>& pfMatr, double* pfVect, double* pfSolution, int nDim)
{
  double maxElem; // Used to determine row with max element.
  double temp; // Just a temp variable for switching

  int i, j, k, m; // indexing variables

  // For each column in matrix...
  for(k=0; k<(nDim-1); k++)
  // Here k will act as both the starting row and the
  // current column for the sub-matrix.
  {
    // Find the row with the max element in column k
    // (ignoring rows above k already completed).
    // NOTE: This is to lessen potential rounding errors.

    maxElem = fabs( pfMatr[k][k] ); // First element
    m = k; // m is the index of the currently found max row
    for(i=k+1; i<nDim; i++) // Iterate through every row below k.
    {
      if(maxElem < fabs(pfMatr[i][k]) )
      { // If this row has the new max element... set values.
        maxElem = fabs(pfMatr[i][k]);
        m = i;
      }
    }
    // Permute the kth row with the row with the max element (if necessary).
    if(m != k)
    {
      for(i=k; i<nDim; i++)
      {
        temp               = pfMatr[k][i];
        pfMatr[k][i] = pfMatr[m][i];
        pfMatr[m][i] = temp;
      }
      // Also permute the vector.
      temp = pfVect[k];
      pfVect[k] = pfVect[m];
      pfVect[m] = temp;
    }

    if( pfMatr[k][k] == 0.0) {
      return 1; // Matrix has a column of all 0s !!!
    }

    // Triangulate matrix by turning every value in column k below row k
    // into a 0 using row operations.
    for(j=(k+1); j<nDim; j++) // j is each row below row k.
    {
      // The multiplicative value in the row operation.
      temp = - pfMatr[j][k] / pfMatr[k][k];
       // For each entry in row j to the right of column k
      for(i=k; i<nDim; i++)
      { // perform row operation
        pfMatr[j][i] += temp*pfMatr[k][i];
      }
      // Also perform operation on the vector
      pfVect[j] = pfVect[j] + temp*pfVect[k];
    }
  }

  // For each row beginning with the bottom
  for(k=(nDim-1); k>=0; k--)
  { // Calculate the solution for variable k.
    pfSolution[k] = pfVect[k];
    // Adjust solution using variables already known.
    for(i=(k+1); i<nDim; i++)
    {
      pfSolution[k] -= (pfMatr[k][i]*pfSolution[i]);
    }
    // If the solution is 0, don't bother dividing.
    if(fabs(pfMatr[k][k]) < 0.000000001) {
       pfSolution[k] = 0;
    } else {
      pfSolution[k] = pfSolution[k] / pfMatr[k][k];
    }
  }

  return 0;
}

vector<double> quadratic(double a, double b, double c)
{
   /*                       _________
                     +   _ | 2
               -b    -    \|b  - 4*a*c
         x =  -------------------------
                         2*a
   */ 
   vector<double> solutions;
   double inside = b*b - 4*a*c; // The value inside the square root
   if(inside < 0)
   {
     // No real solutions
     return solutions;
   }
   if(inside == 0)
   {
     // Only one solution
     double sol = b * (-1) / (2*a);
     solutions.push_back(sol);
     return solutions;
   }
   double sol1 = ((-1)*b + sqrt(inside)) / (2*a);
   double sol2 = ((-1)*b - sqrt(inside)) / (2*a);
   solutions.push_back(sol1);
   solutions.push_back(sol2);
   return solutions;
}

double distancePoint(Point a, Point b)
{
   /*         ____________________________             
           _ |           2              2    
     d =    \|(a_x - b_x)  + (a_y - b_y)
   */
   return sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
}

vector<Point> circleIntersection(Point center1, double r1, Point center2, double r2)
{
    vector<Point> solutions;
    if(distancePoint(center1 , center2) > (r1 + r2))
    {
        // No solutions
        return solutions;
    }
    if(center1.x == center2.x && center1.y == center2.y)
    {
       if(r1 == r2)
       {
             // Same circle, infinite options, return same point.
             solutions.push_back(center1);
             return solutions;
       }
       else{
       // No solutions
          return solutions;
       }
    }
    if(distancePoint(center1, center2) < fabs(r1 - r2))
    {
       return solutions;
    }
    // (x-x1)^2 + (y-y1)^2 = r1^2
    // (x-x2)^2 + (y-y2)^2 = r2^2
    
    /* x^2 - 2*x1*x + x1^2 + y^2 - 2*y1*y + y1^2 = r1^2
       -
       x^2 - 2*x2*x + x2^2 + y^2 - 2*y2*y + y2^2 = r2^2
       ------------------------------------------------
       (2*x2 - 2*x1)x +(2*y2 - 2*y2)y = r1^2 - x1^2 - y1^2 - (r2^2 - x2^2 - y2^2)
       
       xComp*x + yComp*y = rComp
    */
    double xComp = 2 * center2.x - 2 * center1.x;
    double yComp = 2 * center2.y - 2 * center1.y;
    double r1Comp = r1*r1 - center1.x * center1.x - center1.y * center1.y;
    double r2Comp = r2*r2 - center2.x * center2.x - center2.y * center2.y;
    double rComp = r1Comp - r2Comp;
    
    if(yComp == 0)
    {
       /* x = rComp / xComp
                         _________________
          y = y1  +/-  \|r1^2 - (x - x1)^2  
       */    
       double ySol1 = sqrt(r1*r1 - pow((rComp/xComp - center1.x), 2)) + center1.y;
       double ySol2 = (-1)*sqrt(r1*r1 - pow((rComp/xComp - center1.x),2)) + center1.y;
       Point sol1(rComp/xComp, ySol1);
       Point sol2(rComp/xComp, ySol2);
       solutions.push_back(sol1);
       solutions.push_back(sol2);
       return solutions;
    }
    if(xComp == 0)
    {
        /* y = rComp / yComp
                          _________________
           x = x1  +/-  \|r1^2 - (y - y1)^2  
        */
       double xSol1 = sqrt(r1*r1 - pow((rComp/yComp - center1.y), 2)) + center1.x;
       double xSol2 = (-1)*sqrt(r1*r1 - pow((rComp/yComp - center1.y),2)) + center1.x;
       Point sol1(xSol1, rComp/yComp);
       Point sol2(xSol2, rComp/yComp);
       solutions.push_back(sol1);
       solutions.push_back(sol2);
       return solutions;
    }
    
    /*
        y = (rComp - xComp*x) / yComp
        
     => x^2 - 2*x1*x + x1^2 + ((rComp - xComp*x) / yComp - y1)^2 - r1^2 = 0
        
     => (1 + xComp/yComp)^2 * x^2 - 2*[x1 - (rComp/yComp - y1)*xComp/yComp] * x
           + x1^2 + (rComp/yComp - y1)^2 - r1^2 = 0
    */
    double b = (-2)*center1.x - 2*(rComp/yComp - center1.y)*(xComp/yComp);

    double a = 1 + pow((xComp/yComp), 2);

    double c = center1.x*center1.x + pow((rComp/yComp - center1.y), 2) - r1*r1;

    // Find x solutions using quadratic formula.
    vector<double> quadSol = quadratic(a, b, c);
    for(int i = 0; i < quadSol.size(); i++)
    {
        
        double xSol = quadSol[i];
        double ySol = rComp/yComp - xComp/yComp*xSol;

        Point sol(xSol, ySol);
        solutions.push_back(sol);
    }
    return solutions;
}

vector<Point> circleIntersection(Circle circle1, Circle circle2)
{
    Point center1 = circle1.getCenter();
    Point center2 = circle2.getCenter();
    double r1 = circle1.getRadius();
    double r2 = circle2.getRadius();              
    return circleIntersection(center1, r1, center2, r2);
}

Point rotateVector(Point vector, double angle)
{
      double x = vector.x*cos(angle) - vector.y*sin(angle);
      double y = vector.x*sin(angle) + vector.y*cos(angle);
      if(x < 0.00000001 && x > -0.00000001) // Error checking
           x = 0;
      if(y < 0.00000001 && y > -0.00000001) // Error checking
           y = 0;
      Point p(x,y);
      return p;
}

Point findPoint(Line l, double length, double angle)
{
      Point vect(l.getEndingX()-l.getInitialX(), l.getEndingY() - l.getInitialY());
      Point rotate = rotateVector(vect, angle);
      Point vectResult(rotate.x/l.getLength()*length,
                       rotate.y/l.getLength()*length);
      Point p(l.getInitialX() + vectResult.x, l.getInitialY() + vectResult.y);
      return p;
}

double angle(double len1, double len2, double len3) {
     return acos((len1*len1 + len2*len2 - len3*len3)/ (2*len1*len2));  
}

/***************BEGINING OF EDGE LABELING CODE*****************/
StdEdge labelEdge( Edge& e, Vertex& v ){
  StdEdge retval;

  vector<int>* localVertices = e.getLocalVertices();
  retval.v1 = v.getIndex();
  
  for(int ii = 0; ii < localVertices->size(); ii++){
    if( localVertices->at(ii) != retval.v1 )
      retval.v2 = localVertices->at( ii );
  }

  return retval;
}

// This is a helper procedure for the "labelFace" procedures.
// The input StdFace record has correctly labeled vertices.
// From these, fixFaceEdges derives the correct labeling of
// the edges of the input Face f
void fixFaceEdges( Face& f, StdFace& sf ){
 vector<int>* localEdges = f.getLocalEdges();
  for(int ii = 0; ii < localEdges->size(); ii++ ){
    Edge& ed = Triangulation::edgeTable[ localEdges->at(ii) ];

    bool b1 = ed.isAdjVertex( sf.v1 );
    bool b2 = ed.isAdjVertex( sf.v2 );
    bool b3 = ed.isAdjVertex( sf.v3 );

    if( b1 && b2 ){ sf.e12 = ed.getIndex(); }
    else if( b1 && b3 ){ sf.e13 = ed.getIndex(); }
    else if( b2 && b3 ){ sf.e23 = ed.getIndex(); }
    else{
      fprintf( stderr, "Corrupt Edge Data!\n");
    }
  }   
}

StdFace labelFace( Face& f, Edge& e ){
  StdFace retval;

  vector<int>* localVertices = e.getLocalVertices();

  retval.v1 = localVertices->at(0);
  retval.v2 = localVertices->at(1);

  vector<int>* allVertices = f.getLocalVertices();
  int ii;
  for( ii = 0; ii < localVertices->size(); ii++ ){
    if( allVertices->at( ii ) != retval.v1 &&
	allVertices->at( ii ) != retval.v2 )
      break;
  }
  retval.v3 = allVertices->at( ii );
   
  fixFaceEdges( f, retval );
  
  return retval;
}

StdFace labelFace(Face& f, Vertex& v){
  StdFace retval;

  retval.v1 = v.getIndex();

  vector<int> otherVertices;
  vector<int>* localVertices = f.getLocalVertices();  
  int ii;
  for( ii = 0; ii < localVertices->size(); ii++ ){
    int jj = localVertices->at( ii );
    if( jj != v.getIndex() )
      otherVertices.push_back( jj );
  }

  retval.v2 = otherVertices[0];
  retval.v3 = otherVertices[1];
       
  fixFaceEdges( f, retval );
  
  return retval;
}

/*****************************************************/
// As in fixFaceEdges, this procedure takes in a tetrahedron
// and a StdTetra struct with correctly labeled vertices.
// From this information, it derives the correct labeling of
// the edges of the tetrahedron.
void fixTetraEdges( Tetra& t, StdTetra& st ){
  vector<int>* localEdges = t.getLocalEdges();
  for( int ii = 0; ii < localEdges->size(); ii++ ){
    Edge ed = Triangulation::edgeTable[ localEdges->at( ii ) ];
    
    bool b1 = ed.isAdjVertex( st.v1 );
    bool b2 = ed.isAdjVertex( st.v2 );
    bool b3 = ed.isAdjVertex( st.v3 );
    bool b4 = ed.isAdjVertex( st.v4 );

    if( b1 && b2 ){
      st.e12 = ed.getIndex();
    } else if( b1 && b3 ){
      st.e13 = ed.getIndex();
    } else if( b1 && b4 ){
      st.e14 = ed.getIndex();
    } else if( b2 && b3 ){
      st.e23 = ed.getIndex();
    } else if( b2 && b4 ){
      st.e24 = ed.getIndex();
    } else if( b3 && b4 ){
      st.e34 = ed.getIndex();
    } else {
      fprintf( stderr, "Error! Corrupt Tetra data!\n" );
    }
  }  
}

// Similar to fixTetraEdges, this procedure takes in a tetrahedron
// and a StdTetra struct with correctly labeled edges.
// From this information, it derives the correct labeling of
// the faces of the tetrahedron.
void fixTetraFaces( Tetra& t, StdTetra& st ){
  vector<int>* localFaces = t.getLocalFaces();
  for( int ii = 0; ii < localFaces->size(); ii++ ){    
    Face& f = Triangulation::faceTable[ localFaces->at(ii) ];

    bool b1 = f.isAdjEdge( st.e12 );
    bool b2 = f.isAdjEdge( st.e13 );
    bool b3 = f.isAdjEdge( st.e14 );
    bool b4 = f.isAdjEdge( st.e34 );
    
    if( b1 && b2 ){ st.f123 = f.getIndex(); }
    else if( b1 && b3 ){ st.f124 = f.getIndex(); }
    else if( b2 && b3 ){ st.f134 = f.getIndex(); }
    else if( b4 && ! b1){ st.f234 = f.getIndex(); }
    else {
      fprintf( stderr, "Error! Corrupt Tetra data!\n" );
    }
  }
}

StdTetra labelTetra(Tetra& t, Vertex& v){
  StdTetra retval;

  retval.v1 = v.getIndex(); 

  vector<int>* localVertices = t.getLocalVertices();
  vector<int> verts; 
  int index = v.getIndex();

  for( int ii = 0; ii < localVertices->size(); ii++ ){
    int jj = localVertices->at( ii );
    if( jj != index ){
      verts.push_back( jj ); 
    }
  }
  
  retval.v2 = verts[0];
  retval.v3 = verts[1];
  retval.v4 = verts[2];
  
  fixTetraEdges( t, retval );
  fixTetraFaces( t, retval );

  return retval;
}


StdTetra labelTetra(Tetra& t, Edge& e){
  StdTetra retval;

  vector<int>* localVertices = e.getLocalVertices();
  vector<int> verts; 

  retval.v1 = localVertices->at(0);
  retval.v2 = localVertices->at(1);

  localVertices = t.getLocalVertices();
  for( int ii = 0; ii < localVertices->size(); ii++ ){
    int jj =  localVertices->at( ii );
    if( jj != retval.v1 && jj != retval.v2 ){
      verts.push_back( jj ); 
    }
  }

  retval.v3 = verts[0];
  retval.v4 = verts[1];
  
  fixTetraEdges( t, retval );
  fixTetraFaces( t, retval );
  
  return retval;
}

StdTetra labelTetra(Tetra& t, Face& f){
  StdTetra retval;
 
  vector<int>* verts = f.getLocalVertices();
  
  retval.v1 = verts->at(0);    
  retval.v2 = verts->at(1); 
  retval.v3 = verts->at(2);     
  
  vector<int> diff = listDifference( t.getLocalVertices(), f.getLocalVertices() );
  retval.v4 = diff[0]; 

  fixTetraEdges( t, retval );
  fixTetraFaces( t, retval );
    
  return retval;
}

// Make v1 = v, e12 = e if v is in e, e23 = e if not.
StdTetra labelTetra( Tetra& t, Vertex& v, Edge& e ){
  StdTetra retval;
  
  vector<int>* local_verts = e.getLocalVertices();
  vector<int> verts;
  int vIndex;
  
  retval.v1 = v.getIndex();
  
  if(e.isAdjVertex(retval.v1)) {
    if(local_verts->at(0) == retval.v1) {
      retval.v2 = local_verts->at(1);
    } else {
      retval.v2 = local_verts->at(0);
    }
    local_verts = t.getLocalVertices();
    for(int ii = 0; ii < local_verts->size(); ii++) {
      vIndex = local_verts->at(ii);
      if(vIndex != retval.v1 && vIndex != retval.v2) {
        verts.push_back(vIndex);
      }
    }
    retval.v3 = verts[0];
    retval.v4 = verts[1];
  } else {
    retval.v2 = local_verts->at(0);
    retval.v3 = local_verts->at(1);
    
    local_verts = t.getLocalVertices();
    for(int ii = 0; ii < local_verts->size(); ii++) {
      vIndex = local_verts->at(ii);
      if(vIndex != retval.v1 && vIndex != retval.v2 && vIndex != retval.v3) {
        retval.v4 = vIndex;
      }
    }
  }
  
  fixTetraEdges(t, retval);
  fixTetraFaces(t, retval);
  
  return retval;
}

// Make e12 = e, v1 = v if v is in e, v3 = v otherwise
StdTetra labelTetra( Tetra& t, Edge& e, Vertex& v ){
  StdTetra retval;

  vector<int>* local_verts = e.getLocalVertices();
  vector<int> verts;
  int vIndex;

  if(e.isAdjVertex(v.getIndex())) {
    retval.v1 = v.getIndex();
    if(local_verts->at(0) == retval.v1) {
      retval.v2 = local_verts->at(1);
    } else {
      retval.v2 = local_verts->at(0);
    }
    
    local_verts = t.getLocalVertices();
    for(int ii = 0; ii < local_verts->size(); ii++) {
      vIndex = local_verts->at(ii);
      if(vIndex != retval.v1 && vIndex != retval.v2) {
        verts.push_back(vIndex);
      }
    }
    retval.v3 = verts[0];
    retval.v4 = verts[1];
  } else {
    retval.v1 = local_verts->at(0);
    retval.v2 = local_verts->at(1);
    retval.v3 = v.getIndex();
    
    local_verts = t.getLocalVertices();
    for(int ii = 0; ii < local_verts->size(); ii++) {
      vIndex = local_verts->at(ii);
      if(vIndex != retval.v1 && vIndex != retval.v2 && vIndex != retval.v3) {
        retval.v4 = vIndex;
      }
    }
  }

  fixTetraEdges(t, retval);
  fixTetraFaces(t, retval);

  return retval;
}

// Make e12 = e, e12 = f if e = f, e13 = f if e and f are adjacent,
// e34 = f otherwise
StdTetra labelTetra( Tetra& t, Edge& e, Edge& f ){
  StdTetra retval;

  vector<int>* local_verts = e.getLocalVertices();
  vector<int>* local_verts2 = f.getLocalVertices();
  vector<int> verts;
  int vIndex;

  if(e.getIndex() == f.getIndex()) {
    retval.v1 = local_verts->at(0);
    retval.v2 = local_verts->at(1);
    local_verts = t.getLocalVertices();
    for(int ii = 0; ii < local_verts->size(); ii++) {
      vIndex = local_verts->at(ii);
      if(vIndex != retval.v1 && vIndex != retval.v2) {
        verts.push_back(vIndex);
      }
    }
    retval.v3 = verts[0];
    retval.v4 = verts[1];
  } else if( e.isAdjEdge(f.getIndex()) ) {
    if(f.isAdjVertex(local_verts->at(0))) {
      retval.v1 = local_verts->at(0);
      retval.v2 = local_verts->at(1);
    } else {
      retval.v1 = local_verts->at(1);
      retval.v2 = local_verts->at(0);
    }
    
    if(local_verts2->at(0) == retval.v1) {
      retval.v3 = local_verts2->at(1);
    } else {
      retval.v3 = local_verts2->at(0);
    }
    
    local_verts = t.getLocalVertices();
    for(int ii = 0; ii < local_verts->size(); ii++) {
      vIndex = local_verts->at(ii);
      if(vIndex != retval.v1 && vIndex != retval.v2 && vIndex != retval.v3) {
        retval.v4 = vIndex;
      }
    }
  } else {
    retval.v1 = local_verts->at(0);
    retval.v2 = local_verts->at(1);
    retval.v3 = local_verts2->at(0);
    retval.v4 = local_verts2->at(1);
  }

  fixTetraEdges(t, retval);
  fixTetraFaces(t, retval);

  return retval;
}

StdTetra labelTetra( Tetra& t ){
  Vertex& v = Triangulation::vertexTable[ (*(t.getLocalVertices()))[0] ];
  return labelTetra( t, v );
}

