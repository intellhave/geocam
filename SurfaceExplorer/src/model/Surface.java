package model;

import de.jreality.geometry.ParametricSurfaceFactory.Immersion;

public abstract class Surface implements Immersion {
	public abstract double getUMin(); 
	public abstract double getUMax();
	public abstract double getVMin(); 
	public abstract double getVMax();
	
	public abstract Coordinates makeCoordinates( double uu, double vv );
	
	public void immersePoint(Coordinates c, double[] R3Point) {
		evaluate(c.u, c.v, R3Point, 0);
	}

	public abstract Vector immerseVector(Coordinates c, Vector v);	
	
	private static Vector ddu = new Vector(1,0);
	private static Vector ddv = new Vector(0,1);
	public Vector getSurfaceNormal(Coordinates c){
		Vector s = immerseVector( c, ddu );
		Vector t = immerseVector( c, ddv );
		s.normalize();
		t.normalize();		
		return s.crossProduct( t );		
	}
	
	public boolean isImmutable() { return true; }
	
	/* Ideally, this method would move points along geodesics.
	 * For present purposes, just "moving a point in coordinates"
	 * is an OK substitute. */ 
	public Coordinates move(Coordinates start, Vector direction, double distance){
		Vector v = new Vector(direction);
		v.normalize();
		v.scale(distance);
		Coordinates retval = new Coordinates( start.u, start.v );
		retval.u += v.components[0];
		retval.v += v.components[1];
		
		return retval;		
	}
	
	public int getDimensionOfAmbientSpace() { return 3; }
}
