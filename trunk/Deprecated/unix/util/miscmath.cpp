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
#include <cstdio>

vector<int> listIntersection(vector<int>* list1, vector<int>* list2){
  vector<int> sameAs;
             
  for(int i = 0; i < (*list1).size(); i++){
    for(int j = 0; j < (*list2).size(); j++){
      if((*list1)[i] == (*list2)[j]) {
	sameAs.push_back((*list1)[i]);
	break;
      }
    }
  }
  return sameAs;
}

vector<int> listDifference(vector<int>* list1, vector<int>* list2){
  vector<int> diff;
            
  for(int i = 0; i < (*list1).size(); i++){
    for(int j = 0; j < (*list2).size(); j++){
      if((*list1)[i] == (*list2)[j]) break;
      if(j == (*list2).size() - 1) 
	diff.push_back((*list1)[i]);
    }
  }
  return diff;
}

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

StdTetra labelTetra( Tetra& t ){
  Vertex& v = Triangulation::vertexTable[ (*(t.getLocalVertices()))[0] ];
  return labelTetra( t, v );
}

