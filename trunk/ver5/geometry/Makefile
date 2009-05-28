BASE= /home/jthomas/geocamDS/ver5
GEOM= $(BASE)/geometry
TOPO= $(BASE)/topology
CC= g++ -ansi -c -iquote$(GEOM) -iquote$(TOPO) -iquote$(BASE) 
QUANTS= eta.o radius.o length.o euc_angle.o curvature2d.o

archive: $(QUANTS) Geometry.o triposition.o
	ar rcs geometry.a *.o

##################################################################

eta.o : eta.cpp geoquant.h triposition.h
	$(CC) eta.cpp

radius.o : radius.cpp geoquant.h triposition.h
	$(CC) radius.cpp

length.o : length.cpp geoquant.h triposition.h
	$(CC) length.cpp 

euc_angle.o : euc_angle.cpp geoquant.h triposition.h
	$(CC) euc_angle.cpp 

curvature2d.o : curvature2d.cpp geoquant.h triposition.h
	$(CC) curvature2d.cpp 

##################################################################

Geometry.o: Geometry.[cpp,h]
	$(CC) Geometry.cpp

triposition.o: triposition.*[cpp,h]
	$(CC) triposition.cpp 

##################################################################

tidy:
	/bin/rm -rf *~ *.o

clean:
	/bin/rm -rf *~ *.o *.a