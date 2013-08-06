package development;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import util.Vector;



public class ConvexHull2D {
  private ArrayList<Vector> points;
  
  public ConvexHull2D(ArrayList<Vector> vertices) {
    findHull(vertices);
  }

  public Vector getPointAt(int i) {
    return points.get(i);
  }
  
  public int getNumPoints() {
    return points.size();
  }
  
  public boolean isEmpty() {
    return points.isEmpty();
  }
  
  private void findHull(ArrayList<Vector> unsortedPoints) {
    points = new ArrayList<Vector>();
    
    int startIndex = findHighestPoint(unsortedPoints);    
    Vector start = unsortedPoints.get(startIndex);
    unsortedPoints.remove(startIndex);
    points.add(start);
    
    ArrayList<Vector> vectors = getVectors(start, unsortedPoints);
    Vector baseLine = new Vector(-1, 0);
    
    HashMap<Double, Integer> angleMap = new HashMap<Double, Integer>();
    ArrayList<Double> cosines = new ArrayList<Double>();
    for(int i = 0; i < vectors.size(); i++) {
      double result = Vector.dot(baseLine, vectors.get(i))/vectors.get(i).length();
      cosines.add(result);
      angleMap.put(result, i);
    }
    Collections.sort(cosines);
    for(int i = cosines.size()-1; i >= 0; i--) {
      int index = angleMap.get(cosines.get(i));
      points.add(unsortedPoints.get(index));
    }
  }
  
  private ArrayList<Vector> getVectors(Vector start, ArrayList<Vector> pts) {
    ArrayList<Vector> vectors = new ArrayList<Vector>();
    for(int i = 0; i < pts.size(); i++) {
      vectors.add((Vector) Vector.subtract(pts.get(i), start));
    }
    return vectors;
  }
  
  private int findHighestPoint(ArrayList<Vector> points) {
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

  public ArrayList<Vector> getPoints() {
    return points;
  }

  public EmbeddedFace getAsFace() {
    Vector[] vectors = new Vector[points.size()];
    for(int i = 0; i < vectors.length; i++) {
      Vector point = points.get(i);
      vectors[i] = new Vector(point.getComponent(0), point.getComponent(1), 0);
      System.out.println(vectors[i].toString());
    }
    return new EmbeddedFace(vectors);
  }
}
