package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.Timer;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.TimingStatistics;
import development.Vector;

public class DevelopmentNoGUI {
 
  private static int INITIAL_POINT_SIZE = 4;
  private static double radius = INITIAL_POINT_SIZE/100.0;
  private static int currentDepth = 21;

  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;

  private static DevelopmentViewCave view;

  private static Timer moveTimer; // timer for moving objects
  
  public static void main(String[] args) {

    colorScheme = new ColorScheme(schemes.FACE);

    development = null;
    String filename = "Data/off/cone.off";
    loadSurface(filename);

    System.out.println("======= Initializing Viewer =======");
    view = new DevelopmentViewCave(development, colorScheme, radius);
    System.out.println("---------------- done ----------------");
    development.addObserver(view);
    
    moveTimer = new Timer(50, null);
    moveTimer.addActionListener(new ObjectMoveListener());
    //moveTimer.start();
    
    //make it display timing statistics on exit (maybe there's a better way to do this?)
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run(){ TimingStatistics.printData(); }
    });
  }

  /*
   * loads triangulated surface from file given by filename. Chooses an arbitrary source face
   * and source point in that face. Constructs a new Development if one hasn't already been
   * made. Otherwise rebuilds development with new surface.
   */
  private static void loadSurface(String filename) {

    EmbeddedTriangulation.readEmbeddedSurface(filename);
    
    Iterator<Integer> i = null;
    // pick some arbitrary face and source point
    i = Triangulation.faceTable.keySet().iterator();
    sourceFace = Triangulation.faceTable.get(i.next());

    sourcePoint = new Vector(0, 0);
    Iterator<Vertex> iv = sourceFace.getLocalVertices().iterator();
    while (iv.hasNext()) {
      sourcePoint.add(Coord2D.coordAt(iv.next(), sourceFace));
    }
    sourcePoint.scale(1.0f / 3.0f);

    if (development == null)
      development = new Development(sourceFace, sourcePoint, currentDepth, radius);
    else development.rebuild(sourceFace, sourcePoint, currentDepth);

  }

  public static class ObjectMoveListener implements ActionListener {
    private long time;
    public ObjectMoveListener() {
      time = System.currentTimeMillis();
    }
    public void actionPerformed(ActionEvent e) {
      long newtime = System.currentTimeMillis();
      long dt = newtime - time;      
      development.moveObjects(dt);
      time = System.currentTimeMillis();
    }
  }
}