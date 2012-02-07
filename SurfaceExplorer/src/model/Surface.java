package model;

import de.jreality.geometry.ParametricSurfaceFactory.Immersion;

public interface Surface extends Immersion {
	double getUMin(); 
	double getUMax();
	double getVMin(); 
	double getVMax();
	
	Coordinates makeCoordinates( double uu, double vv );
	void immersePoint(Coordinates c, double[] R3Point);
	Vector immerseVector(Coordinates c, Vector v);	
	Vector getSurfaceNormal(Coordinates c);
	Coordinates move(Coordinates start, Vector direction, double distance);
}
