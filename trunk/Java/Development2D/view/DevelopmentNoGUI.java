package view;

import java.util.Iterator;
import java.util.Random;

import objects.ManifoldPosition;
import objects.ObjectDynamics;
import objects.ShootingGame;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.TimingStatistics;
import development.Vector;

public class DevelopmentNoGUI  implements Development.DevelopmentViewer, ObjectDynamics.DynamicsListener {
 
  private static int INITIAL_POINT_SIZE = 4;
  private static double radius = INITIAL_POINT_SIZE/100.0;
  private static int currentDepth = 8;

  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;

  private static DevelopmentViewCave view;
  
  //--- objects ------------------------
  private static ShootingGame shootingGame;
  private static final boolean INITIAL_MOVEMENT_STATUS = true;
  private static double targetSpeed = 0.5;
  private static final double TARGET_SPEED_INCREMENT = 0.1;
  private static int nHits = 0; 
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
    
    //add random targets for the shooting game
    shootingGame = new ShootingGame(50);
    shootingGame.setTargetSpeed(targetSpeed);
    System.out.println("Initial target is moving " + targetSpeed + " units/sec.");
    shootingGame.addTarget(development.getSource(), randomUnitVector() );
    shootingGame.addListener(this);
    if(INITIAL_MOVEMENT_STATUS){ shootingGame.start(); }
    view.installShootTool(shootingGame);
    
    //make source point invisible
    //development.getSourceObject().setVisible(false);

    //initial geometry
    updateGeometry(true,true);
    view.initializeNewManifold();
  }

  private Vector randomUnitVector(){
    Random rand = new Random();
    double a = rand.nextDouble()*Math.PI*2;
    return new Vector(Math.cos(a), Math.sin(a));
  }
  
  public void dynamicsEvent(int eventID){
    
    if(eventID == ObjectDynamics.EVENT_DYNAMICS_EVOLVED){
      
      updateGeometry(false,true);

    }else if(eventID == ShootingGame.EVENT_OBJECT_HIT){
      
      //make a new target, and a little faster
      nHits++;
      targetSpeed += TARGET_SPEED_INCREMENT;
      System.out.println("Hit " + nHits + " target(s).  Now try " + targetSpeed + " units/sec!");
      shootingGame.setTargetSpeed(targetSpeed);
      shootingGame.addTarget(development.getSource(), randomUnitVector() );
    }
  }
  
  public void updateDevelopment(){
    updateGeometry(true,true);
  }
  
  private synchronized void updateGeometry(boolean dev, boolean obj){
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