package objects;

import java.util.LinkedList;

/* This is a type of ObjectDynamics which allows the user to add some moving VisibleObjects
 * and watch them move around
 */

public class BasicMovingObjects extends ObjectDynamics{

  private LinkedList<MovingObject> objectList = new LinkedList<MovingObject>();
  
  public BasicMovingObjects(int tickMS){ super(tickMS); } 
  
  protected void evolveDynamics(long dt){
    
    for(MovingObject o : objectList){ 
      o.updatePosition(dt); 
    }
  }
  
  public void addObject(MovingObject o){
    objectList.add(o);
  }
}
