package frontend;

public class MarkerSettings {
	private int numMarkers;
	private double markerSpeed;
	private double markerSize;
	private boolean moveMarkers;
	private boolean drawAvatar;
	private boolean drawMarkers;

	public MarkerSettings(int numMarkers, double markerSpeed,
			double markerScale, boolean moveMarkers,
			boolean drawAvatar, boolean drawMarkers) {
		this.numMarkers = numMarkers;
		this.markerSpeed = markerSpeed;
		this.markerSize = markerScale;
		this.moveMarkers = moveMarkers;
		this.drawAvatar = drawAvatar;
		this.drawMarkers = drawMarkers;
	}
	
	public int getNumMarkers(){
		return this.numMarkers;	
	}
	
	public double getMarkerSpeed(){
		return this.markerSpeed;	
	}
	
	public double getMarkerScale(){
		return this.markerSize;	
	}
	
	public boolean moveMarkers(){
		return this.moveMarkers;
	}
	
	public boolean drawAvatar(){
		return this.drawAvatar;
	}
	
	public boolean drawMarkers(){
		return this.drawMarkers;
	}

	public String toString(){
		String retval = "";
		retval += super.toString() + "\n";
		retval += "\t # Markers: " + this.numMarkers + "\n";
		retval += "\t Marker Speed: " + this.markerSpeed + "\n"; 
		retval += "\t Marker Size: " + this.markerSize + "\n"; 
		retval += "\t Markers Move: " + this.moveMarkers + "\n"; 
		retval += "\t Draw Avatar: " + this.drawAvatar + "\n"; 
		retval += "\t Draw Markers: " + this.drawMarkers+ "\n"; 
		return retval;
	}
}
