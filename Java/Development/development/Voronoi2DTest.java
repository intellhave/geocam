package development;

import java.awt.Color;
import java.util.ArrayList;

import de.jreality.plugin.JRViewer;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;

public class Voronoi2DTest {

  public static void main(String[] args){
    
    //geometry for face
    ArrayList<Vector> vlist = new ArrayList<Vector>();
    vlist.add(new Vector(-1,-1));
    vlist.add(new Vector(1,-1));
    vlist.add(new Vector(1,1));
    vlist.add(new Vector(-1,1));
    EmbeddedFace f = new EmbeddedFace(vlist);
    Geometry g = f.getGeometry(Color.RED);
    
    SceneGraphComponent sgc_face = new SceneGraphComponent();
    sgc_face.setGeometry(g);
    
    //geometry for points
    //Vector[] voropts = new Vector[] {
    //    new Vector(-.5,0),
    //    new Vector(.5,.3),
    //    new Vector(.5,-.3) };
    Vector[] voropts = new Vector[] {
        new Vector(-.5,0),
        new Vector(.5,.3),
        new Vector(.5,-.3)  };
    SceneGraphComponent sgc_voropts = DevelopmentApp2D.sgcFromPoints2D(voropts);
    
    //root SGC
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    sgc_root.addChild(sgc_face);
    sgc_root.addChild(sgc_voropts);
    
    //geometry for voronoi cells
    Voronoi2D vdiag = new Voronoi2D(f,voropts);
    SceneGraphComponent[] sgc_cells = new SceneGraphComponent[voropts.length];
    for(int i=0; i<voropts.length; i++){
      sgc_cells[i] = new SceneGraphComponent();
      sgc_cells[i].setGeometry(vdiag.getCellAt(i).getGeometry(Color.BLUE));
      sgc_root.addChild(sgc_cells[i]);
    }
    
    //display
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.startup();
  }
}
