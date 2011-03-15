package objects;

/* This is the most basic type of VisibleObject
 * It doesn't do anything but sit in one place
 * This is useful for drawing, for example, the source point on a development
 */

public class FixedObject extends VisibleObject{

  public FixedObject(ManifoldPosition manifoldPosition, ObjectAppearance appearance){
    super(manifoldPosition, appearance);
  }

  public void setManifoldPosition(ManifoldPosition newPosition){
    setManifoldPosition(newPosition.getFace(), newPosition.getPosition());
  }
}
