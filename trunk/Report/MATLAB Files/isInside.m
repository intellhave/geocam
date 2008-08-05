function [boo] = isInside(x,y,X,Y)
boo = 0;
X1 = X(1);
X2 = X(2);
X3 = X(3);
Y1 = Y(1);
Y2 = Y(2);
Y3 = Y(3);
D12 = sqrt((X1-X2)^2 + (Y1-Y2)^2);
D13 = sqrt((X1-X3)^2 + (Y1-Y3)^2);
D23 = sqrt((X2-X3)^2 + (Y2-Y3)^2);
xd1 = X1 - x;
xd2 = X2 - x;
xd3 = X3 - x;
yd1 = Y1 - y;
yd2 = Y2 - y;
yd3 = Y3 - y;
D1 = sqrt(xd1^2 + yd1^2);
D2 = sqrt(xd2^2 + yd2^2);
D3 = sqrt(xd3^2 + yd3^2);
A1 = acos((D1^2 + D2^2 - D12^2)/(2*D1*D2));
A2 = acos((D1^2 + D3^2 - D13^2)/(2*D1*D3));
A3 = acos((D2^2 + D3^2 - D23^2)/(2*D2*D3));
if (A1 + A2 + A3) >= 2*pi - .00001
    boo = 1;
end
    