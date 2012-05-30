package model;

public class Vector {
	
	public double[] components;
		
	public Vector( double... components ){
		this.components = components.clone();		
	}
		
	public Vector( Vector other ){
		this.components = other.components.clone();
	}
		
	public double[] getComponents(){
		return components.clone();
	}
	
	public double getNorm(){
		return Math.sqrt( this.innerProduct( this ) );
	}
	
	public void normalize(){
		double norm = getNorm();
		for( int ii = 0; ii < components.length; ii++ ){
			components[ii] = components[ii]/norm;
		}		
	}
	
	public Vector crossProduct(Vector other){
		if( other.components.length != 3 || this.components.length != 3 ){
			return null;
		}
		
		double xx =   this.components[1] * other.components[2] - this.components[2] * other.components[1];
		double yy = - this.components[0] * other.components[2] + this.components[2] * other.components[0];
		double zz =   this.components[0] * other.components[1] - this.components[1] * other.components[0];
		
		return new Vector( xx, yy, zz );
	}
	
	public double innerProduct(Vector other){		
		double sum = 0.0;
		for( int ii = 0; ii < components.length; ii++ ){
			sum += this.components[ii] * other.components[ii]; 
		}
		return sum;
	}
	
	public double angle(Vector other){
		return Math.acos( this.innerProduct(other) / (this.getNorm()*other.getNorm()) );
	}

	public void scale(double lambda){
		for( int ii=0; ii < components.length; ii++ ){
			components[ii] *= lambda;
		}
	}
	
	public void rotate(double phi) {
		double x = Math.cos(phi)*components[0] - Math.sin(phi)*components[1];
		double y = Math.sin(phi)*components[0] + Math.cos(phi)*components[1];
		components[0] = x;
		components[1] = y;
		normalize();
	}
}
