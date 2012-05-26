package viewMKII;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;

import markers.ManifoldPosition;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme;
import view.ColorScheme.schemes;
import de.jreality.jogl.JOGLConfiguration;
import development.Coord2D;
import development.Development;
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
  // private static AbstractList<Marker> markers;
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
   *********************************************************************************/
  // private static MouseController mouseControl;
    private static KeyboardController keyboardControl;

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
  private static JSlider pointSizeSlider;
  private static JSlider speedSlider;
  private static JSlider depthSlider;
  private static JButton stopStartButton;

  private static ColorScheme colorScheme;

  public static void main(String[] args) {
    JOGLConfiguration.getLogger().setLevel(Level.INFO);

    initModel();
    initViews();
    initModelControls();
    initViewControls();
    
    TimerTask tt = new TimerTask(){
      public void run() {        
        for(View v : views){
          v.updateGeometry(true,true);
          v.updateScene();
        }
      }
    };
      
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(tt, 0, 1000 / 40); // Try for 40 frames per second.    
  }

  /*********************************************************************************
   * Method initModel
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
   * Method loadSurface
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
  }

  /*********************************************************************************
   * Method initSurface
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
          sourcePoint), 7, 1.0);
    } else {
      development.rebuild(new ManifoldPosition(sourceFace, sourcePoint), 7);
    }
  }

  /*********************************************************************************
   * Method initMarkers
   * 
   * This method initializes the markers (ants, rockets, cookies, etc.) that
   * will appear on the surface for games and exploration.
   *********************************************************************************/
  private static void initMarkers() {

  }

  /*********************************************************************************
   * Method initViews
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
    views[0] = new FirstPersonView(development, colorScheme);
    views[1] = new ExponentialView(development, colorScheme);
    views[2] = new EmbeddedView(development, colorScheme);
    
    int[][] framePositions = {{0,310},{0,10},{400,10}}; 
    int[][] frameSizes = {{800,400},{400,300},{400,300}};

    frames = new HashMap<View, JFrame>();

    for (int ii = 0; ii < 3; ii++) {
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
   * Method initModelControls
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
    
    
//    Component comp = (Component) views[0].getViewer().getViewingComponent();
//    comp.addKeyListener(new KeyAdapter() {
//
//      public void keyPressed(KeyEvent e) {
//
//        System.out.println("Received keypress: " + e.getKeyCode());
//
//        switch (e.getKeyCode()) {
//        case KeyEvent.VK_RIGHT:
//          development.rotate(0.1);
//          break;
//        case KeyEvent.VK_LEFT:
//          development.rotate(-0.1);
//          break;
//        case KeyEvent.VK_UP:
//          development.translateSourcePoint(0.1, 0);
//          break;
//        case KeyEvent.VK_DOWN:
//          development.translateSourcePoint(-0.1, 0);
//          break;
//        }
//
//        for (View v : views) {
//          v.updateGeometry(true, true);          
//          v.updateScene();
//        }
//      }
//    });
  }

  /*********************************************************************************
   * Method initViewControls
   * 
   * This method is responsible for initializing the buttons and sliders that
   * will control how the model is visualized for the user. For example, a
   * slider that controls how big the markers are should be initialized here.
   *********************************************************************************/
  private static void initViewControls() {}
}
