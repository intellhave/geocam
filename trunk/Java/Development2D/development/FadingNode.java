package development;

import java.awt.Color;
import java.util.ArrayList;

import triangulation.Face;

/*
 * Fading Node
 * 
 * Author: K. Kiviat
 * 
 * An extension of node that leaves a trail behind 
 * and disappears after some number of steps.
 * 
 * Possible upgrades:
 *    * change steps_to_take to allow for specifying the time that
 *       the node stays visible, distance visible, etc.
 */

public class FadingNode extends Node{
  protected int steps_to_take;
  protected boolean dead = false;
  protected ArrayList<Trail> trails = new ArrayList<Trail>();
  protected Trail currentTrail;
  
  protected static final int DISTANCE = 5;
  
  public FadingNode(Color color, Face face, Vector pos, double radius) {
    super(color, face, pos, radius);
    steps_to_take = 100;
    transparency = 0.2;
    currentTrail = new Trail(pos, pos, face, color);
  }
  
  public FadingNode(FadingNode node) {
    super(node);
    steps_to_take = node.getStepsLeft();
    dead = node.isDead();
    trails = new ArrayList<Trail>(node.getTrails());
    currentTrail = new Trail(node.getCurrentTrail());
  }

  @Override
  public void move(double elapsedTime) {
    steps_to_take--;
    if(steps_to_take <= 0) {
      die();
      return;
    }
    
    Vector oldPos = new Vector(pos);
    Vector oldMove = new Vector(movement);
    
    Vector v = new Vector(movement);
    v.scale(elapsedTime*units_per_millisecond);
    pos = computeEnd(Vector.add(pos, v), face, null);
        
    if(oldMove.equals(movement)) { // movement only changes if it enters a new face
      currentTrail.end = pos;
    } else {
      oldMove.scale(0.5);
      currentTrail.end = Vector.add(oldPos, oldMove); // stretch it a little to big avoid jumps
      trails.add(currentTrail);
      Vector s = new Vector(movement);
      s.scale(-0.5);
      s.add(pos);
      currentTrail = new Trail(s, pos, face, color);
    }
  }
  
  public boolean isDead() {
    return dead;
  }
  
  private void die() {
    dead = true;
  }
  
  public Trail getCurrentTrail() {
    return currentTrail;
  }
  
  public ArrayList<Trail> getTrails() {
    return trails;
  }
  
  public ArrayList<Trail> getAllTrails() {
    ArrayList<Trail> allTrails = new ArrayList<Trail>(trails);
    allTrails.add(currentTrail);
    return allTrails;
  }  
  public int getStepsLeft() {
    return steps_to_take;
  }
  
}
