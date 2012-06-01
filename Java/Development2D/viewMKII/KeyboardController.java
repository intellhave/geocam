package viewMKII;

import java.awt.EventQueue;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.AbstractQueue;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import development.Development;

public class KeyboardController implements KeyEventDispatcher {

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

  /*********************************************************************************
   * This enumeration defines all of the different kinds of user input that we
   * know how to process.
   *********************************************************************************/
  private static enum Action {
    Right, Left, Forward, Back
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

  Timer keyRepeatTimer;
  Map<Action, TimerTask> repeatingTasks;

  /*********************************************************************************
   * KeyboardController
   * 
   * This constructor builds a new KeyboardController to control the input
   * Development. From this development, we construct the data structures we use
   * internally to process actions, and signal the KeyboardFocusManager that we
   * want this code to be notified of input from the keyboard.
   **********************************************************************************/
  public KeyboardController(Development dev) {
    development = dev;
    actionQueue = new LinkedBlockingQueue<Action>();
    repeatingTasks = new EnumMap<Action, TimerTask>(Action.class);
    init();
  }

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
  public boolean dispatchKeyEvent(KeyEvent e) {

    assert EventQueue.isDispatchThread();

    int kc = e.getKeyCode();

    if (e.getID() == KeyEvent.KEY_PRESSED) {
      // Note: If repeat is activated, we ignore KEY_PRESSED events.
      // Technically, it should be impossible for another KEY_PRESSED event to
      // be created if one is already repeating, so this check is somewhat
      // redundant.
      if (kc == KeyEvent.VK_LEFT && !isRepeating(Action.Left))
        addRepeatingAction(Action.Left);
      if (kc == KeyEvent.VK_RIGHT && !isRepeating(Action.Right))
        addRepeatingAction(Action.Right);
      if (kc == KeyEvent.VK_DOWN && !isRepeating(Action.Back))
        addRepeatingAction(Action.Back);
      if (kc == KeyEvent.VK_UP && !isRepeating(Action.Forward))
        addRepeatingAction(Action.Forward);
    }

    if (e.getID() == KeyEvent.KEY_RELEASED) {
      if (kc == KeyEvent.VK_LEFT)
        stopRepeating(Action.Left);
      if (kc == KeyEvent.VK_RIGHT)
        stopRepeating(Action.Right);
      if (kc == KeyEvent.VK_DOWN)
        stopRepeating(Action.Back);
      if (kc == KeyEvent.VK_UP)
        stopRepeating(Action.Forward);
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
    assert EventQueue.isDispatchThread();

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
   * init
   * 
   * This method resets the keyboard focus manager that this instance of
   * KeyboardController uses. Usually, this code is called from the constructor.
   * Or, one might need to "reboot" the controller after calling uninit.
   *********************************************************************************/
  public synchronized void init() {
    if (!isInited()) {
      keyRepeatTimer = new Timer("Key Repeat Timer");
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .addKeyEventDispatcher(this);
    }
  }

  /*********************************************************************************
   * isInited
   * 
   * This method returns a boolean indicating whether the timing variables for
   * this instance of KeyboardController are initialized.
   *********************************************************************************/
  public synchronized boolean isInited() {
    return keyRepeatTimer != null;
  }

  /*********************************************************************************
   * uninit
   * 
   * This method deactivates the KeyboardController, so it no longer receives
   * keyboard input. To reverse this operation, one calls init();
   *********************************************************************************/
  public synchronized void uninit() {
    if (isInited()) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .removeKeyEventDispatcher(this);

      keyRepeatTimer.cancel();
      keyRepeatTimer = null;
    }
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
      development.translateSourcePoint(0.001, 0);
      break;
    case Back:
      development.translateSourcePoint(-0.001, 0);
      break;
    case Left:
      development.rotate(-0.005);
      break;
    case Right:
      development.rotate(0.005);
      break;
    }

    return true;
  }
}
