function delaunayPlot2(filename)

K = textread(filename);
S = (size(K,1))/4;
XMIN = min(K(:,2));
XMAX = max(K(:,2));
YMIN = min(K(:,3));
YMAX = max(K(:,3));
[X,Y] = meshgrid(XMIN:.5:XMAX, YMIN:.5:YMAX);
res = [];
for i = 1:size(X,1)*size(X,2)
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
        end
        if count >= 2
            plot(X(i),Y(i),'m.')
        end
        if count == 0
             plot(X(i),Y(i),'b+')
        end
        if count < 0
             plot(X(i),Y(i),'g.')
        end
    hold on;
    end
    
end
delaunayPlot(filename,'k')
unique(res)


% for j = 1:S-2
%     for m = j+1:S-1
%                 X1 = [K((j)*4 + 2,2) K((j)*4 + 3,2) K((j)*4 + 4,2) K((j)*4 + 2,2)];
%                 Y1 = [K((j)*4 + 2,3) K((j)*4 + 3,3) K((j)*4 + 4,3) K((j)*4 + 2,3)];
%                 X2 = [K((m)*4 + 2,2) K((m)*4 + 3,2) K((m)*4 + 4,2) K((m)*4 + 2,2)];
%                 Y2 = [K((m)*4 + 2,3) K((m)*4 + 3,3) K((m)*4 + 4,3) K((m)*4 + 2,3)]; 
%                 [X1,Y1] = poly2cw(X1,Y1);
%                 [X2,Y2] = poly2cw(X2,Y2);
%                 [xc, yc] = polybool('and',X1,Y1,X2,Y2);
%                 if size(xc,1) ~= 0;
%                     c = K(j*4+1,1) + K(m*4+1,1);
%                     if c == 2
%                     fill(xc,yc,'b')
%                     end
%                     if c == -2
%                             fill(xc,yc,[204/255 0 0]);
%                     end
%                                     [j m c]; pause();
%                 end
% 
%                 hold on;
%     end
% end