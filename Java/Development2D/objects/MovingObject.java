package objects;

import development.Vector;

/* This is a particular type of VisibleObject which is used in the BasicMovingObjects dynamics
 */

public class MovingObject extends VisibleObject{
  
  private double speed; //in units per millisecond; 'forward' vector is normalized velocity

  public MovingObject(ManifoldPosition manifoldPosition, ObjectAppearance appearance, Vector velocityUnitsPerSecond){
    super(manifoldPosition, appearance);
    setVelocity(velocityUnitsPerSecond);
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
    move(dt*speed, 0);
  }
}
