E = textread('c:\Dev-Cpp\geocam\Triangulations\EdgeLengths.txt');
K = textread('c:\Dev-Cpp\geocam\Triangulations\Degrees.txt');

    fprintf('\nnumber of vertices is: %d \n', size(K,1));
    fprintf('number of edges is: %d \n' , size(E,1));
    
% E = E(1:size(E,2) - 1);
% K = K(1:size(K,2) - 1);    
%     fprintf('\nnumber of vertices is: %d \n', size(K,2));
%     fprintf('number of edges is: %d \n' , size(E,2));
    
subplot(2,2,1);
bar(E);
xlabel('Edge #');
ylabel('Edge length');
subplot(2,2,2)
hist(E,max(max(E),10));
xlabel('Edge length');
ylabel('Frequency');
    fprintf('average edge length is: %G \n', mean(E));
    fprintf('stand dev is: %G \n',std(E));
subplot(2,2,3)
bar(K);
xlabel('Vertex #');
ylabel('Degree');
subplot(2,2,4)
hist(K,2:max(K));
xlabel('Degree');
ylabel('Frequency');
    fprintf('average degree is: %G \n', mean(K));
    fprintf('stand dev is: %G \n',std(K));
