#include "hinge_flip.h"

class FlipAlgorithm {

    map<int, Edge>::iterator currEdge;
    
    int whichStep;

    public:
    FlipAlgorithm(void);
    ~FlipAlgorithm(void);

    int flipConvexHinges(void);
    
    int flipOneNonConvexHinge(void);

    void performFlip(void);

    int currentEdgeIndex(void);
    
    int step(void);

    void runFlipAlgorithm(void);
};
