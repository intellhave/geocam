package view;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

import java.awt.Color;
import java.io.File;
import java.util.EnumMap;

import util.AssetManager;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ImageData;
import de.jreality.shader.ShaderUtility;
import de.jreality.shader.Texture2D;
import de.jreality.shader.TextureUtility;
import de.jreality.util.Input;

/*********************************************************************************
 * TextureLibrary
 * 
 * The static methods in this class are responsible for creating and storing
 * textures for use in various visualizations. Since we need textures in many
 * places, going to disk each time we need to texture a polygon would be very
 * expensive (and make the simulation appear to lag). Instead, this class caches
 * those textures, so that the same appearance can be shared by several items in
 * a scene.
 *********************************************************************************/
class TextureLibrary {

  /*********************************************************************************
   * This enumeration class records the various textures the library can
   * provide. The EnumMap below maintains a mapping of these constants to cached
   * appearances.
   *********************************************************************************/
  enum TextureDescriptor {
    BATHROOMTILE, CHECKER, CLAY, COBBLESTONE, DOTS, GRID, 
    LIGHTHOUSE, MARBLE, PLAID, STUCCO, SWIRLS, ZIGZAG
  }

  private static EnumMap<TextureDescriptor, Appearance> library;

  /*********************************************************************************
   * This block of code initializes our library of textures, so we aren't always
   * fetching copies from the disk.
   *********************************************************************************/
  static {
    library = new EnumMap<TextureDescriptor, Appearance>(
        TextureDescriptor.class);

    for (TextureDescriptor td : TextureDescriptor.values()) {
      Appearance tdApp = initializeAppearance(td);
      library.put(td, tdApp);
    }
  }

  /*********************************************************************************
   * initializeShaders
   * 
   * This helper method is used in the "initializeAppearance" methods below to
   * set up the shaders the appearance will use. Basically, this ensures the
   * shaders are set up in a consistent way (not doing this has created odd bugs
   * in the past).
   * 
   * We set whether edges and vertices are drawn elsewhere (in View.java). In
   * the past, we've found that trying to use the
   * "Appearance.setAttribute(CommonAttributes.*)" method of setting attributes
   * and setting this properties on a shader (Ex. dgs.setDrawLines(false);)
   * leads to problems. Thus, we don't specify whether edges/vertices should be
   * drawn in the properties below.
   *********************************************************************************/
  private static void initializeShaders(Appearance app, Color faceColor) {
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app, true);
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs
        .createPolygonShader("default");

    dps.setAmbientColor(faceColor); // dps.setAmbientColor(c);
    dps.setDiffuseColor(faceColor); // dps.setDiffuseColor(c);
    dps.setAmbientCoefficient(0.3); // These coefficients seem to help the
                                    // texture look "bright"
    dps.setDiffuseCoefficient(0.8); // when it gets mapped to the surface.
    dps.setSpecularCoefficient(0.0); // We don't need faces to look shiny by
                                     // default.

    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setDiffuseColor(Color.orange);
    dls.setTubeDraw(true);
    dls.setTubeRadius(0.05);
  }

  /*********************************************************************************
   * initializeAppearance
   * 
   * This method is used to load each texture from disk the first time it is
   * needed. It should only be called once (and the results stored) which is why
   * this method is private.
   *********************************************************************************/
  private static Appearance initializeAppearance(TextureDescriptor td) {
    Appearance app = new Appearance();
    initializeShaders(app, Color.white);

    ImageData id = null;

    try {
      File ff = null;
      switch (td) {
      case BATHROOMTILE:
        ff = AssetManager.getAssetFile("textures/bathroomtile.jpg");
        break;
      case CHECKER:
        ff = AssetManager.getAssetFile("textures/checker.jpg");
        break;
      case CLAY:
        ff = AssetManager.getAssetFile("textures/clay.jpg");
        break;
      case COBBLESTONE:
        ff = AssetManager.getAssetFile("textures/cobblestone.jpg");
        break;
      case DOTS:
        ff = AssetManager.getAssetFile("textures/dots.jpg");
        break;
      case GRID:
        ff = AssetManager.getAssetFile("textures/grid.jpg");
        break;
      case LIGHTHOUSE:
        ff = AssetManager.getAssetFile("textures/lighthouse.jpg");
        break;
      case MARBLE:
        ff = AssetManager.getAssetFile("textures/marble.jpg");
        break;
      case PLAID:
        ff = AssetManager.getAssetFile("textures/plaid.jpg");
        break;
      case STUCCO:
        ff = AssetManager.getAssetFile("textures/stucco.jpg");
        break;
      case SWIRLS:
        ff = AssetManager.getAssetFile("textures/swirls.jpg");
        break;
      case ZIGZAG:
        ff = AssetManager.getAssetFile("textures/zigzag.jpg");
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

  /*********************************************************************************
   * getAppearance
   * 
   * This method is the primary method in this class used by outside code. Given
   * an input TextureDescriptor, this method returns the corresponding cached
   * appearance object. Implicitly, we assume that the Appearance object that is
   * returned will not be modified (since it is shared among possibly many scene
   * graph components).
   *********************************************************************************/
  static Appearance getAppearance(TextureDescriptor td) {
    return library.get(td);
  }

  static Appearance getAppearance(Color color) {
    Appearance app = new Appearance();
    initializeShaders(app, color);
    return app;
  }

}