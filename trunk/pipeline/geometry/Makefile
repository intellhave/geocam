BASE= /home/jthomas/geocam/ver7
GEOM= $(BASE)/geometry
TOPO= $(BASE)/topology
CC= g++ -c -iquote$(GEOM) -iquote$(TOPO) -iquote$(BASE) 
## QUANTS= eta.o radius.o length.o euc_angle.o curvature2d.o

archive: $(QUANTS) Geometry.o triposition.o
	ar rcs geometry.a *.o

##################################################################
%.o: %.cpp
	$(CC) $<

##################################################################

triposition.o: triposition.*[cpp,h]
	$(CC) triposition.cpp 

##################################################################

tidy:
	/bin/rm -rf *~ *.o

clean:
	/bin/rm -rf *~ *.o *.a