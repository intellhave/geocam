package viewMKII;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

import java.awt.Color;
import java.io.File;

import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ImageData;
import de.jreality.shader.ShaderUtility;
import de.jreality.shader.Texture2D;
import de.jreality.shader.TextureUtility;
import de.jreality.util.Input;

public class TextureLibrary {

  public enum TextureDescriptor { Dots, Checkerboard }
  
  static void initializeAppearance(Appearance app, TextureDescriptor td, Color c){
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility.createDefaultGeometryShader(app, true);
    dgs.setShowLines(false);
    dgs.setShowPoints(false);
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.createPolygonShader("default");
    
    dps.setAmbientColor(c);
    dps.setDiffuseColor(c);
    dps.setAmbientCoefficient(0.3); // These coefficients seem to help the texture look "bright"
    dps.setDiffuseCoefficient(0.8); // when it gets mapped to the surface.
    
    ImageData id = null;
    switch(td){
    case Dots:
      try{
        File ff = new File("Data/textures/GridSmall.jpg");
        id = ImageData.load(Input.getInput(ff));
      } catch (Exception ee){
        ee.printStackTrace();
        System.exit(1);
      }
      break;
    case Checkerboard:
      try{
        File ff = new File("Data/textures/checker.gif");
        id = ImageData.load(Input.getInput(ff));
      } catch (Exception ee){
        ee.printStackTrace();
        System.exit(1);
      }
      break;
    }
    Texture2D tex = TextureUtility.createTexture(app, POLYGON_SHADER, id);
    tex.setTextureMatrix(MatrixBuilder.euclidean().scale(3.0).getMatrix());
  }
  
  
}
