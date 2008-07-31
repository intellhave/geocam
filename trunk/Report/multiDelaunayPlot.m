%This function allows us to observe the evolution of a triangulation as it
%undergoes flips to become Delaunay. The function requires one input, an
%integer determining the number of iterations the evolution went through.
%This can be found by observing the number of flips performed during the
%weightedFlipAlgorithm program. 

%There are two main methods of viewing. The first shows the most recent
%version of the flips as it runs through the loop. It is exciting to see
%where the different flips take place. However, it can be hard to see it at
%times, especially if there is a large triangulation. The second method is
%to plot the new evolution on top of the old evolution, now in a different
%color. This allows the viewer to see which Edges were flipped and where
%they are now. 

%This program relies heavily on the delaunayPlot program which plots
%individual graphs of the steps in the process.

function multiDelaunayPlot(k)

%This is for method 1.
for i = 0:k
    h = num2str(i);
    j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
  
    delaunayPlot(j,[0 1 0]);
    pause();
    if i ~= k
        clf;
    end
    
% %This is for method 2.     
%     h = num2str(0);
%     j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
%     delaunayPlot(j,[0 1 0]);
%     pause(.1);
%     for i = 1:k
%         h = num2str(i);
%         j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
%         h2 = num2str(i-1);
%         j2 = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h2 '.txt'];
%         delaunayPlot(j2,[1 0 0]);
%         delaunayPlot(j,'c');
%         pause(.1);
%         if i ~= k
%             clf;
%         end
end
