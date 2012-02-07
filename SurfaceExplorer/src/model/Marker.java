package model;

public class Marker {
		
	public static enum MarkerType { Rocket, Sattelite, Tube };
	
	private MarkerType type;
	private Coordinates position;
	private Vector facing;
	
	public Marker( MarkerType type, Coordinates position, Vector facing ){
		this.type = type;
		this.position = position;
		this.facing = facing;
	}
	
	public MarkerType getMarkerType(){
		return this.type;
	}
	
	public Coordinates getPosition(){
		return this.position;
	}
	
	public Vector getFacing(){
		return this.facing;
	}

	public void move(double lambda) {
		position.u += lambda * facing.components[0];
		position.v += lambda * facing.components[1];		
	}
}
