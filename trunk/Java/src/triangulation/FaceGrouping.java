package triangulation;

import java.util.ArrayList;
import java.util.LinkedList;
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
  
  /*********************************************************************************
   * getLocalEdges
   * 
   * This method returns a list containing all the edges that lie around the
   * outside of the face group, but not the internal edges (i.e. it returns the
   * edges of the larger polygon the group represents).
   * 
   * To find these edges it inspects the local edges of every face in the group.
   * Since each edge has at most two faces connected to it, this means that each
   * edge may be inspected either once or twice. If it is inspected once, this
   * means it belongs to only one face in the group and should be returned. If
   * it is inspected twice, this means that it belongs to two faces in the
   * triangulation and is therefore an "internal" edge which should not be
   * returned. Consequently, the first time an edge is inspected it is added to
   * the list of edges to be returned. If it is inspected a second time, it is
   * removed from the list.
   *********************************************************************************/
  public List<Edge> getLocalEdges() {
    List<Edge> edges = new LinkedList<Edge>();
    for(Face f : faces)
      for(Edge e : f.getLocalEdges()){
        if(edges.contains(e))
          edges.remove(e);
        else
          edges.add(e);
      }
    return edges;
  }
}
