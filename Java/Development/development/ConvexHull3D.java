package development;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.jreality.scene.SceneGraphComponent;

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
    // findHull(unsortedVectors);
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

  private void findHull(ArrayList<Vector3D> unsorted) {
    int i = 3;
    Vector3D v1 = Vector3D.subtract(unsorted.get(1), unsorted.get(0));
    Vector3D v2 = Vector3D.subtract(unsorted.get(2), unsorted.get(0));

    while (coplanar(v1, v2, Vector3D.subtract(unsorted.get(i), unsorted.get(0)))) {
      System.out.println("coplanar with " + i);
      i++;
    }

    Face f1 = new Face(unsorted.get(0), unsorted.get(1), unsorted.get(2));
    double dot = Vector3D.dot(f1.getNormal(), Vector3D.subtract(
        unsorted.get(i), unsorted.get(0)));
    if (dot > 0) { // change order
      f1 = new Face(unsorted.get(0), unsorted.get(2), unsorted.get(1));
    }

    // complete initial tetra
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

    // while(!unsorted.isEmpty()) {
    // addPoint(unsorted.get(0));
    // unsorted.remove(0);
    // }
  }

  public boolean addPoint() {
    if (unsorted.isEmpty())
      return false;
    addPoint(unsorted.get(0));
    return true;
  }

//  private ArrayList<Vector2D> getVectors2D(ArrayList<Vector3D> vectors) {
//    System.out.println("projecting vectors; ");
//    for(int i =0 ; i < vectors.size(); i++) {
//      System.out.println(vectors.get(i));
//    }
//    System.out.println();
//    ArrayList<Vector2D> vectors2D = new ArrayList<Vector2D>();
//    Vector3D vy = Vector3D.subtract(vectors.get(1), vectors.get(0));
//    Vector3D vx = Vector3D.subtract(vectors.get(vectors.size() - 1), vectors
//        .get(0));
//    for (int i = 0; i < vectors.size(); i++) {
//      vectors2D.add(new Vector2D(Vector3D.dot(vx, vectors.get(i)), Vector3D
//          .dot(vy, vectors.get(i))));
//    }
//    return vectors2D;
//  }

  private void addPoint(Vector3D v) {
    System.out.println("adding point: " + v.toString());
    for (int i = 0; i < faces.size(); i++) {
      System.out.println("face index " + i);
      Face cur = faces.get(i);
      for (int j = 0; j < cur.getNumberVertices(); j++) {
        System.out.println(cur.getVectorAt(j).toString());
      }
      System.out.println();
    }
    ArrayList<Face> visibleFaces = new ArrayList<Face>();
    ArrayList<Face> coplanarFaces = new ArrayList<Face>();

    for (int i = 0; i < faces.size(); i++) {
      System.out.println("checking face index " + i);
      double dot = Vector3D.dot(faces.get(i).getNormal(), Vector3D.subtract(v,
          faces.get(i).getVectorAt(0)));
      if (Math.abs(dot) < epsilon) { // call it planar
        System.out.println("coplanar");
        coplanarFaces.add(faces.get(i));

      } else if (dot >= epsilon) {
        System.out.println("not coplanar");
        visibleFaces.add(faces.get(i));
      }
    }

    ArrayList<HashSet<Vector3D>> badEdges = getBadEdges(visibleFaces);
    Face cur = visibleFaces.get(0);
    Vector3D start = cur.getVectorAt(0);
    Vector3D cur_vec = cur.getVectorAt(0);
    Vector3D next_vec = cur.getVectorAt(1);

    ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
    vertices.add(cur.getVectorAt(0));

    while (!next_vec.equals(start)) {
      System.out.println("in loop");
      HashSet<Vector3D> set = new HashSet<Vector3D>();
      set.add(cur_vec);
      set.add(next_vec);

      if (!badEdges.contains(set)) {
//        System.out.println("list of all vertices in cur");
//        for(int i = 0; i < cur.getNumberVertices(); i++) {
//          System.out.println(cur.getVectorAt(i));
//        }
//        System.out.println();
//        System.out.println("adding vertex " + next_vec);
        vertices.add(next_vec);
        cur_vec = next_vec;
        next_vec = cur.getVectorAt((cur.indexOf(cur_vec) + 1)
            % cur.getNumberVertices());
        System.out.println("new next_vec = " + next_vec);
      } else {
        boolean found = false;
        for (int i = (faces.indexOf(cur) + 1) % faces.size(); !found; i++) {
          int index = i % faces.size();
          if (faces.get(index).contains(cur_vec)) {
            found = true;
            cur = faces.get(index);
            next_vec = cur.getVectorAt((cur.indexOf(cur_vec) + 1)
                % cur.getNumberVertices());
          }
        }
      }
    }

    System.out.println("vertices");
    for (int i = 0; i < vertices.size(); i++) {
      System.out.println(vertices.get(i).toString());
    }

    ArrayList<Face> newFaces = getNewFaces(vertices, v);

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

  // newFace should have 3 points, 2 shared with old face
  private void mergeFaces(Face oldFace, Face newFace) {
    ArrayList<Vector3D> vectors = new ArrayList<Vector3D>();
    Vector3D start = null, end = null;
    int i;
    for(i = 0; i < oldFace.getNumberVertices(); i++) {
      if(newFace.contains(oldFace.getVectorAt(i)) && newFace.contains(oldFace.getVectorAt((i+1)%oldFace.getNumberVertices()))) {
        end = oldFace.getVectorAt((i+1)%oldFace.getNumberVertices());
        start = oldFace.getVectorAt(i);
        break;
      }
    }
    vectors.add(end);
    int nextIndex = (i+2)%oldFace.getNumberVertices();
    Vector3D next = oldFace.getVectorAt(nextIndex);
    while(!next.equals(end)) {
      vectors.add(next);
      nextIndex = (nextIndex+1)%oldFace.getNumberVertices();
      next = oldFace.getVectorAt(nextIndex);
    }
    
    nextIndex = (newFace.indexOf(start)+1)%newFace.getNumberVertices();
    next = newFace.getVectorAt(nextIndex);
    while(!next.equals(end)) {
      vectors.add(next);
      nextIndex = (nextIndex+1)%newFace.getNumberVertices();
      next = newFace.getVectorAt(nextIndex);
    }
    
    faces.add(new Face(vectors));
    
    
//    System.out.println("merging faces: ");
//    for (int j = 0; j < oldFace.getNumberVertices(); j++) {
//      System.out.println(oldFace.getVectorAt(j).toString());
//    }
//    System.out.println();
//    for (int j = 0; j < newFace.getNumberVertices(); j++) {
//      System.out.println(newFace.getVectorAt(j).toString());
//    }
//    System.out.println();
//
//    ArrayList<Vector3D> vectors = oldFace.getVectors();
//    for (int i = 0; i < newFace.getNumberVertices(); i++) {
//      if (!vectors.contains(newFace.getVectorAt(i)))
//        vectors.add(newFace.getVectorAt(i));
//    }
//    ArrayList<Vector2D> vectors2D = getVectors2D(vectors);
//    HashMap<Vector2D, Vector3D> vectorMap = new HashMap<Vector2D, Vector3D>();
//    for (int j = 0; j < vectors.size(); j++) {
//      vectorMap.put(vectors2D.get(j), vectors.get(j));
//    }
//    ConvexHull2D hull = new ConvexHull2D(vectors2D);
//    ArrayList<Vector2D> orderedVectors2D = hull.getPoints();
//    ArrayList<Vector3D> orderedVectors3D = new ArrayList<Vector3D>();
//
//    for (int j = 0; j < orderedVectors2D.size(); j++) {
//      System.out.println(vectorMap.get(orderedVectors2D.get(j)).toString());
//      orderedVectors3D.add(vectorMap.get(orderedVectors2D.get(j)));
//    }
//    faces.add(new Face(orderedVectors3D));
  }

  private ArrayList<Face> getNewFaces(ArrayList<Vector3D> vertices, Vector3D point) {
    ArrayList<Face> newFaces = new ArrayList<Face>();
    for (int i = 0; i < vertices.size() - 1; i++) {
      newFaces.add(new Face(vertices.get(i), vertices.get(i + 1), point));
      System.out.println("adding face: ");
      System.out.println(vertices.get(i) + ", " + vertices.get(i + 1) + ", "
          + point);
    }
    newFaces.add(new Face(vertices.get(vertices.size() - 1), vertices.get(0),
        point));
    System.out.println("adding face: ");
    System.out.println(vertices.get(vertices.size() - 1) + ", "
        + vertices.get(0) + ", " + point);
    return newFaces;
  }

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

  private boolean coplanar(Vector3D v1, Vector3D v2, Vector3D v3) {
    Vector3D cross1 = Vector3D.cross(v1, v2);
    Vector3D cross2 = Vector3D.cross(v2, v3);
    Vector3D result = Vector3D.cross(cross1, cross2);
    return result.isZero();
  }

  public Face getFaceAt(int i) {
    return faces.get(i);
  }

}
