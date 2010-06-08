package util;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class MiscMath {
  
  public List<Object> listIntersection(List<Object> l1, List<Object> l2) {
    List<Object> sameAs = new LinkedList<Object>();
    
    Hashtable<Object, Boolean> indexExists = new Hashtable<Object, Boolean>();
    
    for (Object obj : l1) {
      indexExists.put(obj, true);
    }
    
    for (Object obj : l2) {
      if (indexExists.contains(obj)){
        sameAs.add(obj);
        indexExists.put(obj, false);      
      }
    }

    return sameAs;
  }
  
  public List<Object> listDifference(List<Object> l1, List<Object> l2) {
    List<Object> diff = new LinkedList<Object>();
    
    Hashtable<Object, Boolean> indexExists = new Hashtable<Object, Boolean>();

    for (Object obj : l2) {
      indexExists.put(obj, true);
    }

    for (Object obj : l1) {
      if (indexExists.put(obj, false)) {
        diff.add(obj);
        indexExists.put(obj, true);
      }
    }

    return diff;
  }
  
  public List<Object> multiplicityUnion(List<Object> l1, List<Object> l2) {
    List<Object> merge = new LinkedList<Object>();
    
    for (Object obj : l1) {
      merge.add(obj);
    }
    for (Object obj : l2) {
      merge.add(obj);
    }
    return merge;
  }
  
  public List<Object> multiplicityIntersection(List<Object> l1, List<Object> l2){
    List<Object> inter = new LinkedList<Object>();

    Hashtable<Object, Integer> indexToQuantity = new Hashtable<Object, Integer>();

    for (Object obj : l1) {
      if (indexToQuantity.get(obj) == null) {
        indexToQuantity.put(obj, 1);
      }
      else {
        indexToQuantity.put(obj, indexToQuantity.get(obj) + 1);
      }
    }

    for (Object obj : l2) {
      if (indexToQuantity.get(obj) == null) {
        continue;
      }
      else if (indexToQuantity.get(obj) > 0) {
        indexToQuantity.put(obj, indexToQuantity.get(obj) - 1);
        inter.add(obj);
      }
    }

    return inter;
  }
  
  public List<Object> multiplicityDifference(List<Object> l1, List<Object> l2) {
    List<Object> diff = new LinkedList<Object>();

    Hashtable<Object, Integer> indexToQuantity = new Hashtable<Object, Integer>();

    for (Object obj : l2) {
      if (indexToQuantity.get(obj) == null) {
        indexToQuantity.put(obj, 1);
      }
      else {
        indexToQuantity.put(obj, indexToQuantity.get(obj) + 1);
      }
    }

    for (Object obj : l1) {
      if (indexToQuantity.get(obj) > 0) {
        indexToQuantity.put(obj, indexToQuantity.get(obj) - 1);
      } 
      else {
        diff.add(obj);
      }
    }

    return diff;
  }
}  






