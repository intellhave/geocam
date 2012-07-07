package controller;

import java.util.EnumMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import controller.UserController.Action;

import viewMKII.Development;

public class SNESController extends UserController {

  /*********************************************************************************
   * This enumeration defines all the buttons available on an SNES Controller
   *********************************************************************************/
  private static enum Button {
    Left, Right, Up, Down, A, B, X, Y, L, R, Select, Start
  }

  /*********************************************************************************
   * ButtonState Enum
   * 
   * This enumeration describes the possible states that any button on the
   * controller can be in. Buttons can either be up (released) or down
   * (pressed).
   *********************************************************************************/
  private static enum ButtonState {
    Up, Down
  }

  /*********************************************************************************
   * Controller Variables
   * 
   *********************************************************************************/
  private Controllers allControllers;
  private Controller userController;

  /*********************************************************************************
   * SNESController
   * 
   * This constructor builds a new SNESController to control the input
   * Development. From this development, we construct the data structures we use
   * internally to process actions, and then the initialize method is called.
   **********************************************************************************/
  public SNESController(Development dev) {
    super(dev);
    init();
  }
  
  public SNESController(){
    super();
    init();
  }

  /*********************************************************************************
   * init
   * 
   * This method finds all of the controllers active in the computer and selects
   * one to be the user controller.
   *********************************************************************************/
  public synchronized void init() {

    try {
      allControllers.create();
    } catch (LWJGLException e) {
      System.err.println("Error: Unable to initialize controllers.");
      e.printStackTrace();
    }

    if (allControllers.getControllerCount() == 0) {
      System.err.println("Error: No controllers detected.");
      System.exit(1);
    }

    userController = allControllers.getController(0);
  }

  /*********************************************************************************
   * ControllerState Class
   * 
   * This is a private inner class that stores information about which buttons
   * are currently being pressed on the controller. ControllerState contains an
   * equals method that compares two ControllerStates and tells whether they are
   * the same.
   *********************************************************************************/
  private class ControllerState {
    public EnumMap<Button, ButtonState> controllerState;

    public ControllerState(){
      controllerState = new EnumMap<Button, ButtonState>(Button.class);
      controllerState.put(SNESController.Button.A, ButtonState.Up);
      controllerState.put(SNESController.Button.B, ButtonState.Up);
      controllerState.put(SNESController.Button.X, ButtonState.Up);
      controllerState.put(SNESController.Button.Y, ButtonState.Up);
      controllerState.put(SNESController.Button.L, ButtonState.Up);
      controllerState.put(SNESController.Button.R, ButtonState.Up);
      controllerState.put(SNESController.Button.Select, ButtonState.Up);
      controllerState.put(SNESController.Button.Start, ButtonState.Up);
      
      controllerState.put(SNESController.Button.Right, ButtonState.Up);
      controllerState.put(SNESController.Button.Left, ButtonState.Up);
      controllerState.put(SNESController.Button.Up, ButtonState.Up);
      controllerState.put(SNESController.Button.Down, ButtonState.Up);
    }
    
    public ControllerState(Controller c) {
      controllerState = new EnumMap<Button, ButtonState>(Button.class);

      controllerState.put(SNESController.Button.A, getButtonState(c, 1));
      controllerState.put(SNESController.Button.B, getButtonState(c, 0));
      controllerState.put(SNESController.Button.X, getButtonState(c, 3));
      controllerState.put(SNESController.Button.Y, getButtonState(c, 2));
      controllerState.put(SNESController.Button.L, getButtonState(c, 4));
      controllerState.put(SNESController.Button.R, getButtonState(c, 5));
      controllerState.put(SNESController.Button.Select, getButtonState(c, 6));
      controllerState.put(SNESController.Button.Start, getButtonState(c, 7));

      // Note: the controller's x and y values are not calibrated until the controller 
      // is used, so initially getXAxisValue() and getYAxisValue() may not return 0
      // when the arrow keys are released, but instead some very small decimal.
      if (Math.abs(c.getXAxisValue()) < 0.05) {
        controllerState.put(SNESController.Button.Left,
            SNESController.ButtonState.Up);
        controllerState.put(SNESController.Button.Right,
            SNESController.ButtonState.Up);
      }

      if (c.getXAxisValue() == 1.0) {
        controllerState.put(SNESController.Button.Left,
            SNESController.ButtonState.Up);
        controllerState.put(SNESController.Button.Right,
            SNESController.ButtonState.Down);
      }

      if (c.getXAxisValue() == -1.0) {
        controllerState.put(SNESController.Button.Left,
            SNESController.ButtonState.Down);
        controllerState.put(SNESController.Button.Right,
            SNESController.ButtonState.Up);
      }

      if (Math.abs(c.getYAxisValue()) < 0.05) {
        controllerState.put(SNESController.Button.Up,
            SNESController.ButtonState.Up);
        controllerState.put(SNESController.Button.Down,
            SNESController.ButtonState.Up);
      }

      if (c.getYAxisValue() == 1.0) {
        controllerState.put(SNESController.Button.Up,
            SNESController.ButtonState.Up);
        controllerState.put(SNESController.Button.Down,
            SNESController.ButtonState.Down);
      }

      if (c.getYAxisValue() == -1.0) {
        controllerState.put(SNESController.Button.Up,
            SNESController.ButtonState.Down);
        controllerState.put(SNESController.Button.Down,
            SNESController.ButtonState.Up);
      }
    }

    public SNESController.ButtonState getButtonState(Controller c, int buttonNum) {
      if (c.isButtonPressed(buttonNum)) {
        return SNESController.ButtonState.Down;
      } else {
        return SNESController.ButtonState.Up;
      }
    }

    public boolean equals(ControllerState other) {
      for (SNESController.Button button : SNESController.Button.values()) {
        if (controllerState.get(button) != other.controllerState.get(button))
          return false;
      }
      return true;
    }
  }

  /*********************************************************************************
   * run
   * 
   * This method compares the previous ControllerState with the current state
   * and determines whether or not any changes have occurred. If there have been
   * any changes (buttons have been pressed or released), the method calls
   * dispatchKeyEvent.
   *********************************************************************************/
  public void run() {
    ControllerState prev, curr;

    curr = new ControllerState();
    while (true) {
      if (SLEEP_TIME > 0) {
        try {
          Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
          System.err.println("Error: Could not sleep the controller thread.");
          e.printStackTrace();
        }
      }

      prev = curr;
      userController.poll();
      curr = new ControllerState(userController);

      if (prev.equals(curr)) continue;

      for (Button b : Button.values()) {
        if (prev.controllerState.get(b) != curr.controllerState.get(b)) {
          dispatchKeyEvent(b, curr.controllerState.get(b));
        }
      }
    }
  }

  /*********************************************************************************
   * dispatchKeyEvent
   * 
   * This method is responsible for receiving controller input. One important
   * fact about this method is that it is only supposed to RECEIVE and log user
   * input, NOT act on it. This is because we need to carefully synchronize the
   * tasks of rendering the scene and processing user input to achieve a smooth
   * animation.
   *********************************************************************************/
  public void dispatchKeyEvent(Button eventButton, ButtonState currentState) {

    /*
     * If the buttonState is down, that means that someone just pressed the
     * button and this action needs to be added.
     */
    if (currentState == ButtonState.Down) {
      switch (eventButton) {
      case Left:
        startRepeatingAction(Action.Left);
        break;
      case Right:
        startRepeatingAction(Action.Right);
        break;
      case Down:
        startRepeatingAction(Action.Back);
        break;
      case Up:
        startRepeatingAction(Action.Forward);
        break;
      case A:
        startRepeatingAction(Action.A_Button);
        break;
      case B:
        startRepeatingAction(Action.B_Button);
        break;
      case Start:
        startRepeatingAction(Action.start);
        break;
      }
    }
    /*
     * If the current ButtonSate is up, this means that someone just released
     * the button and that this action needs to be removed.
     */
    if (currentState == ButtonState.Up) {
      switch (eventButton) {
      case Left:
        stopRepeatingAction(Action.Left);
        break;
      case Right:
        stopRepeatingAction(Action.Right);
        break;
      case Down:
        stopRepeatingAction(Action.Back);
        break;
      case Up:
        stopRepeatingAction(Action.Forward);
        break;
      case A:
        stopRepeatingAction(Action.A_Button);
        break;
      case B:
        stopRepeatingAction(Action.B_Button);
        break;
      case Start:
        stopRepeatingAction(Action.start);
        break;
      }
    }
  }
//  
//  
//  /*********************************************************************************
//   * createAction
//   *  
//   * This method provides an alternative to userController's 
//   * start/stopRepeatingActions methods for generating actions from buttons on 
//   * the controller which need to receive discrete inputs for the purposes of 
//   * calling menus from within the simulation. For example the start button needs
//   * to generate only one action when pressed in while running DevelopmentUI (in
//   * order to call up the pause menu).
//   * 
//   **********************************************************************************/
//  
//  protected synchronized void createAction(Action action){
//    actionQueue.add( action );
//  }

}
