package Geoquant;
import java.util.ArrayList;
import java.util.Iterator;


public abstract class Geoquant {
  protected ArrayList<Geoquant> dependents;
  protected double value;
  protected TriPosition pos;
  private boolean valid;
	
  protected abstract void recalculate();
  protected abstract void remove();
	
  protected Geoquant() {
    dependents = new ArrayList<Geoquant>();
    valid = false;
  }
  

  protected void addDependent(Geoquant q) {
    dependents.add(q);
  }
  
  protected void removeDependent(Geoquant q) {
    dependents.remove(q);
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
      notifyDependents();
    }
  }
  
  private void notifyDependents() {
    for(Geoquant g : dependents) {
      g.invalidate();
    }
  }
}
