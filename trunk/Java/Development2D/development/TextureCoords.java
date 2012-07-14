package development;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import triangulation.Edge;
import triangulation.Face;
import triangulation.FaceGrouping;
import triangulation.Triangulation;
import triangulation.Vertex;

/*********************************************************************************
 * TextureCoords
 * 
 * This class is responsible for computing then storing the texture coordinates
 * for all faces in the triangulation. It contains a map from Faces => Texture
 * coordinates. When someone wants to get texture coordinates for a particular
 * face from this map, it returns the value if it has already been computed,
 * otherwise it computes it and adds it to its map. Since the data and methods
 * in this class are static, it is important that the class be reset every time
 * a new manifold is loaded into Triangulation.
 *********************************************************************************/

public class TextureCoords {

  private static HashMap<Face, List<Vector>> coordMap = new HashMap<Face, List<Vector>>();

  /*********************************************************************************
   * getCoords
   * 
   * This method checks if the requested coordinates have already been computed.
   * If they have not, it gets the FaceBundle associated with that face and
   * computes the coordinates for all faces in the bundle using a recursive
   * algorithm before returning the value.
   *********************************************************************************/
  public static List<Vector> getCoords(Face f) {
    if (coordMap.keySet().contains(f))
      return coordMap.get(f);
    
    //compute and map the coords for the entire face bundle
    FaceGrouping fg = Triangulation.groupTable.get(f);
    List<Face> faces = fg.getFaces();
    Face first = faces.get(0);
    // get the Coord2D values for first and use them as first's tex coords
    List<Vector> standCoords = new ArrayList<Vector>();
    for (Vertex v : first.getLocalVertices()) {
      Vector coords = Coord2D.coordAt(v, first);
      standCoords.add(coords);
    }
    coordMap.put(first, standCoords);
    AffineTransformation identity = new AffineTransformation(2);
    buildCoords(first, identity);

    return coordMap.get(f);
  }

  /*********************************************************************************
   * buildCoords
   * 
   * A recursive helper method for computing texture coordinates. This method
   * uses a composition of affine transformations to compute the texture
   * coordinates of the three vertices on a given face. It gets the standard
   * coordinates associated with the face's vertices using the Coord2D geoquant,
   * then applies the appropriate affine transformation to line the face up with
   * the previous face in the grouping.
   *********************************************************************************/

  private static void buildCoords(Face f, AffineTransformation old) {
    for (Edge e : f.getLocalEdges()) {
      Face newFace = DevelopmentComputations.getNewFace(f, e);
      if (newFace == null)
        continue;
      if(coordMap.containsKey(newFace)) continue;
      FaceGrouping group = Triangulation.groupTable.get(f);
      if (group.contains(newFace)) {
        // generate standard coordinates for the faces
        // This code uses the standard coordinates of the vertices on the
        //common edge to find an affine transformation gluing the faces
        //together along that edge
        List<Vertex> verts = e.getLocalVertices();
        Vector[] qverts = new Vector[3];
        Vector[] pverts = new Vector[3];
        int index = 0;
        for (Vertex v : verts) {
          Vector coords = Coord2D.coordAt(v, newFace);
          pverts[index] = coords;
          Vector otherCoords = Coord2D.coordAt(v, f);
          qverts[index] = otherCoords;
          index++;
        }
        //to use the AffineTransformation constructor, need the "spare" vertices too
        LinkedList<Vertex> qvertsLeftover = new LinkedList<Vertex>(f.getLocalVertices());
        qvertsLeftover.removeAll(e.getLocalVertices());
        Vertex q = qvertsLeftover.get(0);
        Vector leftover_q = Coord2D.coordAt(q, f);
        qverts[2] = leftover_q;
        
        LinkedList<Vertex> pvertsLeftover = new LinkedList<Vertex>(newFace.getLocalVertices());
        pvertsLeftover.removeAll(e.getLocalVertices());
        Vertex p = pvertsLeftover.get(0);
        Vector leftover_p = Coord2D.coordAt(p, newFace);
        pverts[2] = leftover_p;

        AffineTransformation newTrans = new AffineTransformation(2);
        
        try{
          newTrans = new AffineTransformation(pverts, qverts);
        } catch(Exception exception){
          System.err.println("Error: Could not find AffineTransformation to compute texture coordinates");
          exception.printStackTrace();
          System.exit(1);
        }
        
        //compose the affine transformations so far and apply them to all
        //three vertices' standard coords
        newTrans.leftMultiply(old);
        List<Vector> standCoords = new ArrayList<Vector>();
        List<Vector> texCoords = new ArrayList<Vector>();
        for (Vertex v : newFace.getLocalVertices()) {
          Vector coords = Coord2D.coordAt(v, newFace);
          standCoords.add(coords);
        }
        for (Vector vec : standCoords) {
          Vector textureCoordinate = newTrans.affineTransPoint(vec);
          texCoords.add(textureCoordinate);
        }
        coordMap.put(newFace, texCoords);
        buildCoords(newFace, newTrans);
      }
    }
  }

  /*********************************************************************************
   * getCoordsAsArray
   * 
   * This method returns the texture coordinates as a double[][] rather than a
   * list of vectors. This allows the EmbeddedFace view to get the coordinates
   * and hand them directly to jReality's IndexedFaceSetFactory. Note that the
   * other views get the texture coordinates from EmbeddedFace objects so never
   * need to use this method.
   *********************************************************************************/
  public static double[][] getCoordsAsArray(Face f) {
    List<Vector> texVerts = getCoords(f);
    double[][] texCoords = new double[texVerts.size()][texVerts.get(0).getDimension()];
    for(int ii = 0; ii < texVerts.size(); ii++) {
      Vector v = texVerts.get(ii);
      for(int jj = 0; jj < v.getDimension(); jj++ ){
        texCoords[ii][jj] = v.getComponent(jj);
      }
    }
    
    return texCoords;
  }

  /*********************************************************************************
   * reset
   * 
   * Allows the class's data to be wiped clean every time a new triangulation is
   * read into the program.
   *********************************************************************************/
  public static void reset() {
    coordMap.clear();
  }
}
