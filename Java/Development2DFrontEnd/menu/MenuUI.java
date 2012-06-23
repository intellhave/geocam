package menu;

import java.io.File;

import javax.swing.JFrame;

import controllerMKII.SNESController;
import controllerMKII.UserController;
import controllerMKII.UserController.Action;

public class MenuUI {

  // TODO: Build all menus the user will actually see, and integrate this with the simulation UI

  private static JFrame menuViewer;
  private static UserController controller;

  public static void main(String[] args) {
    controller = new SNESController();
    initMenuView();
  }

  /*********************************************************************************
   * initMenuView
   * 
   * Initializes the JFrame that will display the menus. Also starts the thread
   * that will monitor and read input from the controller.
   *********************************************************************************/
  private static void initMenuView() {
    menuViewer = new JFrame("TestViewer");
    menuViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // start the controller thread
    Thread t = new Thread(controller);
    t.start();

    // call startMenu method to monitor controller
    startMenu();
  }

  /*********************************************************************************
   * startMenu, gameMenu, optionsMenu
   * 
   * Each menu is implemented as a static method. It has a MenuState which keeps
   * track of which option is highlighted on the screen. Different states are
   * displayed to the user as separate images created by the MenuImage class.
   * For example, startMenu has two states with two different images: one where
   * the "start" button is highlighted and one where the "options" button is
   * highlighted. Inside startMenu a while loop is continuously running checking
   * for user input. When the user "selects" one of the options, the while loop
   * terminates and calls the method for the menu the user selected.
   * 
   *********************************************************************************/
  private static enum StartMenuState {
    start, select
  };

  private static void startMenu() {
    // compose the background and menu images
    File backgroundImage = new File("Development2DFrontEnd/data/TestBackground.png");
    File textImage = new File("Development2DFrontEnd/data/TestScreen_start.png");
    MenuImage.compose(backgroundImage, textImage);

    MenuImage image = new MenuImage();
    menuViewer.add(image);
    menuViewer.pack();
    menuViewer.setVisible(true);

    // set the initial state
    StartMenuState currentState = StartMenuState.start;

    while (true) {
      Action act = controller.getNextAction();
      if (act == null)
        continue;
      if (act.equals(Action.Forward) || act.equals(Action.Back)) {
        File background = new File("Development2DFrontEnd/data/TestBackground.png");
        File text;
        if (currentState.equals(StartMenuState.start)) {
          text = new File("Development2DFrontEnd/data/TestScreen_select.png");
          currentState = StartMenuState.select;
          //System.out.println("Button pressed!");
        } else {
          text = new File("Development2DFrontEnd/data/TestScreen_start.png");
          currentState = StartMenuState.start;
          //System.out.println("Button pressed!");
        }

        // update the image
        MenuImage.compose(background, text);
        image = new MenuImage();
        menuViewer.repaint();
      }
      if (act.equals(Action.A_Button)) {
        //System.out.println("A button pressed!");
        break;
      }
    }
    if (currentState.equals(StartMenuState.start))
      gameMenu();
    else
      optionsMenu();
  }

  private static void gameMenu() {
    File backgroundImage = new File("Development2DFrontEnd/data/TestBackground2.png");
    File textImage = new File("Development2DFrontEnd/data/GameMenu.png");
    MenuImage.compose(backgroundImage, textImage);

    MenuImage image = new MenuImage();
    menuViewer.add(image);
    menuViewer.pack();
    menuViewer.setVisible(true);

    while (true) {
      Action act = controller.getNextAction();
      if (act == null)
        continue;
      if (act.equals(Action.B_Button)) {
        //System.out.println("B button pressed!");
        break;
      }
    }
    startMenu();
  }

  private static void optionsMenu() {
    File backgroundImage = new File("Development2DFrontEnd/data/TestBackground2.png");
    File textImage = new File("Development2DFrontEnd/data/OptionMenu.png");
    MenuImage.compose(backgroundImage, textImage);

    MenuImage image = new MenuImage();
    menuViewer.add(image);
    menuViewer.pack();
    menuViewer.setVisible(true);

    while (true) {
      Action act = controller.getNextAction();
      if (act == null)
        continue;
      if (act.equals(Action.B_Button)) {
        //System.out.println("B button pressed!");
        break;
      }
    }
    startMenu();
  }
}
