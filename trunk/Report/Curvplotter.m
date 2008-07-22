%graphs curvature and weight plots for evolutions
%Edit 7/14: NOW ON ONE PLOT!

%input file

<<<<<<< .mine
K = xlsread('c:\Dev-Cpp\geocam\Triangulations\ODE Result Sph False 9 Torus 1 Test');
=======
K = xlsread('c:\Dev-Cpp\geocam\Triangulations\Long Tetrahedron Conv');
>>>>>>> .r358
%K = xlsread('c:\Dev-Cpp\geocam\Triangulations\spherical data');

%other inputs
<<<<<<< .mine
%S = (size(K,1)-(10-1))/10; %if number of vertices known
S = 210; %if number of steps in calcFlow known
=======

S = 14500; %# of steps, as in calcFlow

>>>>>>> .r358
M = 1; %default value
N = size(K,1);  % = number of lines in Excel

%figure out how many vertices
i = 1;
k = 1;
while i < N - S + M
    i = i + S + M; 
    k = k + 1;
end

%configure colors
colors = rand(k,3);

% %plot weights
figure;
subplot(2,1,1); %This lets us plot two or more graphs on one figure. 
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

% %plot curvatures
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


% find max, min K for each step, plot, if desired

% maxK = zeros(1,S);
% minK = maxK;
% for j = 1:S
%     
%     temp = zeros(1,k);
%     for i = 1:k
%         temp(i) = K((i-1)*(S+M) + j, 2);
%     end
% 
% maxK(j) = max(temp);
% minK(j) = min(temp);
% end
% 
% plot(1:S,minK,'k','linewidth',2)
% hold on;
% plot(1:S,maxK,'k','linewidth',2)

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


