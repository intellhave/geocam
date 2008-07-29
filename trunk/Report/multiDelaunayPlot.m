function multiDelaunayPlot(k)
for i = 0:k
    h = num2str(i);
    j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
    delaunayPlot(j);
    %axis([-15 15 -15 15]);
    pause(0.2);
    clf;
end
