package beta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import menus.SimulationManager;

/**
 * This JMenu would enable the selected AnimatedTexture.  
 * The relevant code is commented out, because AnimatedTextures are not currently working to deployment standards.
 * To enable them, you would 
 * <li> Add function setAnimateTexture() to SimulationManager which calls View.setAnimated()
 * <li> Un-comment the code in the AnimationChangeListener subclass 
 * <li> Add this JMenu to the Controller
 */
public class AnimatedTextureMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private SimulationManager sim;
	private static List<Class<? extends AnimatedTexture>> animations = new LinkedList<Class<? extends AnimatedTexture>>();
	
	static { // Add desired AnimatedTextures in this static block
		animations.add(Strobe.class);
		animations.add(GameOfLife.class);
		animations.add(CellularAutomata.class);
	}
	
	public AnimatedTextureMenu(SimulationManager sim) {
		this.sim = sim;
		this.setText("Animation");

		for (Class<? extends AnimatedTexture> animClass : animations) {
			JMenuItem item = new JMenuItem();
			this.add(item);
			item.setText(animClass.getSimpleName());
			item.addActionListener(new AnimationChangeListener(animClass));
		}
	}

	private class AnimationChangeListener implements ActionListener {
	    private Class<? extends AnimatedTexture> animation;
	    public AnimationChangeListener(Class<? extends AnimatedTexture> animation) {
	      this.animation = animation;
	    }
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
	      AnimatedTexture atex = null;
	      try {
	        Constructor<? extends AnimatedTexture> atConstructor = animation.getConstructor();
	        atex = atConstructor.newInstance();
	      } catch (Exception except) {
	        except.printStackTrace();
	        return;
	      }
//	      sim.setAnimateTextures(atex);
	      System.out.println(atex.getName());
	    }
	  }
}
