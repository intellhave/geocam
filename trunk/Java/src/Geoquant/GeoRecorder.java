package Geoquant;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GeoRecorder implements Observer{
  private List<List<Geoquant>> geoList;
  private HashMap<Class<? extends Geoquant>, List<List<Double>>> valueList;
  
  public GeoRecorder() {
    geoList = new LinkedList<List<Geoquant>>();
    valueList = new HashMap<Class<? extends Geoquant>, List<List<Double>>>();
  }
  
  public GeoRecorder(List<Class<? extends Geoquant>> recordList) {
    geoList = new LinkedList<List<Geoquant>>();
    valueList = new HashMap<Class<? extends Geoquant>, List<List<Double>>>();
    for(Class<? extends Geoquant> c : recordList) {
      geoList.add(Geometry.getGeoquants(c));
      valueList.put(c, new LinkedList<List<Double>>());
    }
  }
  
  public void update(Observable obs, Object obj) {
     for(List<Geoquant> list : geoList) {
       List<Double> values = new LinkedList<Double>();
       Class<? extends Geoquant> c = null;
       for(Geoquant q : list) {
         c = q.getClass();
         values.add(q.getValue());
       }
       if(c != null) {
         valueList.get(c).add(values);
       }
     }
  }
  
  public List<List<Double>> getGeoquantHistory(Class<? extends Geoquant> c) {
    List<List<Double>> result = valueList.get(c);
    return result;
  }
  
  public void addGeoquant(Class<? extends Geoquant> c) {
    if(valueList.get(c) == null) {
      geoList.add(Geometry.getGeoquants(c));
      valueList.put(c, new LinkedList<List<Double>>());
    }
  }
  
  public Collection<List<List<Double>>> getAll() {
    return valueList.values();
  }
}
