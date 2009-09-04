#include <cstdlib>
#include <iostream>
#include <list>

#include "triangulation.h"
#include "3DTriangulation/3DInputOutput.h"

#include "textRuns.h"

#include "radius.h"
#include "eta.h"
#include "utilities.h"

typedef struct event {
  char name[1000];
  //event* nextEvent;
} event;


void parseInputFile(FILE* input, char* outputFile) {
     char line[1000];
     char triangulationFile[200];
     
     char radius[] = "radius";
     char eta[] = "eta";
     
     char type[20];
     int index;
     double value;
     
     fgets(line, 1000, input);
     //printf("Line 1: %s\n", line);
     sscanf(line, "Triangulation: %s", triangulationFile);
     fgets(line, 1000, input);
     //printf("Line 2: %s\n", line);
     sscanf(line, "Output: %s", outputFile);
     
     read3DTriangulationFile(triangulationFile);
     
     int i = 3;
     while(!feof(input)) {
        fgets(line, 1000, input);
        //printf("Line %d: %s", i++, line);
        sscanf(line, "%s %d: %lf", type, &index, &value);
        
        //printf("type: %s, index: %d, value %f\n", type, index, value);
        
        if(strcmp(type, radius) == 0) {
          Radius::At(Triangulation::vertexTable[index])->setValue(value);
        } else if(strcmp(type, eta) == 0) {
          Eta::At(Triangulation::edgeTable[index])->setValue(value);
        } else {
          // ERROR
        }
     }
}

void checkPastEvents(FILE* log, list<event>* eventList) {  
     while(!feof(log)) {
        event e;
        fscanf(log, "%s", e.name);
        eventList->push_back( e );
     }
}

void logEvents(FILE* log, list<event>* eventList) {
     list<event>::iterator evit;
     int i;
     for(evit = eventList->begin(), i = 0; evit != eventList->end(); evit++, i++) {
       if(i == 0) {
          fprintf(log, "%s", (*evit).name);
       } else {
          fprintf(log, "\n%s", (*evit).name);
       }
     }     
}

int main(int argc, char *argv[])
{
    int fileChoice, program;
    FILE* inputFile;
    FILE* log = fopen("TriangulationFiles/InputUI/eventlog.txt", "r");
    //event* head = NULL;
    list<event> eventList;
    checkPastEvents(log, &eventList);
    fclose(log);

    printf("Select to read one of the input files below, or choose to provide a new file: \n");
    list<event>::iterator evit;
    int i;
    for(evit = eventList.begin(), i = 1; evit != eventList.end(); evit++, i++) {
       printf("\t%d) %s\n", i, (*evit).name);
    }
    printf("\t0) New file\n");
    printf("Choice: ");
    scanf("%d", &fileChoice);
    
    if(fileChoice == 0) {
       printf("Provide an input file: ");
       char filename[1000];
       scanf("%s", filename);
       inputFile = fopen(filename, "r");
       
       event e;
       strcpy(e.name, filename);
       eventList.push_front(e);
       for(evit = eventList.begin(), i = 0; evit != eventList.end(); evit++, i++) {
          if(i != 0) {
               if(strcmp(evit->name, e.name) == 0) {
                  eventList.erase(evit);
                  break;
               }
          }
       }
       if(eventList.size() > 5) {
          eventList.pop_back();
       }
    } else {
       for(evit = eventList.begin(), i = 1; i < fileChoice; evit++, i++) {
       }
       event e = (*evit);
       inputFile = fopen(e.name, "r");
       
       eventList.erase(evit);
       eventList.push_front(e);
    }
    
    char outputFile[1000];
    parseInputFile(inputFile, outputFile);
    fclose(inputFile);
    
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    
    printf("\n");
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
    {
       printf("Radius %d: %f\n", vit->first, Radius::valueAt(vit->second));
    }
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       printf("Eta %d: %f\n", eit->first, Eta::valueAt(eit->second));
    }
    
    printf("\n");
    printf("Choose a program from the following list using its numerical key:\n");
    printf("\t1) Newtons Method on radii\t2) Newtons Method on etas\n");
    printf("\t3) Yamabe Flow\n");
    
    printf("Program: ");
    scanf("%d", &program);
    
    if(program == 1) {
       runPipelinedNewtonsMethod(outputFile);
    } else if(program == 2) {
       runNewtonsMethod(outputFile);
    } else if(program == 3) {
       printf("Yamabe Flow\n");
    } else {
       // ERROR
    }
    log = fopen("TriangulationFiles/InputUI/eventlog.txt", "w");
    logEvents(log, &eventList);
    fclose(log);
    
    pause("Done...press enter to exit");
}
