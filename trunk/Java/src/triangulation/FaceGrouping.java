package triangulation;

import java.util.ArrayList;
import java.util.List;

/*********************************************************************************
 * FaceGrouping
 * 
 * This class is responsible for holding information about Faces which belong to
 * the same surface on the manifold. Each instance of FaceGrouping holds a list
 * of Faces that are all part of a particular surface on the manifold. This
 * allows us to compute consistent texture coordinates for all faces on that
 * surface.
 *********************************************************************************/

public class FaceGrouping {

  private List<Face> faces;
  
  /*********************************************************************************
   * constructors, add, and contains methods are self-explanatory
   *********************************************************************************/
  public FaceGrouping() {
    faces = new ArrayList<Face>();
  }

  public FaceGrouping(Face f) {
    faces = new ArrayList<Face>();
    faces.add(f);
  }

  public boolean add(Face f) {
    if (!faces.contains(f))
      return faces.add(f);
    return false;
  }

  public boolean contains(Face f) {
    return faces.contains(f);
  }

  /*********************************************************************************
   * getFaces
   * 
   * Note that getFaces returns a copy of the FaceGrouping's list so that
   * clients cannot alter the underlying data in the Triangulation.
   *********************************************************************************/
  public List<Face> getFaces() {
    List<Face> copy = new ArrayList<Face>();
    for (Face f : faces)
      copy.add(f);
    return copy;
  }
}
