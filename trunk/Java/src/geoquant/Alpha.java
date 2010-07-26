package geoquant;

import java.util.HashMap;

import triangulation.Vertex;

/**
 * The Alpha geoquant is defined on  vertex and is used to modify the
 * calculation of edge length as follows:
 * 
 * l_ij = alpha_i * r_i^2 + alpha_j * r_j^2 + 2 * r_i * r_j * eta_ij
 * 
 * An alpha geoquant does not depend on any other quantities.
 * 
 * @author Alex Henniges
 *
 */
public class Alpha extends Geoquant {
  // This is the map that holds all instances of Alpha geoquants.
  // Each alpha is unique up to its vertex's serial number.
  private static HashMap<TriPosition, Alpha> Index = new HashMap<TriPosition, Alpha>();
  
  // The private constructor can't be called outside the class.
  private Alpha(Vertex v) {
    super(v); // ALWAYS have to call this first
    value = 0; // default, may want to change back to 1 at some point.
  }
  
  /**
   * This static method provides access to an Alpha geoquant, represented
   * by its vertex. If an Alpha geoquant already exists at vertex v, that
   * reference is returned, otherwise, a new Alpha geoquant is created.
   * 
   * @param v - The incident vertex of the desired alpha.
   * @return The alpha geoquant at vertex v.
   */
  public static Alpha at(Vertex v) {
    // The TriPosition is created with serial numbers.
    // Note that serial numbers are unique across all simplices.
    TriPosition T = new TriPosition( v.getSerialNumber() );
    // Attempt to get the geoquant from the construction map.
    // If it does not yet exist, the map will return null.
    Alpha q = Index.get(T);
    if(q == null) {
      // Create the geoquant since it does not yet exist.
      q = new Alpha(v);
      q.pos = T;
      // Add the geoquant to the map.
      Index.put(T, q);
    }
    return q;
  }
  
  /**
   * This method is a shorthand way to both access the Alpha geoquant 
   * and retrieve its value. In other words, this method is equivalent
   * to:
   * 
   * Alpha.at(v).getValue()
   * 
   * One important this to note is that it is more efficient in terms of
   * processing time if the user maintains a reference to the Alpha geoquant
   * instead of repeated calls to the at() function, which requires map
   * look-up time to complete.
   * 
   * @param v - The incident vertex of the desired alpha.
   * @return The value of the Alpha geoquant at vertex v.
   */
  public static double valueAt(Vertex v) {
    return at(v).getValue();
  }
  
  protected void recalculate() {
    // This is empty for a alpha
    value = 0;
  }
 
  public void remove() {
    deleteDependents();
    Index.remove(pos);
  }

}
