#include "EtaGenerator.h"

double EtaCalculation(double length, double radius1, double radius2) {
       double temp=0;
       temp = (length*length-radius1*radius1-radius2*radius2)/(2*radius1*radius2);
       return temp;
       }
       
      
void EtaGeneration (double lengths[]) {
     // call this function by first storing the radii as usual, and pass the array lengths to this function. 
     map<int, Edge>::iterator eit;
     int i = 1;
     double tempEta=0;
     double radius1=0;
     double radius2=0;
     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
           eit++, i++) {
                  
                  vector<int> localV = *((eit->second).getLocalVertices());
                                    
                  radius1=Radius::valueAt(Triangulation::vertexTable[localV[0]]);
                  radius2=Radius::valueAt(Triangulation::vertexTable[localV[1]]);
                  
                  tempEta=EtaCalculation(lengths[i], radius1, radius2);
       Eta::At(eit->second)->setValue(tempEta);
       }        
     }
