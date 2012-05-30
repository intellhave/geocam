package model;

import de.jreality.geometry.ParametricSurfaceFactory.Immersion;

public class Saddle implements Surface, Immersion {
	
	private static final double B = 50.0;
	
	public boolean isImmutable() { return true;	}
	public int getDimensionOfAmbientSpace() { return 3; }

	public void evaluate(double u, double v, double[] xyz, int index) {
		xyz[0] = u;
		xyz[1] = v;
		xyz[2] =  0.01*(u*u - v*v);
	}

	public double getUMin() { return -B; }
	public double getUMax() { return B; }
	public double getVMin() { return -B; }
	public double getVMax() { return B;	}

	public Coordinates makeCoordinates(double uu, double vv) {
		return new Coordinates( uu, vv );
	}

	public void immersePoint(Coordinates c, double[] R3Point) {
		evaluate(c.u, c.v, R3Point, 0);
	}

	private static Vector ddu = new Vector(1,0);
	private static Vector ddv = new Vector(0,1);
	
	public Vector immerseVector(Coordinates c, Vector vec) {
		double u = c.u;
		double v = c.v;
		
		double dxdu = 1;
		double dxdv = 0;
				
		double dydu = 0;
		double dydv = 1;
		
		double dzdu = 0.01 * 2*u;
		double dzdv = 0.01 *(-2)*v;
		
		double v0 = dxdu * vec.components[0] + dxdv * vec.components[1];
		double v1 = dydu * vec.components[0] + dydv * vec.components[1];
		double v2 = dzdu * vec.components[0] + dzdv * vec.components[1];
		
		return new Vector(v0,v1,v2);
	}
	
	public Vector getSurfaceNormal(Coordinates c) {
		Vector s = immerseVector( c, ddu );
		Vector t = immerseVector( c, ddv );
		s.normalize();
		t.normalize();		
		return s.crossProduct( t );
	}
	
	// This method is wrong, but yields an OK approximation.
	public Coordinates move(Coordinates start, Vector direction, double distance) {
		Vector v = new Vector(direction);
		v.normalize();
		v.scale(distance);
		Coordinates retval = new Coordinates( start.u, start.v );
		retval.u += v.components[0];
		retval.v += v.components[1];
		
		return retval;		
	}

}
