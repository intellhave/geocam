#include <vector>


#ifndef POINT_H
#define POINT_H
class Point
{
      public:
      Point(double, double);
      ~Point();
      double x, y;
};
#endif // POINT_H

#ifndef LINE_H
#define LINE_H
class Line
{
      double x1, y1, x2, y2;
      double slope;
      double intercept;
      bool vertical;
      public:
      Line(double, double, double, double);
      Line(Point, Point);
      ~Line();
      double getInitialX();
      double getInitialY();
      double getEndingX();
      double getEndingY();
      double getSlope();
      double getIntercept();
      bool isVertical();
      bool isBelow(double, double);
      bool isBelow(Point);
      bool isOnLine(double, double);
      bool isOnLine(Point);
};
#endif // LINE_H

