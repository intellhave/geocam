#include "FlipAlgorithm.h"
#include "delaunay.h"
#include <iostream>

FlipAlgorithm::FlipAlgorithm()
{
    currEdge = Triangulation::edgeTable.begin();
}
FlipAlgorithm::~FlipAlgorithm()
{
}

//checks the current edge then checks subsequent edges
int FlipAlgorithm::findNextFlip(void)
{
    while( currEdge != Triangulation::edgeTable.end()) {
        if (isWeightedDelaunay(currEdge->second)) {
            currEdge++;
        } else {
            break;
        }
    }
    
    if (currEdge == Triangulation::edgeTable.end()) {
        return 0;
    } else {
        return 1;
    }
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

void FlipAlgorithm::runFlipAlgorithm(void)
{
    while (step()) {}
}

bool FlipAlgorithm::step(void) {
    if (findNextFlip()) {
        performFlip();
        currEdge++;
        return true;
    } else {
        return false;
    }
}

void FlipAlgorithm::reset(void) {
    currEdge == Triangulation::edgeTable.begin();
}
