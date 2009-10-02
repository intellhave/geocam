#ifndef LENGTH_H_
#define LENGTH_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "triangulation.h"

#include "radius.h"
#include "eta.h"

class Length : public virtual GeoQuant {
 private:
  Radius* radius1;
  Radius* radius2;
  Eta* eta;  

protected:
  Length( Edge& e );
  void recalculate();

public:
  ~Length();
  static Length* At( Edge& e );
  static double valueAt( Edge& e ) {
         return Length::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* LENGTH_H_ */
