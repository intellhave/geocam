package editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * Self-contained Swing causes spaghetti code -- sorry.
 */

/**
 * A runnable class which provides a GUI for generating textures based on Conway's Game-Of-Life.
 * @author Tanner Prynn
 */
public class TextureGenerator {
	
	private static JFrame menuFrame;
	public static void main(String[] args) {
		menuFrame = new JFrame("Options");
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.add(new TextureGeneratorMenu());
		menuFrame.pack();
		menuFrame.setVisible(true);
	}
	
	/**
	 * Create a BufferedImage of a single color.
	 */
	public static BufferedImage createColoredImage(int width, int height, Color c) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getWidth(); y++) {
				image.setRGB(x, y, c.getRGB());
			}
		}
		
		return image;
	}
	
	private final static int CELL_WIDTH = 5;
	private final static int CELL_HEIGHT = 5;

	/**
	 * Create a BufferedImage based on Conway's Game-of-Life on the torus, with
	 * each cell drawn as a CELL_WIDTHxCELL_HEIGHT block of pixels.
	 * 
	 * @param width
	 *            The width of the resulting image, which will be rounded up to
	 *            the nearest number divisible by CELL_WIDTH
	 * @param height
	 *            The height of the resulting image, which will be rounded up to
	 *            the nearest number divisible by CELL_HEIGHT
	 * @param liveColor
	 *            The color drawn for each live cell
	 * @param deadColor
	 *            The color drawn wherever there is not a live cell
	 * @param cells
	 *            The number of cells to initialize randomly -- keep in mind
	 *            that the total number of grid spots is approximately
	 *            <b>width</b>*<b>height</b>/(CELL_WIDTH*CELL_HEIGHT)
	 * @param generations
	 *            The number of generations to run the Game-Of-Life simulation.
	 *            Zero generations means a random grid of <b>cells</b> cells.
	 */
	public static BufferedImage createGameOfLifeImage(int width, int height,
			Color liveColor, Color deadColor, int cells, int generations) {
		width = width + width % CELL_WIDTH;
		height = height + height % CELL_HEIGHT;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		byte[][] game = new byte[width/CELL_WIDTH][height/CELL_HEIGHT];
		
		Random r = new Random();
		while(cells > 0) {
			int x = r.nextInt(game.length);
			int y = r.nextInt(game[0].length);
			if(game[x][y] == 0) {
				game[x][y] = 1;
				cells--;
			}
		}
		
		for(int i = 0; i < generations; i++) {
			game = stepGOL(game);
		}
		
		for(int i = 0; i < game.length; i++) {
			for(int j = 0; j < game[0].length; j++) {
				for(int m = 0; m < CELL_WIDTH; m++) {
					for(int n = 0; n < CELL_HEIGHT; n++) {
						if(game[i][j] == 0) {
							image.setRGB(i*CELL_WIDTH + n, j*CELL_HEIGHT + m, deadColor.getRGB());
						}
						else {
							image.setRGB(i*CELL_WIDTH + n, j*CELL_HEIGHT + m, liveColor.getRGB());
						}
					}
				}
			}
		}
		
		return image;
	}
	
	/**
	 * Return the next generation, given some 2D game-of-life array.
	 * @param game Game-Of-Life board at time i
	 * @return new Game-Of-Life board for time i+1
	 */
	private static byte[][] stepGOL(byte[][] game) {
		byte[][] res = new byte[game.length][game[0].length];
		
		for(int x = 0; x < game.length; x++) {
			for(int y = 0; y < game.length; y++) {
				int count = neighborCount(game, x, y);
				
				if(game[x][y] == 1 && (count == 2 || count == 3)) {
					res[x][y] = 1;
				} 
				else if(count == 3) {
					res[x][y] = 1;
				} 
				else {
					res[x][y] = 0;
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Neighbor count for game-of-life on a torus.
	 * @param game
	 * @param x
	 * @param y
	 * @return The number of live neighbors surrounding a cell
	 */
	private static int neighborCount(byte[][] game, int x, int y) {
		int res = 0;
		
		int xm; // x-1
		if(x-1 < 0)
			xm = game.length-1;
		else
			xm = x-1;
		
		int ym; // y-1
		if(y-1 < 0)
			ym = game[0].length-1;
		else
			ym = y-1;
		
		int xp; // x+1
		if(x+1 > game.length-1)
			xp = 0;
		else 
			xp = x+1;
		
		int yp; // y+1
		if(y+1 > game[0].length-1)
			yp = 0;
		else
			yp = y+1;
		
		res += game[xm][ym];
		res += game[x ][ym];
		res += game[xm][y ];
		res += game[xp][ym];
		res += game[xm][yp];
		res += game[xp][y ];
		res += game[x ][yp];
		res += game[xp][yp];
		
		return res;
	}
	
	/**
	 * A TextureGeneratorMenu is a small GUI containing the options texture generation.
	 */
	private static class TextureGeneratorMenu extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private JButton generateButton = new JButton("Generate");
		
		private JButton liveColorChooserButton;
		private Color liveColor = DEFAULT_LIVE_COLOR;
		
		private JButton deadColorChooserButton;
		private Color deadColor = DEFAULT_DEAD_COLOR;
		
		private JLabel sizeLabel = new JLabel("Size");
		private JTextField sizeField = new JTextField("300");
		
		private JLabel generationsLabel = new JLabel("Generations");
		private JTextField generationsField = new JTextField("20");
		
		private JLabel cellsLabel = new JLabel("Cells");
		private JTextField cellsField = new JTextField("1000");
		
		private static final Color DEFAULT_LIVE_COLOR = Color.GREEN;
		private static final Color DEFAULT_DEAD_COLOR = Color.BLACK;
		
		public TextureGeneratorMenu() {
			this.add(sizeLabel);
			this.add(sizeField);
			this.add(generationsLabel);
			this.add(generationsField);
			this.add(cellsLabel);
			this.add(cellsField);
			
			ImageIcon defaultLiveColorIcon = iconFromColor(DEFAULT_LIVE_COLOR);
			liveColorChooserButton = new JButton("Live Color", defaultLiveColorIcon);
			liveColorChooserButton.addActionListener(new ColorChooserListener());
			this.add(liveColorChooserButton);
			
			ImageIcon defaultDeadColorIcon = iconFromColor(DEFAULT_DEAD_COLOR);
			deadColorChooserButton = new JButton("Dead Color", defaultDeadColorIcon);
			deadColorChooserButton.addActionListener(new ColorChooserListener());
			this.add(deadColorChooserButton);
			
			generateButton.addActionListener(new GenerateButtonListener());
			this.add(generateButton);
			
			this.setVisible(true);
		}
		
		private static final int DEFAULT_ICON_WIDTH = 32;
		private static final int DEFAULT_ICON_HEIGHT = 32;
		
		/**
		 * Create an Icon of a single color for use on the live/dead color JButtons
		 */
		private static ImageIcon iconFromColor(Color c) {
			BufferedImage image = new BufferedImage(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
			for(int i = 0; i < DEFAULT_ICON_WIDTH; i++) {
				for(int j = 0; j < DEFAULT_ICON_HEIGHT; j++) {
					image.setRGB(i, j, c.getRGB());
				}
			}
			
			return new ImageIcon(image);
		}
		
		/**
		 * A listener for the two ColorChooser buttons, which pops up a
		 * JColorChooser for the user to select the desired color.
		 */
		private class ColorChooserListener implements ActionListener {
			protected JFrame ccFrame;
			@Override
			public void actionPerformed(ActionEvent e) {
				ccFrame = new JFrame();
				ColorChooserPanel ccPanel = new ColorChooserPanel((JButton)e.getSource());
				ccFrame.add(ccPanel);
				ccFrame.pack();
				ccFrame.setVisible(true);
			}
			
			private class ColorChooserPanel extends JPanel {
				private static final long serialVersionUID = 1L;
				
				private JButton selectButton = new JButton("Select");
				private JButton cancelButton = new JButton("Cancel");
				private JColorChooser colorChooser;
				private JButton liveOrDeadButton;
				public ColorChooserPanel(JButton button) {
					liveOrDeadButton = button;
					colorChooser = new JColorChooser();
					this.add(colorChooser);
					
					selectButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if(liveOrDeadButton.equals(liveColorChooserButton)) {
								liveColor = colorChooser.getColor();
								liveOrDeadButton.setIcon(iconFromColor(liveColor));
							} else if(liveOrDeadButton.equals(deadColorChooserButton)) {
								deadColor = colorChooser.getColor();
								liveOrDeadButton.setIcon(iconFromColor(deadColor));
							}
							ccFrame.dispose();
						}		
					});
					this.add(selectButton);
					
					cancelButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							ccFrame.dispose();
						}					
					});
					this.add(cancelButton);
				}
			}
		}
		
		private List<JFrame> textureFrames = new ArrayList<JFrame>();

		/**
		 * A Listener for the GenerateButton, which creates a new window and
		 * displays the generated image.
		 */
		private class GenerateButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame displayFrame = new JFrame("Texture");
				
				int width = Integer.parseInt(sizeField.getText());
				int height = width;
				int generations = Integer.parseInt(generationsField.getText());
				int cells = Integer.parseInt(cellsField.getText());

				BufferedImage image = createGameOfLifeImage(width, height, liveColor, deadColor, cells, generations);
				JPanel displayPanel = new TextureDisplayPanel(image);
				displayFrame.add(displayPanel);
				
				if(textureFrames.isEmpty())
					displayFrame.setLocation(100, 100);
				else
					displayFrame.setLocationRelativeTo(textureFrames.get(textureFrames.size()-1));
				textureFrames.add(displayFrame);
				
				displayFrame.addWindowListener(new DisplayWindowListener());
				
				displayFrame.pack();
				displayFrame.setVisible(true);
			}
		}

		/**
		 * A listener which helps to keep track of which display windows are
		 * open, in order to pop-up new windows relative to the other windows,
		 * rather than directly on top of each other.
		 */
		private class DisplayWindowListener implements WindowListener {
			@Override
			public void windowClosing(WindowEvent e) {
				textureFrames.remove(e.getComponent());
			}
			
			public void windowClosed(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		}
		
	}
	
	/**
	 * Small static class which displays a BufferedImage and allows the user to
	 * save it as a PNG.
	 */
	private static class TextureDisplayPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JPanel panel = this;
		private JButton saveButton;
		private JFileChooser fileChooser;
		public TextureDisplayPanel(BufferedImage image) {
			saveButton = new JButton("Save As...");
			saveButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter("PNG Image", "png");
					fileChooser.setFileFilter(filter);
					fileChooser.setCurrentDirectory(new File("Data" + File.separator + "textures"));
					int returnval = fileChooser.showSaveDialog(panel);
					if(returnval == JFileChooser.APPROVE_OPTION) {
						String filename = fileChooser.getSelectedFile().getAbsolutePath();
						File file;
						if(filename.substring(filename.length()-4).equals(".png"))
							file = fileChooser.getSelectedFile();
						else
							file = new File(filename + ".png");
						saveImage(file);
					}
				}
			});
			this.add(saveButton);
			
			setImage(image);
		}
		
		private JLabel imageLabel;
		private BufferedImage image;
		public void setImage(BufferedImage image) {
			this.image = image;
			imageLabel = new JLabel(new ImageIcon(image));
			this.add(imageLabel);
		}
		
		public void saveImage(File file) {
			try {
				ImageIO.write(image, "png", file);
				System.out.println("Saved file as: " + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
