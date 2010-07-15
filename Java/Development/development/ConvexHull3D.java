package development;

import java.util.ArrayList;
import java.util.HashSet;

public class ConvexHull3D {
  private static final double epsilon = Math.pow(10, -6);
  private ArrayList<Face> faces = new ArrayList<Face>();
  private ArrayList<Vector3D> unsorted;

  public ConvexHull3D(ArrayList<Vector3D> unsortedVectors) {
    unsorted = unsortedVectors;
    findHull(unsorted);
  }

  public ConvexHull3D(Vector3D... vectors) {
    ArrayList<Vector3D> unsortedVectors = new ArrayList<Vector3D>();
    for (int i = 0; i < vectors.length; i++)
      unsortedVectors.add(vectors[i]);
    findHull(unsortedVectors);
  }

  public ConvexHull3D(ConvexHull3D original) {
    faces = new ArrayList<Face>();
    unsorted = new ArrayList<Vector3D>();

    for (int i = 0; i < original.getNumberFaces(); i++) {
      faces.add(new Face(original.getFaceAt(i)));
    }
    ArrayList<Vector3D> oldUnsorted = original.getUnsorted();
    for (int i = 0; i < oldUnsorted.size(); i++) {
      unsorted.add(new Vector3D(oldUnsorted.get(i)));
    }
  }

  private ArrayList<Vector3D> getUnsorted() {
    return unsorted;
  }

  public int getNumberFaces() {
    return faces.size();
  }
  
  public Face getFaceAt(int i) {
    return faces.get(i);
  }

  /*
   * Constructs a face from the first three points, then a tetrahedron with the
   * first non-coplanar point. Then adds additional points individually with
   * addPoint method.
   */
  private void findHull(ArrayList<Vector3D> unsorted) {
    int i = 3;
    Vector3D v1 = Vector3D.subtract(unsorted.get(1), unsorted.get(0));
    Vector3D v2 = Vector3D.subtract(unsorted.get(2), unsorted.get(0));

    while (coplanar(v1, v2, Vector3D.subtract(unsorted.get(i), unsorted.get(0)))) {
      i++;
    }

    Face f1 = new Face(unsorted.get(0), unsorted.get(1), unsorted.get(2));
    double dot = Vector3D.dot(f1.getNormal(), Vector3D.subtract(
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
  
  private boolean coplanar(Vector3D v1, Vector3D v2, Vector3D v3) {
    Vector3D cross1 = Vector3D.cross(v1, v2);
    Vector3D cross2 = Vector3D.cross(v2, v3);
    Vector3D result = Vector3D.cross(cross1, cross2);
    return result.length() < epsilon;
  }

  /*
   * 1. Finds the faces "visible" from and coplanar with the given vector 2.
   * Constructs a ccw list of the vectors bounding the visible area 3. Adds new
   * faces connecting the given vector with the bounding vectors 4. Merges the
   * faces marked as coplanar with the new face containing a shared edge 5.
   * Removes the visible faces
   */
  private void addPoint(Vector3D v) {
    System.out.println("adding point: " + v);
    ArrayList<Face> visibleFaces = new ArrayList<Face>();
    ArrayList<Face> coplanarFaces = new ArrayList<Face>();

    for (int i = 0; i < faces.size(); i++) {
      double dot = Vector3D.dot(faces.get(i).getNormal(), Vector3D.subtract(v,
          faces.get(i).getVectorAt(0)));
      if (Math.abs(dot) < epsilon) { // call it planar
        coplanarFaces.add(faces.get(i));

      } else if (dot >= epsilon) {
        visibleFaces.add(faces.get(i));
      } // else not visible
    }

    ArrayList<HashSet<Vector3D>> badEdges = getBadEdges(visibleFaces);

    ArrayList<Vector3D> vertices = findAttachmentLoop(visibleFaces, badEdges);
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
  private ArrayList<Vector3D> findAttachmentLoop(ArrayList<Face> visibleFaces,
      ArrayList<HashSet<Vector3D>> badEdges) {
    Face cur_face = visibleFaces.get(0);
    Vector3D start = cur_face.getVectorAt(0);
    Vector3D cur_vec = cur_face.getVectorAt(0);
    Vector3D next_vec = cur_face.getVectorAt(1);

    ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
    vertices.add(cur_face.getVectorAt(0));

    while (!next_vec.equals(start)) {
      HashSet<Vector3D> set = new HashSet<Vector3D>();
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
          if (visibleFaces.get(index).contains(cur_vec)) {
            found = true;
            cur_face = visibleFaces.get(index);
            next_vec = cur_face.getVectorAt((cur_face.indexOf(cur_vec) + 1)
                % cur_face.getNumberVertices());
          }
        }
      }

      if (next_vec.equals(start)) {
        set = new HashSet<Vector3D>();
        set.add(cur_vec);
        set.add(next_vec);
        if (badEdges.contains(set)) {
          boolean found = false;
          for (int i = (visibleFaces.indexOf(cur_face) + 1)
              % visibleFaces.size(); !found; i++) {
            int index = i % visibleFaces.size();
            if (visibleFaces.get(index).contains(cur_vec)) {
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
    ArrayList<Vector3D> vectors = new ArrayList<Vector3D>();

    int startIndex = 0; // will be index of vector before the non-shared one.
    for (int i = 0; i < 3; i++) {
      if (!oldFace.contains(newFace.getVectorAt(i))) {
        startIndex = i - 1;
      }
    }
    if (startIndex < 0)
      startIndex = 2;

    // add vectors from newFace
    vectors.add(newFace.getVectorAt(startIndex));
    vectors.add(newFace.getVectorAt((startIndex + 1)
        % newFace.getNumberVertices()));
    vectors.add(newFace.getVectorAt((startIndex + 2)
        % newFace.getNumberVertices()));

    Vector3D v = newFace.getVectorAt((startIndex + 2)
        % newFace.getNumberVertices());

    // add vectors from oldFace
    boolean done = false;
    for (int i = (oldFace.indexOf(v) + 1) % oldFace.getNumberVertices(); !done; i++) {
      int index = i % oldFace.getNumberVertices();
      if (oldFace.getVectorAt(index).equals(v))
        done = true;
      else
        vectors.add(oldFace.getVectorAt(index));
    }

    faces.add(new Face(vectors));
  }

  /*
   * Creates new faces with point and sequential pairs of vectors from vertices
   * list. Assumes vertices are in counter-clockwise order, looking in; creates
   * new faces similarly counter-clockwise.
   */
  private ArrayList<Face> getNewFaces(ArrayList<Vector3D> vertices,
      Vector3D point) {
    ArrayList<Face> newFaces = new ArrayList<Face>();
    for (int i = 0; i < vertices.size() - 1; i++) {
      newFaces.add(new Face(vertices.get(i), vertices.get(i + 1), point));
    }
    newFaces.add(new Face(vertices.get(vertices.size() - 1), vertices.get(0),
        point));
    return newFaces;
  }

  /*
   * Returns a list of all pairs of vertices contained in more than one face in
   * the given list.
   */
  private ArrayList<HashSet<Vector3D>> getBadEdges(ArrayList<Face> faces) {
    ArrayList<HashSet<Vector3D>> pairs = new ArrayList<HashSet<Vector3D>>();
    for (int i = 0; i < faces.size(); i++) {
      Face cur = faces.get(i);
      for (int j = 0; j < cur.getNumberVertices(); j++) {
        for (int k = i + 1; k < faces.size(); k++) {
          Face next = faces.get(k);
          if (next.contains(cur.getVectorAt(j))
              && next.contains(cur.getVectorAt((j + 1)
                  % cur.getNumberVertices()))) {
            HashSet<Vector3D> HashSet = new HashSet<Vector3D>();
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
