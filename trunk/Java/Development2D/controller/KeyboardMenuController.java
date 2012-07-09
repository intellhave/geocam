package controller;

public class KeyboardMenuController extends KeyboardController {

  public KeyboardMenuController() {
    super(null);
  }

  protected synchronized void startRepeatingAction(Action action) {
  }

  protected synchronized void stopRepeatingAction(Action action) {
    actionQueue.add(action);
  }
}
