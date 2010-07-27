package development;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import triangulation.Face;
import triangulation.Tetra;
import triangulation.Vertex;

public class OrientedTetra {
  
  //from a tetra and an affine transformation, construct oriented faces
  //corresponding to each local face of tetra_

  Tetra tetra_;
  OrientedFace orientedFaces[];
  
  public OrientedTetra(Tetra tetra, AffineTransformation T){
    
    //store reference to tetra
    tetra_ = tetra;
    
    //get vertices and store transformed coordinates
    HashMap<Vertex, Vector> transCoords = new HashMap<Vertex, Vector>();
    
    Iterator<Vertex> vi = tetra.getLocalVertices().iterator();
    for(int i=0; i<4; i++){
      
      Vertex vert = vi.next();
      
      Vector vect = null;
      try { vect = T.affineTransPoint(Coord3D.coordAt(vert, tetra)); }
      catch (Exception e) { e.printStackTrace(); }
      
      transCoords.put(vert, vect);
    }
   
    //determine oriented faces
    orientedFaces = new OrientedFace[4];
    
    Iterator<Face> fi = tetra.getLocalFaces().iterator();
    for(int j=0; j<4; j++){

      Face f = fi.next();

      //get shared vertices of cur_tetra and next_tetra (i.e. verts of f)
      Vertex vert[] = new Vertex[4];
      Iterator<Vertex> fvi = f.getLocalVertices().iterator();
      for(int i=0; i<3; i++){ vert[i] = fvi.next(); }

      //get non-common vertex on cur_tetra
      LinkedList<Vertex> leftover = new LinkedList<Vertex>(tetra.getLocalVertices());
      leftover.removeAll(f.getLocalVertices());
      vert[3] = leftover.get(0);
      
      //get transformed coordinates
      Vector[] vect = new Vector[4];
      for(int i=0; i<4; i++){ vect[i] = transCoords.get(vert[i]); }
      
      //make orientation of verts 0-1-2 is CCW
      Vector u = Vector.subtract(vect[1], vect[0]);
      Vector v = Vector.subtract(vect[2], vect[0]);
      Vector w = Vector.subtract(vect[3], vect[0]);
      
      //want cross(u,v) in opposite direction of w, i.e. (uxv).w < 0
      double z = Vector.dot(Vector.cross(u,v),w);
      if(z > 0){ //flip orientation
        Vertex tempvert = vert[2];
        Vector tempvect = vect[2];
        vert[2] = vert[1];
        vect[2] = vect[1];
        vert[1] = tempvert;
        vect[1] = tempvect;
      }
      
      //store orientedface
      orientedFaces[j] = new OrientedFace(f, vert[0], vert[1], vert[2], vect[0], vect[1], vect[2]);
    }
  }
  
  public Tetra getTetra(){ return tetra_; }
  public OrientedFace getOrientedFace(int i){ return orientedFaces[i]; }
}
