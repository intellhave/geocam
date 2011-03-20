package development;

import java.util.ArrayList;

import triangulation.Face;

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
  private EmbeddedFace clippedFace = null;
  private Face face;
  private AffineTransformation affineTrans;
  private ArrayList<DevelopmentNode> children = new ArrayList<DevelopmentNode>();
  private Frustum2D frustum;
  private int depth;

  public DevelopmentNode(DevelopmentNode prev, Face f, Frustum2D frust, EmbeddedFace cf, AffineTransformation at) {
    
    if (prev == null){ depth = 0; }
    else{  depth = prev.getDepth() + 1; }
    
    frustum = frust;
    face = f;
    clippedFace = cf;
    affineTrans = at;
  }

  public void addChild(DevelopmentNode node) { children.add(node); }
  public void removeChild(DevelopmentNode node) { children.remove(node); }
  
  public EmbeddedFace getClippedFace() {
    
    if(clippedFace == null){
      //generate the clippedFace from frustum and affineTrans
      if(frustum == null){ clippedFace = affineTrans.affineTransFace(face); }
      else{ clippedFace = frustum.clipFace(affineTrans.affineTransFace(face)); }
    }
    return clippedFace;
  }

  //accessors
  public Face getFace() { return face; }
  public int getDepth() { return depth; }
  public Frustum2D getFrustum(){ return frustum; }
  public AffineTransformation getAffineTransformation() { return affineTrans; }
  public ArrayList<DevelopmentNode> getChildren() { return new ArrayList<DevelopmentNode>(children); }

  //public boolean isRoot() { return depth == 0; }
  //public boolean faceIsSource() { return face.equals(source.getFace()); }
}