package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import triangulation.Face;
import view.Development.DevelopmentNode;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;

public class SGCTree {
	private SGCNode root;
	private ColorScheme colorScheme;
	private int dimension;
	private final double simulated3DHeight = 0.08;

	public SGCTree(Development d, ColorScheme scheme, int dim) {
		dimension = dim;
		colorScheme = scheme;
		root = new SGCNode(d.getRoot(), dimension);
		buildTree(root, d.getRoot());
		setVisibleDepth(d.getDesiredDepth());
	}

	public void setColorScheme(ColorScheme scheme) {
		colorScheme = scheme;
		changeColors(root);
	}

	private void changeColors(SGCNode node) {
		node.updateColor();
		Iterator<SGCNode> itr = node.getChildren().iterator();
		while (itr.hasNext()) {
			changeColors(itr.next());
		}
	}

	public void setVisibleDepth(int depth) {
		setVisibility(root, depth);
	}

	private void setVisibility(SGCNode node, int depth) {
		if (node.getDepth() <= depth)
			node.getSGC().setVisible(true);
		else
			node.getSGC().setVisible(false);
		Iterator<SGCNode> itr = node.getChildren().iterator();
		while (itr.hasNext()) {
			setVisibility(itr.next(), depth);
		}
	}

	private void buildTree(SGCNode parent, DevelopmentNode node) {
		SGCNode newNode = new SGCNode(node, dimension);
		parent.addChild(newNode);
		Iterator<DevelopmentNode> itr = node.getChildren().iterator();
		while (itr.hasNext()) {
			buildTree(newNode, itr.next());
		}
	}

	public SGCNode getRoot() {
		return root;
	}

	public static Appearance getFaceAppearance(double transparency) {

		// create appearance for developed faces
		Appearance app_face = new Appearance();

		// set some basic attributes
		app_face.setAttribute(CommonAttributes.FACE_DRAW, true);
		app_face.setAttribute(CommonAttributes.EDGE_DRAW, true);
		app_face.setAttribute(CommonAttributes.VERTEX_DRAW, false);
		app_face.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
		app_face.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);

		// set shaders
		DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
				.createDefaultGeometryShader(app_face, true);

		// line shader
		DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
		dls.setTubeDraw(false);
		dls.setLineWidth(0.0);
		dls.setDiffuseColor(Color.BLACK);

		// polygon shader
		DefaultPolygonShader dps = (DefaultPolygonShader) dgs
				.getPolygonShader();
		dps.setDiffuseColor(Color.WHITE);
		dps.setTransparency(transparency);

		return app_face;
	}

	public class SGCNode {
		private SceneGraphComponent sgc;
		private DevelopmentNode node;
		private ArrayList<SGCNode> children;
		private int dimension;

		public SGCNode(DevelopmentNode n, int dim) {
			dimension = dim;
			node = n;
			sgc = new SceneGraphComponent();
			if (dimension == 3) {
				sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
						colorScheme.getColor(node), simulated3DHeight));
				sgc.setAppearance(getFaceAppearance(0.5f));
			} else {
				sgc.setGeometry(node.getEmbeddedFace().getGeometry(
						colorScheme.getColor(node)));
				sgc.setAppearance(getFaceAppearance(0.5f));
			}
			children = new ArrayList<SGCNode>();
		}

		public int getDepth() {
			return node.getDepth();
		}

		public void updateColor() {
			if (dimension == 3)
				sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
						colorScheme.getColor(node), simulated3DHeight));
			else
				sgc.setGeometry(node.getEmbeddedFace().getGeometry(
						colorScheme.getColor(node)));
		}

		public void addChild(SGCNode node) {
			children.add(node);
		}

		public Face getFace() {
			return node.getFace();
		}

		public ArrayList<SGCNode> getChildren() {
			return children;
		}

		public SceneGraphComponent getSGC() {
			return sgc;
		}
	}
}
