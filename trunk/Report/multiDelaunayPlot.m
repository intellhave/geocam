function multiDelaunayPlot(k)
    h = num2str(0);
    j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];
    delaunayPlot(j,[0 1 0]);
    pause(0.2);
for i = 1:k
    h = num2str(i);
    j = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h '.txt'];

%     h2 = num2str(i-1);
%     j2 = ['c:\Dev-Cpp\geocam\Triangulations\flips\Step ' h2 '.txt'];
%     delaunayPlot(j2,[1 0 0]);
    delaunayPlot(j,[0 1 0]);
    pause(.1);
    if i ~= k
    clf;
    end
end
