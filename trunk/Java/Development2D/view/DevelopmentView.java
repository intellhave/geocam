package view;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.util.CameraUtility;
import development.Development;
import development.Node;
import development.Trail;


public abstract class DevelopmentView extends JRViewer implements Observer{
  protected SceneGraphComponent sgcRoot = new SceneGraphComponent();
  protected SceneGraphComponent sgcDevelopment = new SceneGraphComponent();
  protected SceneGraphComponent objects = new SceneGraphComponent();
  protected Scene scene;
  protected ColorScheme colorScheme;
  protected Development development;
  protected double radius = 0.15; // radius of sourcePoint objects
  protected ArrayList<Node> nodeList = new ArrayList<Node>();
  protected ArrayList<Trail> trailList = new ArrayList<Trail>();
  protected int dimension;
  
  public DevelopmentView(Development development, ColorScheme colorScheme, double radius) {
    this.development = development;
    this.colorScheme = colorScheme;
    this.radius = radius;
    
    scene = this.getPlugin(Scene.class);

    sgcDevelopment.addChild(objects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    sgcRoot.addChild(sgcDevelopment);
  }
  
  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry();
  }
  
  public void setRadius(double radius) {
    this.radius = radius;
    setObjectsSGC();
  }
  
  protected void setObjectsSGC() {
    sgcDevelopment.removeChild(objects);
    objects = new SceneGraphComponent();
    for(Node n : nodeList) {
      n.setRadius(radius);
      //System.out.println("setObjectsSGC(): adding node at " + n.getPosition());
      objects.addChild(SGCMethods.sgcFromNode(n, dimension));
    }
    for(Trail t : trailList) {
      objects.addChild(SGCMethods.sgcFromTrail(t, radius));
    }
    sgcDevelopment.addChild(objects);
  }
  
  protected abstract void updateGeometry();
  
  public void addCircles(){
    PointSetFactory psf = new PointSetFactory();
    double[][] verts = circle(10,.5);
    psf.setVertexCount( verts.length );
    psf.setVertexCoordinates( verts );
   
    psf.update();
    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(psf.getGeometry());
    this.sgcRoot.addChild(sgc);
    this.setContent(sgc);
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
