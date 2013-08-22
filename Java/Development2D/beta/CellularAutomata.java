package beta;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Simplex;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Vector;
import view.TextureLibrary;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.shader.ImageData;
import de.jreality.shader.Texture2D;
import de.jreality.shader.TextureUtility;
import development.Coord2D;

/* TODO:
 * x Each edge is divided into cells, shared between both faces
 * x random (uniform) distribution of cells on each face
 * - get an ordering and orientation for edge cells
 * ? threshold for connection of cell based on distance between midpoints
 * x non-edge cells connect to edge cells
 * - now you have a(n embedded) graph! PARTY
 */

/**
 * The class CellularAutomata should define a graph upon the manifold, which can
 * then be extended by a rule (e.g. Game Of Life) to create a network of
 * animated cells.
 * 
 * @author Tanner Prynn
 * 
 */
public class CellularAutomata extends AnimatedTexture {

  protected class Cell {
    private int index;
    private int hashcode;
    private int state = 0;
    private int nextState;
    int neighborCount;
    int liveNeighbors;

    private Simplex localSimplex;
    private Set<Cell> neighbors = new HashSet<Cell>();

    private Vector location;

    public Cell(Simplex s, int index) {
      this.index = index;
      localSimplex = s;
      hashcode = this.getClass().hashCode()
          ^ (new Integer(this.index)).hashCode();
    }

    public Cell(Face f, int index, Vector location) {
      localSimplex = (Simplex) f;
      this.location = location;
      this.index = index;
      hashcode = this.getClass().hashCode()
          ^ (new Integer(this.index)).hashCode();
    }

    public int index() {
      return index;
    }

    public int state() {
      return state;
    }
    
    public void computeNextState() {
      
    }
    
    public void nextState() {
      state = nextState;
    }

    public List<Face> getLocalFaces() {
      return localSimplex.getLocalFaces();
    }

    public Vector getLocation() {
      return new Vector(location);
    }

    public void addNeighbor(Cell cell) {
      neighbors.add(cell);
      cell.neighbors.add(this);
    }

    public Set<Cell> getNeighbors() {
      return Collections.unmodifiableSet(neighbors);
    }

    @Override
    public boolean equals(Object other) {
      if (!other.getClass().equals(Cell.class))
        return false;
      if (((Cell) other).index() == index)
        return true;
      return false;
    }

    @Override
    public int hashCode() {
      return hashcode;
    }
  }

  private static int cellCounter = 0;
  private HashMap<Simplex, Set<Cell>> cellmap = new HashMap<Simplex, Set<Cell>>();

  public CellularAutomata() {
    createEdgeCells();
    createVertexCells();
    createFaceCells();
    
    connectCells();
  }
  
  private static final int EDGE_SEGMENTS = 5;
  /**
   * Attach cells along all edges. There are EDGE_SEGMENTS - 1 cells on each
   * edge.
   */
  private void createEdgeCells() {
    Set<Cell> cells;
    Cell c;
    for (Edge e : Triangulation.edgeTable.values()) {
      cells = new HashSet<Cell>();
      cellmap.put(e, cells);

      for (int i = 0; i < EDGE_SEGMENTS; i++) {
        c = new Cell(e, cellCounter);
        cellCounter++;
        cells.add(c);
      }
    }
  }

  /**
   * Attach a cell to each vertex.
   */
  private void createVertexCells() {
    Set<Cell> cells;
    Cell c;
    for (Vertex v : Triangulation.vertexTable.values()) {
      cells = new HashSet<Cell>();
      cellmap.put(v, cells);

      c = new Cell(v, cellCounter);
      cellCounter++;
      cells.add(c);
    }
  }
  
  private static final int FACE_CELL_COUNT = 5;
  private boolean print = false;
  /**
   * Scatter FACE_CELL_COUNT cells on each face in random locations.
   */
  private void createFaceCells() {
    Set<Cell> cells;
    Cell c;
    Random r = new Random();
    for (Face f : Triangulation.faceTable.values()) {
      cells = new HashSet<Cell>();
      cellmap.put(f, cells);

      ArrayList<Vector> vertices = new ArrayList<Vector>();
      //System.out.println(f.toString());
      for (Vertex v : f.getLocalVertices()) {
        vertices.add(Coord2D.coordAt(v, f));
        //System.out.print(Coord2D.coordAt(v, f) + " ");
      }
      //System.out.println();

      for (int i = 0; i < FACE_CELL_COUNT; i++) {
        double r1 = r.nextDouble();
        double r2 = r.nextDouble();

        /*
         * Take a random point inside the triangle formed by the vertices of the
         * face See: http://www.cs.princeton.edu/~funk/tog02.pdf Note: There
         * should always be exactly three vertices
         */
        Vector v1 = new Vector(vertices.get(0));
        v1.scale(1 - Math.sqrt(r1));
        Vector v2 = new Vector(vertices.get(1));
        v2.scale(Math.sqrt(r1) * (1 - r2));
        Vector v3 = new Vector(vertices.get(2));
        v3.scale(r2 * Math.sqrt(r1));

        Vector location = new Vector(0, 0);
        location.add(v1);
        location.add(v2);
        location.add(v3);
        
        if(print) {
          System.out.println(location.toString());
        }

        c = new Cell(f, cellCounter, location);
        cellCounter++;
        cells.add(c);
      }
      print = false;
    }
  }
  
  /**
   * Connect the graph. Each face is a complete subgraph. Vertices are
   * connected to cells on their local edges. Edge cells are connected to all
   * other cells on the same edge.
   */
  private void connectCells() {
    for (Vertex v : Triangulation.vertexTable.values()) {
      Cell c1 = cellmap.get(v).iterator().next();
      // For each vertex cell
      for (Edge e : v.getLocalEdges()) {
        // Connect all cells on edges adjacent to that vertex to that vertex cell
        for (Cell c2 : cellmap.get(e)) {
          c1.addNeighbor(c2);
        }
      }
    }

    for (Edge e : Triangulation.edgeTable.values()) {
      // For each cell on each edge
      for (Cell c1 : cellmap.get(e)) {
        // Connect to every other cell on that edge
        for (Cell c2 : cellmap.get(e)) {
          if (!c1.equals(c2))
            c1.addNeighbor(c2);
        }
      }
    }

    for (Face f : Triangulation.faceTable.values()) {
      // For each cell on each face
      for (Cell c0 : cellmap.get(f)) {
        // Connect to all other cells on that face
        for (Cell c1 : cellmap.get(f)) {
          c0.addNeighbor(c1);
        }

        // Connect to all cells on edges incident on that face
        for (Edge e : f.getLocalEdges()) {
          for (Cell c1 : cellmap.get(e)) {
            c0.addNeighbor(c1);
          }
        }

        // Connect to all cells on vertices incident on that face
        for (Vertex v : f.getLocalVertices()) {
          for (Cell c1 : cellmap.get(v)) {
            c0.addNeighbor(c1);
          }
        }
      }
    }
  }

  private boolean c = false;
  @Override
  public void update() {
    if(c) return;
    for(Set<Cell> cells : cellmap.values()) {
      for(Cell c : cells) {
        c.computeNextState();
      }
    }
    
    for(Set<Cell> cells : cellmap.values()) {
      for(Cell c : cells) {
        c.nextState();
      }
    }
    setChanged();
    c = true;
  }

  private static final int IMG_WIDTH = 128;
  private static final int IMG_HEIGHT = 128;
  @Override
  public Appearance getCurrentAppearance(Face face) {
    Appearance app = new Appearance();
    
    double width = 0, height = 0, x, y;
    int x_i, y_i;
    byte pixelArray[][][] = new byte[IMG_WIDTH][IMG_HEIGHT][4];
    
    for(Vertex vert : face.getLocalVertices()) {
      x = Coord2D.coordAt(vert, face).getComponent(0);
      y = Coord2D.coordAt(vert, face).getComponent(1);
      
      if(x > width) width = x;
      if(y > height) height = y;
    }
    for(Cell c : cellmap.get(face)) {
      Vector location = c.getLocation();
      x = location.getComponent(0) / width;
      y = location.getComponent(1) / height;
      
      x_i = (int) Math.round(x * IMG_WIDTH);
      y_i = (int) Math.round(y * IMG_HEIGHT);
      
      //System.out.println(x + ", " + y + " => " + x_i + ", " + y_i);
      
      pixelArray[x_i][y_i][0] = 0;
      pixelArray[x_i][y_i][1] = 0;
      pixelArray[x_i][y_i][2] = 0;
      pixelArray[x_i][y_i][3] = 127;
    }
    
    byte[] data = new byte[4*IMG_WIDTH*IMG_HEIGHT];
    
    for(int i = 0; i < IMG_HEIGHT; i++) {
      for(int j = 0; j < IMG_WIDTH; j++) {
        for(int k = 0; k < 4; k++) {
          data[(i*IMG_WIDTH)+(j*4)+k] = pixelArray[i][j][k];
        }
      }
    }
    
    // Create a red border
    for(int i = 0; i < IMG_HEIGHT; i++) {
      for(int k = 0; k < 4; k++) {
        
      }
    }
    
    ImageData id = new ImageData(data, IMG_WIDTH, IMG_HEIGHT);
    TextureLibrary.initializeShaders(app, Color.GREEN);
    Texture2D tex = TextureUtility.createTexture(app, POLYGON_SHADER, id);
    tex.setTextureMatrix(MatrixBuilder.euclidean().scale(1).getMatrix());
    return app;
  }

  @Override
  public double getScale() {
    return 1;
  }
  
  @Override
  public String getName() {
    return "Cellular Automata";
  }
}
