#ifndef FACEHEIGHT_H_
#define FACEHEIGHT_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "edge_height.h"
#include "dih_angle.h"


class FaceHeight : public virtual GeoQuant {
private:
  EdgeHeight* hij_l;
  EdgeHeight* hij_k;
  DihedralAngle* beta_ij_kl;

protected:
  FaceHeight( Face& f, Tetra& t );
  void recalculate();

public:
  ~FaceHeight();
  static FaceHeight* At( Face& f, Tetra& t );
  static void CleanUp();
  static void Record( char* filename );
};

#endif /* FACEHEIGHT_H_ */
