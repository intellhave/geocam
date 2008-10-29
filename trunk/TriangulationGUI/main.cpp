#include <windows.h>
#include <ctime>
#include <fstream>
#include <cmath>
#include <vector>
#include "resources.h"
#include "TriangulationModel.h"
#define PI 	3.141592653589793238
#include <gl/gl.h>
BOOL CALLBACK PolygonProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam);
VOID EnableOpenGL( HWND hWnd, HDC * hDC, HGLRC * hRC );
VOID DisableOpenGL( HWND hWnd, HDC hDC, HGLRC hRC );
HINSTANCE hInst;
void FileChooser(HWND hwnd, LPSTR szFileName)
{
	OPENFILENAME ofn;
	ZeroMemory(&ofn, sizeof(ofn));

	ofn.lStructSize = sizeof(OPENFILENAME);
	ofn.hwndOwner = hwnd;
	ofn.lpstrFilter = "Text Files (*.txt)\0*.txt\0All Files (*.*)\0*.*\0";
	ofn.lpstrFile = szFileName;
	ofn.nMaxFile = MAX_PATH;
	ofn.Flags = OFN_EXPLORER | OFN_FILEMUSTEXIST | OFN_HIDEREADONLY;
	ofn.lpstrDefExt = "txt";

	GetOpenFileName(&ofn);
}

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
void UpdateConsole(HWND hwnd, LPSTR message)
{
    SetDlgItemText(hwnd, IDC_HIDDENTEXT, message);
    int len = GetWindowTextLength(GetDlgItem(hwnd, IDC_CONSOLE));
    int len2 = GetWindowTextLength(GetDlgItem(hwnd, IDC_HIDDENTEXT));
    if(len > 0)
    {
        char* buf;
        char* buf2;
        buf = (char*)GlobalAlloc(GPTR, len + len2 + 1);
        buf2 = (char*)GlobalAlloc(GPTR, len2 + 1);
        GetDlgItemText(hwnd, IDC_CONSOLE, buf, len + 1);
        GetDlgItemText(hwnd, IDC_HIDDENTEXT, buf2, len2 + 1);
        for(int i = 0; i< len2 + 1; i++)
        {
            buf[len + i] = buf2[i];
        }
        int newlines = 0;;
        vector<int> newlinePos;
        for(int i = 0; i < len + len2 + 1 ; i++)
        {
           if(buf[i] == '\n')
           {
              newlines++;
              newlinePos.push_back(i);
           }
        }
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
            SetDlgItemText(hwnd, IDC_CONSOLE, buf3);
        } else
        {
          SetDlgItemText(hwnd, IDC_CONSOLE, buf);
        }
        GlobalFree((HANDLE)buf);
        GlobalFree((HANDLE)buf2);
    }
}
BOOL CALLBACK PolygonProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam) 
{
     switch(Message)
     {
        case WM_INITDIALOG:
        {

        }
        break;
        case WM_COMMAND:
            switch(LOWORD(wParam))
            {
                case IDPOLYGON: 
                {
                   HWND hPoly = GetDlgItem(hwnd, IDC_POLYGON);
                   HDC hDC;
                   HGLRC hRC;
                   ifstream scanner("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt");
                   
                   EnableOpenGL( hPoly, &hDC, &hRC );
                   SetDlgItemText(hwnd, IDC_POLYGON_STEP, "0000");
                   // program main loop
                   char step[] = {'0', '0', '0', '1', '\0'};
                   while (scanner.good()) 
                   {
                      
                      SwapBuffers( hDC );
                      int size = Triangulation::vertexTable.size();
                      double curv[size];
                      for(int i = 0; i < size; i++)
                      {
                          scanner >> curv[i];
                      }
                      glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
                      glClear(GL_COLOR_BUFFER_BIT); 
                      glBegin(GL_LINE_LOOP);
                      glColor3f( 0.0f, 0.0f, 0.0f ); 
                      for(int i = 0; i < size; i++)
                      {
                          double angle = 2 * PI / size * i;
                          glVertex2f((curv[i]) / 12 * cos(angle), (curv[i]) / 12 * sin(angle));              
                      }
                      glEnd();
                      SetDlgItemText(hwnd, IDC_POLYGON_STEP, step);
                      if(step[3] == '9') {
                         step[3] = '0';
                         if(step[2] == '9') {
                            step[2] = '0';
                            if(step[1] == '9') {
                               step[1] = '0';
                               if(step[0] == '9') {
                                  step[0] = '0';
                               } else {
                                  step[0]++;
                               }
                            } else {
                               step[1]++;
                            }
                         } else {
                            step[2]++;
                         }
                      } else {
                        step[3]++;
                      }
                      Sleep(100);
                    }
                    SetDlgItemText(hwnd, IDC_POLYGON_STEP, "----");
                    DisableOpenGL( hPoly, hDC, hRC );
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
           SendMessage(hcombo, CB_ADDSTRING, 0, (LPARAM)"Random");
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
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_FLOWDIALOG), hwnd, FlowDlgProc);
                     switch(ret)
                     {
                        case IDWEIGHTSRANDOM:
                        {
                            srand(time(NULL));
                            int vertexSize = Triangulation::vertexTable.size();
                            double weights[vertexSize];
                            for(int i = 1; i <= vertexSize; i++)
                            {
                               weights[i - 1] =   (rand() % 10 + 1);
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_EUCLIDEAN);
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
                            double weights[weightsVec.size()];
                            for(int i = 0; i < weightsVec.size(); i++)
                            {
                               weights[i] = weightsVec[i];
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_EUCLIDEAN);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
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
                            double weights[vertexSize];
                            for(int i = 1; i <= vertexSize; i++)
                            {
                               weights[i - 1] =   (rand() % 100 + 1)/ 125.0;
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_SPHERICAL);
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
                            double weights[weightsVec.size()];
                            for(int i = 0; i < weightsVec.size(); i++)
                            {
                               weights[i] = weightsVec[i];
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_SPHERICAL);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
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
                            double weights[vertexSize];
                            for(int i = 1; i <= vertexSize; i++)
                            {
                               weights[i - 1] =   (rand() % 100 + 1)/ 125.0;
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_HYPERBOLIC);
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
                            double weights[weightsVec.size()];
                            for(int i = 0; i < weightsVec.size(); i++)
                            {
                               weights[i] = weightsVec[i];
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_HYPERBOLIC);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
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
                            double weights[vertexSize];
                            for(int i = 1; i <= vertexSize; i++)
                            {
                               weights[i - 1] =   (rand() % 5 + 1);
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_YAMABE);
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
                            double weights[weightsVec.size()];
                            for(int i = 0; i < weightsVec.size(); i++)
                            {
                               weights[i] = weightsVec[i];
                            }
                            TriangulationModel::runCalcFlow(weights, ID_RUN_FLOW_YAMABE);
                            UpdateConsole(hwnd, "Flow complete.\r\n");
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
//                             Sleep(200);
//                             HWND hHiddenPoly = GetDlgItem(hwnd, IDC_POLYGON);
//                             //ShowWindow(hwnd, SW_HIDE);
//                             ShowWindow(hHiddenPoly, SW_SHOW);
//                             //HWND hedit = NULL;
//                             //hedit = GetDlgItem(hwnd, IDC_RESULTSFIELD);
//                             HDC hDC;
//                             HGLRC hRC;
//                             ifstream scanner("C:/Dev-Cpp/geocam/Triangulation Files/ODE Result.txt");
//                             //EnableOpenGL( hedit, &hDC, &hRC );
//                             EnableOpenGL( hHiddenPoly, &hDC, &hRC );
////                             glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
////                             glClear(GL_COLOR_BUFFER_BIT);
////                             glBegin(GL_LINE_LOOP);
////                             glColor3f( 0.0f, 0.0f, 0.0f );
////                             glVertex2f(0.5, 0.5);
////                             glVertex2f(0.5, -0.5);
////                             glVertex2f(-0.5, -0.5);
////                             glVertex2f(-0.5, 0.5);
////                             glEnd();
////                             SwapBuffers( hDC );
//
//                             // program main loop
//                             while (scanner.good()) 
//                             {
//     
//                                 int size = Triangulation::vertexTable.size();
//                                 double curv[size];
//                                 for(int i = 0; i < size; i++)
//                                 {
//                                     scanner >> curv[i];
//                                 }
//                                 glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
//                                 glClear(GL_COLOR_BUFFER_BIT); 
//                                 glBegin(GL_LINE_LOOP);
//                                 glColor3f( 0.0f, 0.0f, 0.0f ); 
//                                 for(int i = 0; i < size; i++)
//                                 {
//                                    double angle = 2 * PI / size * i;
//                                    glVertex2f( /*log(abs*/(curv[i])/*)*/ / 4 * cos(angle), /*log(abs*/(curv[i])/*)*/ / 4 * sin(angle));              
//                                 }
//                                 glEnd();
//            
//                                 SwapBuffers( hDC );
//                                 Sleep(100);
//                              } 
//                              SwapBuffers( hDC );
//                              //DisableOpenGL( hedit, hDC, hRC );
//                              DisableOpenGL( hHiddenPoly, hDC, hRC );
//                              //ShowWindow(hwnd, SW_SHOW);
//                              ShowWindow(hHiddenPoly, SW_HIDE);
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
