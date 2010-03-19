#include "hinge_flip.h"

#ifndef FLIPALGORITHM_H
#define FLIPALGORITHM_H

class FlipAlgorithm {

    //points to the currenly selected edge
    map<int, Edge>::iterator currEdge;

    //flip flops between 0 and 1 so that step() knows which function to call
    int whichStep;

    public:
    FlipAlgorithm(void);
    ~FlipAlgorithm(void);

    //iterates over the edges flipping any that are convex and not weighted Delaunay
    //returns 0 if none are found, 1 if a flip occurs
    int flipConvexHinges(void);

    //seeks out the a non-convex hinge that is also not weighted Delaunay and flips it
    //returns 0 if non are found, 1 if a flip occurs
    int flipOneNonConvexHinge(void);

    //calls the function flip_hinge on currently selected edge
    void performFlip(void);

    //returns the index of the currenly selected edge
    int currentEdgeIndex(void);

    void FlipAlgorithm::resetCurrEdge(void);

    //performs a single "step" of the algorithm, by calling either flipConvexHinges, or flipOneNonConvexHinge
    int step(void);

    //performs steps until 0 is returned by step
    void runFlipAlgorithm(void);
};
#endif
