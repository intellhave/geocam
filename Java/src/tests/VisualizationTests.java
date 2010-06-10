package tests;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import Visualization.Circle;
import Visualization.Line;
import Visualization.Point;

public class VisualizationTests {

  @Test
  public void PointAngleTest() {
    
      Point pt = new Point(1, 1/2);
      double a = pt.angle();
      assertEquals(a, Math.PI/2);
      pt = new Point(0, 0);
      a = pt.angle();
      assertEquals(a, 0);
      pt = new Point(-1, -1/2);
      a = pt.angle();
      assertEquals(a, -Math.PI/2);
  }
  
  @Test
  public void PointRotateTest(){
      
      Point pt = new Point(1 ,2);
      Double ang = Math.PI/4;
      pt = pt.rotate(ang);
      Point soln = new Point(-1/Math.sqrt(2), 3/Math.sqrt(2));
      assertEquals(pt.x, soln.x, 0.000000001);
      assertEquals(pt.y, soln.y, 0.000000001);
  }
  
  @Test
  public void PointDistancePointTest() {
      
      Point p = new Point(0, 0);
      Point q = new Point(0, 0);
      double d = p.distancePoint(q);
      assertEquals(d, 0);
      p = new Point(1, 2);
      q = new Point(3, 4);
      d = p.distancePoint(q);
      assertEquals(d, Math.sqrt(8));
      p = new Point(-1, -2);
      q = new Point(-3, -4);
      d = p.distancePoint(q);
      assertEquals(d, Math.sqrt(8));
  }
  
  @Test
  public void PointAddSubtractTest() {
      
      Point p = new Point(-1,-2);
      Point q = new Point(-3,-4);  
      Point pt = p.add(q);
      Point soln = new Point(-4,-6);
      assertEquals(pt, soln);
      pt = p.subtract(q);
      soln = new Point(2,2);
      assertEquals(pt, soln);
      p = new Point(1,2);
      q = new Point(-1,-2);
      pt = p.add(q);
      soln = new Point(0, 0);
      assertEquals(pt, soln);
      pt = p.subtract(q);
      soln = new Point(2,4);
      assertEquals(pt, soln);
  }
  
  @Test
  public void PointCopyTest() {
      
      Point p = new Point(1,2);
      Point pt = p.copy();
      Point soln = new Point(1,2);
      assertEquals(pt, soln);
  }
  
  @Test
  public void PointEqualsTest() {
      //equals
      Point p = new Point (1,2);
      Point q = new Point (3,4);
      assertFalse(p.equals(q));
      q = new Point(1,2);
      assertTrue(p.equals(q));
  }
  
  @Test
  public void LineFindPointTest() {
    
    Point p = new Point(1, 2);
    Point q = new Point(3, 4);
    Line l = new Line(p, q);
    double len1 = 1;
    double ang = Math.PI/4;
    Point pt = l.findPoint(len1, ang);
    Point soln = new Point(1,3);
    assertEquals(pt.x, soln.x, 0.0000000001);
    assertEquals(pt.y, soln.y, 0.0000000001); 
  }
  
  @Test
  public void LineAngleTest() {
    
    Point p = new Point(1, 2);
    Point q = new Point(3, 4);
    Line l = new Line(p, q);
    double len1 = 1;
    double len2 = 2;
    double len3 = 3;
    double a = l.angle(len1, len2, len3);
    double sol = Math.PI;
    assertEquals(a, sol);
  }
  
  @Test
  public void LineEqualsTest() {
    
    Point p = new Point(1, 2);
    Point q = new Point(3, 4);
    Line l = new Line(p, q);
    Point r = new Point(1, 2);
    Point s = new Point(3, 4);
    Line l2 = new Line(r, s);
    assertTrue(l.equals(l2));
    r = new Point(5, 8);
    l2 = new Line(r, s);
    assertFalse(l.equals(l2));
  }
  
  @Test
  public void CircleIntersectionNoIntersectTest() {
    
    Point center = new Point(0,0);
    double r = 1;
    Circle c1 = new Circle(center, r);
    Point center2 = new Point (1,2);
    double r2 = 1;
    Circle c2 = new Circle(center2, r2);
    List<Point> inter = c1.circleIntersection(center, r, center2, r2);
    List<Point> soln  = new LinkedList<Point>();
    assertEquals(inter, soln);
    inter = c2.circleIntersection(c1, c2);
    assertEquals(inter, soln);
  }
  
  @Test
  public void CircleIntersectionSameCircleTest() {
    
    Point center = new Point(0,0);
    double r = 1;
    Circle c1 = new Circle(center, r);
    Point center2 = new Point(0, 0);
    double r2 = 1;
    Circle c2 = new Circle(center2, r2);
    List<Point> inter = c1.circleIntersection(c1, c2);
    List<Point> soln  = new LinkedList<Point>();
    soln.add(center);
    assertEquals(inter, soln);
  }
  
  @Test
  public void CircleIntersectionQuadraticTest() {
    
   
    Point center = new Point(0, 0);
    Point center2 = new Point(1, 2);
    double r = 1;
    double r2 = 2;
    Circle c1 = new Circle(center, r);
    Circle c2 = new Circle(center2, r2);
    List<Point> inter = c1.circleIntersection(c1, c2);
    List<Point> soln  = new LinkedList<Point>();
    Point sol1 = new Point(1, 0);
    Point sol2 = new Point(-0.6, 0.8);
    soln.add(sol1);
    soln.add(sol2);
    assertEquals(inter, soln); 
  }
  
  @Test
  public void CircleIntersectionYCompTest() {
    
    Point center = new Point(0, 1);
    Point center2 = new Point(1, 1);
    double r = 1;
    double r2 = 1;
    Circle c1 = new Circle(center, r);
    Circle c2 = new Circle(center2, r2);
    List<Point> inter = c1.circleIntersection(c1, c2);
    List<Point> soln  = new LinkedList<Point>();
    Point sol1 = new Point(0.5, (2 + Math.sqrt(3)) / 2);
    Point sol2 = new Point(0.5, (2 - Math.sqrt(3)) / 2);
    soln.add(sol1);
    soln.add(sol2);
    assertEquals(inter, soln);
  }
  
  @Test
  public void CircleIntersectionXCompTest() {
    
    Point center = new Point(1, 0);
    Point center2 = new Point(1, 1);
    double r = 1;
    double r2 = 1;
    Circle c1 = new Circle(center, r);
    Circle c2 = new Circle(center2, r2);
    List<Point> inter = c1.circleIntersection(c1, c2);
    List<Point> soln  = new LinkedList<Point>();
    Point sol1 = new Point((2 + Math.sqrt(3)) / 2, 0.5);
    Point sol2 = new Point((2 - Math.sqrt(3)) / 2, 0.5);
    soln.add(sol1);
    soln.add(sol2);
    assertEquals(inter, soln);
  }
  
  @Test
  public void CircleEqualsTest() {
    
    Point center = new Point(1, 0);
    Point center2 = new Point(1, 1);
    double r = 1;
    double r2 = 1;
    Circle c1 = new Circle(center, r);
    Circle c2 = new Circle(center2, r2);
    assertFalse(c1.equals(c2)); 
    Point center3 = new Point(1, 0);
    double r3 = 1;
    Circle c3 = new Circle(center3, r3);
    assertTrue(c1.equals(c3));
  }
}
