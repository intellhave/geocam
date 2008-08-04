%This function is to relate the initial positiveness of a triangulation
%with its end result. This can be used to ensure that the flip algorithm is
%working correctly. The input is a single file name, much like the regular
%delaunayPlot. 

%We produce a graph by determining the extremes of a triangulation in the
%plane, and construct a meshgrid rectangle for all possible points. While
%this can be computationally expensive, it was the first thing we could
%thingk of that worked. For each point in the grid, it determines if it
%lies inside a face of the triangulation by using the program isInside. We
%can accumulate the resulting pixels relating them to the positive ot
%negative Delaunay triangles that they are found inside of. 

function delaunayPlot2(filename)

K = textread(filename);
S = (size(K,1))/4;
XMIN = min(K(:,2));
XMAX = max(K(:,2));
Xdelta = (XMAX - XMIN)/200;
YMIN = min(K(:,3));
YMAX = max(K(:,3));
Ydelta = (YMAX - YMIN)/200;
[X,Y] = meshgrid(XMIN:Xdelta:XMAX, YMIN:Ydelta:YMAX);
res = [];
resdat0 = 0;
resdat1 = 0;
resdatn1 = 0;
resdat2 = 0;
resdatn2 = 0;
resdat3 = 0;
resdatn3 = 0;
resdat4 = 0;
total = 0;
fives = 0;
for i = 1:size(X,1)*size(X,2)
    if mod(i, round(size(X,1)*size(X,2)/20)) == 0
        fives = fives + 5;
        fprintf('%G \n',fives)
        if fives == 100
            fprintf('Generating graph...');
        end
    end
    count = 0;
    useful = 0;
    for j = 0:S-1
        X1 = [K((j)*4 + 2,2) K((j)*4 + 3,2) K((j)*4 + 4,2) K((j)*4 + 2,2)];
        Y1 = [K((j)*4 + 2,3) K((j)*4 + 3,3) K((j)*4 + 4,3) K((j)*4 + 2,3)];
        if (isInside(X(i),Y(i),X1,Y1) == 1)
            count = count + K(j*4 + 1, 1);
            useful = useful + 1;
        end
    end
    
    if useful > 0
        res = [res count];
        if count == 1
            plot(X(i),Y(i),'r.')
            resdat1 = resdat1 + 1;
            total = total + count; 
        end
        if count == 2
            plot(X(i),Y(i),'r.')
            resdat2 = resdat2 + 1;
            total = total + count; 
        end
        if count == 3
            plot(X(i),Y(i),'m.')
            resdat3 = resdat3 + 1;
            total = total + count; 
        end
        if count == 4
            plot(X(i),Y(i),'m.')
            resdat4 = resdat4 + 1;
            total = total + count; 
        end
        if count == 0
             plot(X(i),Y(i),'b.')
             resdat0 = resdat0 + 1;
             total = total + count; 
        end
        if count == -1
             plot(X(i),Y(i),'g.')
             resdatn1 = resdatn1 + 1;
             total = total + count; 
        end
        if count == -2
            plot(X(i),Y(i),'y.')
            resdatn2 = resdatn2 + 1;
            total = total + count;
        end
        if count == -3
            plot(X(i),Y(i),'k.')
            resdatn3 = resdatn3 + 1;
            total = total + count; 
        end
    hold on;
    end
    
end
delaunayPlot(filename,'k')
nn = unique(res)
total
for i = min(nn):max(nn)
        fprintf('number of %G''s is: ', i);
        if i >= -3 && i <= 3
            if i == -3
                fprintf('%G \n', resdatn3)
            end
            if i == -2
                fprintf('%G \n', resdatn2)
            end
            if i == -1
                fprintf('%G \n', resdatn1)
            end
            if i == 0
                fprintf('%G \n', resdat0)
            end
            if i == 1
                fprintf('%G \n', resdat1)
            end
            if i == 2
                fprintf('%G \n', resdat2)
            end
            if i == 3
                fprintf('%G \n', resdat3)
            end
            if i == 4
                fprintf('%G \n', resdat4)
            end
        else
            fprintf('unaccounted for\n')
        end
end