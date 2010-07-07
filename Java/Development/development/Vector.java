package development;

import java.util.Arrays;

public class Vector {
	
	private double[] components_;
	
	//constructors
	public Vector(double[] components){
		components_ = Arrays.copyOf(components,components.length);
	}
	
	public Vector(double x, double y, double z){
		components_ = new double[] { x, y, z };
	}
	
	public Vector(Vector copy){
		components_ = Arrays.copyOf(copy.components_,copy.components_.length);
	}
	
	//methods
	@Override 
	public String toString() {
	    String result = new String("");
	    result += "(" + components_[0];
	    for(int i=1; i<components_.length; i++){
	    	result += "," + components_[i];
	    }
	    result += ")";
	    return result.toString();
	}

	public double getComponent(int k){
		return components_[k];
	}
	
	public int getDimension(){
		return components_.length;
	}

	public void add(Vector rhs) throws Exception{
	//add rhs to this
		if(components_.length != rhs.components_.length){
			throw new Exception("Dimension mismatch");
		}else{
			for(int i=0; i<components_.length; i++){
				components_[i] += rhs.components_[i];
			}
		}
	}
	
	public void subtract(Vector rhs) throws Exception{
	//subtract rhs from this
		if(components_.length != rhs.components_.length){
			throw new Exception("Dimension mismatch");
		}else{
			for(int i=0; i<components_.length; i++){
				components_[i] -= rhs.components_[i];
			}
		}
	}
	
	public void scale(double factor){
	//scale this by factor
		for(int i=0; i<components_.length; i++){
			components_[i] *= factor;
		}
	}
	
	static public double dot(Vector a, Vector b) throws Exception{

		if(a.getDimension() != b.getDimension()){
			throw new Exception("Dimension mismatch");
		}else{
			double result = 0;
			for(int i=0; i<a.getDimension(); i++){
				result += a.components_[i]*b.components_[i];
			}
			return result;
		}
	}
	
	static public Vector cross(Vector a, Vector b) throws Exception{
		
		if((a.getDimension() != 3) || (b.getDimension() != 3)){ 
			throw new Exception("Dimension must be 3");
		}else{
			return new Vector(
					a.components_[1]*b.components_[2]-a.components_[2]*b.components_[1], 
					-a.components_[0]*b.components_[2]+a.components_[2]*b.components_[0], 
					a.components_[0]*b.components_[1]-a.components_[1]*b.components_[0]
			);
		}
	}
	
	public double lengthSquared(){
		
		double result = 0;
		for(int i=0; i<components_.length; i++){
			result += components_[i]*components_[i];
		}
		return result;
	}
	
	public void normalize(){
		
		double len = Math.sqrt(lengthSquared());
		for(int i=0; i<components_.length; i++){
			components_[i] /= len;
		}
	}
}
