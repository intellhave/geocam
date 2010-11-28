package development;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;
import util.Matrix;

public class Development extends Observable {
  private DevelopmentNode root;
  private Vector sourcePoint;
  private Face sourceFace;
  private int maxDepth;
  private int desiredDepth; // the depth to which observers will want to show
  private double STEP_SIZE = 0.04;

  public Development(Face sourceF, Vector sourcePt, int max, int desired) {
    maxDepth = max;
    desiredDepth = desired;
    sourcePoint = sourcePt;
    sourceFace = sourceF;

    System.out.println("building development tree");
    buildTree();
    System.out.println("done");
  }

  public void rebuild(Face sourceF, Vector sourcePt, int max, int desired) {
    maxDepth = max;
    desiredDepth = desired;
    sourcePoint = sourcePt;
    sourceFace = sourceF;

    System.out.println("building development tree");
    buildTree();
    System.out.println("done");
    setChanged();
    notifyObservers("surface");
  }

  public DevelopmentNode getRoot() {
    return root;
  }

  public Vector getSourcePoint() {
    return sourcePoint;
  }

  public void setDesiredDepth(int d) {
    desiredDepth = d;
    setChanged();
    notifyObservers("depth");
  }

  public int getDesiredDepth() {
    return desiredDepth;
  }

  // assumes magnitude of direction is 1
  public void translateSourcePoint(Vector v) {
    Vector direction = new Vector(v);
    direction.scale(STEP_SIZE);
    Vector direction3d = new Vector(direction.getComponent(0),
        direction.getComponent(1), 1);
    try {
      root.getAffineTransformation().inverse().transformVector(direction3d);
    } catch (Exception e) {
      e.printStackTrace();
    }

    direction = new Vector(direction3d.getComponent(0),
        direction3d.getComponent(1));

    sourcePoint.add(direction);

    // currently not dealing with edge case
    sourceFace = findContainingFace(sourcePoint);
    setSourcePoint(sourcePoint);
  }

  private Face findContainingFace(Vector point) {
    return findContainingFace(point, root);
  }

  private Face findContainingFace(Vector point, DevelopmentNode node) {
    Vector l = getBarycentricCoords(point, node.getFace());
    double l1 = l.getComponent(0);
    double l2 = l.getComponent(1);
    double l3 = l.getComponent(2);

    List<Vertex> vertices = node.getFace().getLocalVertices();
    //System.out.println("Face: " + Coord2D.coordAt(vertices.get(0),node.getFace()) + "," + Coord2D.coordAt(vertices.get(1),node.getFace()) + "," + Coord2D.coordAt(vertices.get(2),node.getFace()));
    //System.out.println("Point: " + point);
    //System.out.print("Contains? ");
    // currently not properly handling edge case and assuming point is at least
    // in face adjacent to current source
    if (l1 >= 0 && l1 < 1 && l2 >= 0 && l2 < 1 && l3 >= 0 && l3 < 1) {
      //System.out.println("yes");
      return node.getFace();
    } else {
      //System.out.println("no");
      Iterator<DevelopmentNode> itr = node.getChildren().iterator();
      while (itr.hasNext()) {
        DevelopmentNode next = itr.next();
        Face f = next.getFace();
        vertices = f.getLocalVertices();
        //System.out.println("Face: " + Coord2D.coordAt(vertices.get(0),f) + "," + Coord2D.coordAt(vertices.get(1),f) + "," + Coord2D.coordAt(vertices.get(2),f) + "(" + f + ")");
        //System.out.println("Point: " + point);
        //System.out.print("Contains? ");
        l = getBarycentricCoords(point, f);
        l1 = l.getComponent(0);
        l2 = l.getComponent(1);
        l3 = l.getComponent(2);
        if (l1 >= 0 && l1 < 1 && l2 >= 0 && l2 < 1 && l3 >= 0 && l3 < 1) {
          //System.out.println("yes");
          return f;
        }//else System.out.println("no");
      }
    }
    return null;
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

  public void setSourcePoint(Vector point) {
    sourcePoint = point;
    StopWatch s = new StopWatch();
    s.start();
    buildTree();
    s.stop();
    System.out.println("time to build development: " + s.getElapsedTime());
    setChanged();
    notifyObservers("source");
  }

  // TODO modify general recursive method to handle special initial case
  private void buildTree() {
    // get transformation taking sourcePoint to origin (translation by
    // -1*sourcePoint)
    AffineTransformation t = new AffineTransformation(Vector.scale(sourcePoint,
        -1));

    Vector.scale(sourcePoint, -1);

    EmbeddedFace transformedFace = t.affineTransFace(sourceFace);
    root = new DevelopmentNode(null, sourceFace, transformedFace, t);
    List<Vertex> vertices = sourceFace.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      List<Edge> list0 = v0.getLocalEdges();
      List<Edge> list1 = v1.getLocalEdges();
      Edge edge = null;
      for (int j = 0; j < list0.size(); j++) {
        if (list1.contains(list0.get(j))) {
          edge = list0.get(j);
          break;
        }
      }

      // build frustum through edge end-points
      Vector vect0 = t.affineTransPoint(Coord2D.coordAt(v0, sourceFace));
      Vector vect1 = t.affineTransPoint(Coord2D.coordAt(v1, sourceFace));
      Frustum2D frustum = new Frustum2D(vect1, vect0);

      // each edge is adjacent to 2 faces; take the one that is not the
      // current face
      List<Face> faces = edge.getLocalFaces();
      Face newFace;
      if (faces.get(0) == sourceFace)
        newFace = faces.get(1);
      else
        newFace = faces.get(0);

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

    List<Vertex> vertices = face.getLocalVertices();
    for (int i = 0; i < vertices.size(); i++) {
      Vertex v0 = vertices.get(i);
      Vertex v1 = vertices.get((i + 1) % vertices.size());
      List<Edge> list0 = v0.getLocalEdges();
      List<Edge> list1 = v1.getLocalEdges();

      Edge edge = null;
      for (int j = 0; j < list0.size(); j++) {
        if (list1.contains(list0.get(j))) {
          edge = list0.get(j);
          break;
        }
      }
      if (edge.equals(sourceEdge)) {
        continue;
      }

      // build frustum through edge end-points
      Vector vect0 = newTrans.affineTransPoint(Coord2D.coordAt(v0, face));
      Vector vect1 = newTrans.affineTransPoint(Coord2D.coordAt(v1, face));
      // check which is left and which is right
      Vector left = vect0;
      Vector right = vect1;
      Vertex v3 = vertices.get((i + 2) % vertices.size());
      Vector vect3 = newTrans.affineTransPoint(Coord2D.coordAt(v3, face));

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

      Frustum2D newFrustum = Frustum2D.intersect(new Frustum2D(left, right),
          frustum);

      // each edge is adjacent to 2 faces; take the one that is not the
      // current face
      List<Face> faces = edge.getLocalFaces();
      Face newFace;
      if (faces.get(0) == face)
        newFace = faces.get(1);
      else
        newFace = faces.get(0);

      if (newFrustum != null)
        buildTree(node, newFace, edge, newFrustum, newTrans, depth + 1);
    }
  }
  
  
  
/////////////////////////////////////////////////////
// DevelopmentNode
/////////////////////////////////////////////////////

  public class DevelopmentNode {
    private EmbeddedFace embeddedFace;
    private Face face;
    private AffineTransformation affineTrans;
    private ArrayList<DevelopmentNode> children;
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
      children = new ArrayList<DevelopmentNode>();
      for (int i = 0; i < nodes.length; i++) {
        children.add(nodes[i]);
      }
    }

    public void addChild(DevelopmentNode node) {
      children.add(node);
    }

    public void removeChild(DevelopmentNode node) {
      children.remove(node);
    }

    public EmbeddedFace getEmbeddedFace() {
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
      return children;
    }

    public boolean isRoot() {
      return parent == null;
    }

    public boolean faceIsSource() {
      return face.equals(sourceFace);
    }
  }
}
