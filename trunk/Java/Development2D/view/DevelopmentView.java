package view;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
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
    Iterator<Node> itr = nodeList.iterator();
    while(itr.hasNext()) {
      Node n = itr.next();
      objects.addChild(SGCMethods.sgcFromPoint(n.getPosition(), radius, n.getColor()));
    }
    sgcDevelopment.addChild(objects);
  }
  
  protected abstract void updateGeometry();
  
  
  @Override
  public void update(Observable o, Object arg) {
    // TODO Auto-generated method stub
    
  }

}
