package development;

import java.util.LinkedList;
import java.util.List;

import markers.ManifoldPosition;
import markers.MarkerAppearance;
import markers.VisibleMarker;

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

  private AffineTransformation rotation = new AffineTransformation(2);
  private DevelopmentNode root;
  
  private ManifoldPosition source;

  private VisibleMarker sourceObject; //fixed object at source point

  private int maxDepth;

  //-- 'custom observable' code -----------------------
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
    sourceObject = new VisibleMarker(source, new MarkerAppearance());
    rebuild();
  }

  public void rebuild(){
    buildTree();
    notifyViewers();
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

  // ------------ Getters and Setters ------------

  public DevelopmentNode getRoot() { return root; }
  public ManifoldPosition getSource() { return source; }
  public VisibleMarker getSourceObject() { return sourceObject; }
  public int getDepth() { return maxDepth; }

  // ---------------------------------------------

  /*
   * rotate CCW WRT [forward, left] ordered basis
   */
  public void rotate(double angle) {
    
    source.rotateOrientation(angle);
    // jthomas: For reasons I don't understand, it is very
    // important to reset the sourceObject's manifold position.
    // I would think this ought to be covered by pointing to
    // the same ManifoldPosition object, but this isn't true. 
    sourceObject.setManifoldPosition(source);
    rebuild();
  }
  
  /*
   * Assumes direction and left are both normalized; moves specified number of units.
   * (this is used in DevelopmentViewCave; the VR viewer has movement built in,
   *  this just translates movement in VR space to movement in manifold)
   */
  public void translateSourcePoint(double dForward, double dLeft) {

    source.move(source.getDirection(dForward, dLeft));
    // jthomas: For reasons I don't understand, it is very
    // important to reset the sourceObject's manifold position.
    sourceObject.setManifoldPosition(source);
    rebuild();
    
  }

  
  public AffineTransformation getRotationInverse() {
    
    //rotation matrix sending direction -> (1,0), left -> (0,1)
    //assumes det(dir, left) = 1
    Vector F = source.getDirectionForward();
    Vector L = source.getDirectionLeft();
    Matrix M = new Matrix(new double[][]{
       new double[] {  L.getComponent(1), -L.getComponent(0) },
       new double[] { -F.getComponent(1),  F.getComponent(0) }
    });
    //double det = F.getComponent(0)*L.getComponent(1) - F.getComponent(1)*L.getComponent(0);
    //M.scaleMatrix(1/det);
    
    return new AffineTransformation(M);
  }

  private void buildTree() {

    // get transformation taking sourcePoint to origin (translation by -1*sourcePoint)
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

      if (newFace != null) {
        buildTree(root, newFace, edge, frustum, t, 1);
      }
    }
  }

  private void buildTree(DevelopmentNode parent, Face face, Edge sourceEdge,
      Frustum2D frustum, AffineTransformation t, int depth) {
  //System.out.println(face.getLocalVertices()); 
    
    AffineTransformation newTrans = new AffineTransformation(2);
    
    AffineTransformation coordTrans = CoordTrans2D.affineTransAt(face, sourceEdge);
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

}
