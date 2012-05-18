package markers;

import java.util.LinkedList;

/* This is a type of ObjectDynamics which allows the user to add some moving VisibleObjects
 * and watch them move around
 */

public class BasicMovingMarkers extends MarkerDynamics{

  private LinkedList<MovingMarker> objectList = new LinkedList<MovingMarker>();
  
  public BasicMovingMarkers(int tickMS){ super(tickMS); } 
  
  protected void evolveDynamics(long dt){
    
    for(MovingMarker o : objectList){ 
      o.updatePosition(dt); 
    }
  }
  
  public void addObject(MovingMarker o){
    objectList.add(o);
  }
  
  public void removeObject(MovingMarker o){
    objectList.remove(o);
  }
}
