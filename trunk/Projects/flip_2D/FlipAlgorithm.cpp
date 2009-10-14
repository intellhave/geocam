#include "FlipAlgorithm.h"
#include "delaunay.h"
#include <iostream>

FlipAlgorithm::FlipAlgorithm()
{
    whichStep = 0;
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
        if (!(currEdge->second).isBorder()          &&  isConvexHinge(currEdge->second) &&
            !isWeightedDelaunay(currEdge->second)   &&  facesAreTheSame(currEdge->second)) {
            flip(currEdge->second);
            ret = currEdge->first;
            //return ret;
        }
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
            return currEdge->first;
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

int FlipAlgorithm::step() {
    int flipped = 0;
    if (!isWeightedDelaunay()) {
        if (whichStep == 0) {
            flipped = flipConvexHinges();
        } else if (whichStep == 1) {
            flipped = flipOneNonConvexHinge();
        }
    } else {
        cout << "Triangulation is already weighted Delaunay\n";
    }
    whichStep = (whichStep + 1) % 2;
    return flipped;
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
