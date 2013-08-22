package beta;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

import java.awt.Color;
import java.awt.Image;

import triangulation.Face;
import view.TextureLibrary;
import de.jreality.scene.Appearance;
import de.jreality.shader.ImageData;
import de.jreality.shader.TextureUtility;

/**
 * An AnimatedTexture that displays a copy of the Game of Life on each face group of the manifold.
 * <p>Based on jreality code.
 */
public class GameOfLife extends AnimatedTexture {
  private static final int WIDTH = 64;
  private static final int HEIGHT = 64;

  private int current = 0;
  private double[][] lifeColors = {{0,.6,1,1}, {1, 1,1,1}}; 
  private byte[] faceColors;
  private int[][][] lifeBoard;
  public GameOfLife() {
    super();
    lifeBoard = new int[2][HEIGHT][WIDTH];
    resetBoard();
    faceColors = new byte[WIDTH*HEIGHT*4];    
    updateColors();
  }
  private void resetBoard() {
    for (int i = 0; i<HEIGHT; ++i)  {
      for (int j = 0; j<WIDTH; ++j) {
        lifeBoard[current][i][j] = (Math.random() > .7) ? 1 : 0;
      }
    }
  }
  private void updateLife()  {
    for (int i = 0; i<HEIGHT; ++i)  {
      for (int j = 0; j<WIDTH; ++j) {
        int curval = lifeBoard[current][i][j] % 2;
        int sum = 0;
        for (int n = -1; n<2; ++n)  {
          for (int m = -1; m<2; ++m)  {
            sum += lifeBoard[current][(i+n+HEIGHT)%HEIGHT][(j+m+WIDTH)%WIDTH];
          }
        }
        if ( (curval == 1 && (sum == 3 || sum == 4)) ||
           (curval == 0) && (sum == 3))
          lifeBoard[1-current][i][j] = 1;
        else lifeBoard[1-current][i][j] = 0;
      }
    }
    current = 1 - current;
  }
  public void updateColors()  {
    for (int i = 0; i<HEIGHT; ++i)  {
      for (int j = 0; j<WIDTH; ++j) {
        for (int k = 0; k<4; ++k) {
          faceColors[4*(i*WIDTH +j)+k] = (byte) (255.0 * lifeColors[ lifeBoard[current][i][j] ][k]);
        }
      }
    }
  }
  
  public Image currentValue() {
    return currentValue.getImage();
  }
  
  ImageData currentValue;
  public void update()  {
    updateLife();
    updateColors();
    currentValue = new ImageData(faceColors, WIDTH, HEIGHT);
    setChanged();
  }

  @Override
  public Appearance getCurrentAppearance(Face face) {
    Appearance app = new Appearance();
    TextureLibrary.initializeShaders(app, Color.GREEN);
    TextureUtility.createTexture(app, POLYGON_SHADER, currentValue);
    return app;
  }
  
  public double getScale() {
    return 1.0;
  }
  
  @Override
  public String getName() {
    return "Game of Life";
  }

}
