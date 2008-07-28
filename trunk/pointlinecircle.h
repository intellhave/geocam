/**************************************************************
Class: Point, Line, and Circle
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
This file holds the information for the three classes Point,
Line, and Circle. These classes can be used independently of a
triangulation, but are useful for providing coordinates to such
a system. Currently implemented as a two-dimensional model.
**************************************************************/
#include <vector>


#ifndef POINT_H
#define POINT_H
/*
 * The Point class is simply a container for a pair of doubles. Its
 * two variables, doubles x and y, are public and so can be accessed
 * by p.x and p.y for a Point p.
 */
class Point
{
      public:
      /*
       * Constructs a Point given an x-coordinate and a y-coordinate.
       */
      Point(double, double);
      /*
       * A default constructor for use by the computer when needed.
       */
      Point();
      ~Point();
      double x, y;
};
#endif // POINT_H


#ifndef LINE_H
#define LINE_H
/*
 * The Line class is composed of an initial point and an ending point
 * and can be treated either as a true line or a line segment. The
 * members of the Line class are not public, but include slope, intercept
 * and whether or not the Line is vertical. A vertical line is to be
 * treated special and does not have a slope or intercept.
 */
class Line
{
      double x1, y1, x2, y2;
      double slope;
      double intercept;
      bool vertical;
      public:
      /*
       * Constructs a Line given four doubles, representing in order:
       *            intialX, initialY, endingX, endingY
       */
      Line(double, double, double, double);
      /*
       * Constructs a Line given an intial Point and an ending Point.
       */
      Line(Point, Point);
      /*
       * A default constructor for use by the computer when needed.
       */
      Line();
      ~Line();
      /*
       * Returns the initial x-coordinate that was used to define the Line.
       */
      double getInitialX();
      /*
       * Returns the initial y-coordinate that was used to define the Line.
       */
      double getInitialY();
      /*
       * Returns the initial Point that was used to define the Line.
       */
      Point getInitial();
      /*
       * Returns the ending x-coordinate that was used to define the Line.
       */
      double getEndingX();
      /*
       * Returns the ending y-coordinate that was used to define the Line.
       */
      double getEndingY();
      /*
       * Returns the ending Point that was used to define the Line.
       */
      Point getEnding();
      /*
       * Returns the slope of the Line. If the Line is vertical, the 
       * slope was never calculated.
       */
      double getSlope();
      /*
       * Returns the y-intercept of the Line. If the Line is vertical, 
       * the intercept was never calculated.
       */
      double getIntercept();
      /*
       * Returns the distance between the initial and ending points of
       * the Line.
       */
      double getLength();
      /*
       * Returns true if the Line is a vertical line (the initial and 
       * ending x-coordinates are the same). False otherwise.
       */
      bool isVertical();
      /*
       * Returns true if the Line is above a given point. If a Line is
       * vertical, it is above all points to the left of it. If the point
       * is on the line, this function returns false.
       */
      bool isAbove(double, double);
      /*
       * Returns true if the Line is above a given Point. If a Line is
       * vertical, it is above all points to the left of it. If the Point
       * is on the line, this function returns false.
       */
      bool isAbove(Point);
      /*
       * Returns true if the given point is on the Line. This does not
       * depend on the point being between the intial and ending
       * conditions.
       */
      bool hasPoint(double, double);
      /*
       * Returns true if the given Point is on the Line. This does not
       * depend on the Point being between the intial and ending
       * conditions.
       */
      bool hasPoint(Point);
      /*
       * Returns a Point representing the intersection point between
       * this Line and a Line given. If the slopes are parallel, an
       * error is thrown.
       */
      Point intersection(Line);
      Line getPerpendicular(Point);
};
#endif // LINE_H

#ifndef CIRCLE_H
#define CIRCLE_H
/*
 * The Circle class represents a circle with a given radius and a given
 * center. The Circle class is especially designed for use with
 * circleIntersection(c1, c2) in the miscMath file.
 */
class Circle
{
      Point center;
      double radius;
      public:
      /*
       * Constructs a circle with center given as a Point and a radius.
       */
      Circle(Point, double);
      /*
       * Constructs a circle with center given as two doubles, x- and
       * y-coordinates and a radius.
       */
      Circle(double, double, double);
      ~Circle();
      /*
       * Returns the radius of the Circle.
       */
      double getRadius();
      /*
       * Sets the radius to the given double.
       */
      void setRadius(double);
      /*
       * Returns the center of the circle as a Point.
       */
      Point getCenter();
      /*
       * Sets the center to the given Point.
       */
      void setCenter(Point);
      /*
       * Sets the center to a point given by two doubles.
       */
      void setCenter(double, double);
};
#endif // CIRCLE_H
