#ifndef Function_flag
#define Function_flag

#include "triangulation.h"
#include <string.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <cmath>

class Function {

    map<int, double> func;
    char* varName;
    char* file;
    bool appendMoreValues;
    void init(char* fileName, char* nameSpecifier, vector<int> indexList, bool append);

    public:
        Function(char* fileName, char* nameSpecifier, vector<int> indexList);
        Function(char* fileName, char* nameSpecifier, vector<int> indexList, bool append);
        Function(char* fileName, char* nameSpecifier, map<int, Vertex> simplexList, bool append);
        Function(char* fileName, char* nameSpecifier, map<int, Edge> simplexList, bool append);
        Function(char* fileName, char* nameSpecifier, map<int, Face> simplexList, bool append);
        Function(char* fileName, char* nameSpecifier, map<int, Tetra> simplexList, bool append);
        
        ~Function(void);
        
        double valueOf(int index);
        double valueOf(Simplex s);
};

#endif
