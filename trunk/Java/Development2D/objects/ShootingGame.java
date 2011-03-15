package objects;

import java.awt.Color;
import java.util.LinkedList;

import development.Vector;

/* This is a type of ObjectDynamics which allows the user to add some moving targets and bullets
 * when a bullet hits a target, the target disappears
 */

public class ShootingGame extends ObjectDynamics{

  private LinkedList<MovingObject> targetList = new LinkedList<MovingObject>();
  private LinkedList<MovingObject> bulletList = new LinkedList<MovingObject>();
  
  double targetSpeed = 0.5;
  double bulletSpeed = 10;
  ObjectAppearance targetAppearance = new ObjectAppearance(0.05, Color.BLUE);
  ObjectAppearance bulletAppearance = new ObjectAppearance(0.01, Color.BLACK);
  
  
  public ShootingGame(int tickMS){ super(tickMS); }
  public ShootingGame(int tickMS, double targetUnitsPerSecond, double bulletUnitsPerSecond){ 
    super(tickMS);
    
    targetSpeed = targetUnitsPerSecond;
    bulletSpeed = bulletUnitsPerSecond;
  }
  
  public void addTarget(ManifoldPosition mp, Vector direction){
    
    double L = direction.length();
    if(L == 0){ return; }
    double scalefactor = targetSpeed/L;
    targetList.add(new MovingObject(mp, targetAppearance, Vector.scale(direction,scalefactor)));
  }
  
  public void addBullet(ManifoldPosition mp, Vector direction){
    
    double L = direction.length();
    if(L == 0){ return; }
    double scalefactor = bulletSpeed/L;
    bulletList.add(new MovingObject(mp, bulletAppearance, Vector.scale(direction,scalefactor)));
  }
  
  protected void evolveDynamics(long dt){
    
    for(MovingObject o : targetList){ o.updatePosition(dt); } 
    for(MovingObject o : bulletList){ o.updatePosition(dt); } 
    
    //TODO check for any instance of a bullet hitting a target
    
  }
}
