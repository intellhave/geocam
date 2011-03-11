package development;

import geoquant.Radius;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Matrix;
import view.NodeImage;
import view.SourceNodeImage;
import de.jreality.math.Rn;

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

public class Development extends Observable {

  private AffineTransformation rotation = new AffineTransformation(2);
  private DevelopmentNode root;
  private Vector sourcePoint;
  private Vector direction = new Vector(1, 0);
  private Vector left = new Vector(0, 1);
  private Face sourceFace;
  private int maxDepth;
  private ArrayList<Node> nodeList = new ArrayList<Node>();
  private ArrayList<FadingNode> bulletList = new ArrayList<FadingNode>();
  private Node sourcePointNode;
  private double radius; // default radius of objects
  private double units_per_millisecond = 0.0004;

  public boolean building = false;

  public Development(Face sourceF, Vector sourcePt, int depth, double radius) {
    this.radius = radius;
    maxDepth = depth;
    sourcePoint = sourcePt;
    sourceFace = sourceF;
    System.out.println("source point = " + sourcePoint);
    sourcePointNode = new Node(Color.blue, sourceFace, sourcePoint,
        units_per_millisecond, radius);
    sourcePointNode.setRadius(radius);
    synchronized (nodeList) {
      nodeList.add(sourcePointNode);
    }

    // add nodes in random faces, to be shot
    Color[] colors = { Color.red, Color.green, Color.orange, Color.cyan };
    Integer[] keyList = new Integer[Triangulation.faceTable.keySet().size()];// =
                                                                             // (Integer[])(Triangulation.faceTable.keySet().toArray());
    Triangulation.faceTable.keySet().toArray(keyList);
    Random rand = new Random();
    for (int i = 0; i < 4; i++) {
      // get random index of face in which to place object
      int index = rand.nextInt(keyList.length);
      Face f = Triangulation.faceTable.get(index);

      // get point inside face
      Vector p = new Vector(0, 0);
      Iterator<Vertex> iv = f.getLocalVertices().iterator();
      while (iv.hasNext()) {
        p.add(Coord2D.coordAt(iv.next(), f));
      }
      p.scale(1.0f / 3.0f);

      // create node
      Node n = new Node(colors[i], f, p, units_per_millisecond, radius);
      n.setRadius(radius);

      // set random movement direction
      Vector move = new Vector(Math.random(), Math.random());
      move.normalize();
      n.setMovement(move);
      synchronized (nodeList) {
        nodeList.add(n);
      }
    }
    buildTree();
  }

  public void rebuild(Face sourceF, Vector sourcePt, int depth) {
    maxDepth = depth;
    sourcePoint = sourcePt;
    sourceFace = sourceF;

    synchronized (nodeList) {
      nodeList.clear();
    }

    sourcePointNode = new Node(Color.blue, sourceFace, sourcePoint,
        units_per_millisecond, radius);
    sourcePointNode.setRadius(radius);
    synchronized (nodeList) {
      nodeList.add(sourcePointNode);
    }

    Node n = new Node(Color.red, sourceFace, sourcePoint,
        units_per_millisecond, radius);
    n.setRadius(radius);
    n.setMovement(new Vector(1, 0));
    synchronized (nodeList) {
      nodeList.add(n);
    }

    buildTree();
    setChanged();
    notifyObservers("surface");
  }

  public void setDepth(int depth) {
    maxDepth = depth;
    buildTree();
    setChanged();
    notifyObservers("depth");
  }

  public void setRadius(double r) {
    radius = r;
    sourcePointNode.setRadius(r);

    root.updateObjects();
    setChanged();
    notifyObservers("objects");
  }

  public void moveObjects(double elapsedTime) {
    ArrayList<Node> removeList = new ArrayList<Node>();
    synchronized (nodeList) {
      for (Node node : nodeList) {
        if (node instanceof FadingNode && ((FadingNode) node).isDead())
          removeList.add(node);
        else
          node.move(elapsedTime);
      }
      nodeList.removeAll(removeList);
    }

    ArrayList<FadingNode> bulletRemoveList = new ArrayList<FadingNode>();
    synchronized (bulletList) {
      for (FadingNode bullet : bulletList) {
        if (bullet.isDead()) {
          bulletList.remove(bullet);
          return;
        }
        bullet.move(elapsedTime);
        Node target = hitObject(bullet);
        if (target != null) {
          bulletRemoveList.add(bullet);
          nodeList.remove(target);
        }
      }
    }
    if (building)
      return;

    root.updateObjects();

    setChanged();
    notifyObservers("objects");
  }

  /*
   * returns the Node hit by the given bullet, if it exists otherwise, returns
   * null
   */
  private Node hitObject(Node bullet) {
    double[] bulletPos = bullet.getPosition().getVectorAsArray();
    for (Node node : nodeList) {
      if (node.getFace().equals(bullet.getFace())) {
        double[] objectPos = node.getPosition().getVectorAsArray();
        if (Rn.euclideanDistance(bulletPos, objectPos) < node.getRadius()) {
          System.out.println("HIIIIIIIIT!!!!");
          return node;
        }
      }
    }
    return null;
  }

  /*
   * Places a new fading node at the source point, with specified movement
   * direction
   */
  public void addNodeAtSource(Color color, Vector vector) {
    
    rotation = getRotationInverse();
    vector = rotation.affineTransVector(vector);

    FadingNode node = new FadingNode(color, sourceFace, sourcePoint,
        units_per_millisecond, radius);
    node.setMovement(vector);
    synchronized (nodeList) {
      nodeList.add(node);
    }
  }

  public void addBulletAtSource(Color color, Vector vector) {
    
    rotation = getRotationInverse();
    vector = rotation.affineTransVector(vector);

    FadingNode bullet = new FadingNode(color, sourceFace, sourcePoint,
        units_per_millisecond, radius / 3);
    bullet.setMovement(vector);
    synchronized (bulletList) {
      bulletList.add(bullet);
    }
  }

  public void addVertexNode(Color color, Vertex v) {
    Face sourceFace = null;
    for (Face f : v.getLocalFaces()) {
      sourceFace = f;
      break;
    }

    Node node = new Node(color, sourceFace, Coord2D.coordAt(v, sourceFace),
        units_per_millisecond, radius);
    // System.err.println("1 "+ node.getRadius());
    node.setRadius(Radius.valueAt(v));
    // node.setRadius(2);
    node.setTransparency(.1);
    synchronized (nodeList) {
      nodeList.add(node);
    }
    buildTree();
    // int last = nodeList.size()-1;
    // System.err.println("3 "+ nodeList.get(last).getRadius());
    // this.rebuild(this.sourceFace, this.sourcePoint, this.maxDepth);
  }

  // ------------ Getters and Setters ------------

  public DevelopmentNode getRoot() {
    return root;
  }

  public Vector getSourcePoint() {
    return sourcePoint;
  }

  public int getDepth() {
    return maxDepth;
  }

  public void setVelocity(double v) {
    units_per_millisecond = v;
    sourcePointNode.setVelocity(v);
  }

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

    notifyObservers("rotation");
  }

  /*
   * Assumes direction is normalized. Moves source point in direction of
   * direction vector, by distance given by scaleVal*units_per_millisecond.
   * (scaleVal has units of milliseconds)
   */
  public void translateSourcePoint(double scaleVal) {
    Vector movement = new Vector(direction);
    movement.scale(scaleVal * units_per_millisecond);

    movement.add(sourcePoint);
    computeEnd(movement, sourceFace, null);
    setSourcePoint(sourcePoint); // notifies observers
  }
  
  public void rebuild(){
    buildTree();
    setChanged();
  }
  
  /*
   * Assumes direction and left are both normalized; moves specified number of units.
   * (this is used in DevelopmentViewCave; the VR viewer has movement built in,
   *  this just translates movement in VR space to movement in manifold)
   */
  public void translateSourcePoint(double dForward, double dLeft) {

    Vector movement = new Vector(sourcePoint);
    movement.add(Vector.scale(direction, dForward));
    movement.add(Vector.scale(left, dLeft));
    
    computeEnd(movement, sourceFace, null);
    setSourcePoint(sourcePoint); // notifies observers
  }

  /*
   * Traces geodesic in direction of point, applying the appropriate affine
   * transformation whenever it crosses an edge. When a face is found containing
   * the transformed point, these become the new source face and point.
   * ignoreEdge is the edge just crossed, so don't want to cross it again.
   */
  private void computeEnd(Vector point, Face face, Edge ignoreEdge) {

    // see if current face contains point
    Vector l = DevelopmentComputations.getBarycentricCoords(point, face);
    double l1 = l.getComponent(0);
    double l2 = l.getComponent(1);
    double l3 = l.getComponent(2);
    if (l1 >= 0 && l1 < 1 && l2 >= 0 && l2 < 1 && l3 >= 0 && l3 < 1) {
      sourceFace = face;
      sourcePoint = new Vector(point);
      return;
    }

    // find which edge vector intersects to get next face
    // (currently not handling vector through vertex)
    boolean foundEdge = false;
    Edge edge = null;
    List<Edge> edges = face.getLocalEdges();

    for (int i = 0; i < edges.size(); i++) {
      edge = edges.get(i);
      if (ignoreEdge != null && edge.equals(ignoreEdge))
        continue;

      Vector v1 = Coord2D.coordAt(edge.getLocalVertices().get(0), face);
      Vector v2 = Coord2D.coordAt(edge.getLocalVertices().get(1), face);

      Vector edgeDiff = Vector.subtract(v1, v2);
      Vector sourceDiff = Vector.subtract(sourcePoint, v2);
      Vector pointDiff = Vector.subtract(point, v2);
      Vector intersection = Vector.findIntersection(sourceDiff, pointDiff,
          edgeDiff);
      if (intersection != null) {
        foundEdge = true;
        break;
      }
    }

    if (foundEdge) {
      Face nextFace = null;

      List<Face> faces = edge.getLocalFaces();
      if (faces.get(0).equals(face))
        nextFace = faces.get(1);
      else
        nextFace = faces.get(0);

      // get transformation taking current face to next
      AffineTransformation trans = CoordTrans2D.affineTransAt(face, edge);
      Vector newPoint = trans.affineTransPoint(point);
      direction = trans.affineTransVector(direction);
      left = trans.affineTransVector(left);

      computeEnd(newPoint, nextFace, edge);

    } else {
      System.err.println("computeEnd: did not find edge\n");
    }
  }

  public void setSourcePoint(Vector point) {
    sourcePoint = point;
    synchronized (nodeList) {
      nodeList.remove(sourcePointNode);
    }
    double radius = sourcePointNode.getRadius();
    sourcePointNode = new Node(Color.blue, sourceFace, sourcePoint,
        units_per_millisecond, radius);
    sourcePointNode.setRadius(radius);
    synchronized (nodeList) {
      nodeList.add(sourcePointNode);
    }

    buildTree();

    setChanged();
    notifyObservers("source");
  }

  private void buildTree() {
    // get transformation taking sourcePoint to origin (translation by
    // -1*sourcePoint)
    AffineTransformation t = new AffineTransformation(Vector.scale(sourcePoint,
        -1));

    rotation = getRotationInverse();
    t.leftMultiply(rotation);

    // EmbeddedFace transformedFace = t.affineTransFace(sourceFace);
    root = new DevelopmentNode(null, sourceFace, null, t);

    // continue development across each adjacent edge
    List<Vertex> vertices = sourceFace.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      Edge edge = DevelopmentComputations.findSharedEdge(v0, v1);

      // build frustum through edge end-points
      Vector vect0 = t.affineTransPoint(Coord2D.coordAt(v0, sourceFace));
      Vector vect1 = t.affineTransPoint(Coord2D.coordAt(v1, sourceFace));
      Frustum2D frustum = new Frustum2D(vect1, vect0);

      Face newFace = DevelopmentComputations.getNewFace(sourceFace, edge);
      // System.out.println("gogo"+newFace);
      if (newFace != null) {
        buildTree(root, newFace, edge, frustum, t, 1);
      }
    }
  }
  
  private void buildTree(DevelopmentNode parent, Face face, Edge sourceEdge,
      Frustum2D frustum, AffineTransformation t, int depth) {

    AffineTransformation newTrans = new AffineTransformation(2);
    // System.err.println("lala"+face);
    AffineTransformation coordTrans = CoordTrans2D.affineTransAt(face,
        sourceEdge);
    newTrans.leftMultiply(coordTrans);
    newTrans.leftMultiply(t);

    EmbeddedFace clippedFace = frustum.clipFace(newTrans.affineTransFace(face));
    if (clippedFace == null) {
      return;
    }

    DevelopmentNode node = new DevelopmentNode(parent, face, frustum, newTrans);
    parent.addChild(node);

    if (depth >= maxDepth)
      return;
    
    // continue developing across each edge
    List<Vertex> vertices = face.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      Vertex v2 = vertices.get((i + 2) % vertices.size());

      Edge edge = DevelopmentComputations.findSharedEdge(v0, v1);
      if (edge.equals(sourceEdge))
        continue;

      Vector vect0 = newTrans.affineTransPoint(Coord2D.coordAt(v0, face));
      Vector vect1 = newTrans.affineTransPoint(Coord2D.coordAt(v1, face));
      Vector vect2 = newTrans.affineTransPoint(Coord2D.coordAt(v2, face));

      Frustum2D newFrustum = DevelopmentComputations.getNewFrustum(frustum,
          vect0, vect1, vect2);

      Face newFace = DevelopmentComputations.getNewFace(face, edge);

      if (newFrustum != null && newFace != null)
        buildTree(node, newFace, edge, newFrustum, newTrans, depth + 1);
    }
  }
  
  public Vector getManifoldVector(double componentForward, double componentLeft){
    Vector v = new Vector(0,0);
    v.add(Vector.scale(direction,componentForward));
    v.add(Vector.scale(left,componentLeft));
    return v;
  }

  public AffineTransformation getTranslation() {
    return new AffineTransformation(Vector.scale(sourcePoint, -1));
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
  
  /*public AffineTransformation getRotationInverse() {
    // get transformation taking sourcePoint to origin (translation by
    // -1*sourcePoint)

    // rotation matrix sending direction -> (1,0)
    double x = direction.getComponent(0);
    double y = direction.getComponent(1);
    Matrix M = new Matrix(new double[][] { new double[] { x, y },
        new double[] { -y, x } });
    rotation = new AffineTransformation(M);

    AffineTransformation toReturn = null;
    try {
      toReturn = new AffineTransformation(M.inverse());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return toReturn;
    
  }*/

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
    private EmbeddedFace embeddedFace = null;
    private Face face;
    private AffineTransformation affineTrans;
    private ArrayList<DevelopmentNode> children = new ArrayList<DevelopmentNode>();
    private ArrayList<NodeImage> containedObjects = new ArrayList<NodeImage>();
    private ArrayList<Trail> containedTrails = new ArrayList<Trail>();
    private Frustum2D frustum;
    private int depth;

    public DevelopmentNode(DevelopmentNode prev, Face f, Frustum2D frust,
        AffineTransformation at) {
      frustum = frust;
      if (prev == null)
        depth = 0;
      else
        depth = prev.getDepth() + 1;
      face = f;
      affineTrans = at;

      updateObjects();
    }

    public void updateObjects() {
      containedObjects.clear();
      containedTrails.clear();
      synchronized (nodeList) {
        for (Node node : nodeList) {
          if (node.getFace().equals(face)) {
            Vector point = node.getPosition();

            Vector transPoint = affineTrans.affineTransPoint(point);
            Vector transPoint2d = new Vector(transPoint.getComponent(0),
                transPoint.getComponent(1));

            if (isRoot() || frustum.checkInterior(transPoint2d)) {
              // containment alg does not work for root
              if (node.equals(sourcePointNode)) {
                containedObjects.add(new SourceNodeImage(node, affineTrans));
              } else
                containedObjects.add(new NodeImage(node, new Vector(
                    transPoint2d)));
            }
          }

          if (node instanceof FadingNode) {
            for (Trail trail : ((FadingNode) node).getAllTrails()) {
              if (trail.getFace().equals(face)) {
                Vector transStart = affineTrans.affineTransPoint(trail
                    .getStart());
                Vector transStart2d = new Vector(transStart.getComponent(0),
                    transStart.getComponent(1));
                Vector transEnd = affineTrans.affineTransPoint(trail.getEnd());
                Vector transEnd2d = new Vector(transEnd.getComponent(0),
                    transEnd.getComponent(1));
                Trail clippedTrail;

                if (frustum == null) {
                  clippedTrail = new Trail(transStart2d, transEnd2d, face,
                      trail.color);
                } else {
                  clippedTrail = frustum.clipTrail(transStart2d, transEnd2d,
                      face, trail.color);
                }
                if (clippedTrail != null)
                  containedTrails.add(clippedTrail);
              }
            }
          }
        }
      }

      synchronized (bulletList) {
        for (Node bullet : bulletList) {
          if (bullet.getFace().equals(face)) {
            Vector point = bullet.getPosition();

            Vector transPoint = affineTrans.affineTransPoint(point);
            Vector transPoint2d = new Vector(transPoint.getComponent(0),
                transPoint.getComponent(1));

            if (isRoot() || frustum.checkInterior(transPoint2d)) {
              // containment alg does not work for root
              containedObjects.add(new NodeImage(bullet, new Vector(
                  transPoint2d)));
            }
          }
        }
      }

      for (DevelopmentNode child : children) {
        child.updateObjects();
      }
    }

    public void addChild(DevelopmentNode node) {
      children.add(node);
    }

    public void removeChild(DevelopmentNode node) {
      children.remove(node);
    }

    public EmbeddedFace getEmbeddedFace() {
      if (embeddedFace == null) {
        if (frustum == null)
          embeddedFace = affineTrans.affineTransFace(face);
        else
          embeddedFace = frustum.clipFace(affineTrans.affineTransFace(face));
      }
      return embeddedFace;
    }

    public Face getFace() {
      return face;
    }

    public int getDepth() {
      return depth;
    }

    public AffineTransformation getAffineTransformation() {
      return affineTrans;
    }

    public ArrayList<DevelopmentNode> getChildren() {
      return new ArrayList<DevelopmentNode>(children);
    }

    public ArrayList<NodeImage> getObjects() {
      return containedObjects;
    }

    public ArrayList<Trail> getTrails() {
      return containedTrails;
    }

    public boolean isRoot() {
      return depth == 0;
    }

    public boolean faceIsSource() {
      return face.equals(sourceFace);
    }
  }
}
