% figure 1. Right triangle made of circles. 
th = linspace(0,2*pi);
x = cos(th); y = sin(th);
x2 = 4 + 3*cos(th); y2 = 3*sin(th);
x3 = 2*cos(th); y3 = 3 + 2*sin(th);
x4 = zeros(100);
y4 = linspace(0,3);
x5 = linspace(0,4);
y5 = zeros(100);
x6 = x5; y6 = 3 - .75*x6;
plot(x,y,'k',x2,y2,'k',x3,y3,'k',x4,y4,'k',x5,y5,'k',x6,y6,'k')
axis image; axis off;

%figure 2. Basic flat tetrahedron. One vertex at (0,0). 
k = sqrt(3)/2;
%format is [x1, x2, ..., x_n],[y1,y2, ... , y_n]. 'k' means make them 
%all black (default is blue)
plot([-.5,.5,1.5,1,.5,0,-.5,.5,1,0,.5],[k,k,k,0,-k,0,k,k,0,0,k],'k')
axis image; axis off;

%figure 3. Basic 3D tetrahedron. 
plot([-.5,.5,1.5,1,.5,0,-.5,.5,1,0,.5],[k,k,k,0,-k,0,k,k,0,0,k],'k')
axis image; axis off;

%figure 4. Triangulation of 9 point torus.
plot([0,3,3,0,3,],[])
