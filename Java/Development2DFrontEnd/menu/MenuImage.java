package menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/*********************************************************************************
 * MenuImage
 * 
 * This class handles the menu images for MenuUI. It takes two different images:
 * a background image (such as a screenshot from DevelopmentUI) and a foreground
 * menu image (which has a transparent background and options buttons). It
 * composes these two images and saves the result. MenuImage can then be added
 * to a JFrame and the composite image can then be displayed.
 *********************************************************************************/

public class MenuImage extends Component {
  private static final long serialVersionUID = 1L;
  static BufferedImage composition;
  static File newImage;

  public MenuImage() {
    composition = null;
    try {
      composition = ImageIO.read(newImage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*********************************************************************************
   * compose
   * 
   * This method takes as arguments two File objects, the background image and
   * the menu image. It combines the images and writes the resulting image to
   * the file Composition.png in Development2DFrontEnd/data.
   *********************************************************************************/
  public static void compose(File backgroundLayer, File textLayer) {
    // load source images
    BufferedImage background = null;
    try {
      background = ImageIO.read(backgroundLayer);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    BufferedImage text = null;
    try {
      text = ImageIO.read(textLayer);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    // may need to crop buffered image of background before composing images
    // TODO: Make sure this can also scale the image up if necessary
    if (background.getWidth() > text.getWidth()) {
      background = background.getSubimage(0, 0, text.getWidth(),
          background.getHeight());
    }
    if (background.getHeight() > text.getHeight()) {
      background = background.getSubimage(0, 0, background.getWidth(),
          text.getHeight());
    }

    // create a new BufferedImage and paint the two images onto it
    int w = text.getWidth();
    int h = text.getHeight();
    BufferedImage combined = new BufferedImage(w, h,
        BufferedImage.TYPE_INT_ARGB);
    Graphics g = combined.getGraphics();
    g.drawImage(background, 0, 0, null);
    g.drawImage(text, 0, 0, null);

    newImage = new File("Development2DFrontEnd/data/combined.png");
    try {
      ImageIO.write(combined, "PNG", newImage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*********************************************************************************
   * Paint
   * 
   *********************************************************************************/
  public void paint(Graphics g) {
    g.drawImage(composition, 0, 0, null);
  }

  /*********************************************************************************
   * getPreferredSize
   * 
   *********************************************************************************/
  public Dimension getPreferredSize() {
    if (composition == null) {
      return new Dimension(100, 100);
    } else {
      return new Dimension(composition.getWidth(null),
          composition.getHeight(null));
    }
  }

  /*********************************************************************************
   * A main method for testing purposes
   * 
   *********************************************************************************/
  public static void main(String[] args) {
    File background = new File("Development2DFrontEnd/data/TestBackground.png");
    File text = new File("Development2DFrontEnd/data/TestScreen_select.png");
    compose(background, text);
    MenuImage test = new MenuImage();
    JFrame frame = new JFrame("Image loader");

    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    frame.add(test);
    frame.pack();
    frame.setVisible(true);
  }
}
