package development;

import java.awt.Color;

import triangulation.Face;

public class FadingNode extends Node{
  protected int steps_to_take;
  protected boolean dead = false;
  
  protected static final int DISTANCE = 5;
  
  public FadingNode(Color color, Face face, Vector pos) {
    super(color, face, pos);
    steps_to_take = 100;
    transparency = 0.2;
  }
  
  public FadingNode(FadingNode node) {
    super(node);
    steps_to_take = node.getStepsLeft();
    dead = node.isDead();
  }

  @Override
  public void move() {
    steps_to_take--;
    if(steps_to_take <= 0) {
      die();
      return;
    }
    if(steps_to_take <= 20) {
      transparency += 0.055;
    }
    pos = computeEnd(Vector.add(pos, movement), face, null);
  }
  
  public boolean isDead() {
    return dead;
  }
  
  private void die() {
    dead = true;
  }
  
  public int getStepsLeft() {
    return steps_to_take;
  }
  
}
