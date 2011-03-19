package objects;

import java.awt.Color;

import de.jreality.scene.Appearance;
import de.jreality.shader.CommonAttributes;

/* This class holds appearance information for a VisiblePath
 */

public class PathAppearance {

  double rTube = 0.04; //tube radius
  double rVertex = 0.05; //vertex radius
  Color cTube = Color.BLACK; //tube color
  Color cVertex = Color.BLUE; //vertex color
  
  public PathAppearance(){ }
  public PathAppearance(double tubeSize, Color tubeColor, double vertexSize, Color vertexColor){
    rTube = tubeSize;
    cTube = tubeColor;
    rVertex = vertexSize;
    cVertex = vertexColor;
  }
  
  public void setTubeRadius(double radius){ rTube = radius; }
  public void setVertexRadius(double radius){ rVertex = radius; }
  public void setTubeColor(Color color){ cTube = color; }
  public void setVertexColor(Color color){ cVertex = color; }
  
  public Appearance getJRealityAppearance(){
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app.setAttribute(CommonAttributes.POINT_RADIUS, rVertex);
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.TUBES_DRAW, true);
    app.setAttribute(CommonAttributes.TUBE_RADIUS, rTube);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    app.setAttribute(CommonAttributes.PICKABLE, false);
    return app;
  }
  
  public Color getTubeColor(){ return cTube; }
  public Color getVertexColor(){ return cVertex; }
}
