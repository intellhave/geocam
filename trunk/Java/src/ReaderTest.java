import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;
import static de.jreality.shader.CommonAttributes.LINE_SHADER;
import static de.jreality.shader.CommonAttributes.POINT_SHADER;

import java.awt.Color;
import java.io.IOException;

import de.jreality.geometry.Primitives;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.Appearance;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.SceneGraphComponent;

public class ReaderTest {
	public static void main(String args[])throws IOException{

	Appearance app = new Appearance();
	SceneGraphComponent sgc = new SceneGraphComponent();
	IndexedLineSet ils = Primitives.arrow(0, 0, 1, 1, .25);
  sgc.setGeometry(ils);
  app.setAttribute(LINE_SHADER+"."+ DIFFUSE_COLOR, Color.red);
  app.setAttribute(POINT_SHADER + "." +DIFFUSE_COLOR, Color.red);
  
  sgc.setAppearance(app);
 
	JRViewer.display(sgc);
	
	}
}
