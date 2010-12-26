package development;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;
import util.Matrix;
import view.SGCMethods;
import de.jreality.scene.SceneGraphComponent;

public class Development extends Observable {

  private AffineTransformation rotation = new AffineTransformation(2);
  private DevelopmentNode root;
  private Vector sourcePoint;
  private Vector direction = new Vector(1, 0);
  private Face sourceFace;
  private int maxDepth;
  private double step_size = 0.05;
  private ArrayList<Node> nodeList = new ArrayList<Node>();
  private Node sourcePointNode;

  public Development(Face sourceF, Vector sourcePt, int depth, double step) {
    step_size = step;
    maxDepth = depth;
    sourcePoint = sourcePt;
    sourceFace = sourceF;
    sourcePointNode = new Node(Color.blue, sourceFace, sourcePoint);
    nodeList.add(sourcePointNode);

    Node n = new Node(Color.red, sourceFace, sourcePoint);
    n.setRadius(0.2);
    nodeList.add(n);
    buildTree();
  }

  public void rebuild(Face sourceF, Vector sourcePt, int depth) {
    maxDepth = depth;
    sourcePoint = sourcePt;
    sourceFace = sourceF;

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
  
  public void setStepSize(double size) {
    step_size = size;
  }

  public DevelopmentNode getRoot() {
    return root;
  }

  public Vector getSourcePoint() {
    return sourcePoint;
  }

  public int getDepth() {
    return maxDepth;
  }

  public void rotate(double angle) {
    double cos = Math.cos(-angle);
    double sin = Math.sin(-angle);
    double x = direction.getComponent(0);
    double y = direction.getComponent(1);

    double x_new = cos * x - sin * y;
    double y_new = sin * x + cos * y;
    direction = new Vector(x_new, y_new);

    buildTree();
    setChanged();
    notifyObservers("rotation");
  }

  // /assumes direction is normalized
  public void translateSourcePoint(String fb) {
    Vector movement = new Vector(direction);
    movement.scale(step_size);
    if (fb.equals("back"))
      movement.scale(-1);

    Vector direction3d = new Vector(movement.getComponent(0),
        movement.getComponent(1), 1);
    Matrix inverse = null;
    try {
      inverse = root.getAffineTransformation().inverse();
    } catch (Exception e) {
      e.printStackTrace();
    }
    inverse.transformVector(direction3d);
    inverse.transformVector(new Vector(direction.getComponent(0), direction
        .getComponent(1), 0));

    movement = new Vector(direction3d.getComponent(0),
        direction3d.getComponent(1));

    movement.add(sourcePoint);
    computeEnd(movement, sourceFace, null);
    setSourcePoint(sourcePoint);
  }

  /*
   * Traces geodesic in direction of point, applying the appropriate affine
   * transformation whenever it crosses an edge. When a face is found containing
   * the transformed point, these become the new source face and point.
   * ignoreEdge is the edge just crossed, so don't want to cross it again.
   */
  private void computeEnd(Vector point, Face face, Edge ignoreEdge) {

    // see if current face contains point
    Vector l = getBarycentricCoords(point, face);
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

      computeEnd(newPoint, nextFace, edge);
    } else {
      System.out.println("did not find edge\n");
    }
  }

  public void setSourcePoint(Vector point) {
    sourcePoint = point;
    nodeList.remove(sourcePointNode);
    sourcePointNode = new Node(Color.blue, sourceFace, sourcePoint);
    nodeList.add(sourcePointNode);

    buildTree();

    setChanged();
    notifyObservers("source");
  }

  private void buildTree() {
    // get transformation taking sourcePoint to origin (translation by
    // -1*sourcePoint)
    AffineTransformation t = new AffineTransformation(Vector.scale(sourcePoint,
        -1));
    Vector.scale(sourcePoint, -1);

    
    // rotation matrix sending direction -> (1,0)
    double x = direction.getComponent(0);
    double y = direction.getComponent(1);
    Matrix M = new Matrix(new double[][] { new double[] { x, y },
        new double[] { -y, x } });
    rotation = new AffineTransformation(M);

    t.leftMultiply(rotation);


    EmbeddedFace transformedFace = t.affineTransFace(sourceFace);
    root = new DevelopmentNode(null, sourceFace, transformedFace, t);
    List<Vertex> vertices = sourceFace.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      Edge edge = findSharedEdge(v0, v1);

      // build frustum through edge end-points
      Vector vect0 = t.affineTransPoint(Coord2D.coordAt(v0, sourceFace));
      Vector vect1 = t.affineTransPoint(Coord2D.coordAt(v1, sourceFace));
      Frustum2D frustum = new Frustum2D(vect1, vect0);

      Face newFace = getNewFace(sourceFace, edge);

      buildTree(root, newFace, edge, frustum, t, 1);
    }
  }

  private void buildTree(DevelopmentNode parent, Face face, Edge sourceEdge,
      Frustum2D frustum, AffineTransformation t, int depth) {

    AffineTransformation newTrans = new AffineTransformation(2);
    AffineTransformation coordTrans = CoordTrans2D.affineTransAt(face,
        sourceEdge);
    newTrans.leftMultiply(coordTrans);
    newTrans.leftMultiply(t);

    if (depth > maxDepth)
      return;

    EmbeddedFace clippedFace = frustum.clipFace(newTrans.affineTransFace(face));
    if (clippedFace == null) {
      return;
    }

    DevelopmentNode node = new DevelopmentNode(parent, face, clippedFace,
        newTrans);
    parent.addChild(node);

    if (depth == maxDepth)
      return;

    // continue developing across each edge
    List<Vertex> vertices = face.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      Vertex v2 = vertices.get((i + 2) % vertices.size());

      Edge edge = findSharedEdge(v0, v1);
      if (edge.equals(sourceEdge))
        continue;

      Vector vect0 = newTrans.affineTransPoint(Coord2D.coordAt(v0, face));
      Vector vect1 = newTrans.affineTransPoint(Coord2D.coordAt(v1, face));
      Vector vect2 = newTrans.affineTransPoint(Coord2D.coordAt(v2, face));

      Frustum2D newFrustum = getNewFrustum(frustum, vect0, vect1, vect2);

      Face newFace = getNewFace(face, edge);

      if (newFrustum != null)
        buildTree(node, newFace, edge, newFrustum, newTrans, depth + 1);
    }
  }

  /*
   * Returns the edge local to both vertices (null if no such edge exists)
   */
  private Edge findSharedEdge(Vertex v0, Vertex v1) {
    List<Edge> list0 = v0.getLocalEdges();
    List<Edge> list1 = v1.getLocalEdges();

    Edge edge = null;
    for (int j = 0; j < list0.size(); j++) {
      if (list1.contains(list0.get(j))) {
        edge = list0.get(j);
        break;
      }
    }
    return edge;
  }

  private Frustum2D getNewFrustum(Frustum2D frustum, Vector vect0,
      Vector vect1, Vector vect3) {
    // build frustum through edge end-points

    // check which is left and which is right
    Vector left = vect0;
    Vector right = vect1;

    Vector l = Vector.subtract(left, vect3);
    Vector r = Vector.subtract(right, vect3);
    Vector l3d = new Vector(l.getComponent(0), l.getComponent(1), 0);
    Vector r3d = new Vector(r.getComponent(0), r.getComponent(1), 0);
    Vector cross = Vector.cross(r3d, l3d);
    if (cross.getComponent(2) < 0) {// made the wrong choice if z-component is
                                    // negative
      left = vect1;
      right = vect0;
    }

    return Frustum2D.intersect(new Frustum2D(left, right), frustum);
  }

  private Face getNewFace(Face face, Edge edge) {
    // each edge is adjacent to 2 faces; take the one that is not the
    // current face
    Face newFace;
    List<Face> faces = edge.getLocalFaces();
    if (faces.get(0) == face)
      newFace = faces.get(1);
    else
      newFace = faces.get(0);
    return newFace;
  }

  private Vector getBarycentricCoords(Vector point, Face face) {
    // barycentric coordinates
    // point in interior if l1,l2,l3 all in (0,1)
    // point on edge if l1,l2,l3 in [0,1] with at least one 0
    // otherwise outside
    List<Vertex> vertices = face.getLocalVertices();
    Vector v1 = Coord2D.coordAt(vertices.get(0), face);
    Vector v2 = Coord2D.coordAt(vertices.get(1), face);
    Vector v3 = Coord2D.coordAt(vertices.get(2), face);

    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);
    double x3 = v3.getComponent(0);
    double y3 = v3.getComponent(1);
    double x = point.getComponent(0);
    double y = point.getComponent(1);

    Matrix T = new Matrix(new double[][] { { (x1 - x3), (x2 - x3) },
        { (y1 - y3), (y2 - y3) } });
    double det = T.determinant();

    double l1 = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / det;
    double l2 = ((y3 - y2) * (x - x3) + (x1 - x3) * (y - y3)) / det;
    double l3 = 1 - l1 - l2;

    return new Vector(l1, l2, l3);
  }

  // ///////////////////////////////////////////////////
  // DevelopmentNode
  // ///////////////////////////////////////////////////

  public class DevelopmentNode {
    private EmbeddedFace embeddedFace;
    private Face face;
    private AffineTransformation affineTrans;
    private ArrayList<DevelopmentNode> children = new ArrayList<DevelopmentNode>();
    private ArrayList<Node> containedObjects = new ArrayList<Node>();
    private DevelopmentNode parent;
    private int depth;

    public DevelopmentNode(DevelopmentNode prev, Face f, EmbeddedFace ef,
        AffineTransformation at, DevelopmentNode... nodes) {
      parent = prev;
      if (parent == null)
        depth = 0;
      else
        depth = parent.getDepth() + 1;
      embeddedFace = new EmbeddedFace(ef);
      face = f;
      affineTrans = at;
      for (int i = 0; i < nodes.length; i++) {
        children.add(nodes[i]);
      }
      
      // add any objects contained in this face
      Iterator<Node> itr = nodeList.iterator();
      while(itr.hasNext()) {
        Node node = itr.next();
        if(node.getFace().equals(face)) {
          Vector point = node.getPosition();
          Vector transPoint = affineTrans.affineTransPoint(point);
          Vector transPoint2d = new Vector(transPoint.getComponent(0),
              transPoint.getComponent(1));

          if(isRoot() || embeddedFace.contains(transPoint2d)) { 
            // containment alg does not work for root
            containedObjects.add(new Node(node.getColor(), node.getFace(), transPoint2d));
          }
        }
      }
      
    }

    public void addChild(DevelopmentNode node) { children.add(node); }
    public void removeChild(DevelopmentNode node) { children.remove(node); }
    public EmbeddedFace getEmbeddedFace() { return embeddedFace; }
    public Face getFace() { return face; }
    public int getDepth() { return depth; }
    public AffineTransformation getAffineTransformation() { return affineTrans; }
    public ArrayList<DevelopmentNode> getChildren() { return children; }
    public ArrayList<Node> getObjects() { return containedObjects; }
    
    public boolean isRoot() { return parent == null; }
    public boolean faceIsSource() { return face.equals(sourceFace); }
  }
}
