package viewMKII;

import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import markers.ManifoldPosition;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme;
import view.ColorScheme.schemes;

import de.jreality.util.RenderTrigger;
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
  //private static AbstractList<Marker> markers;
  private static Development development;

  /*********************************************************************************
   * View Data
   * 
   * These variables are responsible for keeping track of the viewers that will
   * present the mathematical objects to the user.
   *********************************************************************************/
  private static AbstractList<View> views;
  private static JPanel compositor;
  private static JFrame frame;

  /*********************************************************************************
   * Model Control Data
   * 
   * These variables hold the listeners and other objects responsible for
   * keeping track of user input to the program that effects the model.
   *********************************************************************************/
  // private static MouseController mouseControl;
  // private static KeyboardController keyboardControl;

  /*********************************************************************************
   * View Control Data
   * 
   * These variables are responsible for keeping track of the swing/AWT
   * components that allow the user to vary parameters of the views.
   * 
   * For example, a slider bar that controls how big the objects on the surfaces
   * are belongs here.
   *********************************************************************************/
  private static JSlider pointSizeSlider;
  private static JSlider speedSlider;
  private static JSlider depthSlider;
  private static JButton stopStartButton;

  private static ColorScheme colorScheme;

  public static void main(String[] args) {
    initModel();
    initViews();
    initModelControls();
    initViewControls();
    ((SideBySideViewPanel) compositor).updateScene();
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

    if (development == null){
      development = new Development(new ManifoldPosition(sourceFace, sourcePoint), 7, 1.0);
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
   * This method is responsible for initializing the objects that will create a
   * visualization of the triangulated surface for the user. This may involve
   * composing several different visualizations into a single image of the
   * surface.
   *********************************************************************************/
  private static void initViews() {
    colorScheme = new ColorScheme(schemes.FACE);

    views = new LinkedList<View>();
    View v = new EmbeddedView(development, colorScheme);
    View w = new ExponentialView(development, colorScheme);
    compositor = new SideBySideViewPanel(v, w);
    views.add(v);
    views.add(w);

    frame = new JFrame();
    frame.setVisible(true);

    // jthomas: For reasons that aren't completely clear, this code seems to be
    // very delicate. Changing settings can cause the views to stop appearing,
    // leaving a grey screen instead. I think this might be a JOGL/AWT
    // communication problem. Modify with caution.
    // Begin Delicate Code
    frame.setSize(compositor.getSize());
    frame.add(compositor);
    frame.setResizable(false);
    // End Delicate Code

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Now the Views are situated in the frame. We can tell the views to begin
    // displaying the model.
    for (View u : views) {
      u.updateGeometry();
      u.initializeNewManifold();
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
  }

  /*********************************************************************************
   * Method initViewControls
   * 
   * This method is responsible for initializing the buttons and sliders that
   * will control how the model is visualized for the user. For example, a
   * slider that controls how big the markers are should be initialized here.
   *********************************************************************************/
  private static void initViewControls() {
    frame.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
          ((SideBySideViewPanel) compositor).updateScene();
          System.out.println("Enter pressed. Updating Scene.");

        }
      }
    });
  }
}
