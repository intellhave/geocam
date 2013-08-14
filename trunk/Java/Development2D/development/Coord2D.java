package development;

import geoquant.Angle;
import geoquant.Geoquant;
import geoquant.Length;
import geoquant.TriPosition;

import java.util.HashMap;

import triangulation.Face;
import triangulation.StdFace;
import triangulation.Vertex;
import util.Vector;

//note that the coordinates this geoquant gives do not take orientation into account

public class Coord2D extends Geoquant {
    // Index map
    private static HashMap<TriPosition, Coord2D> Index = new HashMap<TriPosition, Coord2D>();

    private Length lij;
    private Length ljk;
    private Length lik;
    private Angle ai;

    private Face face;
    private Vertex vert;
    private int stdFIndex; // coordinate computed differently depending on index
    private Vector coord; // 2d point holding coords for this vertex/face pair

    public Coord2D(Vertex v, Face f) {
	super(v, f);
	face = f;
	vert = v;
	StdFace stdF = new StdFace(f);
	lij = Length.at(stdF.e12);
	ljk = Length.at(stdF.e23);
	lik = Length.at(stdF.e13);
	ai = Angle.at(stdF.v1, f);

	// figure out index of v in stdFace vertex list
	if (v == stdF.v1) {
	    stdFIndex = 0;
	} else if (v == stdF.v2) {
	    stdFIndex = 1;
	} else if (v == stdF.v3) {
	    stdFIndex = 2;
	}

	lij.addObserver(this);
	ljk.addObserver(this);
	lik.addObserver(this);
	ai.addObserver(this);
    }

    protected void recalculate() {

	StdFace stdF = new StdFace(face);
	lij = Length.at(stdF.e12);
	ljk = Length.at(stdF.e23);
	lik = Length.at(stdF.e13);
	ai = Angle.at(stdF.v1, face);

	if (vert == stdF.v1) {
	    stdFIndex = 0;
	} else if (vert == stdF.v2) {
	    stdFIndex = 1;
	} else if (vert == stdF.v3) {
	    stdFIndex = 2;
	}

	if (stdFIndex == 0) {

	    coord = new Vector(new double[] { 0, 0 });

	} else if (stdFIndex == 1) {

	    double l1 = lij.getValue();
	    coord = new Vector(new double[] { l1, 0 });

	} else if (stdFIndex == 2) {

	    double l3 = lik.getValue();
	    double t = ai.getValue();
	    coord = new Vector(new double[] { l3 * Math.cos(t),
		    l3 * Math.sin(t) });
	}

	value = 0; // unused
    }

    public void remove() {
	deleteDependents();
	lij.deleteObserver(this);
	ljk.deleteObserver(this);
	lik.deleteObserver(this);
	ai.deleteObserver(this);
	Index.remove(pos);
    }

    public static Coord2D At(Vertex v, Face f) {
	TriPosition T = new TriPosition(v.getSerialNumber(),
		f.getSerialNumber());
	Coord2D q = Index.get(T);
	if (q == null) {
	    q = new Coord2D(v, f);
	    q.pos = T;
	    Index.put(T, q);
	}
	return q;
    }

    public static double valueAt(Vertex v, Face f) {
	return At(v, f).getValue();
    }

    // like getValue(), but returns coordinate point
    public Vector getCoord() {
	double d = getValue(); // used to invoke recalculate if invalid
	return coord;
    }

    // like valueAt(), but returns coordinate point
    public static Vector coordAt(Vertex v, Face f) {
	return At(v, f).getCoord();
    }

}
