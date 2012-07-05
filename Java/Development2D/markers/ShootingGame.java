package markers;

import java.util.ArrayList;
import java.util.LinkedList;

import markersMKII.MarkerAppearance;

import development.Vector;

/* This is a type of ObjectDynamics which allows the user to add some moving targets and bullets
 * when a bullet hits a target, the target disappears
 */

public class ShootingGame extends MarkerDynamics{

  //list of objects
  private LinkedList<MovingMarker> targetList = new LinkedList<MovingMarker>();
  private LinkedList<MovingMarker> bulletList = new LinkedList<MovingMarker>();
  
  //extra events
  public static final int EVENT_OBJECT_HIT = getNextEventID();
  
  //options
  private double targetSpeed = 0.5; //units per sec
  private double bulletSpeed = 2.0; //units per sec
  private MarkerAppearance targetAppearance = new MarkerAppearance();
  private MarkerAppearance bulletAppearance = new MarkerAppearance();
  //private PathAppearance bulletTrailAppearance = new PathAppearance(0.01, Color.BLACK, 0.01, Color.BLACK);
  private double collisionThreshhold = 0.06; //should be target radius + bullet radius
  private boolean clearBulletsOnHit = true;
  
  //constructor
  public ShootingGame(int tickMS){ super(tickMS); }
  
  //options
  public void setTargetSpeed(double targetUnitsPerSecond){ targetSpeed = targetUnitsPerSecond; }
  public void setBulletSpeed(double bulletUnitsPerSecond){ bulletSpeed = bulletUnitsPerSecond; }
  public void setTargetRadius(double radius){ targetAppearance.setScale(radius); }
  public void setBulletRadius(double radius){ bulletAppearance.setScale(radius); }
  public void setCollisionThreshhold(double d){ collisionThreshhold = d; }
  public void setClearBulletsOnHit(boolean value){ clearBulletsOnHit = value; }
  
  //adding targets and bullets
  public void addTarget(ManifoldPosition mp, Vector direction){
    
    double L = direction.length();
    if(L == 0){ return; }
    double scalefactor = targetSpeed/L;
    targetList.add(new MovingMarker(mp, targetAppearance, Vector.scale(direction,scalefactor)));
  }
  
  public void addBullet(ManifoldPosition mp, Vector direction){
    
    double L = direction.length();
    if(L == 0){ return; }
    double scalefactor = bulletSpeed/L;
    
    MovingMarker newBullet = new MovingMarker(mp, bulletAppearance, Vector.scale(direction,scalefactor));
    //newBullet.setTrailEnabled(1,bulletTrailAppearance);
    bulletList.add(newBullet);
  }
  
  //dynamics
  protected void evolveDynamics(long dt){

    for(MovingMarker o : targetList){ o.updatePosition(dt); } 
    for(MovingMarker o : bulletList){ o.updatePosition(dt); } 
    
    //check for any instance of a bullet hitting a target
    boolean hitObject = false;
    
    ArrayList<MovingMarker> killedTargets = new ArrayList<MovingMarker>();
    ArrayList<MovingMarker> killedBullets = new ArrayList<MovingMarker>();
    
    for(MovingMarker bullet : bulletList){
      for(MovingMarker target : targetList){
        boolean hit = checkCollision(bullet,target);
        if(hit){ 
          hitObject = true;
          killedTargets.add(target);
          killedBullets.add(bullet);
        }
      }
    }
    
    //get rid of the killed targets and bullets
    for(MovingMarker killedBullet : killedBullets){
      bulletList.remove(killedBullet);
      killedBullet.removeFromManifold();
    }
    for(MovingMarker killedTarget : killedTargets){
      ManifoldPosition ktPos = new ManifoldPosition(killedTarget.getFace(), killedTarget.getPosition());
      ExplodingMarker eo = new ExplodingMarker(ktPos);
      targetList.remove(killedTarget);
      killedTarget.removeFromManifold();
    }
    
    //notify listeners if hit occurred
    if(hitObject){
      if(clearBulletsOnHit){
        for(MovingMarker bullet : bulletList){
          bullet.removeFromManifold();
        }
        bulletList.clear();
      }
      notifyListeners(EVENT_OBJECT_HIT); 
    }
  }
  
  //check if the objects are within the collision threshhold
  //should use the real manifold metric here, but that's not implemented.  someday...
  private boolean checkCollision(MovingMarker bullet, MovingMarker target){
    
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
