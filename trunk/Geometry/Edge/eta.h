#ifndef ETA_H_
#define ETA_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

class Eta : public virtual GeoQuant {

protected:
  Eta( Edge& e );
  void recalculate();

public:
  ~Eta();
  static Eta* At( Edge& e );
  static double valueAt(Edge& e) {
         return Eta::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
  static void print(FILE* out);
};

#endif /* ETA_H_ */
