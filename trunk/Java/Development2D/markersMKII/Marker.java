package markersMKII;

import development.Vector;

/*********************************************************************************
 * Marker
 * 
 * This class is responsible for describing items that "inhabit" a piecewise
 * flat manifold. This means keeping track of the item's position, orientation,
 * speed, and appearance. By "appearance," we mean a piece of data describing
 * the marker (e.g. is it an Ant or a Rocket?) not the data that will represent
 * the marker in a given view (e.g. some mesh data from a file).
 * 
 *********************************************************************************/
public class Marker {
  /*********************************************************************************
   * Internally, each Marker is assigned a unique id, based on the value of this
   * idCounter. Note that currently, accessing the counter is not thread safe,
   * so if one allows multiple threads to construct markers, there is a good
   * possibility the id numbers will not be unique.
   *********************************************************************************/
  private static int idCounter = 0;

  protected int index;
  protected boolean isVisible;
  protected MarkerAppearance app;
  protected ManifoldPosition pos;

  protected double speed;

  /*********************************************************************************
   * Marker Constructors
   * 
   * Given a position on the manifold, a marker appearance, and possibly a
   * velocity, these constructors create a new marker with those parameters.
   * 
   * TODO: Reimplement this so that the input ManifoldPosition and
   * MarkerAppearance are copied, rather than referenced. This will require
   * changing the code in other places.
   *********************************************************************************/
  public Marker(ManifoldPosition mp, MarkerAppearance ma) {
    // Assign this Marker a unique ID.
    // Note: This code is not thread safe.
    index = idCounter;
    idCounter = idCounter + 1;

    pos = mp;
    app = ma;
    isVisible = true;
    speed = 0.0;
  }

  public Marker(ManifoldPosition mp, MarkerAppearance ma, Vector velocity) {
    this(mp, ma);
    Vector forward = new Vector(velocity);
    forward.normalize();
    pos.setOrientation(forward);
    speed = velocity.length();
  }

  /*********************************************************************************
   * get/set Methods These are self explanatory, unless otherwise documented.
   *********************************************************************************/
  public ManifoldPosition getPosition() {
    return pos;
  }

  public void setPosition(ManifoldPosition pos) {
    this.pos = new ManifoldPosition(pos);
  }

  public int getIndex() {
    return index;
  }

  /*********************************************************************************
   * setVisible / isVisible
   * 
   * These methods determine whether the marker in question should be visible in
   * the scenes it will belong to. By default, markers are visible.
   *********************************************************************************/
  public void setVisible(boolean visibility) {
    isVisible = visibility;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public MarkerAppearance getAppearance() {
    return app;
  }

  public void setAppearance(MarkerAppearance appearance) {
    app = appearance;
  }

  /*********************************************************************************
   * setVelocity
   * 
   * This method takes as input a vector indicating the desired velocity of the
   * marker, in "units per second" where "units" are our abstract unit of length
   * measurement in the simulation. If the zero vector is given as input, we
   * assign the marker to have an arbitrary orientation (in the first coordinate
   * direction).
   *********************************************************************************/
  public void setVelocity(Vector velocityUnitsPerSecond) {
    double L = velocityUnitsPerSecond.length();
    if (L == 0) {
      pos.setOrientation(new Vector(1, 0));
    } else {
      pos.setOrientation(Vector.scale(velocityUnitsPerSecond, 1 / L));
    }
    speed = L * .001; // convert to units per ms
  }

  /*********************************************************************************
   * setSpeed
   * 
   * Given an input speed, this method adjusts the velocity vector of the marker
   * to have that speed. Unlike with setVelocity, setting the speed of the
   * marker to 0 with this method will maintain the object's orientation.
   *********************************************************************************/
  public void setSpeed(double speedUnitsPerSecond) {
    speed = speedUnitsPerSecond * .001;
  }
  
  /*********************************************************************************
   * getSpeed
   * 
   * Returns speed in units per second
   *********************************************************************************/
  public double getSpeed(){
    return speed * 1000;
  }
  
  /*********************************************************************************
   * toString
   * 
   * This method constructs a brief textual description of the marker in
   * question.
   *********************************************************************************/
  public String toString() {
    return "Marker #" + this.hashCode() + "< index=" + index + " visible="
        + isVisible + ">";
  }

  /*********************************************************************************
   * updatePosition
   * 
   * Given an input time-step dt, this method updates the position of the marker
   * (based on its velocity) by an amount specified by the time-step.
   *********************************************************************************/
  public void updatePosition(double dt) {
    pos.move(pos.getDirection(dt * speed, 0));
  }
}
