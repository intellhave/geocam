package frontend;

import geoquant.Geometry;
import geoquant.Radius;
import inputOutput.TriangulationIO;

import java.util.Iterator;
import java.util.Observable;

import marker.BreadCrumbs;
import marker.ForwardGeodesic;
import marker.Marker;
import marker.MarkerHandler;
import marker.MarkerAppearance;
import solvers.Solver;
import solvers.implemented.Yamabe2DFlow;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import controller.KeyboardController;
import controller.UserController;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.Vector;

public class SimulationManager extends Observable implements Runnable {
	private boolean simulationRunning = false;

	/*********************************************************************************
	 * Model Data
	 * 
	 * These variables are responsible for keeping track of the states of the
	 * mathematical objects in the simulation.
	 *********************************************************************************/
	private MarkerHandler markerHandler;
	private Marker source;
	private BreadCrumbs crumbs;
	private Development development;
	private ForwardGeodesic geo;

	/*********************************************************************************
	 * View Data
	 * 
	 * These variables are responsible for keeping track of the viewers that
	 * will present the mathematical objects to the user.
	 *********************************************************************************/
	private boolean isEmbedded;

	/*********************************************************************************
	 * Model Control Data
	 * 
	 * These variables hold the listeners and other objects responsible for
	 * keeping track of user input to the program that effects the model.
	 *********************************************************************************/
	private UserController userControl;

	public SimulationManager(String pathToSurfaceData) {
		initSurface(pathToSurfaceData);
		initModelControls();
		initMarkers();
	}

	/*********************************************************************************
	 * initMarkers
	 * 
	 * This method initializes the markers (ants, rockets, cookies, etc.) that
	 * will appear on the surface for games and exploration.
	 *********************************************************************************/
	private void initMarkers() {
		markerHandler = new MarkerHandler();
		crumbs = new BreadCrumbs(markerHandler);
		geo = new ForwardGeodesic(markerHandler);

		ManifoldPosition pos;
		MarkerAppearance app;

		pos = development.getSource();
		app = new MarkerAppearance(MarkerAppearance.ModelType.LADYBUG);
		markerHandler.addSourceMarker(new Marker(pos, app, Marker.MarkerType.SOURCE));
		source = markerHandler.getSourceMarker();
	}	

	/*********************************************************************************
	 * run
	 * 
	 * This method is where the "rendering" thread spends most of its time.
	 * Within this method's while loop, we receive input from the user, update
	 * the state of the simulation, and then render to all of the active views
	 * managed by this object. We break out of the while loop only when we
	 * receive a message from another thread.
	 *********************************************************************************/
	public void run() {
		simulationRunning = true;
		final long dt = 10; // Timestep size, in microseconds

		long startTime = System.currentTimeMillis();
		long currentTime = startTime;
		long accumulator = 0;
		Thread control = new Thread(userControl);
		control.start();

		userControl.resetPausedFlag();
		userControl.clear();
		while (simulationRunning && !userControl.isPaused()) {
			long newTime = System.currentTimeMillis();
			long frameTime = newTime - currentTime;

			currentTime = newTime;
			accumulator += frameTime;

			while (accumulator >= dt) {
				/*
				 * FIXME: This code ensures the source point marker is displayed
				 * correctly, but it doesn't belong in the DevelopmentUI class.
				 * Refactoring is needed here.
				 */
				// PATCH START
				Face prev = development.getSource().getFace();
				userControl.runNextAction();
				Face next = development.getSource().getFace();
				source.setPosition(development.getSource());
				if (next != prev) {
					markerHandler.updateMarker(source, prev);
				}
				// PATCH END

				markerHandler.updateMarkers(dt);
				accumulator -= dt;
			}
			super.setChanged();
			super.notifyObservers();
		}
		control.interrupt();
	}

	/*********************************************************************************
	 * terminate
	 * 
	 * This method allows other threads (like the AWT Event thread) to signal
	 * the thread running in the "run" loop that it should stop the simulation
	 * at its earliest convenience.
	 *********************************************************************************/
	public void terminate() {		
		simulationRunning = false;
	}

	/*********************************************************************************
	 * initSurface
	 * 
	 * Given a file with path fileName that describes a triangulated surface,
	 * this method loads the description of that surface and initializes the
	 * data structure that specifies the surface for the rest of the program.
	 *********************************************************************************/
	private void initSurface(String fileName) {
		String extension = fileName.substring(fileName.length() - 3, fileName.length());
		if (extension.contentEquals("off")) {
			EmbeddedTriangulation.readEmbeddedSurface(fileName);
			isEmbedded = true;
		} else if (extension.contentEquals("xml")) {
			TriangulationIO.readTriangulation(fileName);
			isEmbedded = false;
		} else {
			System.err.println("invalid file");
		}

		Iterator<Integer> i = null;
		// pick some arbitrary face and source point
		i = Triangulation.faceTable.keySet().iterator();
		Face sourceFace = Triangulation.faceTable.get(i.next());
		Vector sourcePoint = new Vector(0, 0);
		Iterator<Vertex> iv = sourceFace.getLocalVertices().iterator();
		while (iv.hasNext()) {
			sourcePoint.add(Coord2D.coordAt(iv.next(), sourceFace));
		}
		sourcePoint.scale(1.0f / 3.0f);

		if (development == null) {
			development = new Development(new ManifoldPosition(sourceFace, sourcePoint), 1, 1.0);
		} else {
			development.rebuild(new ManifoldPosition(sourceFace, sourcePoint), 1);
		}
	}

	/*********************************************************************************
	 * initModelControls
	 * 
	 * This method is responsible for initializing the objects that will listen
	 * for the user's input and modify the triangulated surface accordingly.
	 * 
	 * Notice: These controls are the ones that modify the model, NOT the ones
	 * that modify how the model is visualized. Those controls belong in the
	 * "initViewControls" method.
	 *********************************************************************************/
	private void initModelControls() {
		// FIXME
		// userControl = new SNESController(development, crumbs, geo);
		userControl = new KeyboardController(development, crumbs, geo);
	}

	/*********************************************************************************
	 * Simulation and Marker Control Methods
	 * 
	 * These methods are used by outside code (like ViewerController instances)
	 * to set certain common parameters of the simulation pertinent to markers,
	 * like the number of markers, their speed and size, etc.
	 *********************************************************************************/
	public void setGeodesicLength(int length) {
		if (length < 0)
			return;
		clearGeodesic();
		geo.setLength(length);
	}

	public void clearGeodesic() {
		for (Marker m : geo.getMarkers()) {
			m.flagForRemoval();
		}
	}

	public void runFlow() {
		double[] radiiLengths = new double[Triangulation.vertexTable.size()];
		Radius[] radii = new Radius[Triangulation.vertexTable.size()];

		int i = 0;

		for (Radius r : Geometry.getRadii()) {
			radiiLengths[i] = r.getValue();
			radii[i] = r;
			i++;
		}

		Solver solver = new Yamabe2DFlow();
		solver.setStoppingCondition(0.001);
		solver.setStepsize(0.002);
		for (int j = 0; j < 100; j++) {
			radiiLengths = solver.run(radiiLengths, j);
			for (i = 0; i < Triangulation.vertexTable.size(); i++) {
				radii[i].setValue(radiiLengths[i]);
			}

			development.rebuild();
			super.notifyObservers();
		}

		System.out.println("runFlow()");
	}

	public boolean isCurrentManifoldEmbedded() {
		return this.isEmbedded;
	}

	public MarkerHandler getMarkerHandler() {
		return markerHandler;
	}
	
	public Development getDevelopment() {
		return development;
	}
}
