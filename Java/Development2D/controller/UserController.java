package controller;

import java.awt.EventQueue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import marker.BreadCrumbs;
import marker.ForwardGeodesic;
import marker.Marker;

import development.Development;
import development.Vector;
import frontend.ViewerController;

public abstract class UserController implements Runnable {
  /*********************************************************************************
   * Timing Constants
   * 
   * These constants define certain "timing constants" (in nanoseconds) that
   * specify how often certain actions can be created. For example, if the user
   * holds down the up arrow key for 2 seconds, the KEY_REPEAT_RATE variable
   * explains how many "Forward" actions those 2 seconds translate to.
   *********************************************************************************/
  protected static final long KEY_REPEAT_DELAY = 1;//1;
  protected static final long KEY_REPEAT_RATE = 90;//90
  protected static long SLEEP_TIME = 10;
  protected final int MAX_REPEAT_RATE = 100; // Hz

  public boolean isPaused = false;


  /*********************************************************************************
   * This enumeration defines all of the different kinds of user input that we
   * know how to process.
   *********************************************************************************/
  public static enum Action {
    Right, Left, Forward, Back, A_Button, B_Button, start, L, R
  }

  /*********************************************************************************
   * Data Structures
   * 
   * We use actions ordered by the actionQueue to make changes to the model,
   * through the reference "development."
   * 
   * The "keyRepeatTimer" variable and "repeatingTasks" map are used to process
   * certain repeating keystrokes (like holding down the up arrow) into a
   * collection of discrete actions.
   *********************************************************************************/
  private Development development;
  protected Marker source;
  protected BlockingQueue<Action> actionQueue;
  private BreadCrumbs crumbs;
  private ForwardGeodesic geodesic;

  protected Timer keyRepeatTimer;
  private Map<Action, TimerTask> repeatingTasks;

  /*********************************************************************************
   * UserController
   * 
   * This constructor builds a new UserController to control the input
   * Development. From this development, we construct the data structures we use
   * internally to process actions.
   **********************************************************************************/
  public UserController(Development dev, BreadCrumbs bc, ForwardGeodesic geo) {
    development = dev;
    actionQueue = new LinkedBlockingQueue<Action>();
    geodesic = geo;
    repeatingTasks = new EnumMap<Action, TimerTask>(Action.class);
    keyRepeatTimer = new Timer("Button Repeat Timer");
    crumbs = bc;
  }
  
  public UserController(){
    development = null;
    geodesic = null;
    actionQueue = new LinkedBlockingQueue<Action>();
    repeatingTasks = new EnumMap<Action, TimerTask>(Action.class);
    keyRepeatTimer = new Timer("Button Repeat Timer");
  }

  /*********************************************************************************
   * isRepeating
   * 
   * This convenience method checks whether the input action is currently being
   * monitored for repeating.
   *********************************************************************************/
  protected synchronized boolean isRepeating(Action m) {
    return repeatingTasks.get(m) != null;
  }

  /*********************************************************************************
   * startRepeating
   * 
   * Given an input Action, this method is responsible for configuring our data
   * structures to log repeating input from the keyboard. This means setting a
   * timer to keep track of how long the input action has been taking place, and
   * placing the corresponding events in the "actionQueue" for processing.
   *********************************************************************************/
  protected synchronized void startRepeatingAction(Action action) {
    assert EventQueue.isDispatchThread();

    if (isRepeating(action))
      return;

    long delay = KEY_REPEAT_DELAY;
    int rate = (int) KEY_REPEAT_RATE;
    if (rate >= MAX_REPEAT_RATE) {
      rate = MAX_REPEAT_RATE;
    }

    long period = (long) (1000.0 / rate);

    final Action repeatAction = action;
    TimerTask tt = new TimerTask() {
      public void run() {
        switch (repeatAction) {
        case Forward:
          actionQueue.add(Action.Forward);
          break;
        case Back:
          actionQueue.add(Action.Back);
          break;
        case Left:
          actionQueue.add(Action.Left);
          break;
        case Right:
          actionQueue.add(Action.Right);
          break;
        case A_Button:
          actionQueue.add(Action.A_Button);
          break;
        case B_Button:
          actionQueue.add(Action.B_Button);
          break;
        case start:
          actionQueue.add(Action.start);
          break;
        case L:
          actionQueue.add(Action.L);
        }
        // Attempt to make it more responsive to key-releases.
        // Even if there are multiple this-tasks piled up (due to
        // "scheduleAtFixedRate") we don't want this thread to take
        // precedence over AWT thread.
        Thread.yield();
      }
    };

    repeatingTasks.put(action, tt);
    keyRepeatTimer.scheduleAtFixedRate(tt, delay, period);
  }

  /*********************************************************************************
   * stopRepeating
   * 
   * As its name suggests, this method is called when we need to adjust our data
   * structures (timers, etc.) to signal that a particular repeating action (a
   * user holding down a key) has stopped.
   *********************************************************************************/
  protected synchronized void stopRepeatingAction(Action m) {
    if (!isRepeating(m))
      return;
    repeatingTasks.get(m).cancel();
    repeatingTasks.put(m, null);
  }

  /*********************************************************************************
   * runNextAction
   * 
   * Code that uses KeyboardController call this method in order to make a
   * single "atomic" update to the model, based upon some user input. If there
   * are user input actions sitting in the queue, this method takes the first
   * one, processes it, and returns true (to indicate an update occurred). If no
   * change takes place (due to lack of actions) then the method returns false.
   *********************************************************************************/
  public boolean runNextAction() {
    if (actionQueue.isEmpty())
      return false;

    Action aa = actionQueue.poll();

    switch (aa) {
    case Forward:
      if(development == null){
        Vector dx = source.getPosition().getDirectionForward();
        dx.normalize();
        dx.scale(.01);
        source.getPosition().move(dx);
      }  
      else
      development.translateSourcePoint(0.01, 0);
      break;
    case Back:
      if(development == null){
        Vector forward = source.getPosition().getDirectionForward();
        forward.normalize();
        Vector dx = new Vector(forward);
        dx.scale(-.01);
        source.getPosition().move(dx);
      }  
      else
      development.translateSourcePoint(-0.01, 0);
      break;
    case Left:
      if(development == null){
         source.getPosition().rotateOrientation(-.05);
      }  
      else
      development.rotate(-0.05);
      break;
    case Right:
      if(development == null){
        source.getPosition().rotateOrientation(.05);
      }
      else
      development.rotate(0.05);
      break;
    case start:
      isPaused = true;
      break;
    case A_Button:
      if(crumbs != null && ViewerController.allowMarkerBox.isSelected())
        crumbs.addMarker( development.getSource() );
      break;
    case B_Button:
      if( geodesic != null && ViewerController.allowMarkerBox.isSelected())
        geodesic.generateGeodesic( development.getSource() );
      break;
    }
    
    return true;
  }
  
  /*********************************************************************************
   * getNextAction
   * 
   * Allows any class with a UserController object to get the next action in the
   * queue.  Allows the controller to be used for input in any setting (navigating 
   * menus for example) not only in running the development simulation.
   *********************************************************************************/
  public Action getNextAction(){
    Action aa = actionQueue.poll();
    return aa;
  }
  
  public Action seeNextAction(){
    Action aa = actionQueue.peek();
    return aa;
  }

  public void resetPausedFlag(){
    isPaused = false;
  }
  
  public boolean isPaused(){
    return isPaused;
  }
  
  public void clear() {
    this.actionQueue.clear();
  }
}
