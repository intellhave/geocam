E = textread('c:\Dev-Cpp\geocam\Triangulations\EdgeLengths.txt');
K = textread('c:\Dev-Cpp\geocam\Triangulations\Degrees.txt');
    
    fprintf('\nnumber of vertices is: %d \n', size(K,1));
    fprintf('number of edges is: %d \n' , size(E,1));
subplot(2,2,1);
bar(E);
xlabel('Edge #');
ylabel('Edge length');
subplot(2,2,2)
hist(E,max(E));
xlabel('Edge length');
ylabel('Frequency');
    fprintf('average edge length is: %d \n', mean(E));
subplot(2,2,3)
bar(K);
xlabel('Vertex #');
ylabel('Degree');
subplot(2,2,4)
hist(K,2:max(K));
xlabel('Degree');
ylabel('Frequency');
    fprintf('average degree is: %d \n', mean(K));
    
