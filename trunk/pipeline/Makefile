BASE= /home/jthomas/geocam/pipeline
GEOM= $(BASE)/geometry
TOPO= $(BASE)/topology
CC= g++ -ansi -c -iquote$(GEOM) -iquote$(TOPO) -iquote$(BASE)

geotest: geometry/geometry.a topology/topology.a main.o miscmath.o
	g++ -ansi main.o miscmath.o geometry/geometry.a topology/topology.a -o geotest 	

####################################################################

main.o : main.cpp
	$(CC) main.cpp 

miscmath.o : miscmath.*[cpp,h]
	$(CC) miscmath.cpp 

#####################################################################

clean:
	/bin/rm -rf *~ *.o *.a