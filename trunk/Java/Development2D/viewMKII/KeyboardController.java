package viewMKII;

import java.awt.EventQueue;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import development.Development;

public class KeyboardController implements KeyEventDispatcher {

  private static enum Move { Right, Left, Forward, Back }

  private static final long KEY_REPEAT_DELAY = 1;
  private static final long KEY_REPEAT_RATE = 25;
  
  private final int MAX_REPEAT_RATE = 100; // Hz

  private Development development;
 
  Timer keyRepeatTimer;
  Map<Move, TimerTask> repeatingTasks = new EnumMap<Move, TimerTask>( Move.class );

  public KeyboardController(Development dev) {
    this.development = dev;
    
    if (!isInited()) {
      keyRepeatTimer = new Timer("Key Repeat Timer");
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .addKeyEventDispatcher(this);
    }
  }

  public boolean dispatchKeyEvent(KeyEvent e) {

    assert EventQueue.isDispatchThread();

    int kc = e.getKeyCode();

    if (e.getID() == KeyEvent.KEY_PRESSED) {
      System.out.println("Received key press" + kc);
      // If repeat is activated, ignore KEY_PRESSED events.
      // Should actually not occur, since KEY_RELEASED *should* have been
      // intercepted since last KEY_PRESSED.
      if ( kc == KeyEvent.VK_LEFT && !isRepeating(Move.Left))
        move(Move.Left);
      if ( kc == KeyEvent.VK_RIGHT && !isRepeating(Move.Right))
        move(Move.Right);
      if ( kc == KeyEvent.VK_DOWN && !isRepeating(Move.Back))        
        move(Move.Back);
      if ( kc == KeyEvent.VK_UP && !isRepeating(Move.Forward))        
        move(Move.Forward);
    }

    if (e.getID() == KeyEvent.KEY_RELEASED) {
      if (kc == KeyEvent.VK_LEFT)
        stopRepeating(Move.Left);
      if (kc == KeyEvent.VK_RIGHT)
        stopRepeating(Move.Right);
      if (kc == KeyEvent.VK_DOWN)
        stopRepeating(Move.Back);
      if (kc == KeyEvent.VK_UP)
        stopRepeating(Move.Forward);  
    }

    return false;
  }

  private synchronized void stopRepeating(Move m) {
    if (!isRepeating(m))
      return;
    repeatingTasks.get(m).cancel();
    repeatingTasks.remove(m);
  }

  private synchronized boolean isRepeating(Move m) {
    return repeatingTasks.get(m) != null;
  }

  private synchronized void move(Move move) {
    assert EventQueue.isDispatchThread();

    // Initiate key repeats
    int delay = (int) KEY_REPEAT_DELAY;
    int rate = (int) KEY_REPEAT_RATE;
    if (delay > 0 && rate > 0)
      startRepeating(move);
  }
  
  private synchronized void startRepeating(Move move) {
    assert EventQueue.isDispatchThread();

    if (isRepeating(move))
      return;

    long delay = KEY_REPEAT_DELAY;
    int rate = (int) KEY_REPEAT_RATE;
    if (rate >= MAX_REPEAT_RATE) {
      rate = MAX_REPEAT_RATE;      
    }

    long period = (long) (1000.0 / rate);

    final Move repeatMove = move;
    TimerTask tt = new TimerTask() {

      // Should only be executed by keyRepeatTimer thread.
      public void run() {     
        System.out.println("Executing Move" + repeatMove);
        
        switch (repeatMove) {
        case Forward:   
          development.translateSourcePoint(0.05, 0);         
          break;
        case Back:   
          development.translateSourcePoint(-0.05, 0);        
          break;
        case Left:   
          development.rotate(-0.1);
          break;
        case Right:   
          development.rotate(0.1);
          break;
        }        
        // Attempt to make it more responsive to key-releases.
        // Even if there are multiple this-tasks piled up (due to
        // "scheduleAtFixedRate") we don't want this thread to take
        // precedence over AWT thread.
        Thread.yield();
      }
    };
    repeatingTasks.put(move, tt);
    keyRepeatTimer.scheduleAtFixedRate(tt, delay, period);
  }

  public synchronized void init() {
    if (!isInited()) {
      keyRepeatTimer = new Timer("Key Repeat Timer");
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .addKeyEventDispatcher(this);
    }
  }

  public synchronized boolean isInited() {
    return keyRepeatTimer != null;
  }

  public synchronized void uninit() {
    if (isInited()) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .removeKeyEventDispatcher(this);

      keyRepeatTimer.cancel();
      keyRepeatTimer = null;
    }
  }
}
