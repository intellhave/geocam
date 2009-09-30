function Curvplotter(filename,index,steps)

%This m-file generates plots for the curvatures and weights of a
%triangulation over the number of iterations used in the calcFlow programs.
%It takes three inputs, the first a filename read as a string, the second
%an indicator whether the file is a text or Excel file (use 0 for text, 1
%for Excel), and the last is the number of steps as run in calcFlow.  

%The input file is generated using one of the calcFlow programs using the
%printResultNum command. The listing is the weights of the vertices in one
%column, and the curvatures in the second. All listings for The output is
%in .txt format. MATLAB can read these results directly, but if we wished
%to save these results for later reference, we can import them into an
%Excel spreadsheet which MATLAB can also read in, or leave them as .txt
%files. To date, Excel files are stored as the 2003 version. 


%Use these lines for an output that is stored in Excel format. 

%Use these lines for an output that is in text format. 
if index == 0
    K = textread(filename);
else if index == 1
        K = xlsread(filename);
    end
end

M = index;
S = steps;

%other inputs:

N = size(K,1);  % = number of lines in output

%Figure out how many vertices
i = 1;
k = 1;
while i < N - S + M
    i = i + S + M; 
    k = k + 1;
end

%configure colors for plot. 
colors = rand(k,3);

%plot weights
figure;

%The subplot command lets us plot two or more graphs on one figure, side by
%side (Use (1,2,1)) or one atop another (Use (2,1,1)). To plot on separate
%graphs, remove the subplot command and insert a 'figure' command before
%plotting the second graph. 

subplot(2,1,1);
i = 1;
j = 1;
plot(K(1:S,1),'color',colors(j,1:3),'linewidth',2)
hold on;
while i < N-S-M
    i = i + S + M;
    j = j+1;
    plot(K(i:i+S-1,1),'color',colors(j,1:3),'linewidth',2);
end
xlabel('Step #');
ylabel('Weight');
%title('Spherical Weights and Curvatures, 10 vertex, genus 0, non vertex transitive');

%plot curvatures
%figure;
subplot(2,1,2);
i = 1;
j = 1;
plot(K(1:S,2),'color',colors(j,1:3),'linewidth',2)
hold on;
while i < N-S-M
    i = i + S + M;
    j = j + 1;
    plot(K(i:i+S-1,2),'color',colors(j,1:3),'linewidth',2);
end
xlabel('Step #');
ylabel('Curvature');


% find max, min curvature for each step, plot, if desired

maxK = zeros(1,S);
minK = maxK;
for j = 1:S
    
    temp = zeros(1,k);
    for i = 1:k
        temp(i) = K((i-1)*(S+M) + j, 2);
    end

maxK(j) = max(temp);
minK(j) = min(temp);
end

figure;
plot(1:S,minK,'k','linewidth',2)
hold on;
plot(1:S,maxK,'k','linewidth',2)

%plot average curvature, average weight, if desired

avgK = zeros(1,S);
avgW = zeros(1,S);
for j = 1:S
    sumK = 0;
    sumW = 0;
    for i = 1:k
        sumK = sumK + K((i-1)*(S+M) + j, 2);
        sumW = sumW + K((i-1)*(S+M) + j, 1);
    end
    avgK(j) = sumK/k;
    avgW(j) = sumW/k;
end

figure;
plot(1:S, avgK, 'ko', 'linewidth', 2);
hold on;
plot(1:S, avgW, 'r+', 'linewidth', 2);

