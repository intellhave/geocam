package viewMKII;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import inputOutput.TriangulationIO;

import javax.swing.JFrame;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme;
import view.ColorScheme.schemes;

import controllerMKII.KeyboardController;
import controllerMKII.UserController;
import development.Coord2D;
import development.EmbeddedTriangulation;
import development.Vector;

import markers.ManifoldPosition;
import markersMKII.Marker;
import markersMKII.MarkerAppearance;
import markersMKII.MarkerHandler;

public class CookieGame {

  private static boolean developerMode = false;
  public static boolean paused;
  public static boolean gameWon;

  private static Marker source;
  private static Marker cookie;

  private static MarkerHandler markers;
  private static Development development;
  private static JFrame viewer;
  private static UserController userControl;
  private static ColorScheme colorScheme;
  private static View gameView;

  public static void main(String[] args) {
    initModel();
    initView();
    initModelControls();
    runGame();
  }

  private static String[] filenames = { "Data/off/square2.off", // 0
      "Data/off/tetra.off",// 1
      "Data/off/tetra2.off",// 2
      "Data/blender/neckpinch.off",// 3
      "Data/blender/torus.off",// 4
      "Data/blender/cube_surf.off",// 5
      "Data/off/icosa.off",// 6
      "Data/off/dodec2.off",// 7
      "Data/off/cone.off",// 8
      "Data/off/epcot.off",// 9
      "Data/off/square2.off",// 10
      "Data/Triangulations/2DManifolds/tetrahedronnonembed2.xml",// 11
      "Data/Triangulations/2DManifolds/tetrahedron2.xml",// 12
      "Data/Triangulations/2DManifolds/tetrahedronnew.xml",// 13
      "Data/Triangulations/2DManifolds/torus-9-2.xml",// 14
      "Data/Triangulations/2DManifolds/domain.xml"};//15
  private static String filename = filenames[5];

  private static void initModel() {
    loadSurface(filename);
  }

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

  private static void initMarkers() {
    Random rand = new Random();

    markers = new MarkerHandler();
    source = development.getSourceMarker();
    markers.addMarker(source);
    ManifoldPosition pos;
    MarkerAppearance app;
    pos = new ManifoldPosition(development.getSource());
    app = new MarkerAppearance(MarkerAppearance.ModelType.COOKIE, 1.0);
    double a = rand.nextDouble() * Math.PI * 2;
    Vector direction = new Vector(Math.cos(a), Math.sin(a));

    // TODO: Do we need to tweak the scaling on this?
    pos.move(Vector.scale(direction, 4));
    cookie = new Marker(pos, app);
    markers.addMarker(cookie);
  }

  private static void initView() {
    colorScheme = new ColorScheme(schemes.FACE);
    int[] framePosition = { 0, 10 };
    int[] frameSize = { 700, 700 };
    gameView = new ExponentialView(development, markers, colorScheme);
    gameView.updateGeometry();
    gameView.initializeNewManifold();
    gameView.updateScene();

    viewer = new JFrame();
    viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    viewer.setVisible(true);
    viewer.setLocation(framePosition[0], framePosition[1]);
    viewer.setResizable(false);

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

  private static void initModelControls() {
    userControl = new KeyboardController(development);
  }

  public static void runGame() {
    final long dt = 10; // Timestep size, in microseconds
    // final long maxFrameTime = 80;

    paused = false;
    gameWon = false;

    long startTime = System.currentTimeMillis();
    long currentTime = startTime;
    long accumulator = 0;
    Thread t = new Thread(userControl);
    t.start();
    while (!paused && !gameWon) {
      long newTime = System.currentTimeMillis();
      long frameTime = newTime - currentTime;

      currentTime = newTime;
      accumulator += frameTime;

      while (accumulator >= dt) {
        Face prev = development.getSource().getFace();
        userControl.runNextAction();
        Face next = development.getSource().getFace();
        if (next != prev) {
          markers.updateMarker(development.getSourceMarker(), prev);
        }
        // PATCH END

        markers.updateMarkers(dt);

        double epsilon = .25;
        if (source.getPosition().getFace() == cookie.getPosition().getFace()
            && (Vector.distanceSquared(source.getPosition().getPosition(),
                cookie.getPosition().getPosition()) < epsilon))
          gameWon = true;
        accumulator -= dt;

      }
      render();
    }
    t.interrupt();

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

  private static void render() {
    gameView.updateGeometry(true, true);
    gameView.updateScene();
  }
}
