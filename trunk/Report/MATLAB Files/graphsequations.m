% This m-file contains graphs for the miscellaneous pictures used in the
% report. The program can be run all at once, or individual figures can be
% produced, either copied and pasted into the main MATLAB window, or
% highlight, right click, and pick "Evaluate Selection". 3-D pictures can
% be rotated to a preferred angle. 

% figure 1. Right triangle made of circles. 
figure;
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
figure; 
k = sqrt(3)/2;
%format is [x1, x2, ..., x_n],[y1,y2, ... , y_n]. 'k' means make them 
%all black (default is blue)
plot([-.5,.5,1.5,1,.5,0,-.5,.5,1,0,.5],[k,k,k,0,-k,0,k,k,0,0,k],'k')
axis image; axis off;

%figure 3. Basic 3D tetrahedron. 
figure;
k = sqrt(3)/2; h = sqrt(2/3);
plot3([0,1,.5,0,.5,.5,1,.5],[0,0,k,0,k/3,k,0,k/3],[0,0,0,0,h,0,0,h],'k')
axis image; axis off;

%figure 4. Triangulation of 9 point torus.
figure; 
plot([0,3,3,0,0,3,3,0,0,2,1,1,3,3,0,0,1,2,2,3],[0,0,3,3,0,3,2,2,1,3,3,0,2,1,1,2,3,3,0,1],'k')
axis off; axis image;
axis([-0.1,3.1,-0.1,3.1])

%figure 5. 9 point torus w/ added vertex (1-3 Flip). 
figure; 
plot([0,3,3,0,0,3,3,0,0,2,1,1,3,3,0,0,1,2,2,3,3,1,0.3,0,0,0.3],[0,0,3,3,0,3,2,2,1,3,3,0,2,1,1,2,3,3,0,1,3,3,2.7,3,2,2.7],'k')
axis off; axis image;
axis([-0.1,3.1,-0.1,3.1])

%figure 6. Dual edge
figure
k = sqrt(3)/2; th = [0:.01:2*pi];
plot([1 + k/3*cos(th)],[2*k/3 + k/3*sin(th)],'k','linewidth',2)
hold on
plot([.5 + k/3*cos(th)],[k/3 + k/3*sin(th)],'k','linewidth',2)
plot([0,1,1.5,0.5,0,1,0.5],[0,0,k,k,0,0,k],'k','linewidth',2)
plot([.5,1],[k/3,2*k/3],'k','linewidth',2)
axis image
axis off

%figure 7. 2-2 Flip
figure;
k = sqrt(3)/2;
x1 = [0,1,1.5,0.5,0,1,0.5]; y1 =[0,0,k,k,0,0,k];
x2 = [3,4,4.5,3.5,3,4.5]; y2 = [0,0,k,k,0,k];
x3 = x2 - 1.5; 
plot(x1,y1,'k','linewidth',2)
hold on;
plot(x3,y2,'k','linewidth',2)
axis off; axis image;

%figure 8. Adjoined tetrahedra at a vertex. 
figure;
k = sqrt(3)/2; h = sqrt(2/3);
x = [0,.5,-.5,0,-.5,.5,0,0,.5,-.5,0,0,0,.5,-.5,0,0];
y = [0,k,k,0,-k,-k,0,2*k/3,k,k,2*k/3,0,-2*k/3,-k,-k,-2*k/3,0];
z = [0,0,0,0,0,0,0,h,0,0,h,0,h,0,0,h,0];
plot3(x,y,z,'k')
axis off

%figure 9. Overlapping radii.
figure;
th = 0:.01:2*pi;
x = 5*cos(th);
y = 5*sin(th);
x2 = 7 + 4*cos(th);
y2 = 4*sin(th);
plot(x,y,'k',x2,y2,'k',[0 7],[0 0],'k')
axis off
hold on; z = 8*sqrt(6)/7;
plot([0 29/7],[0 z],'k',[7 29/7],[0 z],'k')
text(29/7 - .4, z + 1.6, '\Phi', 'FontSize', 20)
plot([29/7 7.3 29/7 2],[z 5.5 z 6],'k');
axis image

%figure 10. Bad case. 
figure;
gx = [0,3,3,0,0,3,3+sqrt(24.75),3,1.5,0,-sqrt(24.75),0,1.5,0];
gy = [0,0,1,1,0,1,.5,0,-sqrt(18.75),0,.5,1,1+sqrt(18.75),1];
gx(7) = 3 + sqrt(3.75);
gx(11) = -sqrt(3.75);
gy(9) = -sqrt(4-2.25);
gy(13) = 1 + sqrt(4-2.25);
gx(14) = 3;
plot(gx,gy,'k')
text(1.1,0.5,num2str(5),'FontSize',15)
text(-.2,.5,num2str(2),'FontSize',15)
text(1.5,1.1,num2str(4),'FontSize',15)
text(2,2,num2str(5),'FontSize',15)
axis off;

%figure 11. Octahedron sphere
figure;
th = 0:.001:2*pi;
x1 = zeros(size(th)); y1 = 1.01*cos(th); z1 = 1.01*sin(th);
y2 = zeros(size(th)); z2 = 1.01*cos(th); x2 = 1.01*sin(th);
z3 = zeros(size(th)); x3 = 1.01*cos(th); y3 = 1.01*sin(th);
XT = -1:.1:1;
YT1 = sqrt(1 - XT.^2);
YT2 = -sqrt(1 - XT.^2);
YT = [YT1 YT2];
[XT, YT] = meshgrid(XT,YT);
Z = real(sqrt(1 - XT.^2 - YT.^2));
Z2 = -Z;
mesh(XT,YT,Z)
hold on
mesh(XT,YT,Z2)
plot3(x1,y1,z1,'k','linewidth',3);
plot3(x2,y2,z2,'k','linewidth',3);
plot3(x3,y3,z3,'k','linewidth',3);
axis image; axis off

%figure 12. A Poincare disk of sorts, enclosed by circle of radius 1. 
figure;
maxr = 50;
for r = 1:maxr
    th = 0:.01:2*pi;
    for j = 1:size(th,1)
        x = (log10(r)/log10(maxr))*cos(th);
        y = (log10(r)/log10(maxr))*sin(th);
        plot(x,y,'k-'); hold on
    end
end
axis image
axis off

%Figure 13. An example of an anti- triangle. The red line is the one that
%is flipped. Red dots are used to illustrate the negative area. 

figure;
Y = [0 0 1 0];
X = [0 1 2 0];
plot(X,Y,'linewidth',2)
hold on; 
plot(X,-Y,'g','linewidth',2)
hold on;
plot([0 1],[0 0],'r','linewidth',2)
axis off;

figure;
X2 = 1:.1:1.9;
Y2 = -1:.1:1;
[X2, Y2] = meshgrid(X2,Y2);
for i = 1:size(X2,1)*size(X2,2)
    if Y2(i) - X2(i) >= -1;
        X2(i) = 1.1; Y2(i) = 0;
    end
    if X2(i) + Y2(i) <= 1
        X2(i) = 1.1; Y2(i) = 0;
    end  
end
plot(X2,Y2,'r.','linewidth',2);
hold on;
plot([2 2],[-1,1],'r','linewidth',2)
plot([0 2 1],[0 1 0],'linewidth',2)
plot([0 2 1], [0 -1 0],'g', 'linewidth', 2)
X3 = 0.05:.1:1.95;
Y3 = -.95:.1:.95;
[X3, Y3] = meshgrid(X3,Y3);
for i = 1:size(X3,1)*size(X3,2)
    if Y3(i) - .5*X3(i) >= 0
        X3(i) = 1.1; Y3(i) = 0;
    end
    if .5*X3(i) + Y3(i) <= 0
        X3(i) = 1.1; Y3(i) = 0;
    end  
end
plot(X3,Y3,'b.')
axis off;