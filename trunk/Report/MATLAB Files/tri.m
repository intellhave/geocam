%This program serves as an example of the program isInside.m which is in
%this folder. A triangle is generated using random coordinates, and a mesh
%of all possible points in the grid is run through the program. If a point
%is inside the triangle, the resultant sum will be 2*pi, and less than that
%if outside the triangle. 

%The output is 2 graphs, one above the other. The first is a simple drawing
%of the triangle, and the second is a surface plot of the resultant angle
%sum for each point in the grid. This can be rotated to see how the region
%inside the triangle is flat. 

[X,Y] = meshgrid(-10:.2:10,-10:.2:10);
Z = zeros(size(X));
X1 = -10 + 20*rand();
X2 = -10 + 20*rand();
X3 = -10 + 20*rand();
Y1 = -10 + 20*rand();
Y2 = -10 + 20*rand();
Y3 = -10 + 20*rand();
D12 = sqrt((X1-X2)^2 + (Y1-Y2)^2);
D13 = sqrt((X1-X3)^2 + (Y1-Y3)^2);
D23 = sqrt((X2-X3)^2 + (Y2-Y3)^2);
subplot(2,1,1);
plot([X1,X2,X3,X1],[Y1,Y2,Y3,Y1]);
axis([-10,10,-10,10]);
for i = 1:size(X,1)*size(X,2)
    xd1 = X1 - X(i);
    xd2 = X2 - X(i);
    xd3 = X3 - X(i);
    yd1 = Y1 - Y(i);
    yd2 = Y2 - Y(i);
    yd3 = Y3 - Y(i);
    D1 = sqrt(xd1^2 + yd1^2);
    D2 = sqrt(xd2^2 + yd2^2);
    D3 = sqrt(xd3^2 + yd3^2);
    A1 = acos((D1^2 + D2^2 - D12^2)/(2*D1*D2));
    A2 = acos((D1^2 + D3^2 - D13^2)/(2*D1*D3));
    A3 = acos((D2^2 + D3^2 - D23^2)/(2*D2*D3));
    Z(i) = A1 + A2 + A3;
end
subplot(2,1,2);
surf(X,Y,Z);