package development;

import java.awt.Color;
import java.util.ArrayList;

import marker.ManifoldPosition;


/**
 * Fading Node
 * 
 * @author K. Kiviat
 * 
 * An extension of node that leaves a trail behind 
 * and disappears after some length of time.
 * 
 * Possible upgrades:
 *    * change time_to_live to allow for specifying distance visible, etc.
 */

public class FadingNode extends Node{
  protected int time_to_live; // milliseconds before node should disappear
  protected boolean dead = false;
  protected ArrayList<Trail> trails = new ArrayList<Trail>();
  protected Trail currentTrail;
  
  protected static final int DISTANCE = 5;
  
  public FadingNode(Color color, ManifoldPosition pos, double velocity, double radius) {
    super(color, pos, velocity, radius);
    time_to_live = 10*1000;
    transparency = 0.2;
    currentTrail = new Trail(pos.getPosition(), pos.getPosition(), pos.getFace(), color);
  }
  
  public FadingNode(FadingNode node) {
    super(node);
    time_to_live = node.getTimeLeft();
    dead = node.isDead();
    trails = new ArrayList<Trail>(node.getTrails());
    currentTrail = new Trail(node.getCurrentTrail());
  }

  @Override
  public void move(double elapsedTime) {
    time_to_live -= elapsedTime;
    if(time_to_live <= 0) {
      die();
      return;
    }
    
    if(time_to_live < 7000 && time_to_live > 6000) 
      transparency = 0.3;
    if(time_to_live < 6000 && time_to_live > 5000)
      transparency = 0.4;
    else if(time_to_live < 5000 && time_to_live > 4000)
      transparency = 0.5;
    else if(time_to_live < 4000 && time_to_live > 3000)
      transparency = 0.6;
    else if(time_to_live < 3000 && time_to_live > 2000)
      transparency = 0.7;
    else if(time_to_live < 2000 && time_to_live > 1000)
      transparency = 0.8;
    else if(time_to_live < 1000)
      transparency = 0.9;
    
    Vector oldPos = new Vector(pos.getPosition());
    Vector oldMove = new Vector(movement);
    
    Vector v = new Vector(movement);
    v.scale(elapsedTime*units_per_millisecond);
    //pos = computeEnd(Vector.add(pos.getPosition(), v), face, null);
    pos.move(v,movement);
        
    if(oldMove.equals(movement)) { // movement only changes if it enters a new face
      currentTrail.end = pos.getPosition();
    } else {
      oldMove.scale(units_per_millisecond*elapsedTime*0.5);
      currentTrail.end = Vector.add(oldPos, oldMove); // stretch it a little to big avoid jumps
      trails.add(currentTrail);
      Vector s = new Vector(movement);
      s.scale(-0.5*elapsedTime*units_per_millisecond);
      s.add(pos.getPosition());
      currentTrail = new Trail(s, pos.getPosition(), pos.getFace(), color);
    }
  }
  
  public boolean isDead() {
    return dead;
  }
  
  private void die() {
    System.out.println("dying");
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
  public int getTimeLeft() {
    return time_to_live;
  }
  
}
