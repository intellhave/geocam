package development;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



public class ConvexHull2D {
  private ArrayList<Vector2D> points;
  
  public ConvexHull2D(ArrayList<Vector2D> unsortedPoints) {
    findHull(unsortedPoints);
  }

  public Vector2D getPointAt(int i) {
    return points.get(i);
  }
  
  public int getNumPoints() {
    return points.size();
  }
  
  public boolean isEmpty() {
    return points.isEmpty();
  }
  
  private void findHull(ArrayList<Vector2D> unsortedPoints) {
    points = new ArrayList<Vector2D>();
    
    int startIndex = findHighestPoint(unsortedPoints);    
    Vector2D start = unsortedPoints.get(startIndex);
    unsortedPoints.remove(startIndex);
    points.add(start);
    
    ArrayList<Vector2D> vectors = getVectors(start, unsortedPoints);
    Vector2D baseLine = new Vector2D(-1, 0);
    
    HashMap<Double, Integer> angleMap = new HashMap<Double, Integer>();
    ArrayList<Double> cosines = new ArrayList<Double>();
    for(int i = 0; i < vectors.size(); i++) {
      double result = Vector2D.dot(baseLine, vectors.get(i))/vectors.get(i).length();
      cosines.add(result);
      angleMap.put(result, i);
    }
    Collections.sort(cosines);
    for(int i = cosines.size()-1; i >= 0; i--) {
      int index = angleMap.get(cosines.get(i));
      points.add(unsortedPoints.get(index));
    }
  }
  
  private ArrayList<Vector2D> getVectors(Vector2D start, ArrayList<Vector2D> pts) {
    ArrayList<Vector2D> vectors = new ArrayList<Vector2D>();
    for(int i = 0; i < pts.size(); i++) {
      vectors.add((Vector2D) Vector2D.subtract(pts.get(i), start));
    }
    return vectors;
  }
  
  private int findHighestPoint(ArrayList<Vector2D> points) {
    double max_y = points.get(0).getComponent(1);
    int index = 0;
    for(int i = 1; i < points.size(); i++) {
      if(points.get(i).getComponent(1) > max_y) {
        max_y = points.get(i).getComponent(1);
        index = i;
      }
    }
    return index;
  }

  public ArrayList<Vector2D> getPoints() {
    return points;
  }

  public Face getAsFace() {
    Vector3D[] vectors = new Vector3D[points.size()];
    for(int i = 0; i < vectors.length; i++) {
      Vector2D point = points.get(i);
      vectors[i] = new Vector3D(point.getComponent(0), point.getComponent(1), 0);
      System.out.println(vectors[i].toString());
    }
    return new Face(vectors);
  }
}
