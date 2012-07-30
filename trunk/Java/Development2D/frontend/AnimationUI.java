package frontend;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JFrame;

import marker.Marker;
import marker.MarkerAppearance;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.FaceAppearanceScheme;
import view.UnfoldingView;
import view.View;
import controller.KeyboardController;
import controller.UserController;
import development.Coord2D;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

public class AnimationUI {
  private static Marker source;
  private static View unfoldingView;
  private static JFrame frame;
  private static UserController control;
  private static FaceAppearanceScheme faceAppearanceScheme;
  private static boolean developerMode;

  public static void main(String[] args) {
    // This is a good option to set if you wan't more information about what
    // JReality does behind the scenes
    // JOGLConfiguration.getLogger().setLevel(Level.INFO);

    // Note: For correct initialization, it is important the method calls listed
    // below occur in the particular order listed.
    developerMode = true;
    initModel();
    initViews();
    initControls();
    runSimulation();
  }

  private static void initControls() {
  control = new KeyboardController(source);

  }

  private static void runSimulation() {

    final long dt = 10; // Timestep size, in microseconds
    long startTime = System.currentTimeMillis();
    long currentTime = startTime;
    long accumulator = 0;
    Thread t = new Thread(control);
    t.start();

    control.clear();
    while (true) {

      long newTime = System.currentTimeMillis();
      long frameTime = newTime - currentTime;

      currentTime = newTime;
      accumulator += frameTime;

      while (accumulator >= dt) {

        //Face prev = source.getPosition().getFace();
        control.runNextAction();
       // if(Vector.closeToAnyOf(vectors, v, epsilon))
        accumulator -= dt;
      }
      unfoldingView.updateGeometry();
      unfoldingView.updateScene();
    }
  }

  private static void initViews() {
    faceAppearanceScheme = new FaceAppearanceScheme();
    unfoldingView = new UnfoldingView(source, faceAppearanceScheme);
    unfoldingView.updateGeometry();
    unfoldingView.initializeNewManifold();
    unfoldingView.updateScene();

    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setLocation(40, 40);
    frame.setResizable(true);

    Dimension size = new Dimension(400, 400);
    Container contentPane = frame.getContentPane();
    contentPane
        .add((Component) unfoldingView.getViewer().getViewingComponent());
    contentPane.setMinimumSize(size);
    contentPane.setPreferredSize(size);
    contentPane.setMaximumSize(size);
    frame.pack();
    frame.validate();
    frame.setVisible(true);
  }

  private static void initModel() {
    String file = "Data/surfaces/dodec2.off";

    String extension = file.substring(file.length() - 3, file.length());

    if (extension.contentEquals("off")) {
      EmbeddedTriangulation.readEmbeddedSurface(file);
    } else {
      System.err
          .println("Invalid file. This view supports embedded polytopes only.");
      System.exit(1);
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

    ManifoldPosition mp = new ManifoldPosition(sourceFace, sourcePoint);
    source = new Marker(mp, new MarkerAppearance(), Marker.MarkerType.SOURCE);
  }
}
