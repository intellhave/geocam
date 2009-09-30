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

# This definition tells where the definitions for the
# geometry pipeline can be found.
GEOM= $(BASE)/ehr_geometry

#################################################################
#
# Compiler options:
#
# For general use, use this compiler:
CC= g++ -c -iquote$(TOPO) -iquote$(UTIL) -iquote$(GEOM)

# For proviling/debugging, use this compiler:
#CC= g++ -g -pg -fno-omit-frame-pointer -c -iquote$(TOPO) -iquote$(UTIL)

#################################################################

OBJS= main.o Pipelined_Newtons_Method.o 

newtons: $(OBJS) $(IO) $(GEOM)/geometry.a $(TOPO)/topology.a $(UTIL)/util.a
	g++ $(OBJS) $(GEOM)/geometry.a $(TOPO)/topology.a $(UTIL)/util.a -o newtons

##################################################################
#
# This is a general rule that compiles the input cpp file to
# object code.
#
##################################################################
%.o: %.cpp
	$(CC) $<

#####################################################################

log_clean:
	/bin/rm *.log
clean:
	/bin/rm -rf *~ *.o *.a newtons