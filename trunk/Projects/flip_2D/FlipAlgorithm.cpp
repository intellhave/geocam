#include "FlipAlgorithm.h"
#include "delaunay.h"
#include <iostream>

FlipAlgorithm::FlipAlgorithm()
{
    pausesON = 0;
}
FlipAlgorithm::~FlipAlgorithm()
{
}

/*
 * return 0 is nothing happened, 1 is something happened
 */
int FlipAlgorithm::flipConvexHinges() {
    int ret = 0;
    for (currEdge = Triangulation::edgeTable.begin(); currEdge != Triangulation::edgeTable.end(); currEdge++) {
            cout << "here about to check\n";
        if (!(currEdge->second).isBorder() && isConvexHinge(currEdge->second) && !isWeightedDelaunay(currEdge->second)) {
            cout << "flipped an edge\n";
            flip(currEdge->second);
            ret = 1;
            //return ret;
        }
        cout << "checked an edge\n";
    }
    return ret;
}

/*
 * return 0 is nothing happened, 1 is something happened
 */
int FlipAlgorithm::flipOneNonConvexHinge() {
    bool somethingHappened = false;
    for (currEdge = Triangulation::edgeTable.begin(); currEdge != Triangulation::edgeTable.end(); currEdge++) {
        if (!(currEdge->second).isBorder()  && facesAreTheSame((currEdge->second))
                                            && !isConvexHinge(currEdge->second)
                                            && !isWeightedDelaunay(currEdge->second)) {
            flip(currEdge->second);
            return 1;
        }
    }
    return 0;
}

//flips and increments iterator
void FlipAlgorithm::performFlip(void)
{
    if (currEdge != Triangulation::edgeTable.end()) {
        flip(currEdge->second);
    }
}

int FlipAlgorithm::currentEdgeIndex(void)
{
    return currEdge->first;
}

void FlipAlgorithm::runFlipAlgorithm()
{
    int count = 0;
    //cout << "flip beginning\n";
    while(!isWeightedDelaunay() && count < 5) {
        //cout << "in loop\n";
        flipConvexHinges();
        flipOneNonConvexHinge();
        count++;
    }
    //cout << "flip loop ending\n";
}
