/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/
#include "miscmath.h"
#include <iostream>
#include<cmath>


vector<double> quadratic(double a, double b, double c)
{
   /*                       _________
                     +   _ | 2
               -b    -    \|b  - 4*a*c
         x =  -------------------------
                         2*a
   */ 
   vector<double> solutions;
   double inside = b*b - 4*a*c; // The value inside the square root
   if(inside < 0)
   {
     // No real solutions
     return solutions;
   }
   if(inside == 0)
   {
     // Only one solution
     double sol = b * (-1) / (2*a);
     solutions.push_back(sol);
     return solutions;
   }
   double sol1 = ((-1)*b + sqrt(inside)) / (2*a);
   double sol2 = ((-1)*b - sqrt(inside)) / (2*a);
   solutions.push_back(sol1);
   solutions.push_back(sol2);
   return solutions;
}

double distancePoint(Point a, Point b)
{
   /*         ____________________________             
           _ |           2              2    
     d =    \|(a_x - b_x)  + (a_y - b_y)
   */
   return sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
}

vector<Point> circleIntersection(Point center1, double r1, Point center2, double r2)
{
    vector<Point> solutions;
    if(distancePoint(center1 , center2) > (r1 + r2))
    {
        cout << "No solutions\n";
        // No solutions
        return solutions;
    }
    if(center1.x == center2.x && center1.y == center2.y)
    {
       if(r1 == r2)
       {     cout << "Infinite solutions\n";
             // Same circle, infinite options, return same point.
             solutions.push_back(center1);
             return solutions;
       }
       else{
       // No solutions
       cout << "No solutions2\n";
          return solutions;
       }
    }
    if(distancePoint(center1, center2) < fabs(r1 - r2))
    {
       cout << "No solutions3\n";
       return solutions;
    }
    // (x-x1)^2 + (y-y1)^2 = r1^2
    // (x-x2)^2 + (y-y2)^2 = r2^2
    
    /* x^2 - 2*x1*x + x1^2 + y^2 - 2*y1*y + y1^2 = r1^2
       -
       x^2 - 2*x2*x + x2^2 + y^2 - 2*y2*y + y2^2 = r2^2
       ------------------------------------------------
       (2*x2 - 2*x1)x +(2*y2 - 2*y2)y = r1^2 - x1^2 - y1^2 - (r2^2 - x2^2 - y2^2)
       
       xComp*x + yComp*y = rComp
    */
    double xComp = 2 * center2.x - 2 * center1.x;
    double yComp = 2 * center2.y - 2 * center1.y;
    double r1Comp = r1*r1 - center1.x * center1.x - center1.y * center1.y;
    double r2Comp = r2*r2 - center2.x * center2.x - center2.y * center2.y;
    double rComp = r1Comp - r2Comp;
    
    if(yComp == 0)
    {
       /* x = rComp / xComp
                         _________________
          y = y1  +/-  \|r1^2 - (x - x1)^2  
       */    
       double ySol1 = sqrt(r1*r1 - pow((rComp/xComp - center1.x), 2)) + center1.y;
       double ySol2 = (-1)*sqrt(r1*r1 - pow((rComp/xComp - center1.x),2)) + center1.y;
       Point sol1(rComp/xComp, ySol1);
       Point sol2(rComp/xComp, ySol2);
       solutions.push_back(sol1);
       solutions.push_back(sol2);
       return solutions;
    }
    if(xComp == 0)
    {
        /* y = rComp / yComp
                          _________________
           x = x1  +/-  \|r1^2 - (y - y1)^2  
        */
       double xSol1 = sqrt(r1*r1 - pow((rComp/yComp - center1.y), 2)) + center1.x;
       double xSol2 = (-1)*sqrt(r1*r1 - pow((rComp/yComp - center1.y),2)) + center1.x;
       Point sol1(xSol1, rComp/yComp);
       Point sol2(xSol2, rComp/yComp);
       solutions.push_back(sol1);
       solutions.push_back(sol2);
       return solutions;
    }
    
    /*
        y = (rComp - xComp*x) / yComp
        
     => x^2 - 2*x1*x + x1^2 + ((rComp - xComp*x) / yComp - y1)^2 - r1^2 = 0
        
     => (1 + xComp/yComp)^2 * x^2 - 2*[x1 - (rComp/yComp - y1)*xComp/yComp] * x
           + x1^2 + (rComp/yComp - y1)^2 - r1^2 = 0
    */
    double b = (-2)*center1.x - 2*(rComp/yComp - center1.y)*(xComp/yComp);

    double a = 1 + pow((xComp/yComp), 2);

    double c = center1.x*center1.x + pow((rComp/yComp - center1.y), 2) - r1*r1;

    // Find x solutions using quadratic formula.
    vector<double> quadSol = quadratic(a, b, c);
    for(int i = 0; i < quadSol.size(); i++)
    {
        
        double xSol = quadSol[i];
        double ySol = rComp/yComp - xComp/yComp*xSol;

        Point sol(xSol, ySol);
        solutions.push_back(sol);
    }
    return solutions;
}

vector<Point> circleIntersection(Circle circle1, Circle circle2)
{
    Point center1 = circle1.getCenter();
    Point center2 = circle2.getCenter();
    double r1 = circle1.getRadius();
    double r2 = circle2.getRadius();              
    return circleIntersection(center1, r1, center2, r2);
}

void printPoint(Point p)
{
     cout << "Point: (" << p.x << ", " << p.y << ") ";
}

Point rotateVector(Point vector, double angle)
{
      double x = vector.x*cos(angle) - vector.y*sin(angle);
      double y = vector.x*sin(angle) + vector.y*cos(angle);
      if(x < 0.00000001 && x > -0.00000001) // Error checking
           x = 0;
      if(y < 0.00000001 && y > -0.00000001) // Error checking
           y = 0;
      Point p(x,y);
      return p;
}

Point findPoint(Line l, double length, double angle)
{
      Point vect(l.getEndingX()-l.getInitialX(), l.getEndingY() - l.getInitialY());
      Point rotate = rotateVector(vect, angle);
      Point vectResult(rotate.x/l.getLength()*length,
                       rotate.y/l.getLength()*length);
      Point p(l.getInitialX() + vectResult.x, l.getInitialY() + vectResult.y);
      return p;
}
