#include "FlipAlgorithm.h"
#include "delaunay.h"
#include <iostream>

FlipAlgorithm::FlipAlgorithm()
{
    whichStep = 0;
    currEdge = Triangulation::edgeTable.begin();
}
FlipAlgorithm::~FlipAlgorithm()
{
}

/*
 * return 0 is nothing happened, 1 is something happened
 */
int FlipAlgorithm::flipConvexHinges() {
    cout << "\n1\n";
    //currEdge = Triangulation::edgeTable.begin();
    for (; currEdge != Triangulation::edgeTable.end(); currEdge++) {
        cout << "in fch looking at edge: " << (currEdge->first) << "\n";
        if (!(currEdge->second).isBorder()          &&  isConvexHinge(currEdge->second) &&
            !isWeightedDelaunay(currEdge->second)   &&  facesAreTheSame(currEdge->second)) {
            cout << "about to flip edge " << (currEdge->first) << "\n";
            flip(currEdge->second);
            cout << "finished flipping edge " << (currEdge->first) << "\n";
            return (currEdge->first);
        }
    }
    return -1;
}

/*
 * return -1 is nothing happened, edge index is something happened
 */
int FlipAlgorithm::flipOneNonConvexHinge() {
    cout << "\n2\n";
    //currEdge = Triangulation::edgeTable.begin();
    for (; currEdge != Triangulation::edgeTable.end(); currEdge++) {
        cout << "in fonch looking at edge: " << (currEdge->first) << "\n";
        if (!(currEdge->second).isBorder()  && facesAreTheSame((currEdge->second))
                                            && !isConvexHinge(currEdge->second)
                                            && !isWeightedDelaunay(currEdge->second)) {
            cout << "about to flip edge " << currEdge->first << "\n";
            flip(currEdge->second);
            cout << "finished flipping edge " << (currEdge->first) << "\n";
            return (currEdge->first);
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
    int stayOnThisStep = 0; //doubles as last flipped edge's index
    if (whichStep == 0) {
      stayOnThisStep = flipConvexHinges();
    } else if (whichStep == 1) {
      stayOnThisStep = flipOneNonConvexHinge();
    }
    if (0 > stayOnThisStep) {
      whichStep = (whichStep + 1) % 2;
      currEdge = Triangulation::edgeTable.begin();
    }
    return stayOnThisStep;
}

void FlipAlgorithm::resetCurrEdge(void) {
    cout << "yoyoyoyoyoyoy\n";
    currEdge = Triangulation::edgeTable.begin();
}

//dont use right now
void FlipAlgorithm::runFlipAlgorithm()
{
    while(false == isWeightedDelaunay()) {
        cout << "started\n";
        flipConvexHinges();
        cout << "moving on\n";
        system("PAUSE");
        flipOneNonConvexHinge();
        cout << "moving on\n";
        system("PAUSE");
    }
}
