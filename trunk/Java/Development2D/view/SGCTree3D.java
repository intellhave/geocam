package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import triangulation.Face;
import de.jreality.scene.SceneGraphComponent;
import development.Development;
import development.Development.DevelopmentNode;
import development.EmbeddedFace;
import development.Vector;

public class SGCTree3D {
  private SGCNode root;
  private ColorScheme colorScheme;
  private final double simulated3DHeight = 0.08;
  private Vector sourcePoint;
  private SceneGraphComponent objects = new SceneGraphComponent();

  public SGCTree3D(Development d, ColorScheme scheme) {
    sourcePoint = d.getSourcePoint();
    colorScheme = scheme;
    double[][] faceVerts = vertsFromFace(d.getRoot().getEmbeddedFace());
    root = new SGCNode(d.getRoot(), faceVerts);
    buildTree(root, d.getRoot());
    setVisibleDepth(d.getDepth());
  }

  public SceneGraphComponent getObjects() {
    return objects;
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
    SGCNode newNode = new SGCNode(node, vertsFromFace(node.getEmbeddedFace()));
    parent.addChild(newNode);
    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      buildTree(newNode, itr.next());
    }
  }

  public SGCNode getRoot() {
    return root;
  }

  private double[][] vertsFromFace(EmbeddedFace face) {
    double[][] verts = new double[face.getNumberVertices()][3];
    for (int i = 0; i < face.getNumberVertices(); i++) {
      verts[i] = face.getVectorAt(i).getVectorAsArray();
    }
    return verts;
  }

  // /////////////////////////////////////////////
  // SGCNode
  // /////////////////////////////////////////////
  public class SGCNode {
    private SceneGraphComponent sgc;
    private DevelopmentNode node;
    private ArrayList<SGCNode> children;
    private double[][] verts;

    public SGCNode(DevelopmentNode n, double[][] vertList) {
      verts = vertList;
      node = n;
      sgc = new SceneGraphComponent();

      sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
          colorScheme.getColor(node), simulated3DHeight));
      sgc.setAppearance(SGCMethods.getFaceAppearance(0.5f));
      children = new ArrayList<SGCNode>();

      if (node.faceIsSource()) {
        Vector newSource = new Vector(sourcePoint.getComponent(0),
            sourcePoint.getComponent(1), 1);
        Vector transSourcePoint = node.getAffineTransformation().transformVector(
            newSource);
        Vector transSourcePoint2d = new Vector(transSourcePoint.getComponent(0),
            transSourcePoint.getComponent(1));

        if (node.getEmbeddedFace().contains(transSourcePoint2d)) {
          objects.addChild(SGCMethods.sgcFromPoint(transSourcePoint2d));
        }
      }
    }

    public int getDepth() {
      return node.getDepth();
    }

    public void updateColor() {
      sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
          colorScheme.getColor(node), simulated3DHeight));
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

    public double[][] getVertices() {
      return verts;
    }

    public Color getColor() {
      return colorScheme.getColor(node);
    }
  }
}
