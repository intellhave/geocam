#ifndef QUANTITY_H_
#define QUANTITY_H_

class QUANTITY : public virtual GeoQuant {
private:
  /* Your private varaibles go here. For example:
   * - Private parameters (doubles, ints, etc) for
   *   tracking the state of the quantity
   * - Pointers to other GeoQuant objects that your
   *   quantity depends on.
   */

public:
  quantity( ?????? ) : GeoQuant() {
    /* YOUR INITIALIZATION CODE GOES HERE:
     * 1) Establish a triposition
     * 2) Calculate dependencies (run AddDependents)
     * 3) initialize private variables.
     */
  }

  ~quantity() {
    /* IMPORTANT: 
     * Free any memory you allocated to hold your
     * private variables. For an example, see
     * the destructor in euc_angle.cpp.
     */
  }
  
  void recalculate(){
    /* Recalculate this quantity, given your chosen
     * private variables and the quantities your
     * quantity depends on.
     */
  }
};

#endif /* QUANTITY_H_ */
