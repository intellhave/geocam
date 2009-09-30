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

QUANTS= area.o curvature2D.o curvature3D.o curvature_partial.o \
	dih_angle.o dih_angle_sum.o dualarea.o dualareasegment.o \
	edge_curvature.o edge_height.o ehr_partial.o ehr_second_partial.o \
	eta.o euc_angle.o face_height.o length.o partial_edge.o radius.o \
	sectional_curvature.o totalcurvature.o totalvolume.o \
	total_volume_partial.o triposition.o volume.o volume_partial.o \
	volume_partial_sum.o volume_second_partial.o

archive: $(QUANTS) triposition.o
	ar rcs geometry.a $(QUANTS) triposition.o

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