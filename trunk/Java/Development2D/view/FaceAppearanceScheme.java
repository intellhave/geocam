package view;

import java.awt.Color;

import triangulation.Face;
import triangulation.Triangulation;
import view.TextureLibrary.TextureDescriptor;

/*********************************************************************************
 * FaceAppearanceScheme
 * 
 * This class is responsible for playing the role of ColorScheme in previous
 * versions of our code. It is responsible for providing mappings from faces to
 * textures and/or colors.
 *********************************************************************************/
public class FaceAppearanceScheme {

  /*********************************************************************************
   * getColor
   *********************************************************************************/
  public Color getColor(Face f) {
    if (f.hasColor())
      return f.getColor();
    return Color.getHSBColor((float) f.getIndex()
        / (float) Triangulation.faceTable.size(), 0.5f, 0.9f);
  }

  /*********************************************************************************
   * getTextureDescriptor
   * 
   * For now, this code provides a consistent mapping from faces to textures. In
   * the future, we will allow the mapping to be customizable.
   *********************************************************************************/
  public TextureDescriptor getTextureDescriptor(Face f) {
    int index = Math.abs(getColor(f).getRGB());
    TextureDescriptor[] textures = TextureDescriptor.values();
    return textures[index % textures.length];
  }

}
