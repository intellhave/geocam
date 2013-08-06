package development;

import java.awt.Color;

import triangulation.Face;
import util.Vector;

public class Trail {
  protected Vector start;
  protected Vector end;
  protected Face face;
  protected Color color;
  
  public Trail(Vector start, Vector end, Face face, Color color) {
    this.start = start;
    this.end = end;
    this.face = face;
    this.color = color;
  }
  
  public Trail(Trail trail) {
    this.start = new Vector(trail.getStart());
    this.end = new Vector(trail.getEnd());
    this.face = trail.getFace();
    this.color = trail.getColor();
  }

  public Face getFace() { return face; }
  public Vector getStart() { return start; }
  public Vector getEnd() { return end; }
  public Color getColor() { return color; }
}
