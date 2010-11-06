package view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import triangulation.Face;
import view.Development.DevelopmentNode;
import de.jreality.scene.SceneGraphComponent;
import development.EmbeddedFace;
import development.Frustum2D;
import development.Vector;

public class SGCTree {
  private SGCNode root;
  private ColorScheme colorScheme;
  private int dimension;
  private final double simulated3DHeight = 0.08;
  private Vector sourcePoint;

  public SGCTree(Development d, ColorScheme scheme, int dim) {
    sourcePoint = d.getSourcePoint();
    dimension = dim;
    colorScheme = scheme;
    root = new SGCNode(d.getRoot(), dimension);
    buildTree(root, d.getRoot());
    setVisibleDepth(d.getDesiredDepth());
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    changeColors(root);
  }

  private void changeColors(SGCNode node) {
    node.updateColor();
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      changeColors(itr.next());
    }
  }

  public void setVisibleDepth(int depth) {
    setVisibility(root, depth);
  }

  private void setVisibility(SGCNode node, int depth) {
    if (node.getDepth() <= depth)
      node.getSGC().setVisible(true);
    else
      node.getSGC().setVisible(false);
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      setVisibility(itr.next(), depth);
    }
  }

  private void buildTree(SGCNode parent, DevelopmentNode node) {
    SGCNode newNode = new SGCNode(node, dimension);
    parent.addChild(newNode);
    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      buildTree(newNode, itr.next());
    }
  }

  public SGCNode getRoot() {
    return root;
  }

  public class SGCNode {
    private SceneGraphComponent sgc;
    private DevelopmentNode node;
    private ArrayList<SGCNode> children;
    private int dimension;

    public SGCNode(DevelopmentNode n, int dim) {
      dimension = dim;
      node = n;
      sgc = new SceneGraphComponent();

      Vector transSourcePoint2d = null;
      if (node.faceIsSource()) {
        Vector newSource = new Vector(sourcePoint.getComponent(0),
            sourcePoint.getComponent(1), 1);
        Vector transSourcePoint = node.getAffineTransformation()
            .transformVector(newSource);
        transSourcePoint2d = new Vector(transSourcePoint.getComponent(0),
            transSourcePoint.getComponent(1));
      }

      if (dimension == 3) {
        sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
            colorScheme.getColor(node), simulated3DHeight));
        sgc.setAppearance(SGCMethods.getFaceAppearance(0.5f));

        if (node.faceIsSource()
            && contains(node.getEmbeddedFace(), transSourcePoint2d)) {
          sgc.addChild(SGCMethods.sgcFromPoint(transSourcePoint2d));
        }

      } else if (dimension == 2) {
        sgc.setGeometry(node.getEmbeddedFace().getGeometry(
            colorScheme.getColor(node)));
        sgc.setAppearance(SGCMethods.getFaceAppearance(0.5f));
        if (node.faceIsSource()
            && contains(node.getEmbeddedFace(), transSourcePoint2d)) {
          sgc.addChild(SGCMethods.sgcFromPoint(transSourcePoint2d));
        } else if (node.isRoot()) {// containment algorithm doesn't work for
                                   // root face
          sgc.addChild(SGCMethods.sgcFromPoint(transSourcePoint2d));
        }
      }
      children = new ArrayList<SGCNode>();
    }

    public int getDepth() {
      return node.getDepth();
    }

    public void updateColor() {
      if (dimension == 3)
        sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
            colorScheme.getColor(node), simulated3DHeight));
      else
        sgc.setGeometry(node.getEmbeddedFace().getGeometry(
            colorScheme.getColor(node)));
    }

    public void addChild(SGCNode node) {
      children.add(node);
    }

    public Face getFace() {
      return node.getFace();
    }

    public ArrayList<SGCNode> getChildren() {
      return children;
    }

    public SceneGraphComponent getSGC() {
      return sgc;
    }

    private boolean contains(EmbeddedFace face, Vector point) {
      List<Vector> vertices = face.getVectors();
      for (int i = 0; i < vertices.size(); i++) {
        Vector v1 = vertices.get(i);
        Vector v2 = vertices.get((i + 1) % vertices.size());
        Frustum2D frustum = new Frustum2D(v1, v2);
        if (frustum.checkInterior(point))
          return true;
      }
      return false;
    }
  }
}
