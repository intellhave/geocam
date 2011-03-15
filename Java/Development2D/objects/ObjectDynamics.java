package objects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Timer;

import development.TimingStatistics;

/* abstract class representing some specific dynamics for VisibleObjects on the Triangulation
 * 
 * To use this, extend it and add whatever features or special types of VisibleObjects are necessary
 * then simply create an instance of the class and call ObjectDynamics.start()
 * 
 * Note that you are not forced to use the Timer; you can manually call 'evolve' whenever
 * 
 * If you have some class which draws objects on the triangulation, and you want it to be
 * notified when the dynamics have changed the objects, then implement the ObjectViewer interface
 * and use addViewer to add the object you want to be notified (like Observer/Observable)
 * 
 * Example implementations:  BasicMovingObjects, ShootingGame
 */

public abstract class ObjectDynamics {

  public static int nextEventID = 0; 
  public static final int EVENT_DYNAMICS_EVOLVED = getNextEventID();
  
  /*TODO (Timing)*/ private static final int TASK_OBJECT_DYNAMICS = TimingStatistics.generateTaskTypeID("Object Dynamics");
  
  //-- 'custom obsevable' code -----------------------
  public interface DynamicsListener{
    public abstract void dynamicsEvent(int eventID); 
  }
  LinkedList<DynamicsListener> listeners = new LinkedList<DynamicsListener>();
  
  protected void notifyListeners(int eventID){
    for(DynamicsListener dl : listeners){ 
      if(dl != null){ dl.dynamicsEvent(eventID); }
    }
  }
  
  public void addListener(DynamicsListener dl){ listeners.add(dl); }
  public void removeListener(DynamicsListener dl){ listeners.remove(dl); }
  
  protected static int getNextEventID(){
    nextEventID++;
    return nextEventID;
  }
  //-----------------------------------------------
  
  private long time;
  private Timer moveTimer;
  
  public ObjectDynamics(int tickMS){ 
    
    moveTimer = new Timer(tickMS,null);
    moveTimer.addActionListener(new ActionListener(){
      
      public void actionPerformed(ActionEvent e){
        
        long newtime = System.currentTimeMillis();
        long dt = newtime - time;
        
        /*TODO (Timing)*/ long taskID = TimingStatistics.startTask(TASK_OBJECT_DYNAMICS);
        evolve(dt);
        /*TODO (Timing)*/ TimingStatistics.endTask(taskID);
        
        time = System.currentTimeMillis();
      }
      
    });
  }
  
  public void start(){
    time = System.currentTimeMillis();
    moveTimer.start();
  }
  
  public void stop(){
    moveTimer.stop();
  }
  
  public void evolve(long dt){
    evolveDynamics(dt);
    notifyListeners(EVENT_DYNAMICS_EVOLVED);
  }
  
  abstract protected void evolveDynamics(long dt);
 
}
