package discretegeodesics;

import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Dimension;

import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

import de.jreality.reader.Readers;
import de.jreality.util.CameraUtility;
import de.jreality.util.Input;
import de.jreality.math.Pn;
import de.jreality.math.Rn;

import de.jreality.tools.RotateTool;

import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Geometry;
import de.jreality.scene.data.DataList;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.Appearance;
import de.jreality.shader.CommonAttributes;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;

import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.AxisState;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import discretegeodesics.AbstractFace.ExpInitialConditions;
import discretegeodesics.AbstractFace.PathNodeType;
import discretegeodesics.AbstractFace.MallMapGeometry;

public class DiscreteGeodesics{
	
	//the list of AbstractFaces is a minimalistic 'abstract manifold'
	private static AbstractFace[] facelist_; 
	
	//position on the abstract manifold
	private static AbstractFace source_face_ = null; 
	private static double[] source_local_coords_ = new double[] {0.0, 0.0};
	private static double[] source_direction_ = new double[] {1.0,0.0};
	//3d things
	private static Scene scene_;
	private static SceneGraphComponent sgc_polyhedron_ = new SceneGraphComponent();
	private static SceneGraphComponent sgc_exp_path_ = new SceneGraphComponent();
	//2d things
	private static JRViewer jrv_2d_ = null; //need reference so we can startup/shutdown from checkbox
	private static Scene scene_2d_;
	private static SceneGraphComponent sgc_mall_map_ = new SceneGraphComponent();
	private static SceneGraphComponent sgc_exp_path_2d_ = new SceneGraphComponent();
	//computation options
	private static boolean show_mall_map_ = false;
	private static double exp_path_length_ = 20.0;
	private static final double exp_path_max_length_ = 40.0; //just used to create the scrollbar
	private static int mall_map_recursion_max_depth_ = 4; 
	private static final int mall_map_recursion_max_max_depth_ = 10; //max setting for max depth
	//movement options
	private static final double movement_seconds_per_rotation_ = 4.0;
	private static final double movement_units_per_second_ = 1.0;
	
	//tools for input
	//see http://www3.math.tu-berlin.de/jreality/mediawiki/index.php/Write_a_simple_tool
	//===========================================================================================

	//tool for picking source position
	static class SourcePositionTool extends AbstractTool {

		public SourcePositionTool() {
			super(InputSlot.RIGHT_BUTTON);
			addCurrentSlot(InputSlot.getDevice("PointerTransformation"));
		}
		@Override
		public void activate(ToolContext tc) { perform(tc); }

		@Override
		public void perform(ToolContext tc) {
			
			PickResult pr = tc.getCurrentPick();
			
			if(pr==null){ return; }
			else if(pr.getPickType() == PickResult.PICK_TYPE_FACE){
				//if(source_face_ != pr.getIndex()){ System.out.printf("New source_face_: %d\n", pr.getIndex()); }
				source_face_ = facelist_[pr.getIndex()];
				source_local_coords_ = source_face_.getLocalCoordsFromWorldCoords(pr.getObjectCoordinates());
				computeVisibleGeometry();
			}
		}  
	};

	//movement forward/backward
	static class ManifoldMovementToolFB extends AbstractTool {
		
		private static long time; 
		private static final double units_per_millisecond = movement_units_per_second_/1000;
		
		private static final InputSlot FORWARD_BACKWARD = InputSlot.getDevice("ForwardBackwardAxis");
		private static final InputSlot SYSTEM_TIMER = InputSlot.SYSTEM_TIME;  
		
		public ManifoldMovementToolFB() {
			super(FORWARD_BACKWARD); //'activate' tool on F/B
			addCurrentSlot(SYSTEM_TIMER); //'perform' tool on tick
		}
		
		@Override
		public void activate(ToolContext tc) {
			time = tc.getTime(); //set initial time
		}
		
		@Override
		public void perform(ToolContext tc) {
			
			//get axis state
			AxisState as_fb = tc.getAxisState(FORWARD_BACKWARD);
			
			//get dt and update time
			long newtime = tc.getTime();
			long dt = newtime - time;
			time = newtime;

			//move forward/backward
			if(as_fb.isPressed()){
			
				//set initial conditions
				ExpInitialConditions eic = new ExpInitialConditions();
				eic.face = source_face_;
				eic.position = source_local_coords_;
				eic.direction = Rn.times(null,-as_fb.doubleValue(),source_direction_);
			
				//iterate
				for(double cur_length = dt*units_per_millisecond; cur_length > 0; 
					cur_length -= eic.face.computeExponentialMap(null, null, null, eic, cur_length)
				){}
				
				//get new position
				source_face_ = eic.face; //endpoint.face;
				source_local_coords_ = eic.position; //endpoint.position;
				if(as_fb.doubleValue() > 0){
					Rn.times(source_direction_,-1,eic.direction);
				}else{
					source_direction_ = eic.direction;
				}
			}
			
			//recompute
			computeVisibleGeometry();
		}  
	};

	//rotation
	static class ManifoldMovementToolLR extends AbstractTool {
		
		private static long time; 
		private static final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
	
		private static final InputSlot LEFT_RIGHT = InputSlot.getDevice("LeftRightAxis");
		private static final InputSlot SYSTEM_TIMER = InputSlot.SYSTEM_TIME;  
		
		public ManifoldMovementToolLR() {
			super(LEFT_RIGHT); //'activate' tool on L/R
			addCurrentSlot(SYSTEM_TIMER); //'perform' tool on tick
		}
		
		@Override
		public void activate(ToolContext tc) {
			time = tc.getTime(); //set initial time
		}
		
		@Override
		public void perform(ToolContext tc) {
			
			//get axis state
			AxisState as_lr = tc.getAxisState(LEFT_RIGHT);
			
			//get dt and update time
			long newtime = tc.getTime();
			long dt = newtime - time;
			time = newtime;
			
			//rotate
			if(as_lr.isPressed()){
				double dtheta = radians_per_millisecond*dt*as_lr.doubleValue();
				double cdtheta = Math.cos(dtheta);
				double sdtheta = Math.sin(dtheta);
				double[] newdirection = new double[] { 
					cdtheta*source_direction_[0] - sdtheta*source_direction_[1],  
					sdtheta*source_direction_[0] + cdtheta*source_direction_[1]
				};
				Rn.normalize(source_direction_,newdirection);
			}
			
			//recompute
			computeVisibleGeometry();
		}  
	};

	//the user interface as a plugin
	//see http://www3.math.tu-berlin.de/jreality/api/de/jreality/plugin/basic/ViewShrinkPanelPlugin.html
	//===========================================================================================
	static class UIPanel_ModelSelect extends ViewShrinkPanelPlugin {

		String[][] files = new String[][] {
				{ "Box Torus", "models/boxtorus.off" },
				{ "Box Torus (Smoothed)", "models/boxtoruscc2.off" },
				{ "Cone", "models/cone.off" },
				{ "Dodecahedron", "models/dodecahedron.off" },
				{ "Dodecahedron (Snub)", "models/snub_dodecahedron.off" },
				{ "Epcot", "models/epcot.off" },
				{ "Mushroom", "models/mushroom.off" },
				{ "The letter N", "models/N.off" }
		};
		private HashMap<String, Integer> fileIndices = new HashMap<String, Integer>();

		private void makeUIComponents() {

			//radio buttons for model choice
			ActionListener alFileSelect = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String selectedBox = e.getActionCommand();
					int selectionIndex = ((Integer) fileIndices.get(selectedBox)).intValue();
					loadFile(files[selectionIndex][1]);
				}
			};
			
			ActionListener alFileOpen = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final JFileChooser fc = new JFileChooser();
					int fc_return = fc.showOpenDialog(null);
					if(fc_return == JFileChooser.APPROVE_OPTION){
						loadFile(fc.getSelectedFile().getAbsolutePath());
					}
				}
			};
			
			ButtonGroup group = new ButtonGroup();
			for (int i = 0; i < files.length; i++) {
				JRadioButton button = new JRadioButton(files[i][0]);
				if(i==0){ button.setSelected(true); }
				button.addActionListener(alFileSelect);
				shrinkPanel.add(button);
				group.add(button);
				fileIndices.put(files[i][0], new Integer(i));
			}
			JRadioButton customContentButton = new JRadioButton("Other file...");
			customContentButton.addActionListener(alFileOpen);
			shrinkPanel.add(customContentButton);
			group.add(customContentButton);
			
			//specify layout
			shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6)); //a little padding
			shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),BoxLayout.Y_AXIS));
		}
		
		@Override
		public void install(Controller c) throws Exception {
			makeUIComponents();
			super.install(c);
		}

		@Override
		public PluginInfo getPluginInfo(){
			PluginInfo info = new PluginInfo("Choose Geometry", "");
			return info;
		}
	};
	
	static class UIPanel_PathOptions extends ViewShrinkPanelPlugin {

		TitledBorder border_dir = BorderFactory.createTitledBorder("");
		TitledBorder border_len = BorderFactory.createTitledBorder("");
		
		private void makeUIComponents() {
			
			//some explanation
			JLabel infolabel = new JLabel(
				"<html>" +
				"To move, right click or WASD.<br>" +
				"Nodes are colored as follows:<br>" +
				"<ul>" +
				"  <li>Blue: Starting point" +
				"  <li>Yellow: Edge crossing" +
				"  <li>Red: Vertex intersection" +
				"  <li>Black: Endpoint" +
				"</ul>" +
				"</html>"
			);
			infolabel.setAlignmentX(0.0f);
			shrinkPanel.add(infolabel);
			
			//checkbox for showing nodes
			ActionListener al_shownodes = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean val = ((JCheckBox)e.getSource()).isSelected();
					Appearance app = sgc_exp_path_.getAppearance();
					app.setAttribute(CommonAttributes.VERTEX_DRAW, val);
					sgc_exp_path_.setAppearance(app);
				}
			};
			JCheckBox check_nodes = new JCheckBox("Show Path Nodes");
			check_nodes.setSelected(true);
			check_nodes.setAlignmentX(0.0f);
			check_nodes.addActionListener(al_shownodes);
			shrinkPanel.add(check_nodes);
			
			//slider for node size
			ChangeListener cl_nodesize = new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					double val = ((JSlider)e.getSource()).getValue();
					Appearance app = sgc_exp_path_.getAppearance();
					app.setAttribute(CommonAttributes.POINT_RADIUS, val/1000.0);
					sgc_exp_path_.setAppearance(app);
				}
			};
			JSlider slider_nodesize = new JSlider(0,100,0);
			slider_nodesize.setMaximumSize(new Dimension(400,100));
			slider_nodesize.setAlignmentX(0.0f);
			slider_nodesize.setBorder(BorderFactory.createTitledBorder("Node Size"));
			slider_nodesize.addChangeListener(cl_nodesize);
			shrinkPanel.add(slider_nodesize);
			
			//slider for direction
			ChangeListener cl_direction = new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					//change angle to 2pi * (val/1000)
					double val = ((JSlider)e.getSource()).getValue();
					border_dir.setTitle(String.format("Path Direction (%1.3f*PI)",val/500.0));
					source_direction_[0] = Math.cos(Math.PI*val/500.0);
					source_direction_[1] = Math.sin(Math.PI*val/500.0);
					computeVisibleGeometry();
				}
			};
			JSlider slider_dir = new JSlider(0,1000,0);
			slider_dir.setMaximumSize(new Dimension(400,100));
			slider_dir.setAlignmentX(0.0f);
			border_dir.setTitle(String.format("Path Direction (0.0*PI)"));
			slider_dir.setBorder(border_dir);
			slider_dir.addChangeListener(cl_direction);
			shrinkPanel.add(slider_dir);

			//slider for length
			ChangeListener cl_length = new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					double val = ((JSlider)e.getSource()).getValue();
					exp_path_length_ = val*exp_path_max_length_/100;
					border_len.setTitle(String.format("Path Length (%1.3f)",exp_path_length_));
					computeVisibleGeometry();
				}
			};
			JSlider slider_len = new JSlider(0,100,(int)(100*exp_path_length_/exp_path_max_length_));
			slider_len.setMaximumSize(new Dimension(400,100));
			slider_len.setAlignmentX(0.0f);
			border_len.setTitle(String.format("Path Length (%1.3f)",exp_path_length_));
			slider_len.setBorder(border_len);
			slider_len.addChangeListener(cl_length);
			shrinkPanel.add(slider_len);
			
			//specify layout
			shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6)); //a little padding
			shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),BoxLayout.Y_AXIS));
		}
		
		@Override
		public void install(Controller c) throws Exception {
			makeUIComponents();
			super.install(c);
		}
		
		@Override
		public PluginInfo getPluginInfo(){
			PluginInfo info = new PluginInfo("Geodesic Path Options", "");
			return info;
		}
		
		//these lines would add a help page for the tool
		//@Override public String getHelpDocument() { return "BeanShell.html"; }
		//@Override public String getHelpPath() { return "/de/jreality/plugin/help/"; }
	};
	

	static class UIPanel_MallMapOptions extends ViewShrinkPanelPlugin {

		TitledBorder border_depth = BorderFactory.createTitledBorder("");
		
		private void makeUIComponents() {
			
			//checkbox for showing mall map
			ActionListener al_show2d = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean val = ((JCheckBox)e.getSource()).isSelected();
					setMallMapVisible(val);
				}
			};
			JCheckBox check_show2d = new JCheckBox("Show Mall Map");
			check_show2d.setSelected(show_mall_map_);
			check_show2d.setAlignmentX(0.0f);
			check_show2d.addActionListener(al_show2d);
			shrinkPanel.add(check_show2d);
			
			//slider for recursion depth
			ChangeListener cl_recdepth = new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					int val = ((JSlider)e.getSource()).getValue();
					mall_map_recursion_max_depth_ = val;
					border_depth.setTitle(String.format("Max Recursion Depth (%d)",mall_map_recursion_max_depth_));
					computeVisibleGeometry();
					fix2DCamera();
				}
			};
			JSlider slider_depth = new JSlider(1,mall_map_recursion_max_max_depth_,mall_map_recursion_max_depth_);
			slider_depth.setMaximumSize(new Dimension(400,100));
			slider_depth.setAlignmentX(0.0f);
			border_depth.setTitle(String.format("Max Recursion Depth (%d)",mall_map_recursion_max_depth_));
			slider_depth.setBorder(border_depth);
			slider_depth.addChangeListener(cl_recdepth);
			shrinkPanel.add(slider_depth);

			//specify layout
			shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6)); //a little padding
			shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),BoxLayout.Y_AXIS));
		}
		
		@Override
		public void install(Controller c) throws Exception {
			makeUIComponents();
			super.install(c);
		}
		
		@Override
		public PluginInfo getPluginInfo(){
			PluginInfo info = new PluginInfo("Mall Map Options", "");
			return info;
		}
	};
	
	//main
	//===========================================================================================
	public static void main(String[] args){
		
		//set polyhedron appearance
		Appearance app_polyhedron = new Appearance();
		app_polyhedron.setAttribute(CommonAttributes.EDGE_DRAW, true);
		app_polyhedron.setAttribute(CommonAttributes.VERTEX_DRAW, false);
		app_polyhedron.setAttribute(CommonAttributes.TUBES_DRAW, false);
		app_polyhedron.setAttribute(CommonAttributes.TUBE_RADIUS, 0.01); //picking works as though the tubes are actually there, even if not drawn (as noted in documentation)
		app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
		app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY, 0.7d);
		app_polyhedron.setAttribute(CommonAttributes.PICKABLE, true);
		sgc_polyhedron_.setAppearance(app_polyhedron);
		
		//set mall map appearance
		Appearance app_mall_map = new Appearance();
		app_mall_map.setAttribute(CommonAttributes.EDGE_DRAW, true);
		app_mall_map.setAttribute(CommonAttributes.VERTEX_DRAW, false);
		app_mall_map.setAttribute(CommonAttributes.TUBES_DRAW, false);
		app_mall_map.setAttribute(CommonAttributes.TUBE_RADIUS, 0.01); //picking works as though the tubes are actually there, even if not drawn (as noted in documentation)
		app_mall_map.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
		app_mall_map.setAttribute(CommonAttributes.TRANSPARENCY, 0.7d);
		app_mall_map.setAttribute(CommonAttributes.PICKABLE, true);
		sgc_mall_map_.setAppearance(app_mall_map);
		
		//set exponential path appearance
		Appearance app_exp_path = new Appearance();
		app_exp_path.setAttribute(CommonAttributes.EDGE_DRAW, true);
		app_exp_path.setAttribute(CommonAttributes.VERTEX_DRAW, true);
		app_exp_path.setAttribute(CommonAttributes.TUBES_DRAW, false);
		app_exp_path.setAttribute(CommonAttributes.POINT_RADIUS, 0.01);
		app_exp_path.setAttribute(CommonAttributes.PICKABLE, false);
		sgc_exp_path_.setAppearance(app_exp_path);
		sgc_exp_path_2d_.setAppearance(app_exp_path);
		
		//make the scene graphs
		SceneGraphComponent sgc_root = new SceneGraphComponent();
		sgc_root.addChild(sgc_polyhedron_);
		sgc_root.addChild(sgc_exp_path_);
		
		//add tools
		sgc_polyhedron_.addTool(new SourcePositionTool()); //used to select starting position of the path
		sgc_root.addTool(new ManifoldMovementToolLR());
		sgc_root.addTool(new ManifoldMovementToolFB());
		sgc_root.addTool(new RotateTool(){ @Override public void deactivate(ToolContext tc){} }); //enable rotate, but disable the annoying thing where it keeps rotating 
		
		//set up the main JRViewer
		JRViewer jrv = new JRViewer();
		scene_ = jrv.getPlugin(Scene.class); //reference used to 'encompass' on loadfile
		jrv.addBasicUI();
		jrv.registerPlugin(new UIPanel_ModelSelect());
		jrv.registerPlugin(new UIPanel_MallMapOptions());
		jrv.registerPlugin(new UIPanel_PathOptions());
		jrv.setShowPanelSlots(true,false,false,false);
		jrv.setContent(sgc_root);
		
		//load default model
		loadFile("models/boxtorus.off");
		
		//start
		jrv.startup();
		setMallMapVisible(show_mall_map_);
	}
	
	public static void setMallMapVisible(boolean val){
		
		if((val == true) && (jrv_2d_ == null)){
			
			SceneGraphComponent sgc_root_2d = new SceneGraphComponent();
			sgc_root_2d.addChild(sgc_mall_map_);
			sgc_root_2d.addChild(sgc_exp_path_2d_);
		
			jrv_2d_ = new JRViewer();
			scene_2d_ = jrv_2d_.getPlugin(Scene.class);
			jrv_2d_.setContent(sgc_root_2d);
			jrv_2d_.startup();
			
			computeMallMap(mall_map_recursion_max_depth_);
			fix2DCamera();
		}
		
		if((val == false) && (jrv_2d_ != null)){ 
			jrv_2d_.dispose(); 
			jrv_2d_ = null;
		}
		
		show_mall_map_ = val;
	}
	
	//loads a file, sets up AbstractFaces structure, finds a default source_position_, and updates SGCs
	//===========================================================================================
	public static void loadFile(String filename){

		//read the data file
		Geometry geom = sgc_polyhedron_.getGeometry();
		try{
			Input input = Input.getInput(filename);
			geom = Readers.read(input).getGeometry();
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		sgc_polyhedron_.setGeometry(geom);
		
		//get abstract manifold data
		computeAbstractFacesFromGeometry(geom); 
		
		//set a reasonable source position (just some arbitrary initial choice)
		source_face_ = facelist_[0];
		double[] v0 = source_face_.getVertCoords(0);
		double[] v1 = source_face_.getVertCoords(1);
		double[] v2 = source_face_.getVertCoords(2);
		source_local_coords_ = new double[] {(v0[0]+v1[0]+v2[0])/3, (v0[1]+v1[1]+v2[1])/3};
		
		//compute whatever is visible
		computeVisibleGeometry();
		
		//fix cameras
		fixCamera(scene_);
		fix2DCamera();
	}
	
	public static void computeVisibleGeometry(){
		//generate the exponential path geometry
		computeExponentialPath(exp_path_length_);
		//generate mall-map geometry
		if(show_mall_map_){  computeMallMap(mall_map_recursion_max_depth_); }
	}
	
	public static void fixCamera(Scene scene){
		//set the camera so the new geometry fits properly
		CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(), scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);				
	}
	
	public static void fix2DCamera(){
		
		if(show_mall_map_){
			sgc_exp_path_2d_.setVisible(false);
			fixCamera(scene_2d_);
			sgc_exp_path_2d_.setVisible(true);
		}
	}
	
	//updates the exp_path SceneGraphComponent using the output from AbstractFace.computeExponentialMap
	//===========================================================================================
	public static void computeExponentialPath(double length){

		//produce polyline in 3d from exponential path (see AbstractFace code for computeExponentialMap)
		ArrayList<double[]> path = new ArrayList<double[]>();
		ArrayList<AbstractFace.PathNodeType> nodetypes = new ArrayList<AbstractFace.PathNodeType>();
		
		//set initial conditions
		ExpInitialConditions eic = new ExpInitialConditions();
		eic.face = source_face_;
		eic.position = source_local_coords_;
		eic.direction = source_direction_;
		//add initial point
		path.add(source_face_.getWorldCoordsFromLocalCoords(source_local_coords_));
		nodetypes.add(PathNodeType.PATH_START);
		//iterate
		for(double cur_length = length; cur_length > 0; 
			cur_length -= eic.face.computeExponentialMap(path, null, nodetypes, eic, cur_length)
		){}

		//put the polyline geometry into sgc_exp_path_
		IndexedLineSetFactory ilsf = new IndexedLineSetFactory(); 
		ilsf.setVertexCount(path.size());
		ilsf.setEdgeCount(path.size()-1);
		
		double[][] ilsf_verts = new double[path.size()][3];
		Color[] ilsf_vcolors = new Color[path.size()];
		int[][] ilsf_edges = new int[path.size()-1][2];
		
		for(int i=0; i<path.size(); i++){
			ilsf_verts[i] = path.get(i);
			switch(nodetypes.get(i)){
				case PATH_START:  ilsf_vcolors[i] = Color.BLUE; break;
				case EDGE_INTERSECTION:  ilsf_vcolors[i] = Color.YELLOW; break;
				case VERTEX_INTERSECTION:  ilsf_vcolors[i] = Color.RED; break;
				case PATH_END:  ilsf_vcolors[i] = Color.BLACK; break;
				case PATH_DEBUG:  ilsf_vcolors[i] = Color.GREEN; break;
			}
		}
		for(int j=0; j<path.size()-1; j++){ ilsf_edges[j] = new int[] {j,j+1}; }
		
		ilsf.setVertexCoordinates(ilsf_verts);
		ilsf.setVertexColors(ilsf_vcolors);
		ilsf.setEdgeIndices(ilsf_edges);
		ilsf.update();
		sgc_exp_path_.setGeometry(ilsf.getGeometry());
	}
	
	//updates the mall_map SceneGraphComponent using the output from AbstractFace.computeMallMap
	//===========================================================================================
	public static void computeMallMap(int recursion_max_depth){
		
		//geometry stored as list of faces, where a face is a list of vertices
		MallMapGeometry geometry = new MallMapGeometry();
		source_face_.computeMallMap(recursion_max_depth,source_local_coords_,source_direction_,geometry);
		
		//put geometry into sgc_mall_map_
		IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
		
		double[][] ifsf_verts = geometry.getVerts();
		int[][] ifsf_faces = geometry.getFaces();

		ifsf.setVertexCount(ifsf_verts.length);
		ifsf.setVertexCoordinates(ifsf_verts);
		ifsf.setFaceCount(ifsf_faces.length);
		ifsf.setFaceIndices(ifsf_faces);
		ifsf.setGenerateEdgesFromFaces(true);
		ifsf.update();
		sgc_mall_map_.setGeometry(ifsf.getGeometry());
		
		//make a simple line indicating where the exp path is
		IndexedLineSetFactory ilsf = new IndexedLineSetFactory(); 
		ilsf.setVertexCount(2);
		ilsf.setEdgeCount(1);

		ilsf.setVertexCoordinates(new double[][] { {0,0,1}, {exp_path_length_,0,1} });
		ilsf.setVertexColors(new Color[] { Color.blue, Color.black });
		ilsf.setEdgeIndices(new int[][] { {0,1} });
		ilsf.update();
		sgc_exp_path_2d_.setGeometry(ilsf.getGeometry());
	}
	
	//computes all the data for each AbstractFace:  see the AbstractFace code for details
	//===========================================================================================
	public static void computeAbstractFacesFromGeometry(Geometry geom){
		
		boolean verbose = false;
		if(verbose){ System.out.println("\nComputing abstract geometry..."); }
		
		//get the geometry data
		DataList dl_faceindices = geom.getAttributes(Geometry.CATEGORY_FACE,Attribute.INDICES);
		DataList dl_facenormals = geom.getAttributes(Geometry.CATEGORY_FACE,Attribute.NORMALS);
		DataList dl_edgeindices = geom.getAttributes(Geometry.CATEGORY_EDGE,Attribute.INDICES);
		DataList dl_vertcoords = geom.getAttributes(Geometry.CATEGORY_VERTEX,Attribute.COORDINATES);
		
		int nfaces = dl_faceindices.size();
		int nedges = dl_edgeindices.size();
		int nverts = dl_vertcoords.size();
		if(verbose){ System.out.printf("%d verts, %d edges, %d faces\n", nverts,nedges,nfaces); }
		facelist_ = new AbstractFace[nfaces]; 
		
		//gather some data from each face
		if(verbose){ System.out.println("\nFace data:\n--------------------"); }
		for(int i=0; i<nfaces; i++){
			
			int[] vi = dl_faceindices.item(i).toIntArray(null); //list of vertex indices for this face
			
			//get ortho basis (fnormal, ftanx, ftany) at a point
			double[] fnormal = dl_facenormals.item(i).toDoubleArray(null); //normal to face
			double[] v0 = dl_vertcoords.item(vi[0]).toDoubleArray(null); //3d coords for first vertex
			double[] v1 = dl_vertcoords.item(vi[1]).toDoubleArray(null); //3d coords for next vertex
			double[] ftanx = Rn.normalize(null,Rn.subtract(null,v1,v0)); //tangent to face
			double[] ftany = Rn.crossProduct(null,fnormal,ftanx); //completes ON basis
			
			//print some details about the face
			if(verbose){
				System.out.printf("\nFace %d: \n",i);
				System.out.printf("   N: (%f,%f,%f)\n", fnormal[0], fnormal[1], fnormal[2]);
				System.out.printf("   TX: (%f,%f,%f)\n", ftanx[0], ftanx[1], ftanx[2]);
				System.out.printf("   TY: (%f,%f,%f)\n   VI: ", ftany[0], ftany[1], ftany[2]);
				for(int j : vi){ System.out.printf("%d ", j); } System.out.printf("\n");
			}
			
			//set up the vertices for the corresponding abstract face (in particular find 2d coords)
			double[][] vc2d = new double[vi.length][2];
			if(verbose){ System.out.println("   Local coords:"); }
			for(int j=0; j<vi.length; j++){
				double[] vc3d = dl_vertcoords.item(vi[j]).toDoubleArray(null); //3d coords for current vertex, will translate so v0 is at origin
				vc2d[j][0] = Rn.innerProduct(Rn.subtract(null,vc3d,v0), ftanx); //component in ftanx direction
				vc2d[j][1] = Rn.innerProduct(Rn.subtract(null,vc3d,v0), ftany); //component in ftany direction
				if(verbose){ System.out.printf("     (%f,%f)\n",vc2d[j][0],vc2d[j][1]); }
			}
			
			//store vertex info for abstract face
			facelist_[i] = new AbstractFace();
			facelist_[i].setCoords(v0,ftanx,ftany);
			facelist_[i].setVerts(vi,vc2d);
		}
		
		//now determine how the faces are connected, and generate coordinate transformations over each edge
		if(verbose){ System.out.println("\nEdge data:\n--------------------"); }
		for(int i=0; i<nedges; i++){
			
			int[] ei = dl_edgeindices.item(i).toIntArray(null);
			if(verbose){ System.out.printf("\nEdge %d:  vi %d,%d; f ",i,ei[0],ei[1]); }
			int[] incident_faces = new int[2]; //it is possible that there is only one incident face, like for a 'cone' shaped face
			int[] local_edge_index = new int[2]; //current edge has index local_edge_index[j] in the list of edges for this incident_faces[j]
			//the variable ei[j] is the vertex e[i] in the coords on incident_faces[j]
			double[][] e0 = new double[2][2]; 
			double[][] e1 = new double[2][2]; 
			//edge_tangent[j] is the unit vector running along e[0] to e[1] in the coords on incident_faces[j]
			double[][] edge_tangent = new double[2][2];
			
			//find incident faces (this is inefficient, but it's still fast and only runs once anyway)
			int c=0;
			for(int j=0; (j<nfaces) && (c<2); j++){
				int[] vi = dl_faceindices.item(j).toIntArray(null);
				for(int k=0; k<vi.length; k++){
					int vi0 = vi[k], vi1 = vi[(k+1)%vi.length];
					if( ((vi0 == ei[0]) && (vi1 == ei[1])) || ((vi0 == ei[1]) && (vi1 == ei[0])) ){
						incident_faces[c] = j;
						local_edge_index[c] = k;
						if(vi0 == ei[0]){
							e0[c] = facelist_[j].getVertCoords(k);
							e1[c] = facelist_[j].getVertCoords((k+1)%vi.length);
						}else{
							e1[c] = facelist_[j].getVertCoords(k);
							e0[c] = facelist_[j].getVertCoords((k+1)%vi.length);
						}
						c++; break;
					}
				}
			}
			//if only one face has this edge, it should connect back onto itself; could also reflect incorrectly encoded geometry 
			if(c==1){ System.err.println("Warning: single-face edges not implemented"); }
			if(verbose){ System.out.printf("%d,%d\n", incident_faces[0],incident_faces[1]); }
			
			//determine coordinate transformation data (from inc_face[0] to inc_face[1])
			edge_tangent[0] = Rn.normalize(null,Rn.subtract(null, e1[0], e0[0])); 
			edge_tangent[1] = Rn.normalize(null,Rn.subtract(null, e1[1], e0[1])); 
			double trans_cos = Rn.innerProduct(edge_tangent[0], edge_tangent[1]);
			double trans_sin = edge_tangent[0][0]*edge_tangent[1][1]-edge_tangent[0][1]*edge_tangent[1][0]; //note: there is no abs here because this correctly computes the sine of the full angle going from et[0] to et[1], which may be MORE than pi 
			
			//now the transformation is:  subtract e0[0], hit with matrix [c,s][-s,c], then add e0[1]
			double[][] trans_matrix = new double[3][3];
			trans_matrix[0] = new double[] {trans_cos, -trans_sin, e0[1][0] - trans_cos*e0[0][0] + trans_sin*e0[0][1]};
			trans_matrix[1] = new double[] {trans_sin, trans_cos, e0[1][1] - trans_sin*e0[0][0] - trans_cos*e0[0][1]};
			trans_matrix[2] = new double[] {0,0,1};
			
			//need the inverse also, since that is the transformation from inc_face[1] to inc_face[0]; easy to compute since orthogonal transformation
			double[][] trans_matrix_inv = new double[3][3];
			trans_matrix_inv[0] = new double[] {trans_cos, trans_sin, e0[0][0] - trans_cos*e0[1][0] - trans_sin*e0[1][1]};
			trans_matrix_inv[1] = new double[] {-trans_sin, trans_cos, e0[0][1] + trans_sin*e0[1][0] - trans_cos*e0[1][1]};
			trans_matrix_inv[2] = new double[] {0,0,1};
			
			//store the data
			facelist_[incident_faces[0]].setEdge(local_edge_index[0], i, trans_matrix, facelist_[incident_faces[1]]);
			facelist_[incident_faces[1]].setEdge(local_edge_index[1], i, trans_matrix_inv, facelist_[incident_faces[0]]);
			
			//now display the information
			if(verbose){
				System.out.println("Transformation data:");
				System.out.printf("  Vertex e[0] in face 0 coords: (%f, %f)\n", e0[0][0], e0[0][1]);
				System.out.printf("  Vertex e[1] in face 0 coords: (%f, %f)\n", e1[0][0], e1[0][1]);
				System.out.printf("  Vertex e[0] in face 1 coords: (%f, %f)\n", e0[1][0], e0[1][1]);
				System.out.printf("  Vertex e[1] in face 1 coords: (%f, %f)\n", e1[1][0], e1[1][1]);
				System.out.printf("  Transformation matrix:\n");
				System.out.printf("    [%f, %f, %f]\n", trans_matrix[0][0], trans_matrix[0][1], trans_matrix[0][2]);
				System.out.printf("    [%f, %f, %f]\n", trans_matrix[1][0], trans_matrix[1][1], trans_matrix[1][2]);
				System.out.printf("  Transformation matrix inverse:\n");
				System.out.printf("    [%f, %f, %f]\n", trans_matrix_inv[0][0], trans_matrix_inv[0][1], trans_matrix_inv[0][2]);
				System.out.printf("    [%f, %f, %f]\n", trans_matrix_inv[1][0], trans_matrix_inv[1][1], trans_matrix_inv[1][2]);
			}
		}
		
		//compute sum of interior angles at each vertex
		double[] anglesum = new double[nverts];
		if(verbose){ System.out.printf("\nCurvature data:\n--------------------"); }
		for(int i=0; i<nverts; i++){
			
			//find any face with this vertex, and find edges incident to the vertex
			int faceind = -1;
			for(int j=0; (j<nfaces) && (faceind<0); j++){
				int[] vi = dl_faceindices.item(j).toIntArray(null);
				for(int k=0; k<vi.length; k++){
					if(vi[k] == i){ faceind = j; break; }
				}
			}
			if(faceind < 0){ System.err.println("Warning: found an unused vertex"); continue; }
			
			//compute, recursively, the sum of interior angles sharing this vertex
			anglesum[i] = facelist_[faceind].getAngleSum(i);
			if(verbose){ System.out.printf("\nVertex %d: Angle sum %f (%f pi)",i,anglesum[i],anglesum[i]/Math.PI); }
		}
		if(verbose){ System.out.printf("\n\n"); }
		
		//store 'straight angles' for straighest geodesic determination
		for(int i=0; i<nfaces; i++){
			
			int[] vi = dl_faceindices.item(i).toIntArray(null);
			double[] straightangles = new double[vi.length];
			for(int j=0; j<vi.length; j++){
				straightangles[j] = anglesum[vi[j]]/2;
			}
			facelist_[i].setVertStraightAngles(straightangles);
		}
		
		if(verbose){ System.out.printf("Abstract geometry computed.\n"); }
	}
}