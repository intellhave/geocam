package deprecated;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import controller.KeyboardMenuController;
import controller.UserController;
import controller.UserController.Action;
import frontend.SimulationManager;

/*********************************************************************************
 * MenuUI
 * 
 * This source code is responsible for implementing the system of menus that
 * enables a user (a museum visitor) to configure the development program (and
 * similar programs), and then launch the simulation.
 * 
 * Each menu is built from a collection of images. First, we have a background
 * image which is combined with a foreground image to create the illusion of an
 * interactive menu. Generally, we have one foreground image for each button
 * that can be active. For each menu we have a static method which explains how
 * the images should be changed according to user input.
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
   *********************************************************************************/

  private static BufferedImage start_games;
  private static BufferedImage start_explorer;
  private static BufferedImage start_about;
  private static BufferedImage game_tag;
  private static BufferedImage game_cookie;
  private static BufferedImage explorer_start;
//  private static BufferedImage explorer_options;
//  private static BufferedImage explorerPause_resume;
//  private static BufferedImage explorerPause_options;
//  private static BufferedImage explorerPause_exit;
  private static BufferedImage cookiePause_resume;
  private static BufferedImage cookiePause_options;
  private static BufferedImage cookiePause_exit;
  private static BufferedImage cookieWin_again;
  private static BufferedImage cookieWin_exit;

  private static BufferedImage about;
  private static BufferedImage tag;
//  private static BufferedImage options;

  private static BufferedImage currentMenu;

  /*********************************************************************************
   * This main method starts our whole application, including the "explorer"
   * version of our simulation and the "cookie game" version of the simulation.
   *********************************************************************************/

  public static void main(String[] args) throws IOException {
    new MenuUI();
  }

  /*********************************************************************************
   * 
   *********************************************************************************/

  public MenuUI() {
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
    File pausedMenuBackground = new File(bg_path + "paused_background_temp.png");

    File startMenu_games = new File(menu_path + "StartMenu_games.png");
    File startMenu_explorer = new File(menu_path + "StartMenu_explorer.png");
    File startMenu_about = new File(menu_path + "StartMenu_about.png");
    File gameMenu_tag = new File(menu_path + "GameSelection_tag.png");
    File gameMenu_cookie = new File(menu_path + "GameSelection_cookie.png");
    File explorerMenu_start = new File(menu_path + "SurfaceExplorer_start.png");
//    File explorerMenu_options = new File(menu_path
//        + "SurfaceExplorer_options.png");
//    File explorerPausedResume = new File(menu_path + "ExplorerPause_resume.png");
//    File explorerPausedOptions = new File(menu_path
//        + "ExplorerPause_options.png");
//    File explorerPausedExit = new File(menu_path + "ExplorerPause_exit.png");
    File cookiePauseResume = new File(menu_path + "cookiepause_resume.png");
    File cookiePauseOptions = new File(menu_path + "cookiepause_options.png");
    File cookiePauseExit = new File(menu_path + "cookiepause_exit.png");
    File cookieWinAgain = new File(menu_path + "CookieWin_playAgain.png");
    File cookieWinExit = new File(menu_path + "CookieWin_exit.png");

    File aboutPage = new File(menu_path + "AboutPage.png");
    File tagGame = new File(menu_path + "TagGame.png");
//    File explorerOptions = new File(menu_path + "Options.png");

    start_games = compose(startMenuBackground, startMenu_games);
    start_explorer = compose(startMenuBackground, startMenu_explorer);
    start_about = compose(startMenuBackground, startMenu_about);
    game_tag = compose(gameMenuBackground, gameMenu_tag);
    game_cookie = compose(gameMenuBackground, gameMenu_cookie);
    explorer_start = compose(explorerMenuBackground, explorerMenu_start);
//    explorer_options = compose(explorerMenuBackground, explorerMenu_options);
//    explorerPause_resume = compose(pausedMenuBackground, explorerPausedResume);
//    explorerPause_options = compose(pausedMenuBackground, explorerPausedOptions);
//    explorerPause_exit = compose(pausedMenuBackground, explorerPausedExit);
    cookiePause_resume = compose(pausedMenuBackground, cookiePauseResume);
    cookiePause_options = compose(pausedMenuBackground, cookiePauseOptions);
    cookiePause_exit = compose(pausedMenuBackground, cookiePauseExit);
    cookieWin_again = compose(pausedMenuBackground, cookieWinAgain);
    cookieWin_exit = compose(cookieWinExit, cookieWinExit);

    about = compose(aboutPage, aboutPage);
    tag = compose(tagGame, tagGame);
//    options = compose(explorerOptions, explorerOptions);
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
    this.setLocation(0, 10);
    // Makes the frame transparent for pause menus
    this.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.25f));
    this.setResizable(false);
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
   *********************************************************************************/
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
    controller.clear();
    while (!exit) {
      if (CookieGame.gameWon) {
        cookieWinMenu();
      }
      if (CookieGame.paused && !CookieGame.gameWon) {
        cookiePauseMenu();
      }
    }
  }

  private void cookiePauseMenu() {
    BufferedImage[] menuImages = { cookiePause_resume, cookiePause_exit,
        cookiePause_options };
    int state = 0;
    currentMenu = cookiePause_resume;

    this.setSize(700, 723);
    this.repaint();
    this.setVisible(true);

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;

      switch (a) {
      case Back:
        state = (state + 1) % 3;
        break;
      case Forward:
        state = (state + 2) % 3;
        break;
      }
      if (a == Action.A_Button) {
        if (state == 0) {
          this.setVisible(false);
          CookieGame.runGame();
          controller.clear();
          break;
        }
        if (state == 1) {
          CookieGame.quitCookie();
          exit = true;
          this.setSize(700, 700);
          break;
        }
        if (state == 2) {
          // TODO: implement options for cookie game
        }
      }

      currentMenu = menuImages[state];
      this.repaint();
    }
  }

  private void cookieWinMenu() {
    BufferedImage[] menuImages = { cookieWin_again, cookieWin_exit };
    int state = 0;
    currentMenu = cookieWin_again;

    this.setSize(700, 723);
    this.repaint();
    this.setVisible(true);
    controller.clear();

    Action a = null;

    while (true) {
      a = controller.getNextAction();
      if (a == null)
        continue;

      switch (a) {
      case Back:
      case Forward:
        state = (state + 1) % 2;
        break;
      }
      if (a == Action.A_Button) {
        if (state == 0) {
          this.setVisible(false);
          CookieGame.quitCookie();
          CookieGame.runCookie();
          controller.clear();
          this.setVisible(true);
          break;
        }
        if (state == 1) {
          exit = true;
          CookieGame.quitCookie();
          this.setSize(700, 700);
          break;
        }
      }
      currentMenu = menuImages[state];
      this.repaint();
    }
  }

  private void explorerMenu() {
    currentMenu = explorer_start;
    this.repaint();

    Action act;
    while (true) {
      act = controller.getNextAction();

      if (act == null)
        continue;

      if (act == Action.A_Button) {
        this.setVisible(false);
        String defaultPath = "Data/surfaces/tetra2.off";

        SimulationManager dui = new SimulationManager(defaultPath);
        SimulationSettingsFrame vc = new SimulationSettingsFrame(dui);

        while (true) {
          dui.run(); // We only return from this call once ViewerController has
                     // signaled that we should quit the simulation.
          
          if( vc.sessionEnded() ) break;
          
          // Now we know that we need to display a new surface.
          dui = new SimulationManager(vc.getPath());
          // Now we have a fresh, initialized DevelopmentUI instance. We're
          // ready to
          // allow user input from vc again.
          vc.setSimulation(dui);
          vc.setEnabled(true);
        }
        this.setVisible(true);
        controller.clear();
      }

      if (act == Action.B_Button)
        break;

      this.repaint();
    }
  }

// These methods have been removed for now. Pausing of Development Explorer will
// be handled by the DevelopmentUI/ViewerController code.
//
//  private void pauseExplorerMenu() {
//    BufferedImage[] menuImages = { explorerPause_resume, explorerPause_options,
//        explorerPause_exit };
//    int state = 0;
//    currentMenu = explorerPause_resume;
//
//    // Dimensions should be 800 by 400, but this appears too short
//    // Need to size up height by approx. 20?
//    this.setSize(800, 423);
//    this.repaint();
//    this.setVisible(true);
//
//    Action a = null;
//
//    while (true) {
//      a = controller.getNextAction();
//      if (a == null)
//        continue;
//
//      switch (a) {
//      case Back:
//        state = (state + 1) % 3;
//        break;
//      case Forward:
//        state = (state + 2) % 3;
//        break;
//      case A_Button:
//        if (state == 0) {
//          this.setVisible(false);
//          dui.run();
//          controller.clear();
//          this.setVisible(true);
//          break;
//        }
//        if (state == 1) {
//          // TODO: implement options menu
//        }
//      }
//
//      if (a == Action.A_Button && state == 2) {
//        dui.terminate();
//        this.setSize(700, 700);
//        break;
//      }
//
//      currentMenu = menuImages[state];
//      this.repaint();
//    }
//  }
//
//  // TODO: implement options menu
//  private void explorerOptions() {
//    currentMenu = options;
//    this.repaint();
//
//    Action a = null;
//
//    while (true) {
//      a = controller.getNextAction();
//      if (a == null)
//        continue;
//      if (a == Action.B_Button)
//        break;
//    }
//  }

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
   *********************************************************************************/
  public void paint(Graphics g) {
    /*
     * Previously, there was a line here which read: g.clearRect(0, 0,
     * this.getWidth(), this.getHeight()); This caused menu to flicker.
     * Basically, this is because we are writing directly to the screen. Since
     * we're writing whole images to the screen (and our backgrounds aren't
     * transparent), there's no need to clear the screen before we use it again.
     */
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
