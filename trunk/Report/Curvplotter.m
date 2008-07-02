%graphs curvature and weight plots for evolutions

%input file
K = xlsread('c:\Documents and Settings\Math\Desktop\Triangulations\ODE result X3');

%other inputs
S = 47; %# of steps, as in calcFlow
M = 1; %default value
N = size(K,1);  % = number of lines in Excel

%plot weights
figure;

i = 1;
plot(K(1:S,1))
hold on;
k = 1;
while i < N-S-M
    i = i + S + M;
    plot(K(i:i+S-1,1),'color',[rand(),rand(),rand()]);
    k = k + 1;
end

%plot curvatures
figure;
i = 1;
plot(K(1:S,2))
hold on;
k = 1;
while i < N-S-M
    i = i + S + M;
    plot(K(i:i+S-1,2),'color',[rand(),rand(),rand()]);
    k = k + 1;
end

% k = number of vertices

% find max, min K for each step, plot 
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

plot([1:S],minK,'k','linewidth',2)
plot([1:S],maxK,'k','linewidth',2)


