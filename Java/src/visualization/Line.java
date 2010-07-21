package visualization;

public class Line {
  public final Point p,q;
  
  public Line(Point p, Point q) {
    this.p = p;
    this.q = q;
  }
  
  public Point findPoint(double length, double angle) {
    Point vect = new Point(q.x - p.x, q.y - p.y);
    Point rotate = vect.rotate(angle);
    Point vectResult = new Point(rotate.x /p.distancePoint(q) * length, rotate.y / p.distancePoint(q) * length);
    Point pt = new Point(p.x + vectResult.x, p.y + vectResult.y);
    return pt;
}
  

  
  
  public double angle(double len1, double len2, double len3) {
    return  Math.acos((len1 * len1 + len2 * len2 - len3 * len3)/ (2 * len1 * len2));
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
