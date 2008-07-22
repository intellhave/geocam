#include <vector>


#ifndef POINT_H
#define POINT_H
class Point
{
      public:
      Point(double, double);
      Point();
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
      Line();
      ~Line();
      double getInitialX();
      double getInitialY();
      Point getInitial();
      Point getEnding();
      double getEndingX();
      double getEndingY();
      double getSlope();
      double getIntercept();
      bool isVertical();
      bool isAbove(double, double);
      bool isAbove(Point);
      bool hasPoint(double, double);
      bool hasPoint(Point);
      double getLength();
      Point intersection(Line);
      Line getPerpendicular(Point);
};
#endif // LINE_H

#ifndef CIRCLE_H
#define CIRCLE_H
class Circle
{
      Point center;
      double radius;
      public:
      Circle(Point, double);
      Circle(double, double, double);
      ~Circle();
      double getRadius();
      void setRadius(double);
      Point getCenter();
      void setCenter(Point);
      void setCenter(double, double);
};
#endif // CIRCLE_H
