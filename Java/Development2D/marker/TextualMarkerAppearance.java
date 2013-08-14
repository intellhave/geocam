package marker;

import de.jreality.geometry.Primitives;
import de.jreality.scene.PointSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;

public class TextualMarkerAppearance extends MarkerAppearance {
	private String text;

	public TextualMarkerAppearance(String text, double scale) {
		this.text = text;
		super.model = ModelType.TEXT;
		this.setSize(scale);
		this.setDefaultScale(1.0);
	}

	public SceneGraphComponent makeSceneGraphComponent() {
		double[] psn = { 0, 0, 0 };
		PointSet ps = Primitives.point(psn);

		SceneGraphComponent sgc = new SceneGraphComponent();
		sgc.setGeometry(ps);
		
//		Appearance app = super.getBasicAppearance();
//		app.setAttribute(CommonAttributes.VERTEX_DRAW, true);
//		sgc.setAppearance(super.getBasicAppearance());
//		sgc.setTransformation(super.getScalngTransformation());

		String[] labels = { this.text };		
		ps.setVertexAttributes(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
		return sgc;
	}

	/*
	 * Below is some other labeling code that worked. I am leaving this here,
	 * because we have had some trouble with labels and jreality in the past,
	 * and might need to take a different approach like the one below in the
	 * future.
	 */
	// // create the image
	// Font f = new Font("Sans Serif", Font.PLAIN, 24);
	// Image img = LabelUtility.createImageFromString(text, f, Color.blue);
	//
	// Appearance app = new Appearance();
	// app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
	// app.setAttribute(CommonAttributes.EDGE_DRAW, false);
	// app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
	//
	// Texture2D tex = TextureUtility.createTexture(app, "polygonShader",
	// new ImageData(img));
	//
	// SceneGraphComponent cmp = new SceneGraphComponent();
	// MatrixBuilder.euclidean().rotateX(Math.PI).assignTo(cmp);
	//
	// double eps = 0.25;
	// double ratio = img.getWidth(null) / (img.getHeight(null) + 0.001);
	// double h = 1.0;
	// double w = ratio * h;
	// double[] points = { 0, 0, eps, w, 0, eps, w, h, eps, 0, h, eps };
	// IndexedLineSet quad = Primitives.texturedQuadrilateral(points);
	// cmp.setAppearance(app);
	// cmp.setGeometry(quad);
	//
	// return cmp;
}
