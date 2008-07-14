#include "line.h"

Line::Line(double xi1, double yi1, double xi2, double yi2)
{
    x1 = xi1;
    y1 = yi1;
    x2 = xi2;
    y2 = yi2;
    slope = 0;
    intercept = 0;
    vertical = x1 == x2;
    if(!vertical)
    {
      slope = (y2 - y1)/(x2 - x1);
      intercept = y1 - slope * x1;
    }
}

Line::~Line()
{
}

double Line::getInitialX()
{
    return x1;
}
double Line::getInitialY()
{
    return y1;
}
double Line::getEndingX()
{
    return x2;
}
double Line::getEndingY()
{
    return y2;
}

double Line::getSlope()
{
    return slope;
}

double Line::getIntercept()
{
    return intercept;
}

bool Line::isVertical()
{
     return vertical;
}

bool Line::isBelow(double x, double y)
{
     if(vertical)
     {
        return x < x1;
     }
     double yVal = slope * x + intercept;
     return y < yVal;
}

bool Line::isOnLine(double x, double y)
{
      if(vertical)
     {
        return x == x1;
     }
     double yVal = slope * x + intercept;
     return y == yVal;
}
