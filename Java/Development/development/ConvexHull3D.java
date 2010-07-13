package development;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class ConvexHull3D {
  private static final double epsilon = Math.pow(10, -6);
  private ArrayList<Face> faces = new ArrayList<Face>();
  
//  public static void main(String[] args) {
//    Vector3D v1 = new Vector3D(-1, 0, 0);
//    Vector3D v2 = new Vector3D(0, 1, 0);
//    Vector3D v3 = new Vector3D(1, 0, 0);
//    Vector3D v4 = new Vector3D(0, 0, 1);
//
//    Face f1 = new Face(v1, v2, v3);
//    Face f2 = new Face(v3, v2, v4);
//    Face f3 = new Face(v4, v2, v1);
//    Face f4 = new Face(v1, v3, v4);
//
//    ArrayList<Face> faces = new ArrayList<Face>();
//    faces.add(f1);
//    faces.add(f2);
//    faces.add(f3);
//    faces.add(f4);
//    ArrayList<Vector3D[]> badPairs = getBadEdges(faces);
//    for(int i =0 ; i < badPairs.size(); i++) {
//      System.out.println(badPairs.get(i)[0].toString() + " and " + badPairs.get(i)[1].toString());
//    }
//  }

  public ConvexHull3D(ArrayList<Vector3D> unsortedVectors) {
    findHull(unsortedVectors);
  }

  private void findHull(ArrayList<Vector3D> unsorted) {
    int i = 4;
    while (coplanar(unsorted.get(0), unsorted.get(1), unsorted.get(2), unsorted
        .get(i))) {
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
    Face f4 = new Face(f1.getVectorAt(2), f1.getVectorAt(0), unsorted.get(i));

    unsorted.remove(0);
    unsorted.remove(1);
    unsorted.remove(2);
    unsorted.remove(i);

    faces.add(f1);
    faces.add(f2);
    faces.add(f3);
    faces.add(f4);

    addPoint(unsorted.get(0));
  }

  private void addPoint(Vector3D v) {
    ArrayList<Face> visibleFaces = new ArrayList<Face>();
    
    for (int i = 0; i < faces.size(); i++) {
      double dot = Vector3D.dot(faces.get(i).getNormal(), Vector3D.subtract(v, faces
          .get(i).getVectorAt(0)));
      if (Math.abs(dot) < epsilon) { // call it planar
        ArrayList<Vector3D> vectors = faces.get(i).getVectors();
        vectors.add(v);
      }
      else if(dot >= epsilon) {
        visibleFaces.add(faces.get(i));
      }
    }
    
    ArrayList<HashSet<Vector3D>> badEdges = getBadEdges(visibleFaces);
  }
  
  public static ArrayList<HashSet<Vector3D>> getBadEdges(ArrayList<Face> faces) {
    ArrayList<HashSet<Vector3D>> pairs = new ArrayList<HashSet<Vector3D>>();
    for(int i = 0; i < faces.size(); i++) {
      Face cur = faces.get(i);
      for(int j = 0; j < cur.getNumberVertices(); j++) {
        for(int k = i+1; k < faces.size(); k++) {
          Face next = faces.get(k);
          if(next.contains(cur.getVectorAt(j)) && next.contains(cur.getVectorAt((j+1)%cur.getNumberVertices()))){
            HashSet<Vector3D> HashSet = new HashSet<Vector3D>();
            HashSet.add(cur.getVectorAt(j));
            HashSet.add(cur.getVectorAt((j+1)%cur.getNumberVertices()));
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


}
