/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include <iostream>
#include <cmath>
#include "triangulationPlane.h"
#define PI 	3.141592653589793238

using namespace std;

void runFlow()
{
   char to[] = "Triangulations/icosahedron.txt";
   readTriangulationFile(to);

   //char from[] = "Triangulations/manifold.txt";
   //char to[] = "Triangulations/manifold converted.txt";
   //makeTriangulationFile(from, to);


   //flip(Triangulation::edgeTable[1]);  
   //addNewVertex(Triangulation::faceTable[3], 1);
   //removeVertex(Triangulation::vertexTable[3]);
   //addLeaf(Triangulation::edgeTable[1], 2);
   //addNewVertex(Triangulation::faceTable[5], 1);
   //addNewVertex(Triangulation::faceTable[12], 1);
   //map<int, Face>::iterator fit;

   srand(time(NULL)); 
   
    int vertexSize = Triangulation::vertexTable.size();
    double weights[vertexSize];
    double product = 1;
    for(int i = 1; i <= vertexSize; i++)
   {

       //weights[i - 1] = 3.141592653589793238/(rand() % 100 + 1.0);
       //weights[i - 1] = (i+0.0)/(2*vertexSize);
      weights[i-1] = 0.1;
      //weights[i - 1] = acos(1)/2 + (rand() % 5+1.0)/100.0;
      //weights[i - 1] =   (rand() % 80 + 1)/100.0;
      
   }
                                                   
   char fileName[] = "Triangulations/ODE Result.txt";
   time_t start, end;
   time(&start);
   vector<double> weightsR;
   vector<double> curvatures;
   double dt = 0.03;

   int Steps = 1000;

   calcFlow(&weightsR, &curvatures, dt, weights, Steps, true);
   printResultsStep(fileName, &weightsR, &curvatures);


   time(&end);
   cout << difftime(end, start) << " real time seconds" << endl;
   cout << dt*Steps << " simulated seconds" << endl;
}

void testDelaunay()
{
    char filename[] = "Triangulations/Plane Test.txt";
    generateTriangulation(10);
    map<int, Edge>::iterator eit;
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
         cout << eit->second.getLength() << endl;
    }
    system("PAUSE");
    cout << isWeightedDelaunay(Triangulation::edgeTable[13]) << endl;
    
    
    system("PAUSE");
    for(int i = 1; i < Triangulation::edgeTable.size(); i++)
    {
        cout << "Edge " << i << ": ";
        cout << isDelaunay(Triangulation::edgeTable[i]) << "    ";
        cout << endl;
    }
}


void testGeneratePlane()
{

     generateTriangulation(100);

     char filename[] = "Triangulations/Plane Test.txt";
     writeTriangulationFile(filename);
     TriangulationCoordinateSystem tp;
     tp.generatePlane();
     int index;
     int edgeSize = Triangulation::edgeTable.size();
     int vertexSize = Triangulation::vertexTable.size();

     for(int i = 1; i <= edgeSize; i++)
     {
        cout << i << ": " << Triangulation::edgeTable[i].getLength() << "     ";
       
        if(i % 3 == 0)
        {
             cout << endl;
        }
     }
     cout << "\n";
     
     char fileN[] = "Triangulations/ODE Result.txt";
     tp.printToFile(fileN);


     flipAlgorithm();
     char fileN2[] = "Triangulations/ODE Result 2.txt";
     tp.generatePlane();
     tp.printToFile(fileN2);

}

void testMiscMath()
{
     Line l(4,5,1,1);
     Point p = findPoint(l, 3, -acos(3/5.));
     //printPoint(p);
     Line l2(l.getEnding(), p);
     Line l1(l.getInitial(), p);
     cout << l2.getLength() << " ";
     cout << l1.getLength();
     cout << endl;
}

int main(int argc, char *argv[])
{
    TriangulationCoordinateSystem tcs;
    generateTriangulation(50); 

    tcs.generatePlane();
    generateWeights();
    weightedFlipAlgorithm();
    system("PAUSE");

    int faceSize = Triangulation::faceTable.size();
    cout << faceSize << "\n";
    system("PAUSE");
    for (int i = 0; i < faceSize; i++){
        for (int j = 0 ; j <= 1; j++){
            Vertex v = Triangulation::vertexTable[(*(Triangulation::faceTable[i].getLocalVertices()))[j]]; 
               tcs.addDuals(v);
        }   
    }
 
    tcs.printDuals("Triangulations/duals2.txt");


    
    generateTriangulation(100);
    generateWeights();
    tcs.generatePlane();
    try{
       weightedFlipAlgorithm();
    } catch(string s) {
       cout << s << "\n";
    }
    system("PAUSE");
    return 0;
    

}
