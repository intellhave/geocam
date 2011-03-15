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

  public static final Integer EVENT_OBJECT_UPDATE = new Integer(0);
  /*TODO (Timing)*/ private static final int TASK_OBJECT_DYNAMICS = TimingStatistics.generateTaskTypeID("Object Dynamics");
  
  //-- 'custom obsevable' code -----------------------
  public interface ObjectViewer{
    public abstract void updateObjects(); 
  }
  LinkedList<ObjectViewer> viewers = new LinkedList<ObjectViewer>();
  
  private void notifyViewers(){
    for(ObjectViewer ov : viewers){ 
      if(ov != null){ ov.updateObjects(); }
    }
  }
  
  public void addViewer(ObjectViewer ov){ viewers.add(ov); }
  public void removeViewer(ObjectViewer ov){ viewers.remove(ov); }
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
    notifyViewers();
  }
  
  abstract protected void evolveDynamics(long dt);
 
}
