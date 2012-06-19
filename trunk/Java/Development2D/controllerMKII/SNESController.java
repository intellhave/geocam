package controllerMKII;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import viewMKII.Development;

public class SNESController implements Runnable {

  // This thread sleeps for 10 milliseconds between runs through the loop.
  private static long sleep_time = 10;

  /*********************************************************************************
   * Timing Constants
   * 
   * These constants define certain "timing constants" (in nanoseconds) that
   * specify how often certain actions can be created. For example, if the user
   * holds down the up arrow key for 2 seconds, the KEY_REPEAT_RATE variable
   * explains how many "Forward" actions those 2 seconds translate to.
   *********************************************************************************/
  private static final long KEY_REPEAT_DELAY = 1;
  private static final long KEY_REPEAT_RATE = 50;
  private final int MAX_REPEAT_RATE = 100; // Hz

  private static enum Action {
    Forward, Back, Left, Right
  }

  private static enum Button {
    Left, Right, Up, Down, A, B, X, Y, L, R, Select, Start
  }

  private Development development;
  private BlockingQueue<Action> actionQueue;

  private Timer keyRepeatTimer;
  private Map<Action, TimerTask> repeatingTasks;

  private Controllers allControllers;
  private Controller userController;
  
  public SNESController(Development dev) {
    development = dev;
    actionQueue = new LinkedBlockingQueue<Action>();
    repeatingTasks = new EnumMap<Action, TimerTask>(Action.class);
    init();
  }

  /*********************************************************************************
   * FIXME: Does this need to be synchronized?
   *********************************************************************************/
  public synchronized void init() {
    keyRepeatTimer = new Timer("Button Repeat Timer");
    try {
      allControllers.create();
    } catch (LWJGLException e) {
      System.err.println("Error: Unable to initialize controllers.");
      e.printStackTrace();
    }
    
    if( allControllers.getControllerCount() == 0){
      System.err.println("Error: No controllers detected.");
      System.exit(1);
    }
    
    userController = allControllers.getController(0);    
  }

  private static enum ButtonState{ Up, Down }
  private class ControllerState {
    public EnumMap<Button, ButtonState> controllerState;
    
    public ControllerState( Controller c ){
      controllerState = new EnumMap<Button, ButtonState>(Button.class);
      
      controllerState.put(SNESController.Button.A, getButtonState(c,1));
      controllerState.put(SNESController.Button.B, getButtonState(c,0));
      controllerState.put(SNESController.Button.X, getButtonState(c,3));
      controllerState.put(SNESController.Button.Y, getButtonState(c,2));
      controllerState.put(SNESController.Button.L, getButtonState(c,4));
      controllerState.put(SNESController.Button.R, getButtonState(c,5));
      controllerState.put(SNESController.Button.Select, getButtonState(c,6));
      controllerState.put(SNESController.Button.Start, getButtonState(c,7));
      
      if( c.getXAxisValue() == 0.0 ){
        controllerState.put( SNESController.Button.Left, SNESController.ButtonState.Up );
        controllerState.put( SNESController.Button.Right, SNESController.ButtonState.Up );
      }
      
      if( c.getXAxisValue() == 1.0 ){
        controllerState.put( SNESController.Button.Left, SNESController.ButtonState.Up );
        controllerState.put( SNESController.Button.Right, SNESController.ButtonState.Down );
      }
      
      if( c.getXAxisValue() == -1.0 ){
        controllerState.put( SNESController.Button.Left, SNESController.ButtonState.Down );
        controllerState.put( SNESController.Button.Right, SNESController.ButtonState.Up );
      }
      
      if( c.getYAxisValue() == 0.0 ){
        controllerState.put( SNESController.Button.Up, SNESController.ButtonState.Up );
        controllerState.put( SNESController.Button.Down, SNESController.ButtonState.Up );
      }
     
      if( c.getYAxisValue() == 1.0 ){
        controllerState.put( SNESController.Button.Up, SNESController.ButtonState.Up );
        controllerState.put( SNESController.Button.Down, SNESController.ButtonState.Down );
      }
      
      if( c.getYAxisValue() == -1.0 ){
        controllerState.put( SNESController.Button.Up, SNESController.ButtonState.Down );
        controllerState.put( SNESController.Button.Down, SNESController.ButtonState.Up );
      }
    }
    
    public SNESController.ButtonState getButtonState( Controller c, int buttonNum ){
        if( c.isButtonPressed( buttonNum ) ){
          return SNESController.ButtonState.Down;
        } else {
          return SNESController.ButtonState.Up;
        }
    }
    
    public boolean equals( ControllerState other ){
      for( SNESController.Button button : SNESController.Button.values() ){
        if( controllerState.get(button) != other.controllerState.get(button))
          return false;
      }
      return true;
    }
  }
  
  public void run() {
    ControllerState prev, curr;
    
    curr = new ControllerState( userController );
    while (true) {
      prev = curr;
      userController.poll();
      curr = new ControllerState( userController );
      
      if( ! prev.equals( curr ) ){
        for( Button b : Button.values() ){
          if(prev.controllerState.get(b) != curr.controllerState.get(b)){
            dispatchKeyEvent( b, curr.controllerState.get(b) );
          }
        }
      }
      
      try {
        Thread.sleep(sleep_time);
      } catch (InterruptedException e) {
        System.err.println("Error: Could not sleep the controller thread.");
        e.printStackTrace();
      }
    }
  }

  /*
   * currentState == Up => Button was released.
   * currentState == Down => Button was pressed.
   */
  /*********************************************************************************
   * dispatchKeyEvent
   * 
   * This method is responsible for receiving keyboard input --- it gets called
   * by external code, as a result of the call we made to KeyboardFocusManager
   * in the constructor. One important fact about this method is that it is only
   * supposed to RECEIVE and log user input, NOT act on it. This is because we
   * need to carefully synchronize the tasks of rendering the scene and
   * processing user input to achieve a smooth animation.
   *********************************************************************************/
  public boolean dispatchKeyEvent(Button eventButton, ButtonState currentState) {

    if ( currentState == ButtonState.Down ) {
      switch(eventButton){
        case Left:
        addRepeatingAction(Action.Left);
        break;
        case Right:
        addRepeatingAction(Action.Right);
       break;
        case Down:
        addRepeatingAction(Action.Back);
        break;
        case Up:
        addRepeatingAction(Action.Forward);
        break;
    }
    }

    if ( currentState == ButtonState.Up ){
      switch( eventButton ){
      case Left:
        stopRepeating(Action.Left);
        break;
      case Right:
        stopRepeating(Action.Right);
        break;
      case Down:
        stopRepeating(Action.Back);
        break;
      case Up:
        stopRepeating(Action.Forward);
        break;
      }
    }

    return false;
    }
 

  /*********************************************************************************
   * isRepeating
   * 
   * This convenience method checks whether the input action is currently being
   * monitored for repeating.
   *********************************************************************************/
  private synchronized boolean isRepeating(Action m) {
    return repeatingTasks.get(m) != null;
  }

  /*********************************************************************************
   * addRepeatingAction
   * 
   * Given an input action, this method initializes the timing variables needed
   * to record the user input corresponding to that action. (For example,
   * addRepeatingAction(Action.Left) is called when the user starts holding down
   * the "left" arrow on his keyboard.)
   *********************************************************************************/
  private synchronized void addRepeatingAction(Action move) {
    // Initiate key repeats
    long delay = KEY_REPEAT_DELAY;
    long rate = KEY_REPEAT_RATE;
    if (delay > 0 && rate > 0)
      startRepeating(move);
  }

  /*********************************************************************************
   * startRepeating
   * 
   * Given an input Action, this method is responsible for configuring our data
   * structures to log repeating input from the keyboard. This means setting a
   * timer to keep track of how long the input action has been taking place, and
   * placing the corresponding events in the "actionQueue" for processing.
   *********************************************************************************/
  private synchronized void startRepeating(Action action) {
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
  private synchronized void stopRepeating(Action m) {
    if (!isRepeating(m))
      return;
    repeatingTasks.get(m).cancel();
    repeatingTasks.put(m,null);
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
}
