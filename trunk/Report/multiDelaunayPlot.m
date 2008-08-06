%This function allows us to observe the evolution of a triangulation as it
%undergoes flips to become Delaunay. The function requires two inputs. The
%first is an integer determining the number of iterations the evolution
%went through. This can be found by observing the number of flips performed
%during the weightedFlipAlgorithm program. The second specifies which
%method of graphing to use by inputting either 1,2, or 3. 

%There are three main methods of viewing. The first shows the most recent
%version of the flips as it runs through the loop. It is exciting to see
%where the different flips take place. However, it can be hard to see it at
%times, especially if there is a large triangulation. The second method is
%to plot the new evolution on top of the old evolution, now in a different
%color. This allows the viewer to see which Edges were flipped and where
%they are now, however, it is computationally expensive especially as there
%are more faces, edges and vertices. The third method simply shows the
%differences between the initial and final triangulations. 

%This program relies heavily on the delaunayPlot program which plots
%individual graphs of the steps in the process.

function multiDelaunayPlot(k, option)


%This is for method 1.
if option == 1
    for i = 0:k
    
        %Take a number argument and convert it to a readable string
        h = num2str(i);

        %Produce the desired file name by concatenating strings
        j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
        %j = ['C:\Dev-Cpp\geocam\Triangulations\notable examples\Animated 1 (negative flips)\Step ' h '.txt'];
        %Perform the individual delaunayPlot for a given file. 
        delaunayPlot(j,[0 1 0]);
        titlename = ['Step # ' h];
        title(titlename);
        pause(0.1);

        %Delete the old graph, except for the last iteration. 
        if i ~= k
            clf;
        end
    end
end
    
%This is for method 2. 
if option == 2
    h = num2str(0);
    j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
    delaunayPlot(j,[0 1 0]);
    pause();
    for i = 1:k
        h = num2str(i);
        j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
        h2 = num2str(i-1);
        j2 = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h2 '.txt'];
        delaunayPlot(j2,'r');
        delaunayPlot(j,'g');
        titlename = ['Step # ' h];
        title(titlename);
        pause(.1);
        if i ~= k
            clf;
        end
    end
end

%This is for the third option. 
if option == 3
    n = num2str(0); n2 = num2str(k);
    l = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' n '.txt'];
    l2 = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' n2 '.txt'];
    delaunayPlot(l,'r');
    hold on;
    delaunayPlot(l2,'g');
end

