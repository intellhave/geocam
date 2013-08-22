package beta;

import java.awt.Color;

import triangulation.Face;
import view.TextureLibrary;
import de.jreality.scene.Appearance;

/**
 * A simple AnimatedTexture which flips between Black and White colors.
 */
public class Strobe extends AnimatedTexture {
  private Appearance onapp = new Appearance();
  private Appearance offapp = new Appearance(); 
  public Strobe() {
    super();
    TextureLibrary.initializeShaders(onapp, Color.white);
    TextureLibrary.initializeShaders(offapp, Color.black);
  }
  
  public void update() {
    on = !on;
    setChanged();
  }

  private boolean on;
  public Appearance getCurrentAppearance(Face face) {
    return on ? onapp : offapp;
  }
  
  public double getScale() {
    return 1.0;
  }
  
  public String getName() {
    return "Strobe";
  } 
}
