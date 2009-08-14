#include "hinge_flip.h"

class FlipAlgorithm {

    map<int, Edge>::iterator currEdge;

    public:
    FlipAlgorithm(void);
    ~FlipAlgorithm(void);

    int findNextFlip(void);

    void performFlip(void);

    //void undoFlip(void);

    int currentEdgeIndex(void);
    
    bool step(void);
    
    void reset(void);

    void runFlipAlgorithm(void);
};
