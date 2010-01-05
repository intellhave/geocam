#include <cstdlib>
#include "triangulation.h"
#include "triangulationInputOutput.h"
#include "radius.h"
#include "geoquant.h"
#include "eta.h"
#include "alpha.h"
#include "curvature2Dwneg.h"
#include "approximatorme.h"
#include "eulerApprox.h"

void Ricci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  double Ki, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
    if (vit->first==7 ) {
       Ki = 2*PI - Curvature2Dwneg::valueAt(vit->second);}
    else if ((vit->second).isAdjVertex(7)) {
       Ki =  Curvature2Dwneg::valueAt(vit->second);}
    else {
         Ki = 2*PI + Curvature2Dwneg::valueAt(vit->second);
         } ;   
    ri = Radius::valueAt(vit->second);     
    derivs[ii] = (-1) * Ki * ri;
  }
}



int main(int argc, char** argv) {
   map<int, Vertex>::iterator vit;
   map<int, Edge>::iterator eit;
   map<int, Face>::iterator fit;
        
   vector<int> edges;
   vector<int> faces;
    
   
   time_t start, end;
   
   // File to read in triangulation from.
   char from[] = "Data/2DManifolds/LutzFormat/domain.txt";
   // File to convert to proper format.
   char to[] = "Data/2DManifolds/StandardFormat/domain.txt";
   // Convert, then read in triangulation.
   makeTriangulationFile(from, to);
   readTriangulationFile(to);

   int vertSize = Triangulation::vertexTable.size();
   int edgeSize = Triangulation::edgeTable.size();
   int faceSize = Triangulation::faceTable.size();
   
   
   // Set the radii and alphas
   for(int i = 1; i <= vertSize; i++) {
      Radius::At(Triangulation::vertexTable[i])->setValue(1.0 ) ;        
      Alpha::At(Triangulation::vertexTable[i])->setValue(0.0 );
        printf("alph= %f\n",Alpha::valueAt(Triangulation::vertexTable[i]));
   }
   Radius::At(Triangulation::vertexTable[6])->setValue(0.3 ) ;
   Radius::At(Triangulation::vertexTable[7])->setValue(4.0 ) ;
   Alpha::At(Triangulation::vertexTable[7])->setValue(1.0 ) ;
   // Set the etas
   for(int i = 1; i <= edgeSize; i++) {
   if ((Triangulation::edgeTable[i]).isAdjVertex(7)) {
       Eta::At(Triangulation::edgeTable[i])->setValue(-1.0);}
       else {
       Eta::At(Triangulation::edgeTable[i])->setValue(1.0);}
   printf("%f\n",Eta::valueAt(Triangulation::edgeTable[i]));
   }
   // Construct an Approximator object that uses the Euler method and Ricci flow while
   // recording radii, curvatures.
   for(int i = 1; i <= faceSize; i++) {
   if ((Triangulation::faceTable[i]).isAdjVertex(7)) {
       (Triangulation::faceTable[i]).setNegativity(true); printf("hi");}
       }
 
 
   Approximator *app = new EulerApprox((sysdiffeq) Ricci, "rfnew");

   // Run the flow with precision and accuracy bounds of 0.0001 and stepsize of 0.01
   app->run(100, 0.01);
 writeTriangulationFileWithData("trydata.txt");
   // Print out radii, curvatures and volumes
  printResultsStep("./ODE Result.txt", &(app->radiiHistory), &(app->curvHistory));
   system("Pause");
   return 0;
}
