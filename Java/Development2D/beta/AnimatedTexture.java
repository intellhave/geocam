package beta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.Timer;

import triangulation.Face;
import de.jreality.scene.Appearance;

/**
 * The abstract AnimatedTexture class is extended by any class which wants to
 * display an AnimatedTexture on each face of the manifold. This class sets up a
 * Timer which repeatedly calls the update() method and then notifies the
 * observers (the different views) of the change, so they can update the
 * Appearance for every Face by calling getCurrentAppearance().
 * 
 * @author Tanner Prynn
 */
public abstract class AnimatedTexture extends Observable {
  protected Timer timer;
  public static final int DEFAULT_DELAY = 300;

  /**
   * Superconstructor for classes extending AnimatedTexture, uses default 300ms
   * delay between calls to update()
   */
  protected AnimatedTexture() {
    this.timer = new Timer(DEFAULT_DELAY, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        update();
        notifyObservers();
      }
    });
    timer.setInitialDelay(1000);
    timer.start();
  }

  /**
   * Superconstructor for classes extending AnimatedTexture
   * 
   * @param delay
   *          The delay between calls to update(), in milliseconds
   */
  protected AnimatedTexture(int delay) {
    this.timer = new Timer(delay, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println(getName());
        update();
        notifyObservers();
      }
    });
    timer.setInitialDelay(1000);
    timer.start();
  }

  public void pause() {
    timer.stop();
  }

  public void resume() {
    timer.start();
  }
  
  public abstract double getScale();

  /**
   * Update this AnimatedTexture to its next state, so that
   * getCurrentAppearance() will return an updated Appearance.
   * <p>
   * Remember to call Observable.setChanged() within this method if the
   * AnimatedTexture's state is updated.
   */
  public abstract void update();

  /**
   * Retrieve the current Appearance object for a given face
   * 
   * @param face
   *          The face to retrieve the current appearance for
   */
  public abstract Appearance getCurrentAppearance(Face face);

  /**
   * Retrieve a short string that represents the class extending AnimatedTexture
   */
  public abstract String getName();
}
