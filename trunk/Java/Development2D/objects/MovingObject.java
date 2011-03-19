package objects;

import development.Vector;

/* This is a particular type of VisibleObject which is used in the BasicMovingObjects dynamics
 */

public class MovingObject extends VisibleObject{
  
  private double speed; //in units per millisecond; 'forward' vector is normalized velocity
  
  private boolean trailVisible = false; 
  private double trailLength = 0;
  private VisiblePath trail = null;
  private PathAppearance trailAppearance;

  public MovingObject(ManifoldPosition manifoldPosition, ObjectAppearance appearance, Vector velocityUnitsPerSecond){
    super(manifoldPosition, appearance);
    setVelocity(velocityUnitsPerSecond);
  }
  
  public void setTrailEnabled(double length, PathAppearance appearance){
    trailVisible = true;
    trailLength = length;
    trailAppearance = appearance;
    updateTrail();
  }
  public void setTrailDisabled(){
    trailVisible = false;
    trail.removeFromManifold();
  }
  private void updateTrail(){
    if(!trailVisible){ return; }
    
    //clear existing trail from the manifold, if it exists
    if(trail == null){ trail = new VisiblePath(trailAppearance); }
    else{ trail.clear(); }
    
    //make a copy of the object's position and move it backwards, recording the path
    //since 'trail' is a VisiblePath, ManifoldObjectHandler is automatically updated
    ManifoldPosition mp = new ManifoldPosition(this);
    mp.moveWithTrail(getDirection(-trailLength,0),trail);
  }
  public void setVelocity(Vector velocityUnitsPerSecond){
    double L = velocityUnitsPerSecond.length();
    if(L == 0){ setOrientation(new Vector(1,0)); } 
    else{ setOrientation(Vector.scale(velocityUnitsPerSecond,1/L)); }
    speed = L*.001; //convert to units per ms
  }
  
  public void setSpeed(double speedUnitsPerSecond){
    speed = speedUnitsPerSecond*.001;
  }
  
  public void updatePosition(double dt){
    move(getDirection(dt*speed, 0));
    updateTrail();
  }
  
  public void removeFromManifold(){
    super.removeFromManifold();
    if(trail != null){ trail.removeFromManifold(); }
  }
}
