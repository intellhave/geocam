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
import controller.KeyboardController;
import controller.UserController;
import development.Coord2D;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

public class AnimationUI {
  private static Marker source;
  private static UnfoldingView unfoldingView;
  private static JFrame frame;
  private static UserController control;
  private static FaceAppearanceScheme faceAppearanceScheme;

  public static void main(String[] args) {
    initModel();
    initViews();
    //display the unfolding, then give control to the user
    unfoldingView.displayUnfolding();
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
        control.runNextAction();
        accumulator -= dt;
      }      
      unfoldingView.update();
    }
  }

  private static void initViews() {
    faceAppearanceScheme = new FaceAppearanceScheme();
    unfoldingView = new UnfoldingView(source, faceAppearanceScheme);
    unfoldingView.initializeNewManifold();
    unfoldingView.update();

    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setLocation(40, 40);
    frame.setResizable(true);

    Dimension size = new Dimension(800, 800);
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
    //String file = "Data/surfaces/cube_surf.off";

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
