package view;
/*
 * Basis class for 2D and simulated 3D views of development
 */
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import triangulation.Triangulation;
import triangulation.Vertex;

import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.util.CameraUtility;
import development.Development;
import development.Node;
import development.Trail;
import geoquant.Radius;

/*
 * DevelopmentView
 * 
 * Overview: Base viewing object. Extensions give different view methods
 * 
 *  * sgcRoot is the root of the scene tree
 *  * sgcDevelopment is scene graph component displaying embedded (clipped) 
 *    faces
 *  * pointer to development
 *  
 *  Possible upgrades:
 *    * remove circles (make an extension)
 */

public abstract class DevelopmentView extends JRViewer implements Observer{
  protected SceneGraphComponent sgcRoot = new SceneGraphComponent();
  protected SceneGraphComponent sgcDevelopment = new SceneGraphComponent();
  protected SceneGraphComponent sgcObjects = new SceneGraphComponent();
  protected Scene scene;
  protected ColorScheme colorScheme;
  protected Development development;
  protected ArrayList<Node> nodeList = new ArrayList<Node>();
  protected ArrayList<Trail> trailList = new ArrayList<Trail>();
  protected int dimension;
  
  public DevelopmentView(Development development, ColorScheme colorScheme, double radius) {
    this.development = development;
    this.colorScheme = colorScheme;
    
    scene = this.getPlugin(Scene.class);

    sgcDevelopment.addChild(sgcObjects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    sgcRoot.addChild(sgcDevelopment);
  }
  
  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry();
  }
  
  public void setDrawEdges(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.EDGE_DRAW, value);
  }
  
  public void setDrawFaces(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.FACE_DRAW, value);
  }
  
  protected void setObjectsSGC() {
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = new SceneGraphComponent();
    for(Node n : nodeList) {
      sgcObjects.addChild(SGCMethods.sgcFromNode(n, dimension));
    }
    for(Trail t : trailList) {
      sgcObjects.addChild(SGCMethods.sgcFromTrail(t));
    }
    sgcDevelopment.addChild(sgcObjects);
  }
  
  protected abstract void updateGeometry();
  
  public void addCircles(){
    for (Vertex v: Triangulation.vertexTable.values()){
      PointSetFactory psf = new PointSetFactory();
      double[][] verts = circle(20,Radius.valueAt(v));
      psf.setVertexCount( verts.length );
      psf.setVertexCoordinates( verts );
     
      psf.update();
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(psf.getGeometry());
      this.sgcRoot.addChild(sgc);
      this.setContent(sgc);
    }
    updateGeometry();
    
  }
  
  public static double[][] circle(int n, double r) {
    double[][] verts = new double[n][3];
    double dphi = 2.0*Math.PI/n;
    for (int i=0; i<n; i++) {
      verts[i][0]=r*Math.cos(i*dphi);
      verts[i][1]=r*Math.sin(i*dphi);
    }
    return verts;
  }
  
  @Override
  public void update(Observable dev, Object arg) {
    development = (Development) dev;
    String whatChanged = (String) arg;
    updateGeometry();
    if (whatChanged.equals("surface") || whatChanged.equals("depth")) {
      CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
          scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    }

  }

}
