%This function will draw the dual lengths of the faces of a triangulation
%as developed in the program printDuals in C++. 

%Currently, all duals are drawn, positive and negative. 

function drawDuals
h = 'C:\Dev-Cpp\geocam\Triangulations\duals2.txt';
K = textread(h);
S = size(K,1)/2;
for i = 0:S-1
    plot([K(2*i + 1,1) K(2*i + 2,1)],[K(2*i + 1,2) K(2*i + 2,2)],'r','linewidth',2)
    hold on;
end