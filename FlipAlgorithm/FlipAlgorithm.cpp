#include "FlipAlgorithm.h"
#include "delaunay.h"
#include <iostream>

FlipAlgorithm::FlipAlgorithm()
{
    whichStep = 0;
    resetCurrEdge();
}
FlipAlgorithm::~FlipAlgorithm()
{
}

/*
 * return 0 is nothing happened, 1 is something happened
 */
int FlipAlgorithm::flipConvexHinges() {
    cout << "1\n";
    //currEdge = Triangulation::edgeTable.begin();
    for (; currEdge != Triangulation::edgeTable.end(); currEdge++) {
        if (!(currEdge->second).isBorder()          &&  isConvexHinge(currEdge->second) &&
            !isWeightedDelaunay(currEdge->second)   &&  facesAreTheSame(currEdge->second)) {
            flip(currEdge->second);
            return currEdge->first;
        }
    }
    return -1;
}

/*
 * return -1 is nothing happened, edge index is something happened
 */
int FlipAlgorithm::flipOneNonConvexHinge() {
    cout << "2\n";
    //currEdge = Triangulation::edgeTable.begin();
    for (; currEdge != Triangulation::edgeTable.end(); currEdge++) {
        if (!(currEdge->second).isBorder()  && facesAreTheSame((currEdge->second))
                                            && !isConvexHinge(currEdge->second)
                                            && !isWeightedDelaunay(currEdge->second)) {
            flip(currEdge->second);
            return currEdge->first;
        }
    }
    return -1;
}

//flips
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
    int stayOnThisStep = 0;
    if (whichStep == 0) {
      stayOnThisStep = flipConvexHinges();
    } else if (whichStep == 1) {
      stayOnThisStep = flipOneNonConvexHinge();
    }
    //cout << stayOnThisStep << "\n";
    //cout << currEdge->first << "\n\n";
    if (0 > stayOnThisStep) {
      whichStep = (whichStep + 1) % 2;
      currEdge = Triangulation::edgeTable.begin();
    }
    return stayOnThisStep;
}

void FlipAlgorithm::resetCurrEdge(void) {
  currEdge = Triangulation::edgeTable.begin();
}

void FlipAlgorithm::runFlipAlgorithm()
{
    int step_return = 1;
    //cout << "flip beginning\n";
    while(step_return ) {
        //cout << "in loop\n";
        step_return = step();
        step_return = step_return + step();
    }
    //cout << "flip loop ending\n";
}
