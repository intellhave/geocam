package view;

import java.awt.Color;
import java.util.Iterator;
import java.util.Random;

import objects.BasicMovingObjects;
import objects.ManifoldPosition;
import objects.MovingObject;
import objects.ObjectAppearance;
import objects.ObjectDynamics;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.TimingStatistics;
import development.Vector;

public class DevelopmentNoGUI  implements Development.DevelopmentViewer, ObjectDynamics.ObjectViewer {
 
  private static int INITIAL_POINT_SIZE = 4;
  private static double radius = INITIAL_POINT_SIZE/100.0;
  private static int currentDepth = 8;

  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;

  private static DevelopmentViewCave view;
  
  //--- objects ------------------------
  //private static ShootingGame shootingGame = new ShootingGame();
  private static BasicMovingObjects dynamics = new BasicMovingObjects(50);
  private static final boolean INITIAL_MOVEMENT_STATUS = false;
  private static final int MOVING_OBJECT_COUNT = 4;
  private static final double objectRadius = 0.1;
  //------------------------------------
  
  public static void main(String[] args) {
    DevelopmentNoGUI noGUI = new DevelopmentNoGUI();
  }
  
  public DevelopmentNoGUI(){

    colorScheme = new ColorScheme(schemes.FACE);

    development = null;
    String filename = "Data/off/tetra2.off";
    loadSurface(filename);

    System.out.println("====== Initializing Cave Viewer ======");
    view = new DevelopmentViewCave(development, colorScheme);
    System.out.println("---------------- done ----------------");
    
    development.addViewer(this);
    
    //make it display timing statistics on exit
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run(){ TimingStatistics.printData(); }
    });
    
    //set up objects
    development.getSourceObject().getAppearance().setRadius(objectRadius);
    Random rand = new Random();
    for(int i=0; i<MOVING_OBJECT_COUNT; i++){
      dynamics.addObject(new MovingObject( development.getSource(), new ObjectAppearance(objectRadius, randomColor(rand)), randomUnitVector(rand) ));
    }
    dynamics.addViewer(this);
    if(INITIAL_MOVEMENT_STATUS){ dynamics.start(); }

    //initial geometry
    updateGeometry(true,true);
    view.initializeNewManifold();
  }

  private Color randomColor(Random rand){
    return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
  }
  
  private Vector randomUnitVector(Random rand){
    double a = rand.nextDouble()*Math.PI*2;
    return new Vector(Math.cos(a), Math.sin(a));
  }
  
  public void updateObjects(){
    updateGeometry(false,true);
  }
  
  public void updateDevelopment(){
    updateGeometry(true,true);
  }
  
  private void updateGeometry(boolean dev, boolean obj){
    view.updateGeometry(dev,obj);
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
      development = new Development(new ManifoldPosition(sourceFace, sourcePoint), currentDepth, radius);
    else development.rebuild(new ManifoldPosition(sourceFace, sourcePoint), currentDepth);

  }

}