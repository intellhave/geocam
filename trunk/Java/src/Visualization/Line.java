package Visualization;

public class Line {
  public final Point p,q;
  
  public Line(Point p, Point q) {
    this.p = p;
    this.q = q;
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof Line && ((Line) obj).p.equals(this.p) && ((Line) obj).q.equals(this.q));
  }
  
  public int hashCode() {
    return p.hashCode() ^ q.hashCode();
  }
  
  public String toString() {
    return "(" + p + "," + q + ")";
  }
}
