function drawDuals

%This function will draw the dual lengths of the faces of a triangulation
%as developed in the program printDuals in C++. This can be for either
%weighted or uunweighted triangulations. The duals for an unweighted
%triangulation are just the perpendicular bisectors of an edge. 
%
%Currently, all duals are drawn, positive and negative. The duals naturally
%coincide at the centers of the orthocircles of the triangles, serving a
%useful check in our derivation of dual lengths.  


h = 'C:\Dev-Cpp\geocam\Triangulations\duals.txt';
K = textread(h);
S = size(K,1)/2;
for i = 0:S-1
    plot([K(2*i + 1,1) K(2*i + 2,1)],[K(2*i + 1,2) K(2*i + 2,2)],'r','linewidth',2)
    hold on;
end