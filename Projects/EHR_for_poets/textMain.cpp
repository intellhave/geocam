/*=========================================================================
 ||Application: NewtonsMethodForPoets
 ||Author: Alex Henniges (henniges@email.arizona.edu)
 ||
 ||Description: The purpose of this program is to provide a front-end
 ||       interface for basic users hoping to run perform an action on
 ||       a triangulation, such as Newton's Method or Yamabe Flow. The
 ||       user is first asked to provide an input file. The input file
 ||       should provide much of the information necessary to perform
 ||       one of the actions. The specific format is described in the
 ||       readme file, but the input file includes a triangulation file,
 ||       a file for output, and the initial values for radii and etas.
 ||       The user will then choose which application to run.
 ||       
 ||       The program provides some ease to the user by maintaining a 
 ||       knowledge of the last 5 unique input files provided. There are 
 ||       then presented as options in the program. In order for this to
 ||       occur, the program accesses a hidden file in a subfolder of the
 ||       TirangulationFiles directory.
 ||
 ||Bugs/TODO: Implement the option for Yamabe Flow. Add checks for the
 ||     non-existance of the the hidden file, and simply ignore uses
 ||       of the file if it does not exist.
 *=========================================================================*/

/* ==== Include Files ==== */
#include <cstdlib>
#include <iostream>
#include <list>

#include "triangulation.h"
#include "3DInputOutput.h"

#include "textRuns.h"

#include "radius.h"
#include "eta.h"
#include "utilities.h"
/* ======================= */

/* This struct represents an event that the program will memorize
   for later use. The event is the choice of an input file name.
*/
typedef struct event {
  char name[1000];
  //event* nextEvent;
} event;


/*==============================================================================
 ||Function: parseInputFile(FILE* input, char* output)
 ||Params: FILE* input - The input file, already opened.
 ||        char* outputFile - A placeholder for the output file,
 ||                           which will be retrieved by parsing the
 ||                           input file.
 ||Return: void
 ||Description: This function parses a given input file, reading in the
 ||             triangulation, extracting the name of the output file,
 ||             and setting the initial radii and etas.
 ||             
 ||Bugs/TODO:   Does not handle poorly formatted input files. Functionality
 ||             is undefined when this occurs.
 *============================================================================*/
void parseInputFile(FILE* input, char* outputFile) {
     char line[1000]; // Holds a line of the input file, up to 1000 characters.
     char triangulationFile[200]; // Holds the name of the triangulation file. 
     
     char radius[] = "radius"; // Fixed string for the word "radius".
     char eta[] = "eta"; // Fixed string for the word "eta".
     
     char type[20]; // Holds the name of the type of value (radius, eta).
     int index; // Holds the index number of the current value (radius 1, eta 3).
     double value; // Holds the actual value.
     
     // Get the first line of the input file.
     fgets(line, 1000, input);
     // Scan the line to get the name of the triangulation file.
     sscanf(line, "Triangulation: %s", triangulationFile);
     // Get the next line of the input file.
     fgets(line, 1000, input);
     // Scan the line to get the name of the output file.
     sscanf(line, "Output: %s", outputFile);
     
     // Read in the triangulation.
     read3DTriangulationFile(triangulationFile);
     
     // While there are more lines to be read...
     while(!feof(input)) {
        //... read a line.
        fgets(line, 1000, input);
        // Determine the type, index and value.
        sscanf(line, "%s %d: %lf", type, &index, &value);
        
        // If the type is radius...
        if(strcmp(type, radius) == 0) {
          //... set the radius at vertex given by index with the value given.
          Radius::At(Triangulation::vertexTable[index])->setValue(value);
        } else if(strcmp(type, eta) == 0) { // If the type is eta...
          //... set the eta at edge given by index with the value given.
          Eta::At(Triangulation::edgeTable[index])->setValue(value);
        } else {
          // ERROR in type field
        }
     }
}

/* Helper function scans through the list of filenames in log and adds them
 * to the event list.
 */
void checkPastEvents(FILE* log, list<event>* eventList) {  
     while(!feof(log)) {
        event e;
        fscanf(log, "%s", e.name);
        eventList->push_back( e );
     }
}

/*
 * Helper function places the list of the latest five events into
 * the file given by log.
 */
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

/*
 * Main function.
 */
int main(int argc, char *argv[])
{
    int fileChoice, program; // Values that will be filled in by the user.
    FILE* inputFile; // File handle for the input file.
    // Open the hidden file.
    FILE* log = fopen("Projects/EHR_for_poets/eventlog.txt", "r");
    if(log == NULL) {
         pause("Error: Couldn't find log file\n");       
    }
    // Collect any previous events.
    list<event> eventList;
    checkPastEvents(log, &eventList);
    fclose(log);

    // Query for an input file, providing a list of the past 5 choices.
    printf("Select to read one of the input files below, or choose to provide a new file: \n");
    list<event>::iterator evit;
    int i;
    for(evit = eventList.begin(), i = 1; evit != eventList.end(); evit++, i++) {
       printf("\t%d) %s\n", i, (*evit).name);
    }
    printf("\t0) New file\n");
    printf("Choice: ");
    scanf("%d", &fileChoice);
    
    // If they chose to provide a new file...
    if(fileChoice == 0) {
       // Ask and receive the filename.
       printf("Provide an input file: ");
       char filename[1000];
       scanf("%s", filename);
       inputFile = fopen(filename, "r"); // TODO: Check for bad file
       // Add this filename to the front of the vent list, then search the list
       // for a previous occurence if it exists and remove it from the list.
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
       // If there are now more than 5 events, remove the oldest one.
       if(eventList.size() > 5) {
          eventList.pop_back();
       }
    } else {
       // If a filename was chosen, iterate to it and open it. Then push
       // that filename to the front of the event list.
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
       runMin(outputFile);
    }
    log = fopen("Projects/EHR_for_poets/eventlog.txt", "w");
    logEvents(log, &eventList);
    fclose(log);
    
    pause("Done...press enter to exit");
}
