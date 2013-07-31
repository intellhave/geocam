package view;

import java.awt.Color;
import java.util.EnumMap;

import triangulation.Face;
import triangulation.Triangulation;
import view.TextureLibrary.TextureDescriptor;

/*********************************************************************************
 * FaceAppearanceScheme
 * 
 * This class is responsible for playing the role of ColorScheme in previous
 * versions of our code. It is responsible for providing mappings from faces to
 * textures and/or colors.
 *********************************************************************************/
public class FaceAppearanceScheme {

	public enum Colors {
		SKY_BLUE, TURQUOISE, STRAWBERRY_RED, BRICK_RED, GRASS_GREEN, FOREST_GREEN, PASTEL_YELLOW, BRIGHT_ORANGE, MOCCASSIN, ORCHID_PURPLE, PLAIN_PURPLE, PASTEL_PINK, HOT_PINK, KHAKI, SIENNA, DARK_GRAY, DEFAULT
	}

	private static EnumMap<Colors, TextureDescriptor> colorMap;
	
	static { 
		colorMap = new EnumMap<Colors, TextureDescriptor>(Colors.class);
		colorMap.put(Colors.SKY_BLUE, TextureDescriptor.BATHROOMTILE);
		colorMap.put(Colors.TURQUOISE, TextureDescriptor.CHECKER);
		colorMap.put(Colors.STRAWBERRY_RED, TextureDescriptor.CLAY);
		colorMap.put(Colors.BRICK_RED, TextureDescriptor.COBBLESTONE);
		colorMap.put(Colors.GRASS_GREEN, TextureDescriptor.DOTS);
		colorMap.put(Colors.FOREST_GREEN, TextureDescriptor.GRID);
		colorMap.put(Colors.PASTEL_YELLOW, TextureDescriptor.LIGHTHOUSE);
		colorMap.put(Colors.BRIGHT_ORANGE, TextureDescriptor.PLAID);
		colorMap.put(Colors.MOCCASSIN, TextureDescriptor.STUCCO);
		colorMap.put(Colors.ORCHID_PURPLE, TextureDescriptor.SWIRLS);
		colorMap.put(Colors.PLAIN_PURPLE, TextureDescriptor.BATHROOMTILE);
		colorMap.put(Colors.PASTEL_PINK, TextureDescriptor.MARBLE);
		colorMap.put(Colors.HOT_PINK, TextureDescriptor.CLAY);
		colorMap.put(Colors.KHAKI, TextureDescriptor.COBBLESTONE);
		colorMap.put(Colors.SIENNA, TextureDescriptor.DOTS);
		colorMap.put(Colors.DARK_GRAY, TextureDescriptor.ZIGZAG);
		colorMap.put(Colors.DEFAULT, TextureDescriptor.ZIGZAG);
	}

	/*********************************************************************************
	 * getColor
	 *********************************************************************************/
	public Color getColor(Face f) {
		if (f.hasColor())
			return f.getColor();
		return Color.getHSBColor((float) f.getIndex()
				/ (float) Triangulation.faceTable.size(), 0.5f, 0.9f);
	}

	/*********************************************************************************
	 * getTextureDescriptor
	 * 
	 * For now, this code provides a consistent mapping from faces to textures.
	 * In the future, we will allow the mapping to be customizable.
	 *********************************************************************************/
	public TextureDescriptor getTextureDescriptor(Face f) {
		if (f.hasColor()) {
			Color faceColor = getColor(f);
			switch (faceColor.getRGB()) {
			case -16734503:
				return colorMap.get(Colors.SKY_BLUE);
			case -12525359:
				return colorMap.get(Colors.TURQUOISE);
			case -55770:
				return colorMap.get(Colors.STRAWBERRY_RED);
			case -5037791:
				return colorMap.get(Colors.BRICK_RED);
			case -16721638:
				return colorMap.get(Colors.GRASS_GREEN);
			case -14578655:
				return colorMap.get(Colors.FOREST_GREEN);
			case -218:
				return colorMap.get(Colors.BRIGHT_ORANGE);
			case -169:
				return colorMap.get(Colors.PASTEL_YELLOW);
			case -7243:
				return colorMap.get(Colors.MOCCASSIN);
			case -4565804:
				return colorMap.get(Colors.ORCHID_PURPLE);
			case -6217232:
				return colorMap.get(Colors.PLAIN_PURPLE);
			case -19006:
				return colorMap.get(Colors.PASTEL_PINK);
			case -38475:
				return colorMap.get(Colors.HOT_PINK);
			case -989556:
				return colorMap.get(Colors.KHAKI);
			case -6204882:
				return colorMap.get(Colors.SIENNA);
			case -9868951:
				return colorMap.get(Colors.DARK_GRAY);
			default:
				// This returns a random texture based on the RGB components of
				// the color
				return TextureDescriptor.values()[(Math.abs(faceColor.getRGB()
						% TextureDescriptor.values().length))];
			}
		}

		int mf;
		if (f.hasMetaFace()) {
			mf = f.getMetaFace().get(0);
		} else {
			mf = f.getIndex();
		}

		int textureCount = 16;
		switch (mf % textureCount) {
		case 0:
			return colorMap.get(Colors.SKY_BLUE);
		case 1:
			return colorMap.get(Colors.TURQUOISE);
		case 2:
			return colorMap.get(Colors.STRAWBERRY_RED);
		case 3:
			return colorMap.get(Colors.BRICK_RED);
		case 4:
			return colorMap.get(Colors.GRASS_GREEN);
		case 5:
			return colorMap.get(Colors.FOREST_GREEN);
		case 6:
			return colorMap.get(Colors.BRIGHT_ORANGE);
		case 7:
			return colorMap.get(Colors.PASTEL_YELLOW);
		case 8:
			return colorMap.get(Colors.MOCCASSIN);
		case 9:
			return colorMap.get(Colors.ORCHID_PURPLE);
		case 10:
			return colorMap.get(Colors.PLAIN_PURPLE);
		case 11:
			return colorMap.get(Colors.PASTEL_PINK);
		case 12:
			return colorMap.get(Colors.HOT_PINK);
		case 13:
			return colorMap.get(Colors.KHAKI);
		case 14:
			return colorMap.get(Colors.SIENNA);
		default:
			return colorMap.get(Colors.DARK_GRAY);
		}

	}
}
