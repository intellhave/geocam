package viewMKII;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;

import markers.ManifoldPosition;
import markers.MarkerAppearance;
import markersMKII.Marker;
import markersMKII.MarkerHandler;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.EmbeddedTriangulation;
import development.Vector;

public class DevelopmentUI {

  /*********************************************************************************
   * Model Data
   * 
   * These variables are responsible for keeping track of the states of the
   * mathematical objects in the simulation.
   *********************************************************************************/
  // TODO: Allow the manifold to be specified by an object like the one below,
  // rather than a single global object.
  // private static TriangulatedManifold manifold;

  private static MarkerHandler markers;
  private static Development development;

  /*********************************************************************************
   * View Data
   * 
   * These variables are responsible for keeping track of the viewers that will
   * present the mathematical objects to the user.
   *********************************************************************************/
  private static AbstractMap<View, JFrame> frames;

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
  // private static MouseController mouseControl;
  private static KeyboardController keyboardControl;
  // private static MarkerController markerControl;
  // private static NetworkController networkControl;

  /*********************************************************************************
   * View Control Data
   * 
   * These variables are responsible for keeping track of the swing/AWT
   * components that allow the user to vary parameters of the views.
   * 
   * For example, a slider bar that controls how big the objects on the surfaces
   * are belongs here.
   *********************************************************************************/
  static View[] views;
  // private static JSlider pointSizeSlider;
  // private static JSlider speedSlider;
  // private static JSlider depthSlider;
  // private static JButton stopStartButton;

  private static ColorScheme colorScheme;

  public static void main(String[] args) {
    // This is a good option to set if you wan't more information about what
    // JReality does behind the scenes
    // JOGLConfiguration.getLogger().setLevel(Level.INFO);

    // Note: For correct initialization, it is important the method calls listed
    // below occur in the particular order listed.
    initModel();
    initViews();
    initModelControls();
    initViewControls();
    runSimulation();
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
  private static void runSimulation() {
    boolean quit = false;

    final long dt = 10; // Timestep size, in microseconds
    // final long maxFrameTime = 80;

    long startTime = System.currentTimeMillis();
    long currentTime = startTime;
    long accumulator = 0;

    while (!quit) {
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
        keyboardControl.runNextAction();
        Face next = development.getSource().getFace();
        if(next != prev){
          markers.updateMarker(development.getSourceMarker(), prev);
        }
        // PATCH END
        
        markers.updateMarkers(dt);

        accumulator -= dt;
      }

      // Normally, we would form a "convex combination" of our current and
      // previous states here, to ensure a smooth animation. We will omit
      // this code for now.
      // const double alpha = accumulator / dt;
      // State state = currentState*alpha + previousState * ( 1.0 - alpha );

      render();
    }
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
    String filename = "Data/blender/cube_surf.off";
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
    Random rand = new Random();

    markers = new MarkerHandler();

    markers.addMarker(development.getSourceMarker());

    ManifoldPosition pos;
    MarkerAppearance app;
    // Introduce three other markers to move around on the manifold.
    for (int ii = 0; ii < 3; ii++) {
      pos = new ManifoldPosition(development.getSource());
      app = new MarkerAppearance(MarkerAppearance.ModelType.ANT, 0.5);
      double a = rand.nextDouble() * Math.PI * 2;
      Vector vel = new Vector(Math.cos(a), Math.sin(a));
      vel.scale(0.001);

      Marker m = new Marker(pos, app, vel);
      markers.addMarker(m);
    }

    // Move the markers along their trajectories for 300ms, so that they don't
    // sit on top of each other.
    markers.updateMarkers(300);
  }

  /*********************************************************************************
   * initViews
   * 
   * This method is responsible for initializing the code that creates a
   * visualization of the triangulated surface for the user. This method is also
   * responsible for positioning the views on the screen.
   * 
   * Because we're interested in displaying this in a museum setting (where
   * users shouldn't be allowed to change window sizes), it's OK to hard code
   * this information.
   *********************************************************************************/
  private static void initViews() {
    colorScheme = new ColorScheme(schemes.FACE);

    int viewCount = 3;
    views = new View[viewCount];
    // views[0] = new FirstPersonView(development, markers, colorScheme);
    views[0] = new FirstPersonView(development, markers, colorScheme);
    views[1] = new ExponentialView(development, markers, colorScheme);
    views[2] = new EmbeddedView(development, markers, colorScheme);

    int[][] framePositions = { { 0, 10 }, { 400, 10 }, { 800, 10 } };
    int[][] frameSizes = { { 400, 400 }, { 400, 400 }, { 400, 400 } };

    frames = new HashMap<View, JFrame>();

    for (int ii = 0; ii < viewCount; ii++) {
      View u = views[ii];

      u.updateGeometry();
      u.initializeNewManifold();
      u.updateScene();

      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      frame.setLocation(framePositions[ii][0], framePositions[ii][1]);
      frame.setResizable(false);

      Dimension size = new Dimension(frameSizes[ii][0], frameSizes[ii][1]);
      Container contentPane = frame.getContentPane();
      contentPane.add((Component) u.getViewer().getViewingComponent());
      contentPane.setMinimumSize(size);
      contentPane.setPreferredSize(size);
      contentPane.setMaximumSize(size);
      frame.pack();
      frame.validate();
      frame.setVisible(true);

      frames.put(u, frame);
    }
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
    keyboardControl = new KeyboardController(development);
  }

  /*********************************************************************************
   * initViewControls
   * 
   * This method is responsible for initializing the buttons and sliders that
   * will control how the model is visualized for the user. For example, a
   * slider that controls how big the markers are should be initialized here.
   *********************************************************************************/
  private static void initViewControls() {
  }
}
