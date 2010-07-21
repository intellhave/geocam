package geo;
import java.io.PrintStream;
import java.util.Collections;
import java.util.ArrayList;


public class TriPosition implements Comparable<TriPosition>{
  private ArrayList<Integer> pointIDs;
  
  public TriPosition(int... points) {
    pointIDs = new ArrayList<Integer>();
    for(int point : points) {
      pointIDs.add(point);
    }
    Collections.sort(pointIDs);
  }
  
  public void print(PrintStream out) {
    out.print(pointIDs);
  }

  public int compareTo(TriPosition y) {
    int xLength = this.pointIDs.size();
    int yLength = y.pointIDs.size();
    
    if(xLength != yLength) {
      return xLength - yLength;
    }
    for(int ii = 0; ii < xLength; ii++){
      if(this.pointIDs.get(ii) != y.pointIDs.get(ii)){
        return this.pointIDs.get(ii) - y.pointIDs.get(ii); 
      }
    }
    return 0;
  }
  
  @Override
  public boolean equals(Object other) {
    return compareTo((TriPosition) other) == 0;
  }
  
  @Override
  public int hashCode() {
    return pointIDs.hashCode();
  }
}
