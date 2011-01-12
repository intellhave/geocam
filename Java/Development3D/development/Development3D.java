package development;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import triangulation.Face;
import triangulation.Tetra;
import view.ColorScheme3D;
import view.SGCMethods.DevelopmentGeometry;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.Geometry;

public class Development3D {
  private Tetra sourceTetra_;
  private Vector sourcePoint_;
  private int maxDepth_;
  private double stepSize_;
  private double radius_;
  private DevelopmentNode3D root_;
  private ArrayList<Node3D> nodeList_ = new ArrayList<Node3D>();
  private ArrayList<Node3D> embeddedNodeList_ = new ArrayList<Node3D>();
  private Node3D sourcePointNode_;

  public Development3D(Tetra sourceTetra, Vector sourcePoint, int depth,
      double stepSize, double radius) {
    sourceTetra_ = sourceTetra;
    sourcePoint_ = sourcePoint;
    maxDepth_ = depth;
    stepSize_ = stepSize;
    radius_ = radius;
    
    sourcePointNode_ = new Node3D(Color.blue, sourceTetra_, sourcePoint_);
    sourcePointNode_.setRadius(radius);
    nodeList_.add(sourcePointNode_);
    
    buildTree();
  }

  public void rebuild(Tetra sourceTetra, Vector sourcePoint, int depth) {
    maxDepth_ = depth;
    sourcePoint_ = sourcePoint;
    sourceTetra_ = sourceTetra;
    buildTree();
  }
  
  public void setDepth(int depth) {
    maxDepth_ = depth;
    buildTree();
  }

  private void buildTree() {
    AffineTransformation T = new AffineTransformation(Vector.scale(
        sourcePoint_, -1));   
    buildTree(0, sourceTetra_, null, null, T, null);
  }

  private void buildTree(int depth, Tetra curTetra, Face sourceFace,
      Frustum3D currentFrustum, AffineTransformation currentTrans, DevelopmentNode3D parent) {
    // get new affine transformation
    AffineTransformation newTrans = new AffineTransformation(3);
    if (sourceFace != null) {
      AffineTransformation coord_trans = CoordTrans3D.affineTransAt(curTetra,
          sourceFace);
      newTrans.leftMultiply(coord_trans);
    }
    newTrans.leftMultiply(currentTrans);

    // apply new_trans to tetra and orient the faces
    OrientedTetra oriented_tetra = new OrientedTetra(curTetra, newTrans);

    ArrayList<EmbeddedFace> clippedTetra = new ArrayList<EmbeddedFace>();

    if (currentFrustum != null) {
      // get list of EmbeddedFaces by clipping with Frustum3D and using
      // ConvexHull3D
      ArrayList<Vector> cliptetra_points = currentFrustum
          .clipFace(oriented_tetra);
      if (cliptetra_points.size() < 4) {
        return;
      } // quit this branch if cliptetra has no faces
      ConvexHull3D ch3d = new ConvexHull3D(cliptetra_points);
      clippedTetra = ch3d.getFaces();
    } else{
      //get list of EmbeddedFaces directly from oriented_tetra
      for(int j=0; j<4; j++){
        OrientedFace of = oriented_tetra.getOrientedFace(j);
        clippedTetra.add(new EmbeddedFace(of.getVector(0), of.getVector(1), of.getVector(2)));
      }
    }
    
    DevelopmentNode3D node = new DevelopmentNode3D(parent, curTetra, clippedTetra, currentFrustum,
        newTrans, depth);
    if(parent == null) {
      root_ = node;
    } else
      parent.addChild(node);
    
  //see which faces to continue developing on, if any
    if(depth >= maxDepth_){ return; }
    
    for(int j=0; j<4; j++){
      OrientedFace of = oriented_tetra.getOrientedFace(j);
      
      Face f = of.getFace();
      if(f == sourceFace){ continue; }
      
      //find tetra incident to cur_tetra through this face
      Iterator<Tetra> it = f.getLocalTetras().iterator();
      Tetra nextTetra = it.next();
      if(nextTetra == curTetra){ nextTetra = it.next(); }
      
      //intersect frustums
      Frustum3D frust = new Frustum3D(of.getVector(0), of.getVector(1), of.getVector(2));
      Frustum3D newFrust = null;
      if(currentFrustum == null){ newFrust = frust; }
      else{ newFrust = Frustum3D.intersect(frust, currentFrustum); }
      if(newFrust == null){ continue; }
      
      
      //develop off of oriented_face
      if(depth == 0) buildTree(depth+1, nextTetra, f, newFrust, newTrans, root_);
      else buildTree(depth+1, nextTetra, f, newFrust, newTrans, node);
    }
  }
  
  public DevelopmentNode3D getRoot() { return root_; }
  
  public Geometry getGeometry(ColorScheme3D colorScheme) {
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    ArrayList<Color> colors = new ArrayList<Color>();
    embeddedNodeList_ = new ArrayList<Node3D>();
    
    computeDevelopment(root_, colors, geometry, colorScheme);
    
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

    Color[] colorList = new Color[colors.size()];
    for (int i = 0; i < colors.size(); i++) {
      colorList[i] = colors.get(i);
    }

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setFaceColors(colorList);
    ifsf.update();
    return ifsf.getGeometry();
  }

  private void computeDevelopment(DevelopmentNode3D devNode,
      ArrayList<Color> colors, DevelopmentGeometry geometry, ColorScheme3D colorScheme) {
    for (EmbeddedFace ef : devNode.getClippedTetra()) {
      double[][] face = ef.getVectorsAsArray();
      geometry.addFace(face);
      colors.add(colorScheme.getColor(devNode));
    }
    for(Node3D n : devNode.getObjects()) {
      embeddedNodeList_.add(n);
    }

    for (DevelopmentNode3D n : devNode.getChildren())
      computeDevelopment(n, colors, geometry, colorScheme);
  }
  
  public ArrayList<Node3D> getObjects() { return embeddedNodeList_; }
  
  
  
  // ==================== DevelopmentNode3D ====================
  
  public class DevelopmentNode3D {
    public DevelopmentNode3D parent_;
    public Tetra tetra_;
    public ArrayList<EmbeddedFace> clippedTetra_;
    public Frustum3D frustum_;
    public AffineTransformation trans_;
    private int depth_;
    private ArrayList<Node3D> containedObjects_ = new ArrayList<Node3D>();
    
    private ArrayList<DevelopmentNode3D> children_ = new ArrayList<DevelopmentNode3D>();
    
    public DevelopmentNode3D(DevelopmentNode3D parent, Tetra tetra, ArrayList<EmbeddedFace> clippedTetra, Frustum3D frustum, AffineTransformation trans, int depth) {
      parent_ = parent;
      tetra_ = tetra;
      clippedTetra_ = clippedTetra;
      frustum_ = frustum;
      trans_ = trans;
      depth_ = depth;
      
      // add any objects contained in this face
      for(Node3D node : nodeList_) {
        if(node.getTetra().equals(tetra_)) {
          Vector point = node.getPosition();

          Vector transPoint = trans_.affineTransPoint(point);

          if(isRoot() || frustum_.checkInterior(transPoint)) {
              containedObjects_.add(new Node3D(node.getColor(), node.getTetra(), new Vector(transPoint), node.getRadius()));
          }
        }
      }
    }
    
    public void addChild(DevelopmentNode3D node) {
      children_.add(node);
    }
    public ArrayList<EmbeddedFace> getClippedTetra() { return clippedTetra_; }
    public ArrayList<DevelopmentNode3D> getChildren() { return children_; }
    public int getDepth() { return depth_; }
    public boolean isRoot() { return parent_ == null; }   
    public ArrayList<Node3D> getObjects() { return containedObjects_; }
  }

}
