package frontend;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.EmbeddedView;
import view.ExponentialView;
import view.FaceAppearanceScheme;
import view.FirstPersonView;
import view.View;
import controller.KeyboardController;
import controller.UserController;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

public class DevelopmentUI {

  private static boolean developerMode = false;

  /*********************************************************************************
   * Model Data
   * 
   * These variables are responsible for keeping track of the states of the
   * mathematical objects in the simulation.
   *********************************************************************************/
  // TODO: Allow the manifold to be specified by an object like the one below,
  // rather than a single global object.
  // private static TriangulatedManifold manifold;

  private static MarkerHandler markerHandler;
  private static Development development;

  /*********************************************************************************
   * View Data
   * 
   * These variables are responsible for keeping track of the viewers that will
   * present the mathematical objects to the user.
   *********************************************************************************/
  private static AbstractMap<View, JFrame> frames;
  static Set<View> views;
  static View firstPersonView;
  static View exponentialView;
  static View embeddedView;

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

  /*********************************************************************************
   * View Control Data
   * 
   * These variables are responsible for keeping track of the swing/AWT
   * components that allow the user to vary parameters of the views.
   * 
   * For example, a slider bar that controls how big the objects on the surfaces
   * are belongs here.
   *********************************************************************************/
  private static ViewerController viewerController;

  private static FaceAppearanceScheme faceAppearanceScheme;

  public static void main(String[] args) {
    // This is a good option to set if you wan't more information about what
    // JReality does behind the scenes
    // JOGLConfiguration.getLogger().setLevel(Level.INFO);

    // Note: For correct initialization, it is important the method calls listed
    // below occur in the particular order listed.
    developerMode = true;
    initModel();
    initViews();
    initModelControls();
    initViewControls();
    runSimulation();
  }

  /*********************************************************************************
   * runExplorer, quitExplorer
   * 
   * These public methods allow MenuUI to start and stop a DevelopmentUI
   * simulation. The runExplorer method initializes a new simulation (i.e. loads
   * a model and views, creates a new controller object, and starts the
   * simulation running). The quitExplorer method closes and discards the view
   * windows.
   * 
   * Note that the simulation may be paused without quitting it. In order to
   * resume a simulation in progress, MenuUI calls the runSimulation method (not
   * the runExplorer method).
   *********************************************************************************/

  public static void runExplorer() {
    initModel();
    initViews();
    initModelControls();
    runSimulation();
  }

  public static void quitExplorer() {
    for (View view : views) {
      JFrame window = frames.get(view);
      window.setVisible(false);
      window.dispose();
    }
  }

  /*********************************************************************************
   * runSimulation
   * 
   * This method gathers together all the pieces needed to carry out the
   * simulation. Specifically, it contains the "game loop" which specifies when
   * the model should be updated and when it should be rendered.
   * 
   * The logic in this method solves a somewhat tricky problem: How should one
   * time updates to the model and rendering so as to display images smoothly?
   * Basically, the idea is to fix a certain time-step size T (which specifies a
   * frame-rate), and then write a render loop such that each run through the
   * loop takes at most time T.
   * 
   * TODO Add more description here.
   *********************************************************************************/
  public static void runSimulation() {

    final long dt = 10; // Timestep size, in microseconds

    long startTime = System.currentTimeMillis();
    long currentTime = startTime;
    long accumulator = 0;
    Thread t = new Thread(userControl);
    t.start();

    Marker source = markerHandler.getSourceMarker();
    userControl.resetPausedFlag();
    userControl.clear();
    while (!userControl.isPaused() || developerMode) {      
      long newTime = System.currentTimeMillis();
      long frameTime = newTime - currentTime;

      currentTime = newTime;
      accumulator += frameTime;

      while (accumulator >= dt) {

        // FIXME: This code will make sure the source point marker is displayed
        // correctly, but this code does not belong in the DevelopmentUI class.
        // Refactoring is needed here.

        // PATCH START
        Face prev = development.getSource().getFace();
        userControl.runNextAction();
        Face next = development.getSource().getFace();
        source.setPosition(development.getSource());
        if (next != prev) {
          markerHandler.updateMarker(source, prev);
        }
        // PATCH END

        markerHandler.updateMarkers(dt);
        accumulator -= dt;
      }
      render();
    }
    t.interrupt();
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
    for (View v : views) {
      v.updateGeometry(true, true);
      v.updateScene();
    }
  }

  /*********************************************************************************
   * initModel
   * 
   * This method is responsible for initializing the mathematical model of the
   * simulation we'll present to the user. Specifically, this means setting up
   * the triangulated surface and the markers that will be placed on it.
   *********************************************************************************/
  private static void initModel() {
    String filename = "Data/surfaces/tetra.off";
    //String filename = "Data/surfaces/dodec.off";
    loadSurface(filename);
  }

  /*********************************************************************************
   * loadSurface
   * 
   * This method uses the input file name (which should include the path to the
   * file), and reads that file to determine the surface that will be displayed.
   *********************************************************************************/
  public static void loadSurface(String file) {
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
      development = new Development(new ManifoldPosition(sourceFace,
          sourcePoint), 3, 1.0);
    } else {
      development.rebuild(new ManifoldPosition(sourceFace, sourcePoint), 3);
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

    pos = development.getSource();
    app = new MarkerAppearance(MarkerAppearance.ModelType.ANT);
    markerHandler.addSourceMarker(new Marker(pos, app));

    Random rand = new Random();
    // Introduce three other markers to move around on the manifold.
    for (int ii = 0; ii < 3; ii++) {
      pos = new ManifoldPosition(development.getSource());
      app = new MarkerAppearance(MarkerAppearance.ModelType.COOKIE, 0.5);
      double a = rand.nextDouble() * Math.PI * 2;
      Vector vel = new Vector(Math.cos(a), Math.sin(a));
      vel.scale(0.0005);

      Marker m = new Marker(pos, app, vel);
      markerHandler.addMarker(m);
    }

    // Move the markers along their trajectories for 300ms, so that they don't
    // sit on top of each other.
    markerHandler.updateMarkers(300);
  }

  /*********************************************************************************
   * initViews
   * 
   * This method initializes the data structures that keep track of the views in
   * our program. These views will be initialized in ViewerController.
   *********************************************************************************/
  private static void initViews() {
    faceAppearanceScheme = new FaceAppearanceScheme();
    views = Collections.newSetFromMap(new ConcurrentHashMap<View, Boolean>());
    frames = new HashMap<View, JFrame>();
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
    // userControl = new SNESController(development);
    userControl = new KeyboardController(development);
  }

  /*********************************************************************************
   * initViewControls
   * 
   * This method is responsible for initializing the buttons and sliders that
   * will control how the model is visualized for the user. For example, a
   * slider that controls how big the markers are should be initialized here.
   *********************************************************************************/
  private static void initViewControls() {
    viewerController = new ViewerController(markerHandler, development, views);
    viewerController.setVisible(true);
  }

  public static void setDrawEdges(boolean drawEdge) {
    for (View v : views) {
      v.setDrawEdges(drawEdge);
    }
  }

  public static void setDrawFaces(boolean drawFace) {
    for (View v : views) {
      v.setDrawFaces(drawFace);
    }
  }

  public static void resetView() {
    if (exponentialView != null) {
      setExponentialView(false);
      setExponentialView(true);
    }

    if (embeddedView != null) {
      setEmbeddedView(false);
      setEmbeddedView(true);
    }

    if (firstPersonView != null) {
      setFirstPersonView(false);
      setFirstPersonView(true);
    }

    viewerController.setMarkerHandler(markerHandler);
  }

  public static void setExponentialView(boolean viewEnabled) {
    if (viewEnabled) {
      exponentialView = new ExponentialView(development, markerHandler, faceAppearanceScheme);
      exponentialView.updateGeometry();
      exponentialView.initializeNewManifold();
      exponentialView.updateScene();

      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      frame.setLocation(400, 40);
      frame.setResizable(true);

      Dimension size = new Dimension(400, 400);
      Container contentPane = frame.getContentPane();
      contentPane.add((Component) exponentialView.getViewer()
          .getViewingComponent());
      contentPane.setMinimumSize(size);
      contentPane.setPreferredSize(size);
      contentPane.setMaximumSize(size);
      frame.pack();
      frame.validate();
      frame.setVisible(true);
      
      views.add(exponentialView);
      frames.put(exponentialView, frame);
    } else {
      if (exponentialView == null)
        return;
      JFrame frame = frames.remove(exponentialView);
      frame.setVisible(false);
      frame.dispose();
      views.remove(exponentialView);
      exponentialView = null;
    }
  }

  public static void setEmbeddedView(boolean viewEnabled) {
    if (viewEnabled) {
      embeddedView = new EmbeddedView(development, markerHandler, faceAppearanceScheme);
      embeddedView.updateGeometry();
      embeddedView.initializeNewManifold();
      embeddedView.updateScene();

      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      frame.setLocation(805, 40);
      frame.setResizable(true);

      Dimension size = new Dimension(400, 400);
      Container contentPane = frame.getContentPane();
      contentPane.add((Component) embeddedView.getViewer()
          .getViewingComponent());
      contentPane.setMinimumSize(size);
      contentPane.setPreferredSize(size);
      contentPane.setMaximumSize(size);
      frame.pack();
      frame.validate();
      frame.setVisible(true);

      views.add(embeddedView);
      frames.put(embeddedView, frame);
    } else {
      if (embeddedView == null)
        return;
      JFrame frame = frames.remove(embeddedView);
      frame.setVisible(false);
      frame.dispose();
      views.remove(embeddedView);
      embeddedView = null;
    }
  }

  public static void setFirstPersonView(boolean viewEnabled) {
    if (viewEnabled) {
      firstPersonView = new FirstPersonView(development, markerHandler, faceAppearanceScheme);
      firstPersonView.updateGeometry();
      firstPersonView.initializeNewManifold();
      firstPersonView.updateScene();

      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      frame.setLocation(1210, 40);
      frame.setResizable(true);

      Dimension size = new Dimension(400, 400);
      Container contentPane = frame.getContentPane();
      contentPane.add((Component) firstPersonView.getViewer()
          .getViewingComponent());
      contentPane.setMinimumSize(size);
      contentPane.setPreferredSize(size);
      contentPane.setMaximumSize(size);
      frame.pack();
      frame.validate();
      frame.setVisible(true);

      views.add(firstPersonView);
      frames.put(firstPersonView, frame);
    } else {
      if (firstPersonView == null)
        return;
      JFrame frame = frames.remove(firstPersonView);
      frame.setVisible(false);
      frame.dispose();
      views.remove(firstPersonView);
      firstPersonView = null;
    }
  }

}
