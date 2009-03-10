#include "triangulation/triangulation.h"
#include "eulerApprox.h"

void EulerApprox::step(double dt){
    int vertexCount = Triangulation::vertexTable.size();
    
    // Variables for iterating over the verticies
    map<int, Vertex>::iterator vIter; 
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
    
    double slopes[vertexCount];
    local_derivs(slopes);
    
    double ri;
    int ii = 0;
    for(vIter = vBegin; vIter != vEnd; vIter++, ii++){
        ri = vIter->second.getRadius();
        vIter->second.setRadius(ri + slopes[ii]*dt);
    }
}
