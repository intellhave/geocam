package view;

import java.awt.Color;

import triangulation.Face;
import triangulation.Triangulation;
import view.Development.DevelopmentNode;


public class ColorScheme {
	public enum schemes {
		DEPTH, FACE
	};

	private schemes scheme;

	public ColorScheme(schemes s) {
		scheme = s;
	}

	public Color getColor(DevelopmentNode node) {
		if (node.isRoot())
			return Color.WHITE;

		if (scheme == schemes.FACE) {
			Face f = node.getFace();
			return Color.getHSBColor((float) f.getIndex()
					/ (float) Triangulation.faceTable.size(), 0.5f, 0.9f);
		} else {
			return Color.getHSBColor((float) node.getDepth() / 15f, 0.5f, 0.9f);
		}
	}

	public schemes getSchemeType() {
		return scheme;
	}
}
