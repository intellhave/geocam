package development;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.jreality.scene.SceneGraphComponent;

public class ConvexHull3D {
  private static final double epsilon = Math.pow(10, -6);
  private ArrayList<Face> faces = new ArrayList<Face>();

  public ConvexHull3D(ArrayList<Vector3D> unsortedVectors) {
    findHull(unsortedVectors);
  }

  public ConvexHull3D(Vector3D... vectors) {
    ArrayList<Vector3D> unsortedVectors = new ArrayList<Vector3D>();
    for (int i = 0; i < vectors.length; i++)
      unsortedVectors.add(vectors[i]);
    // findHull(unsortedVectors);
  }
  
  public int getNumberFaces() {
    return faces.size();
  }

  private void findHull(ArrayList<Vector3D> unsorted) {
    int i = 3;
    System.out.println("unsorted.size() = " + unsorted.size());
    while (coplanar(unsorted.get(0), unsorted.get(1), unsorted.get(2), unsorted
        .get(i))) {
      System.out.println("coplanar with " + i);
      i++;
    }
    System.out.println("unsorted.size() = " + unsorted.size());

    Face f1 = new Face(unsorted.get(0), unsorted.get(1), unsorted.get(2));
    double dot = Vector3D.dot(f1.getNormal(), Vector3D.subtract(
        unsorted.get(i), unsorted.get(0)));
    if (dot > 0) { // change order
      f1 = new Face(unsorted.get(0), unsorted.get(2), unsorted.get(1));
    }
    System.out.println("unsorted.size() = " + unsorted.size());

    // complete initial tetra
    Face f2 = new Face(f1.getVectorAt(1), f1.getVectorAt(0), unsorted.get(i));
    Face f3 = new Face(f1.getVectorAt(2), f1.getVectorAt(1), unsorted.get(i));
    Face f4 = new Face(f1.getVectorAt(2), f1.getVectorAt(0), unsorted.get(i));
    
    System.out.println("unsorted.size() = " + unsorted.size());
    unsorted.remove(i);
    System.out.println("unsorted.size() = " + unsorted.size());

    unsorted.remove(2);
    System.out.println("unsorted.size() = " + unsorted.size());

    unsorted.remove(1);
    System.out.println("unsorted.size() = " + unsorted.size());

    unsorted.remove(0);

    faces.add(f1);
    faces.add(f2);
    faces.add(f3);
    faces.add(f4);

    addPoint(unsorted.get(0));
  }

  public ArrayList<Vector3D> testAddPoint(ArrayList<Face> faces, Vector3D point) {
    this.faces = faces;
    return addPoint(point);
  }

  private ArrayList<Vector2D> getVectors2D(ArrayList<Vector3D> vectors) {
    ArrayList<Vector2D> vectors2D = new ArrayList<Vector2D>();
    Vector3D vx = Vector3D.subtract(vectors.get(1), vectors.get(0));
    Vector3D vy = Vector3D.subtract(vectors.get(vectors.size() - 1), vectors
        .get(0));
    for (int i = 0; i < vectors.size(); i++) {
      vectors2D.add(new Vector2D(Vector3D.dot(vx, vectors.get(i)), Vector3D
          .dot(vy, vectors.get(i))));
    }
    return vectors2D;
  }

  private ArrayList<Vector3D> addPoint(Vector3D v) {
    ArrayList<Face> visibleFaces = new ArrayList<Face>();
    boolean foundCoplanar = false;
    int coplanarIndex = 0;

    for (int i = 0; i < faces.size(); i++) {
      double dot = Vector3D.dot(faces.get(i).getNormal(), Vector3D.subtract(v,
          faces.get(i).getVectorAt(0)));
      if (Math.abs(dot) < epsilon) { // call it planar
        System.out.println("coplanar");
        foundCoplanar = true;
        coplanarIndex = i;

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
      HashSet<Vector3D> set = new HashSet<Vector3D>();
      set.add(cur_vec);
      set.add(next_vec);

      if (!badEdges.contains(set)) {
        vertices.add(next_vec);
        cur_vec = next_vec;
        next_vec = cur.getVectorAt((cur.indexOf(cur_vec) + 1)
            % cur.getNumberVertices());
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

    int oldSize = faces.size();
    addNewFaces(vertices, v);
    if(foundCoplanar) {
      Face coplanarFace = faces.get(coplanarIndex);
      Face newFace;
      for(int i = oldSize; i < faces.size(); i++) {
        if(faces.get(i).sharesEdgeWith(coplanarFace)) {
          newFace = faces.get(i);
          break;
        }
            
      }
      mergeFaces(faces.get(coplanarIndex), coplanarFace);
    }

    faces.removeAll(visibleFaces);

    return vertices;
  }
  
  // newFace should have 3 points, 2 shared with old face
  private void mergeFaces(Face oldFace, Face newFace) {
    ArrayList<Vector3D> vectors = oldFace.getVectors();
    for(int i = 0; i < newFace.getNumberVertices(); i++) {
      if(!vectors.contains(newFace.getVectorAt(i)))
        vectors.add(newFace.getVectorAt(i));
    }
    ArrayList<Vector2D> vectors2D = getVectors2D(vectors);
    HashMap<Vector2D, Vector3D> vectorMap = new HashMap<Vector2D, Vector3D>();
    for(int j = 0; j < vectors.size(); j++) {
      vectorMap.put(vectors2D.get(j), vectors.get(j));
    }
    ConvexHull2D hull = new ConvexHull2D(Point2D.getPoints(vectors2D));
    ArrayList<Vector2D> orderedVectors2D = Vector2D.getVectors(hull.getPoints());
    ArrayList<Vector3D> orderedVectors3D = new ArrayList<Vector3D>();
    for(int j = 0; j < orderedVectors2D.size(); j++) {
      orderedVectors3D.add(vectorMap.get(orderedVectors2D.get(j)));
    }
    faces.remove(oldFace);
    faces.remove(newFace);
    faces.add(new Face(orderedVectors3D));
  }

  private void addNewFaces(ArrayList<Vector3D> vertices, Vector3D point) {
    for (int i = 0; i < vertices.size() - 1; i++) {
      faces.add(new Face(vertices.get(i), vertices.get(i + 1), point));
    }
    faces.add(new Face(vertices.get(vertices.size() - 1), vertices.get(0),
        point));
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

  private boolean coplanar(Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v4) {
    Vector3D cross1 = Vector3D.cross(v1, v2);
    Vector3D cross2 = Vector3D.cross(v3, v4);
    Vector3D result = Vector3D.cross(cross1, cross2);
    return result.isZero();
  }

  public Face getFaceAt(int i) {
    return faces.get(i);
  }

}
