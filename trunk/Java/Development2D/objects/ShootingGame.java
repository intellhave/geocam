package objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

import development.Vector;

/* This is a type of ObjectDynamics which allows the user to add some moving targets and bullets
 * when a bullet hits a target, the target disappears
 */

public class ShootingGame extends ObjectDynamics{

  //list of objects
  private LinkedList<MovingObject> targetList = new LinkedList<MovingObject>();
  private LinkedList<MovingObject> bulletList = new LinkedList<MovingObject>();
  
  //extra events
  public static final int EVENT_OBJECT_HIT = getNextEventID();
  
  //options
  private double targetSpeed = 0.5; //units per sec
  private double bulletSpeed = 2.0; //units per sec
  private ObjectAppearance targetAppearance = new ObjectAppearance(0.05, Color.BLUE);
  private ObjectAppearance bulletAppearance = new ObjectAppearance(0.01, Color.BLACK);
  private PathAppearance bulletTrailAppearance = new PathAppearance(0.01, Color.BLACK, 0.01, Color.BLACK);
  private double collisionThreshhold = 0.06; //should be target radius + bullet radius
  private boolean clearBulletsOnHit = true;
  
  //constructor
  public ShootingGame(int tickMS){ super(tickMS); }
  
  //options
  public void setTargetSpeed(double targetUnitsPerSecond){ targetSpeed = targetUnitsPerSecond; }
  public void setBulletSpeed(double bulletUnitsPerSecond){ bulletSpeed = bulletUnitsPerSecond; }
  public void setTargetRadius(double radius){ targetAppearance.setRadius(radius); }
  public void setBulletRadius(double radius){ bulletAppearance.setRadius(radius); }
  public void setCollisionThreshhold(double d){ collisionThreshhold = d; }
  public void setClearBulletsOnHit(boolean value){ clearBulletsOnHit = value; }
  
  //adding targets and bullets
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
    
    MovingObject newBullet = new MovingObject(mp, bulletAppearance, Vector.scale(direction,scalefactor));
    newBullet.setTrailEnabled(1,bulletTrailAppearance);
    bulletList.add(newBullet);
  }
  
  //dynamics
  protected void evolveDynamics(long dt){

    for(MovingObject o : targetList){ o.updatePosition(dt); } 
    for(MovingObject o : bulletList){ o.updatePosition(dt); } 
    
    //check for any instance of a bullet hitting a target
    boolean hitObject = false;
    
    ArrayList<MovingObject> killedTargets = new ArrayList<MovingObject>();
    ArrayList<MovingObject> killedBullets = new ArrayList<MovingObject>();
    
    for(MovingObject bullet : bulletList){
      for(MovingObject target : targetList){
        boolean hit = checkCollision(bullet,target);
        if(hit){ 
          hitObject = true;
          killedTargets.add(target);
          killedBullets.add(bullet);
        }
      }
    }
    
    //get rid of the killed targets and bullets
    for(MovingObject killedBullet : killedBullets){
      bulletList.remove(killedBullet);
      killedBullet.removeFromManifold();
    }
    for(MovingObject killedTarget : killedTargets){
      ManifoldPosition ktPos = new ManifoldPosition(killedTarget.getFace(), killedTarget.getPosition());
      ExplodingObject eo = new ExplodingObject(ktPos);
      targetList.remove(killedTarget);
      killedTarget.removeFromManifold();
    }
    
    //notify listeners if hit occurred
    if(hitObject){
      if(clearBulletsOnHit){
        for(MovingObject bullet : bulletList){
          bullet.removeFromManifold();
        }
        bulletList.clear();
      }
      notifyListeners(EVENT_OBJECT_HIT); 
    }
  }
  
  //check if the objects are within the collision threshhold
  //should use the real manifold metric here, but that's not implemented.  someday...
  private boolean checkCollision(MovingObject bullet, MovingObject target){
    
    if(bullet.getFace() == target.getFace()){
      Vector v = bullet.getPosition();
      Vector w = target.getPosition();
      
      double d = collisionThreshhold+1;
      try{ d=Vector.distance(v,w); }catch(Exception e){}
      
      return (d < collisionThreshhold);
    }
    return false;
  }
}
