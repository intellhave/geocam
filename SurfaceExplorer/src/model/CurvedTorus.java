package model;

public class CurvedTorus extends Surface {
	private static final double TWO_PI = 2 * Math.PI;
	
	public static final double INNER_RADIUS = 40;
	public static final double TUBE_RADIUS = 20;
	private static final double R = INNER_RADIUS;
	private static final double r = TUBE_RADIUS;

	public double getUMin() { return 0; }
	public double getUMax() { return TWO_PI; }
	public double getVMin() { return 0; }
	public double getVMax() { return TWO_PI; }

	public Coordinates makeCoordinates( double uu, double vv ){		
		while( uu >= TWO_PI){
			uu -= TWO_PI;			
		}
		while( uu < 0 ){
			uu += TWO_PI;
		}
		
		while( vv >= TWO_PI){
			vv -= TWO_PI;			
		}
		while( vv < 0 ){
			vv += TWO_PI;
		}
		
		return new Coordinates(uu,vv);
	}
	
	public Vector immerseVector( Coordinates c, Vector vec ){		
		double u = c.u;
		double v = c.v;
		
		double dxdu = r * Math.sin(u) * Math.cos(v);
		double dxdv = Math.sin(v) * (R + r * Math.cos(u));
				
		double dydu = r * Math.sin(u) * Math.sin(v);
		double dydv = - Math.cos(v) * (R + r * Math.cos(u));
		
		double dzdu = - r * Math.cos(u);
		double dzdv = 0;
		
		double v0 = dxdu * vec.components[0] + dxdv * vec.components[1];
		double v1 = dydu * vec.components[0] + dydv * vec.components[1];
		double v2 = dzdu * vec.components[0] + dzdv * vec.components[1];
		
		return new Vector(v0,v1,v2);
	}

	// This method enables the torus to be rendered by jreality.
	public void evaluate(double u, double v, double[] xyz, int index) {
		xyz[0] = - (R + r * Math.cos(u)) * Math.cos(v);
		xyz[1] = - (R + r * Math.cos(u)) * Math.sin(v);
		xyz[2] = - r * Math.sin(u);
	}

}
