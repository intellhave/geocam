package frontend;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import viewMKII.CookieGame;
import viewMKII.DevelopmentUI;
import controllerMKII.KeyboardMenuController;
import controllerMKII.SNESMenuController;
import controllerMKII.UserController;
import controllerMKII.UserController.Action;

/*********************************************************************************
 * MenuUI
 * 
 * This source code is responsible for implementing the system of menus that
 * enables a user (a museum visitor) to configure the development program (and
 * similar programs), and then launch the simulation.
 * 
 * Each menu is built from a collection of images. First, we have a background
 * image which is composited with a foreground image to create the illusion of
 * an interactive menu. Generally, we have one foreground image for each button
 * that can be active. For each menu we have a static method which explains how
 * the images should be changed according to user input.
 * 
 *********************************************************************************/

public class MenuUI extends JFrame {
  private static final long serialVersionUID = 1L;

  // TODO: Build all menus the user will actually see, and integrate this with
  // the simulation UI

  private UserController controller;
  private Boolean exit;

  /*********************************************************************************
   * Image data
   * 
   * These are the composite BufferedImages that will be displayed to the user.
   * To improve the speed of the program (i.e. to avoid repeatedly reading from
   * the disk) these images are read in from image files only once as the
   * program initializes.
   * 
   *********************************************************************************/

  private static BufferedImage start_games;
  private static BufferedImage start_explorer;
  private static BufferedImage start_about;
  private static BufferedImage game_tag;
  private static BufferedImage game_cookie;
  private static BufferedImage explorer_start;
  private static BufferedImage explorer_options;

  private static BufferedImage about;
  private static BufferedImage tag;
  // private static BufferedImage cookie;
  private static BufferedImage options;
  private static BufferedImage paused;
  private static BufferedImage cookiePaused;
  private static BufferedImage cookieWin;

  private static BufferedImage currentMenu;

  /*********************************************************************************
   * 
   *********************************************************************************/

  public static void main(String[] args) throws IOException {
    new MenuUI();
  }

  /*********************************************************************************
   * 
   *********************************************************************************/

  public MenuUI() {
    // controller = new SNESMenuController();
    controller = new KeyboardMenuController();
    try {
      initFiles();
    } catch (IOException ioe) {
      System.err.println("Error: Could not locate menu images.");
      ioe.printStackTrace();
    }

    initMenuView();
  }

  /*********************************************************************************
   * initFiles
   * 
   *********************************************************************************/

  private void initFiles() throws IOException {
    final String bg_path = "Data/menu/backgrounds/";
    final String menu_path = "Data/menu/menus/";

    File startMenuBackground = new File(bg_path + "dodecBackground.png");
    File gameMenuBackground = new File(bg_path + "barbellBackground2.png");
    File explorerMenuBackground = new File(bg_path + "dodecFirstPerson.png");

    File startMenu_games = new File(menu_path + "StartMenu_games.png");
    File startMenu_explorer = new File(menu_path + "StartMenu_explorer.png");
    File startMenu_about = new File(menu_path + "StartMenu_about.png");
    File gameMenu_tag = new File(menu_path + "GameSelection_tag.png");
    File gameMenu_cookie = new File(menu_path + "GameSelection_cookie.png");
    File explorerMenu_start = new File(menu_path + "SurfaceExplorer_start.png");
    File explorerMenu_options = new File(menu_path
        + "SurfaceExplorer_options.png");

    File aboutPage = new File(menu_path + "AboutPage.png");
    File tagGame = new File(menu_path + "TagGame.png");
    //File cookieGame = new File(menu_path + "CookieGame.png");
    File explorerOptions = new File(menu_path + "Options.png");
    File pause_screen = new File(menu_path + "Paused.png");
    File pause_cookie = new File(menu_path + "CookiePaused.png");
    File win = new File(menu_path + "CookieWin.png");

    start_games = compose(startMenuBackground, startMenu_games);
    start_explorer = compose(startMenuBackground, startMenu_explorer);
    start_about = compose(startMenuBackground, startMenu_about);
    game_tag = compose(gameMenuBackground, gameMenu_tag);
    game_cookie = compose(gameMenuBackground, gameMenu_cookie);
    explorer_start = compose(explorerMenuBackground, explorerMenu_start);
    explorer_options = compose(explorerMenuBackground, explorerMenu_options);

    about = compose(aboutPage, aboutPage);
    tag = compose(tagGame, tagGame);
    // cookie = compose(cookieGame, cookieGame);
    options = compose(explorerOptions, explorerOptions);
    paused = compose(pause_screen, pause_screen);
    cookiePaused = compose(pause_cookie, pause_cookie);
    cookieWin = compose(win, win);
  }

  /*********************************************************************************
   * initMenuView
   * 
   * Initializes the JFrame that will display the menus. Also starts the thread
   * that will monitor and read input from the controller.
   *********************************************************************************/
  private void initMenuView() {
    currentMenu = start_games;

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(currentMenu.getWidth(), currentMenu.getHeight());
    this.setVisible(true);

    // start the controller thread
    Thread t = new Thread(controller);
    t.start();

    // call startMenu method to display the start menu
    startMenu();
  }

  /*********************************************************************************
   * startMenu, gameMenu, explorerMenu, etc
   * 
   * Each menu is implemented as a static method. It has a menu state integer
   * which keeps track of which option is highlighted on the screen, and it
   * displays different states to the user as different BufferedImages (creating
   * the illusion of interactive menus).
   * 
   * For example, explorerMenu has two states with two different images: one
   * where the "start" button is highlighted and one where the "options" button
   * is highlighted. Inside explorerMenu a while loop is continuously running
   * checking for user input. When the user "selects" one of the options,
   * explorerMenu either calls the optionsMenu method or starts the explorer
   * simulation.
   * 
   * The menus are implemented as a tree. For example suppose the user is
   * viewing the explorerMenu. If they then wish to go back to the previous
   * menu, explorerMenu breaks from its while loop and the previous menu's while
   * loop resumes where it left off.
   * 
   *********************************************************************************/

  // TODO: Is this enum necessary for anything?

  // private static enum MenuState {
  // START_GAMES, START_EXPLORER, START_ABOUT, GAME_TAG, GAME_COOKIE,
  // EXPLORER_START, EXPLORER_OPTIONS
  // };

  private void startMenu() {
    BufferedImage[] menuImages = { start_games, start_explorer, start_about };
    currentMenu = start_games;
    int state = 0;

    this.repaint();

    controller.clear();
    Action act = null;

    while (true) {
      act = controller.getNextAction();
      if (act == null)
        continue;

      switch (act) {
      case Back:
        state = (state + 1) % 3;
        break;
      case Forward:
        // 2 is congruent to -1 mod 3.
        // (Java only likes positive integers for % calculations.)
        state = (state + 2) % 3;
        break;
      case A_Button:
        if (state == 0)
          gameMenu();
        else if (state == 1)
          explorerMenu();
        else
          aboutPage();
      }
      currentMenu = menuImages[state];
      this.repaint();
    }
  }

  private void gameMenu() {
    BufferedImage[] menuImages = { game_tag, game_cookie };
    currentMenu = game_tag;
    int state = 0;

    this.repaint();

    Action act = null;

    while (true) {
      act = controller.getNextAction();
      if (act == null)
        continue;

      switch (act) {
      case Back:
      case Forward:
        state = (state + 1) % 2;
        break;
      case A_Button:
        if (state == 0)
          tagGame();
        else
          runCookie();
        break;
      }
      if (act == Action.B_Button)
        break;

      currentMenu = menuImages[state];
      this.repaint();
    }
  }

  /*********************************************************************************
   * runCookie
   * 
   * This method monitors the state of the cookie game until the player chooses
   * to exit. It switches control between the win menu and the pause menu as
   * necessary, then returns control to gameMenu when done.
   *********************************************************************************/
  private void runCookie() {
    exit = false;
    this.setVisible(false);
    CookieGame.runCookie();
    while (!exit) {
      if (CookieGame.gameWon) {
        CookieGame.quitCookie();
        cookieWinMenu();
      }
      if (CookieGame.paused) {
        cookiePauseMenu();
      }
    }
  }

  private void cookiePauseMenu() {
    currentMenu = cookiePaused;
    this.repaint();
    this.setVisible(true);

    Action a = null;
    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;
      if (a == Action.B_Button) {
        this.setVisible(false);
        CookieGame.runGame();
        break;
      }
      if (a == Action.A_Button) {
        CookieGame.quitCookie();
        exit = true;
        break;
      }
    }
  }

  private void cookieWinMenu() {
    currentMenu = cookieWin;
    this.repaint();
    this.setVisible(true);

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;

      if (a == Action.B_Button) {
        this.setVisible(false);
        CookieGame.runCookie();
        break;
      }
      if (a == Action.A_Button) {
        exit = true;
        break;
      }
    }
  }

  private void explorerMenu() {
    BufferedImage[] menuImages = { explorer_start, explorer_options };
    currentMenu = explorer_start;
    int state = 0;

    this.repaint();

    Action act = null;

    while (true) {
      act = controller.getNextAction();
      if (act == null)
        continue;

      switch (act) {
      case Back:
      case Forward:
        state = (state + 1) % 2;
        break;
      case A_Button:
        if (state == 0) {
          this.setVisible(false);
          // Menu windows are now hidden, we can pass control to the Explorer
          // simulation.
          DevelopmentUI.runExplorer();
          // Now we have returned from the runExplorer method call.
          // This means that the Explorer simulation is currently paused.
          // Consequently, we need to bring the menu windows back and restore
          // the menu controller.

          // this.setVisible(true);

          // resumeController();
          pauseExplorerMenu();
        } else
          explorerOptions();
      }
      if (act == Action.B_Button)
        break;

      currentMenu = menuImages[state];
      this.repaint();
    }
  }

  private void pauseExplorerMenu() {
    currentMenu = paused;
    this.repaint();
    this.setVisible(true);

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;

      if (a == Action.B_Button) {
        this.setVisible(false);
        DevelopmentUI.runSimulation();
        this.setVisible(true);
      }
      if (a == Action.A_Button) {
        DevelopmentUI.quitExplorer();
        break;
      }
    }
  }

  // TODO: implement options menu
  private void explorerOptions() {
    currentMenu = options;
    this.repaint();

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;
      if (a == Action.B_Button)
        break;
    }
  }

  // TODO: Implement Tag game
  private void tagGame() {
    currentMenu = tag;
    this.repaint();

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;
      if (a == Action.B_Button)
        break;
    }
  }

  // TODO: Implement About page
  private void aboutPage() {
    currentMenu = about;
    this.repaint();

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;
      if (a == Action.B_Button)
        break;
    }
  }

  /*********************************************************************************
   * paint
   * 
   *********************************************************************************/

  public void paint(Graphics g) {
    g.drawImage(currentMenu, 0, 0, null);
  }

  /*********************************************************************************
   * compose
   * 
   * This method takes as input two files which contain images: a background
   * image (such as a screenshot from DevelopmentUI) and a foreground menu image
   * (which has a transparent background and option buttons). It composes these
   * two images and saves the result to a BufferedImage, which it returns.
   * 
   * If the background image is too big, this method crops an area out of the
   * center of the image to use in the final composited image. If the background
   * image is too small, it centers it. The foreground image (or textLayer) is
   * used to determine the size of the BufferedImage that will be returned.
   *********************************************************************************/
  private BufferedImage compose(File backgroundLayer, File textLayer) {
    // load source images
    BufferedImage background = null;
    try {
      background = ImageIO.read(backgroundLayer);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    BufferedImage text = null;
    try {
      text = ImageIO.read(textLayer);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    // may need to crop buffered image of background before composing images
    if (background.getWidth() > text.getWidth()) {
      int x = (background.getWidth() - text.getWidth()) / 2;
      background = background.getSubimage(x, 0, text.getWidth(),
          background.getHeight());
    }
    if (background.getHeight() > text.getHeight()) {
      int y = (background.getHeight() - text.getHeight()) / 2;
      background = background.getSubimage(0, y, background.getWidth(),
          text.getHeight());
    }

    // create a new BufferedImage and paint the two images onto it
    // if background is too small, center it behind the menu image
    int w = text.getWidth();
    int h = text.getHeight();
    int x, y;
    if (background.getWidth() < text.getWidth())
      x = (text.getWidth() - background.getWidth()) / 2;
    else
      x = 0;
    if (background.getHeight() < text.getHeight())
      y = (text.getHeight() - background.getHeight()) / 2;
    else
      y = 0;

    BufferedImage composition = new BufferedImage(w, h,
        BufferedImage.TYPE_INT_ARGB);
    Graphics g = composition.getGraphics();
    g.drawImage(background, x, y, null);
    g.drawImage(text, 0, 0, null);

    return composition;
  }
}
