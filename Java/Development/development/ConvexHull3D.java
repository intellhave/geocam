package development;

import java.util.ArrayList;
import java.util.HashSet;

public class ConvexHull3D {
  private static final double epsilon = Math.pow(10, -6);
  private ArrayList<Face> faces = new ArrayList<Face>();
  private ArrayList<Vector> unsorted;
  private ArrayList<Face> visible = new ArrayList<Face>();
  private ArrayList<Face> hiddenFaces = new ArrayList<Face>();
  private ArrayList<Face> newF = new ArrayList<Face>();
  private ArrayList<Vector> vertices = new ArrayList<Vector>();
  private Vector lastPoint = null;

  public ConvexHull3D(ArrayList<Vector> unsortedVectors) {
    unsorted = unsortedVectors;
    findHull(unsorted);
    lastPoint = unsorted.get(0);
  }

  public ConvexHull3D(Vector... vectors) {
    ArrayList<Vector> unsortedVectors = new ArrayList<Vector>();
    for (int i = 0; i < vectors.length; i++)
      unsortedVectors.add(vectors[i]);
    findHull(unsortedVectors);
  }

  public ConvexHull3D(ConvexHull3D original) {
    faces = new ArrayList<Face>();
    unsorted = new ArrayList<Vector>();

    for (int i = 0; i < original.getNumberFaces(); i++) {
      faces.add(new Face(original.getFaceAt(i)));
    }
    ArrayList<Vector> oldUnsorted = original.getUnsorted();
    for (int i = 0; i < oldUnsorted.size(); i++) {
      unsorted.add(new Vector(oldUnsorted.get(i)));
    }
  }

  private ArrayList<Vector> getUnsorted() {
    return unsorted;
  }

  public int getNumberFaces() {
    return faces.size();
  }
  
  public Face getFaceAt(int i) {
    return faces.get(i);
  }
  
  public ArrayList<Face> getFaces() {
    ArrayList<Face> list = new ArrayList<Face>();
    for(int i = 0; i < faces.size(); i++)
      list.add(new Face(faces.get(i)));
    return list;
  }
  
  // TODO for testing
  public Vector lastPoint() {
    return lastPoint;
  }

  public ArrayList<Face> getHiddenFaces() {
    return hiddenFaces;
  }
  
  public ArrayList<Face> getVisibleFaces() {
    return visible;
  }
  
  public ArrayList<Face> getNewFaces() {
    return newF;
  }
  
  public ArrayList<Vector> getVertices() {
    ArrayList<Vector> newVertices = new ArrayList<Vector>();
    for(int i = 0; i < vertices.size(); i++) {
     newVertices.add(new Vector(vertices.get(i))); 
    }
    return newVertices;
  }

  /*
   * Constructs a face from the first three points, then a tetrahedron with the
   * first non-coplanar point. Then adds additional points individually with
   * addPoint method.
   */
  private void findHull(ArrayList<Vector> unsorted) {
    int i = 3;
    Vector v1 = Vector.subtract(unsorted.get(1), unsorted.get(0));
    Vector v2 = Vector.subtract(unsorted.get(2), unsorted.get(0));

    while (coplanar(v1, v2, Vector.subtract(unsorted.get(i), unsorted.get(0)))) {
      i++;
    }

    Face f1 = new Face(unsorted.get(0), unsorted.get(1), unsorted.get(2));
    double dot = Vector.dot(f1.getNormal(), Vector.subtract(
        unsorted.get(i), unsorted.get(0)));
    if (dot > 0) { // change order
      f1 = new Face(unsorted.get(0), unsorted.get(2), unsorted.get(1));
    }

    // complete initial tetrahedron
    Face f2 = new Face(f1.getVectorAt(1), f1.getVectorAt(0), unsorted.get(i));
    Face f3 = new Face(f1.getVectorAt(2), f1.getVectorAt(1), unsorted.get(i));
    Face f4 = new Face(f1.getVectorAt(0), f1.getVectorAt(2), unsorted.get(i));

    unsorted.remove(i);
    unsorted.remove(2);
    unsorted.remove(1);
    unsorted.remove(0);

    faces.add(f1);
    faces.add(f2);
    faces.add(f3);
    faces.add(f4);

    // TODO restore when done testing
    // while(!unsorted.isEmpty()) {
    // addPoint(unsorted.get(0));
    // unsorted.remove(0);
    // }
  }

  // TODO remove after done testing
  public boolean addPoint() {
    if (unsorted.isEmpty())
      return false;
    addPoint(unsorted.get(0));
    return true;
  }
  
  private boolean coplanar(Vector v1, Vector v2, Vector v3) {
    Vector cross1 = Vector.cross(v1, v2);
    Vector cross2 = Vector.cross(v2, v3);
    Vector result = Vector.cross(cross1, cross2);
    return result.length() < epsilon;
  }

  /*
   * 1. Finds the faces "visible" from and coplanar with the given vector 2.
   * Constructs a ccw list of the vectors bounding the visible area 3. Adds new
   * faces connecting the given vector with the bounding vectors 4. Merges the
   * faces marked as coplanar with the new face containing a shared edge 5.
   * Removes the visible faces
   */
  private void addPoint(Vector v) {
    lastPoint = v;
    visible.clear();
    hiddenFaces.clear();
    System.out.println("adding point: " + v);
    ArrayList<Face> visibleFaces = new ArrayList<Face>();
    ArrayList<Face> coplanarFaces = new ArrayList<Face>();

    for (int i = 0; i < faces.size(); i++) {
      double dot = Vector.dot(faces.get(i).getNormal(), Vector.subtract(v,
          faces.get(i).getVectorAt(0)));
      if (Math.abs(dot) < epsilon) { // call it planar
        System.out.println("coplanar with face: ");
        for(int j = 0; j < faces.get(i).getNumberVertices(); j++)
          System.out.println(faces.get(i).getVectorAt(j));
        System.out.println();
        coplanarFaces.add(faces.get(i));
        hiddenFaces.add(faces.get(i));  // TODO testing
      } else if (dot >= epsilon) {
        visibleFaces.add(faces.get(i));
        visible.add(faces.get(i)); // TODO testing
      } else // else not visible
        hiddenFaces.add(faces.get(i));  // TODO testing
    }

    ArrayList<HashSet<Vector>> badEdges = getBadEdges(visibleFaces);

    vertices = findAttachmentLoop(visibleFaces, badEdges);
    System.out.println("vertices: " );
    for(int i = 0; i < vertices.size(); i++) {
      System.out.println(vertices.get(i));
    }

    ArrayList<Face> newFaces = getNewFaces(vertices, v);

    System.out.println("coplanarFaces.size() = " + coplanarFaces.size());
    for (int i = 0; i < coplanarFaces.size(); i++) {
      Face coplanarFace = coplanarFaces.get(i);
      Face newFace = null;
      for (int j = 0; j < newFaces.size(); j++) {
        if (newFaces.get(j).sharesEdgeWith(coplanarFace)) {
          newFace = newFaces.get(j);
          break;
        }
      }
      mergeFaces(coplanarFace, newFace);
      faces.remove(coplanarFace);
      newFaces.remove(newFace);
    }

    faces.removeAll(visibleFaces);
    faces.addAll(newFaces);

    // TODO remove after testing:
    unsorted.remove(0);
  }

  /*
   * Beginning on an arbitrary vertex of an arbitrary face, walks around each
   * face in counter-clockwise order until it reaches a bad edge. Then it moves
   * on to the next face containing the current vertex. Continues until it finds
   * a good edge ending at the start vertex.
   */
  private ArrayList<Vector> findAttachmentLoop(ArrayList<Face> visibleFaces,
      ArrayList<HashSet<Vector>> badEdges) {
    Face cur_face = visibleFaces.get(0);
    Vector start = cur_face.getVectorAt(0);
    Vector cur_vec = cur_face.getVectorAt(0);
    Vector next_vec = cur_face.getVectorAt(1);

    ArrayList<Vector> vertices = new ArrayList<Vector>();
    vertices.add(cur_face.getVectorAt(0));

    while (!next_vec.equals(start)) {
      HashSet<Vector> set = new HashSet<Vector>();
      set.add(cur_vec);
      set.add(next_vec);

      if (!badEdges.contains(set)) {
        vertices.add(next_vec);
        cur_vec = next_vec;
        next_vec = cur_face.getVectorAt((cur_face.indexOf(cur_vec) + 1)
            % cur_face.getNumberVertices());
      } else { // find next face containing cur_vec
        boolean found = false;
        for (int i = (visibleFaces.indexOf(cur_face) + 1) % visibleFaces.size(); !found; i++) {
          int index = i % visibleFaces.size();
          if (visibleFaces.get(index).hasVertex(cur_vec)) {
            found = true;
            cur_face = visibleFaces.get(index);
            next_vec = cur_face.getVectorAt((cur_face.indexOf(cur_vec) + 1)
                % cur_face.getNumberVertices());
          }
        }
      }

      if (next_vec.equals(start)) {
        set = new HashSet<Vector>();
        set.add(cur_vec);
        set.add(next_vec);
        if (badEdges.contains(set)) {
          boolean found = false;
          for (int i = (visibleFaces.indexOf(cur_face) + 1)
              % visibleFaces.size(); !found; i++) {
            int index = i % visibleFaces.size();
            if (visibleFaces.get(index).hasVertex(cur_vec)) {
              found = true;
              cur_face = visibleFaces.get(index);
              next_vec = cur_face.getVectorAt((cur_face.indexOf(cur_vec) + 1)
                  % cur_face.getNumberVertices());
            }
          }
        }
      }
    }
    return vertices;
  }

  /*
   * Assumes newFace has 3 points, 2 shared with oldFace (as it should).
   * Adds the 3 vectors of newFace in ccw order, starting with the one
   * before the non-shared vertex. Then adds from oldFace in ccw order
   * between the last and first added from newFace.
   */
  private void mergeFaces(Face oldFace, Face newFace) {
    System.out.println("merging faces: ");
    System.out.println("oldFace");
    for(int i = 0; i < oldFace.getNumberVertices(); i++) {
      System.out.println(oldFace.getVectorAt(i));
    }
    System.out.println();
    System.out.println("newFace");
    for(int i = 0; i < newFace.getNumberVertices(); i++) {
      System.out.println(newFace.getVectorAt(i));
    }
    System.out.println();
    ArrayList<Vector> vectors = new ArrayList<Vector>();

    int startIndex = 0; // will be index of vector before the non-shared one.
    for (int i = 0; i < 3; i++) {
      if (!oldFace.hasVertex(newFace.getVectorAt(i))) {
        startIndex = i - 1;
      }
    }
    if (startIndex < 0)
      startIndex = 2;

    // add vectors from newFace
    System.out.println("on new face");
    Vector v1 = newFace.getVectorAt(startIndex);
    Vector v2 = newFace.getVectorAt((startIndex + 1)
        % newFace.getNumberVertices());
    Vector v3 = newFace.getVectorAt((startIndex + 2)
        % newFace.getNumberVertices());
    System.out.println("adding " + v1);
    System.out.println("adding " + v2);
    System.out.println("adding " + v3);
    vectors.add(v1);
    vectors.add(v2);
    vectors.add(v3);

    // add vectors from oldFace
    System.out.println("on old face");
    boolean done = false;
    for (int i = (oldFace.indexOf(v3) + 1) % oldFace.getNumberVertices(); !done; i++) {
      int index = i % oldFace.getNumberVertices();
      System.out.println("at " + oldFace.getVectorAt(index));
      if (oldFace.getVectorAt(index).equals(v1)) {
        done = true;
        System.out.println("not adding");
      }
      else {
        vectors.add(oldFace.getVectorAt(index));
        System.out.println("adding");
      }
    }
    
    System.out.println("result");
    for(int i = 0; i < vectors.size(); i++) {
      System.out.println(vectors.get(i));
    }
    System.out.println();

    faces.add(new Face(vectors));
  }

  /*
   * Creates new faces with point and sequential pairs of vectors from vertices
   * list. Assumes vertices are in counter-clockwise order, looking in; creates
   * new faces similarly counter-clockwise.
   */
  private ArrayList<Face> getNewFaces(ArrayList<Vector> vertices,
      Vector point) {
    ArrayList<Face> newFaces = new ArrayList<Face>();
    newF.clear();
    for (int i = 0; i < vertices.size() - 1; i++) {
      newFaces.add(new Face(vertices.get(i), vertices.get(i + 1), point));
      newF.add(new Face(vertices.get(i), vertices.get(i + 1), point)); // TODO for testing

    }
    newFaces.add(new Face(vertices.get(vertices.size() - 1), vertices.get(0),
        point));
    newF.add(new Face(vertices.get(vertices.size() - 1), vertices.get(0),
        point)); // TODO for testing
    
    return newFaces;
  }

  /*
   * Returns a list of all pairs of vertices contained in more than one face in
   * the given list.
   */
  private ArrayList<HashSet<Vector>> getBadEdges(ArrayList<Face> faces) {
    ArrayList<HashSet<Vector>> pairs = new ArrayList<HashSet<Vector>>();
    for (int i = 0; i < faces.size(); i++) {
      Face cur = faces.get(i);
      for (int j = 0; j < cur.getNumberVertices(); j++) {
        for (int k = i + 1; k < faces.size(); k++) {
          Face next = faces.get(k);
          if (next.hasVertex(cur.getVectorAt(j))
              && next.hasVertex(cur.getVectorAt((j + 1)
                  % cur.getNumberVertices()))) {
            HashSet<Vector> HashSet = new HashSet<Vector>();
            HashSet.add(cur.getVectorAt(j));
            HashSet.add(cur.getVectorAt((j + 1) % cur.getNumberVertices()));
            pairs.add(HashSet);
          }
        }
      }
    }
    return pairs;
  }
}
