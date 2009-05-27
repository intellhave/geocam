#ifndef TRIPOSITION_H_
#define TRIPOSITION_H_

/* This allows the triposition constructor to take multiple arguments. */
#include <cstdarg>
#include <new>
#include <map>

typedef enum typenum{LENGTH, ANGLE, AREA} quantID;

#define MAX_POINT_IDS 15

class TriPosition {
 public:
  int length;
  quantID qid;
  int pointIDs[MAX_POINT_IDS];

  /* Input: numPoints (a count of the simplicies that follow),
  ** followed by numPoints arguments, each a simplex pointer.*/
  TriPosition(){
    length = 0;
    for(int ii = 0; ii < MAX_POINT_IDS; ii++)
      pointIDs[ii] = -1;
  }
  TriPosition(quantID q, int numPoints, ...);
  ~TriPosition(){}
 
   void print();
};

/* THIS IS BAD BAD BAD. IT MUST BE FIXED LATER. */
class TriPositionCompare{
public:
  bool operator()(const TriPosition x, const TriPosition y){
    if(x.qid < y.qid) return true;

    for(int ii = 0; ii < x.length; ii++){
      if(x.pointIDs[ii] < y.pointIDs[ii]){ return true; }
      else if (x.pointIDs[ii] > y.pointIDs[ii]) { return false;} 

      if(ii == x.length && ii < y.length) return true;
      else if(ii < x.length && ii == y.length) return false;
    }
 
    return false;
  }
};

#endif /* TRIPOSITION_H_ */
