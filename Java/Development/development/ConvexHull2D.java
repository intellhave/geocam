package development;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



public class ConvexHull2D {
  private ArrayList<Point2D> points;
  
  public static void main(String[] args) throws Exception {
    ArrayList<Point2D> list = new ArrayList<Point2D>();
    list.add(new Point2D(0, 0));
    list.add(new Point2D(0, 1));
    list.add(new Point2D(-.5, .5));
    list.add(new Point2D(.5, .5));
    list.add(new Point2D(-.25, -.25));
    ConvexHull2D hull = new ConvexHull2D(list);
    for(int i = 0; i < hull.getNumPoints()-1; i++) {
      System.out.print(hull.getPointAt(i) + ", " );
    }
    System.out.println(hull.getPointAt(hull.getNumPoints()-1));
  }
  
  public ConvexHull2D(ArrayList<Point2D> unsortedPoints) throws Exception {
    findHull(unsortedPoints);
  }
  
  public Point2D getPointAt(int i) {
    return points.get(i);
  }
  
  public int getNumPoints() {
    return points.size();
  }
  
  public boolean isEmpty() {
    return points.isEmpty();
  }
  
  private void findHull(ArrayList<Point2D> unsortedPoints) throws Exception {
    points = new ArrayList<Point2D>();
    
    int startIndex = findHighestPoint(unsortedPoints);    
    Point2D start = unsortedPoints.get(startIndex);
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
  
  private ArrayList<Vector2D> getVectors(Point2D start, ArrayList<Point2D> pts) throws Exception {
    ArrayList<Vector2D> vectors = new ArrayList<Vector2D>();
    Vector2D startV = new Vector2D(start);
    for(int i = 0; i < pts.size(); i++) {
      Vector2D v = new Vector2D(pts.get(i));
      v.subtract(startV);
      vectors.add((Vector2D) Vector2D.subtract(v, startV));
    }
    return vectors;
  }
  
  private int findHighestPoint(ArrayList<Point2D> points) {
    double max_y = points.get(0).getY();
    int index = 0;
    for(int i = 1; i < points.size(); i++) {
      if(points.get(i).getY() > max_y) {
        max_y = points.get(i).getY();
        index = i;
      }
    }
    return index;
  }
}
