#
# Source Code Structure Definitions:
#
# Base = Root directory of the unix portion of the project
BASE= ..

# This definition tells where definitions for the topological
# datastructure (triangulation, vertex, etc.) can be found.
TOPO= $(BASE)/topology

# This definition tells where definitions for basic utilities
# can be found (like reading a triangulation from a file,
# labeling a simplex, etc.)
UTIL= $(BASE)/util

#################################################################
#
# Compiler options:
#
# For general use, use this compiler:
CC= g++ -c -iquote$(TOPO) -iquote$(UTIL)

# For proviling/debugging, use this compiler:
#CC= g++ -g -pg -fno-omit-frame-pointer -c -iquote$(TOPO) -iquote$(UTIL)

#################################################################

OBJS= 3DInputOutput.o triangulationInputOutput.o triangulationIO.o miscmath.o

####################################################################

archive: $(OBJS)
	ar rcs util.a $(OBJS)

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