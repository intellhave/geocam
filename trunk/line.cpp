#include "line.h"
#include<cmath>

Point::Point(double xi, double yi)
{
   x = xi;
   y = yi;
}

Point::~Point()
{
}

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
Line::Line(Point init, Point end)
{
    x1 = init.x;
    y1 = init.y;
    x2 = end.x;
    y2 = end.y;
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

bool Line::isBelow(Point p)
{
     return isBelow(p.x, p.y);
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

bool Line::isOnLine(Point p)
{
     return isOnLine(p.x, p.y);
}

