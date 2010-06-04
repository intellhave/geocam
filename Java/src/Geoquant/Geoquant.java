package Geoquant;
import java.util.LinkedList;
import java.util.Observer;
import java.util.Observable;
import Triangulation.Simplex;


public abstract class Geoquant extends Observable implements Observer{
  protected LinkedList<Geoquant> dependents;
  protected double value;
  protected TriPosition pos;
  private boolean valid;
	protected LinkedList<Simplex> location;
  
  protected abstract void recalculate();
  protected abstract void remove();
	
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
    for(Geoquant q: dependents) {
      q.remove();
    }
    dependents.clear();
  }
  
  public double getValue() {
    if(! valid){
      recalculate();
      valid = true;
    }
    return value;		
  }
  
  public void setValue(double val) {
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
