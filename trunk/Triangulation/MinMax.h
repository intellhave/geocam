#include "triangulation.h"

double FE(double deltaEta, int index);
double FR(double deltaRadius, int index);
double F();
void updateEtas(map<int, double>* deltaFE, double b);
void updateRadii(map<int, double>* deltaFR, double b);
bool allNegative(map<int, double>* deltaFE);
bool allPositive(map<int, double>* deltaFR);
void calcDeltaFE(map<int, double>* deltaFE, double deltaEta);
void calcDeltaFR(map<int, double>* deltaFR, double deltaRadius);
void MinMax(double deltaRadius, double a, double deltaEta, double b);
void MinMax(double deltaEta, double b);
