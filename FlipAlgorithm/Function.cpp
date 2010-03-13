#include "Function.h"

void Function::init(char* fileName, char* nameSpecifier, vector<int> indexList, bool append) {
    appendMoreValues = append;
    file = fileName;
    varName = nameSpecifier;
    ifstream scanner(fileName);
    map<int, bool> wasPutIn;

    char buff[strlen(nameSpecifier) + 2];
    while(scanner.good()) {

        int key;
        double value;
        scanner.getline(buff, strlen(nameSpecifier) + 2, ':');
        if (strcmp(buff, nameSpecifier) == 0) { //we are on a line that begins with our nameSpecifier
            scanner >> key;
            scanner >> value;
            func[key] = value;
            wasPutIn[key] = true;
        }
        scanner.ignore(100, '\n');
    }
    scanner.close();

    ofstream output(fileName, ios::app);
    vector<int>::iterator indexIter;

    srand(time(NULL));
    for (indexIter = indexList.begin(); indexIter != indexList.end(); indexIter++) {
        if (wasPutIn[*indexIter] == false) { //func value does not exist for the current Index
            if(appendMoreValues) {
                double randNum = ((double) rand()/RAND_MAX);
                output << nameSpecifier << ": " << *indexIter << " " << randNum << "\n";
                func[*indexIter] = randNum;
            } else {
                cout << "the file you specified " << *fileName << " did not have sufficient values labeled " << *nameSpecifier;
                system("PAUSE");
                exit(1);
            }
        }
    }
    output.close();
}

Function::Function(char* fileName, char* nameSpecifier, vector<int> indexList) {
    init(fileName, nameSpecifier, indexList, true);
}


Function::Function(char* fileName, char* nameSpecifier, vector<int> indexList, bool append) {
    init(fileName, nameSpecifier, indexList, append);
}

Function::Function(char* fileName, char* nameSpecifier, map<int, Vertex> simplexList, bool append) {
    map<int, Vertex>::iterator sit;
    sit = simplexList.begin();
    vector<int> v;
    for (; sit != simplexList.end(); sit++) {
        v.push_back(sit->first);
    }
    init(fileName, nameSpecifier, v, append);
}

Function::Function(char* fileName, char* nameSpecifier, map<int, Edge> simplexList, bool append) {
    map<int, Edge>::iterator sit;
    sit = simplexList.begin();
    vector<int> v;
    for (; sit != simplexList.end(); sit++) {
        v.push_back(sit->first);
    }
    init(fileName, nameSpecifier, v, append);
}

Function::Function(char* fileName, char* nameSpecifier, map<int, Face> simplexList, bool append) {
    map<int, Face>::iterator sit;
    sit = simplexList.begin();
    vector<int> v;
    for (; sit != simplexList.end(); sit++) {
        v.push_back(sit->first);
    }
    init(fileName, nameSpecifier, v, append);
}

Function::Function(char* fileName, char* nameSpecifier, map<int, Tetra> simplexList, bool append) {
    map<int, Tetra>::iterator sit;
    sit = simplexList.begin();
    vector<int> v;
    for (; sit != simplexList.end(); sit++) {
        v.push_back(sit->first);
    }
    init(fileName, nameSpecifier, v, append);
}

Function::~Function(void) { }

double Function::valueOf(int index) {
    if (func.count(index) > 0) {
        return func[index];
    } else {
        return 0;
    }
}

double Function::valueOf(Simplex s) {
    int index = s.getIndex();
    return valueOf(index);
}
