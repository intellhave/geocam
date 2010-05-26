#include "special_flip.h"
#include <queue>

bool isThreeHinge(Vertex v) {
    return (v.getLocalFaces()->size() == 3);
}

void makeThreeFace(Edge e, Vertex v) {

    vector<int>::iterator eit = v.getLocalEdges()->begin();
    queue<int> q;

    //queue up the edges that we will potentially be flipping
    for (; eit != v.getLocalEdges()->end(); eit++) {
        //we don't want to consider flipping the edge that is part of
        //the non-convex hinge
        if (*eit == e.getIndex()) {
            continue;
        }
        q.push(*eit);
    }

    while (!q.empty()) {
        int anEdge = q.front();
        q.pop(); //pop only deletes, doesn't return front; mega-lame

        //flip and discard any convex hinges we come across
        if (isConvexHinge(Triangulation::edgeTable[anEdge])) {
            flip(Triangulation::edgeTable[anEdge]);
        } else {
        //non-convex hinges will be examined again once all other hinges have been flipped
            q.push(anEdge);
        }
    }
}

Vertex findNonConvexVertex(Edge e) {
    if (isConvexHinge(e)) {
        cout << "Trying to find non-convex vertex on Edge: " << e.getIndex() << " but,\n";
        cout << "Edge: " << e.getIndex() << " is convex\n";
        cout << "going to exit\n";
        system("PAUSE");
        exit(1);
    }
    int f0 = (*(e.getLocalFaces()))[0];
    int f1 = (*(e.getLocalFaces()))[1];
    int v0 = (*(e.getLocalVertices()))[0];
    int v1 = (*(e.getLocalVertices()))[1];

    double ang0 = EuclideanAngle::valueAt(Triangulation::vertexTable[v0], Triangulation::faceTable[f0]);
    ang0 += EuclideanAngle::valueAt(Triangulation::vertexTable[v0], Triangulation::faceTable[f1]);

    double ang1 = EuclideanAngle::valueAt(Triangulation::vertexTable[v1], Triangulation::faceTable[f0]);
    ang1 += EuclideanAngle::valueAt(Triangulation::vertexTable[v1], Triangulation::faceTable[f1]);

    if (ang0 > PI) {
        return Triangulation::vertexTable[v0];
    } else if (ang1 > PI) {
        return Triangulation::vertexTable[v1];
    } else {
        return true;
    }
}

bool flip3to1(Vertex v) {
    if (!isThreeHinge(v)) {
        cout << "Trying to do a 3-1 flip with a vertex that is not 3-1, nothing happens...";
        return false;
    }

    Face *newFace = &(Triangulation::faceTable[*(v.getLocalFaces()->begin())]);

    vector<int> badEdges;
    vector<int> commonEdges;
    vector<int>::iterator eit;
    vector<int>::iterator fit;

    //collect all the edges adjacent to v; they may not be in 0,1,2 spots of the
    //vector so this will ensure that
    eit = v.getLocalEdges()->begin();
    for (; eit != v.getLocalEdges()->end(); eit++) {
        badEdges.push_back(*eit);
    }
    //get a copy of all the edges that will be part of the newface after
    //the center vertex is removed
    fit = v.getLocalFaces()->begin();
    for (; fit != v.getLocalFaces()->end(); fit++) {
        Face face = Triangulation::faceTable[*fit];
        eit = face.getLocalEdges()->begin();
        for (; eit != face.getLocalEdges()->end(); eit++) {
            //make sure this edges is not next to the center vertex
            if (badEdges[0] != *eit && badEdges[1] != *eit && badEdges[2] != *eit) {
                commonEdges.push_back(*eit);
            }
        }
    }

    //remove simplices around vertex
    //remove faces near this vertex
    fit = v.getLocalFaces()->begin();
    for (; fit != v.getLocalFaces()->end(); fit++) {
        Triangulation::eraseFace(*fit);
    }
    //remove edges near this vertex
    eit = v.getLocalEdges()->begin();
    for (; eit != v.getLocalEdges()->end(); eit++) {
        Triangulation::eraseEdge(*eit);
    }

    newFace->getLocalVertices()->clear();
    newFace->getLocalFaces()->clear();
    newFace->getLocalEdges()->clear();

    //add to the newFace (which is one of the old faces) the appropriate simplices

    //add in the center vertices three adjacent vertices
    vector<int>::iterator vit = v.getLocalVertices()->begin();
    for (; vit != v.getLocalVertices()->end(); vit++) {
        newFace.addVertex(*vit);
        Triangulation::vertexTable[*vit].addFace(newFace.getIndex());
    }
    if (commonEdges.size() != 3) {
        cout << "common edges should have only three entries\n";
        system("PAUSE");
    }
    //add back in the edges, note that there should be three of these
    for (int i= 0; i < commonEdges.size(); i++) {
        newFace->addEdge(commonEdges[i]);
        Triangulation::edgeTable[commonEdges[i]].addFace(newFace->getIndex());

        //also add in the face that is adjacent to this edge
        //there should only be one for each, because the three faces adjacent
        //to the cetner vertex were removed from
        //the triangulation prior to this, and part of that involves removing
        //them from these edges
        Edge edge = Triangulation::edgeTable[commonEdges[i]];
        //if this edge was already a boundary edge before this function was called
        //then there will be no adjacent face at this point
        if (edge.getLocalFaces()->size() >= 1) {
            Face tmpFace = *(edge.getLocalFaces()->begin())
            newFace->addFace(tmpFace.getIndex());
            Triangulation::faceTable[tmpFace.getIndex()].addFace(newFace->getIndex());
        }
    }

    Triangulation::eraseVertex(v.getIndex());

    return true;
}
