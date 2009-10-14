#include "triangulation/triangulation.h"

void getRadii(double*);
void setRadii(double*);
double FE(double deltaEta, int index);
double FR(double deltaRadius, int index);
double F();
void updateEtas(map<int, double>* deltaFE, double b, double a);
void updateRadii(map<int, double>* deltaFR, double b);
void calcDeltaFE(map<int, double>* deltaFE, double deltaEta);
void calcDeltaFR(map<int, double>* deltaFR, double deltaRadius);
void MinMax(double deltaRadius, double a, double deltaEta, double b);
void MinMax(double deltaEta, double b, double a);
