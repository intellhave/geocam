class Line
{
      double x1, y1, x2, y2;
      double slope;
      double intercept;
      bool vertical;
      public:
      Line(double, double, double, double);
      ~Line();
      double getInitialX();
      double getInitialY();
      double getEndingX();
      double getEndingY();
      double getSlope();
      double getIntercept();
      bool isVertical();
      bool isBelow(double, double);
      bool isOnLine(double, double);
};
