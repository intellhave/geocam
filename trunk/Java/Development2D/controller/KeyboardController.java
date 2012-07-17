package controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import marker.BreadCrumbs;

import development.Development;


public class KeyboardController extends UserController implements
    KeyEventDispatcher {

  /*********************************************************************************
   * KeyboardController
   * 
   * This constructor builds a new KeyboardController to control the input
   * Development. From this development, we construct the data structures we use
   * internally to process actions, and then the initialize method is called.
   **********************************************************************************/
  public KeyboardController(Development dev, BreadCrumbs bc) {
    super(dev, bc);
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(this);
  }

  /*********************************************************************************
   * dispatchKeyEvent
   * 
   * This method is responsible for receiving keyboard input.This method gets
   * called by external code. One important fact about this method is that it is
   * only supposed to RECEIVE and log user input, NOT act on it. This is because
   * we need to carefully synchronize the tasks of rendering the scene and
   * processing user input to achieve a smooth animation.
   *********************************************************************************/
  public boolean dispatchKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        startRepeatingAction(Action.Left);
        break;
      case KeyEvent.VK_RIGHT:
        startRepeatingAction(Action.Right);
        break;
      case KeyEvent.VK_UP:
        startRepeatingAction(Action.Forward);
        break;
      case KeyEvent.VK_DOWN:
        startRepeatingAction(Action.Back);
        break;
      case KeyEvent.VK_SPACE:
        startRepeatingAction(Action.start);
        break;
      case KeyEvent.VK_ENTER:
        startRepeatingAction(Action.A_Button);
        break;
      case KeyEvent.VK_BACK_SPACE:
      startRepeatingAction(Action.B_Button);
      break;
      }
    }

    if (e.getID() == KeyEvent.KEY_RELEASED) {
      switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        stopRepeatingAction(Action.Left);
        break;
      case KeyEvent.VK_RIGHT:
        stopRepeatingAction(Action.Right);
        break;
      case KeyEvent.VK_UP:
        stopRepeatingAction(Action.Forward);
        break;
      case KeyEvent.VK_DOWN:
        stopRepeatingAction(Action.Back);
        break;
      case KeyEvent.VK_SPACE:
        stopRepeatingAction(Action.start);
        break;
      case KeyEvent.VK_ENTER:
        stopRepeatingAction(Action.A_Button);
        break;
      case KeyEvent.VK_BACK_SPACE:
      stopRepeatingAction(Action.B_Button);
      break;
      case KeyEvent.VK_SHIFT:
        actionQueue.add(Action.L);
      }
    }
    // The code that calls this method expects a boolean.
    return false;
  }

  /*********************************************************************************
   * run
   * 
   * This class has a run method because it extends runnable. However, this
   * method does not need to do anything.
   **********************************************************************************/
  public void run() {
  }
}