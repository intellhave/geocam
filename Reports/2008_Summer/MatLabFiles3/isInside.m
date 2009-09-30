%This function determines if a given point (x,y) is inside a triangle
%defined by its vertex coordinates. The user inputs the data point in
%question, followed by coordinate vectors relating to the vertices on the
%triangle. X and Y may have more elements, but only the first three will be
%retrieved. 

%The result is a boolean, 1 if true, 0 if false. This can be used for other
%programs or run on its own. 

%The method behind this program is that a point can make three triangles
%using any 2 of the 3 vertices of the triangle in question. If the sum of
%the angles of all three triangles at the point in question equals 2*pi,
%then it is inside the triangle. We allow a small tolerance of error due to
%approximation issues using the arccosine function. 

%For a simulation of how the sum of angles is relative to a point's
%position, see the m-file tri.m, which generates a random triangle and
%shows all the angles for points in and around it. The sum plateaus at 2*pi
%inside the triangle, validating out result. 

function [boo] = isInside(x,y,X,Y)

%initialize our boolean to false
boo = 0;

%Establish our triangl boundary and coordinates
X1 = X(1);
X2 = X(2);
X3 = X(3);
Y1 = Y(1);
Y2 = Y(2);
Y3 = Y(3);

%Find the edge lengths of the triangle, using the pythagorean theorm. 
D12 = sqrt((X1-X2)^2 + (Y1-Y2)^2);
D13 = sqrt((X1-X3)^2 + (Y1-Y3)^2);
D23 = sqrt((X2-X3)^2 + (Y2-Y3)^2);

%Construct the differences from the point in question to the boundary
%values
xd1 = X1 - x;
xd2 = X2 - x;
xd3 = X3 - x;
yd1 = Y1 - y;
yd2 = Y2 - y;
yd3 = Y3 - y;

%Find the distance from the point in question to the vertices
D1 = sqrt(xd1^2 + yd1^2);
D2 = sqrt(xd2^2 + yd2^2);
D3 = sqrt(xd3^2 + yd3^2);

%Use the law of cosines to determine the angle associated at point (x,y)
%while picking two of the sides of the triangle in question. Repeat for all
%combinations. 
A1 = acos((D1^2 + D2^2 - D12^2)/(2*D1*D2));
A2 = acos((D1^2 + D3^2 - D13^2)/(2*D1*D3));
A3 = acos((D2^2 + D3^2 - D23^2)/(2*D2*D3));

%If the sums of the angles is close enough to 2*pi (with a tolerance), then
%change our boolean to true. 
if (A1 + A2 + A3) >= 2*pi - .00001
    boo = 1;
end