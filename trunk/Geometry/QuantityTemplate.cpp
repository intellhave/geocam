#ifndef QUANTITY_H_
#define QUANTITY_H_

class QUANTITY : public virtual GeoQuant {
private:
  /* Your private varaibles here. */

public:
  QUANTITY( ?????? ) : GeoQuant() {
    /* YOUR INITIALIZATION CODE GOES HERE:
     * 1) Establish a triposition
     * 2) Calculate dependencies (run AddDependents)
     * 3) initialize private variables.
     */
  }

  void recalculate(){
    /* Recalculate this quantity your chosen
     * private variables
     */
  }
};


#endif /* QUANTITY_H_ */
