#include <windows.h>
#include <ctime>
#include "triangulation.h"
#include "triangulationInputOutput.h"
#include "triangulationmath.h"
#include "resources.h" 

void FileChooser(HWND hwnd, char* szFileName)
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
           return TRUE;
        }
        case WM_COMMAND:
            switch(LOWORD(wParam))
            {
                case IDOK: 
                {
                    char format[25];
                    GetDlgItemText(hwnd, IDC_WEIGHTSELECTBOX, format, 25);
                    if(format[0] == 'R')
                       EndDialog(hwnd, IDSTANDARD);
                    else if(format[0] == 'F')
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
BOOL CALLBACK DlgProc(HWND hwnd, UINT Message, WPARAM wParam, LPARAM lParam)
{
	switch(Message)
	{
        
		case WM_INITDIALOG:
			// This is where we set up the dialog box, and initialise any default values

			SetDlgItemText(hwnd, IDC_CONSOLE, "Welcome to the Triangulation Program");
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
                     FileChooser((HWND)hwnd, filename);
                     SetDlgItemText(hwnd, IDC_CONSOLE, "Loading file...");
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_CHOOSEFILETYPE), hwnd, FormatDlgProc);
                     if(ret == IDSTANDARD)
                     {
                       Triangulation::resetTriangulation();
                       readTriangulationFile(filename);
                       SetDlgItemText(hwnd, IDC_CONSOLE, "Loading file...File loaded.");   
                     } else if(ret == IDLUTZ)
                     {
                        Triangulation::resetTriangulation();
                        makeTriangulationFile(filename,"Triangulations/manifold converted.txt");
                        readTriangulationFile("Triangulations/manifold converted.txt");   
                        SetDlgItemText(hwnd, IDC_CONSOLE, "Loading file...File loaded.");   
                     } else if(ret == IDCANCEL)
                     {
                         SetDlgItemText(hwnd, IDC_CONSOLE, "Loading file...Load canceled.");   
                     }
                }
                break;
                case ID_RUN_FLOW_EUCLIDEAN:
                {
                     int ret = DialogBox(GetModuleHandle(NULL),
                        MAKEINTRESOURCE(IDD_FLOWDIALOG), hwnd, FlowDlgProc); 
                     srand(time(NULL));
                     int vertexSize = Triangulation::vertexTable.size();
                     double weights[vertexSize];
                     for(int i = 1; i <= vertexSize; i++)
                     {
                          weights[i - 1] =   (rand() % 80 + 1)/100.0;
                     }
                     vector<double> weightsR;
                     vector<double> curvatures;
                     double dt = 0.03;
                     int Steps = 1000;
                     SetDlgItemText(hwnd, IDC_CONSOLE, "Running flow....");
                     calcFlow(&weightsR, &curvatures, dt, weights, Steps, true);
                     printResultsStep("C:/Dev-Cpp/geocam/TriangulationGUI/Triangulations/ODE Result.txt", &weightsR, &curvatures);
                     SetDlgItemText(hwnd, IDC_CONSOLE, "Flow complete");

                     
                }
                break;
                case IDC_RESULTSBUTTON:
                {
                     char filename[MAX_PATH] ="";
                     FileChooser((HWND)hwnd, filename);
                     HWND hedit = NULL;
                     hedit = GetDlgItem(hwnd, IDC_RESULTSFIELD);
                     if(hedit == NULL)
                     {
                        SetDlgItemText(hwnd, IDC_CONSOLE, "NULL!");      
                     }
                     bool success = LoadTextFile(hedit, filename);
                     if(success)
                        SetDlgItemText(hwnd, IDC_CONSOLE, "Success!");
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
