package frontend;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import marker.BreadCrumbs;
import marker.ForwardGeodesic;
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
import controller.SNESController;
import controller.UserController;
import de.jreality.jogl.JOGLViewer;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

/*********************************************************************************
 * DevelopmentUI
 * 
 * This object is responsible for bringing together three views of a particular
 * triangulated surface and its development. It provides a means to receive
 * input from the user, and to control all three views in a consistent way.
 * 
 * Originally, much of the code responsible for adjusting the views appeared in
 * ViewerController. This was problematic, because it meant that whenever we
 * wanted to set up a new simulation (display a different surface) on the fly,
 * both the DevelopmentUI and ViewerController had to update many state
 * variables. Inevitably, we got the updates wrong, and null pointer exceptions
 * resulted.
 * 
 * In our current system, ViewerController is ONLY responsible for setting up
 * the buttons/sliders/etc. and converting their events into method calls to the
 * current DevelopmentUI object. This means that a ViewerController object NEVER
 * queries the DevelopmentUI object for information --- it ONLY tells the
 * DevelopmentUI object what it wants, and sets its display based on that
 * information.
 * 
 * To repeat this important point: Information flows only one way, from
 * ViewerController to DevelopmentUI!
 * 
 * When we want to display a different surface, we simply destroy the current
 * DevelopmentUI object and make a new one. Unfortunately, because our
 * application is multithreaded and we have a global "Triangulation" object,
 * this is a little tricky. The thread that wants to end the simulation should
 * begin by calling the "terminate" method. This signals the thread running
 * within the run method's render loop that it is time to stop the simulation.
 * This "rendering thread" will break out of the render loop at it's first
 * opportunity.
 * 
 * We adopted this structure because trying to get the AWT-Event thread to
 * synchronize with the render thread created deadlock. The "rendering thread"
 * should usually be the thread created by the program's main method. If a new
 * simulation is terminated, the rendering thread is the thread that should
 * create a new DevelopmentUI instance and start running it. Because we have a
 * single Triangulation object, two DevelopmentUI instances (for distinct
 * surfaces) cannot exist without problems.
 * 
 * To repeat an important idiom: The ViewerController (and its event based code)
 * only passes signals and information to the main thread! If there's heavy
 * lifting to be done (e.g. creating a new simulation for a different surface),
 * ViewerController should give the main thread the signal to do this work. To
 * ensure correctness, an AWT-Event thread may want to lock the current
 * ViewerController instance, so that no new button/slider/etc. events can be
 * created. This is done with ViewerController's setEnabled method.
 *********************************************************************************/

public class DevelopmentUI implements Runnable {
  private boolean simulationRunning = false;

  /*********************************************************************************
   * Model Data
   * 
   * These variables are responsible for keeping track of the states of the
   * mathematical objects in the simulation.
   *********************************************************************************/
  private MarkerHandler markerHandler;
  private Marker source;
  private BreadCrumbs crumbs;
  private Development development;
  private ForwardGeodesic geo;
  private double movingMarkerSpeed;
  private double movingMarkerScale;

  /*********************************************************************************
   * View Data
   * 
   * These variables are responsible for keeping track of the viewers that will
   * present the mathematical objects to the user.
   *********************************************************************************/
  private AbstractMap<View, JFrame> frames;
  private Set<View> views;
  private View firstPersonView;
  private ExponentialView exponentialView;
  private View embeddedView;
  private boolean isEmbedded;
  private static FaceAppearanceScheme faceAppearanceScheme;

  private boolean texturesEnabled = true;
  private boolean edgesEnabled = false;
  private boolean facesEnabled = true;
  private double embeddedZoom;
  private double exponentialZoom;

  /*********************************************************************************
   * Model Control Data
   * 
   * These variables hold the listeners and other objects responsible for
   * keeping track of user input to the program that effects the model.
   *********************************************************************************/
  private UserController userControl;

  public DevelopmentUI(String pathToSurfaceData) {
    initSurface(pathToSurfaceData);
    initMarkers();
    initViews();
    initModelControls();
  }

  /*********************************************************************************
   * run
   * 
   * This method is where the "rendering" thread spends most of its time. Within
   * this method's while loop, we receive input from the user, update the state
   * of the simulation, and then render to all of the active views managed by
   * this object. We break out of the while loop only when we receive a message
   * from another thread.
   *********************************************************************************/
  public void run() {
    simulationRunning = true;
    final long dt = 10; // Timestep size, in microseconds

    long startTime = System.currentTimeMillis();
    long currentTime = startTime;
    long accumulator = 0;
    Thread control = new Thread(userControl);
    control.start();

    userControl.resetPausedFlag();
    userControl.clear();
    while (!userControl.isPaused() && simulationRunning) {
      removeFlaggedMarkers();

      long newTime = System.currentTimeMillis();
      long frameTime = newTime - currentTime;

      currentTime = newTime;
      accumulator += frameTime;

      while (accumulator >= dt) {

        // FIXME: This code will make sure the source point marker is
        // displayed
        // correctly, but this code does not belong in the DevelopmentUI
        // class.
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

      /* Now render the scene in all views. */
      for (View v : views) {
        v.update();
      }
    }
    control.interrupt();
  }

  /*********************************************************************************
   * removeFlaggedMarkers
   * 
   * This is a helper method for run. When a marker needs to be removed from the
   * simulation, it indicates this by setting its markertype to REMOVED. In this
   * method, we find all such markers and remove them from each part of the
   * simulation.
   *********************************************************************************/
  private void removeFlaggedMarkers() {
    Set<Marker> removedMarkers = new HashSet<Marker>();
    for (Marker m : markerHandler.getAllMarkers()) {
      if (m.isRemoved()) {
        removedMarkers.add(m);
      }
    }

    for (Marker m : removedMarkers) {
      markerHandler.removeMarker(m);
      for (View v : views) {
        v.removeMarker(m);
      }
    }
  }

  /*********************************************************************************
   * terminate
   * 
   * This method allows other threads (like the AWT Event thread) to signal the
   * thread running in the "run" loop that it should stop the simulation at it's
   * earliest convenience.
   *********************************************************************************/
  public void terminate() {
    simulationRunning = false;
    setExponentialView(false);
    setEmbeddedView(false);
    setFirstPersonView(false);
  }

  /*********************************************************************************
   * initSurface
   * 
   * Given a file with path fileName that describes a triangulated surface, this
   * method loads the description of that surface and initializes the data
   * structure that specifies the surface for the rest of the program.
   *********************************************************************************/
  private void initSurface(String fileName) {
    String extension = fileName.substring(fileName.length() - 3,
        fileName.length());
    if (extension.contentEquals("off")) {
      EmbeddedTriangulation.readEmbeddedSurface(fileName);
      isEmbedded = true;
    } else if (extension.contentEquals("xml")) {
      TriangulationIO.readTriangulation(fileName);
      isEmbedded = false;
    } else {
      System.err.println("invalid file");
    }

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
          sourcePoint), 5, 1.0);
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
  private void initMarkers() {
    markerHandler = new MarkerHandler();
    crumbs = new BreadCrumbs(markerHandler);
    geo = new ForwardGeodesic(markerHandler);

    ManifoldPosition pos;
    MarkerAppearance app;

    pos = development.getSource();
    app = new MarkerAppearance(MarkerAppearance.ModelType.LADYBUG);
    markerHandler
        .addSourceMarker(new Marker(pos, app, Marker.MarkerType.SOURCE));
    source = markerHandler.getSourceMarker();
  }

  /*********************************************************************************
   * initViews
   * 
   * This method initializes the data structures that keep track of the views in
   * our program. These views will be initialized in ViewerController.
   *********************************************************************************/
  private void initViews() {
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
  private void initModelControls() {

    //FIXME
//    userControl = new SNESController(development, crumbs, geo);
    userControl = new KeyboardController(development, crumbs, geo);
  }

  /*********************************************************************************
   * Simulation and Marker Control Methods
   * 
   * These methods are used by outside code (like ViewerController instances) to
   * set certain common parameters of the simulation pertinent to markers, like
   * the number of markers, their speed and size, etc.
   *********************************************************************************/
  public void setRecursionDepth(int depth) {
    development.setDepth(depth);
  }

  public void setMarkerMobility(boolean canMove) {
    if (canMove) {
      markerHandler.unpauseSimulation();
    } else {
      markerHandler.pauseSimulation();
    }
  }

  public void setMovingMarkerCount(int numMarkers) {
    Set<Marker> markers = markerHandler.getAllMarkers();
    // Get the current number of moving markers.
    int currentMarkers = 0;
    for (Marker m : markers) {
      if (m.getMarkerType() == Marker.MarkerType.MOVING) {
        currentMarkers++;
      }
    }

    // If necessary, add moving markers.
    if (currentMarkers < numMarkers) {
      Random rand = new Random();
      ManifoldPosition pos;
      MarkerAppearance app;

      for (int ii = 0; ii < numMarkers - currentMarkers; ii++) {
        pos = new ManifoldPosition(development.getSource());
        app = new MarkerAppearance(MarkerAppearance.ModelType.ANT,
            movingMarkerScale);
        double a = rand.nextDouble() * Math.PI * 2;
        Vector vel = new Vector(Math.cos(a), Math.sin(a));
        // Move the new marker away from the source point.
        vel.scale(0.25);
        pos.move(vel);

        Marker m = new Marker(pos, app, Marker.MarkerType.MOVING, vel);
        m.setSpeed(movingMarkerSpeed);
        m.setVisible(true);
        markerHandler.addMarker(m);
      }
      markerHandler.updateMarkers(100);
    }

    // If necessary, remove moving markers.
    if (currentMarkers > numMarkers) {
      int counter = 0;
      for (Marker m : markers) {
        if (m.getMarkerType() == Marker.MarkerType.MOVING) {
          m.flagForRemoval();
          counter++;
        }
        if (counter == currentMarkers - numMarkers)
          break;
      }
    }
  }

  public void setMovingMarkerSpeed(double speed) {
    movingMarkerSpeed = speed;
    for (Marker m : markerHandler.getAllMarkers()) {
      if (m.getMarkerType() == Marker.MarkerType.MOVING) {
        m.setSpeed(speed);
      }
    }
  }

  public void setMovingMarkerScale(double scale) {
    movingMarkerScale = scale;
    for (Marker m : markerHandler.getAllMarkers()) {
      if (m.getMarkerType() == Marker.MarkerType.MOVING) {
        m.getAppearance().setScale(scale);
      }
    }
  }

  public void setSourceVisible(boolean b) {
    markerHandler.getSourceMarker().setVisible(b);
  }

  public void setGeodesicLength(int length) {
    if (length < 0)
      return;
    clearGeodesic();
    geo.setLength(length);
  }

  public void clearGeodesic() {
    for (Marker m : geo.getMarkers()) {
      m.flagForRemoval();
    }
  }

  /*********************************************************************************
   * View Control Methods
   *********************************************************************************/
  public void resetAllViews() {
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
  }

  /*********************************************************************************
   * makeJFrame
   * 
   * This method builds a JFrame to contain the input viewer, and places it at
   * the input screen coordinates.
   *********************************************************************************/
  private JFrame makeJFrame(JOGLViewer v, int locationX, int locationY) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setLocation(locationX, locationY);
    frame.setResizable(true);

    Dimension size = new Dimension(400, 400);
    Container contentPane = frame.getContentPane();
    contentPane.add((Component) v.getViewingComponent());
    contentPane.setMinimumSize(size);
    contentPane.setPreferredSize(size);
    contentPane.setMaximumSize(size);
    frame.pack();
    frame.validate();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    // Add a component listener so that the viewer can be resized while
    // preserving the aspect ratio.
    frame.addComponentListener(new ComponentListener() {
      public void componentShown(ComponentEvent arg0) {
      }

      public void componentHidden(ComponentEvent arg0) {
      }

      public void componentMoved(ComponentEvent arg0) {
      }

      public void componentResized(ComponentEvent arg0) {                
        Component c = arg0.getComponent();
        Rectangle r = c.getBounds();
        int m = Math.max( Math.max(r.height, r.width), 50);
        c.setBounds(r.x,r.y,m,m);        
      }
    });

    return frame;
  }

  public void setExponentialView(boolean viewEnabled) {
    if (viewEnabled) {
      exponentialView = new ExponentialView(development, markerHandler,
          faceAppearanceScheme);

      exponentialView.setDrawEdges(edgesEnabled);
      exponentialView.setDrawFaces(facesEnabled);
      exponentialView.setDrawTextures(texturesEnabled);
      exponentialView.setZoom(exponentialZoom);
      
      exponentialView.initializeNewManifold();
      exponentialView.update();

      JFrame frame = makeJFrame(exponentialView.getViewer(), 400, 40);
      views.add(exponentialView);
      frames.put(exponentialView, frame);
      frame.setTitle("Exponential (Map) View");
    } else if (exponentialView != null) {
      JFrame frame = frames.remove(exponentialView);
      frame.setVisible(false);
      frame.dispose();
      views.remove(exponentialView);
      exponentialView = null;
    }
  }

  public void setEmbeddedView(boolean viewEnabled) {
    if (viewEnabled && isEmbedded) {
      embeddedView = new EmbeddedView(development, markerHandler,
          faceAppearanceScheme);

      embeddedView.setDrawEdges(edgesEnabled);
      embeddedView.setDrawFaces(facesEnabled);
      embeddedView.setDrawTextures(texturesEnabled);
      embeddedView.setZoom(embeddedZoom);

      embeddedView.initializeNewManifold();
      embeddedView.update();

      JFrame frame = makeJFrame(embeddedView.getViewer(), 805, 50);
      views.add(embeddedView);
      frames.put(embeddedView, frame);
      frame.setTitle("3D Embedded View");
    } else if (embeddedView != null) {
      JFrame frame = frames.remove(embeddedView);
      frame.setVisible(false);
      frame.dispose();
      views.remove(embeddedView);
      embeddedView = null;
    }
  }

  public void setFirstPersonView(boolean viewEnabled) {
    if (viewEnabled) {
      firstPersonView = new FirstPersonView(development, markerHandler,
          faceAppearanceScheme);

      firstPersonView.setDrawEdges(edgesEnabled);
      firstPersonView.setDrawFaces(facesEnabled);
      firstPersonView.setDrawTextures(texturesEnabled);
      firstPersonView.setZoom(exponentialZoom);

      firstPersonView.initializeNewManifold();
      firstPersonView.update();

      JFrame frame = makeJFrame(firstPersonView.getViewer(), 1210, 40);

      views.add(firstPersonView);
      frames.put(firstPersonView, frame);
      frame.setTitle("First Person View");
    } else if (firstPersonView != null) {
      JFrame frame = frames.remove(firstPersonView);
      frame.setVisible(false);
      frame.dispose();
      views.remove(firstPersonView);
      firstPersonView = null;
    }
  }

  public void setDrawEdges(boolean showEdges) {
    edgesEnabled = showEdges;
    for (View v : views) {
      v.setDrawEdges(showEdges);
    }
  }

  public void setDrawFaces(boolean showFaces) {
    facesEnabled = showFaces;
    for (View v : views) {
      v.setDrawFaces(showFaces);
    }
  }

  public void setDrawTextures(boolean texturingOn) {
    texturesEnabled = texturingOn;
    for (View v : views) {
      v.setDrawTextures(texturingOn);
      v.update();
    }
  }

  public void setEmbeddedZoom(double zoomValue) {
    embeddedZoom = zoomValue;
    if (embeddedView != null)
      embeddedView.setZoom(zoomValue);
  }

  public void setExponentialZoom(double zoomValue) {
    exponentialZoom = zoomValue;
    if (exponentialView != null)
      exponentialView.setZoom(zoomValue);
  }
}
