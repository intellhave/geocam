package model;

public class OpenSquare implements Surface {
	private static final double SIDELENGTH = 100.0;
	
	public double getUMin() { return 0; }
	public double getUMax() { return SIDELENGTH; }
	public double getVMin() { return 0; }
	public double getVMax() { return SIDELENGTH; }

	public Coordinates makeCoordinates( double uu, double vv ){		
		while( uu >= SIDELENGTH){
			uu -= SIDELENGTH;			
		}
		while( uu < 0 ){
			uu += SIDELENGTH;
		}
		
		while( vv >= SIDELENGTH){
			vv -= SIDELENGTH;			
		}
		while( vv < 0 ){
			vv += SIDELENGTH;
		}
		
		return new Coordinates(uu,vv);
	}

	public void immersePoint(Coordinates c, double[] R3Point) {
		evaluate( c.u, c.v, R3Point, 0 );
	}

	public Vector immerseVector( Coordinates c, Vector vec ){		
		return new Vector(vec.components[0],vec.components[1],0);
	}

	private static Vector ddu = new Vector(1,0);
	private static Vector ddv = new Vector(0,1);
	
	public Vector getSurfaceNormal(Coordinates c) {
		Vector s = immerseVector( c, ddu );
		Vector t = immerseVector( c, ddv );
		return s.crossProduct( t );
	}
	
	public boolean isImmutable() { return true; }
	
	public int getDimensionOfAmbientSpace() { return 3; }
	
	// This method enables the torus to be rendered by jreality.
	public void evaluate(double u, double v, double[] xyz, int index) {
		xyz[0] = u;
		xyz[1] = v;
		xyz[2] = 0.0;
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

