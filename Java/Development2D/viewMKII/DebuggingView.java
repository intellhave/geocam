package viewMKII;

import de.jreality.geometry.Primitives;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.Light;
import de.jreality.scene.SceneGraphComponent;
import development.Development;
import view.ColorScheme;

/*********************************************************************************
 * DebuggingView
 * 
 * This very simple view is a debugging aid for the View and UI classes.
 * Basically, it sets up a simple scene with lights and an icosahedron,
 * independent of the development code. 
 *********************************************************************************/

public class DebuggingView extends View {

  public DebuggingView(Development development, ColorScheme colorScheme) {
    super(development, colorScheme);
    IndexedFaceSet ifs = Primitives.icosahedron();
    sgcDevelopment.setGeometry(ifs);
    SceneGraphComponent lightNode = new SceneGraphComponent("light");
    Light dl = new DirectionalLight();
    lightNode.setLight(dl);
    sgcCamera.addChild(lightNode);
  }

  @Override
  protected void generateManifoldGeometry() {
  }

  @Override
  protected void generateMarkerGeometry() {
  }

  @Override
  protected void initializeNewManifold() {
  }

  @Override
  protected void updateCamera() {
    // TODO Auto-generated method stub
    
  }

}
