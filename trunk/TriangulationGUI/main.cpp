#include <windows.h>
#include <ctime>
#include <fstream>
#include <vector>
#include "resources.h"
#include "TriangulationModel.h"

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
                             LoadTextFile(hedit, "C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt");
                        }
                        break;
                        case 'v':
                        {
                             TriangulationModel::printResults(IDPRINTVERTEX);
                             LoadTextFile(hedit, "C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt");
                        }
                        break;
                        case 'n':
                        {
                             TriangulationModel::printResults(IDPRINTNUM);
                             LoadTextFile(hedit, "C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt");
                        }
                        break;
                        default:
                             MessageBox(NULL, "Choose a print type", "Error", MB_OK | MB_ICONINFORMATION);   
                     }
                }
                /*
				case IDC_ADD:
				{
					// When somebody clicks the Add button, first we get the number of
					// they entered

					BOOL bSuccess;
					int nTimes = GetDlgItemInt(hwnd, IDC_NUMBER, &bSuccess, FALSE);
					if(bSuccess) 
					{
						// Then we get the string they entered
						// First we need to find out how long it is so that we can
						// allocate some memory

						int len = GetWindowTextLength(GetDlgItem(hwnd, IDC_TEXT));
						if(len > 0)
						{
							// Now we allocate, and get the string into our buffer

							int i;
							char* buf;

							buf = (char*)GlobalAlloc(GPTR, len + 1);
							GetDlgItemText(hwnd, IDC_TEXT, buf, len + 1);

							// Now we add the string to the list box however many times
							// the user asked us to.

							for(i = 0;i < nTimes; i++)
							{
								int index = SendDlgItemMessage(hwnd, IDC_LIST, LB_ADDSTRING, 0, (LPARAM)buf);

								// Here we are associating the value nTimes with the item 
								// just for the heck of it, we'll use it to display later.
								// Normally you would put some more useful data here, such
								// as a pointer.
								SendDlgItemMessage(hwnd, IDC_LIST, LB_SETITEMDATA, (WPARAM)index, (LPARAM)nTimes);
							}

							// Dont' forget to free the memory!
							GlobalFree((HANDLE)buf);
						}
						else 
						{
							MessageBox(hwnd, "You didn't enter anything!", "Warning", MB_OK);
						}
					}
					else 
					{
						MessageBox(hwnd, "Couldn't translate that number :(", "Warning", MB_OK);
					}

				}
				break;
				case IDC_REMOVE:
				{
					// When the user clicks the Remove button, we first get the number
					// of selected items

					HWND hList = GetDlgItem(hwnd, IDC_LIST);
					int count = SendMessage(hList, LB_GETSELCOUNT, 0, 0);
					if(count != LB_ERR)
					{
						if(count != 0)
						{
							// And then allocate room to store the list of selected items.

							int i;
							int *buf = (int*)GlobalAlloc(GPTR, sizeof(int) * count);
							SendMessage(hList, LB_GETSELITEMS, (WPARAM)count, (LPARAM)buf);
							
							// Now we loop through the list and remove each item that was
							// selected.  

							// WARNING!!!  
							// We loop backwards, because if we removed items
							// from top to bottom, it would change the indexes of the other
							// items!!!

							for(i = count - 1; i >= 0; i--)
							{
								SendMessage(hList, LB_DELETESTRING, (WPARAM)buf[i], 0);
							}

							GlobalFree(buf);
						}
						else 
						{
							MessageBox(hwnd, "No items selected.", "Warning", MB_OK);
						}
					}
					else
					{
						MessageBox(hwnd, "Error counting items :(", "Warning", MB_OK);
					}
				}
				break;
				case IDC_CLEAR:
					SendDlgItemMessage(hwnd, IDC_LIST, LB_RESETCONTENT, 0, 0);
				break;
				case IDC_LIST:
					switch(HIWORD(wParam))
					{
						case LBN_SELCHANGE:
						{
							// Get the number of items selected.

							HWND hList = GetDlgItem(hwnd, IDC_LIST);
							int count = SendMessage(hList, LB_GETSELCOUNT, 0, 0);
							if(count != LB_ERR)
							{
								// We only want to continue if one and only one item is
								// selected.

								if(count == 1)
								{
									// Since we know ahead of time we're only getting one
									// index, there's no need to allocate an array.

									int index;
									int err = SendMessage(hList, LB_GETSELITEMS, (WPARAM)1, (LPARAM)&index);
									if(err != LB_ERR)
									{
										// Get the data we associated with the item above
										// (the number of times it was added)

										int data = SendMessage(hList, LB_GETITEMDATA, (WPARAM)index, 0);

										SetDlgItemInt(hwnd, IDC_SHOWCOUNT, data, FALSE);
									}
									else 
									{
										MessageBox(hwnd, "Error getting selected item :(", "Warning", MB_OK);
									}
								}
								else 
								{
									// No items selected, or more than one
									// Either way, we aren't going to process this.
									SetDlgItemText(hwnd, IDC_SHOWCOUNT, "-");
								}
							}
							else
							{
								MessageBox(hwnd, "Error counting items :(", "Warning", MB_OK);
							}
						}
						break;
					}
				break;
				*/
			}
		break;
		
             
		case WM_CLOSE:
			EndDialog(hwnd, 0);
		break;
		default:
			return FALSE;
	}
	return TRUE;
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance,
	LPSTR lpCmdLine, int nCmdShow)
{
	return DialogBox(hInstance, MAKEINTRESOURCE(IDD_MAIN), NULL, DlgProc);
}
