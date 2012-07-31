package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import marker.Marker;
import triangulation.Edge;
import triangulation.Face;
import triangulation.FaceGrouping;
import triangulation.StdFace;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.TextureLibrary.TextureDescriptor;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import development.DevelopmentComputations;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.TextureCoords;
import development.Vector;

public class UnfoldingView extends View {

  private Marker source;
  private SceneGraphComponent sourceSGC;
  private HashMap<FaceGrouping, SceneGraphComponent> faceGroups;
  private List<FaceGrouping> checked;
  private List<SceneGraphComponent> unfolded;
  private int recursionDepth = 3;
  private TreeNode root;

  public UnfoldingView(Marker source, FaceAppearanceScheme fas) {
    super(null, null, fas);
    this.source = source;
    sourceSGC = source.getAppearance().makeSceneGraphComponent();
    sgcMarkers.addChild(sourceSGC);

    faceGroups = new HashMap<FaceGrouping, SceneGraphComponent>();
    checked = new ArrayList<FaceGrouping>();
    unfolded = new ArrayList<SceneGraphComponent>();

    double[][] light_psns = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 },
        { -1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 } };

    Vector origin = new Vector(0, 0, 0);
    for (double[] psn : light_psns) {
      SceneGraphComponent sgcLight = new SceneGraphComponent();
      DirectionalLight light = new DirectionalLight();
      light.setIntensity(1.0);
      sgcLight.setLight(light);

      MatrixBuilder m = View.lookAt(new Vector(psn), origin);
      m.assignTo(sgcLight);

      sgcDevelopment.addChild(sgcLight);
    }
  }

  public void initializeNewManifold() {
    for (FaceGrouping fg : Triangulation.groupTable.values()) {
      if (checked.contains(fg))
        continue;

      checked.add(fg);
      SceneGraphComponent group = new SceneGraphComponent();
      sgcDevelopment.addChild(group);

      List<Face> connectedFaces = fg.getFaces();
      for (Face f : connectedFaces) {
        group.addChild(makeSGC(f));
      }
      faceGroups.put(fg, group);
    }
    Face sourceFace = source.getPosition().getFace();
    SceneGraphComponent startFace = faceGroups.get(sourceFace);
    Matrix identity = MatrixBuilder.euclidean().getMatrix();
    root = new TreeNode(startFace, Triangulation.groupTable.get(sourceFace),
        identity, identity);
  }

  protected SceneGraphComponent makeSGC(Face f) {
    double[][] verts = EmbeddedTriangulation.getFaceGeometry(f);
    double[][] texCoords = TextureCoords.getCoordsAsArray(f);
    int[][] ifsf_faces = new int[][] { { 0, 1, 2 } };

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(verts.length);
    ifsf.setVertexCoordinates(verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setGenerateFaceNormals(true);
    ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, texCoords);
    ifsf.update();
    Appearance app;

    TextureDescriptor td = faceAppearanceScheme.getTextureDescriptor(f);
    app = TextureLibrary.getAppearance(td);

    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(ifsf.getGeometry());
    sgc.setAppearance(app);
    sgc.setVisible(true);
    return sgc;
  }

  protected void generateMarkerGeometry() {
    Face f = source.getPosition().getFace();
    ManifoldPosition sourcePos = source.getPosition();
    Vector[] tuple = new Vector[4];
    tuple[0] = EmbeddedTriangulation.getCoord3D(f, sourcePos.getPosition());
    tuple[1] = EmbeddedTriangulation.embedVector(f,
        sourcePos.getDirectionForward());
    tuple[2] = EmbeddedTriangulation.embedVector(f,
        sourcePos.getDirectionLeft());
    tuple[3] = EmbeddedTriangulation.getEmbeddedNormal(f);
    Vector pos = tuple[0];
    Vector forward = Vector.normalize(tuple[1]);
    Vector left = Vector.normalize(tuple[2]);
    Vector normal = Vector.normalize(tuple[3]);

    double[] matrix = new double[16];

    matrix[0 * 4 + 0] = forward.getComponent(0);
    matrix[0 * 4 + 1] = left.getComponent(0);
    matrix[0 * 4 + 2] = normal.getComponent(0);
    matrix[0 * 4 + 3] = 0.0;

    matrix[1 * 4 + 0] = forward.getComponent(1);
    matrix[1 * 4 + 1] = left.getComponent(1);
    matrix[1 * 4 + 2] = normal.getComponent(1);
    matrix[1 * 4 + 3] = 0.0;

    matrix[2 * 4 + 0] = forward.getComponent(2);
    matrix[2 * 4 + 1] = left.getComponent(2);
    matrix[2 * 4 + 2] = normal.getComponent(2);
    matrix[2 * 4 + 3] = 0.0;

    matrix[3 * 4 + 0] = 0.0;
    matrix[3 * 4 + 1] = 0.0;
    matrix[3 * 4 + 2] = 0.0;
    matrix[3 * 4 + 3] = 1.0;

    MatrixBuilder
        .euclidean()
        .translate(pos.getComponent(0), pos.getComponent(1),
            pos.getComponent(2)).times(matrix)
        .scale(source.getAppearance().getScale()).assignTo(sourceSGC);
    sourceSGC.setVisible(true);

  }

  protected void generateManifoldGeometry() {

  }

  protected void updateCamera() {
    ManifoldPosition pos = source.getPosition();
    Vector embedPos = EmbeddedTriangulation.getCoord3D(pos.getFace(),
        pos.getPosition());
    Vector forward, left, normal;
    forward = EmbeddedTriangulation.embedVector(pos.getFace(),
        pos.getDirectionForward());
    left = EmbeddedTriangulation.embedVector(pos.getFace(),
        pos.getDirectionLeft());
    normal = EmbeddedTriangulation.getEmbeddedNormal(pos.getFace());

    forward.normalize();
    left.normalize();
    normal.normalize();

//    Matrix rot = MatrixBuilder.euclidean()
//        .rotate(-Math.PI / 8, left.getVectorAsArray()).getMatrix();

    Vector adjustedForward = new Vector(forward);
    adjustedForward.scale(-.5);
    Vector offset = Vector.add(normal, adjustedForward);
    offset.scale(5.0);
    
    Vector position = Vector.add(offset,embedPos);
    
    Vector adjustedLeft = left;
//    double matrix[] = new double[16];
//
//    matrix[0 * 4 + 0] = adjustedForward.getComponent(0);
//    matrix[0 * 4 + 1] = adjustedLeft.getComponent(0);
//    matrix[0 * 4 + 2] = adjustedNormal.getComponent(0);
//    matrix[0 * 4 + 3] = 0.0;
//
//    matrix[1 * 4 + 0] = adjustedForward.getComponent(1);
//    matrix[1 * 4 + 1] = adjustedLeft.getComponent(1);
//    matrix[1 * 4 + 2] = adjustedNormal.getComponent(1);
//    matrix[1 * 4 + 3] = 0.0;
//
//    matrix[2 * 4 + 0] = adjustedForward.getComponent(2);
//    matrix[2 * 4 + 1] = adjustedLeft.getComponent(2);
//    matrix[2 * 4 + 2] = adjustedNormal.getComponent(2);
//    matrix[2 * 4 + 3] = 0.0;
//
//    matrix[3 * 4 + 0] = 0.0;
//    matrix[3 * 4 + 1] = 0.0;
//    matrix[3 * 4 + 2] = 0.0;
//    matrix[3 * 4 + 3] = 1.0;
//
//    // adjustedForward.scale(3.0);
//    adjustedNormal.scale(5.0);
//
//    MatrixBuilder.euclidean().translate(adjustedNormal.getVectorAsArray())
//        .translate(embedPos.getVectorAsArray()).times(matrix)
//        .rotateZ(-Math.PI / 2).assignTo(sgcCamera);
     MatrixBuilder mb = lookAt(position, embedPos, normal);
     mb.assignTo(sgcCamera);
  }

  public void removeMarker(Marker m) {
  }

  /*********************************************************************************
   * displayUnfolding
   * 
   * This public method allows AnimationUI to start the unfolding process.
   *********************************************************************************/
  public void displayUnfolding() {
    // Face sourceFace = source.getPosition().getFace();
    // unfolded.add(faceGroups.get(sourceFace));
    buildTree();
    unfoldTree();
  }

  /*********************************************************************************
   * buildTree
   * 
   * This recursive method unfolds all faces in the embedding in a systematic
   * way.
   * 
   * TODO: add more description here
   * 
   * TODO: Make the traversal of the surface "breadth first" TODO: allow for
   * multiple SceneGraphComponents for each group of faces so that they may
   * appear in multiple places TODO: determine how to clip faces
   *********************************************************************************/
  private void buildTree() {
    Matrix identity = MatrixBuilder.euclidean().getMatrix();
    buildTree(identity, 0, root);

    Queue<TreeNode> q = new LinkedList<TreeNode>();
    q.add(root);
    while (q.size() > 0) {
      TreeNode n = q.poll();
      for (TreeNode child : n.children)
        q.add(child);

      SceneGraphComponent rotatingFace = faceGroups.get(n.faceList);
      if (unfolded.contains(rotatingFace)) {
        SceneGraphComponent copy = copySceneGraph(rotatingFace);
        copy.setVisible(false);
        sgcDevelopment.addChild(copy);
        rotatingFace = copy;
      } else {
        unfolded.add(rotatingFace);
      }
      n.sgc = rotatingFace;
    }
  }
  
  @SuppressWarnings("static-access")
  private void buildTree(Matrix t, int depth, TreeNode parent) {
    if (depth < recursionDepth) {
      FaceGrouping fg = parent.faceList;
      Face f1 = null;
      for (Edge sharedEdge : fg.getLocalEdges()) {
        // determine which face in the group has that edge
        for (Face face : fg.getFaces()) {
          if (face.getLocalEdges().contains(sharedEdge)) {
            f1 = face;
          }
        }

        Face f2 = DevelopmentComputations.getNewFace(f1, sharedEdge);
        FaceGrouping fg2 = Triangulation.groupTable.get(f2);
        SceneGraphComponent rotatingFace = faceGroups.get(fg2);

        // Don't unfold the face if it is the source face
        if (rotatingFace.equals(faceGroups.get(Triangulation.groupTable.get(source.getPosition().getFace()))))
          continue;

        double angle = computeAngle(f1, f2);
        Vertex v1 = sharedEdge.getLocalVertices().get(0);
        Vertex v2 = sharedEdge.getLocalVertices().get(1);
        Vertex first = null;
        Vertex second = null;

        int orientation = getOrientation(v1, v2, f1);
        if (orientation == 1) {
          first = v1;
          second = v2;
        } else {
          first = v2;
          second = v1;
        }

        Vector vec1 = EmbeddedTriangulation.getCoord3D(first);
        double[] coord1 = new double[3];
        for (int i = 0; i < 3; i++) {
          coord1[i] = vec1.getComponent(i);
        }
        Vector vec2 = EmbeddedTriangulation.getCoord3D(second);
        double[] coord2 = new double[3];
        for (int i = 0; i < 3; i++) {
          coord2[i] = vec2.getComponent(i);
        }

        // perform the transformation on the rotating face
        // Note that in each step of the animation loop, you must compose the
        // previous
        // transformation (i.e. the transformation to move the face into
        // position) with
        // the current step of the rotation
        Matrix rot;
        rot = MatrixBuilder.euclidean().rotate(coord1, coord2, -angle)
            .getMatrix();
        TreeNode node = new TreeNode(fg2, t, rot, angle, coord1, coord2);
        parent.children.add(node);

        Matrix newTrans = Matrix.times(t, rot);

        buildTree(newTrans, depth + 1, node);
      }
    }
  }

  private SceneGraphComponent copySceneGraph(SceneGraphComponent root) {
    SceneGraphComponent sgc = new SceneGraphComponent();

    sgc.setAppearance(root.getAppearance());
    sgc.setGeometry(root.getGeometry());

    int numChildren = root.getChildComponentCount();
    for (int ii = 0; ii < numChildren; ii++) {
      SceneGraphComponent child = root.getChildComponent(ii);
      sgc.addChild(copySceneGraph(child));
    }

    return sgc;
  }

  private void unfoldTree() {

    Queue<TreeNode> q = new LinkedList<TreeNode>();
    q.add(root);

    while (q.size() > 0) {
      TreeNode node = q.poll();
      for (TreeNode child : node.children) {
        q.add(child);
      }

      if(node == root) continue;
      Matrix rot = node.rotation;
      Matrix t = node.transformation;
      double[] coord1 = node.coord1;
      double[] coord2 = node.coord2;
      double angle = node.angle;
      SceneGraphComponent rotatingFace = node.sgc;
      rotatingFace.setVisible(true);
      int step = 0;
      while (step <= 50) {
        rot = MatrixBuilder.euclidean()
            .rotate(coord1, coord2, -(step / 50.0) * angle).getMatrix();
        Matrix.times(t, rot).assignTo(rotatingFace);
        step++;
        viewer.render();
        try {
          Thread.currentThread().sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /*********************************************************************************
   * computeAngle
   * 
   * This method takes two faces in the embedded manifold and returns the angle
   * through which one of the faces must rotate to lie in the plane of the
   * other. Note that this is not the same thing as the angle between the faces.
   * To compute the angle, it finds normal vectors for the faces and returns the
   * angle between these normal vectors.
   *********************************************************************************/
  private double computeAngle(Face f1, Face f2) {
    // get normal vector of face1
    List<Vertex> vertices1 = f1.getLocalVertices();
    Vertex f1vert1 = vertices1.get(0);
    Vertex f1vert2 = vertices1.get(1);
    Vertex f1vert3 = vertices1.get(2);
    Vector f1coord1 = EmbeddedTriangulation.getCoord3D(f1vert1);
    Vector f1coord2 = EmbeddedTriangulation.getCoord3D(f1vert2);
    Vector f1coord3 = EmbeddedTriangulation.getCoord3D(f1vert3);
    Vector f1vec1 = Vector.subtract(f1coord2, f1coord1);
    Vector f1vec2 = Vector.subtract(f1coord3, f1coord1);
    Vector f1normal = Vector.cross(f1vec1, f1vec2);

    // get normal vector of face2
    List<Vertex> vertices2 = f2.getLocalVertices();
    Vertex f2vert1 = vertices2.get(0);
    Vertex f2vert2 = vertices2.get(1);
    Vertex f2vert3 = vertices2.get(2);
    Vector f2coord1 = EmbeddedTriangulation.getCoord3D(f2vert1);
    Vector f2coord2 = EmbeddedTriangulation.getCoord3D(f2vert2);
    Vector f2coord3 = EmbeddedTriangulation.getCoord3D(f2vert3);
    Vector f2vec1 = Vector.subtract(f2coord2, f2coord1);
    Vector f2vec2 = Vector.subtract(f2coord3, f2coord1);
    Vector f2normal = Vector.cross(f2vec1, f2vec2);

    double angle = Math.acos(Vector.dot(f1normal, f2normal)
        / (f1normal.length() * f2normal.length()));

    return angle;
  }

  /*********************************************************************************
   * getOrientation
   * 
   * Given a face and two vertices that define an edge on that face, this method
   * determines which order these vertices occur in on the face. If the vertices
   * occur in counter-clockwise order, the method returns 1. If they occur in
   * clockwise order, it returns -1.
   * 
   * TODO: Is this necessary to make the rotations work? And if so, is there a
   * simpler algorithm for determining orientation?
   *********************************************************************************/
  private int getOrientation(Vertex v1, Vertex v2, Face f) {

    StdFace standface = new StdFace(f);

    // Find which vertex on the standard face is equal to v1
    // If the next vertex on the standard face is v2, the ordering is
    // counter-clockwise
    // If not, v2 must have occurred directly before v1, and the ordering is
    // clockwise
    if (standface.v1.equals(v1))
      if (standface.v2.equals(v2))
        return 1;
      else
        return -1;
    else if (standface.v2.equals(v1))
      if (standface.v3.equals(v2))
        return 1;
      else
        return -1;
    else if (standface.v1.equals(v2))
      return 1;
    else
      return -1;
  }

  private class TreeNode {
    private SceneGraphComponent sgc;
    private FaceGrouping faceList;
    private Matrix transformation;
    private List<TreeNode> children;
    private Matrix rotation;
    private double angle;
    private double[] coord1;
    private double[] coord2;

    public TreeNode(SceneGraphComponent sgc, FaceGrouping fg, Matrix trans,
        Matrix rotate, double angle, double[] coord1, double[] coord2) {
      this.sgc = sgc;
      faceList = fg;
      transformation = trans;
      children = new LinkedList<TreeNode>();
      rotation = rotate;
      this.angle = angle;
      this.coord1 = coord1;
      this.coord2 = coord2;
    }
    
    public TreeNode(FaceGrouping fg, Matrix trans,
        Matrix rotate, double angle, double[] coord1, double[] coord2) {
      //this.sgc = sgc;
      faceList = fg;
      transformation = trans;
      children = new LinkedList<TreeNode>();
      rotation = rotate;
      this.angle = angle;
      this.coord1 = coord1;
      this.coord2 = coord2;
    }

    // this is a constructor for the root
    public TreeNode(SceneGraphComponent sgc, FaceGrouping fg, Matrix trans,
        Matrix rotate) {
      this.sgc = sgc;
      faceList = fg;
      transformation = trans;
      children = new LinkedList<TreeNode>();
      rotation = rotate;
    }
  }

}
