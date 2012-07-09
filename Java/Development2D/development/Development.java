package development;

import java.util.LinkedList;
import java.util.List;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;
import util.Matrix;

/*********************************************************************************
 * Development
 * 
 * Overview: The development is encoded as a linked tree of development nodes. A
 * development structure contains:
 * 
 * 1) the root of the tree (pointing to a development node)
 * 
 * 2) the source point in the realization of the triangulation
 * 
 * 3) list of nodes living on the triangulation
 * 
 * 4) default radius of nodes (for when they are rendered)
 * 
 * 5) maximum depth is maximum height of tree
 * 
 * 6) step size for walking
 * 
 * 7) initial position/orientation when rendering is specified by a source
 * point, source face, rotation
 * 
 * 8) initial direction for walking
 * 
 * 9) building flag to avoid updating objects while tree is being rebuilt
 * 
 * 10) units_per_millisecond default velocity for objects
 * 
 * See below for description of Development Node
 * 
 *********************************************************************************/

public class Development {

  private AffineTransformation rotation = new AffineTransformation(2);
  private DevelopmentNode root;
  private ManifoldPosition source;  
  private int maxDepth;

  LinkedList<DevelopmentViewer> viewers = new LinkedList<DevelopmentViewer>();

  public Development(ManifoldPosition sourcePoint, int depth, double radius) {
    maxDepth = depth;
    source = sourcePoint;    
    rebuild();
  }

  public void rebuild() {
    buildTree();
    notifyViewers();
  }

  public void rebuild(ManifoldPosition sourcePoint, int depth) {
    maxDepth = depth;
    source = sourcePoint;
    rebuild();
  }

  public int getDepth() {
    return maxDepth;
  }

  public synchronized void setDepth(int depth) {
    maxDepth = depth;
    rebuild();
  }

  public DevelopmentNode getRoot() {
    return root;
  }

  public ManifoldPosition getSource() {
    return source;
  }

  /*********************************************************************************
   * rotate
   * 
   * This method rotates the development counterclockwise with respect to the
   * usual (forward, left) ordered basis.
   *********************************************************************************/
  public void rotate(double angle) {
    source.rotateOrientation(angle);
    rebuild();
  }

  /*********************************************************************************
   * translateSourcePoint
   * 
   * This method moves the source point, according to units given by the ordered
   * frame we store in development. In other words, the new position is
   * 
   * oldPosition + dForward * [Forward Vector] + dLeft * [Left Vector]
   * 
   * This is used in DevelopmentViewCave; the VR viewer has movement built in,
   * this just translates movement in VR space to movement in manifold.
   *********************************************************************************/
  public void translateSourcePoint(double dForward, double dLeft) {
    source.move(source.getDirection(dForward, dLeft));
    rebuild();
  }

  private AffineTransformation getRotationInverse() {

    // rotation matrix sending direction -> (1,0), left -> (0,1)
    // assumes det(dir, left) = 1
    Vector F = source.getDirectionForward();
    Vector L = source.getDirectionLeft();
    Matrix M = new Matrix(new double[][] {
        new double[] { L.getComponent(1), -L.getComponent(0) },
        new double[] { -F.getComponent(1), F.getComponent(0) } });
    // double det = F.getComponent(0)*L.getComponent(1) -
    // F.getComponent(1)*L.getComponent(0);
    // M.scaleMatrix(1/det);

    return new AffineTransformation(M);
  }

  /*********************************************************************************
   * buildTree
   * 
   * This method is responsible for starting a series of recursive function
   * calls that will construct the development and record it in our data
   * structures.
   *********************************************************************************/
  private void buildTree() {
    // Get the transformation taking sourcePoint to origin.
    AffineTransformation t = new AffineTransformation(Vector.scale(
        source.getPosition(), -1));

    rotation = getRotationInverse();
    t.leftMultiply(rotation);

    EmbeddedFace transformedFace = t.affineTransFace(source.getFace());
    root = new DevelopmentNode(null, source.getFace(), null, transformedFace, t);

    // Continue development across each adjacent edge.
    List<Vertex> vertices = source.getFace().getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      Edge edge = DevelopmentComputations.findSharedEdge(v0, v1);

      // Build a frustum through edge end-points
      Vector vect0 = t.affineTransPoint(Coord2D.coordAt(v0, source.getFace()));
      Vector vect1 = t.affineTransPoint(Coord2D.coordAt(v1, source.getFace()));
      Frustum2D frustum = new Frustum2D(vect1, vect0);
      Face newFace = DevelopmentComputations.getNewFace(source.getFace(), edge);
      if (newFace != null) {
        buildTree(root, newFace, edge, frustum, t, 1);
      }
    }
  }

  // This is the recursive helper method for buildTree above.
  private void buildTree(DevelopmentNode parent, Face face, Edge sourceEdge,
      Frustum2D frustum, AffineTransformation t, int depth) {
    // System.out.println(face.getLocalVertices());

    AffineTransformation newTrans = new AffineTransformation(2);

    AffineTransformation coordTrans = CoordTrans2D.affineTransAt(face,
        sourceEdge);
    newTrans.leftMultiply(coordTrans);
    newTrans.leftMultiply(t);

    // get the transformed face vertices and clipped face
    EmbeddedFace transFace = newTrans.affineTransFace(face);
    EmbeddedFace clippedFace = frustum.clipFace(transFace);
    if (clippedFace == null) {
      return;
    }

    DevelopmentNode node = new DevelopmentNode(parent, face, frustum,
        clippedFace, newTrans);
    parent.addChild(node);

    if (depth >= maxDepth)
      return;

    // continue developing across each edge
    List<Vertex> vertices = face.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      int j = (i + 1) % vertices.size();
      int k = (i + 2) % vertices.size();

      Edge edge = DevelopmentComputations.findSharedEdge(vertices.get(i),
          vertices.get(j));
      if (edge.equals(sourceEdge))
        continue;

      Vector vect0 = transFace.getVectorAt(i);
      Vector vect1 = transFace.getVectorAt(j);
      Vector vect2 = transFace.getVectorAt(k);

      Frustum2D newFrustum = DevelopmentComputations.getNewFrustum(frustum,
          vect0, vect1, vect2);
      Face newFace = DevelopmentComputations.getNewFace(face, edge);
      if ((newFrustum == null) || (newFace == null)) {
        continue;
      }

      buildTree(node, newFace, edge, newFrustum, newTrans, depth + 1);
    }
  }

  /*********************************************************************************
   * Observer/Observable Code
   * 
   * This code allows objects observing the development to receive notifications
   * when the development changes.
   *********************************************************************************/
  private void notifyViewers() {
    for (DevelopmentViewer dv : viewers) {
      if (dv != null) {
        dv.updateDevelopment();
      }
    }
  }

  public void addViewer(DevelopmentViewer dv) {
    viewers.add(dv);
  }

  public void removeViewer(DevelopmentViewer dv) {
    viewers.remove(dv);
  }
}
