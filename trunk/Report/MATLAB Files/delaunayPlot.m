function delaunayPlot(fileName, color)

% This program takes the output of generateTriangulation, saved as a text
% or Excel file, and plots it in MATLAB. The inputs are the name of the
% file, and the color you would like the graph to be in. You can use either
% single character arguments, like 'k' for black, 'b' for blue, 'r', 'g',
% 'y', etc. (use the single quotes when specifying a color) or you can
% enter a 1*3 matrix with values ranging from 0 to 1 that pick a color from
% the spectrum. The file stores the coordinates of each triangle by
% vertices so we can draw each complete face by itself. The format is as
% follows:

    % Face 1:   Delaunay? 1 for yes, -1 for no
    %           V1 X1 Y1
    %           V2 X2 Y2
    %           V3 X3 Y3
    %           -blank- 
    % Face 2:   Delaunay?
    %           V1 X1 Y1
    %           V2 X2 Y2
    %           V3 X3 Y3
    %           -blank-
    % etc

%The format can be read directly from the text file and later saved, even
%as an Excel for future reference, or just stored in a special place as a
%text file. 
    
%convert from text to MATLAB. Track the number of faces. 

K = textread(fileName);
S = (size(K,1))/4;

N = []; %Track list of negative triangles
dnegs = 0; %Track number of intersecting negative triangles. 

%Create a 'for' loop that plots each triangle, one at a time, by accessing
%the coordinates per triangle and plotting them in a connect-the-dots
%fashion. The 'hold on' line allows for multiple graphs to be placed on one
%figure. 

for i = 0:S-1
    
    %X = [X1 X2 X3 X1] and Y = [Y1 Y2 Y3 Y1] by accessing the elements from
    %K. We repeat the first coordinate to close the triangle. 
    
    X = [K(i*4 + 2, 2) K(i*4 + 3, 2) K(i*4 + 4, 2) K(i*4 + 2, 2)];
    Y = [K(i*4 + 2, 3) K(i*4 + 3, 3) K(i*4 + 4, 3) K(i*4 + 2, 3)];
    
    if (K(i*4+1,1) == -1) % for anti-triangles
        h = fill(X,Y,[.8 .8 .8]);%Shades in the triangle, gray for now 
        N = [N i];    
        set(h,'EdgeAlpha',.2); %allows us to see the edges underneath. 
    end
    
    hold on;plot(X,Y,'color',color,'linewidth',2);
    
    %Track the positions of the vertices. 
    
    text(K(i*4 + 2, 2), K(i*4 + 2, 3), num2str(K(i*4 + 2, 1)),'color','b','FontSize',10)
    text(K(i*4 + 3, 2), K(i*4 + 3, 3), num2str(K(i*4 + 3, 1)),'color','b','FontSize',10)
    text(K(i*4 + 4, 2), K(i*4 + 4, 3), num2str(K(i*4 + 4, 1)),'color','b','FontSize',10)

% % These lines generate the circumcircles around each triangle to see from a
% % visual standpoint if the resulting triangulation is Delanuay. This is for
% % the non-weighted case.  
%  
%     th = 0:.001:2*pi;
%     a = X(1); b = Y(1); c = X(2); d = Y(2); e = X(3); f = Y(3);
%     ycn = ((a^2 - c^2 - d^2 + b^2)*(c-e) - (c^2 - e^2 - f^2 + d^2)*(a-c));
%     ycd = 2*(f - d)*(a-c) - 2*(d - b)*(c-e);
%     yc = ycn/ycd;
%     xc = (a^2 - c^2 - d^2 + b^2 + 2*yc*(d - b))/(2*(a-c));
%     r = sqrt((xc - a)^2 + (yc - b)^2);
%     P = xc + r*cos(th);
%     Q = yc + r*sin(th);
%     plot(P,Q,'r','linewidth',2)
%     plot(X,Y,'ko','linewidth',5);
    
% To watch the building of the triangulation in action, use this command.
% The number argument is the time interval between each phase of the loop.
% Not entering a time value will require you to press a key like spacebar
% while on the figure to continue. Leaving this command out displays only
% the end figure. If you need to quit the program while it is running, hold
% Ctrl + C to cancel from the main console. This is useful when running a
% single file, but not useful if running multiDelaunayPlot. 

%pause(0.001);

end

%This section determines if any negative triangles overlap, and if so,
%makes the interesection a different color. The 'poly2cw' command takes a
%polygon (as defined by its coordinates) and rewrites the fiure in a
%clockwise fashion. The 'polybool' command can determine if two polygons
%share any regions of intersect, and store these coordinates. 'polybool'
%requires that the polygons be oriented clockwise, and hence the 'poly2cw'
%command is used prior. If there is an intersection, we change the color of
%the region by using the 'fill' command. This tracks up to
%two negative triangles overlapping. 

if size(N,2) > 0
     for j = 1:size(N,2)-1
           for m = j+1:size(N,2)
                X1 = [K(N(j)*4 + 2,2) K(N(j)*4 + 3,2) K(N(j)*4 + 4,2) K(N(j)*4 + 2,2)];
                Y1 = [K(N(j)*4 + 2,3) K(N(j)*4 + 3,3) K(N(j)*4 + 4,3) K(N(j)*4 + 2,3)];
                X2 = [K(N(m)*4 + 2,2) K(N(m)*4 + 3,2) K(N(m)*4 + 4,2) K(N(m)*4 + 2,2)];
                Y2 = [K(N(m)*4 + 2,3) K(N(m)*4 + 3,3) K(N(m)*4 + 4,3) K(N(m)*4 + 2,3)]; 
                [X1,Y1] = poly2cw(X1,Y1);
                [X2,Y2] = poly2cw(X2,Y2);
                [xc, yc] = polybool('and',X1,Y1,X2,Y2);
                if size(xc,1) ~= 0;
                    fill(xc,yc,[204/255 0 0])
                    dnegs = dnegs + 1;
                end
           end
     end
end

%This section tries to see if any overlaps of negative triangles overlap
%each other, indicating that more than two negative triangles inhabit a
%specific region. We generate all the negative triangles again, rebuild
%their overlaps, and then see if those overlaps overlap. We then color the
%new intersection a third color, currently black. 

if dnegs > 1
    for j = 1:size(N,2)-1     
           for m = j+1:size(N,2)
                X1 = [K(N(j)*4 + 2,2) K(N(j)*4 + 3,2) K(N(j)*4 + 4,2) K(N(j)*4 + 2,2)];
                Y1 = [K(N(j)*4 + 2,3) K(N(j)*4 + 3,3) K(N(j)*4 + 4,3) K(N(j)*4 + 2,3)];
                X2 = [K(N(m)*4 + 2,2) K(N(m)*4 + 3,2) K(N(m)*4 + 4,2) K(N(m)*4 + 2,2)];
                Y2 = [K(N(m)*4 + 2,3) K(N(m)*4 + 3,3) K(N(m)*4 + 4,3) K(N(m)*4 + 2,3)]; 
                [X1,Y1] = poly2cw(X1,Y1);
                [X2,Y2] = poly2cw(X2,Y2);
                [xc, yc] = polybool('and',X1,Y1,X2,Y2);
                if size(xc,1) ~= 0
                    for k = 1:size(N,2)-1
                        if k ~= j
                            for l = k+1:size(N,2)
                                if l ~= m
                                    X3 = [K(N(k)*4 + 2,2) K(N(k)*4 + 3,2) K(N(k)*4 + 4,2) K(N(k)*4 + 2,2)];
                                    Y3 = [K(N(k)*4 + 2,3) K(N(k)*4 + 3,3) K(N(k)*4 + 4,3) K(N(k)*4 + 2,3)];
                                    X4 = [K(N(l)*4 + 2,2) K(N(l)*4 + 3,2) K(N(l)*4 + 4,2) K(N(l)*4 + 2,2)];
                                    Y4 = [K(N(l)*4 + 2,3) K(N(l)*4 + 3,3) K(N(l)*4 + 4,3) K(N(l)*4 + 2,3)];
                                    [X3,Y3] = poly2cw(X3,Y3);
                                    [X4,Y4] = poly2cw(X4,Y4);
                                    [xc2,yc2] = polybool('and',X3,Y3,X4,Y4);
                                    [xd, yd] = polybool('and', xc, yc, xc2, yc2);
                                    if size(xc2,1) ~= 0;
                                        fill(xd,yd,'k');
                                    end
                                end
                            end
                        end
                    end
                end
           end
     end    
end

end





%OUTDATED, would require revision if desired 

%convert from Excel to MATLAB

%     K = xlsread('c:\Dev-Cpp\geocam\Triangulations\  ');
%     S = (size(K,1) + 1)/5; 
%     %The Excel format inputs the blank lines except for the last one

% for i = 0:S-1
%     
%     %X = [X1 X2 X3 X4] and Y = [Y1 Y2 Y3 Y4] by accessing the elements from
%     %K. 
%     
%     X = [K(i*5 + 1, 1) K(i*5 + 2, 1) K(i*5 + 3, 1) K(i*5 + 4, 1)];
%     Y = [K(i*5 + 1, 2) K(i*5 + 2, 2) K(i*5 + 3, 2) K(i*5 + 4, 2)];
%     
%     plot(X,Y,'k'); %The 'k' makes all lines black. 
%     hold on;
%    
%     pause(0.01);
% end