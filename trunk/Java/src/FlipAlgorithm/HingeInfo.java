package FlipAlgorithm;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;


//the names of the various values correspond to the pictures below
/*       v2
         /|\
        / | \
  e2   /  |  \ e3
      /   |   \
     /    |e0  \
    /     |     \
v1  \ f0  | f1  / v3
     \    |    /
      \   |   /
  e1   \  |  / e4
        \ | /
         \|/
         v0

         v2
         / \
        /   \
  e2   /     \ e3
      /       \
     /    f1   \
    /           \
    -------------
v1  \    e0     / v3
     \         /
      \  f0   /
  e1   \     / e4
        \   /
         \ /
         v0
*/

//indices in arrays correspond to labeling above
public class HingeInfo {
  public enum HingeType {PositivePositive, PositiveNegative, NegativeNegative};
  
  Vertex[] vertices = new Vertex[4];
  Edge[] edges = new Edge[5];
  Face[] embeddedFaces = new Face[2];
}