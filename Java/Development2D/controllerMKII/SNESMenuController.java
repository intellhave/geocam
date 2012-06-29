package controllerMKII;

public class SNESMenuController extends SNESController {
 
  /*********************************************************************************
   * SNESMenuController
   * 
   * This constructor builds a new SNESController to control the input
   * Development. From this development, we construct the data structures we use
   * internally to process actions, and then the initialize method is called.
   **********************************************************************************/
  public SNESMenuController(){
    super();
  }
  
  protected synchronized void startRepeatingAction(Action action){
  }
  
  protected synchronized void stopRepeatingAction(Action action){
    actionQueue.add( action );
  }
}
