package development;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import objects.FixedObject;
import objects.ManifoldPosition;
import objects.ObjectAppearance;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;
import util.Matrix;

/****************************************
 * Development.java
 * 
 * @author K. Kiviat
 * 
 *         Overview: The development is encoded as a linked tree of development
 *         nodes. A development structure contains: * the root of the tree
 *         (pointing to a development node * the source point in the realization
 *         of the triangulation * list of nodes living on the triangulation *
 *         default radius of nodes (for when they are rendered) * maximum depth
 *         is maximum height of tree * step size for walking * initial
 *         position/orientation when rendering is specified by a source point,
 *         source face, rotation * initial direction for walking * building flag
 *         to avoid updating objects while tree is being rebuilt *
 *         units_per_millisecond default velocity for objects
 * 
 *         See below for description of Development Node
 * 
 */

public class Development {

  /*TODO (Timing)*/ private static final int TASK_TYPE_BUILDTREE = TimingStatistics.generateTaskTypeID("Development.buildTree");
  
  private AffineTransformation rotation = new AffineTransformation(2);
  private DevelopmentNode root;
  
  private ManifoldPosition source;
  private Vector direction = new Vector(1, 0);
  private Vector left = new Vector(0, 1);

  private FixedObject sourceObject; //fixed object at source point

  private int maxDepth;
  
  //private static final boolean VERBOSE_LOCK_DENIED = true;
  //private static final boolean VERBOSE_LOCK_GRANTED = true;
  //private static final boolean VERBOSE_LOCK_RELEASE = true;
  //private static final boolean VERBOSE_LOCK_REQUEST = true;
  //Thread lockOwner = null;
  //String lockReason = "";
  //boolean locked = false;
  
  private boolean locked = false;
  

  //-- 'custom obsevable' code -----------------------
  public interface DevelopmentViewer{
    public abstract void updateDevelopment(); 
  }
  LinkedList<DevelopmentViewer> viewers = new LinkedList<DevelopmentViewer>();
  
  private void notifyViewers(){
    for(DevelopmentViewer dv : viewers){
      if(dv != null){ dv.updateDevelopment(); }
    }
  }
  
  public void addViewer(DevelopmentViewer dv){ viewers.add(dv); }
  public void removeViewer(DevelopmentViewer dv){ viewers.remove(dv); }
  //-----------------------------------------------

  public Development(ManifoldPosition sourcePoint, int depth, double radius) {

    maxDepth = depth;
    source = sourcePoint;
    sourceObject = new FixedObject(source, new ObjectAppearance(radius, Color.BLACK, 0.2));
    rebuild();
  }

  public void rebuild(ManifoldPosition sourcePoint, int depth) {
    
    maxDepth = depth;
    source = sourcePoint;
    sourceObject.setManifoldPosition(source);
    rebuild();
  }

  public void setDepth(int depth) {
    
    maxDepth = depth;
    rebuild();
  }
  
  public boolean lock(){
    if( locked == true ){ return false; }
    locked = true;
    return true;
  }
  
  public void unlock(){
    locked = false;
  }
  
  /*public synchronized boolean lock(Thread requestingThread, String reason){ 
    
    if(VERBOSE_LOCK_REQUEST){
      System.out.println("Lock requested by " + requestingThread.getName() + " for \"" + reason + "\".");
    }
    
    if(locked){
      try{ wait(1000); }
      catch(InterruptedException e){ }
    }
    
    if(!locked){
      locked = true;
      lockOwner = requestingThread;
      if(reason == null){ lockReason = "(None)"; }
      else{ lockReason = reason; }
      if(VERBOSE_LOCK_GRANTED){
        System.out.println("Lock granted to " + lockOwner.getName() + " for \"" + lockReason + "\".");
      }
      return true;
    }else{
      if(VERBOSE_LOCK_DENIED){
        System.err.println("Access to development denied:");
        System.err.println(" - Request by " + requestingThread.getName() + " for \"" + reason + "\".");
        System.err.println(" - Locked by " + lockOwner.getName() + " for \"" + lockReason + "\".");
      }
      return false;
    }
  }
  
  public synchronized boolean unlock(Thread requestingThread){
    if(requestingThread.equals(lockOwner)){
      if(VERBOSE_LOCK_RELEASE){
        System.out.println("Lock released by " + lockOwner.getName() + " for \"" + lockReason + "\".");
      }
      locked = false;
      lockOwner = null;
      lockReason = "";
      notifyAll();
      return true;
    }
    return false;
  }*/

  // ------------ Getters and Setters ------------

  public DevelopmentNode getRoot() { return root; }
  public ManifoldPosition getSource() { return source; }
  public FixedObject getSourceObject() { return sourceObject; }
  public int getDepth() { return maxDepth; }

  // ---------------------------------------------

  /*
   * rotate CCW WRT [forward, left] ordered basis
   */
  public void rotate(double angle) {
    double cos = Math.cos(-angle);
    double sin = Math.sin(-angle);
    
    double x,y,x_new,y_new;
    
    x = direction.getComponent(0);
    y = direction.getComponent(1);
    x_new = cos * x - sin * y;
    y_new = sin * x + cos * y;
    direction = new Vector(x_new, y_new);
    
    x = left.getComponent(0);
    y = left.getComponent(1);
    x_new = cos * x - sin * y;
    y_new = sin * x + cos * y;
    left = new Vector(x_new, y_new);

    rebuild();
  }

  /*
   * Assumes direction is normalized. Moves source point in direction of
   * direction vector, by distance given by scaleVal*units_per_millisecond.
   * (scaleVal has units of milliseconds)
   */
  
  public void rebuild(){
    buildTree();
    notifyViewers();
  }
  
  /*
   * Assumes direction and left are both normalized; moves specified number of units.
   * (this is used in DevelopmentViewCave; the VR viewer has movement built in,
   *  this just translates movement in VR space to movement in manifold)
   */
  public void translateSourcePoint(double dForward, double dLeft) {

    Vector dx = new Vector(0,0);
    dx.add(Vector.scale(direction, dForward));
    dx.add(Vector.scale(left, dLeft));
    
    source.move(dx,direction,left);
    
    sourceObject.setManifoldPosition(source);
    rebuild();
  }

  private void buildTree() {

    /*TODO (Timing)*/ long taskID;
    /*TODO (Timing)*/ taskID = TimingStatistics.startTask(TASK_TYPE_BUILDTREE);
    
    // get transformation taking sourcePoint to origin (translation by
    // -1*sourcePoint)
    AffineTransformation t = new AffineTransformation(Vector.scale(source.getPosition(), -1));

    rotation = getRotationInverse();
    t.leftMultiply(rotation);

    EmbeddedFace transformedFace = t.affineTransFace(source.getFace());
    root = new DevelopmentNode(null, source.getFace(), null, transformedFace, t);

    // continue development across each adjacent edge
    List<Vertex> vertices = source.getFace().getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      Edge edge = DevelopmentComputations.findSharedEdge(v0, v1);

      // build frustum through edge end-points
      Vector vect0 = t.affineTransPoint(Coord2D.coordAt(v0, source.getFace()));
      Vector vect1 = t.affineTransPoint(Coord2D.coordAt(v1, source.getFace()));
      Frustum2D frustum = new Frustum2D(vect1, vect0);

      Face newFace = DevelopmentComputations.getNewFace(source.getFace(), edge);
      // System.out.println("gogo"+newFace);
      if (newFace != null) {
        buildTree(root, newFace, edge, frustum, t, 1);
      }
    }
    
    /*TODO (Timing)*/ TimingStatistics.endTask(taskID);
  }

  private void buildTree(DevelopmentNode parent, Face face, Edge sourceEdge,
      Frustum2D frustum, AffineTransformation t, int depth) {

    AffineTransformation newTrans = new AffineTransformation(2);
    // System.err.println("lala"+face);
    AffineTransformation coordTrans = CoordTrans2D.affineTransAt(face,
        sourceEdge);
    newTrans.leftMultiply(coordTrans);
    newTrans.leftMultiply(t);
    
    //get the transformed face vertices and clipped face
    EmbeddedFace transFace = newTrans.affineTransFace(face);
    EmbeddedFace clippedFace = frustum.clipFace(transFace);
    if (clippedFace == null) {
      return;
    }

    DevelopmentNode node = new DevelopmentNode(parent, face, frustum, clippedFace, newTrans);
    parent.addChild(node);

    if (depth >= maxDepth)
      return;
    
    // continue developing across each edge
    List<Vertex> vertices = face.getLocalVertices();
    for (int i=0; i < vertices.size(); i++) {
      int j = (i+1)%vertices.size();
      int k = (i+2)%vertices.size();

      Edge edge = DevelopmentComputations.findSharedEdge(vertices.get(i), vertices.get(j));
      if (edge.equals(sourceEdge))
        continue;

      Vector vect0 = transFace.getVectorAt(i);
      Vector vect1 = transFace.getVectorAt(j);
      Vector vect2 = transFace.getVectorAt(k);

      Frustum2D newFrustum = DevelopmentComputations.getNewFrustum(frustum, vect0, vect1, vect2);
      Face newFace = DevelopmentComputations.getNewFace(face, edge);
      if ((newFrustum == null) || (newFace == null)){ continue; }
      
      buildTree(node, newFace, edge, newFrustum, newTrans, depth + 1);
    }
  }

  public Vector getManifoldVector(double componentForward, double componentLeft){
    Vector v = new Vector(0,0);
    v.add(Vector.scale(direction,componentForward));
    v.add(Vector.scale(left,componentLeft));
    return v;
  }

  public AffineTransformation getRotationInverse() {
    
    //rotation matrix sending direction -> (1,0), left -> (0,1)
    //assumes det(dir, left) = 1
    
    Matrix M = new Matrix(new double[][]{
       new double[] { left.getComponent(1), -left.getComponent(0)  },
       new double[] { -direction.getComponent(1), direction.getComponent(0) }
    });
    //double det = direction.getComponent(0)*left.getComponent(1)-direction.getComponent(1)*left.getComponent(0);
    //M.scaleMatrix(1/det);
    
    return new AffineTransformation(M);
  }
  
  // ================== DevelopmentNode ==================
  /*
   * DevelopmentNode
   * 
   * Overview: Tree describing the development is encoded by linked development
   * nodes.
   * 
   * Each development node contains: * Face in the triangulation * Embedded
   * face, which is the face clipped by frustum boundary * affine transformation
   * to place embedded face in the scene * pointers to children * list of object
   * images contained in embedded face (nodes, trails) * frustum the embedded
   * face is contained in (used to clip face) * depth of node in current tree
   */

  public class DevelopmentNode {
    private EmbeddedFace clippedFace = null;
    private Face face;
    private AffineTransformation affineTrans;
    private ArrayList<DevelopmentNode> children = new ArrayList<DevelopmentNode>();
    private Frustum2D frustum;
    private int depth;

    public DevelopmentNode(DevelopmentNode prev, Face f, Frustum2D frust, EmbeddedFace cf, AffineTransformation at) {
      
      if (prev == null){ depth = 0; }
      else{  depth = prev.getDepth() + 1; }
      
      frustum = frust;
      face = f;
      clippedFace = cf;
      affineTrans = at;
    }

    public void addChild(DevelopmentNode node) { children.add(node); }
    public void removeChild(DevelopmentNode node) { children.remove(node); }
    
    public EmbeddedFace getClippedFace() {
      
      if(clippedFace == null){
        //generate the clippedFace from frustum and affineTrans
        if(frustum == null){ clippedFace = affineTrans.affineTransFace(face); }
        else{ clippedFace = frustum.clipFace(affineTrans.affineTransFace(face)); }
      }
      return clippedFace;
    }

    //accessors
    public Face getFace() { return face; }
    public int getDepth() { return depth; }
    public Frustum2D getFrustum(){ return frustum; }
    public AffineTransformation getAffineTransformation() { return affineTrans; }
    public ArrayList<DevelopmentNode> getChildren() { return new ArrayList<DevelopmentNode>(children); }

    public boolean isRoot() { return depth == 0; }
    public boolean faceIsSource() { return face.equals(source.getFace()); }
  }
}
