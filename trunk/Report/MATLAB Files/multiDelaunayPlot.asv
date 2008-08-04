%This function allows us to observe the evolution of a triangulation as it
%undergoes flips to become Delaunay. The function requires three inputs,
%with the third being optional. The first is an integer determining the
%number of iterations the evolution went through. This can be found by
%observing the number of flips performed during the weightedFlipAlgorithm
%program. The second specifies which method of graphing to use by inputting
%either 1,2, or 3, which are explained in the next paragraph. The third is
%a time t, which may be given or omitted. If t is given, the program will
%stop for t seconds between each iteration. A good value is .5 or so. If no
%t value is given, for options 1 and 2 the user will manually have to
%scroll through the iterations by pressing a key while clicked on the
%figure. Option 3 does is not affected by the third option, so it can be
%entered or not. 

%There are three main methods of viewing. The first shows the most recent
%version of the flips as it runs through the loop. It is exciting to see
%where the different flips take place. However, it can be hard to see it at
%times, especially if there is a large triangulation or if the flip if
%performed on a small triangle. The second method is to plot the new
%evolution on top of the old evolution, now in a different color. This
%allows the viewer to see which Edges were flipped and where they are now,
%however, it is computationally expensive especially as there are more
%faces, edges and vertices to draw. The third method simply shows the
%differences between the initial and final triangulations, bypassing any
%edges that may exist in intermediary steps.  

%This program relies heavily on the delaunayPlot program which plots
%individual graphs of the steps in the process.

function multiDelaunayPlot(k, option, t)

%This is for method 1.
if option == 1
    for i = 0:k
    
        %Take a number argument and convert it to a readable string
        h = num2str(i);

        %Produce the desired file name by concatenating strings
        j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
        
        %Perform the individual delaunayPlot for a given file. 
        delaunayPlot(j,[0 1 0]);
        titlename = ['Step # ' h];
        title(titlename);
        if nargin == 3
        pause(t);
        else 
            pause();
        end

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
    title('Step # 0');
        if nargin == 3
        pause(t);
        else 
            pause();
        end
    for i = 1:k
        h = num2str(i);
        j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
        h2 = num2str(i-1);
        j2 = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h2 '.txt'];
        delaunayPlot(j2,'r');
        delaunayPlot(j,'g');
        titlename = ['Step # ' h];
        title(titlename);
        if nargin == 3
            pause(t);
        else 
            pause();
        end
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

%j = ['C:\Dev-Cpp\geocam\Triangulations\notable examples\Animated 3 (double
%negatives)\Step ' h '.txt'];

