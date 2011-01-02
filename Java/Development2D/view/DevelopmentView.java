package view;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.util.CameraUtility;
import development.Development;
import development.Node;


public abstract class DevelopmentView extends JRViewer implements Observer{
  protected SceneGraphComponent sgcRoot = new SceneGraphComponent();
  protected SceneGraphComponent sgcDevelopment = new SceneGraphComponent();
  protected SceneGraphComponent objects = new SceneGraphComponent();
  protected Scene scene;
  protected ColorScheme colorScheme;
  protected Development development;
  protected double radius = 0.03; // radius of sourcePoint objects
  protected ArrayList<Node> nodeList = new ArrayList<Node>();
  
  public DevelopmentView(Development development, ColorScheme colorScheme) {
    this.development = development;
    this.colorScheme = colorScheme;
    
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
      objects.addChild(SGCMethods.sgcFromNode(n));
    }
    sgcDevelopment.addChild(objects);
  }
  
  protected abstract void updateGeometry();
  
  
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
