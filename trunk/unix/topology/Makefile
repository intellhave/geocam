CC= g++ -ansi -g -pg -fno-omit-frame-pointer -c  

OBJS= simplex.o vertex.o edge.o face.o tetra.o triangulation.o

####################################################################

archive: $(OBJS)
	ar rcs topology.a $(OBJS)	

%.o: %.cpp
	$(CC) $<

#####################################################################

clean:
	/bin/rm -rf *~ *.o *.a