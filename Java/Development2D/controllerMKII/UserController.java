package controllerMKII;

import java.awt.EventQueue;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import viewMKII.Development;

public abstract class UserController implements Runnable {
  /*********************************************************************************
   * Timing Constants
   * 
   * These constants define certain "timing constants" (in nanoseconds) that
   * specify how often certain actions can be created. For example, if the user
   * holds down the up arrow key for 2 seconds, the KEY_REPEAT_RATE variable
   * explains how many "Forward" actions those 2 seconds translate to.
   *********************************************************************************/
  //TODO: Make it possible for classes that have a UserController to set the "sensitivity"
  //(i.e. adjust KEY_REPEAT_RATE and SLEEP_TIME)
  protected static final long KEY_REPEAT_DELAY = 1;
  protected static final long KEY_REPEAT_RATE = 50; //5;
  protected static long SLEEP_TIME = 10; //0;
  protected final int MAX_REPEAT_RATE = 100; // Hz

  /*********************************************************************************
   * This enumeration defines all of the different kinds of user input that we
   * know how to process.
   *********************************************************************************/
  public static enum Action {
    Right, Left, Forward, Back, A_Button, B_Button
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
  private BlockingQueue<Action> actionQueue;

  protected Timer keyRepeatTimer;
  private Map<Action, TimerTask> repeatingTasks;

  /*********************************************************************************
   * UserController
   * 
   * This constructor builds a new UserController to control the input
   * Development. From this development, we construct the data structures we use
   * internally to process actions.
   **********************************************************************************/
  public UserController(Development dev) {
    development = dev;
    actionQueue = new LinkedBlockingQueue<Action>();
    repeatingTasks = new EnumMap<Action, TimerTask>(Action.class);
    keyRepeatTimer = new Timer("Button Repeat Timer");
  }
  
  public UserController(){
    development = null;
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
      development.translateSourcePoint(0.01, 0);
      break;
    case Back:
      development.translateSourcePoint(-0.01, 0);
      break;
    case Left:
      development.rotate(-0.05);
      break;
    case Right:
      development.rotate(0.05);
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
}
