BASE= /home/jthomas/geocam/pipeline
GEOM= $(BASE)/geometry
TOPO= $(BASE)/topology
CC= g++ -g -c -iquote$(GEOM) -iquote$(TOPO) -iquote$(BASE) 
QUANTS= eta.o radius.o volume_partial.o length.o volume_second_partial.o volume.o partial_edge.o euc_angle.o edge_height.o dih_angle.o face_height.o dih_angle_sum.o edge_curvature.o dualareasegment.o curvature3D.o dualarea.o volume_partial_sum.o totalvolume.o totalcurvature.o curvature_partial.o ehr_partial.o ehr_second_partial.o

archive: $(QUANTS) triposition.o
	ar rcs geometry.a $(QUANTS) triposition.o

##################################################################
%.o: %.cpp
	$(CC) $<

##################################################################

clean:
	/bin/rm -rf *~ *.o *.a