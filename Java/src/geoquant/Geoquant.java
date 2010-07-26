package geoquant;
import java.util.LinkedList;
import java.util.Observer;
import java.util.Observable;

import triangulation.Simplex;

/**
 * The Geoquant class serves as an abstract description of a geometric quantity that
 * exists on a Triangulation. A geoquant has several defining features:<br/>
 * 1. A geoquant has a position described by a set of simplices.<br/>
 *    (Ex. An angle is defined by the set {Vertex, Face})<br/>
 * 2. A geoquant's value is either set or is calculated from other geoquants.<br/>
 *    (Ex. An angle is calculated from the three Lengths of its defining face.
 * 
 * <p>
 * Geoquants employ the Observer/Observable pattern for calculating values.
 * When a geoquant A observes a geoquant B, A is said to be a dependent of B.
 * In such a scenario, when B's value is changed or is made invalid through its 
 * own dependencies, A is notified to be invalid.
 * 
 * <p>
 * A common example is that Angles are dependent on Lengths which are dependent 
 * in part by the Radii at its end points. When the Radius values are changed, the Radii
 * notify the Length quantity that it is invalid. In turn, the Length quantity informs
 * its dependents, which include Angles, that they are invalid.
 * 
 * <p>
 * When the value of a geoquant is requested, the geoquant checks its validity. When it
 * is currently valid, the value is simply returned. Otherwise, the geoquant requests
 * the values of the geoquants it depends on, which in turn may need to recalculate their 
 * values.
 * 
 * <p>
 * A client program has access to two methods: getValue() and setValue().
 * 
 * <p>
 * A static method at() is usually expected of an implemented Geoquant class. This 
 * method takes a set of simplices that defines the desired geoquant. This is how
 * one accesses a geoquant, because the constructor is hidden. This prevents multiple 
 * geoquants of one type to exists at the same location within the Triangulation. Some 
 * Geoquants also have a static method valueAt() which simply returns the value of the 
 * Geoquant.
 * 
 * @author Alex Henniges, Joseph Thomas
 *
 */
public abstract class Geoquant extends Observable implements Observer{
  protected LinkedList<Geoquant> dependents;
  protected double value;
  protected TriPosition pos;
  private boolean valid;
	protected LinkedList<Simplex> location;
  
  protected abstract void recalculate();
  public abstract void remove();
	
  protected Geoquant() {
    dependents = new LinkedList<Geoquant>();
    valid = false;
    location = new LinkedList<Simplex>();
  }
  
  protected Geoquant(Simplex... simplices) {
    dependents = new LinkedList<Geoquant>();
    valid = false;
    location = new LinkedList<Simplex>();
    for(int i = 0; i < simplices.length; i++) {
      location.add(simplices[i]);
    }
  }

  public void addObserver(Observer o) {
    super.addObserver(o);
    Geoquant dep;
    try{
      dep = (Geoquant) o;
      dependents.add(dep);
    } catch(ClassCastException e) {
      return;
    }
  }
  
  public void deleteObserver(Observer o) {
    super.deleteObserver(o);
    Geoquant dep;
    try{
      dep = (Geoquant) o;
      dependents.remove(dep);
    } catch(ClassCastException e) {
      return;
    }
  }
  
  protected void deleteDependents() {
    LinkedList<Geoquant> list = new LinkedList<Geoquant>();
    list.addAll(dependents);
    for(Geoquant q: list) {
      q.remove();
    }
    dependents.clear();
  }
  
  /**
   * Get the value of this geoquant. The value will be recalculated first if this
   * geoquant is invalid. The geoqaunt will be valid after this method returns.
   * 
   * @return The current value of this geoquant.
   */
  public double getValue() {
    if(! valid){
      recalculate();
      valid = true;
    }
    return value;		
  }
  
  /**
   * Set the value of this geoquant to the given parameter. If this value
   * differs from its current value, this geoquant notifies all observers,
   * hence if other geoquants depend on this one, they will become invalid.
   * 
   * WARNING: If this geoquant depends on others, setting the values of those
   *          geoquants will make the value from this method invalid.
   * @param val
   */
  public void setValue(double val) {
    if(value == val && valid) {
      return;
    }
    value = val; 
    invalidate();
    valid = true;
  }
  
  private void invalidate() {
    if(valid){ 
      valid = false;
      setChanged();
      notifyObservers();
    }
  }
    
  public void update(Observable o, Object args) {
    invalidate();
  }
  
  
  public String toString() {
    String type = this.getClass().toString().substring(6);
    if(type.contains(".")) {
      type = type.substring(type.lastIndexOf(".") + 1);
    }
    return type + "@" + location + "=" + this.getValue();
  }
  
  protected enum PartialType{Radius, Eta}
  protected enum SecondPartialType{RadiusRadius, RadiusEta, EtaEta}
}
