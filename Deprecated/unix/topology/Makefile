#################################################################
#
# Compiler options:
#
# For general use, use this compiler:
CC= g++ -c -iquote$(TOPO) -iquote$(UTIL)

# For proviling/debugging, use this compiler:
#CC= g++ -g -pg -fno-omit-frame-pointer -c -iquote$(TOPO) -iquote$(UTIL)

#################################################################

OBJS= simplex.o vertex.o edge.o face.o tetra.o triangulation.o

####################################################################

archive: $(OBJS)
	ar rcs topology.a $(OBJS)	

##################################################################
#
# This is a general rule that compiles the input cpp file to
# object code.
#
##################################################################
%.o: %.cpp
	$(CC) $<

##################################################################
clean:
	/bin/rm -rf *~ *.o *.a