package viewMKII;

/*********************************************************************************
 * Interface ViewCompositor
 * 
 * An "interface compositor" bundles together several views into one image that
 * can be built into a user interface. This is different from a layout manager,
 * in that it takes a very specific number of views as input and may put them
 * together in a way that might not "play nicely" with Swing/AWT without some
 * hackery.
 *********************************************************************************/
public interface ViewCompositor {

  /*********************************************************************************
   * Method updateScene
   * 
   * This method should instruct all the views held by the compositor to update.
   * Then, the compositor might need to perform some operations on the results
   * to present the final view to the user.
   *********************************************************************************/
  public void updateScene();
}
