package view;

import development.Development;
import development.EmbeddedTriangulation;

public class DevelopmentViewEmbedded extends DevelopmentView {

  public DevelopmentViewEmbedded(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, false);

    this.startup();
    
    /*Geometry geom = sgcPolyhedron.getGeometry();
    try {
      File file = new File(filename);
      geom = Readers.read(file).getGeometry();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sgcPolyhedron.setGeometry(geom);
    sgcRoot.addChild(sgcPolyhedron);
    sgcRoot.addChild(sgcObjects);
    sgcRoot.addTool(new RotateTool());

    Appearance app_polyhedron = new Appearance();
    app_polyhedron.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_polyhedron.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_polyhedron.setAttribute(CommonAttributes.TUBES_DRAW, false);
    app_polyhedron.setAttribute(CommonAttributes.TUBE_RADIUS, 0.02);
    app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    // app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY, 0.05d);
    app_polyhedron.setAttribute(CommonAttributes.PICKABLE, true);
    sgcPolyhedron.setAppearance(app_polyhedron);

    this.addBasicUI();

    this.setContent(sgcRoot);
    Scene scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    this.startup();*/

  }
  
  protected void initializeNewManifold(){ }
  
  protected void generateManifoldGeometry(){ 
    
    //use EmbeddedTriangulation to draw the polyhedron (if it exists)
    
  }
  
  protected void generateObjectGeometry(){ 
    
    //draw the objects in 3D
    
  }
  
}
