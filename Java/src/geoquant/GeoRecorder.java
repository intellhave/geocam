package geoquant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The GeoRecorder class records changes in geoquants, most notably during flows.
 * A GeoRecorder maintains lists of doubles and string descriptions for every
 * geoquant in the currently loaded triangulation that have been added to this
 * recorder.
 * 
 * Before recording, a GeoRecorder should either be given a list of Geoquant classes
 * to record, or those classes should be added to the recorder one at a time.
 * 
 * A GeoRecorder records information at "steps" that are triggered by its update
 * function via the Observer/Observable pattern. At such a step, the recorder
 * iterates through its list of marked geoquants, adding their values and string
 * forms to a new step.
 * 
 * Access to the steps are available a geoquant at a time or all at once.
 * 
 * @author Alex Henniges
 *
 */
public class GeoRecorder implements Observer{
  private List<List<Geoquant>> geoList;
  private HashMap<Class<? extends Geoquant>, List<List<Double>>> valueList;
  private HashMap<Class<? extends Geoquant>, List<List<String>>> descriptionList;
  private int numSteps;
  
  public GeoRecorder() {
    geoList = new LinkedList<List<Geoquant>>();
    valueList = new HashMap<Class<? extends Geoquant>, List<List<Double>>>();
    descriptionList = new HashMap<Class<? extends Geoquant>, List<List<String>>>();
    numSteps = 0;
  }
  
  public GeoRecorder(List<Class<? extends Geoquant>> recordList) {
    geoList = new LinkedList<List<Geoquant>>();
    valueList = new HashMap<Class<? extends Geoquant>, List<List<Double>>>();
    descriptionList = new HashMap<Class<? extends Geoquant>, List<List<String>>>();
    for(Class<? extends Geoquant> c : recordList) {
      geoList.add(Geometry.getGeoquants(c));
      valueList.put(c, new LinkedList<List<Double>>());
      descriptionList.put(c, new LinkedList<List<String>>());
    }
  }
  
  public void update(Observable obs, Object obj) {
     for(List<Geoquant> list : geoList) {
       List<Double> values = new LinkedList<Double>();
       List<String> descs = new LinkedList<String>();
       Class<? extends Geoquant> c = null;
       for(Geoquant q : list) {
         c = q.getClass();
         values.add(q.getValue());
         descs.add(q.toString());
       }
       if(c != null) {
         valueList.get(c).add(values);
         descriptionList.get(c).add(descs);
       }
     }
     numSteps++;
  }
  
  public List<List<Double>> getValueHistory(Class<? extends Geoquant> c) {
    List<List<Double>> result = valueList.get(c);
    return result;
  }
  
  public List<List<String>> getPrintableHistory(Class<? extends Geoquant> c) {
    List<List<String>> result = descriptionList.get(c);
    return result;
  }
  
  public void addGeoquant(Class<? extends Geoquant> c) {
    if(valueList.get(c) == null) {
      geoList.add(Geometry.getGeoquants(c));
      valueList.put(c, new LinkedList<List<Double>>());
      descriptionList.put(c, new LinkedList<List<String>>());
    }
  }
  
  public Collection<List<List<Double>>> getAllValues() {
    return valueList.values();
  }
  
  public Collection<List<List<String>>> getAllDescriptions() {
    return descriptionList.values();
  }
  
  public int getNumSteps() {
    return numSteps;
  }
}
