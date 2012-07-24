package frontend;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerAppearance.ModelType;
import marker.MarkerHandler;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ExponentialView;
import view.FaceAppearanceScheme;
import view.View;
import controller.KeyboardController;
import controller.UserController;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

public class CookieGame {

  private static boolean developerMode = false;

  /*********************************************************************************
   * Model Data
   * 
   * These variables are responsible for keeping track of the states of the
   * mathematical objects in the simulation.
   *********************************************************************************/
  public static boolean gameWon;
  public static boolean paused = false;
  private static Marker source;
  private static Marker cookie;

  private static MarkerHandler markerHandler;
  private static Development development;
  
  /*********************************************************************************
   * View Data
   * 
   * These variables are responsible for keeping track of the viewers that will
   * present the mathematical objects to the user.
   *********************************************************************************/
  private static JFrame viewer;
  private static FaceAppearanceScheme faceAppearanceScheme;
  private static View gameView;

  /*********************************************************************************
   * Model Control Data
   * 
   * These variables hold the listeners and other objects responsible for
   * keeping track of user input to the program that effects the model.
   * 
   * mouseControl: Translates mouse input into modifications of the view and
   * model.
   * 
   * keyboardControl: Translates keyboard input into modifications of the view
   * and model.
   * 
   * markerControl: Translates AI movements of markers into modifications of the
   * markers.
   * 
   * networkControl: Receives network input from other instances of
   * "DevelopmentUI", and modifies the model accordingly.
   *********************************************************************************/
  private static UserController userControl;
  
  private static final int recursionDepth = 2;
  
  public static void main(String[] args) {
    developerMode = true;
    initModel();
    initView();
    initModelControls();
    runGame();
  }

  private static String[] filenames = { "square2.off", // 0
      "tetra.off",// 1
      "tetra2.off",// 2
      "neckpinch.off",// 3
      "torus.off",// 4
      "cube_surf.off",// 5
      "icosa.off",// 6
      "dodec.off",// 7
      "cone.off",// 8
      "epcot.off",// 9
      "square2.off" }; // 10
  private static String path = "Data/surfaces/";
  private static String filename = path + filenames[7];

  /*********************************************************************************
   * initModel@math
   * 
   * This method is responsible for initializing the mathematical model of the
   * simulation we'll present to the user. Specifically, this means setting up
   * the triangulated surface and the markers that will be placed on it.
   *********************************************************************************/
  private static void initModel() {
    loadSurface(filename);
  }

  /*********************************************************************************
   * loadSurface
   * 
   * This method uses the input file name (which should include the path to the
   * file), and reads that file to determine the surface that will be displayed.
   *********************************************************************************/
  private static void loadSurface(String file) {
    String extension = file.substring(file.length() - 3, file.length());

    if (extension.contentEquals("off")) {
      EmbeddedTriangulation.readEmbeddedSurface(file);
    } else if (extension.contentEquals("xml")) {
      TriangulationIO.readTriangulation(file);
    } else {
      System.err.println("invalid file");
    }

    initSurface();
    initMarkers();
  }

  /*********************************************************************************
   * initSurface
   * 
   * After "loadSurface" has read the data specifying a triangulated surface
   * into the program, this method is responsible for placing that data into the
   * data structure that specifies the surface for the rest of the program.
   *********************************************************************************/
  private static void initSurface() {
    Iterator<Integer> i = null;
    // pick some arbitrary face and source point
    i = Triangulation.faceTable.keySet().iterator();
    Face sourceFace = Triangulation.faceTable.get(i.next());
    Vector sourcePoint = new Vector(0, 0);
    Iterator<Vertex> iv = sourceFace.getLocalVertices().iterator();
    while (iv.hasNext()) {
      sourcePoint.add(Coord2D.coordAt(iv.next(), sourceFace));
    }
    sourcePoint.scale(1.0f / 3.0f);

    if (development == null) {
      development = new Development(new ManifoldPosition(sourceFace, sourcePoint), recursionDepth, 1.0);
    } else {
      development.rebuild(new ManifoldPosition(sourceFace, sourcePoint), recursionDepth);
    }
  }

  /*********************************************************************************
   * initMarkers
   * 
   * This method initializes the markers (ants, rockets, cookies, etc.) that
   * will appear on the surface for games and exploration.
   *********************************************************************************/
  private static void initMarkers() {
    
    markerHandler = new MarkerHandler();    
    ManifoldPosition pos;
    MarkerAppearance app;
    
    /* Initialize the source marker. */
    pos = development.getSource();
    app = new MarkerAppearance(ModelType.ANT, 1.0);
    source = new Marker(pos, app, Marker.MarkerType.SOURCE);
    markerHandler.addSourceMarker( source );
    
    /* Initialize the cookie marker. */
    pos = new ManifoldPosition(development.getSource());
    app = new MarkerAppearance(ModelType.COOKIE, 1.0);

    Random rand = new Random();
    double a = rand.nextDouble() * Math.PI * 2;
    Vector direction = new Vector(Math.cos(a), Math.sin(a));
    pos.move(Vector.scale(direction, 4));
    cookie = new Marker(pos, app, Marker.MarkerType.FIXED);
    markerHandler.addMarker(cookie);
  }

  /*********************************************************************************
   * initView
   * 
   * This method is responsible for initializing the code that creates a
   * visualization of the triangulated surface for the user. This method is also
   * responsible for positioning the views on the screen.
   * 
   * Because we're interested in displaying this in a museum setting (where
   * users shouldn't be allowed to change window sizes), it's OK to hard code
   * this information.
   *********************************************************************************/
  private static void initView() {
    faceAppearanceScheme = new FaceAppearanceScheme();
    int[] framePosition = { 0, 10 };
    int[] frameSize = { 700, 700 };
    gameView = new ExponentialView(development, markerHandler, faceAppearanceScheme, null, null);
    gameView.updateGeometry();
    gameView.initializeNewManifold();
    gameView.updateScene();

    viewer = new JFrame();
    viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    viewer.setVisible(true);
    viewer.setLocation(framePosition[0], framePosition[1]);
    viewer.setResizable(true);

    Dimension size = new Dimension(frameSize[0], frameSize[1]);
    Container contentPane = viewer.getContentPane();
    contentPane.add((Component) gameView.getViewer().getViewingComponent());
    contentPane.setMinimumSize(size);
    contentPane.setPreferredSize(size);
    contentPane.setMaximumSize(size);
    viewer.pack();
    viewer.validate();
    viewer.setVisible(true);
  }

  /*********************************************************************************
   * initModelControls
   * 
   * This method is responsible for initializing the objects that will listen
   * for the user's input and modify the triangulated surface accordingly.
   * 
   * Notice: These controls are the ones that modify the model, NOT the ones
   * that modify how the model is visualized. Those controls belong in the
   * "initViewControls" method.
   *********************************************************************************/
  private static void initModelControls() {
    userControl = new KeyboardController(development, null, null);
  }

  public static void runGame() {
    final long dt = 10; // Timestep size, in microseconds
    // final long maxFrameTime = 80;

    gameWon = false;

    long startTime = System.currentTimeMillis();
    long currentTime = startTime;
    long accumulator = 0;
    Thread t = new Thread(userControl);
    t.start();
    
    Marker source = markerHandler.getSourceMarker();
    userControl.clear();
    userControl.resetPausedFlag();
    paused = false;
    while (!paused && !gameWon) {
      long newTime = System.currentTimeMillis();
      long frameTime = newTime - currentTime;

      currentTime = newTime;
      accumulator += frameTime;

      while (accumulator >= dt) {
        /* Update the source marker: */
        Face prev = development.getSource().getFace();
        userControl.runNextAction();
        Face next = development.getSource().getFace();
        
        source.setPosition(development.getSource());        
        if (next != prev) {
          markerHandler.updateMarker(source, prev);
        }
        
        /* Update the other markers: */
        markerHandler.updateMarkers(dt);

        /* Check the end of game condition: */
        double epsilon = .25;
        if (source.getPosition().getFace() == cookie.getPosition().getFace()
            && (Vector.distanceSquared(source.getPosition().getPosition(),
                cookie.getPosition().getPosition()) < epsilon))
          gameWon = true;
        
        /* Update accumulator */
        accumulator -= dt;
      }
      render();
      paused = userControl.isPaused();
    }
    t.interrupt();
    if(developerMode)
       winScreen();
  }
  
  /*********************************************************************************
   * winScreen
   * 
   * Displays options to the user when playing in DeveloperMode (not from MenuUI).
   *********************************************************************************/
  public static void winScreen() {
    Object [] options = {"Play again", "Exit"};
    int option = JOptionPane.showOptionDialog(null, "You win", "Cookie Game", JOptionPane.OK_CANCEL_OPTION, 
        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    
    if(option == 0)
      runCookie();
    if(option == 1)
      System.exit(0);
    if(option == JOptionPane.CLOSED_OPTION)
      System.exit(0);
    
  }

  public static void runCookie() {
    initModel();
    initView();
    initModelControls();
    runGame();
  }

  public static void quitCookie() {
    viewer.setVisible(false);
    viewer.dispose();
  }

  /*********************************************************************************
   * render
   * 
   * This method is responsible for causing all of the views to update. Right
   * now this method is pretty simple, but I suspect in the future we will need
   * logic that updates only the parts of the scene that actually changed. In
   * other words, instead of just calling updateGeomtetry(true,true) as below,
   * we'll need to specify how to pick those parameters.
   *********************************************************************************/
  private static void render() {
    gameView.updateGeometry(true, true);
    gameView.updateScene();
  }
}
