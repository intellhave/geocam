package deprecated;

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
import view.FaceAppearanceScheme;
import view.TextureLibrary;
import view.View;
import view.TextureLibrary.TextureDescriptor;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.DirectionalLight;
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
  private SceneGraphComponent sourceFaceSGC;
  private Face sourceFace;
  private HashMap<FaceGrouping, SceneGraphComponent> faceGroups;
  private List<FaceGrouping> checked;
  private List<SceneGraphComponent> inTree;
  private int recursionDepth = 3;
  private TreeNode root;

  public UnfoldingView(Marker source, FaceAppearanceScheme fas) {
    super(null, null, fas);
    
    this.source = source;
    sourceSGC = source.getAppearance().makeSceneGraphComponent();
    sgcMarkers.addChild(sourceSGC);
    
    sourceFace = source.getPosition().getFace();

    faceGroups = new HashMap<FaceGrouping, SceneGraphComponent>();
    checked = new ArrayList<FaceGrouping>();
    inTree = new ArrayList<SceneGraphComponent>();

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

  /*********************************************************************************
   * initializeNewManifold
   * 
   * This method constructs SceneGraphComponents for every face in the manifold,
   * grouping them into FaceGroupings. It adds the SGCs that represents these
   * groupings to the geometry node of the scene graph tree. It builds the tree
   * in such a way that entire face groupings are represented by a single SGC,
   * allowing them to be "unfolded" as a single face. It also marks the SGC that
   * contains the source face for future reference.
   *********************************************************************************/
  public void initializeNewManifold() {
    // Go through the list of FaceGroupings, creating a parent SGC for
    // each then preparing other SGCs for the Faces in that grouping, adding 
    // them as children to first SGC
    // This allows for the rotation transformation to be applied to the
    // SGC representing the entire FaceGrouping, so that a whole
    // "face" appears to unfold
    for (FaceGrouping fg : Triangulation.groupTable.values()) {
      
      //make sure that duplicate FaceGroupings aren't made into SGCs
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
    
    FaceGrouping sourceGroup = Triangulation.groupTable.get(sourceFace);
    sourceFaceSGC = faceGroups.get(sourceFace);
    Matrix identity = MatrixBuilder.euclidean().getMatrix();
    root = new TreeNode(sourceFaceSGC, sourceGroup, identity, identity);
  }

  /*********************************************************************************
   * makeSGC
   * 
   * This helper method takes a Face from the triangulation and constructs a SGC 
   * to represent it in the scene.
   *********************************************************************************/
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

  /*********************************************************************************
   * generateMarkerGeometry
   * 
   * Currently the source marker is the only one in the simulation, so this
   * method merely has to calculate its position on the manifold.
   *********************************************************************************/
  protected void generateMarkerGeometry() {
    ManifoldPosition sourcePos = source.getPosition();
    Vector[] tuple = new Vector[4];
    tuple[0] = EmbeddedTriangulation.getCoord3D(sourceFace, sourcePos.getPosition());
    tuple[1] = EmbeddedTriangulation.embedVector(sourceFace,
        sourcePos.getDirectionForward());
    tuple[2] = EmbeddedTriangulation.embedVector(sourceFace,
        sourcePos.getDirectionLeft());
    tuple[3] = EmbeddedTriangulation.getEmbeddedNormal(sourceFace);
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

  /*********************************************************************************
   * updateCamera
   * 
   * Updates the camera position to follow the source marker around the
   * manifold. 
   * TODO: In the future the user will not be allowed to move around the
   * manifold, so this method should be different or may not be necessary at
   * all.
   *********************************************************************************/
  protected void updateCamera() {
    ManifoldPosition pos = source.getPosition();
    Vector embedPos = EmbeddedTriangulation.getCoord3D(pos.getFace(),
        pos.getPosition());
    Vector forward, normal;
    forward = EmbeddedTriangulation.embedVector(pos.getFace(), pos.getDirectionForward());
    normal = EmbeddedTriangulation.getEmbeddedNormal(pos.getFace());

    forward.normalize();
    normal.normalize();

    Vector adjustedForward = new Vector(forward);
    adjustedForward.scale(-.5);
    Vector offset = Vector.add(normal, adjustedForward);
    offset.scale(5.0);
    
    Vector position = Vector.add(offset,embedPos);
    
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
    buildTree();
    unfoldTree();
  }

  /*********************************************************************************
   * buildTree
   * 
   * This method builds the tree that will be used to display the unfolding of
   * the manifold. It first calls a recursive helper method which builds the
   * tree in a depth-first manner, computing and storing the angles and
   * rotations for each face. It then traverses the tree breadth-first, giving
   * each node of the tree a unique scene graph component to unfold. It looks at
   * the FaceGrouping that each node contains, and if the SGC associated with
   * that grouping already belongs to another node, it makes a copy of the SGC
   * for the current node. Note that it also makes the copied SGCs invisible, so
   * that they appear only when it is time to unfold them.
   * 
   * TODO: determine how to clip faces
   *********************************************************************************/
  private void buildTree() {
    //trigger the depth-first traversal of the tree that determines the transformations
    //for the faces in each node
    Matrix identity = MatrixBuilder.euclidean().getMatrix();
    buildTree(identity, 0, root);

    //traverse the tree breadth-first to make scene graph components for the tree nodes,
    //copying the scene graph component for a given face as necessary
    Queue<TreeNode> q = new LinkedList<TreeNode>();
    q.add(root);
    while (q.size() > 0) {
      TreeNode n = q.poll();
      for (TreeNode child : n.children)
        q.add(child);

      SceneGraphComponent rotatingFace = faceGroups.get(n.faceList);      
      //if a node already has this scene graph component in its data,
      //make a copy of the component for this node
      if (inTree.contains(rotatingFace)) {
        SceneGraphComponent copy = copySceneGraph(rotatingFace);
        copy.setVisible(false);
        sgcDevelopment.addChild(copy);
        rotatingFace = copy;
      } else {
        inTree.add(rotatingFace);
      }
      n.sgc = rotatingFace;
    }
  }
  
  /*********************************************************************************
   * buildTree
   * 
   * This recursive helper method computes the angles and transformations for
   * each face as it builds the tree. It takes as arguments the parent node and
   * the transformation that will move the current face into position next to
   * the parent face. Note that this transformation is composition of the
   * translation and rotation of the parent node.
   * 
   * The method iterates over all faces adjacent to the parent node's face, and
   * finds the angle between the faces and the axis of rotation. It also
   * computes the rotation matrix for the face. All information necessary to the
   * animation is stored in a node which is added as a child of the parent node,
   * and the method is called again until recursion depth is reached.
   *********************************************************************************/
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
        if (rotatingFace.equals(sourceFaceSGC))
          continue;

        double angle = computeAngle(f1, f2);
        Vertex v1 = sharedEdge.getLocalVertices().get(0);
        Vertex v2 = sharedEdge.getLocalVertices().get(1);
        Vertex first = null;
        Vertex second = null;

        // find the orientation of the axis of rotation with relation to the
        // stationary face
        int orientation = getOrientation(v1, v2, f1);
        if (orientation == 1) {
          first = v1;
          second = v2;
        } else {
          first = v2;
          second = v1;
        }

        Vector vec1 = EmbeddedTriangulation.getCoord3D(first);
        double[] coord1 = vec1.getVectorAsArray();
        
        Vector vec2 = EmbeddedTriangulation.getCoord3D(second);
        double[] coord2 = vec2.getVectorAsArray();

        //compute the rotation matrix and create the new tree node
        Matrix rot = MatrixBuilder.euclidean().rotate(coord1, coord2, -angle).getMatrix();
        TreeNode node = new TreeNode(fg2, t, rot, angle, coord1, coord2);
        parent.children.add(node);

        Matrix newTrans = Matrix.times(t, rot);

        buildTree(newTrans, depth + 1, node);
      }
    }
  }

  /*********************************************************************************
   * copySceneGraph
   * 
   * A recursive method for making a "deep" copy of the SGC containing a
   * FaceGrouping.
   *********************************************************************************/
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

  /*********************************************************************************
   * unfoldTree
   * 
   * This method traverses the tree breadth-first and displays the unfolding
   * animation. For the animation loop the angle of rotation is broken into n
   * steps and in each iteration of the loop one step is displayed and the
   * thread is slept to slow the animation down. Note that each time through the
   * loop the nth step of the rotation must be composed with the translation
   * that takes the moving face up to the stationary one.
   *********************************************************************************/
  private void unfoldTree() {

    Queue<TreeNode> q = new LinkedList<TreeNode>();
    q.add(root);

    while (q.size() > 0) {
      TreeNode node = q.poll();
      for (TreeNode child : node.children) {
        q.add(child);
      }

      if(node == root) continue;
      
      Matrix t = node.transformation;
      double[] coord1 = node.coord1;
      double[] coord2 = node.coord2;
      double angle = node.angle;
      SceneGraphComponent rotatingFace = node.sgc;
      rotatingFace.setVisible(true);
      
      // perform the transformation on the rotating face
      // Note that in each step of the animation loop, you must compose the
      // previous transformation (i.e. the transformation to move the face
      // into position) with the current step of the rotation
      int step = 0;
      while (step <= 50) {
        Matrix rot = MatrixBuilder.euclidean()
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
    Vector f1normal = EmbeddedTriangulation.getEmbeddedNormal(f1);
    Vector f2normal = EmbeddedTriangulation.getEmbeddedNormal(f2);

    //f1normal and f2normal are already normalized, so do not need to divide by the norms
    double angle = Math.acos(Vector.dot(f1normal, f2normal));

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

  /*********************************************************************************
   * TreeNode
   * 
   * This is the private inner class for making tree node objects. Each node
   * contains the information necessary to display the unfolding animation of a
   * single face in the tree.
   *********************************************************************************/
  private class TreeNode {
    private SceneGraphComponent sgc;
    private FaceGrouping faceList;
    private Matrix transformation;
    private List<TreeNode> children;
    private Matrix rotation;
    private double angle;
    private double[] coord1;
    private double[] coord2;

    public TreeNode(FaceGrouping fg, Matrix trans, Matrix rotate, double angle, 
        double[] coord1, double[] coord2) {
      this.sgc = new SceneGraphComponent();
      faceList = fg;
      transformation = trans;
      children = new LinkedList<TreeNode>();
      rotation = rotate;
      this.angle = angle;
      this.coord1 = coord1;
      this.coord2 = coord2;
    }

    // this is a constructor for the root
    public TreeNode(SceneGraphComponent sgc, FaceGrouping fg, Matrix trans, Matrix rotate) {
      this.sgc = sgc;
      faceList = fg;
      transformation = trans;
      children = new LinkedList<TreeNode>();
      rotation = rotate;
      angle = 0.0;
      coord1 = null;
      coord2 = null;
    }
  }
  
  /*********************************************************************************
   * setZoom
   * 
   * Currently the unfolding view does not need a zoom method, so this is empty.
   *********************************************************************************/
  public void setZoom(double zoomValue) {
  }

}
