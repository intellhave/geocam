package view;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

import java.awt.Color;
import java.io.File;
import java.util.EnumMap;

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

  public enum TextureDescriptor {
    BATHROOMTILE, CHECKER, CLAY, COBBLESTONE, DOTS, GRID, LIGHTHOUSE, PLAID, STUCCO, SWIRLS, ZIGZAG
  }

  private static EnumMap<TextureDescriptor, Appearance> library;
  
  /*********************************************************************************
   * This block of code initializes our library of textures, so we aren't always
   * fetching copies from the disk.
   *********************************************************************************/
  static {
    library = new EnumMap<TextureDescriptor, Appearance>(TextureDescriptor.class);
    
    for( TextureDescriptor td : TextureDescriptor.values() ){
      Appearance tdApp = initializeAppearance( td );
      library.put(td, tdApp);      
    }
  }

  private static Appearance initializeAppearance(TextureDescriptor td) {
    Appearance app = new Appearance();
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app, true);
    dgs.setShowLines(false);
    dgs.setShowPoints(false);
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs
        .createPolygonShader("default");

    dps.setAmbientColor(Color.white); // dps.setAmbientColor(c);
    dps.setAmbientColor(Color.white); // dps.setDiffuseColor(c);
    dps.setAmbientCoefficient(0.3); // These coefficients seem to help the
                                    // texture look "bright"
    dps.setDiffuseCoefficient(0.8); // when it gets mapped to the surface.

    ImageData id = null;

    try {
      File ff = null;
      switch (td) {
      case BATHROOMTILE:
        ff = new File("Data/textures/bathroomtile.jpg");
        break;
      case CHECKER:
        ff = new File("Data/textures/checker.jpg");
        break;
      case CLAY:
        ff = new File("Data/textures/clay.jpg");
        break;
      case COBBLESTONE:
        ff = new File("Data/textures/cobblestone.jpg");
        break;
      case DOTS:
        ff = new File("Data/textures/dots.jpg");
        break;
      case GRID:
        ff = new File("Data/textures/grid.jpg");
        break;
      case LIGHTHOUSE:
        ff = new File("Data/textures/lighthouse.jpg");
        break;
      case PLAID:
        ff = new File("Data/textures/plaid.jpg");
        break;
      case STUCCO:
        ff = new File("Data/textures/stucco.jpg");
        break;
      case SWIRLS:
        ff = new File("Data/textures/swirls.jpg");
        break;
      case ZIGZAG:
        ff = new File("Data/textures/zigzag.jpg");
        break;
      }

      id = ImageData.load(Input.getInput(ff));
    } catch (Exception ee) {
      System.err.println("Error: Unable to load texture " + td);
      ee.printStackTrace();
    }

    Texture2D tex = TextureUtility.createTexture(app, POLYGON_SHADER, id);
    tex.setTextureMatrix(MatrixBuilder.euclidean().scale(0.5).getMatrix());
    
    return app;
  }

  public static Appearance getAppearance( TextureDescriptor td ){
    return library.get(td);
  }
  
}
