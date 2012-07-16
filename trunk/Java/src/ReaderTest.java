import de.jreality.plugin.JRViewer;
import de.jreality.plugin.JRViewer.ContentType;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentTools;
import de.jreality.reader.Readers;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.util.Input;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;
import de.jreality.scene.Appearance;
import static de.jreality.shader.CommonAttributes.EDGE_DRAW;

import java.io.IOException;

public class ReaderTest {
	public static void main(String args[])throws IOException{
	Input input = Input.getInput("Data/surfaces/Test_cube.off");
	SceneGraphComponent content = Readers.read(input);
	Appearance app = new Appearance();
	app.setAttribute(EDGE_DRAW, false);
	content.setAppearance(app);
	JRViewer.display(content);
	
	}
}
