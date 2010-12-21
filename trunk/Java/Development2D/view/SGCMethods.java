package view;

import java.awt.Color;

import de.jreality.geometry.PointSetFactory;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import development.Vector;

/**
 * useful methods of generating SceneGraphComponents
 */
public class SGCMethods {
  public static SceneGraphComponent sgcFromPoint(Vector point) {
    return sgcFromPoint(point, 0.03);
  }
  public static SceneGraphComponent sgcFromPoint(Vector point, double radius) {
    SceneGraphComponent sgc_points = new SceneGraphComponent();
    Appearance app_points = new Appearance();

    app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);

    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(radius);
    dps.setDiffuseColor(Color.BLUE);

    sgc_points.setAppearance(app_points);

    double[][] vertlist = new double[1][3];
    if (point.getDimension() == 2) {
      vertlist[0] = new double[] { point.getComponent(0),
          point.getComponent(1), 0 };
    } else if (point.getDimension() == 3) {
      vertlist[0] = new double[] { point.getComponent(0),
          point.getComponent(1), point.getComponent(2) };
    }

    // create geometry with pointsetfactory
    PointSetFactory psf = new PointSetFactory();
    psf.setVertexCount(1);
    psf.setVertexCoordinates(vertlist);
    psf.update();

    // set geometry
    sgc_points.setGeometry(psf.getGeometry());

    // return
    return sgc_points;
  }
  
  public static Appearance getFaceAppearance(double transparency) {

    // create appearance for developed faces
    Appearance app_face = new Appearance();

    // set some basic attributes
    app_face.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_face.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_face.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_face.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_face.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);

    // set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_face, true);

    // line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);

    // polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(Color.WHITE);
    dps.setTransparency(transparency);

    return app_face;
  }
  
  public static Appearance getDevelopmentAppearance() {
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.TUBES_DRAW, false);
    app.setAttribute(CommonAttributes.TUBE_RADIUS, 0.01); 
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    app.setAttribute(CommonAttributes.TRANSPARENCY, 0.4d);
    app.setAttribute(CommonAttributes.PICKABLE, true);
    return app;
  }
}
