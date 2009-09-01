# Base = Root directory of the project
BASE= ..
TOPO= $(BASE)/unix/topology
UTIL= $(BASE)/unix/util

CC= g++ -g -pg -fno-omit-frame-pointer -c -iquote$(TOPO) -iquote$(UTIL)

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
%.o: %.cpp
	$(CC) $<

##################################################################

clean:
	/bin/rm -rf *~ *.o *.a