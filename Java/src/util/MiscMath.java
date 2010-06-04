package util;

import java.awt.geom.Point2D;
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
  
  public List<Double> quadratic(double a, double b, double c){
    List<Double> solutions = new LinkedList<Double>();
   
       /*                       _________
                         +   _ | 2
                   -b    -    \|b  - 4*a*c
             x =  -------------------------
                             2*a
       */ 
       
       double inside = b*b - 4*a*c; // The value inside the square root
       if(inside < 0){
         
         // No real solutions
         return solutions;
       }
       
       if(inside == 0){
         
         // Only one solution
         double sol = b * (-1) / (2*a);
         solutions.add(sol);
         return solutions;
       }
       
       double sol1 = ((-1)*b + Math.sqrt(inside)) / (2*a);
       double sol2 = ((-1)*b - Math.sqrt(inside)) / (2*a);
       
       solutions.add(sol1);
       solutions.add(sol2);
       
       return solutions;
  }
  public double distancePoint(Point2D.Double a, Point2D.Double b) {
  
     /*         ____________________________             
             _ |           2              2    
       d =    \|(a_x - b_x)  + (a_y - b_y)
     */
    
     return Math.sqrt((a.getX()-b.getX())*(a.getX()-b.getX())+(a.getY()-b.getY())*(a.getY()-b.getY()));
  }
  
}
