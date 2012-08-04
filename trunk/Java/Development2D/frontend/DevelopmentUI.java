package frontend;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import controller.KeyboardController;
import controller.UserController;

import marker.BreadCrumbs;
import marker.ForwardGeodesic;
import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import marker.MarkerAppearance.ModelType;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.EmbeddedView;
import view.ExponentialView;
import view.FaceAppearanceScheme;
import view.FirstPersonView;
import view.View;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

public class DevelopmentUI implements Runnable {
  private boolean simulationInitialized = false;
  private boolean simulationRunning = false;
  private boolean simulationTerminated = false;

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
  private boolean texturingEnabled = true;

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
    simulationInitialized = true;    
  }

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

    simulationTerminated = true;
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

  public void terminate() {
    simulationRunning = false;
    setExponentialView(false);
    setEmbeddedView(false);
    setFirstPersonView(false);
  }
  
  public boolean isTerminated(){
    return simulationTerminated;
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

    Random rand = new Random();
    // Introduce three other markers to move around on the manifold.
    for (int ii = 0; ii < 3; ii++) {
      pos = new ManifoldPosition(development.getSource());
      app = new MarkerAppearance(ModelType.ANT, .75);
      double a = rand.nextDouble() * Math.PI * 2;
      Vector vel = new Vector(Math.cos(a), Math.sin(a));
      vel.scale(0.0005);

      Marker m = new Marker(pos, app, Marker.MarkerType.MOVING, vel);
      m.setSpeed(0.0);
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
    // userControl = new SNESController(development);
    userControl = new KeyboardController(development, crumbs, geo);
  }

  
  /*********************************************************************************
   * Simulation Controls
   *********************************************************************************/
  public void setRecursionDepth(int depth){
    development.setDepth(depth);
  }
  
  
  /*********************************************************************************
   * Marker Control Methods
   *********************************************************************************/
  public void setMarkerMobility(boolean canMove) {
    if(canMove){
      markerHandler.unpauseSimulation();
    } else {
      markerHandler.pauseSimulation();
    }
  }

  public void setMovingMarkerCount(int numMarkers){           
    Set<Marker> markers = markerHandler.getAllMarkers();
    // Get the current number of moving markers.
    int currentMarkers = 0;
    for( Marker m : markers) {
      if(m.getMarkerType() == Marker.MarkerType.MOVING){
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
        app = new MarkerAppearance(source.getAppearance().getModelType(), movingMarkerScale);
        double a = rand.nextDouble() * Math.PI * 2;
        Vector vel = new Vector(Math.cos(a), Math.sin(a));
        // Move the new marker away from the source point.
        vel.scale(0.25);
        pos.move(vel);
        
        Marker m = new Marker(pos, app, Marker.MarkerType.MOVING, vel);        
        m.setSpeed(movingMarkerSpeed);

        markerHandler.addMarker(m);
      }
    }

    // If necessary, remove moving markers.
    if (currentMarkers > numMarkers) {
      int counter = 0;            
      for (Marker m : markers) {
        if (m.getMarkerType() == Marker.MarkerType.MOVING){
          m.flagForRemoval();
          counter++;
        }
        if (counter == currentMarkers - numMarkers) break;
      }
    }
  }
  
  
  public void setMovingMarkerSpeed(double speed){
    for(Marker m : markerHandler.getAllMarkers()){
      if(m.getMarkerType() == Marker.MarkerType.MOVING){
        m.setSpeed(speed);
      }
    }
  }
  
  public void setMovingMarkerScale(double scale){
    for(Marker m : markerHandler.getAllMarkers()){
      if(m.getMarkerType() == Marker.MarkerType.MOVING){
        m.getAppearance().setScale(scale);
      }
    }
  }

  public void setSourceVisible(boolean b){
    markerHandler.getSourceMarker().setVisible(b);
  }
  
  public void setGeodesicLength(int length){
    if( length < 0 ) return;
    clearGeodesic();    
    geo.setLength(length);    
  }
  
  public void clearGeodesic(){
    for(Marker m : geo.getMarkers()){
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

  public void setExponentialView(boolean viewEnabled) {
    if (viewEnabled) {
      exponentialView = new ExponentialView(development, markerHandler,
          faceAppearanceScheme);
      exponentialView.setTexture(texturingEnabled);
      exponentialView.initializeNewManifold();
      exponentialView.update();

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

  public void setEmbeddedView(boolean viewEnabled) {
    if (viewEnabled && isEmbedded) {
      embeddedView = new EmbeddedView(development, markerHandler,
          faceAppearanceScheme);
      embeddedView.setTexture(texturingEnabled);
      embeddedView.initializeNewManifold();
      embeddedView.update();

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

  public void setFirstPersonView(boolean viewEnabled) {
    if (viewEnabled) {
      firstPersonView = new FirstPersonView(development, markerHandler,
          faceAppearanceScheme);
      firstPersonView.setTexture(texturingEnabled);
      firstPersonView.initializeNewManifold();
      firstPersonView.update();

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

  
  public void setDrawEdges(boolean showEdges){      
  }
  
  public void setDrawFaces(boolean showFaces){
    
  }
  
  public void setTexturing(boolean texturingOn) {
    texturingEnabled = texturingOn;
    for (View v : views) {
      v.setTexture(texturingOn);
      v.update();
    }
  }
}
