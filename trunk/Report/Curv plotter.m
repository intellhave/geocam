%graphs curvature and weight plots for evolutions

K = xlsread('c:\Documents and Settings\Math\Desktop\Triangulations\ODE result 7 torus add v add h');

D = 100;
M = 4; 
N = size(K);
i = 0;
J = 0
plot(K(1:1+D,3))
hold on
while i < N
    J = J + D + M;
    L = J + D;
    plot(K(J:L,3))
end
