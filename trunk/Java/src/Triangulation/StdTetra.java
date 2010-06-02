package Triangulation;

import java.util.Iterator;

public class StdTetra {
  public Vertex v1;
  public Vertex v2;
  public Vertex v3;
  public Vertex v4;
  
  public Edge e12;
  public Edge e13;
  public Edge e14;
  public Edge e23;
  public Edge e24;
  public Edge e34;
  
  public Face f123;
  public Face f124;
  public Face f134;
  public Face f234;
  
  public StdTetra(Tetra t) {
    this(t, t.getLocalVertices().get(0));
  }
  
  public StdTetra(Tetra t, Vertex v) {
    v1 = v; 

    Vertex[] verts = new Vertex[3];
    int count = 0;
    
    for(Vertex w : t.getLocalVertices()) {
      if(w != v) {
        verts[count] = w;
        count++;
      }
    }
    v2 = verts[0];
    v3 = verts[1];
    v4 = verts[2];
    
    fixTetraEdges( t );
    fixTetraFaces( t );    
  }
  
  public StdTetra(Tetra t, Edge e) {
    StdTetra retval;
    
    Iterator<Vertex> localVertices = e.getLocalVertices().iterator();

    v1 = localVertices.next();
    v2 = localVertices.next();

    Vertex[] verts = new Vertex[2];
    int count = 0;
    for(Vertex v : t.getLocalVertices()) {
      if(v != v1 && v != v2) {
        verts[count] = v;
        count++;
      }
    }
    
    v3 = verts[0];
    v4 = verts[1];
    
    fixTetraEdges( t );
    fixTetraFaces( t );
  }
  
  public StdTetra(Tetra t, Face f) {    
    Iterator<Vertex> verts = f.getLocalVertices().iterator();
    
    v1 = verts.next();    
    v2 = verts.next(); 
    v3 = verts.next();     
    
    for(Vertex v : t.getLocalVertices()) {
      if(v != v1 && v != v2 && v != v3) {
        v4 = v;
      }
    }

    fixTetraEdges( t );
    fixTetraFaces( t );
  }
  
  public StdTetra(Tetra t, Vertex v, Vertex w) {
    Vertex[] verts;
    int count = 0;
    
    if(v == w) {
      v1 = v;
      verts = new Vertex[3];
      for(Vertex u : t.getLocalVertices()) {
        if(v1 != u) {
          verts[count] = u;
          count++;
        }
      }
      v2 = verts[0];
      v3 = verts[1];
      v4 = verts[2];
    } else {
      v1 = v;
      v2 = w;
      verts= new Vertex[2];
      for(Vertex u : t.getLocalVertices()) {
        if(v1 != u && v2 != u) {
          verts[count] = u;
          count++;
        }
      }
      v3 = verts[0];
      v4 = verts[1];
    }
    
    fixTetraEdges(t);
    fixTetraFaces(t);
  }
  
  // Make v1 = v, e12 = e if v is in e, e23 = e if not.
  public StdTetra(Tetra t, Vertex v, Edge e) {
    Vertex[] verts = new Vertex[2];
    int count = 0;
    
    v1 = v;
    
    if(e.isAdjVertex(v1)) {
      for(Vertex w : e.getLocalVertices()) {
        if(w != v1) {
          v2 = w;
          break;
        }
      }
      
      for(Vertex w : t.getLocalVertices()) {
        if(w != v1 && w != v2) {
          verts[count] = w;
          count++;
        }
      }
      
      v3 = verts[0];
      v4 = verts[1];
    } else {
      Iterator<Vertex> local_verts = e.getLocalVertices().iterator();
      v2 = local_verts.next();
      v3 = local_verts.next();
      
      for(Vertex w : t.getLocalVertices()) {
        if(w != v1 && w != v2 && w != v3) {
          v4 = w;
          break;
        }
      }
    }
    
    fixTetraEdges(t);
    fixTetraFaces(t);    
  }
  
//Make e12 = e, v1 = v if v is in e, v3 = v otherwise
  public StdTetra(Tetra t, Edge e, Vertex v) {
    if(e.isAdjVertex(v)) {
      v1 = v;
      for(Vertex w :  e.getLocalVertices()) {
        if(w != v) {
          v2 = w;
        }
      }
      Vertex[] verts = new Vertex[2];
      int count = 0;
      for(Vertex w : t.getLocalVertices()) {
        if(w != v1 && w != v2) {
          verts[count] = w;
          count++;
        }
      }
      v3 = verts[0];
      v4 = verts[1];
    } else {
      v1 = e.getLocalVertices().get(0);
      v2 = e.getLocalVertices().get(1);
      v3 = v;
      
      for(Vertex w : t.getLocalVertices()) {
        if( w != v1 && w != v2 && w != v3) {
          v4 = w;
          break;
        }
      }
    }

    fixTetraEdges(t);
    fixTetraFaces(t);
  }
  
  // Make e12 = e, e12 = f if e = f, e13 = f if e and f are adjacent,
  // e34 = f otherwise
  public StdTetra(Tetra t, Edge e, Edge f) {
    if(e == f) {
      v1 = e.getLocalVertices().get(0);
      v2 = e.getLocalVertices().get(1);
      
      Vertex[] verts = new Vertex[2];
      int count = 0;
      for(Vertex v : t.getLocalVertices()) {
        if(v != v1 && v != v2) {
          verts[count] = v;
          count++;
        }
      }
      v3 = verts[0];
      v4 = verts[1];
    } else if( e.isAdjEdge(f) ) {
      if(f.isAdjVertex(e.getLocalVertices().get(0))) {
        v1 = e.getLocalVertices().get(0);
        v2 = e.getLocalVertices().get(1);
      } else {
        v1 = e.getLocalVertices().get(1);
        v2 = e.getLocalVertices().get(0);
      }
      
      for(Vertex v : f.getLocalVertices()) {
        if(v != v1) {
          v3 = v;
          break;
        }
      }
      
      for(Vertex v : t.getLocalVertices()) {
        if(v != v1 && v != v2 && v != v3) {
          v4 = v;
          break;
        }
      }
    } else {
      v1 = e.getLocalVertices().get(0);
      v2 = e.getLocalVertices().get(1);
      v3 = f.getLocalVertices().get(0);
      v4 = f.getLocalVertices().get(1);
    }

    fixTetraEdges(t);
    fixTetraFaces(t);
  }
  
  public void fixTetraEdges( Tetra t ){
    for(Edge e : t.getLocalEdges()) {
      boolean b1 = e.isAdjVertex(v1);
      boolean b2 = e.isAdjVertex(v2);
      boolean b3 = e.isAdjVertex(v3);
      boolean b4 = e.isAdjVertex(v4);
      
      if( b1 && b2 ){
        e12 = e;
      } else if( b1 && b3 ){
        e13 = e;
      } else if( b1 && b4 ){
        e14 = e;
      } else if( b2 && b3 ){
        e23 = e;
      } else if( b2 && b4 ){
        e24 = e;
      } else if( b3 && b4 ){
        e34 = e;
      } 
    }
  }

  // Similar to fixTetraEdges, this procedure takes in a tetrahedron
  // and a StdTetra struct with correctly labeled edges.
  // From this information, it derives the correct labeling of
  // the faces of the tetrahedron.
  public void fixTetraFaces( Tetra t) {
    for(Face f : t.getLocalFaces()) {
      boolean b1 = f.isAdjEdge( e12 );
      boolean b2 = f.isAdjEdge( e13 );
      boolean b3 = f.isAdjEdge( e14 );
      boolean b4 = f.isAdjEdge( e34 );
      
      if( b1 && b2 ) {
        f123 = f; 
      } else if( b1 && b3 ) {
        f124 = f; 
      } else if( b2 && b3 ) {
        f134 = f;
      } else if( b4 && ! b1) {
        f234 = f;
      }
    }
  }
}
