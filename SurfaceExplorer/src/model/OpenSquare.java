package model;

public class OpenSquare extends Surface {
	private static final double SIDELENGTH = 50.0;
	
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

	public Vector immerseVector( Coordinates c, Vector vec ){		
		return new Vector(vec.components[0],vec.components[1],0);
	}
	
	// This method enables the torus to be rendered by jreality.
	public void evaluate(double u, double v, double[] xyz, int index) {
		xyz[0] = u;
		xyz[1] = v;
		xyz[2] = 0.0;
	}
}

