package objects;

import development.Vector;

/* This is a particular type of VisibleObject which is used in the BasicMovingObjects dynamics
 */

public class MovingObject extends VisibleObject{
  
  private Vector normalizedVelocity; 
  private double speed; //in units per millisecond

  public MovingObject(ManifoldPosition manifoldPosition, ObjectAppearance appearance, Vector velocityUnitsPerSecond){
    super(manifoldPosition, appearance);
    setVelocity(velocityUnitsPerSecond);
  }
  
  public void setVelocity(Vector velocityUnitsPerSecond){
    double L = velocityUnitsPerSecond.length();
    if(L == 0){
      normalizedVelocity = new Vector(1,0);
    }else{
      normalizedVelocity = Vector.scale(velocityUnitsPerSecond,1/L);
    }
    speed = L*.001;
  }
  
  public void setSpeed(double speedUnitsPerSecond){
    speed = speedUnitsPerSecond * .001;
  }
  
  public void updatePosition(double dt){
    move(Vector.scale(normalizedVelocity,dt*speed),normalizedVelocity);
  }
}
