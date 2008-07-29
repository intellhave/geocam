function delaunayPlot(fileName)

% This program takes the output of generateTriangulation, saved as a text
% or Excel file, and plots it in MATLAB. The Excel file stores the 
% coordinates of each triangle by four vertices so we can draw each 
% complete face by itself. The format is as follows:

    % Face 1:   Delaunay?
    %           X1 Y1
    %           X2 Y2
    %           X3 Y3
    %           X1 Y1
    % Face 2:   Delaunay?
    %           X1 Y1
    %           X2 Y2
    %           X3 Y3
    %           X1 Y1
    % etc

%The format can be read directly from the text file and later saved as
%an Excel for future reference. 
    
%convert from text to MATLAB


%'c:\Dev-Cpp\geocam\Triangulations\ODE Result.txt'
    K = textread(fileName);
%    K = textread('c:\Dev-Cpp\geocam\Triangulations\flips\Step 5.txt');

    S = (size(K,1))/4;

%Create a 'for' loop that plots each triangle, one at a time, by accessing
%the coordinates per triangle and plotting them in a connect-the-dots
%fashion. The 'hold on' line allows for multiple graphs to be placed on one
%figure. 


for i = 0:S-1
    
    %X = [X1 X2 X3 X4] and Y = [Y1 Y2 Y3 Y4] by accessing the elements from
    %K. 
    
    X = [K(i*4 + 2, 2) K(i*4 + 3, 2) K(i*4 + 4, 2) K(i*4 + 2, 2)];
    Y = [K(i*4 + 2, 3) K(i*4 + 3, 3) K(i*4 + 4, 3) K(i*4 + 2, 3)];
    

    if (K(i*4+1,1) == -1) % for anti-triangles
    h = fill(X,Y,rand(1,3));
    set(h,'EdgeAlpha',.2); %allows us to see the edges. 
    end
    hold on;plot(X,Y,'g','linewidth',2);
    text(K(i*4 + 2, 2), K(i*4 + 2, 3), num2str(K(i*4 + 2, 1)),'color','k','FontSize',15)
    text(K(i*4 + 3, 2), K(i*4 + 3, 3), num2str(K(i*4 + 3, 1)),'color','k','FontSize',15)
    text(K(i*4 + 4, 2), K(i*4 + 4, 3), num2str(K(i*4 + 4, 1)),'color','k','FontSize',15)
    
% These lines generate the circumcircles around each triangle to see from a
% visual standpoint if the resulting triangulation is Delanuay. This is for
% the non-weighted case.  
 
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
% while  on the figure to continue. Leaving this command out displays only
% the end figure. If you need to quit the program while it is running, hold
% Ctrl + C to cancel. 

    %pause(0.1);

   
end

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