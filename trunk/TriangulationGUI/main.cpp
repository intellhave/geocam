/**************************************************************
File: main.cpp
Author: Alex Henniges
Version: December 4, 2008
***************************************************************
This is the main file to run the user interface for the
triangulation program. Requires the program to be run on a
Windows machine.

For a Win32 tutorial: http://www.winprog.org/tutorial/
**************************************************************/

/************** Include Files ***************/
#include <windows.h>
#include <ctime>
#include <iostream>
#include <fstream>
#include <cmath>
#include <vector>
#include <gl/gl.h>
#include "resources.h"
#include "glTriangulation.h"
#include "TriangulationModel.h"
#define PI 	3.141592653589793238
/*******************************************/

/*********** Function Prototypes ***********/
BOOL CALLBACK PolygonProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam);
VOID EnableOpenGL( HWND hWnd, HDC * hDC, HGLRC * hRC );
VOID DisableOpenGL( HWND hWnd, HDC hDC, HGLRC hRC );
/*******************************************/

/************ Global Variables *************/
HINSTANCE hInst;
HDC hDC; // Used to enable a window with gl and other properties
HGLRC hRC; // Used to enable a window with gl
GLuint listbase; // Listbase for drawing text in gl
HFONT hFont; // Font when drawing text in gl
BOOL bQuit; // Boolean variable for use in PolygonProc
/*******************************************/

/**************************************************************
Function: FileChooser
Parameters: HWND hwnd, LPSTR szFileName
Returns: void
***************************************************************
Opens the usual window to select a file from a directory, placing
the pathname of the chosen file in szFileName. hwnd is the handle
to the parent window, mainly needed by the system.

Code outline provided by the Win32 tutorial.
**************************************************************/
void FileChooser(HWND hwnd, LPSTR szFileName)
{
    // An OPENFILENAME contains all the needed information for opening a file
	OPENFILENAME ofn; 
	// Clear ofn first
	ZeroMemory(&ofn, sizeof(ofn));

	ofn.lStructSize = sizeof(OPENFILENAME);
	ofn.hwndOwner = hwnd;
	// lpstrFilter holds the info for allowable file extensions.
	// In this case, onlly need text files, but allow for any type of file
	// to be chosen.
	ofn.lpstrFilter = "Text Files (*.txt)\0*.txt\0All Files (*.*)\0*.*\0";
	ofn.lpstrFile = szFileName; // Give pointer for filename to placed into
	ofn.nMaxFile = MAX_PATH;
	// Flags: only allow existing files, don't show read only files
	ofn.Flags = OFN_EXPLORER | OFN_FILEMUSTEXIST | OFN_HIDEREADONLY;
	ofn.lpstrDefExt = "txt"; // The default extension

	GetOpenFileName(&ofn); // Call Windows to open up file choosing window
}

/**************************************************************
Function: LoadTextFile
Parameters: HWND hEdit, LPCTSTR pszFileName
Returns: BOOL
***************************************************************
Prints the contents of the file given by pszFileName into the
window given by hEdit. Returns a boolean indicating success of
opening and printing file.

Code outline provided by the Win32 tutorial.
**************************************************************/
BOOL LoadTextFile(HWND hEdit, LPCTSTR pszFileName)
{
	HANDLE hFile;
	BOOL bSuccess = FALSE;

	hFile = CreateFile(pszFileName, GENERIC_READ, FILE_SHARE_READ, NULL,
		OPEN_EXISTING, 0, NULL);
	if(hFile != INVALID_HANDLE_VALUE)
	{
		DWORD dwFileSize;

		dwFileSize = GetFileSize(hFile, NULL);
		if(dwFileSize != 0xFFFFFFFF)
		{
			LPSTR pszFileText;

			pszFileText = (LPSTR)GlobalAlloc(GPTR, dwFileSize + 1);
			if(pszFileText != NULL)
			{
				DWORD dwRead;

				if(ReadFile(hFile, pszFileText, dwFileSize, &dwRead, NULL))
				{
					pszFileText[dwFileSize] = 0; // Add null terminator
					if(SetWindowText(hEdit, pszFileText))
						bSuccess = TRUE; // It worked!
				}
				GlobalFree(pszFileText);
			}
		}
		CloseHandle(hFile);
	}
	return bSuccess;
}

/**************************************************************
Function: UpdateConsole
Parameters: HWND hwnd, LPSTR message
Returns: void
***************************************************************
Appends text given by message to the IDC_CONSOLE window.
hwnd is the parent window and used only by system.
**************************************************************/
void UpdateConsole(HWND hwnd, LPSTR message)
{
    // First add message to the hidden_text window.
    SetDlgItemText(hwnd, IDC_HIDDENTEXT, message);
    // Get length of current text and new text.
    int len = GetWindowTextLength(GetDlgItem(hwnd, IDC_CONSOLE));
    int len2 = GetWindowTextLength(GetDlgItem(hwnd, IDC_HIDDENTEXT));
    if(len > 0)
    {
        // Buffers to temporarily place text in
        char* buf; // Will hold previous text and new text
        char* buf2; // Will hold new text
        // Globally allocate the memory for the buffers
        buf = (char*)GlobalAlloc(GPTR, len + len2 + 1);
        buf2 = (char*)GlobalAlloc(GPTR, len2 + 1);
        // Place text in buffers
        GetDlgItemText(hwnd, IDC_CONSOLE, buf, len + 1);
        GetDlgItemText(hwnd, IDC_HIDDENTEXT, buf2, len2 + 1);
        // Copy characters from buf2 into buf1
        for(int i = 0; i< len2 + 1; i++)
        {
            buf[len + i] = buf2[i];
        }
        // Console can only show up to 17 lines, instead of scrolling text
        // we want to only show the 17 newest lines.
        int newlines = 0;
        // Vector holds indices where newlines are in buf.
        vector<int> newlinePos;
        for(int i = 0; i < len + len2 + 1 ; i++)
        {
           if(buf[i] == '\n')
           {
              newlines++;
              newlinePos.push_back(i);
           }
        }
        // If there are more than 17 lines, remove as many lines from old text
        // as there are lines from new text.
        if(newlines > 17) {
            newlines = 0;
            for(int i = 0; i < len2 + 1; i++)
            {
               if(buf2[i] == '\n')
               {
                 newlines++;
               }
            }
            int len3 = len + len2 - newlinePos[newlines - 1];
            char buf3[len3];
            for(int i = 0; i < len3; i++)
            {
               buf3[i] = buf[newlinePos[newlines - 1] + 1 + i];
            }
            // Set the console with the new text
            SetDlgItemText(hwnd, IDC_CONSOLE, buf3);
        } else
        {
          // Set the console with the new text
          SetDlgItemText(hwnd, IDC_CONSOLE, buf);
        }
        // Free allocated memory
        GlobalFree((HANDLE)buf);
        GlobalFree((HANDLE)buf2);
    }
}

/**************************************************************
Function: PolygonProc
Parameters: HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam
Returns: BOOL
***************************************************************
Handles all procedures involving the Polygon Flow dialog box.

The PolygonProc, like all dialog procs has the following parameters:
    HWND hwnd - The parent window calling the procedure
    UINT Message - The particular command sent to the dialog box
    WPARAM wParam - Possibly a parameter attached to the Message.
    LPARAM lParam - Possibly a parameter attached to the Message.
These parameters do not have to be given by the user, they are
handled by the system.

Returns the standard indication of success or failure of a dialog proc.
**************************************************************/
BOOL CALLBACK PolygonProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam) 
{
     switch(Message)
     {
        case WM_INITDIALOG:
        {
             // Set the default flow speed.
             SetDlgItemText(hwnd, IDC_POLYGON_SPEED, "100");
        }
        break;
        case WM_COMMAND:
            switch(LOWORD(wParam))
            {
                case IDPOLYGON: // Run the polygon flow
                {
                   MSG msg; // Used to check for messages while flow is running.
                   bQuit = false; // Setting it to false here resets flow if run
                                  // button is clicked again.
                   HWND hPoly = GetDlgItem(hwnd, IDC_POLYGON);
                   // Read in numerical data to draw flow
                   ifstream scanner("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt");
                   // Enable the hPoly window with gl properties.
                   EnableOpenGL( hPoly, &hDC, &hRC );
                   // Set step number to 0.
                   SetDlgItemText(hwnd, IDC_POLYGON_STEP, "0000");
                   
                   char stepArr[5] = {'\0'}; // Used to maintain step number.
                   int step = 1;
                   
                   char speedArr[5]; // Used to determine a provided speed.
                   GetDlgItemText(hwnd, IDC_POLYGON_SPEED, speedArr, 5);
                   int speed = atoi(speedArr);
                   if(speed <= 0) 
                   {
                      MessageBox(NULL, "Invalid speed", "Error", MB_OK | MB_ICONINFORMATION);
                      return FALSE;
                   }
                   
                   // While there are more steps and not interrupted
                   while (scanner.good() && !bQuit) 
                   {
                     // Check for any messages and perform them.
                     if (PeekMessage(&msg, hwnd, 0, 0, PM_REMOVE)) 
                     {
                        switch(msg.message) {
                          case WM_COMMAND: {
                              switch(LOWORD(msg.wParam)) {
                              case IDCANCEL: {
                                // End flow before closing dialog window.
                                // Else flow just runs in background.
                                bQuit = true;
                              }
                              break;
                              default: ;// do nothing
                              }
                          }
                          break;
                          default: ;
                        }
                        
                          TranslateMessage(&msg);
                          DispatchMessage(&msg);

                      } 

                      SwapBuffers( hDC ); // Swap buffers.
                      // Get number of vertices.
                      int size = Triangulation::vertexTable.size();
                      
                      // Get that number of curvatures from file
                      double curv[size];
                      for(int i = 0; i < size; i++)
                      {
                          scanner >> curv[i];
                      }
                      
                      // Clear window.
                      glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
                      glClear(GL_COLOR_BUFFER_BIT);
                      
                      // Draw a polygon given curvatures.
                      drawPolygon(size, curv);
                      
                      // Display step number, then increment
                      itoa(step, stepArr, 10);
                      SetDlgItemText(hwnd, IDC_POLYGON_STEP, stepArr);
                      step++;
                      
                      // Pause for length of time = speed.
                      Sleep(speed);
                    }
                    SetDlgItemText(hwnd, IDC_POLYGON_STEP, "----");
                    DisableOpenGL( hPoly, hDC, hRC );
                    bQuit = true; // Setting it to true here resets flow if run
                                  // button was clicked more than once.
                }
                break;
                case IDPOLYGON_STOP: // Stop current flow
                     bQuit = true;
                break;                           
                case IDCANCEL: // End dialog box
                    bQuit = true;
                    EndDialog(hwnd, IDCANCEL);
                break;
            }
        break;
        default:
            return FALSE;
    }
    return TRUE;
}

/**************************************************************
Function: RadiiDlgProc
Parameters: HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam
Returns: BOOL
***************************************************************
Handles all procedures involving the manual choosing of radii.

The RadiiDlgProc, like all dialog procs has the following parameters:
    HWND hwnd - The parent window calling the procedure
    UINT Message - The particular command sent to the dialog box
    WPARAM wParam - Possibly a parameter attached to the Message.
    LPARAM lParam - Possibly a parameter attached to the Message.
These parameters do not have to be given by the user, they are
handled by the system.

Returns the standard indication of success or failure of a dialog proc.
**************************************************************/
BOOL CALLBACK RadiiDlgProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam)
{
     switch(Message)
     {
        case WM_INITDIALOG:
        {
           
           map<int, Vertex>::iterator vit;
           int i = 0;
           for(vit = Triangulation::vertexTable.begin(); 
                   vit != Triangulation::vertexTable.end(); vit++) {
               char text[25] = "Vertex    : 1.00000 ";
               int vertexI = vit->first;
               int arrSize = 0;
               int temp = vertexI;
               if(temp <= 0) {
                 arrSize = 1;
               } else {
                 while(temp != 0) {
                  temp /= 10;
                  arrSize++;
                 }
               }
               char vertexNum[arrSize + 1];
               itoa(vertexI, vertexNum, 10);
               strncpy(text + 7, vertexNum, arrSize);
               int index = SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_ADDSTRING, 0, (LPARAM)text);
               SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_SETITEMDATA, (WPARAM)index, 
                                 (LPARAM)vertexI);
               TriangulationModel::setWeight(vertexI, 1.00000);
               i++;
           }
           map<int, Edge>::iterator eit;
           i = 0;
           for(eit = Triangulation::edgeTable.begin(); 
                   eit != Triangulation::edgeTable.end(); eit++) {
               char text[25] = "Edge    : 1.00000 ";
               int edgeI = eit->first;
               int arrSize = 0;
               int temp = edgeI;
               if(temp <= 0) {
                 arrSize = 1;
               } else {
                 while(temp != 0) {
                  temp /= 10;
                  arrSize++;
                 }
               }
               char edgeNum[arrSize + 1];
               itoa(edgeI, edgeNum, 10);
               strncpy(text + 5, edgeNum, arrSize);
               int index = SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_ADDSTRING, 0, (LPARAM)text);
               SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_SETITEMDATA, (WPARAM)index, 
                                 (LPARAM)edgeI);
               TriangulationModel::setEta(edgeI, 1.00000);
               i++;
           }

           
           listbase = glGenLists(255);
 
           // make the system font the device context's selected font
           setFont(&hFont, "Arial", 10);
                      
           return TRUE;
        }
        case WM_COMMAND:
            switch(LOWORD(wParam))
            {
                case IDRADII_SET:
                {
                     char weightStr[MAXWEIGHTSIZE];
                     GetDlgItemText(hwnd, IDC_RADII, weightStr, MAXWEIGHTSIZE);
                     double weight = atof(weightStr);
                     if(weightStr[0] == '\0') {
                        // Do nothing
                     }
                     else if(weight <= 0.0) {
                        MessageBox(NULL, "Invalid weight", "Error", MB_OK | MB_ICONINFORMATION);
                     } else {
       					HWND hList = GetDlgItem(hwnd, IDC_RADII_LIST);
			            int countRadii = SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETSELCOUNT, 0, 0);
					    
						if(countRadii != 0)
						{
							// And then allocate room to store the list of selected items.

							int i;
							int *buf;
                            buf = (int*)GlobalAlloc(GPTR, sizeof(int) * countRadii);
							SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETSELITEMS, (WPARAM)countRadii, (LPARAM)buf);
						    
                            
							for(i = countRadii - 1; i >= 0; i--)
							{
                                char text[25];
                                SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETTEXT, (WPARAM)buf[i], (LPARAM)text);
                                int index = SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETITEMDATA, (WPARAM)buf[i], 0);
                                strncpy(text + 12, weightStr, MAXWEIGHTSIZE);
                                SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_INSERTSTRING, (WPARAM)buf[i], (LPARAM)text);
                                SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_SETITEMDATA, (WPARAM)buf[i], (LPARAM)index);
                                SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_SETSEL, (WPARAM)1, (LPARAM)buf[i]);
                                SendDlgItemMessage(hwnd, IDC_RADII_LIST,  LB_DELETESTRING, (WPARAM)(buf[i] + 1), 0);
                                TriangulationModel::setWeight(index, weight);
     						}

							GlobalFree(buf);
                          }
                     }
                     char etaStr[MAXWEIGHTSIZE];
                     GetDlgItemText(hwnd, IDC_ETA, etaStr, MAXWEIGHTSIZE);
                     double eta = atof(etaStr);
                     
                     int countEta = SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETSELCOUNT, 0, 0);
					    
                     if(countEta != 0 && etaStr[0] != '\0')
                     {
							// And then allocate room to store the list of selected items.

							int i;
							int *buf;
                            buf = (int*)GlobalAlloc(GPTR, sizeof(int) * countEta);
							SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETSELITEMS, (WPARAM)countEta, (LPARAM)buf);
						    
                            
							for(i = countEta - 1; i >= 0; i--)
							{
                                char text[25];
                                SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETTEXT, (WPARAM)buf[i], (LPARAM)text);
                                int index = SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETITEMDATA, (WPARAM)buf[i], 0);
                                strncpy(text + 10, etaStr, MAXWEIGHTSIZE);
                                SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_INSERTSTRING, (WPARAM)buf[i], (LPARAM)text);
                                SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_SETITEMDATA, (WPARAM)buf[i], (LPARAM)index);
                                SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_SETSEL, (WPARAM)1, (LPARAM)buf[i]);
                                SendDlgItemMessage(hwnd, IDC_ETA_LIST,  LB_DELETESTRING, (WPARAM)(buf[i] + 1), 0);
                                TriangulationModel::setEta(index, eta);
     						}

							GlobalFree(buf);
                     }
                     return TRUE;
                }
                break;
                case IDRADII_RANDOM:
                {
                    srand(time(NULL));
                    map<int, Vertex>::iterator vit;
                    int i = 0;
                    char weightStr[MAXWEIGHTSIZE];
                    for(vit = Triangulation::vertexTable.begin(); 
                            vit != Triangulation::vertexTable.end(); vit++) {
                        double weight = rand() % 100 / 100.0 + 0.5;
                        sprintf(weightStr, "%.4f", weight);
                        char text[25];
                        SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETTEXT, (WPARAM)i, (LPARAM)text);
                        strncpy(text + 12, weightStr, MAXWEIGHTSIZE);
                        SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_INSERTSTRING, (WPARAM)i, (LPARAM)text);
                        SendDlgItemMessage(hwnd, IDC_RADII_LIST,  LB_DELETESTRING, (WPARAM)(i + 1), 0);
                        SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_SETITEMDATA, (WPARAM)i, (LPARAM)vit->first);
                        TriangulationModel::setWeight(vit->first, weight);
                        i++;
                    }
                    return TRUE;
                }
                break;
                case IDETA_RANDOM:
                {
                    srand(time(NULL));
                    map<int, Edge>::iterator eit;
                    int i = 0;
                    char etaStr[MAXWEIGHTSIZE];
                    for(eit = Triangulation::edgeTable.begin(); 
                            eit != Triangulation::edgeTable.end(); eit++) {
                        double eta = rand() % 100 / 100.0 + 0.5;
                        sprintf(etaStr, "%.4f", eta);
                        char text[25];
                        SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETTEXT, (WPARAM)i, (LPARAM)text);
                        strncpy(text + 10, etaStr, MAXWEIGHTSIZE);
                        SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_INSERTSTRING, (WPARAM)i, (LPARAM)text);
                        SendDlgItemMessage(hwnd, IDC_ETA_LIST,  LB_DELETESTRING, (WPARAM)(i + 1), 0);
                        SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_SETITEMDATA, (WPARAM)i, (LPARAM)eit->first);
                        TriangulationModel::setEta(eit->first, eta);
                        i++;
                    }
                    return TRUE;
                }                
                case IDC_RADII_LIST: {
                    switch(HIWORD(wParam))
					{
					  case LBN_SELCHANGE:
						{
                          HWND hRadii = GetDlgItem(hwnd, IDC_RADII_FLOWER);
                          EnableOpenGL( hRadii, &hDC, &hRC );
                          SelectObject(hDC, hFont);
                          // create the bitmap display lists 
                          // we're making images of glyphs 0 thru 255 
                          // the display list numbering starts at 1000, an arbitrary choice 
                          wglUseFontBitmaps(hDC, 0, 255, listbase);
                          glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
                          glClear(GL_COLOR_BUFFER_BIT);
                          
                          if(SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETSELCOUNT, 0, 0) == 1) {

                             int index;
                             SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETSELITEMS, (WPARAM)1, (LPARAM)&index);
                             int vIndex = (int) SendDlgItemMessage(hwnd, IDC_RADII_LIST, LB_GETITEMDATA, (WPARAM)index, 0);
                             drawVertex(Triangulation::vertexTable[vIndex]);
                             vector<int> localE = *(Triangulation::vertexTable[vIndex].getLocalEdges());
                             int size = localE.size();

                             glColor3f(1.0f, 0.0f, 0.0f);
                             vector<int> localV;
                             for(int i = 0; i < size; i++) {
                                 Edge edge = Triangulation::edgeTable[localE[i]];
                                 localV = *(edge.getLocalVertices());
                                 int otherIndex = localV[0] == vIndex ? 1 : 0; 
                                 float angle = (float) 2 * PI / size * i;
                                 int arrSize = 0;
                                 int temp = localV[otherIndex];
                                 if(temp <= 0) {
                                    arrSize = 1;
                                 } else {
                                   while(temp != 0) {
                                     temp /= 10;
                                     arrSize++;
                                   }
                                 }
                                 char vertexNum[arrSize + 1];
                                 itoa(localV[otherIndex], vertexNum, 10);
                                
                                 drawText(.8*cos(angle), .8*sin(angle), arrSize, vertexNum, listbase);

                             }
                             
                             glColor3f(0.0f, 0.0f, 1.0f);
                             for(int i = 0; i < size; i++) {
                                 float angle = (float) 2 * PI / size * i;
                                 int arrSize = 0;
                                 int temp = localE[i];
                                 if(temp <= 0) {
                                    arrSize = 1;
                                 } else {
                                   while(temp != 0) {
                                     temp /= 10;
                                     arrSize++;
                                   }
                                 }
                                 char edgeNum[arrSize + 1];
                                 itoa(localE[i], edgeNum, 10);
                                 drawText(.4*cos(angle), .4*sin(angle), arrSize, edgeNum, listbase);                             
                             }

                          }
                          
                          SwapBuffers( hDC );
                          
                          DisableOpenGL( hRadii, hDC, hRC );
                          return TRUE;
                        }
                        break;
                    }
                    
                }
                break;
                case IDC_ETA_LIST: {
                    switch(HIWORD(wParam))
					{
					  case LBN_SELCHANGE:
						{
                          HWND hEta = GetDlgItem(hwnd, IDC_ETA_FLOWER);
                          EnableOpenGL( hEta, &hDC, &hRC );
                          SelectObject(hDC, hFont);
                          // create the bitmap display lists 
                          // we're making images of glyphs 0 thru 255 
                          // the display list numbering starts at 1000, an arbitrary choice 
                          wglUseFontBitmaps(hDC, 0, 255, listbase);

                          glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
                          glClear(GL_COLOR_BUFFER_BIT);
                          if(SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETSELCOUNT, 0, 0) == 1) {

                             int index;
                             SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETSELITEMS, (WPARAM)1, (LPARAM)&index);
                             int eIndex = (int) SendDlgItemMessage(hwnd, IDC_ETA_LIST, LB_GETITEMDATA, (WPARAM)index, 0);
                             vector<int> localV = *(Triangulation::edgeTable[eIndex].getLocalVertices());
                             int size = localV.size();
                             drawEdge(Triangulation::edgeTable[eIndex]);
                             double edgeL = Triangulation::edgeTable[eIndex].getLength();

                             char lengthArr[15];
                             sprintf(lengthArr, "%f", edgeL);
                             glColor3f(0.0f, 0.0f, 1.0f);
                             drawText(0.2, -0.9, strlen(lengthArr), lengthArr, listbase);
                                                      
                             glColor3f(1.0f, 0.0f, 0.0f);
                             // Specify the position of the text
                             int arrSize = 0;
                             int temp = localV[0];
                             if(temp <= 0) {
                                arrSize = 1;
                             } else {
                               while(temp != 0) {
                                 temp /= 10;
                                 arrSize++;
                               }
                             }
                             char vertexNum1[arrSize + 1];
                             itoa(localV[0], vertexNum1, 10);
                             
                             drawText(0.8, 0.0, arrSize, vertexNum1, listbase);
                             
                             arrSize = 0;
                             temp = localV[1];
                             if(temp <= 0) {
                                arrSize = 1;
                             } else {
                               while(temp != 0) {
                                 temp /= 10;
                                 arrSize++;
                               }
                             }
                             char vertexNum2[arrSize + 1];
                             itoa(localV[1], vertexNum2, 10);
                             
                             drawText(-0.8, 0.0, arrSize, vertexNum2, listbase);
                          }
                          
                          SwapBuffers( hDC );
                          
                          DisableOpenGL( hEta, hDC, hRC );
                          return TRUE;

                        }
                        break;
                    }
                }
                break;
                case IDOK: 
                {
                    if(listbase)
                      glDeleteLists(listbase,255);
                    EndDialog(hwnd, IDOK);
                }
                break;
                case IDCANCEL:
                    if(listbase)
                      glDeleteLists(listbase,255);                     
                    EndDialog(hwnd, IDCANCEL);
                break;
            }
        break;
        default:
            return FALSE;
    }
    return TRUE;
}

BOOL CALLBACK FormatDlgProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam)
{
     switch(Message)
     {
        case WM_INITDIALOG:
        {
           HWND hcombo = NULL;
           hcombo = GetDlgItem(hwnd, IDC_FORMATSELECTBOX);
           if(hcombo == NULL)
           {
              MessageBox(NULL, "m_hCmbo is NULL", "Error", MB_OK | MB_ICONINFORMATION);
           }
           SendMessage(hcombo, CB_ADDSTRING, 0, (LPARAM)"Standard");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"Lutz");
           return TRUE;
        }
        case WM_COMMAND:
            switch(LOWORD(wParam))
            {
                case IDOK: 
                {
                    char format[10];
                    GetDlgItemText(hwnd, IDC_FORMATSELECTBOX, format, 10);
                    if(format[0] == 'S')
                       EndDialog(hwnd, IDSTANDARD);
                    else if(format[0] == 'L')
                       EndDialog(hwnd, IDLUTZ);
                    else
                       EndDialog(hwnd, IDCANCEL);
                }
                break;
                case IDCANCEL:
                    EndDialog(hwnd, IDCANCEL);
                break;
            }
        break;
        default:
            return FALSE;
    }
    return TRUE;
}
BOOL CALLBACK FlowDlgProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam)
{
     switch(Message)
     {
        case WM_INITDIALOG:
        {
           HWND hcombo = NULL;
           hcombo = GetDlgItem(hwnd, IDC_WEIGHTSELECTBOX);
           if(hcombo == NULL)
           {
              MessageBox(NULL, "hcombo is NULL", "Error", MB_OK | MB_ICONINFORMATION);
           }
           SendMessage(hcombo, CB_ADDSTRING, 0, (LPARAM)"Manually");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"Random");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"From file");
           hcombo = NULL;
           hcombo = GetDlgItem(hwnd, IDC_FLOWSELECTBOX);
           if(hcombo == NULL)
           {
              MessageBox(NULL, "hcombo is NULL", "Error", MB_OK | MB_ICONINFORMATION);
           }
           SendMessage(hcombo, CB_ADDSTRING, 0, (LPARAM)"Normalized");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"Standard");
           return TRUE;
        }
        case WM_COMMAND:
            switch(LOWORD(wParam))
            {
                case IDOK: 
                {
                    BOOL ready = TRUE;
                    char weight[25];
                    GetDlgItemText(hwnd, IDC_WEIGHTSELECTBOX, weight, 25);
                    char flow[25];
                    GetDlgItemText(hwnd, IDC_FLOWSELECTBOX, flow, 25);
                    TriangulationModel::setFlowFunction(flow[0] == 'N');
                    BOOL numEntered;
                    int steps = GetDlgItemInt(hwnd, IDC_STEPSTEXT, &numEntered, FALSE);
                    ready = ready && numEntered;
                    if(!numEntered)
                       MessageBox(NULL, "Provide the number of steps", "Error", MB_OK | MB_ICONINFORMATION);           
                    else 
                       TriangulationModel::setNumSteps(steps);
                    int stepsize = GetDlgItemInt(hwnd, IDC_DTTEXT, &numEntered, FALSE);
                    ready = ready && numEntered;
                    if(!numEntered)
                       MessageBox(NULL, "Provide the step size", "Error", MB_OK | MB_ICONINFORMATION);           
                    else 
                       TriangulationModel::setStepSize(stepsize / 1000.0);
                    if(ready && weight[0] == 'R')
                       EndDialog(hwnd, IDWEIGHTSRANDOM);
                    else if(ready && weight[0] == 'F')
                       EndDialog(hwnd, IDWEIGHTSFILE);
                    else if(ready && weight[0] == 'M')
                       EndDialog(hwnd, IDWEIGHTSMAN);
                    else if(ready)
                       MessageBox(NULL, "Weights not chosen", "Error", MB_OK | MB_ICONINFORMATION);
                }
                break;
                case IDCANCEL:
                    EndDialog(hwnd, IDCANCEL);
                break;
            }
        break;
        default:
            return FALSE;
    }
    return TRUE;
}
BOOL CALLBACK DlgProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam)
{
	switch(Message)
	{
        
		case WM_INITDIALOG:
        {
           
           HWND hcombo = NULL;
           hcombo = GetDlgItem(hwnd, IDC_RESULTSSELECTBOX);
           if(hcombo == NULL)
           {
              MessageBox(NULL, "hcombo is NULL", "Error", MB_OK | MB_ICONINFORMATION);
           }
           SendMessage(hcombo, CB_ADDSTRING, 0, (LPARAM)"Group by step");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"Group by vertex");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"Numbers only");
           SendMessage(hcombo, CB_ADDSTRING, 1, (LPARAM)"Polygon Flow");
           SetDlgItemText(hwnd, IDC_CONSOLE, "Welcome to the Triangulation Program\r\n");
           HWND hHiddenEdit = GetDlgItem(hwnd, IDC_HIDDENTEXT);
           ShowWindow(hHiddenEdit, SW_HIDE);

        }
		break;
		case WM_COMMAND:
			switch(LOWORD(wParam))
			{
                case ID_FILE_EXIT:
                     EndDialog(hwnd, 0);
                break;
                case ID_FILE_LOAD_MANIFOLD:
                {
                     char filename[MAX_PATH] ="";
                     FileChooser((HWND)hwnd, (LPSTR) filename);
                     UpdateConsole(hwnd, "Loading file...\r\n");
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_CHOOSEFILETYPE), hwnd, FormatDlgProc);
                     if(ret == IDSTANDARD)
                     {
                       TriangulationModel::clearSystem();
                       bool success = TriangulationModel::loadFile(filename, IDSTANDARD);
                       if(!success)
                       {
                         MessageBox(NULL, "Could not load file", "Error", MB_OK | MB_ICONINFORMATION);
                         UpdateConsole(hwnd, "File load failed.\r\n");
                       } 
                       else{
                         UpdateConsole(hwnd, "File loaded.\r\n");
                       } 
                     } else if(ret == IDLUTZ)
                     {
                       TriangulationModel::clearSystem();
                       bool success = TriangulationModel::loadFile(filename, IDLUTZ);
                       if(!success)
                       {
                         MessageBox(NULL, "Could not load file", "Error", MB_OK | MB_ICONINFORMATION);
                         UpdateConsole(hwnd, "File load failed.\r\n");
                       } 
                       else{
                         UpdateConsole(hwnd, "File loaded.\r\n");
                       }    
                     } else if(ret == IDCANCEL)
                     {
                         UpdateConsole(hwnd, "Load canceled.\r\n");   
                     }
                }
                break;
                case ID_FILE_LOAD_3DMANIFOLD:
                {
                     char filename[MAX_PATH] ="";
                     FileChooser((HWND)hwnd, (LPSTR) filename);
                     UpdateConsole(hwnd, "Loading file...\r\n");
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_CHOOSEFILETYPE), hwnd, FormatDlgProc);
                     if(ret == IDSTANDARD)
                     {
                       TriangulationModel::clearSystem();
                       bool success = TriangulationModel::load3DFile(filename, IDSTANDARD);
                       if(!success)
                       {
                         MessageBox(NULL, "Could not load file", "Error", MB_OK | MB_ICONINFORMATION);
                         UpdateConsole(hwnd, "File load failed.\r\n");
                       } 
                       else{
                         UpdateConsole(hwnd, "File loaded.\r\n");
                       } 
                     } else if(ret == IDLUTZ)
                     {
                       TriangulationModel::clearSystem();
                       bool success = TriangulationModel::load3DFile(filename, IDLUTZ);
                       if(!success)
                       {
                         MessageBox(NULL, "Could not load file", "Error", MB_OK | MB_ICONINFORMATION);
                         UpdateConsole(hwnd, "File load failed.\r\n");
                       } 
                       else{
                         UpdateConsole(hwnd, "File loaded.\r\n");
                       }    
                     } else if(ret == IDCANCEL)
                     {
                         UpdateConsole(hwnd, "Load canceled.\r\n");   
                     }
                }
                break;
                case ID_RUN_FLOW_EUCLIDEAN:
                {
                     if(!TriangulationModel::isLoaded()) {
                        MessageBox(NULL, "You must load a Triangulation first!", "Error", MB_OK | MB_ICONINFORMATION);
                        break;
                     }
                     
                     UpdateConsole(hwnd, "Running flow....\r\n");
                     TriangulationModel::clearData();
                     map<int, Edge>::iterator eit;
                     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
                     {
                         eit->second.setEta(1.0);
                     }
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_FLOWDIALOG), hwnd, FlowDlgProc);
                     switch(ret)
                     {
                        case IDWEIGHTSRANDOM:
                        {
                            srand(time(NULL));
                            int vertexSize = Triangulation::vertexTable.size();
                            vector<double> weightsVec;
                            for(int i = 0; i < vertexSize; i++)
                            {
                               weightsVec.push_back(rand() % 10 + 1);
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_EUCLIDEAN);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSFILE:
                        {
                            UpdateConsole(hwnd, "Choose a file containing weights information.\r\n");
                            char filename[MAX_PATH] ="";
                            FileChooser((HWND)hwnd, (LPSTR) filename);
                            ifstream infile(filename);
                            vector<double> weightsVec;
                            while(infile.good())
                            {
                               double weight;
                               infile >> weight;
                               weightsVec.push_back(weight);
                            }
                            if(weightsVec.size() != Triangulation::vertexTable.size()) 
                            {
                               MessageBox(NULL, "Improper file for weights", "Error", MB_OK | MB_ICONINFORMATION);
                               break;
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_EUCLIDEAN);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSMAN:
                        {
                             int ret = DialogBox(GetModuleHandle(NULL),
                               MAKEINTRESOURCE(IDD_RADII), hwnd, RadiiDlgProc);
                             if(ret == IDCANCEL) {
                               UpdateConsole(hwnd, "Flow canceled.\r\n");
                             } else if(ret == IDOK) {
                               TriangulationModel::runCalcFlow(ID_RUN_FLOW_EUCLIDEAN);
                               UpdateConsole(hwnd, "Flow complete.\r\n");
                             }
                        }
                        break;
                        case IDCANCEL:
                        {
                            UpdateConsole(hwnd, "Flow canceled.\r\n"); 
                        }
                        break;
                        default: break;
                     }
                }
                break;
                
                case ID_RUN_FLOW_SPHERICAL:
                {
                     if(!TriangulationModel::isLoaded()) {
                        MessageBox(NULL, "You must load a Triangulation first!", "Error", MB_OK | MB_ICONINFORMATION);
                        break;
                     }
                     UpdateConsole(hwnd, "Running flow....\r\n");
                     TriangulationModel::clearData();       
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_FLOWDIALOG), hwnd, FlowDlgProc);
                     switch(ret)
                     {
                        case IDWEIGHTSRANDOM:
                        {
                            srand(time(NULL));
                            int vertexSize = Triangulation::vertexTable.size();
                            vector<double> weightsVec;
                            for(int i = 0; i < vertexSize; i++)
                            {
                               weightsVec.push_back((rand() % 100 + 1) / 125.0);
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_SPHERICAL);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSFILE:
                        {
                            SetDlgItemText(hwnd, IDC_CONSOLE, "Choose a file containing weights information");
                            char filename[MAX_PATH] ="";
                            FileChooser((HWND)hwnd, (LPSTR) filename);
                            ifstream infile(filename);
                            vector<double> weightsVec;
                            while(infile.good())
                            {
                               double weight;
                               infile >> weight;
                               weightsVec.push_back(weight);
                            }
                            if(weightsVec.size() != Triangulation::vertexTable.size()) 
                            {
                               MessageBox(NULL, "Improper file for weights", "Error", MB_OK | MB_ICONINFORMATION);
                               break;
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_SPHERICAL);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSMAN:
                        {
                             int ret = DialogBox(GetModuleHandle(NULL),
                               MAKEINTRESOURCE(IDD_RADII), hwnd, RadiiDlgProc);
                             if(ret == IDCANCEL) {
                               UpdateConsole(hwnd, "Flow canceled.\r\n");
                             } else if(ret == IDOK) {
                               TriangulationModel::runCalcFlow(ID_RUN_FLOW_SPHERICAL);
                               UpdateConsole(hwnd, "Flow complete.\r\n");
                             }
                        }
                        break;
                        case IDCANCEL:
                        {
                            UpdateConsole(hwnd, "Flow canceled.\r\n"); 
                        }
                        break;
                        default: break;
                     }
                }
                break;
                
                case ID_RUN_FLOW_HYPERBOLIC:
                {
                     if(!TriangulationModel::isLoaded()) {
                        MessageBox(NULL, "You must load a Triangulation first!", "Error", MB_OK | MB_ICONINFORMATION);
                        break;
                     }
                     
                     UpdateConsole(hwnd, "Running flow....\r\n");
                     TriangulationModel::clearData();       
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_FLOWDIALOG), hwnd, FlowDlgProc);
                     switch(ret)
                     {
                        case IDWEIGHTSRANDOM:
                        {
                            srand(time(NULL));
                            int vertexSize = Triangulation::vertexTable.size();
                            vector<double> weightsVec;
                            for(int i = 0; i < vertexSize; i++)
                            {
                               weightsVec.push_back((rand() % 100 + 1) / 125.0);
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_HYPERBOLIC);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSFILE:
                        {
                            SetDlgItemText(hwnd, IDC_CONSOLE, "Choose a file containing weights information");
                            char filename[MAX_PATH] ="";
                            FileChooser((HWND)hwnd, (LPSTR) filename);
                            ifstream infile(filename);
                            vector<double> weightsVec;
                            while(infile.good())
                            {
                               double weight;
                               infile >> weight;
                               weightsVec.push_back(weight);
                            }
                            if(weightsVec.size() != Triangulation::vertexTable.size()) 
                            {
                               MessageBox(NULL, "Improper file for weights", "Error", MB_OK | MB_ICONINFORMATION);
                               break;
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_HYPERBOLIC);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSMAN:
                        {
                             int ret = DialogBox(GetModuleHandle(NULL),
                               MAKEINTRESOURCE(IDD_RADII), hwnd, RadiiDlgProc);
                             if(ret == IDCANCEL) {
                               UpdateConsole(hwnd, "Flow canceled.\r\n");
                             } else if(ret == IDOK) {
                               TriangulationModel::runCalcFlow(ID_RUN_FLOW_HYPERBOLIC);
                               UpdateConsole(hwnd, "Flow complete.\r\n");
                             }
                        }
                        break;
                        case IDCANCEL:
                        {
                            UpdateConsole(hwnd, "Flow canceled.\r\n"); 
                        }
                        break;
                        default: break;
                     }
                }
                break;
                case ID_RUN_FLOW_YAMABE:
                {
                     if(!TriangulationModel::isLoaded()) {
                        MessageBox(NULL, "You must load a Triangulation first!", "Error", MB_OK | MB_ICONINFORMATION);
                        break;
                     }
                     
                     UpdateConsole(hwnd, "Running flow, this may take a few moments...\r\n");
                     TriangulationModel::clearData();
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_FLOWDIALOG), hwnd, FlowDlgProc);
                     switch(ret)
                     {
                        case IDWEIGHTSRANDOM:
                        {
                            srand(time(NULL));
                            int vertexSize = Triangulation::vertexTable.size();
                            vector<double> weightsVec;
                            for(int i = 1; i <= vertexSize; i++)
                            {
                               weightsVec.push_back(1.5 + (rand() % 100) / 100.0);
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_YAMABE);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSFILE:
                        {
                            UpdateConsole(hwnd, "Choose a file containing weights information.\r\n");
                            char filename[MAX_PATH] ="";
                            FileChooser((HWND)hwnd, (LPSTR) filename);
                            ifstream infile(filename);
                            vector<double> weightsVec;
                            while(infile.good())
                            {
                               double weight;
                               infile >> weight;
                               weightsVec.push_back(weight);
                            }
                            if(weightsVec.size() != Triangulation::vertexTable.size()) 
                            {
                               cout << weightsVec.size() << "\n";
                               if(Triangulation::vertexTable.size() != 16) {
                                  MessageBox(NULL, "Improper file for weights*", "Error", MB_OK | MB_ICONINFORMATION);
                               } else {
                                  MessageBox(NULL, "Improper file for weights", "Error", MB_OK | MB_ICONINFORMATION);
                               }
                               break;
                            }
                            TriangulationModel::setWeights(&weightsVec);
                            TriangulationModel::runCalcFlow(ID_RUN_FLOW_YAMABE);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
                        }
                        break;
                        case IDWEIGHTSMAN:
                        {
                             int ret = DialogBox(GetModuleHandle(NULL),
                               MAKEINTRESOURCE(IDD_RADII), hwnd, RadiiDlgProc);
                             if(ret == IDCANCEL) {
                               UpdateConsole(hwnd, "Flow canceled.\r\n");
                             } else if(ret == IDOK) {
                               TriangulationModel::runCalcFlow(ID_RUN_FLOW_YAMABE);
                               UpdateConsole(hwnd, "Flow complete.\r\n");
                             }
                        }
                        break;
                        case IDCANCEL:
                        {
                            UpdateConsole(hwnd, "Flow canceled.\r\n"); 
                        }
                        break;
                        default: break;
                     }
                }
                break;                       
                case IDC_RESULTSBUTTON:
                {
                     char format[60];
                     GetDlgItemText(hwnd, IDC_RESULTSSELECTBOX, format, 60);
                     HWND hedit = NULL;
                     hedit = GetDlgItem(hwnd, IDC_RESULTSFIELD);
                     if(hedit == NULL)
                        MessageBox(NULL, "Edit box is null", "Error", MB_OK | MB_ICONINFORMATION);
                     switch(format[9])
                     {
                        case 's':
                        {
                             TriangulationModel::printResults(IDPRINTSTEP);
                             LoadTextFile(hedit, "C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt");
                        }
                        break;
                        case 'v':
                        {
                             TriangulationModel::printResults(IDPRINTVERTEX);
                             LoadTextFile(hedit, "C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt");
                        }
                        break;
                        case 'n':
                        {
                             TriangulationModel::printResults(IDPRINTNUM);
                             LoadTextFile(hedit, "C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt");
                        }
                        break;
                        case 'l':
                        {
                             UpdateConsole(hwnd, "Polygon flow...\r\n");
                             TriangulationModel::printResults(IDPRINTNUMSTEP);
                             DialogBox(GetModuleHandle(NULL),
                                MAKEINTRESOURCE(IDD_POLYGON), hwnd, PolygonProc);

                              UpdateConsole(hwnd, "Polygon flow complete.\r\n");
                        }
                        break;
                        default:
                             MessageBox(NULL, "Choose a print type", "Error", MB_OK | MB_ICONINFORMATION);   
                     }
                }
			}
		break;
		
             
		case WM_CLOSE: {
			EndDialog(hwnd, 0);
        }
		break;
		default:
			return FALSE;
	}
	return TRUE;
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance,
	LPSTR lpCmdLine, int nCmdShow)
{ 
    hInst = hInstance;
	return DialogBox(hInstance, MAKEINTRESOURCE(IDD_MAIN), NULL, DlgProc);
}

VOID EnableOpenGL( HWND hWnd, HDC * hDC, HGLRC * hRC )
{
  PIXELFORMATDESCRIPTOR pfd;
  int iFormat;

  // get the device context (DC)
  *hDC = GetDC( hWnd );

  // set the pixel format for the DC
  ZeroMemory( &pfd, sizeof( pfd ) );
  pfd.nSize = sizeof( pfd );
  pfd.nVersion = 1;
  pfd.dwFlags = PFD_DRAW_TO_WINDOW | 
  PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER;
  pfd.iPixelType = PFD_TYPE_RGBA;
  pfd.cColorBits = 24;
  pfd.cDepthBits = 16;
  pfd.iLayerType = PFD_MAIN_PLANE;
  iFormat = ChoosePixelFormat( *hDC, &pfd );
  SetPixelFormat( *hDC, iFormat, &pfd );

  // create and enable the render context (RC)
  *hRC = wglCreateContext( *hDC );
  wglMakeCurrent( *hDC, *hRC );
}

// Disable OpenGL

VOID DisableOpenGL( HWND hWnd, HDC hDC, HGLRC hRC )
{
  wglMakeCurrent( NULL, NULL );
  wglDeleteContext( hRC );
  ReleaseDC( hWnd, hDC );
} 
