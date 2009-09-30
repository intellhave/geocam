%This program is intended to give a data analysis on the resulting
%triangulations made from generateTriangulation. The functions printDegrees
%and printEdgeLengths output the data into specified files which can be
%accessed here in MATLAB.
%
% Console output:
%
% Average degree
% Standard Deviation of degree
% Average Edge Length
% Standard Deviation of EdgeLength
%
% A 2*2 array of graphs is shown in a figure. These relate to:
%
% [The length of each]          [A histogram of edge]
% [edge as it is     ]          [lengths, grouped   ]
% [numbered.         ]          [by length          ]
%
% [The degree of each]          [A histogram of     ]
% [vertex as it is   ]          [vertex degrees.    ]
% [numbered.         ]          [                   ]
%

E = textread('c:\Dev-Cpp\geocam\Triangulations\EdgeLengths.txt');
K = textread('c:\Dev-Cpp\geocam\Triangulations\Degrees.txt');

fprintf('\nnumber of vertices is: %d \n', size(K,1));
fprintf('number of edges is: %d \n' , size(E,1));

% E = E(1:size(E,2) - 1);
% K = K(1:size(K,2) - 1);
% fprintf('\nnumber of vertices is: %d \n', size(K,2));
% fprintf('number of edges is: %d \n' , size(E,2));


subplot(2,2,1)
hist(E,max(max(E),10));
xlabel('Edge length');
ylabel('Frequency');
title('BEFORE');
fprintf('average edge length is: %G \n', mean(E));
fprintf('stand dev is: %G \n',std(E));



subplot(2,2,3)
hist(K,2:max(K));
xlabel('Degree');
ylabel('Frequency');
fprintf('average degree is: %G \n', mean(K));
fprintf('stand dev is: %G \n',std(K));

E2 = textread('c:\Dev-Cpp\geocam\Triangulations\EdgeLengths2.txt');
K2 = textread('c:\Dev-Cpp\geocam\Triangulations\Degrees2.txt');
fprintf('\nnumber of vertices is: %d \n', size(K2,1));
fprintf('number of edges is: %d \n' , size(E2,1));



subplot(2,2,2)
hist(E2,max(max(E2),10));
xlabel('Edge length');
ylabel('Frequency');
title('AFTER');
fprintf('average edge length is: %G \n', mean(E2));
fprintf('stand dev is: %G \n',std(E2));



subplot(2,2,4)
hist(K2,2:max(K2));
xlabel('Degree');
ylabel('Frequency');
fprintf('average degree is: %G \n', mean(K2));
fprintf('stand dev is: %G \n',std(K2));

figure;

subplot(2,2,1);
bar(E);
xlabel('Edge #');
ylabel('Edge length');
title('BEFORE');

subplot(2,2,3)
bar(K);
xlabel('Vertex #');
ylabel('Degree');

subplot(2,2,2);
bar(E2);
xlabel('Edge #');
ylabel('Edge length');
title('AFTER');

subplot(2,2,4)
bar(K2);
xlabel('Vertex #');
ylabel('Degree');
