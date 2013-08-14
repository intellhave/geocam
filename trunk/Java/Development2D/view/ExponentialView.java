package view;

import geoquant.Length;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import util.Vector;
import view.TextureLibrary.TextureDescriptor;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import development.AffineTransformation;
import development.Development;
import development.DevelopmentNode;
import development.Frustum2D;
import development.ManifoldPosition;

/*********************************************************************************
 * Exponential View (Previously DevelopmentView2D)
 * 
 * This view visualizes a triangulated 2 dimensional surface using the notion of
 * an exponential map. Starting from the face that contains a designated
 * "source point," this view unfolds pieces of the surface into a plane. The
 * result is a representation of a neighborhood of the source point built out of
 * polygons in the plane.
 *********************************************************************************/
public class ExponentialView extends View {
	protected HashMap<Marker, LinkedList<SceneGraphComponent>> sgcpools;
	protected SceneGraphComponent sgcLight;

	protected HashMap<Face, DevelopmentGeometry> faceDevelopments;
	protected HashMap<Face, SceneGraphComponent> faceSGCs;

	protected double edgeLength;

	/*********************************************************************************
	 * ExponentialView
	 * 
	 * This method initializes a new ExponentialView to use a particular
	 * development (for calculating the visualization) and color scheme (for
	 * coloring the polygons that make up the visualization).
	 *********************************************************************************/
	public ExponentialView(Development d, MarkerHandler mh) {
		super(d, mh);
		this.sgcpools = new HashMap<Marker, LinkedList<SceneGraphComponent>>();
		zoom = 1.0;
		Edge e = markers.getSourceMarker().getPosition().getFace().getLocalEdges().get(0);
		edgeLength = Length.valueAt(e);

		// create light
		sgcLight = new SceneGraphComponent();
		DirectionalLight light = new DirectionalLight();
		light.setIntensity(1.5);
		sgcLight.setLight(light);

		// MatrixBuilder.euclidean().translate(0,0,5).assignTo(sgcLight);
		sgcCamera.addChild(sgcLight);
		Camera cam = sgcCamera.getCamera();

		cam.setPerspective(false);

		faceDevelopments = new HashMap<Face, DevelopmentGeometry>();
		faceSGCs = new HashMap<Face, SceneGraphComponent>();

		for (Face f : Triangulation.faceTable.values()) {
			SceneGraphComponent sgc = new SceneGraphComponent();
			faceSGCs.put(f, sgc);
			this.sgcDevelopment.addChild(sgc);
			// FIXME: Can sgc carry the appearance for all the pieces of the
			// development beneath it???
		}
	}

	/*********************************************************************************
	 * updateCamera
	 * 
	 * This method is responsible for positioning the camera such that the
	 * entire development can be viewed.
	 * 
	 * TODO We still need to determine how far to translate along the z axis.
	 * The rotation ensures that the source point's "forward direction" points
	 * north.
	 *********************************************************************************/
	protected void updateCamera() {
		Camera cam = sgcCamera.getCamera();
		// setting the "On Axis" feature to false allows us to zoom in and out
		// on the surface
		cam.setOnAxis(false);
		double size = 4 * edgeLength * zoom;

		MatrixBuilder.euclidean().translate(0, 0, 3).rotateZ(-Math.PI / 2).assignTo(sgcCamera);

		/*
		 * This Rectangle object describes the field of view of the camera. By
		 * changing the value of size, we can create the illusion of zooming in
		 * and out.
		 */

		Rectangle2D.Double view = new Rectangle2D.Double(-size / 2.0, -size / 2.0, size, size);
		cam.setViewPort(view);

	}

	/*********************************************************************************
	 * generateManifoldGeometry
	 * 
	 * This method constructs the polygons that will make up the development,
	 * and places them in the plane. The polygons are constructed via a
	 * recursive procedure outlined in one of the 2010 REU papers.
	 *********************************************************************************/
	protected void generateManifoldGeometry() {
		for (Face f : Triangulation.faceTable.values()) {
			// TODO: Would it be better to be able to tell a development
			// geometry to
			// clear all its existing data???
			faceDevelopments.put(f, new DevelopmentGeometry());
		}

		generateManifoldGeometry(development.getRoot());

		for (Face f : faceDevelopments.keySet()) {
			SceneGraphComponent sgc = faceSGCs.get(f);

			DevelopmentGeometry dgf = faceDevelopments.get(f);
			double[][] ifsf_verts = dgf.getVerts();
			int[][] ifsf_faces = dgf.getFaces();

			if (ifsf_verts.length == 0) {
				sgc.setVisible(false);
				continue;
			} else {
				sgc.setVisible(true);
			}

			IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
			ifsf.setVertexCount(ifsf_verts.length);
			ifsf.setVertexCoordinates(ifsf_verts);

			double[][] tex_verts = dgf.getTexCoords();

			ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, tex_verts);
			ifsf.setFaceCount(ifsf_faces.length);
			ifsf.setFaceIndices(ifsf_faces);
			// ifsf.setGenerateEdgesFromFaces(true);
			ifsf.setGenerateFaceNormals(true);
			ifsf.setGenerateVertexNormals(true);		
			
			if (showFaceLabels) {
				String[] faceLabels = new String[ifsf_faces.length];
				for (int i = 0; i < ifsf_faces.length; i++) {
					faceLabels[i] = "" + f.getIndex();
				}
				ifsf.setFaceLabels(faceLabels);
			}
			
			ifsf.update();
			
			Appearance app;
			if (showTexture) {
				if (f.hasAppearance()) {
					app = f.getAppearance();
				} else {
					TextureDescriptor td = faceAppearanceScheme.getTextureDescriptor(f);
					app = TextureLibrary.getAppearance(td);
				}
			} else {
				app = TextureLibrary.getAppearance(faceAppearanceScheme.getColor(f));
			}
			// app.setAttribute(VERTEX_DRAW, true);
			sgc.setGeometry(ifsf.getIndexedFaceSet());
			sgc.setAppearance(app);
		}
	}

	// This is a recursive helper method for generateManifoldGeometry().
	private void generateManifoldGeometry(DevelopmentNode node) {
		Face f = node.getFace();
		DevelopmentGeometry dg = faceDevelopments.get(f);
		double[][] face = node.getClippedFace().getVectorsAsArray();
		double[][] texCoords = node.getClippedFace().getTexCoordsAsArray();
		dg.addFace(face, texCoords, 1.0);
		for (DevelopmentNode n : node.getChildren())
			generateManifoldGeometry(n);
	}

	protected LinkedList<SceneGraphComponent> getBigEnoughPool(Marker m, int imagesNeeded) {
		LinkedList<SceneGraphComponent> pool = sgcpools.get(m);

		if (pool == null) {
			pool = new LinkedList<SceneGraphComponent>();
			sgcpools.put(m, pool);
		}

		if (imagesNeeded > pool.size()) {
			int sgcCount = imagesNeeded - pool.size();
			for (int jj = 0; jj < 2 * sgcCount; jj++) {
				MarkerAppearance oa = m.getAppearance();
				SceneGraphComponent sgc = oa.makeSceneGraphComponent();
				pool.add(sgc);
				sgcMarkers.addChild(sgc);
			}
		}

		return pool;
	}

	/*********************************************************************************
	 * generateMarkerGeometry
	 * 
	 * This method is responsible for placing representations of the markers in
	 * the visualization. Due to the nature of this particular view, a single
	 * marker may appear in multiple places. This is why we need the sgcpools
	 * data structure --- it keeps track of the multiple scene graph components
	 * needed to depict each marker in the scene.
	 *********************************************************************************/
	protected void generateMarkerGeometry() {
		HashMap<Marker, ArrayList<Vector[]>> markerImages;
		markerImages = new HashMap<Marker, ArrayList<Vector[]>>();
		developMarkers(development.getRoot(), markerImages);

		Set<Marker> allMarkers = markers.getAllMarkers();

		LinkedList<SceneGraphComponent> pool;
		for (Marker m : allMarkers) {
			ArrayList<Vector[]> images = markerImages.get(m);

			int imagesNeeded = 0;
			if (images != null) {
				imagesNeeded = images.size();
			}

			pool = getBigEnoughPool(m, imagesNeeded);

			if (images == null) {
				for (SceneGraphComponent sgc : pool)
					sgc.setVisible(false);
				continue;
			}

			int counter = 0;
			for (SceneGraphComponent sgc : pool) {
				if (counter >= images.size()) {
					sgc.setVisible(false);
				} else {
					Vector[] triple = images.get(counter);
					Vector position = triple[0];
					Vector forward = triple[1];
					forward.normalize();
					// Vector left = triple[2];
					// left.normalize();

					double[] matrix = new double[16];
					matrix[0 * 4 + 0] = forward.getComponent(0);
					matrix[0 * 4 + 1] = -forward.getComponent(1);
					matrix[0 * 4 + 2] = 0.0;
					matrix[0 * 4 + 3] = 0.0;

					matrix[1 * 4 + 0] = forward.getComponent(1);
					matrix[1 * 4 + 1] = forward.getComponent(0);
					matrix[1 * 4 + 2] = 0.0;
					matrix[1 * 4 + 3] = 0.0;

					matrix[2 * 4 + 0] = 0.0;
					matrix[2 * 4 + 1] = 0.0;
					matrix[2 * 4 + 2] = 1.0;
					matrix[2 * 4 + 3] = 0.0;

					matrix[3 * 4 + 0] = 0.0;
					matrix[3 * 4 + 1] = 0.0;
					matrix[3 * 4 + 2] = 0.0;
					matrix[3 * 4 + 3] = 1.0;

					MatrixBuilder.euclidean()
							.translate(position.getComponent(0), position.getComponent(1), 1.0)
							.times(matrix).scale(m.getAppearance().getScale()).assignTo(sgc);

					sgc.setVisible(true);
				}
				counter++;
			}
		}
	}

	/*********************************************************************************
	 * initializeNewManifold
	 * 
	 * This method is responsible for initializing (or reinitializing) the scene
	 * graph in the event that we wish to display a different manifold.
	 *********************************************************************************/
	public void initializeNewManifold() {
		for (LinkedList<SceneGraphComponent> pool : sgcpools.values()) {
			while (!pool.isEmpty()) {
				SceneGraphComponent sgc = pool.remove();
				sgcMarkers.removeChild(sgc);
			}
		}
		sgcpools.clear();
		updateCamera();
		updateGeometry(true, true);
	}

	/*********************************************************************************
	 * developMarkers
	 * 
	 * Given a development, this method determines where particular markers on
	 * the manifold should appear in that development, and with what
	 * orientation. Once calculated, this data is stored in the input ArrayList
	 * "markerImages", which is used to accumulate results across all of the
	 * recursive calls to developMarkers.
	 *********************************************************************************/
	protected void developMarkers(DevelopmentNode devNode,
		HashMap<Marker, ArrayList<Vector[]>> markerImages) {
		
		Collection<Marker> localMarkers = markers.getMarkers(devNode.getFace());
		
		if (localMarkers != null) {

			Frustum2D frustum = devNode.getFrustum();
			AffineTransformation affineTrans = devNode.getAffineTransformation();

			synchronized (localMarkers) {
				for (Marker m : localMarkers) {
					if (!m.isVisible())
						continue;

					ManifoldPosition pos = m.getPosition();
					Vector transPos = affineTrans.affineTransPoint(pos.getPosition());
					// check if object image should be clipped by frustum
					if (frustum != null && !frustum.checkInterior(transPos))
						continue;

					// add to image list
					ArrayList<Vector[]> imageList = markerImages.get(m);
					if (imageList == null) {
						imageList = new ArrayList<Vector[]>();
						markerImages.put(m, imageList);
					}

					Vector[] triple = new Vector[3];
					triple[0] = transPos;
					triple[1] = affineTrans.affineTransVector(pos.getDirectionForward());
					triple[2] = affineTrans.affineTransVector(pos.getDirectionLeft());

					imageList.add(triple);
				}
			}
		}

		ArrayList<DevelopmentNode> children = devNode.getChildren();
		for (DevelopmentNode child : children)
			developMarkers(child, markerImages);
	}

	@Override
	public void removeMarker(Marker m) {
		LinkedList<SceneGraphComponent> objectImages = sgcpools.get(m);
		for (SceneGraphComponent sgc : objectImages) {
			sgcMarkers.removeChild(sgc);
		}
	}

	public void setZoom(double newZoom) {
		zoom = newZoom;
	}
}
