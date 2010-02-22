#include <cstdlib>
#include "triangulation.h"
#include "triangulationInputOutputgeometric.h"
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
  
  double Ki,Knine, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; vit++){
    if (vit->first==9 ){Knine = 1.0*(2*PI - Curvature2Dwneg::valueAt(vit->second));
      break;}}
  for(vit = vBegin; vit != vEnd; ii++, vit++){
    if (vit->first==9 ) {
      Ki = 1.0*(2*PI - Curvature2Dwneg::valueAt(vit->second));
       }
    else if ((vit->second).isAdjVertex(9)) {
       Ki =  1.0*(Curvature2Dwneg::valueAt(vit->second));}
    else {
         Ki = 2*PI + Curvature2Dwneg::valueAt(vit->second);
         } ;   
    ri = Radius::valueAt(vit->second);     
//    derivs[ii] = (-1) * (Ki - Knine) * ri;
   derivs[ii] = (-1) * (Ki) * ri;
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
   char from[] = "Data/2DManifolds/LutzFormat/domain2.txt";
   // File to convert to proper format.
   char to[] = "Data/2DManifolds/StandardFormat/domain2.txt";
   // Convert, then read in triangulation.
   makeTriangulationFile(from, to);
   readTriangulationFileWithData(to);
writeTriangulationFile("fudge.txt");
   int vertSize = Triangulation::vertexTable.size();
   int edgeSize = Triangulation::edgeTable.size();
   int faceSize = Triangulation::faceTable.size();
   
   
   // Set the radii and alphas
   for(int i = 1; i <= vertSize; i++) {
      Radius::At(Triangulation::vertexTable[i])->setValue(1.0 ) ;        
      Alpha::At(Triangulation::vertexTable[i])->setValue(0.0 );
        printf("alph= %f\n",Alpha::valueAt(Triangulation::vertexTable[i]));
   }
   Radius::At(Triangulation::vertexTable[6])->setValue(1.0 ) ;
   Radius::At(Triangulation::vertexTable[7])->setValue(1.0 ) ;
   Radius::At(Triangulation::vertexTable[8])->setValue(1.2 ) ;
   Radius::At(Triangulation::vertexTable[9])->setValue(90.0 ) ;
   Alpha::At(Triangulation::vertexTable[9])->setValue(1.0 ) ;
   // Set the etas
  // for(int i = 1; i <= edgeSize; i++) {
//   if ((Triangulation::edgeTable[i]).isAdjVertex(9)) {
//       Eta::At(Triangulation::edgeTable[i])->setValue(-1.0);}
//       else {
//       Eta::At(Triangulation::edgeTable[i])->setValue(1.0);}
//   printf("%f\n",Eta::valueAt(Triangulation::edgeTable[i]));
//   }

for(int i = 1; i <= edgeSize; i++) {
   if ((Triangulation::edgeTable[i]).isAdjVertex(9)) {
       Eta::At(Triangulation::edgeTable[i])->setValue(0.0);}
       else {
       Eta::At(Triangulation::edgeTable[i])->setValue(1.0);}
}
  Eta::At(Triangulation::edgeTable[2])->setValue(0.5);
Eta::At(Triangulation::edgeTable[3])->setValue(0.5);
Eta::At(Triangulation::edgeTable[5])->setValue(0.5);
Eta::At(Triangulation::edgeTable[7])->setValue(0.5);
Eta::At(Triangulation::edgeTable[8])->setValue(4.5);
Eta::At(Triangulation::edgeTable[9])->setValue(2.5);
Eta::At(Triangulation::edgeTable[10])->setValue(2.0);
Eta::At(Triangulation::edgeTable[13])->setValue(4.5);
Eta::At(Triangulation::edgeTable[14])->setValue(5.0);
Eta::At(Triangulation::edgeTable[15])->setValue(2.0);
Eta::At(Triangulation::edgeTable[16])->setValue(0.5);

   // Construct an Approximator object that uses the Euler method and Ricci flow while
   // recording radii, curvatures.
   for(int i = 1; i <= faceSize; i++) {
   if ((Triangulation::faceTable[i]).isAdjVertex(9)) {
       (Triangulation::faceTable[i]).setNegativity(true); printf("hi");}
       }
 writeTriangulationFileWithData("initdata.txt"); 
 printf("degree of 1 =%d\n",(Triangulation::vertexTable[1]).getDegree());
   Approximator *app = new EulerApprox((sysdiffeq) Ricci, "rfnew");

   // Run the flow with precision and accuracy bounds of 0.0001 and stepsize of 0.01
   app->run(6000, 0.001);
 writeTriangulationFileWithData("trydata.txt");
   // Print out radii, curvatures and volumes
  printResultsStep("./ODE Result.txt", &(app->radiiHistory), &(app->curvHistory));
   system("Pause");
   return 0;
}
