package development;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;

public class OrientedFace {
  
  //associate the vertices of face f_ with coordinates so that
  //vect_[0]->vect_[1]->vect_[2] is in CCW order
  
  Face f_;
  Vertex vert_[];
  Vector vect_[];
  
  //public OrientedFace(Face face, AffineTransformation T){ }
  
  public OrientedFace(Face f, Vertex vert0, Vertex vert1, Vertex vert2, Vector vect0, Vector vect1, Vector vect2){
    
    vert_ = new Vertex[3];
    vect_ = new Vector[3];
    
    vert_[0] = vert0; vert_[1] = vert1; vert_[2] = vert2;
    vect_[0] = vect0; vect_[1] = vect1; vect_[2] = vect2;
    
    f_ = f;
  }
  
  public Face getFace(){ return f_; }
  public Vertex getVertex(int i){ return vert_[i]; }
  public Vector getVector(int i){ return vect_[i]; }
}
