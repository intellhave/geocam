import de.jreality.geometry.Primitives;
import static de.jreality.shader.CommonAttributes.LINE_SHADER;
import static de.jreality.shader.CommonAttributes.POINT_SHADER;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.JRViewer.ContentType;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentTools;
import de.jreality.reader.Readers;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.util.Input;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;
import de.jreality.scene.Appearance;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.scene.data.DoubleArray;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import static de.jreality.shader.CommonAttributes.EDGE_DRAW;
import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;

import java.awt.Color;
import java.io.IOException;

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
