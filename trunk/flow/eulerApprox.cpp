#include "triangulation/triangulation.h"
#include "Geometry/Geometry.h"
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
        ri = Geometry::radius(vIter->second);
        Geometry::setRadius(vIter->second, ri + slopes[ii]*dt);
    }
}
